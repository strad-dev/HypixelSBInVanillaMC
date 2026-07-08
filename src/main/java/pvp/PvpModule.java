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

		PvpLoadouts loadouts = new PvpLoadouts();
		DuelManager duels = new DuelManager(plugin, cfg, stats, loadouts);
		PvpListener listener = new PvpListener(cfg, stats, duels);
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
		listener.start(plugin);
		// Layer the duel/FFA combat handling on top of SkyBlock's CustomDamage flow.
		PvpHooks.install(listener);

		JoinArenaCommand arena = new JoinArenaCommand(cfg, duels);
		bind(plugin, "joinarena", arena);
		bind(plugin, "leavearena", arena);

		StatsCommand statsCmd = new StatsCommand(cfg);
		bind(plugin, "stats", statsCmd);
		bind(plugin, "pvptop", statsCmd);

		DuelCommand duelCmd = new DuelCommand(duels);
		bind(plugin, "duel", duelCmd);
		var duelCommand = plugin.getCommand("duel");
		if (duelCommand != null) duelCommand.setTabCompleter(duelCmd);

		// PvP loadout editor (/pvploadout): declared in plugin.yml so it shows in /help and tab-completes.
		// The command itself refuses unless 1v1 duels are enabled (checked in PvpLoadoutMenu), and the
		// menu only opens then. Loadouts live in this plugin's own data folder, i.e. stored per-server.
		PvpLoadoutMenu menu = new PvpLoadoutMenu(cfg, loadouts);
		plugin.getServer().getPluginManager().registerEvents(menu, plugin);
		bind(plugin, "pvploadout", menu);

		plugin.getLogger().info("[PvP] ffa=" + cfg.ffaEnabled() + " duel=" + cfg.duelEnabled()
				+ " stats=" + cfg.statsEnabled() + " chat=" + cfg.chatEnabled());
	}

	private static void bind(JavaPlugin plugin, String name, CommandExecutor executor) {
		var c = plugin.getCommand(name);
		if (c != null) c.setExecutor(executor);
		else plugin.getLogger().warning("[PvP] command '" + name + "' missing from plugin.yml");
	}
}
