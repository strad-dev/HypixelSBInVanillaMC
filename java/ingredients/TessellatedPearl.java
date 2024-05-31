package ingredients;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TessellatedPearl implements Ingredients {
	public static ItemStack getItem() {
		ItemStack tessellated = new ItemStack(Material.ENDER_PEARL);
		tessellated.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = tessellated.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Tessellated Ender Pearl");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/tessellated_pearl");
		lore.add("");
		lore.add(ChatColor.GRAY + "An Ender Pearl so dense that even");
		lore.add(ChatColor.GRAY + "the most knowledgeable players");
		lore.add(ChatColor.GRAY + "are mystified by it.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		tessellated.setItemMeta(data);

		return tessellated;
	}
}