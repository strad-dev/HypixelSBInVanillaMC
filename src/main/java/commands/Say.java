package commands;

import listeners.ChatListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Say implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (strings.length == 0) {
			return false;
		}
		ChatListener.sendChat(commandSender, String.join(" ", strings));
		return true;
	}
}
