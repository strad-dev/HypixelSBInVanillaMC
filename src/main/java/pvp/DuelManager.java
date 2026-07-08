package pvp;

import misc.Plugin;
import misc.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;

import java.time.Duration;
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 1v1 duel lifecycle, layered on top of SkyBlock's CustomDamage system (it never registers its own
 * damage listener; CustomDamage drives it through {@link PvpHooks}). Handles invites, the
 * network-facing force-pair, arena placement + countdown, win-on-(would-be-)death without a death
 * screen, intelligence swapping, stats, and returning both players to their pre-duel location.
 */
public class DuelManager {
	// Resistance V makes a player fully immune under CustomDamage (it reduces damage by 20% per level).
	private static final int MAX_RESISTANCE = 4;
	private static final int RETURN_DELAY_TICKS = 100;  // 5s
	private static final long INVITE_TIMEOUT_TICKS = 1200L; // 60s before a duel request expires

	private final JavaPlugin plugin;
	private final PvpConfig cfg;
	private final PvpStats stats;
	private final PvpLoadouts loadouts;

	private final Map<UUID, Duel> byPlayer = new HashMap<>();
	private final Map<UUID, UUID> invites = new HashMap<>();         // target -> inviter
	private final Map<UUID, Integer> inviteTokens = new HashMap<>(); // target -> token, so a stale timeout can't cancel a newer request
	private int inviteCounter = 0;
	// Players who disconnected mid-duel: the return-home teleport (5s later) skips them while offline, so
	// move them out of the arena to a safe spot on their next join instead.
	private final Set<UUID> strandedInArena = new HashSet<>();
	// Single arena: at most one duel runs at a time. Pairs that accept while it's busy wait here (FIFO)
	// and start automatically when it frees. arenaOccupied stays true through the post-match grace period
	// (until both fighters have been returned home), so the next pair never spawns on top of them.
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
		// Expire the request after 60s if it's still the active one for this target.
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

	/** The receiver declines a pending duel request. */
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

	/** The sender cancels their outgoing duel request. */
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
		// Single arena: if a duel is in progress (or still clearing out), queue this pair instead of
		// spawning them on top of the current fight. They start automatically when the arena frees.
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

	/** Place a pair into the (now free) arena and run the countdown. */
	private void begin(Player a, Player b) {
		Location sa = cfg.duelSpawn(0);
		Location sb = cfg.duelSpawn(1);
		if (sa == null || sb == null) {
			a.sendMessage(Utils.msg("<red>The duel arena isn't configured yet"));
			return;
		}
		arenaOccupied = true;
		Duel d = new Duel(a.getUniqueId(), b.getUniqueId(), a.getLocation().clone(), b.getLocation().clone());
		// Stash each player's real intelligence, hunger and saturation so the duel can run everyone at
		// fixed values and restore the originals when it ends.
		d.intelA = readIntelligence(a);
		d.intelB = readIntelligence(b);
		d.foodA = a.getFoodLevel();
		d.foodB = b.getFoodLevel();
		d.satA = a.getSaturation();
		d.satB = b.getSaturation();
		// Save the real inventories so the standardized kit can replace them and be restored at the end.
		d.invA = cloneContents(a.getInventory().getContents());
		d.invB = cloneContents(b.getInventory().getContents());
		// Save each player's real potion effects; they're wiped at FIGHT and restored when the duel ends.
		d.effA = new ArrayList<>(a.getActivePotionEffects());
		d.effB = new ArrayList<>(b.getActivePotionEffects());
		// Save game modes; both players fight in Adventure and are restored to their originals at the end.
		d.gmA = a.getGameMode();
		d.gmB = b.getGameMode();
		byPlayer.put(a.getUniqueId(), d);
		byPlayer.put(b.getUniqueId(), d);
		prepare(a, sa);
		prepare(b, sb);
		// Intelligence, saturation, and the corner snap all happen when the countdown ends (see snapToCorner).
		runCountdown(d, a.getUniqueId(), b.getUniqueId());
	}

