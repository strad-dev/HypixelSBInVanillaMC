package commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class Tell implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		Player player;
		try {
			player = getServer().getPlayer(strings[0]);
		} catch(Exception exception) {
			commandSender.sendMessage(ChatColor.RED + "You didn't provide a player to send messages to.");
			return false;
		}
		if(player == null) {
			commandSender.sendMessage(ChatColor.RED + "Invalid player provided.");
			return false;
		}
		if(commandSender instanceof Player temp && temp.equals(player)) {
			commandSender.sendMessage(ChatColor.RED + "Why would you tell something to yourself, just write it down in Notepad or something.");
			return true;
		}
		StringBuilder string = new StringBuilder();
		for(int i = 1; i < strings.length; i++) {
			string.append(strings[i]).append(' ');
		}
		if(!string.isEmpty()) {
			string.deleteCharAt(string.length() - 1);
		}
		player.sendMessage(ChatColor.ITALIC + String.valueOf(ChatColor.GREEN) + commandSender.getName() + ChatColor.GRAY + ChatColor.ITALIC + " whispers to you: " + string);
		commandSender.sendMessage(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "You whisper to " + ChatColor.GREEN + ChatColor.ITALIC + player.getName() + ChatColor.GRAY + ChatColor.ITALIC + ": " + string);
		Bukkit.getLogger().info(commandSender.getName() + " whispers to " + player.getName() + ": " + string);
		return true;
	}
}