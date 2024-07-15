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

public class Alloy implements Ingredients {
	public static ItemStack getItem() {
		ItemStack alloy = new ItemStack(Material.GOLD_BLOCK);
		alloy.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = alloy.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Divan's Alloy");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/alloy");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "The legendary Divan explored");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "where no other man has set foot,");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "and came back with this");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "incredibly rare alloy.");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		alloy.setItemMeta(data);

		return alloy;
	}
}