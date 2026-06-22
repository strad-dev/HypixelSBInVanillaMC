package commands;

import listeners.ChatListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

public class Say implements CommandExecutor {

	@Override
	public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] strings) {
		if (strings.length == 0) {
			return false;
		}
		ChatListener.sendChat(commandSender, String.join(" ", strings));
		return true;
	}
}
