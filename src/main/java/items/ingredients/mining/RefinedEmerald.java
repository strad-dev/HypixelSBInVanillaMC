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

public class RefinedEmerald implements Ingredients {
	public static ItemStack getItem() {
		ItemStack refinedEmerald = new ItemStack(Material.EMERALD);
		refinedEmerald.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = refinedEmerald.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Refined Emerald");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/refined_emerald");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "Villagers tremble at its");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "immense rarity and value.");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		refinedEmerald.setItemMeta(data);

		return refinedEmerald;
	}
}