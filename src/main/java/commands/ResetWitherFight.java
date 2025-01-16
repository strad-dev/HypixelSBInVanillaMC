package commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static listeners.CustomMobs.updateWitherLordFight;

public class ResetWitherFight implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if(commandSender.isOp()) {
			updateWitherLordFight(false);
			commandSender.sendMessage("Reset the status of the Wither Lords fight.");
			return true;
		} else {
			commandSender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
			return false;
		}
	}
}
