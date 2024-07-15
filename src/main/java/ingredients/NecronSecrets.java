package ingredients;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NecronSecrets implements Ingredients {
	public static ItemStack getItem() {
		ItemStack necronSecrets = new ItemStack(Material.PAPER);
		necronSecrets.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = necronSecrets.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Necron's Secrets");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/necron_secrets");
		lore.add("");
		lore.add(ChatColor.GRAY + "Being the right-hand man of the");
		lore.add(ChatColor.GRAY + "Wither King, Necron knows some tricks");
		lore.add(ChatColor.GRAY + "about being able to fly with lots of armor.");
		lore.add(ChatColor.GRAY + "This paper, which he dropped behind him,");
		lore.add(ChatColor.GRAY + "contains all of those secrets.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		necronSecrets.setItemMeta(data);

		return necronSecrets;
	}
}
