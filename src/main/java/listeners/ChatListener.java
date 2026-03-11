package listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
	private static final boolean DISCORDSRV_PRESENT = Bukkit.getPluginManager().getPlugin("DiscordSRV") != null;

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		sendChat(e.getPlayer(), e.getMessage());
		e.setCancelled(true);
	}

	public static void sendChat(CommandSender sender, String message) {
		String formatted;
		if (sender instanceof Player player) {
			String color = player.getName().equals("Beethoven_")
				? ChatColor.BLUE.toString() : ChatColor.GREEN.toString();
			formatted = color + player.getName() + ChatColor.WHITE + ": " + message;
			if (DISCORDSRV_PRESENT) {
				DiscordForwarder.forward(player, message);
			}
		} else {
			formatted = ChatColor.RED + "" + ChatColor.BOLD + "Server" + ChatColor.RESET + ChatColor.WHITE + ChatColor.BOLD + ": " + message;
			if (DISCORDSRV_PRESENT) {
				DiscordForwarder.forward("**Server: " + message + "**");
			}
		}
		Bukkit.broadcastMessage(formatted);
	}

	public static boolean isDiscordSRVPresent() {
		return DISCORDSRV_PRESENT;
	}
}
