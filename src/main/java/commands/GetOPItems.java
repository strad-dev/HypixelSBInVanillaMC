package commands;

import misc.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class GetOPItems implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("Players only!");
			return true;
		}

		if (!player.getGameMode().equals(GameMode.CREATIVE) && !player.isOp()) {
			sender.sendMessage(ChatColor.RED + "You must be in creative mode or be an operator!");
			return false;
		}

		// Give the menu opener item
		ItemStack menuOpener = createMenuOpener();
		player.getInventory().addItem(menuOpener);
		player.sendMessage(ChatColor.GREEN + "Added SkyBlock Creative Menu to your inventory!");
		return true;
	}

	private ItemStack createMenuOpener() {
		ItemStack item = new ItemStack(Material.NETHER_STAR);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Creative Menu");
		meta.setLore(List.of(ChatColor.GREEN + "Right-click to open"));
		meta.setLore(List.of(""));
		meta.setLore(List.of(ChatColor.RED + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + " SPECIAL " + ChatColor.MAGIC + "a"));

		// Add unique identifier
		NamespacedKey key = new NamespacedKey(Plugin.getInstance(), "creative_menu");
		meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte)1);

		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		return item;
	}
}