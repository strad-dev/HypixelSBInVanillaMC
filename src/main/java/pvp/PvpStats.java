package pvp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * PvP stats writer for the pvp server. Held in memory and flushed to the shared
 * {@code pvp-stats.json} periodically (so frequent per-hit damage updates don't thrash the disk)
 * plus immediately on milestones (kills, deaths, duel results). The pvp server is the only writer;
 * other servers read the file directly for /stats.
 */
public class PvpStats {
	private final Path file;
	private final boolean enabled;
	private Data data;
	private boolean dirty;

	public PvpStats(PvpConfig cfg) {
		this.enabled = cfg.statsEnabled();
		this.file = cfg.statsFile();
		this.data = PvpJson.load(file, Data.class, null);
		if (data == null || data.players == null) {
			data = new Data();
			data.players = new LinkedHashMap<>();
		}
	}

	public void start(JavaPlugin plugin) {
		Bukkit.getScheduler().runTaskTimer(plugin, this::flush, 100L, 100L);
	}

	public void flush() {
		if (enabled && dirty) {
			PvpJson.save(file, data);
			dirty = false;
		}
	}

	public boolean enabled() {
		return enabled;
	}

	private Entry entry(UUID id, String name) {
		Entry e = data.players.computeIfAbsent(id.toString(), k -> new Entry());
		if (name != null) e.name = name;
		return e;
	}

	// ===== FFA =====
	public void recordKill(Player killer, Player victim) {
		if (!enabled) return;
		Entry k = entry(killer.getUniqueId(), killer.getName());
		k.kills++;
		k.killStreak++;
		k.bestKillStreak = Math.max(k.bestKillStreak, k.killStreak);
		Entry v = entry(victim.getUniqueId(), victim.getName());
		v.deaths++;
		v.killStreak = 0;
		dirty = true;
		flush();
	}

	public void recordDeath(Player victim) {
		if (!enabled) return;
		Entry v = entry(victim.getUniqueId(), victim.getName());
		v.deaths++;
		v.killStreak = 0;
		dirty = true;
		flush();
	}

	// ===== 1v1 =====
	public void recordDuel(Player winner, Player loser) {
		if (!enabled) return;
		Entry w = entry(winner.getUniqueId(), winner.getName());
		w.wins++;
		w.matches++;
		w.winStreak++;
		w.bestWinStreak = Math.max(w.bestWinStreak, w.winStreak);
		Entry l = entry(loser.getUniqueId(), loser.getName());
		l.losses++;
		l.matches++;
		l.winStreak = 0;
		dirty = true;
		flush();
	}

	// ===== combat (frequent; flushed on the timer) =====
	public void addDamage(Player dealer, Player taker, double amount) {
		if (!enabled) return;
		entry(dealer.getUniqueId(), dealer.getName()).damageDealt += amount;
		entry(taker.getUniqueId(), taker.getName()).damageTaken += amount;
		dirty = true;
	}

	public void addHit(Player dealer, boolean arrow) {
		if (!enabled) return;
		Entry e = entry(dealer.getUniqueId(), dealer.getName());
		e.hitsLanded++;
		if (arrow) e.arrowsLanded++;
		dirty = true;
	}

	public void reportCombo(Player p, int combo) {
		if (!enabled) return;
		Entry e = entry(p.getUniqueId(), p.getName());
		if (combo > e.longestCombo) {
			e.longestCombo = combo;
			dirty = true;
		}
	}

	// ===== on-disk shapes (also read by StatsCommand) =====
	public static class Data {
		public Map<String, Entry> players = new LinkedHashMap<>();
	}

	public static class Entry {
		public String name = "";
		// FFA
		public int kills, deaths, killStreak, bestKillStreak;
		// 1v1
		public int wins, losses, matches, winStreak, bestWinStreak;
		// combat
		public double damageDealt, damageTaken;
		public int hitsLanded, arrowsLanded, longestCombo;

		public double kd() {
			return deaths == 0 ? kills : (double) kills / deaths;
		}

		public double wl() {
			return losses == 0 ? wins : (double) wins / losses;
		}
	}
}
