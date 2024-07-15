package ingredients;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StormSecrets implements Ingredients {
	public static ItemStack getItem() {
		ItemStack stormSecrets = new ItemStack(Material.PAPER);
		stormSecrets.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = stormSecrets.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Storm's Secrets");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/storm_secrets");
		lore.add("");
		lore.add(ChatColor.GRAY + "Despite being acquainted with the");
		lore.add(ChatColor.GRAY + "power of lightning, Storm is also");
		lore.add(ChatColor.GRAY + "acquainted with attacking players");
		lore.add(ChatColor.GRAY + "from much further than they are");
		lore.add(ChatColor.GRAY + "supposed to.  This paper contains");
		lore.add(ChatColor.GRAY + "the essence of the research.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		stormSecrets.setItemMeta(data);

		return stormSecrets;
	}
}