	private void prepare(Player p, Location spawn) {
		p.teleport(spawn);
		p.setGameMode(GameMode.ADVENTURE); // duels are fought in Adventure; restored in finishPlayer
		healFull(p);
		p.setFoodLevel(20);
		// Use the player's saved PvP loadout if they have one; otherwise the standardized kit. (Their real
		// inventory was saved in start() and is restored when the duel ends.)
		ItemStack[] saved = loadouts == null ? null : loadouts.get(p.getUniqueId());
		if (saved != null) PvpLoadouts.apply(p, saved);
		else DuelKit.apply(p);
		// Players may walk around during the countdown; max Resistance keeps them invulnerable until FIGHT.
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
				// Movement was free during the countdown, so snap both back to their corners, drop the
				// immunity, top them off, and start.
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
		// Battle start: wipe every effect (the countdown Resistance + anything the player walked in with) so
		// both fight on a clean slate. Their pre-duel effects are restored when the duel ends.
		for (PotionEffect eff : new ArrayList<>(p.getActivePotionEffects())) p.removePotionEffect(eff.getType());
		healFull(p);
		// Fixed combat state, applied when the countdown ends: hunger always full, configurable
		// saturation and intelligence.
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
	 * Remove a waiting pair containing {@code id} (they quit or left the queue) and tell their partner.
	 * Returns true if an entry was removed. A player who is actually dueling is left untouched.
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

	/** Arena just cleared: start the next still-valid queued pair, skipping any who went offline. */
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

	/** Re-tell each still-waiting pair their current position (positions shift as duels start / people leave). */
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

	/** A lethal blow landed on the loser; CustomDamage skips the kill and we end the duel here. */
	public void handleDeath(Player loser) {
		Duel d = byPlayer.get(loser.getUniqueId());
		if (d == null) return;
		end(d, Bukkit.getPlayer(d.other(loser.getUniqueId())), loser, false);
	}

	public void handleQuit(Player p) {
		if (removeFromQueue(p.getUniqueId())) return; // was only waiting in the queue, not fighting
		Duel d = byPlayer.get(p.getUniqueId());
		if (d == null) return;
		// Disconnecting forfeits: the opponent wins, and we restore the quitter's inventory/intel/food
		// (via finishPlayer in end()) before they fully leave so their real state is saved.
		end(d, Bukkit.getPlayer(d.other(p.getUniqueId())), p, true);
		strandedInArena.add(p.getUniqueId()); // move them out of the arena on their next join
	}

	/**
	 * If this player disconnected mid-duel, move them out of the arena to a safe spot on rejoin (the FFA
	 * safezone, else the world spawn). Their duel was already ended/forfeited and their inventory restored
	 * when they quit; this just gets them out of the (otherwise sealed) arena. Network servers send the
	 * player to the lobby on reconnect anyway, so this only matters for a standalone / direct pvp relog.
	 */
	public void restoreOnJoin(Player p) {
		if (!strandedInArena.remove(p.getUniqueId())) return;
		Location safe = cfg.ffaSpawn();
		if (safe == null) safe = p.getWorld().getSpawnLocation();
		p.teleport(safe);
	}

	/** A player leaves their duel via /duel leave - they forfeit and the opponent wins. */
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

	/** A /kill (or other absolute death) during a 1v1 ends it as a draw - no win/loss recorded. */
	public void draw(Player who) {
		Duel d = byPlayer.get(who.getUniqueId());
		if (d == null) return;
		drawEnd(d);
	}

	private void end(Duel d, Player winner, Player loser, boolean forfeit) {
		byPlayer.remove(d.a);
		byPlayer.remove(d.b);

		// Capture the winner's remaining health BEFORE finishPlayer heals them, to show the loser.
		double winnerHealth = winner != null ? winner.getHealth() + winner.getAbsorptionAmount() : 0;

		if (winner != null && loser != null && stats != null) stats.recordDuel(winner, loser);

		// Heal both immediately (the loser never sees a death screen) and restore their real
		// intelligence. They stay invulnerable for the grace period before being sent home.
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

	/** Ends the duel as a draw (e.g. a /kill): both players finish, no win/loss recorded. */
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

	/** Returns both players to their pre-duel location 5s after the match ends. */
	private void scheduleReturnHome(Duel d) {
		final UUID aId = d.a, bId = d.b;
		final Location prevA = d.prevA, prevB = d.prevB;
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			Player a = Bukkit.getPlayer(aId);
			Player b = Bukkit.getPlayer(bId);
			returnHome(a, prevA);
			returnHome(b, prevB);
			// On the network, hand off to the network plugin so it can send players who came from another
			// server back home (it transfers them off pvp; the local returnHome above is then a harmless
			// no-op for them). Gated by config so a standalone server never logs an unknown command.
			notifyNetworkDuelEnd(a, b);
			// Arena is now empty - free it and pull in the next queued pair, if any.
			arenaOccupied = false;
			pumpQueue();
		}, RETURN_DELAY_TICKS);
	}

