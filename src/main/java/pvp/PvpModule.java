package pvp;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Wires the (config-gated) PvP feature into SkyBlock: stats writer, duel manager, the damage/FFA
 * listener, and the /joinarena /leavearena /pvpstats /pvptop /duel commands. Everything no-ops unless
 * enabled in config.yml, so SkyBlock stays standalone and inert on non-pvp servers.
 */
public final class PvpModule {
	private PvpModule() {}

	public static void enable(JavaPlugin plugin, PvpConfig cfg) {
		PvpStats stats = new PvpStats(cfg);
		stats.start(plugin);

		PvpLoadouts loadouts = new PvpLoadouts(cfg.loadoutsFile());
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
		bind(plugin, "pvpstats", statsCmd);
		bind(plugin, "pvptop", statsCmd);

		DuelCommand duelCmd = new DuelCommand(duels);
		bind(plugin, "duel", duelCmd);
		var duelCommand = plugin.getCommand("duel");
		if (duelCommand != null) duelCommand.setTabCompleter(duelCmd);

		// PvP loadout editor (/pvploadout): declared in plugin.yml so it shows in /help and tab-completes.
		// The command itself refuses unless 1v1 duels are enabled (checked in PvpLoadoutMenu), and the
		// menu only opens then. Loadouts live wherever pvp.duel.loadouts-file points (own folder by
		// default; the network's shared ~/data on a network pvp server).
		PvpLoadoutMenu menu = new PvpLoadoutMenu(cfg, loadouts);
		plugin.getServer().getPluginManager().registerEvents(menu, plugin);
		bind(plugin, "pvploadout", menu);

		// On the network's pvp server (pvp.duel.catalog-file set), export the duel item palette + default
		// kit to the shared data folder so servers WITHOUT SkyBlock can offer the same items in their own
		// /pvploadout editor. Skipped when unset, so a standalone server exports nothing.
		java.nio.file.Path catalogFile = cfg.catalogFile();
		if (catalogFile != null) {
			PvpCatalogExport.write(catalogFile, PvpLoadoutMenu.palette(), DuelKit.defaultLoadout());
			plugin.getLogger().info("[PvP] exported duel item catalog to " + catalogFile);
		}

		plugin.getLogger().info("[PvP] ffa=" + cfg.ffaEnabled() + " duel=" + cfg.duelEnabled()
				+ " stats=" + cfg.statsEnabled() + " chat=" + cfg.chatEnabled());
	}

	private static void bind(JavaPlugin plugin, String name, CommandExecutor executor) {
		var c = plugin.getCommand(name);
		if (c != null) c.setExecutor(executor);
		else plugin.getLogger().warning("[PvP] command '" + name + "' missing from plugin.yml");
	}
}
