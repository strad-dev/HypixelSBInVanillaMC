package listeners;

import commands.Tell;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandInterceptor implements Listener {

	private static final String[] CHAT_PREFIXES = {"minecraft:say", "minecraft:me"};
	private static final String[] WHISPER_PREFIXES = {"minecraft:tell", "minecraft:msg", "minecraft:w"};

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		String msg = e.getMessage().substring(1); // remove leading /
		String lower = msg.toLowerCase();

		for (String prefix : CHAT_PREFIXES) {
			if (lower.startsWith(prefix + " ") || lower.equals(prefix)) {
				e.setCancelled(true);
				String args = msg.length() > prefix.length() ? msg.substring(prefix.length() + 1) : "";
				if (!args.isEmpty()) {
					ChatListener.sendChat(e.getPlayer(), args);
				}
				return;
			}
		}

		for (String prefix : WHISPER_PREFIXES) {
			if (lower.startsWith(prefix + " ") || lower.equals(prefix)) {
				e.setCancelled(true);
				String args = msg.length() > prefix.length() ? msg.substring(prefix.length() + 1) : "";
				Tell.sendWhisper(e.getPlayer(), args.split(" "));
				return;
			}
		}
	}

	@EventHandler
	public void onServerCommand(ServerCommandEvent e) {
		String cmd = e.getCommand();
		String lower = cmd.toLowerCase();

		for (String prefix : CHAT_PREFIXES) {
			if (lower.startsWith(prefix + " ") || lower.equals(prefix)) {
				e.setCancelled(true);
				String args = cmd.length() > prefix.length() ? cmd.substring(prefix.length() + 1) : "";
				if (!args.isEmpty()) {
					ChatListener.sendChat(e.getSender(), args);
				}
				return;
			}
		}

		for (String prefix : WHISPER_PREFIXES) {
			if (lower.startsWith(prefix + " ") || lower.equals(prefix)) {
				e.setCancelled(true);
				String args = cmd.length() > prefix.length() ? cmd.substring(prefix.length() + 1) : "";
				Tell.sendWhisper(e.getSender(), args.split(" "));
				return;
			}
		}
	}
}
