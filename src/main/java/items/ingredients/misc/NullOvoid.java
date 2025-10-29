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

public class NullOvoid implements Ingredients  {
	public static ItemStack getItem() {
		ItemStack ovoid = new ItemStack(Material.ENDERMAN_SPAWN_EGG);
		ovoid.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = ovoid.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Null Ovoid");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/null_ovoid");
		lore.add("");
		lore.add(ChatColor.GRAY + "The Zealots have utiliezed this");
		lore.add(ChatColor.GRAY + "item to its fullest potential.");
		lore.add(ChatColor.GRAY + "Maybe you can as well?");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		ovoid.setItemMeta(data);

		return ovoid;
	}
}