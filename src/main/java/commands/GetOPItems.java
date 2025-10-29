package commands;

import items.armor.*;
import items.ingredients.mining.*;
import items.ingredients.misc.*;
import items.ingredients.witherLords.*;
import items.misc.*;
import items.summonItems.*;
import items.weapons.Claymore;
import items.weapons.Scylla;
import items.weapons.Terminator;
import misc.Plugin;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

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
		meta.setDisplayName(ChatColor.GOLD + "✦ " + ChatColor.YELLOW + ChatColor.BOLD + "SkyBlock Creative Menu");
		meta.setLore(List.of(ChatColor.GREEN + "Right-click to open"));

		// Add unique identifier
		NamespacedKey key = new NamespacedKey(Plugin.getInstance(), "creative_menu");
		meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte)1);

		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		return item;
	}
}