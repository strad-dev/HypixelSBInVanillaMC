package pvp;

import misc.Plugin;
import misc.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;

import java.time.Duration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;

import java.util.HashMap;
import java.util.Map;
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

	private final JavaPlugin plugin;
	private final PvpConfig cfg;
	private final PvpStats stats;

	private final Map<UUID, Duel> byPlayer = new HashMap<>();
	private final Map<UUID, UUID> invites = new HashMap<>(); // target -> inviter

	public DuelManager(JavaPlugin plugin, PvpConfig cfg, PvpStats stats) {
		this.plugin = plugin;
		this.cfg = cfg;
		this.stats = stats;
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
		if (from.equals(to)) {
			from.sendMessage(Utils.msg("<red>You can't duel yourself."));
			return;
		}
		invites.put(to.getUniqueId(), from.getUniqueId());
		to.sendMessage(Utils.msg("<yellow><gold><s></gold> is challenging you!  Click <click:run_command:'/duel accept'><gold><u>here</u></gold></click> to accept",
				Placeholder.unparsed("s", from.getName())));
		from.sendMessage(Utils.msg("<green>Challenge sent to <white><t></white>.", Placeholder.unparsed("t", to.getName())));
	}

	public void accept(Player target) {
		UUID inv = invites.remove(target.getUniqueId());
		Player from = inv == null ? null : Bukkit.getPlayer(inv);
		if (from == null) {
			target.sendMessage(Utils.msg("<red>You have no pending duel invite."));
			return;
		}
		start(from, target);
	}

	// ===== match =====
	public void start(Player a, Player b) {
		if (notEnabled(a)) return;
		if (inDuel(a.getUniqueId()) || inDuel(b.getUniqueId())) {
			a.sendMessage(Utils.msg("<red>One of you is already in a duel."));
			return;
		}
		Location sa = cfg.duelSpawn(0);
		Location sb = cfg.duelSpawn(1);
		if (sa == null || sb == null) {
			a.sendMessage(Utils.msg("<red>The duel arena isn't configured yet."));
			return;
		}
		Duel d = new Duel(a.getUniqueId(), b.getUniqueId(), a.getLocation().clone(), b.getLocation().clone());
		// Stash each player's real intelligence, hunger and saturation so the duel can run everyone at
		// fixed values and restore the originals when it ends.
		d.intelA = readIntelligence(a);
		d.intelB = readIntelligence(b);
		d.foodA = a.getFoodLevel();
		d.foodB = b.getFoodLevel();
		d.satA = a.getSaturation();
		d.satB = b.getSaturation();
		byPlayer.put(a.getUniqueId(), d);
		byPlayer.put(b.getUniqueId(), d);
		prepare(a, sa);
		prepare(b, sb);
		// Intelligence, saturation, and the corner snap all happen when the countdown ends (see snapToCorner).
		runCountdown(d, a.getUniqueId(), b.getUniqueId());
	}

	private void prepare(Player p, Location spawn) {
		p.teleport(spawn);
		healFull(p);
		p.setFoodLevel(20);
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
						Title.Times.times(Duration.ZERO, Duration.ofMillis(900), Duration.ofMillis(100)));
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
		p.removePotionEffect(PotionEffectType.RESISTANCE);
		healFull(p);
		// Fixed combat state, applied when the countdown ends: hunger always full, configurable
		// saturation and intelligence.
		p.setFoodLevel(20);
		p.setSaturation((float) cfg.duelSaturation());
		setIntelligence(p, cfg.duelIntelligence());
	}

	/** A lethal blow landed on the loser; CustomDamage skips the kill and we end the duel here. */
	public void handleDeath(Player loser) {
		Duel d = byPlayer.get(loser.getUniqueId());
		if (d == null) return;
		end(d, Bukkit.getPlayer(d.other(loser.getUniqueId())), loser);
	}

	public void handleQuit(Player p) {
		Duel d = byPlayer.get(p.getUniqueId());
		if (d == null) return;
		end(d, Bukkit.getPlayer(d.other(p.getUniqueId())), p);
	}

	private void end(Duel d, Player winner, Player loser) {
		byPlayer.remove(d.a);
		byPlayer.remove(d.b);

		if (winner != null && loser != null && stats != null) stats.recordDuel(winner, loser);

		// Heal both immediately (the loser never sees a death screen) and restore their real
		// intelligence. They stay invulnerable for the grace period before being sent home.
		finishPlayer(winner, d);
		finishPlayer(loser, d);

		if (winner != null) {
			winner.sendMessage(Utils.msg("<green>You won the duel!"));
			winner.showTitle(Title.title(
					Utils.msg("<green><bold>VICTORY"),
					Utils.msg("<gray>You defeated <white><o></white>",
							Placeholder.unparsed("o", loser != null ? loser.getName() : "your opponent"))));
		}
		if (loser != null) {
			loser.sendMessage(Utils.msg("<red>You lost the duel."));
			loser.showTitle(Title.title(
					Utils.msg("<red><bold>DEFEAT"),
					Utils.msg("<gray>You were defeated by <white><o></white>",
							Placeholder.unparsed("o", winner != null ? winner.getName() : "your opponent"))));
		}

		printMatchStats(d, winner, loser);

		// Return both to their pre-duel location 5s after the match ends.
		final UUID aId = d.a, bId = d.b;
		final Location prevA = d.prevA, prevB = d.prevB;
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			returnHome(Bukkit.getPlayer(aId), prevA);
			returnHome(Bukkit.getPlayer(bId), prevB);
		}, RETURN_DELAY_TICKS);
	}

	private void finishPlayer(Player p, Duel d) {
		if (p == null) return;
		healFull(p);
		p.setFireTicks(0);
		restoreIntelligence(p, d);
		restoreFood(p, d);
		// Invulnerable during the 5s grace so neither player can be re-hit before returning home.
		p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, RETURN_DELAY_TICKS + 20, MAX_RESISTANCE, false, false));
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

	/** Accumulates a landed hit's damage for the end-of-match summary. */
	public void recordHit(Player attacker, Player victim, double damage) {
		Duel d = byPlayer.get(attacker.getUniqueId());
		if (d == null) return;
		if (d.a.equals(attacker.getUniqueId())) {
			d.dmgA += damage;
			d.hitsA++;
		} else if (d.b.equals(attacker.getUniqueId())) {
			d.dmgB += damage;
			d.hitsB++;
		}
	}

	private void returnHome(Player p, Location prev) {
		if (p == null) return;
		p.removePotionEffect(PotionEffectType.RESISTANCE);
		if (prev != null) p.teleport(prev);
	}

	/** Sends both players a summary of the match they just fought (damage dealt + hits landed). */
	private void printMatchStats(Duel d, Player winner, Player loser) {
		Component summary = Utils.msg("""
				<gray><st>                                        </st>
				<yellow><bold>1v1 Summary</bold>
				<white><na></white><gray>:</gray> <red><da></red> <gray>dmg,</gray> <aqua><ha></aqua> <gray>hits</gray>
				<white><nb></white><gray>:</gray> <red><db></red> <gray>dmg,</gray> <aqua><hb></aqua> <gray>hits</gray>
				<gray><st>                                        </st>""",
				Placeholder.unparsed("na", nameOf(d.a)),
				Placeholder.unparsed("da", fmt(d.dmgA)),
				Placeholder.unparsed("ha", String.valueOf(d.hitsA)),
				Placeholder.unparsed("nb", nameOf(d.b)),
				Placeholder.unparsed("db", fmt(d.dmgB)),
				Placeholder.unparsed("hb", String.valueOf(d.hitsB)));
		if (winner != null) winner.sendMessage(summary);
		if (loser != null) loser.sendMessage(summary);
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
			p.sendMessage(Utils.msg("<red>Duels are disabled on this server."));
			return true;
		}
		return false;
	}

	private static void healFull(Player p) {
		var attr = p.getAttribute(Attribute.MAX_HEALTH);
		if (attr != null) p.setHealth(attr.getValue());
	}

	private static final class Duel {
		final UUID a, b;
		final Location prevA, prevB;
		int intelA = -1, intelB = -1;
		int foodA = 20, foodB = 20;
		float satA, satB;
		double dmgA, dmgB;   // damage dealt by each player this match
		int hitsA, hitsB;    // hits landed by each player this match
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
