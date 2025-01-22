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

public class WitherShield implements Ingredients {
	public static ItemStack getItem() {
		ItemStack witherShield = new ItemStack(Material.ENCHANTED_BOOK);
		witherShield.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = witherShield.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Wither Shield");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/wither_shield");
		lore.add("");
		lore.add(ChatColor.GRAY + "A rare Enchanted Book imbued");
		lore.add(ChatColor.GRAY + "with the power of Goldor.");
		lore.add(ChatColor.GRAY + "Grants the ability to summon");
		lore.add(ChatColor.GRAY + "6 hearts of absorption and");
		lore.add(ChatColor.GRAY + "take 10% less damage for 5 seconds.");
		lore.add(ChatColor.GRAY + "Extra absorption is converted to");
		lore.add(ChatColor.GRAY + "healing after 5 seconds.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		witherShield.setItemMeta(data);

		return witherShield;
	}
}
