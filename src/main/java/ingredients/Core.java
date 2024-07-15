package ingredients;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Core implements Ingredients {
	public static ItemStack getItem() {
		ItemStack core = new ItemStack(Material.CHISELED_QUARTZ_BLOCK);
		core.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = core.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Judgement Core");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/judgement_core");
		lore.add("");
		lore.add(ChatColor.GRAY + "A core so powerful that even");
		lore.add(ChatColor.GRAY + "the most dedicated players");
		lore.add(ChatColor.GRAY + "tremble at it's power.");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		core.setItemMeta(data);

		return core;
	}
}