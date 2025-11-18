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

public class RefinedIron implements Ingredients {
	public static ItemStack getItem() {
		ItemStack refinedIron = new ItemStack(Material.IRON_INGOT);
		refinedIron.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = refinedIron.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Refined Iron");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/refined_iron");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "A shiny ingot in its purest form");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "straight from the mines.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		refinedIron.setItemMeta(data);

		return refinedIron;
	}
}