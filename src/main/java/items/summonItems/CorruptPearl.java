package items.summonItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CorruptPearl implements SummonItem {
	public static ItemStack getItem() {
		ItemStack corruptPearl = new ItemStack(Material.ENDER_EYE);
		corruptPearl.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = corruptPearl.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Corrupted Pearl");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/summon/corrupt_pearl");
		lore.add("");
		lore.add(ChatColor.GRAY + "An interesting mutation");
		lore.add(ChatColor.GRAY + "of Ender Pearls.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Use this on an Enderman to");
		lore.add(ChatColor.GRAY + "summon a Mutant Enderman!");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		corruptPearl.setItemMeta(data);

		return corruptPearl;
	}
}
