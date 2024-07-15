package ingredients;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WardenHeart implements Ingredients {
	public static ItemStack getItem() {

		ItemStack wardenHeart = new ItemStack(Material.REDSTONE_BLOCK);
		wardenHeart.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = wardenHeart.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Warden Heart");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/warden_heart");
		lore.add("");
		lore.add(ChatColor.GRAY + "The heart of a powerful creature,");
		lore.add(ChatColor.GRAY + "dropped by the Warden.");
		lore.add(ChatColor.GRAY + "(NOT the Atoned Horror)");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		wardenHeart.setItemMeta(data);

		return wardenHeart;
	}
}