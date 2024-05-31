package ingredients;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Viscera implements Ingredients {
	public static ItemStack getItem() {
		ItemStack viscera = new ItemStack(Material.COOKED_PORKCHOP);
		viscera.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = viscera.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Revenant Viscera");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/revenant_viscera");
		lore.add("");
		lore.add(ChatColor.GRAY + "The disgusting remains");
		lore.add(ChatColor.GRAY + "of a horror that once");
		lore.add(ChatColor.GRAY + "walked this world.");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		viscera.setItemMeta(data);

		return viscera;
	}
}
