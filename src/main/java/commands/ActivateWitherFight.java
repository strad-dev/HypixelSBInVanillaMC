package commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static listeners.CustomMobs.updateWitherLordFight;

public class ActivateWitherFight implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if(commandSender.isOp()) {
			updateWitherLordFight(true);
			commandSender.sendMessage("Set the status of the Wither Lords fight to active.");
			return true;
		} else {
			commandSender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
			return false;
		}
	}
}