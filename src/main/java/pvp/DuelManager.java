package pvp;

import misc.Plugin;
import misc.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;

import java.time.Duration;
import java.util.*;

/**
 * 1v1 duel lifecycle on top of CustomDamage (no damage listener of its own; driven through {@link PvpHooks}).
 * Invites, network force-pair, arena + countdown, win on would-be death with no death screen, intelligence
 * swap, stats, return home.
 */
public class DuelManager {
	// Resistance V = fully immune under CustomDamage (20% per level).
	private static final int MAX_RESISTANCE = 4;
	private static final int RETURN_DELAY_TICKS = 100;  // 5s
	private static final long INVITE_TIMEOUT_TICKS = 1200L; // 60s

	private final JavaPlugin plugin;
	private final PvpConfig cfg;
	private final PvpStats stats;
	private final PvpLoadouts loadouts;

	private final Map<UUID, Duel> byPlayer = new HashMap<>();
	private final Map<UUID, UUID> invites = new HashMap<>();         // target -> inviter
	private final Map<UUID, Integer> inviteTokens = new HashMap<>(); // target -> token, so a stale timeout can't cancel a newer request
	private int inviteCounter = 0;
	// Quit mid-duel: return-home (5s later) skips offline players, so move them out on next join instead.
	private final Set<UUID> strandedInArena = new HashSet<>();
	// Single arena, one duel at a time. Pairs accepting while busy wait here (FIFO). arenaOccupied stays true
	// through the post-match grace (until both are sent home) so the next pair never spawns on top of them.
	private final Deque<Queued> queue = new ArrayDeque<>();
	private boolean arenaOccupied = false;

	public DuelManager(JavaPlugin plugin, PvpConfig cfg, PvpStats stats, PvpLoadouts loadouts) {
		this.plugin = plugin;
		this.cfg = cfg;
		this.stats = stats;
		this.loadouts = loadouts;
	}

	public boolean inDuel(UUID id) {
		return byPlayer.containsKey(id);
	}

	public boolean armed(UUID id) {
		Duel d = byPlayer.get(id);
		return d != null && d.armed;
	}

	public boolean areOpponents(UUID a, UUID b) {
		Duel d = byPlayer.get(a);
		return d != null && d.has(b);
	}

