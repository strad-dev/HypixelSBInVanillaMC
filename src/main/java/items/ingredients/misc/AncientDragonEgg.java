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

public class AncientDragonEgg implements Ingredients  {
	public static ItemStack getItem() {
		ItemStack egg = new ItemStack(Material.DRAGON_EGG);
		egg.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = egg.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + "Ancient Dragon Egg");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/ancient_dragon_egg");
		lore.add("");
		lore.add(ChatColor.GRAY + "An extremely rare and ancient Dragon Egg.");
		lore.add(ChatColor.GRAY + "Legends say it is the last of its kind.");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		egg.setItemMeta(data);

		return egg;
	}
}