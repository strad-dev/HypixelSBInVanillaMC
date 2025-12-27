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

public class NecromancerBrooch implements Ingredients  {
	public static ItemStack getItem() {
		ItemStack brooch = new ItemStack(Material.AMETHYST_SHARD);
		brooch.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = brooch.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + "Necromancer's Brooch");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/necromancer_brooch");
		lore.add("");
		lore.add(ChatColor.GRAY + "This legendary brooch dropped out of");
		lore.add(ChatColor.GRAY + "Sadan's pocket when he died.  Perhaps");
		lore.add(ChatColor.GRAY + "it can be used to create something?");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		brooch.setItemMeta(data);

		return brooch;
	}
}