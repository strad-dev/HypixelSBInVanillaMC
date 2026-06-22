package listeners;

import misc.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.AsyncChatEvent;

public class ChatListener implements Listener {
	private static final boolean DISCORDSRV_PRESENT = Bukkit.getPluginManager().getPlugin("DiscordSRV") != null;

	@EventHandler
	public void onPlayerChat(AsyncChatEvent e) {
		sendChat(e.getPlayer(), Utils.plain(e.message()));
		e.setCancelled(true);
	}

	public static void sendChat(CommandSender sender, String message) {
		// Player name and chat text are inserted as UNPARSED placeholders so a player cannot inject MiniMessage
		// tags (e.g. <red>, <rainbow>, <click>) by typing them — they render as literal text.
		Component formatted;
		if (sender instanceof Player player) {
			String color = player.getName().equals("Beethoven_") ? "<blue>" : "<green>";
			formatted = Utils.msg(color + "<name><white>: <message>",
					Placeholder.unparsed("name", player.getName()),
					Placeholder.unparsed("message", message));
			if (DISCORDSRV_PRESENT) {
				DiscordForwarder.forward(player, message);
			}
		} else {
			formatted = Utils.msg("<red><bold>Server<white><bold>: <message>",
					Placeholder.unparsed("message", message));
			if (DISCORDSRV_PRESENT) {
				DiscordForwarder.forward("**Server: " + message + "**");
			}
		}
		Bukkit.broadcast(formatted);
	}

	public static boolean isDiscordSRVPresent() {
		return DISCORDSRV_PRESENT;
	}
}
