package ingredients;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Handle implements Ingredients {
	public static ItemStack getItem() {
		ItemStack handle = new ItemStack(Material.STICK);
		handle.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = handle.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Necron's Handle");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/necron_handle");
		lore.add("");
		lore.add(ChatColor.GRAY + "The hilt of the GREATEST sword");
		lore.add(ChatColor.GRAY + "to ever exist, imbued with the");
		lore.add(ChatColor.GRAY + "power of Necron.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		handle.setItemMeta(data);

		return handle;
	}
}