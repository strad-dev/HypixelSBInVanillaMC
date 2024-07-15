package ingredients;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MaxorSecrets implements Ingredients {
	public static ItemStack getItem() {
		ItemStack maxorSecrets = new ItemStack(Material.PAPER);
		maxorSecrets.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = maxorSecrets.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Maxor's Secrets");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/maxor_secrets");
		lore.add("");
		lore.add(ChatColor.GRAY + "Maxor has some secrets.  Being");
		lore.add(ChatColor.GRAY + "the youngest Wither, he was always");
		lore.add(ChatColor.GRAY + "able to snoop around and find");
		lore.add(ChatColor.GRAY + "a fair few bits of information");
		lore.add(ChatColor.GRAY + "that he otherwise was not supposed");
		lore.add(ChatColor.GRAY + "to know about until he was much older.");
		lore.add(ChatColor.GRAY + "This meant that he was also chased");
		lore.add(ChatColor.GRAY + "around a lot, which prompted him to");
		lore.add(ChatColor.GRAY + "learn how to fly faster.  The paper");
		lore.add(ChatColor.GRAY + "is ripped after this point, but");
		lore.add(ChatColor.GRAY + "it still has some useful properties.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		maxorSecrets.setItemMeta(data);

		return maxorSecrets;
	}
}