	// ===== invite flow (local) =====
	public void invite(Player from, Player to) {
		if (notEnabled(from)) return;
		if (inDuel(from.getUniqueId()) || isQueued(from.getUniqueId())) {
			from.sendMessage(Utils.msg("<red>You're already in a duel or waiting in the queue"));
			return;
		}
		if (from.equals(to)) {
			from.sendMessage(Utils.msg("<red>You can't duel yourself"));
			return;
		}
		UUID toId = to.getUniqueId();
		int token = ++inviteCounter;
		invites.put(toId, from.getUniqueId());
		inviteTokens.put(toId, token);
		to.sendMessage(Utils.msg("<yellow><gold><s></gold> is challenging you!  Click <click:run_command:'/duel accept'><green><u>here</u></green></click> to accept or <click:run_command:'/duel decline'><red><u>here</u></red></click> to decline",
				Placeholder.unparsed("s", from.getName())));
		from.sendMessage(Utils.msg("<green>Challenge sent to <white><t></white>  <gray>Click <click:run_command:'/duel cancel'><red><u>here</u></red></click> to cancel",
				Placeholder.unparsed("t", to.getName())));
		// Expire after 60s if still the active request for this target.
		UUID fromId = from.getUniqueId();
		String fromName = from.getName(), toName = to.getName();
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			if (inviteTokens.getOrDefault(toId, -1) == token) {
				invites.remove(toId);
				inviteTokens.remove(toId);
				Player f = Bukkit.getPlayer(fromId);
				Player t = Bukkit.getPlayer(toId);
				if (f != null) f.sendMessage(Utils.msg("<red>Your duel request to <white><t></white> expired", Placeholder.unparsed("t", toName)));
				if (t != null) t.sendMessage(Utils.msg("<gray>The duel request from <white><s></white> expired", Placeholder.unparsed("s", fromName)));
			}
		}, INVITE_TIMEOUT_TICKS);
	}

	public void accept(Player target) {
		UUID inv = invites.remove(target.getUniqueId());
		inviteTokens.remove(target.getUniqueId());
		Player from = inv == null ? null : Bukkit.getPlayer(inv);
		if (from == null) {
			target.sendMessage(Utils.msg("<red>You have no pending duel invite"));
			return;
		}
		start(from, target);
	}

	/** Receiver declines a pending request. */
	public void decline(Player target) {
		UUID inv = invites.remove(target.getUniqueId());
		inviteTokens.remove(target.getUniqueId());
		if (inv == null) {
			target.sendMessage(Utils.msg("<red>You have no pending duel invite"));
			return;
		}
		target.sendMessage(Utils.msg("<yellow>You declined the duel request"));
		Player from = Bukkit.getPlayer(inv);
		if (from != null) from.sendMessage(Utils.msg("<red><white><t></white> declined your duel request", Placeholder.unparsed("t", target.getName())));
	}

	/** Sender cancels their outgoing request. */
	public void cancel(Player sender) {
		UUID senderId = sender.getUniqueId();
		UUID targetId = null;
		for (Map.Entry<UUID, UUID> e : invites.entrySet()) {
			if (e.getValue().equals(senderId)) {
				targetId = e.getKey();
				break;
			}
		}
		if (targetId == null) {
			sender.sendMessage(Utils.msg("<red>You have no outgoing duel request"));
			return;
		}
		invites.remove(targetId);
		inviteTokens.remove(targetId);
		sender.sendMessage(Utils.msg("<yellow>You cancelled your duel request"));
		Player target = Bukkit.getPlayer(targetId);
		if (target != null) target.sendMessage(Utils.msg("<gray><white><s></white> cancelled their duel request", Placeholder.unparsed("s", sender.getName())));
	}

	/**
	 * Clear every duel state: ACTIVE duel ends as a draw, queued pair dropped, requests sent/received cancelled.
	 * True if anything was cleared. Draw, not forfeit: neither fighter chose to stop. Reached via
	 * {@code /duel forceclear <player>}, which the network uses before force-pairing a busy player. Arena stays
	 * occupied for the grace period, so a pair force-started right after waits in the queue.
	 */
	public boolean forceClear(Player p) {
		UUID id = p.getUniqueId();
		boolean cleared = clearInvites(id);
		if (removeFromQueue(id)) {
			p.sendMessage(Utils.msg("<yellow>An admin took you out of the duel queue"));
			return true;
		}
		Duel d = byPlayer.get(id);
		if (d == null) return cleared;
		for (UUID side : new UUID[]{d.a, d.b}) {
			Player pl = Bukkit.getPlayer(side);
			if (pl != null) pl.sendMessage(Utils.msg("<yellow>An admin ended your duel"));
		}
		drawEnd(d);
		return true;
	}

	/** Drop requests they received or sent, telling the other side. */
	private boolean clearInvites(UUID id) {
		boolean any = false;
		UUID inviter = invites.remove(id);
		inviteTokens.remove(id);
		if (inviter != null) {
			any = true;
			Player f = Bukkit.getPlayer(inviter);
			Player t = Bukkit.getPlayer(id);
			if (f != null && t != null) f.sendMessage(Utils.msg("<gray>Your duel request to <white><t></white> was cancelled",
					Placeholder.unparsed("t", t.getName())));
		}
		Iterator<Map.Entry<UUID, UUID>> it = invites.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<UUID, UUID> e = it.next();
			if (!e.getValue().equals(id)) continue;
			it.remove();
			inviteTokens.remove(e.getKey());
			any = true;
			Player t = Bukkit.getPlayer(e.getKey());
			if (t != null) t.sendMessage(Utils.msg("<gray>That duel request was cancelled"));
		}
		return any;
	}

	// ===== match =====
	public void start(Player a, Player b) {
		if (notEnabled(a)) return;
		if (inDuel(a.getUniqueId()) || inDuel(b.getUniqueId())) {
			a.sendMessage(Utils.msg("<red>One of you is already in a duel"));
			return;
		}
		if (isQueued(a.getUniqueId()) || isQueued(b.getUniqueId())) {
			a.sendMessage(Utils.msg("<red>One of you is already waiting in the duel queue"));
			return;
		}
		if (cfg.duelSpawn(0) == null || cfg.duelSpawn(1) == null) {
			a.sendMessage(Utils.msg("<red>The duel arena isn't configured yet"));
			return;
		}
		// Arena busy (or still clearing out): queue this pair; they start when it frees.
		if (arenaOccupied) {
			queue.addLast(new Queued(a.getUniqueId(), b.getUniqueId()));
			Component msg = Utils.msg("<yellow>The duel arena is busy - you're <white>#<n></white> in the queue. You'll be sent in automatically when it's free.  <gray><click:run_command:'/duel leave'>(<red><u>leave queue</u></red>)</click>",
					Placeholder.unparsed("n", String.valueOf(queue.size())));
			a.sendMessage(msg);
			b.sendMessage(msg);
			return;
		}
		begin(a, b);
	}

	/** Place a pair in the free arena and run the countdown. */
	private void begin(Player a, Player b) {
		Location sa = cfg.duelSpawn(0);
		Location sb = cfg.duelSpawn(1);
		if (sa == null || sb == null) {
			a.sendMessage(Utils.msg("<red>The duel arena isn't configured yet"));
			return;
		}
		arenaOccupied = true;
		Duel d = new Duel(a.getUniqueId(), b.getUniqueId(), a.getLocation().clone(), b.getLocation().clone());
		// Stash real intelligence, hunger, saturation: the duel runs fixed values and restores these at the end.
		d.intelA = readIntelligence(a);
		d.intelB = readIntelligence(b);
		d.foodA = a.getFoodLevel();
		d.foodB = b.getFoodLevel();
		d.satA = a.getSaturation();
		d.satB = b.getSaturation();
		d.invA = cloneContents(a.getInventory().getContents());
		d.invB = cloneContents(b.getInventory().getContents());
		// Effects are wiped at FIGHT, restored at end.
		d.effA = new ArrayList<>(a.getActivePotionEffects());
		d.effB = new ArrayList<>(b.getActivePotionEffects());
		d.gmA = a.getGameMode();
		d.gmB = b.getGameMode();
		byPlayer.put(a.getUniqueId(), d);
		byPlayer.put(b.getUniqueId(), d);
		prepare(a, sa);
		prepare(b, sb);
		// Intelligence, saturation and corner snap happen when the countdown ends (snapToCorner).
		runCountdown(d, a.getUniqueId(), b.getUniqueId());
	}

	private void prepare(Player p, Location spawn) {
		p.teleport(spawn);
		p.setGameMode(GameMode.ADVENTURE); // restored in finishPlayer
		healFull(p);
		p.setFoodLevel(20);
		// Saved loadout if any, else the default kit. Refreshed against current item definitions first so a loadout
		// saved before an item change brings no stale items.
		ItemStack[] saved = PvpItemRefresh.refreshSaved(loadouts, p.getUniqueId()).arr();
		if (saved != null) PvpLoadouts.apply(p, saved);
		else DuelKit.apply(p);
		// Free to walk during countdown; max Resistance keeps them invulnerable until FIGHT.
		p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, (cfg.duelCountdown() + 2) * 20, MAX_RESISTANCE, false, false));
	}

	private void runCountdown(Duel d, UUID aId, UUID bId) {
		final int[] left = {Math.max(1, cfg.duelCountdown())};
		Bukkit.getScheduler().runTaskTimer(plugin, task -> {
			Player a = Bukkit.getPlayer(aId);
			Player b = Bukkit.getPlayer(bId);
			if (!byPlayer.containsKey(aId) || a == null || b == null) {
				task.cancel();
				return;
			}
			if (left[0] > 0) {
				String n = String.valueOf(left[0]);
				Component chat = Utils.msg("<yellow>Duel starts in <white><n></white>...", Placeholder.unparsed("n", n));
				Title title = Title.title(
						Utils.msg("<yellow><bold><n></bold>", Placeholder.unparsed("n", n)),
						Utils.msg("<gray>Get ready..."),
						Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO));
				for (Player pl : new Player[]{a, b}) {
					pl.showTitle(title);
					pl.sendActionBar(chat);
					pl.sendMessage(chat);
				}
				left[0]--;
			} else {
				// Snap back to corners, drop immunity, top off, start.
				d.armed = true;
				snapToCorner(a, cfg.duelSpawn(0));
				snapToCorner(b, cfg.duelSpawn(1));
				Component fight = Utils.msg("<green><bold>FIGHT!");
				Title fightTitle = Title.title(fight, Component.empty(),
						Title.Times.times(Duration.ZERO, Duration.ofMillis(800), Duration.ofMillis(200)));
				for (Player pl : new Player[]{a, b}) {
					pl.showTitle(fightTitle);
					pl.sendActionBar(fight);
					pl.sendMessage(fight);
				}
				task.cancel();
			}
		}, 0L, 20L);
	}

	private void snapToCorner(Player p, Location corner) {
		if (corner != null) p.teleport(corner);
		// Wipe every effect (countdown Resistance + whatever they walked in with). Pre-duel effects come back at end.
		for (PotionEffect eff : new ArrayList<>(p.getActivePotionEffects())) p.removePotionEffect(eff.getType());
		healFull(p);
		// Fixed state: full hunger, configured saturation and intelligence.
		p.setFoodLevel(20);
		p.setSaturation((float) cfg.duelSaturation());
		setIntelligence(p, cfg.duelIntelligence());
	}

	// ===== queue (single arena, FIFO) =====
	private boolean isQueued(UUID id) {
		for (Queued q : queue) if (q.a.equals(id) || q.b.equals(id)) return true;
		return false;
	}

	/**
	 * Remove the waiting pair containing {@code id} and tell their partner. True if removed. A player
	 * actually dueling is untouched.
	 */
	private boolean removeFromQueue(UUID id) {
		Iterator<Queued> it = queue.iterator();
		while (it.hasNext()) {
			Queued q = it.next();
			if (q.a.equals(id) || q.b.equals(id)) {
				it.remove();
				Player partner = Bukkit.getPlayer(q.a.equals(id) ? q.b : q.a);
				if (partner != null) partner.sendMessage(Utils.msg("<gray>Your queued duel was cancelled - your opponent left the queue."));
				announcePositions();
				return true;
			}
		}
		return false;
	}

	/** Arena cleared: start the next queued pair, skipping offline ones. */
	private void pumpQueue() {
		while (!queue.isEmpty()) {
			Queued q = queue.pollFirst();
			Player a = Bukkit.getPlayer(q.a);
			Player b = Bukkit.getPlayer(q.b);
			if (a == null || b == null || inDuel(q.a) || inDuel(q.b)) {
				Component gone = Utils.msg("<gray>Your queued duel was cancelled - your opponent is no longer available.");
				if (a != null && !inDuel(q.a)) a.sendMessage(gone);
				if (b != null && !inDuel(q.b)) b.sendMessage(gone);
				continue;
			}
			Component go = Utils.msg("<green>The arena is free - your duel is starting!");
			a.sendMessage(go);
			b.sendMessage(go);
			begin(a, b);
			announcePositions();
			return;
		}
	}

	/** Re-tell each waiting pair their queue position. */
	private void announcePositions() {
		int pos = 0;
		for (Queued q : queue) {
			pos++;
			Component msg = Utils.msg("<gray>You're now <white>#<n></white> in the duel queue.", Placeholder.unparsed("n", String.valueOf(pos)));
			Player a = Bukkit.getPlayer(q.a);
			Player b = Bukkit.getPlayer(q.b);
			if (a != null) a.sendMessage(msg);
			if (b != null) b.sendMessage(msg);
		}
	}

	/** Lethal blow on the loser; CustomDamage skips the kill and the duel ends here. */
	public void handleDeath(Player loser) {
		Duel d = byPlayer.get(loser.getUniqueId());
		if (d == null) return;
		end(d, Bukkit.getPlayer(d.other(loser.getUniqueId())), loser, false);
	}

	public void handleQuit(Player p) {
		if (removeFromQueue(p.getUniqueId())) return; // only queued, not fighting
		Duel d = byPlayer.get(p.getUniqueId());
		if (d == null) return;
		// Quitting forfeits. finishPlayer restores the quitter's inventory/intel/food before they leave so their
		// real state is what gets saved.
		end(d, Bukkit.getPlayer(d.other(p.getUniqueId())), p, true);
		strandedInArena.add(p.getUniqueId());
	}

	/**
	 * Quit mid-duel: on rejoin move them out of the sealed arena (FFA safezone, else world spawn). Duel and
	 * inventory were already handled on quit. Network sends them to the lobby anyway, so standalone only.
	 */
	public void restoreOnJoin(Player p) {
		if (!strandedInArena.remove(p.getUniqueId())) return;
		Location safe = cfg.ffaSpawn();
		if (safe == null) safe = p.getWorld().getSpawnLocation();
		p.teleport(safe);
	}

	/** /duel leave: forfeit. */
	public boolean leave(Player p) {
		if (removeFromQueue(p.getUniqueId())) {
			p.sendMessage(Utils.msg("<yellow>You left the duel queue"));
			return true;
		}
		Duel d = byPlayer.get(p.getUniqueId());
		if (d == null) {
			p.sendMessage(Utils.msg("<red>You're not in a duel"));
			return true;
		}
		end(d, Bukkit.getPlayer(d.other(p.getUniqueId())), p, true);
		return true;
	}

	/** /kill (or other absolute death) mid-duel = draw, no win/loss. */
	public void draw(Player who) {
		Duel d = byPlayer.get(who.getUniqueId());
		if (d == null) return;
		drawEnd(d);
	}

	private void end(Duel d, Player winner, Player loser, boolean forfeit) {
		byPlayer.remove(d.a);
		byPlayer.remove(d.b);

		// Winner's health BEFORE finishPlayer heals them, to show the loser.
		double winnerHealth = winner != null ? winner.getHealth() + winner.getAbsorptionAmount() : 0;

		if (winner != null && loser != null && stats != null) stats.recordDuel(winner, loser);

		// Heal both now (no death screen) and restore intelligence. Invulnerable during grace before going home.
		finishPlayer(winner, d);
		finishPlayer(loser, d);

		if (winner != null) {
			winner.sendMessage(Utils.msg(forfeit ? "<green>You won (by forfeit)" : "<green>You won the duel!"));
			winner.showTitle(Title.title(
					Utils.msg("<green><bold>VICTORY"),
					Utils.msg("<gray>You defeated <white><o></white>",
							Placeholder.unparsed("o", loser != null ? loser.getName() : "your opponent"))));
		}
		if (loser != null) {
			String winnerName = winner != null ? winner.getName() : "your opponent";
			if (forfeit) {
				loser.sendMessage(Utils.msg("<red>You lost (by forfeit)"));
			} else {
				loser.sendMessage(Utils.msg("<red>You lost the duel <dark_gray>-</dark_gray> <white><o></white> <gray>had</gray> <red><h>❤</red> <gray>left",
						Placeholder.unparsed("o", winnerName),
						Placeholder.unparsed("h", fmt(winnerHealth))));
			}
			loser.showTitle(Title.title(
					Utils.msg("<red><bold>DEFEAT"),
					Utils.msg("<gray>Defeated by <white><o></white> <gray>(<red><h>❤</red><gray>)",
							Placeholder.unparsed("o", winnerName),
							Placeholder.unparsed("h", fmt(winnerHealth)))));
		}

		printMatchStats(d, winner, loser);
		scheduleReturnHome(d);
	}

	/** Draw: both finish, no win/loss. */
	private void drawEnd(Duel d) {
		byPlayer.remove(d.a);
		byPlayer.remove(d.b);
		Player a = Bukkit.getPlayer(d.a);
		Player b = Bukkit.getPlayer(d.b);
		finishPlayer(a, d);
		finishPlayer(b, d);
		Title drawTitle = Title.title(Utils.msg("<yellow><bold>DRAW"), Component.empty());
		for (Player p : new Player[]{a, b}) {
			if (p != null) {
				p.sendMessage(Utils.msg("<yellow>The duel ended in a draw"));
				p.showTitle(drawTitle);
			}
		}
		printMatchStats(d, a, b);
		scheduleReturnHome(d);
	}

	/** Both back to their pre-duel location 5s after the match. */
	private void scheduleReturnHome(Duel d) {
		final UUID aId = d.a, bId = d.b;
		final Location prevA = d.prevA, prevB = d.prevB;
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			Player a = Bukkit.getPlayer(aId);
			Player b = Bukkit.getPlayer(bId);
			returnHome(a, prevA);
			returnHome(b, prevB);
			// Plain Bukkit event, so SkyBlock stays standalone. A glue plugin may listen to send players from another
			// server back (its transfer makes returnHome above a no-op for them).
			Bukkit.getPluginManager().callEvent(new DuelEndEvent(a, b));
			// Arena empty: free it, pull in the next queued pair.
			arenaOccupied = false;
			pumpQueue();
		}, RETURN_DELAY_TICKS);
	}

	private void finishPlayer(Player p, Duel d) {
		if (p == null) return;
		healFull(p);
		p.setFireTicks(0);
		restoreIntelligence(p, d);
		restoreFood(p, d);
		restoreInventory(p, d);
		restoreGameMode(p, d);
		restoreEffects(p, d);
		// Invulnerable during the 5s grace so neither is re-hit before going home.
		p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, RETURN_DELAY_TICKS + 20, MAX_RESISTANCE, false, false));
	}

	private void restoreInventory(Player p, Duel d) {
		ItemStack[] saved = d.a.equals(p.getUniqueId()) ? d.invA : d.invB;
		if (saved != null) {
			p.getInventory().setContents(saved);
			p.updateInventory();
		}
	}

	private static ItemStack[] cloneContents(ItemStack[] src) {
		ItemStack[] out = new ItemStack[src.length];
		for (int i = 0; i < src.length; i++) out[i] = src[i] == null ? null : src[i].clone();
		return out;
	}

	private void restoreFood(Player p, Duel d) {
		if (d.a.equals(p.getUniqueId())) {
			p.setFoodLevel(d.foodA);
			p.setSaturation(d.satA);
		} else {
			p.setFoodLevel(d.foodB);
			p.setSaturation(d.satB);
		}
	}

	private void restoreGameMode(Player p, Duel d) {
		GameMode gm = d.a.equals(p.getUniqueId()) ? d.gmA : d.gmB;
		if (gm != null) p.setGameMode(gm);
	}

	/** Wipe duel effects, re-apply saved pre-duel ones. */
	private void restoreEffects(Player p, Duel d) {
		Collection<PotionEffect> saved = d.a.equals(p.getUniqueId()) ? d.effA : d.effB;
		for (PotionEffect e : new ArrayList<>(p.getActivePotionEffects())) p.removePotionEffect(e.getType());
		if (saved != null) for (PotionEffect e : saved) p.addPotionEffect(e);
	}

	/**
	 * Hit for the match summary. {@code hits} counts every connect including i-framed ones; {@code iframes}
	 * counts only those, so landed = hits - iframes. Crits count only if not i-framed.
	 */
	public void recordHit(Player attacker, Player victim, double damage, boolean crit, boolean iframe) {
		Duel d = byPlayer.get(attacker.getUniqueId());
		if (d == null) return;
		if (d.a.equals(attacker.getUniqueId())) {
			d.dmgA += damage;
			d.hitsA++;
			if (iframe) d.iframesA++;
			else if (crit) d.critsA++;
		} else if (d.b.equals(attacker.getUniqueId())) {
			d.dmgB += damage;
			d.hitsB++;
			if (iframe) d.iframesB++;
			else if (crit) d.critsB++;
		}
	}

	/** Mana spent on an ability, for the match summary. */
	public void recordMana(Player p, int amount) {
		Duel d = byPlayer.get(p.getUniqueId());
		if (d == null) return;
		if (d.a.equals(p.getUniqueId())) d.manaUsedA += amount;
		else if (d.b.equals(p.getUniqueId())) d.manaUsedB += amount;
	}

	/** Swing/shot, for accuracy (landed / attempts). */
	public void recordAttempt(Player p) {
		Duel d = byPlayer.get(p.getUniqueId());
		if (d == null || !d.armed) return;
		if (d.a.equals(p.getUniqueId())) d.attemptsA++;
		else if (d.b.equals(p.getUniqueId())) d.attemptsB++;
	}

	public void recordHeal(Player p, double amount) {
		Duel d = byPlayer.get(p.getUniqueId());
		if (d == null) return;
		if (d.a.equals(p.getUniqueId())) d.healedA += amount;
		else if (d.b.equals(p.getUniqueId())) d.healedB += amount;
	}

	public void recordFood(Player p) {
		Duel d = byPlayer.get(p.getUniqueId());
		if (d == null) return;
		if (d.a.equals(p.getUniqueId())) d.foodEatenA++;
		else if (d.b.equals(p.getUniqueId())) d.foodEatenB++;
	}

	private void returnHome(Player p, Location prev) {
		if (p == null) return;
		p.removePotionEffect(PotionEffectType.RESISTANCE);
		if (prev != null) p.teleport(prev);
	}

	private void printMatchStats(Duel d, Player one, Player two) {
		Component summary = Utils.msg("""
				<gray><st>                                                  </st>
				<yellow><bold>1v1 Summary</bold></yellow>
				<white><na></white><gray>:</gray> <red><da> dmg</red> <dark_gray>|</dark_gray> <aqua><lna>/<ha>/<ta> hits (<acca>%)</aqua> <dark_gray>|</dark_gray> <yellow><cra> crits</yellow> <dark_gray>|</dark_gray> <light_purple><maa> mana</light_purple> <dark_gray>|</dark_gray> <green><hea> healed</green> <dark_gray>|</dark_gray> <gold><foa> food</gold>
				<white><nb></white><gray>:</gray> <red><db> dmg</red> <dark_gray>|</dark_gray> <aqua><lnb>/<hb>/<tb> hits (<accb>%)</aqua> <dark_gray>|</dark_gray> <yellow><crb> crits</yellow> <dark_gray>|</dark_gray> <light_purple><mab> mana</light_purple> <dark_gray>|</dark_gray> <green><heb> healed</green> <dark_gray>|</dark_gray> <gold><fob> food</gold>
				<gray><st>                                                  </st>""",
				Placeholder.unparsed("na", nameOf(d.a)),
				Placeholder.unparsed("da", fmt(d.dmgA)),
				Placeholder.unparsed("lna", String.valueOf(d.hitsA - d.iframesA)),
				Placeholder.unparsed("ha", String.valueOf(d.hitsA)),
				Placeholder.unparsed("ta", String.valueOf(d.attemptsA)),
				Placeholder.unparsed("acca", accuracy(d.hitsA, d.attemptsA)),
				Placeholder.unparsed("cra", String.valueOf(d.critsA)),
				Placeholder.unparsed("maa", String.valueOf(d.manaUsedA)),
				Placeholder.unparsed("hea", fmt(d.healedA)),
				Placeholder.unparsed("foa", String.valueOf(d.foodEatenA)),
				Placeholder.unparsed("nb", nameOf(d.b)),
				Placeholder.unparsed("db", fmt(d.dmgB)),
				Placeholder.unparsed("lnb", String.valueOf(d.hitsB - d.iframesB)),
				Placeholder.unparsed("hb", String.valueOf(d.hitsB)),
				Placeholder.unparsed("tb", String.valueOf(d.attemptsB)),
				Placeholder.unparsed("accb", accuracy(d.hitsB, d.attemptsB)),
				Placeholder.unparsed("crb", String.valueOf(d.critsB)),
				Placeholder.unparsed("mab", String.valueOf(d.manaUsedB)),
				Placeholder.unparsed("heb", fmt(d.healedB)),
				Placeholder.unparsed("fob", String.valueOf(d.foodEatenB)));
		if (one != null) one.sendMessage(summary);
		if (two != null) two.sendMessage(summary);
	}

	private static String accuracy(int hits, int attempts) {
		return attempts <= 0 ? "0" : String.format("%.0f", hits * 100.0 / attempts);
	}

	private static String nameOf(UUID id) {
		Player p = Bukkit.getPlayer(id);
		if (p != null) return p.getName();
		String n = Bukkit.getOfflinePlayer(id).getName();
		return n != null ? n : "Unknown";
	}

	private static String fmt(double d) {
		return String.format("%.1f", d);
	}

	// ===== intelligence swap =====
	/** Package-visible so the FFA respawn in {@link PvpListener} can reuse it. */
	static int readIntelligence(Player p) {
		try {
			return Plugin.getIntelligence(p).getScore();
		} catch (Exception e) {
			return -1;  // objective missing
		}
	}

	/** Package-visible so the FFA respawn in {@link PvpListener} can reuse it. */
	static void setIntelligence(Player p, int value) {
		try {
			Score s = Plugin.getIntelligence(p);
			s.setScore(value);
			Plugin.sendIntelligenceBar(p, s);
		} catch (Exception ignored) {
		}
	}

	private void restoreIntelligence(Player p, Duel d) {
		int v = d.a.equals(p.getUniqueId()) ? d.intelA : d.intelB;
		if (v >= 0) setIntelligence(p, v);
	}

	private boolean notEnabled(Player p) {
		if (!cfg.duelEnabled()) {
			p.sendMessage(Utils.msg("<red>Duels are disabled on this server"));
			return true;
		}
		return false;
	}

	private static void healFull(Player p) {
		var attr = p.getAttribute(Attribute.MAX_HEALTH);
		if (attr != null) p.setHealth(attr.getValue());
	}

	/** Pair waiting for the arena. */
	private static final class Queued {
		final UUID a, b;
		Queued(UUID a, UUID b) {
			this.a = a;
			this.b = b;
		}
	}

	private static final class Duel {
		final UUID a, b;
		final Location prevA, prevB;
		int intelA = -1, intelB = -1;
		int foodA = 20, foodB = 20;
		float satA, satB;
		GameMode gmA, gmB;          // saved real game mode
		ItemStack[] invA, invB;     // saved real inventory
		Collection<PotionEffect> effA, effB; // saved real effects
		double dmgA, dmgB;          // damage dealt
		int hitsA, hitsB;           // hits landed
		int attemptsA, attemptsB;   // hit attempts (melee swings + bow shots)
		double healedA, healedB;    // health regained
		int foodEatenA, foodEatenB; // food eaten
		int critsA, critsB;         // crits landed
		int iframesA, iframesB;     // hits into the victim's i-frames
		int manaUsedA, manaUsedB;   // mana spent on abilities
		boolean armed;

		Duel(UUID a, UUID b, Location prevA, Location prevB) {
			this.a = a;
			this.b = b;
			this.prevA = prevA;
			this.prevB = prevB;
		}

		boolean has(UUID id) {
			return a.equals(id) || b.equals(id);
		}

		UUID other(UUID id) {
			return a.equals(id) ? b : a;
		}
	}
}
