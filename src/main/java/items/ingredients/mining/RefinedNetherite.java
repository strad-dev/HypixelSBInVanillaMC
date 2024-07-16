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

public class RefinedNetherite implements Ingredients {
	public static ItemStack getItem() {
		ItemStack refinedNetherite = new ItemStack(Material.NETHERITE_SCRAP);
		refinedNetherite.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = refinedNetherite.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Refined Netherite");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/refined_netherite");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "The legendary Divan took a");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "trip to Hell and came back");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "with this interesting rock.");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		refinedNetherite.setItemMeta(data);

		return refinedNetherite;
	}
}