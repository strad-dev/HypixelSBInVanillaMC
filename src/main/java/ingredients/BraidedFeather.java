package ingredients;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BraidedFeather implements Ingredients {
	public static ItemStack getItem() {
		ItemStack braidedFeather = new ItemStack(Material.FEATHER);
		braidedFeather.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = braidedFeather.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Braided Feather");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/braided_feather");
		lore.add("");
		lore.add(ChatColor.GRAY + "A Feather so sturdy that even");
		lore.add(ChatColor.GRAY + "the most powerful players");
		lore.add(ChatColor.GRAY + "cannot destroy it.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		braidedFeather.setItemMeta(data);

		return braidedFeather;
	}
}
