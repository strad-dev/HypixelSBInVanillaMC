package pvp;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Wires the (config-gated) PvP feature into SkyBlock: stats writer, duel manager, the damage/FFA
 * listener, and the /joinarena /leavearena /stats /pvptop /duel commands. Everything no-ops unless
 * enabled in config.yml, so SkyBlock stays standalone and inert on non-pvp servers.
 */
public final class PvpModule {
	private PvpModule() {}

	public static void enable(JavaPlugin plugin, PvpConfig cfg) {
		PvpStats stats = new PvpStats(cfg);
		stats.start(plugin);

		DuelManager duels = new DuelManager(plugin, cfg, stats);
		plugin.getServer().getPluginManager().registerEvents(new PvpListener(cfg, stats, duels), plugin);

		JoinArenaCommand arena = new JoinArenaCommand(cfg, duels);
		bind(plugin, "joinarena", arena);
		bind(plugin, "leavearena", arena);

		StatsCommand statsCmd = new StatsCommand(cfg);
		bind(plugin, "stats", statsCmd);
		bind(plugin, "pvptop", statsCmd);

		bind(plugin, "duel", new DuelCommand(duels));

		plugin.getLogger().info("[PvP] ffa=" + cfg.ffaEnabled() + " duel=" + cfg.duelEnabled()
				+ " stats=" + cfg.statsEnabled() + " chat=" + cfg.chatEnabled());
	}

	private static void bind(JavaPlugin plugin, String name, CommandExecutor executor) {
		var c = plugin.getCommand(name);
		if (c != null) c.setExecutor(executor);
		else plugin.getLogger().warning("[PvP] command '" + name + "' missing from plugin.yml");
	}
}
