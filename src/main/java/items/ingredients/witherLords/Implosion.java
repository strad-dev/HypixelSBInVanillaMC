package items.ingredients.witherLords;

import items.ingredients.Ingredients;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Implosion implements Ingredients {
	public static ItemStack getItem() {
		ItemStack implosion = new ItemStack(Material.ENCHANTED_BOOK);
		implosion.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = implosion.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Implosion");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/implosion");
		lore.add("");
		lore.add(ChatColor.GRAY + "A rare Enchanted Book imbued");
		lore.add(ChatColor.GRAY + "with the power of Storm.");
		lore.add(ChatColor.GRAY + "Grants the ability to implode");
		lore.add(ChatColor.GRAY + "and damage all enemies within");
		lore.add(ChatColor.GRAY + "10 blocks.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		implosion.setItemMeta(data);

		return implosion;
	}
}
