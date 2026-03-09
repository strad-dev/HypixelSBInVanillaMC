package listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
	static final boolean DISCORDSRV_PRESENT = Bukkit.getPluginManager().getPlugin("DiscordSRV") != null;

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		String player = e.getPlayer().getName();
		String message = e.getMessage();
		String sentMessage = "";
		if(player.equals("Beethoven_")) {
			sentMessage += ChatColor.BLUE;
		} else {
			sentMessage += ChatColor.GREEN;
		}
		sentMessage += player + ChatColor.WHITE + ": " + message;
		Bukkit.broadcastMessage(sentMessage);
		e.setCancelled(true);

		if(DISCORDSRV_PRESENT) {
			DiscordForwarder.forward(e.getPlayer(), message);
		}
	}
}