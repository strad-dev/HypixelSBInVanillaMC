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
 * Reads the PvP + chat config (config.yml). All values default to disabled/empty so SkyBlock is
 * inert on non-pvp servers; admins fill these in on the pvp server.
 */
public class PvpConfig {
	private final JavaPlugin plugin;

	public PvpConfig(JavaPlugin plugin) {
		this.plugin = plugin;
		plugin.saveDefaultConfig();
		mergeMissingDefaults();
	}

	/**
	 * Adds any keys present in the jar's bundled config.yml but missing from the on-disk file, with
	 * their default values. Existing values (and, on modern Paper, comments) are left untouched, so a
	 * config written before new options were added gets them filled in automatically on startup.
	 */
	private void mergeMissingDefaults() {
		InputStream in = plugin.getResource("config.yml");
		if (in == null) return;
		YamlConfiguration bundled = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
		FileConfiguration live = plugin.getConfig();
		boolean changed = false;
		for (String key : bundled.getKeys(true)) {
			// Only copy leaf values; sections are created implicitly by their children.
			if (!bundled.isConfigurationSection(key) && !live.contains(key)) {
				live.set(key, bundled.get(key));
				changed = true;
			}
		}
		if (changed) plugin.saveConfig();
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

	/** Intelligence every player is set to for the duration of a duel (hunger is always full). */
	public int duelIntelligence() {
		return cfg().getInt("pvp.duel.intelligence", 50);
	}

	/** Saturation every player is set to for the duration of a duel. */
	public double duelSaturation() {
		return cfg().getDouble("pvp.duel.saturation", 5);
	}

	/**
	 * Where PvP duel loadouts are stored. Default is this plugin's own folder
	 * ({@code plugins/SkyBlock/pvp-loadouts.json}), so SkyBlock stays self-contained. Relative paths
	 * resolve against that folder; set an ABSOLUTE path (the network's shared {@code ~/data}) so a
	 * player's duel loadout is shared across servers and editable from any of them.
	 */
	public Path loadoutsFile() {
		String f = cfg().getString("pvp.duel.loadouts-file", "pvp-loadouts.json");
		Path p = Paths.get(f);
		if (!p.isAbsolute()) p = plugin.getDataFolder().toPath().resolve(f);
		return p.normalize();
	}

	/**
	 * Absolute path to export the duel item palette + default kit to (the network's shared
	 * {@code ~/data/pvp-item-catalog.json}), so servers without SkyBlock can offer the same items in
	 * their loadout editor. {@code null} when unset - standalone servers do not export.
	 */
	public Path catalogFile() {
		String f = cfg().getString("pvp.duel.catalog-file", "");
		if (f == null || f.isBlank()) return null;
		Path p = Paths.get(f);
		if (!p.isAbsolute()) p = plugin.getDataFolder().toPath().resolve(f);
		return p.normalize();
	}

	/** Player corner i (0 or 1) for a duel, or null if not configured. */
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
		// Default to this plugin's own data folder (plugins/SkyBlock/pvp-stats.json) so SkyBlock stays
		// self-contained. Relative paths resolve against that folder; set an ABSOLUTE path (e.g. the
		// network's shared ~/data) if you want stats shared across servers.
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
