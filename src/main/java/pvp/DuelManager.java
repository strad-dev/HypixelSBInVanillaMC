package pvp;

import misc.Utils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 1v1 duel lifecycle (SkyBlock-owned, standalone). Handles invites, the network-facing
 * force-pair, arena placement + countdown, win-on-death, stats, and returning players to the FFA
 * spawn (or their pre-duel location if no FFA spawn is configured).
 */
public class DuelManager {
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
		to.sendMessage(Utils.msg("<gold><s></gold> <yellow>challenged you to a 1v1! <white>/duel accept</white>.",
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
		byPlayer.put(a.getUniqueId(), d);
		byPlayer.put(b.getUniqueId(), d);
		prepare(a, sa);
		prepare(b, sb);
		runCountdown(d, a.getUniqueId(), b.getUniqueId());
	}

	private void prepare(Player p, Location spawn) {
		p.teleport(spawn);
		healFull(p);
		p.setFoodLevel(20);
		p.setWalkSpeed(0f); // frozen during countdown
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
				a.sendActionBar(Utils.msg("<yellow>Duel starts in <white><n></white>...", Placeholder.unparsed("n", String.valueOf(left[0]))));
				b.sendActionBar(Utils.msg("<yellow>Duel starts in <white><n></white>...", Placeholder.unparsed("n", String.valueOf(left[0]))));
				left[0]--;
			} else {
				d.armed = true;
				a.setWalkSpeed(0.2f);
				b.setWalkSpeed(0.2f);
				a.sendActionBar(Utils.msg("<green><bold>FIGHT!"));
				b.sendActionBar(Utils.msg("<green><bold>FIGHT!"));
				task.cancel();
			}
		}, 0L, 20L);
	}

	/** The loser would have died — end the duel in the opponent's favour. */
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

		// Return to the FFA spawn if one exists, else each player's pre-duel location.
		Location ffa = (cfg.ffaEnabled() ? cfg.ffaSpawn() : null);
		restoreAndSend(winner, ffa, winner != null ? d.prevFor(winner.getUniqueId()) : null);
		restoreAndSend(loser, ffa, loser != null ? d.prevFor(loser.getUniqueId()) : null);

		if (winner != null) winner.sendMessage(Utils.msg("<green>You won the duel!"));
		if (loser != null) loser.sendMessage(Utils.msg("<red>You lost the duel."));
	}

	private void restoreAndSend(Player p, Location ffa, Location prev) {
		if (p == null) return;
		p.setWalkSpeed(0.2f);
		healFull(p);
		p.setFoodLevel(20);
		Location dest = ffa != null ? ffa : prev;
		if (dest != null) p.teleport(dest);
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

		Location prevFor(UUID id) {
			return a.equals(id) ? prevA : prevB;
		}
	}
}
