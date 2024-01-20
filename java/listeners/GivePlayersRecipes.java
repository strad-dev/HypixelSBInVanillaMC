package listeners;

import misc.AddRecipes;
import misc.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GivePlayersRecipes implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().discoverRecipes(AddRecipes.returnRecipes(Plugin.getInstance()));
	}
}
