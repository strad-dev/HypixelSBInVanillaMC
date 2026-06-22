package commands;

import misc.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

import static listeners.CustomMobs.updateWitherLordFight;

public class ActivateWitherFight implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, @NonNull Command command, @NonNull String s, String @NonNull [] strings) {
		if(commandSender.isOp()) {
			updateWitherLordFight(true);
			commandSender.sendMessage(Utils.msg("Set the status of the Wither Lords fight to active."));
			return true;
		} else {
			commandSender.sendMessage(Utils.msg("<red>You do not have permission to execute this command."));
			return false;
		}
	}
}