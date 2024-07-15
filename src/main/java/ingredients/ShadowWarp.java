package ingredients;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShadowWarp implements Ingredients {
	public static ItemStack getItem() {
		ItemStack shadowWarp = new ItemStack(Material.ENCHANTED_BOOK);
		shadowWarp.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = shadowWarp.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Shadow Warp");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/shadow_warp");
		lore.add("");
		lore.add(ChatColor.GRAY + "A rare Enchanted Book imbued");
		lore.add(ChatColor.GRAY + "with the power of Maxor.");
		lore.add(ChatColor.GRAY + "Grants the ability to teleport");
		lore.add(ChatColor.GRAY + "10 blocks using Wither Impact.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		shadowWarp.setItemMeta(data);

		return shadowWarp;
	}
}
