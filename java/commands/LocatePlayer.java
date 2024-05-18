package commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class LocatePlayer implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		Player player;
		try {
			player = getServer().getPlayer(strings[0]);
		} catch(Exception exception) {
			commandSender.sendMessage(ChatColor.RED + "Invalid player provided.");
			return false;
		}
		if(player == null) {
			commandSender.sendMessage(ChatColor.RED + "Invalid player provided.");
			return false;
		}
		if(commandSender instanceof Player temp && temp.equals(player)) {
			commandSender.sendMessage(ChatColor.WHITE + "You are located at yourself!");
			return true;
		}
		Location l = player.getLocation();
		commandSender.sendMessage(ChatColor.WHITE + "Location of " + ChatColor.GREEN + player.getName() + ChatColor.WHITE + ": " + (int) l.getX() + " " + (int) l.getY() + " " + (int) l.getZ() + " in Dimension " + player.getWorld().getEnvironment());
		return true;
	}
}