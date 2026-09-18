package manhunt;

import misc.AddRecipes;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Wires the (config-gated) Manhunt feature into SkyBlock: {@code /manhunt}, the team/intelligence rules and
 * the Hyperion upgrade recipes. With {@code manhunt: false} nothing here registers and the command is
 * unregistered from the command map, so an ordinary server is untouched - same shape as {@code PvpModule}.
 */
public final class ManhuntModule {
	private ManhuntModule() {}

	public static void enable(JavaPlugin plugin, boolean on) {
		Manhunt.setEnabled(on);

		if(!on) {
			unregister(plugin, "manhunt");
			return;
		}

		// The teams outlive the server, so they are read back before anybody can join.
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
	 * Remove {@code /manhunt} from the command map when Manhunt is off. plugin.yml registers every declared
	 * command whether or not it has an executor, so a disabled one has to be actively taken out (both the
	 * bare label and the {@code skyblock:} form). Same technique as {@code PvpModule.unregister}.
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
