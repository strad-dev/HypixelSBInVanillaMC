package commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class GOD implements CommandExecutor {
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if(commandSender instanceof ConsoleCommandSender || commandSender instanceof Player) {
			if(commandSender instanceof ConsoleCommandSender || Objects.requireNonNull(((Player) commandSender).getInventory().getHelmet()).isSimilar(new ItemStack(Material.TOTEM_OF_UNDYING))) {
				StringBuilder string = new StringBuilder();
				for(String value : strings) {
					string.append(value.toUpperCase()).append(' ');
				}
				string.deleteCharAt(string.length() - 1);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a title {\"text\":\"" + string + "\",\"bold\":true,\"color\":\"dark_red\"}");
				Bukkit.broadcastMessage(ChatColor.DARK_RED + String.valueOf(ChatColor.BOLD) + "GOD: " + string);
			} else {
				commandSender.sendMessage(ChatColor.RED + "Only entitled users may use this >:)");
			}
		}
		return true;
	}
}