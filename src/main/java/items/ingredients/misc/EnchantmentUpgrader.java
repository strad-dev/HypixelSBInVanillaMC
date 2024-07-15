package items.ingredients.misc;

import items.ingredients.Ingredients;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentUpgrader implements Ingredients {
	public static ItemStack getItem() {
		ItemStack upgrader = new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
		upgrader.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = upgrader.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Exceedingly Rare Enchantment Upgrader");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/enchantment_upgrader");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "The item name is long both as a reference");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "but also because the devs found it funny.");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		upgrader.setItemMeta(data);

		return upgrader;
	}
}