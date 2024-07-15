package ingredients;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TarantulaSilk implements Ingredients {
	public static ItemStack getItem() {

		ItemStack taraSilk = new ItemStack(Material.COBWEB);
		taraSilk.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = taraSilk.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Tarantula Silk");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/tarantula_silk");
		lore.add("");
		lore.add(ChatColor.GRAY + "A Web so perfect that even");
		lore.add(ChatColor.GRAY + "the most powerful players");
		lore.add(ChatColor.GRAY + "cannot escape it.");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		taraSilk.setItemMeta(data);

		return taraSilk;
	}
}
