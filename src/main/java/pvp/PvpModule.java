package pvp;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Wires the (config-gated) PvP feature into SkyBlock: stats writer, duel manager, the damage/FFA
 * listener, and the /joinarena /leavearena /pvpstats /pvptop /duel /pvploadout commands. The combat
 * listener no-ops unless enabled; each command is only registered when its feature is on and otherwise
 * unregistered from the command map, so SkyBlock stays standalone and inert on non-pvp servers.
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

		// Only register the commands whose feature is actually on; unregister the rest so disabled modes
		// don't clutter tab-complete / help with commands that would just print "disabled". plugin.yml
		// declares them all, so the unused ones are actively removed from the command map here.
		boolean ffa = cfg.ffaEnabled();
		boolean duel = cfg.duelEnabled();

		// FFA arena — only with FFA on.
		if (ffa) {
			JoinArenaCommand arena = new JoinArenaCommand(cfg, duels);
			bind(plugin, "joinarena", arena);
			bind(plugin, "leavearena", arena);
		} else {
			unregister(plugin, "joinarena", "leavearena");
		}

		// Stats + leaderboard — only when at least one tracked mode is on (FFA kills or 1v1 wins). /pvptop
		// tab-completes just the enabled board(s) via StatsCommand's TabCompleter.
		if (ffa || duel) {
			StatsCommand statsCmd = new StatsCommand(cfg);
			bind(plugin, "pvpstats", statsCmd);
			bind(plugin, "pvptop", statsCmd);
			var topCommand = plugin.getCommand("pvptop");
			if (topCommand != null) topCommand.setTabCompleter(statsCmd);
		} else {
			unregister(plugin, "pvpstats", "pvptop");
		}

		// Duels + loadout editor — only with 1v1 duels on. Loadouts live wherever pvp.duel.loadouts-file
		// points (own folder by default; the network's shared ~/data on a network pvp server).
		if (duel) {
			DuelCommand duelCmd = new DuelCommand(duels);
			bind(plugin, "duel", duelCmd);
			var duelCommand = plugin.getCommand("duel");
			if (duelCommand != null) duelCommand.setTabCompleter(duelCmd);

			PvpLoadoutMenu menu = new PvpLoadoutMenu(cfg, loadouts);
			plugin.getServer().getPluginManager().registerEvents(menu, plugin);
			bind(plugin, "pvploadout", menu);
		} else {
			unregister(plugin, "duel", "pvploadout");
		}

		// On the network's pvp server (pvp.duel.catalog-file set), export the duel item palette + default
		// kit to the shared data folder so servers WITHOUT SkyBlock can offer the same items in their own
		// /pvploadout editor. Skipped when unset, so a standalone server exports nothing.
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
	 * Remove plugin.yml-declared commands from the command map for PvP features that are turned off, so they
	 * don't show up in tab-complete / {@code /help} at all. plugin.yml registers every declared command
	 * regardless of config, so the disabled ones have to be actively unregistered (both the bare label and
	 * the {@code skyblock:} form). Same technique as {@code Plugin.releaseChatCommands}.
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
