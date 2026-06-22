package commands;

import misc.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

import static listeners.CustomMobs.updateWitherLordFight;

public class ResetWitherFight implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, @NonNull Command command, @NonNull String s, String @NonNull [] strings) {
		if(commandSender.isOp()) {
			updateWitherLordFight(false);
			commandSender.sendMessage(Utils.msg("Reset the status of the Wither Lords fight."));
			return true;
		} else {
			commandSender.sendMessage(Utils.msg("<red>You do not have permission to execute this command."));
			return false;
		}
	}
}
