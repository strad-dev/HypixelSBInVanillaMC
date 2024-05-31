package ingredients;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GiantSwordRemnant implements Ingredients {
	public static ItemStack getItem() {
		ItemStack giantSwordRemnant = new ItemStack(Material.STICK);
		giantSwordRemnant.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = giantSwordRemnant.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Remnant of the Giant's Sword");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/giant_sword_remnant");
		lore.add("");
		lore.add(ChatColor.GRAY + "A hilt with very strange properties.");
		lore.add(ChatColor.GRAY + "Legend has it that it once belonged to");
		lore.add(ChatColor.GRAY + "Sadan, but historians disagree on this.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		giantSwordRemnant.setItemMeta(data);

		return giantSwordRemnant;
	}
}