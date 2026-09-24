package manhunt;

import misc.AddRecipes;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

/** Wires in Manhunt. With {@code manhunt: false} nothing registers and the command is removed. Same shape as PvpModule. */
public final class ManhuntModule {
	private ManhuntModule() {}

	public static void enable(JavaPlugin plugin, boolean on) {
		Manhunt.setEnabled(on);

		if(!on) {
			unregister(plugin, "manhunt");
			return;
		}

		// Before anyone can join.
		Manhunt.load(plugin);

		ManhuntCommand command = new ManhuntCommand();
		var bound = plugin.getCommand("manhunt");
		if(bound != null) {
			bound.setExecutor(command);
			bound.setTabCompleter(command);
		} else {
			plugin.getLogger().warning("[Manhunt] command 'manhunt' missing from plugin.yml");
		}

		plugin.getServer().getPluginManager().registerEvents(new ManhuntListener(), plugin);

		for(Recipe recipe : AddRecipes.addManhuntRecipes(plugin)) {
			plugin.getServer().addRecipe(recipe);
		}

		plugin.getLogger().info("[Manhunt] enabled");
	}

	/**
	 * plugin.yml registers every declared command with or without an executor, so remove both the bare label
	 * and the {@code skyblock:} form. Same as {@code PvpModule.unregister}.
	 */
	private static void unregister(JavaPlugin plugin, String... names) {
		try {
			org.bukkit.command.SimpleCommandMap map =
					(org.bukkit.command.SimpleCommandMap) ((org.bukkit.craftbukkit.CraftServer) plugin.getServer()).getCommandMap();
			java.util.Map<String, org.bukkit.command.Command> known = map.getKnownCommands();
			String prefix = plugin.getName().toLowerCase(java.util.Locale.ROOT) + ":";
			for(String name : names) {
				org.bukkit.command.PluginCommand ours = plugin.getCommand(name);
				if(ours == null) continue;
				ours.unregister(map);
				known.values().removeIf(c -> c == ours);
				known.remove(name);
				known.remove(prefix + name);
			}
		} catch(Exception e) {
			plugin.getLogger().warning("[Manhunt] could not unregister disabled commands: " + e.getMessage());
		}
	}
}
