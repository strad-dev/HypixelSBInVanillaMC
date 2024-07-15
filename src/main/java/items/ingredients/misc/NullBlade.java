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

public class NullBlade implements Ingredients {
	public static ItemStack getItem() {
		ItemStack nullBlade = new ItemStack(Material.SHEARS);
		nullBlade.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = nullBlade.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Null Blade");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/null_blade");
		lore.add("");
		lore.add(ChatColor.GRAY + "A pair of Shears so null that even");
		lore.add(ChatColor.GRAY + "the most intelligent players");
		lore.add(ChatColor.GRAY + "are confused by it.");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		nullBlade.setItemMeta(data);

		return nullBlade;
	}
}
