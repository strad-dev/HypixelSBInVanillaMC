package commands;

import misc.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import static org.bukkit.Bukkit.getServer;

public class LocatePlayer implements CommandExecutor {

	@Override
	public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String @NonNull [] strings) {
		Player player;
		try {
			player = getServer().getPlayer(strings[0]);
		} catch(Exception exception) {
			commandSender.sendMessage(Utils.msg("<red>Invalid player provided."));
			return false;
		}
		if(player == null) {
			commandSender.sendMessage(Utils.msg("<red>Invalid player provided."));
			return false;
		}
		if(commandSender instanceof Player temp && temp.equals(player)) {
			commandSender.sendMessage(Utils.msg("<white>You are located at yourself!"));
			return true;
		}
		Location l = player.getLocation();
		commandSender.sendMessage(Utils.msg("<white>Location of <green>" + player.getName() + "<white>: " + (int) l.getX() + " " + (int) l.getY() + " " + (int) l.getZ() + " in Dimension " + player.getWorld().getEnvironment()));
		return true;
	}
}