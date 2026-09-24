package pvp;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Wires config-gated PvP: stats, duel manager, damage/FFA listener, /joinarena /leavearena /pvpstats /pvptop
 * /duel /pvploadout. A command is registered only when its feature is on and unregistered otherwise, so
 * SkyBlock stays inert off the pvp server.
 */
public final class PvpModule {
	private PvpModule() {}

	/** Held only so {@link #disable} can shut it down. Null with duels off. */
	private static PvpLoadoutMenu loadoutMenu;

	/**
	 * Give back the real inventory of anyone still in the loadout editor, which IS their inventory while open;
	 * skipping this leaves them with palette copies and their items gone. From onDisable, before players drop.
	 */
	public static void disable() {
		if (loadoutMenu != null) loadoutMenu.restoreAll();
	}

	public static void enable(JavaPlugin plugin, PvpConfig cfg) {
		PvpStats stats = new PvpStats(cfg);
		stats.start(plugin);

		PvpLoadouts loadouts = new PvpLoadouts(cfg.loadoutsFile());
		DuelManager duels = new DuelManager(plugin, cfg, stats, loadouts);
		PvpListener listener = new PvpListener(cfg, stats, duels);
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
		listener.start(plugin);
		PvpHooks.install(listener);

		// Register only commands whose feature is on. plugin.yml declares them all, so the rest are removed from
		// the command map to keep them out of tab-complete and help.
		boolean ffa = cfg.ffaEnabled();
		boolean duel = cfg.duelEnabled();

		if (ffa) {
			JoinArenaCommand arena = new JoinArenaCommand(cfg, duels);
			bind(plugin, "joinarena", arena);
			bind(plugin, "leavearena", arena);
		} else {
			unregister(plugin, "joinarena", "leavearena");
		}

		// Stats + leaderboard when FFA or duels is on. /pvptop tab-completes only enabled boards (StatsCommand).
		if (ffa || duel) {
			StatsCommand statsCmd = new StatsCommand(cfg);
			bind(plugin, "pvpstats", statsCmd);
			bind(plugin, "pvptop", statsCmd);
			var topCommand = plugin.getCommand("pvptop");
			if (topCommand != null) topCommand.setTabCompleter(statsCmd);
		} else {
			unregister(plugin, "pvpstats", "pvptop");
		}

		// Duels + loadout editor. Loadouts at pvp.duel.loadouts-file (own folder by default, shared ~/data on network).
		if (duel) {
			DuelCommand duelCmd = new DuelCommand(duels);
			bind(plugin, "duel", duelCmd);
			var duelCommand = plugin.getCommand("duel");
			if (duelCommand != null) duelCommand.setTabCompleter(duelCmd);

			PvpLoadoutMenu menu = new PvpLoadoutMenu(cfg, loadouts);
			plugin.getServer().getPluginManager().registerEvents(menu, plugin);
			bind(plugin, "pvploadout", menu);
			loadoutMenu = menu;
		} else {
			unregister(plugin, "duel", "pvploadout");
		}

		// pvp.duel.catalog-file set (network pvp server): export palette + default kit to shared data so servers
		// WITHOUT SkyBlock offer the same items in their /pvploadout. Unset = no export.
		java.nio.file.Path catalogFile = cfg.catalogFile();
		if (catalogFile != null) {
			PvpCatalogExport.write(catalogFile, PvpLoadoutMenu.palette(), DuelKit.defaultLoadout(),
					cfg.ffaEnabled(), cfg.duelEnabled());
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

	/**
	 * Unregister commands of disabled PvP features (bare label and {@code skyblock:} form); plugin.yml registers
	 * all of them regardless of config. Same technique as {@code Plugin.releaseChatCommands}.
	 */
	private static void unregister(JavaPlugin plugin, String... names) {
		try {
			org.bukkit.command.SimpleCommandMap map =
					(org.bukkit.command.SimpleCommandMap) ((org.bukkit.craftbukkit.CraftServer) plugin.getServer()).getCommandMap();
			java.util.Map<String, org.bukkit.command.Command> known = map.getKnownCommands();
			String prefix = plugin.getName().toLowerCase(java.util.Locale.ROOT) + ":";
			for (String name : names) {
				org.bukkit.command.PluginCommand ours = plugin.getCommand(name);
				if (ours == null) continue;
				ours.unregister(map);
				known.values().removeIf(c -> c == ours);
				known.remove(name);
				known.remove(prefix + name);
			}
		} catch (Exception e) {
			plugin.getLogger().warning("[PvP] could not unregister disabled commands: " + e.getMessage());
		}
	}
}
