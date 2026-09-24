package pvp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * PvP + chat config (config.yml). Everything defaults to off/empty so SkyBlock is inert off the pvp server.
 */
public class PvpConfig {
	private final JavaPlugin plugin;

	public PvpConfig(JavaPlugin plugin) {
		this.plugin = plugin;
		plugin.saveDefaultConfig();
		mergeMissingDefaults();
	}

	/**
	 * Adds keys in the bundled config.yml missing from the on-disk one, with defaults <b>and their comments</b>.
	 * Existing values and comments untouched, so an old config picks up new options without being deleted.
	 * Additive only: a key dropped from the bundled file stays. Comments are copied by hand since {@code set}
	 * carries only the value; otherwise a new option showed up as a bare {@code manhunt: false} at the end.
	 */
	private void mergeMissingDefaults() {
		InputStream in = plugin.getResource("config.yml");
		if (in == null) return;
		YamlConfiguration bundled = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
		FileConfiguration live = plugin.getConfig();

		// Settle which sections are new BEFORE writing: copying a leaf creates its parents.
		List<String> newSections = new java.util.ArrayList<>();
		for (String key : bundled.getKeys(true)) {
			if (bundled.isConfigurationSection(key) && !live.contains(key)) newSections.add(key);
		}

		boolean changed = false;
		for (String key : bundled.getKeys(true)) {
			// Leaves only; sections come with their children.
			if (!bundled.isConfigurationSection(key) && !live.contains(key)) {
				live.set(key, bundled.get(key));
				copyComments(bundled, live, key);
				changed = true;
			}
		}
		// Section headers last: the path exists only once its children are written.
		for (String key : newSections) {
			if (live.contains(key)) copyComments(bundled, live, key);
		}

		if (changed) plugin.saveConfig();
	}

	private static void copyComments(YamlConfiguration bundled, FileConfiguration live, String key) {
		List<String> comments = bundled.getComments(key);
		if (!comments.isEmpty()) live.setComments(key, comments);
		List<String> inline = bundled.getInlineComments(key);
		if (!inline.isEmpty()) live.setInlineComments(key, inline);
	}

	private ConfigurationSection cfg() {
		return plugin.getConfig();
	}

	// ===== chat =====
	public boolean chatEnabled() {
		return cfg().getBoolean("chat.enabled", true);
	}

	// ===== FFA =====
	public boolean ffaEnabled() {
		return cfg().getBoolean("pvp.ffa.enabled", false);
	}

	/**
	 * Minimum intelligence on FFA respawn; more is kept, so a respawn never costs mana. -1 leaves it alone.
	 */
	public int ffaRespawnIntelligence() {
		return cfg().getInt("pvp.ffa.respawn-intelligence", 50);
	}

	public String ffaWorld() {
		return cfg().getString("pvp.ffa.world", defaultWorld());
	}

	public Location ffaSpawn() {
		return loc("pvp.ffa.spawn", ffaWorld());
	}

	public Region ffaBounds() {
		return region("pvp.ffa.bounds", ffaWorld());
	}

	public boolean safezoneEnabled() {
		return cfg().getBoolean("pvp.ffa.safezone.enabled", false);
	}

	public Region safezone() {
		return region("pvp.ffa.safezone", ffaWorld());
	}

	// ===== duel =====
	public boolean duelEnabled() {
		return cfg().getBoolean("pvp.duel.enabled", false);
	}

	public String duelWorld() {
		return cfg().getString("pvp.duel.world", defaultWorld());
	}

	public Region duelArena() {
		return region("pvp.duel.arena", duelWorld());
	}

	public int duelCountdown() {
		return cfg().getInt("pvp.duel.countdown", 5);
	}

	/** Intelligence for the whole duel (hunger always full). */
	public int duelIntelligence() {
		return cfg().getInt("pvp.duel.intelligence", 50);
	}

	/** Saturation for the whole duel. */
	public double duelSaturation() {
		return cfg().getDouble("pvp.duel.saturation", 5);
	}

	/**
	 * Duel loadout file. Default {@code plugins/SkyBlock/pvp-loadouts.json} (self-contained); relative paths
	 * resolve there. ABSOLUTE path (network's shared {@code ~/data}) shares loadouts across servers.
	 */
	public Path loadoutsFile() {
		String f = cfg().getString("pvp.duel.loadouts-file", "pvp-loadouts.json");
		Path p = Paths.get(f);
		if (!p.isAbsolute()) p = plugin.getDataFolder().toPath().resolve(f);
		return p.normalize();
	}

	/**
	 * Absolute path for the palette + default kit export ({@code ~/data/pvp-item-catalog.json}), so servers
	 * without SkyBlock offer the same items. {@code null} when unset: standalone doesn't export.
	 */
	public Path catalogFile() {
		String f = cfg().getString("pvp.duel.catalog-file", "");
		if (f == null || f.isBlank()) return null;
		Path p = Paths.get(f);
		if (!p.isAbsolute()) p = plugin.getDataFolder().toPath().resolve(f);
		return p.normalize();
	}

	/** Duel corner i (0 or 1), or null if unset. */
	public Location duelSpawn(int i) {
		List<Map<?, ?>> spawns = cfg().getMapList("pvp.duel.spawns");
		if (i < 0 || i >= spawns.size()) return null;
		Map<?, ?> m = spawns.get(i);
		World w = Bukkit.getWorld(duelWorld());
		if (w == null) return null;
		return new Location(w, num(m, "x"), num(m, "y"), num(m, "z"),
				(float) num(m, "yaw"), (float) num(m, "pitch"));
	}

	// ===== stats =====
	public boolean statsEnabled() {
		return cfg().getBoolean("pvp.stats.enabled", false);
	}

	public Path statsFile() {
		// Default plugins/SkyBlock/pvp-stats.json; relative paths resolve there. ABSOLUTE path (network's
		// ~/data) shares stats across servers.
		String f = cfg().getString("pvp.stats.file", "pvp-stats.json");
		Path p = Paths.get(f);
		if (!p.isAbsolute()) p = plugin.getDataFolder().toPath().resolve(f);
		return p.normalize();
	}

	// ===== helpers =====
	private String defaultWorld() {
		return plugin.getServer().getWorlds().isEmpty() ? "world"
				: plugin.getServer().getWorlds().get(0).getName();
	}

	private Location loc(String path, String worldName) {
		ConfigurationSection s = cfg().getConfigurationSection(path);
		World w = Bukkit.getWorld(worldName);
		if (s == null || w == null) return null;
		return new Location(w, s.getDouble("x"), s.getDouble("y"), s.getDouble("z"),
				(float) s.getDouble("yaw"), (float) s.getDouble("pitch"));
	}

	private Region region(String path, String worldName) {
		List<Double> min = cfg().getDoubleList(path + ".min");
		List<Double> max = cfg().getDoubleList(path + ".max");
		if (min.size() < 3 || max.size() < 3) return null;
		return new Region(worldName,
				new double[]{min.get(0), min.get(1), min.get(2)},
				new double[]{max.get(0), max.get(1), max.get(2)});
	}

	private static double num(Map<?, ?> m, String key) {
		Object v = m.get(key);
		return v instanceof Number n ? n.doubleValue() : 0.0;
	}
}
