package ingredients;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GoldorSecrets implements Ingredients {
	public static ItemStack getItem() {

		ItemStack goldorSecrets = new ItemStack(Material.PAPER);
		goldorSecrets.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = goldorSecrets.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Goldor's Secrets");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/goldor_secrets");
		lore.add("");
		lore.add(ChatColor.GRAY + "A piece of research that has over three");
		lore.add(ChatColor.GRAY + "centuries of work, studying how to grant");
		lore.add(ChatColor.GRAY + "just one more of a stat deemed to already");
		lore.add(ChatColor.GRAY + "have been completely maxed out.  Unfortunately,");
		lore.add(ChatColor.GRAY + "Goldor was a but flustered at being defeated,");
		lore.add(ChatColor.GRAY + "and dropped this research behind him.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		goldorSecrets.setItemMeta(data);

		return goldorSecrets;
	}
}
