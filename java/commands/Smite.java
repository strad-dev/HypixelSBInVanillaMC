package commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class Smite implements CommandExecutor {
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if(commandSender.isOp()) {
			Player player;
			try {
				player = getServer().getPlayer(strings[0]);
			} catch(Exception exception) {
				commandSender.sendMessage(ChatColor.RED + "You didn't provide a player.");
				return false;
			}
			if(player == null) {
				commandSender.sendMessage(ChatColor.RED + "Invalid player provided.");
				return false;
			}
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " + strings[0] + " run summon minecraft:lightning_bolt");
			commandSender.sendMessage("Smited " + player.getName());
			player.sendMessage(ChatColor.DARK_RED + String.valueOf(ChatColor.BOLD) + "IMAGINE ANGERING GOD");
		}
		return true;
	}
}