	private void notifyNetworkDuelEnd(Player a, Player b) {
		if (!cfg.duelNetwork() || (a == null && b == null)) return;
		String cmd = "networkduelend " + (a != null ? a.getName() : "-") + " " + (b != null ? b.getName() : "-");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
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
		// Invulnerable during the 5s grace so neither player can be re-hit before returning home.
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

	/** Wipe any effects gained during the duel, then re-apply the player's saved pre-duel potion effects. */
	private void restoreEffects(Player p, Duel d) {
		Collection<PotionEffect> saved = d.a.equals(p.getUniqueId()) ? d.effA : d.effB;
		for (PotionEffect e : new ArrayList<>(p.getActivePotionEffects())) p.removePotionEffect(e.getType());
		if (saved != null) for (PotionEffect e : saved) p.addPotionEffect(e);
	}

	/**
	 * Accumulates a landed hit's damage for the end-of-match summary. {@code hits} counts every
	 * connect (including those negated by the victim's i-frames); {@code iframes} counts only the
	 * negated ones, so effective landed hits = hits - iframes. Crits only count when the hit actually
	 * landed (not negated by i-frames).
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

	/** Accumulates intelligence (mana) spent on an ability for the end-of-match summary. */
	public void recordMana(Player p, int amount) {
		Duel d = byPlayer.get(p.getUniqueId());
		if (d == null) return;
		if (d.a.equals(p.getUniqueId())) d.manaUsedA += amount;
		else if (d.b.equals(p.getUniqueId())) d.manaUsedB += amount;
	}

	/** A swing/shot - counts toward hit-accuracy (landed hits / attempts). */
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

	/** Sends both players a summary of the match they just fought. */
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
	private int readIntelligence(Player p) {
		try {
			return Plugin.getIntelligence(p).getScore();
		} catch (Exception e) {
			return -1;  // objective missing; nothing to restore
		}
	}

	private void setIntelligence(Player p, int value) {
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

	/** A pair waiting for the arena to free up. */
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
		GameMode gmA, gmB;          // saved real game mode (restored when the duel ends)
		ItemStack[] invA, invB;     // saved real inventory (restored when the duel ends)
		Collection<PotionEffect> effA, effB; // saved real potion effects (wiped at FIGHT, restored at end)
		double dmgA, dmgB;          // damage dealt by each player this match
		int hitsA, hitsB;           // hits landed by each player this match
		int attemptsA, attemptsB;   // hit attempts (melee swings + bow shots)
		double healedA, healedB;    // health regained this match
		int foodEatenA, foodEatenB; // food items consumed this match
		int critsA, critsB;         // critical hits landed this match
		int iframesA, iframesB;     // hits landed during the victim's invulnerability frames this match
		int manaUsedA, manaUsedB;   // intelligence (mana) spent on abilities this match
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
