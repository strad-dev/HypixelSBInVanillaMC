package listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandUsageCheck implements Listener {
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		String[] command = e.getMessage().toLowerCase().split(" ");
		if(command[0].contains("say") || command[0].contains("tell")) {
			e.setMessage("/donothing");
			e.getPlayer().sendMessage(ChatColor.RED + "To prevent Microshit from doing stuff, this command is DISABLED.  Please use the chat normally.");
			return;
		}
		if(e.getMessage().contains("kill @e") && !e.getMessage().contains("[")) {
			e.setMessage("/kill " + e.getPlayer().getName());
			e.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to kill every entity in the world.");
			return;
		}
		if(e.getMessage().toLowerCase().contains("beethoven_") && !e.getPlayer().getName().equals("Beethoven_")) {
			if(!command[0].equals("tp") || command[1].equalsIgnoreCase("beethoven_")) {
				e.setMessage(e.getMessage().replace("Beethoven_", e.getPlayer().getName()));
				e.getPlayer().sendMessage(ChatColor.RED + "#notouchingstrad");
			}
		}
	}
}