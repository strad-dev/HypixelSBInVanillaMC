package items.ingredients.mining;

import items.ingredients.Ingredients;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RefinedRedstone implements Ingredients {
	public static ItemStack getItem() {
		ItemStack refinedRedstone = new ItemStack(Material.REDSTONE);
		refinedRedstone.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = refinedRedstone.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Refined Redstone");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/refined_redstone");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "Redstone of the highest quality.");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "Use it wisely.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		refinedRedstone.setItemMeta(data);

		return refinedRedstone;
	}
}