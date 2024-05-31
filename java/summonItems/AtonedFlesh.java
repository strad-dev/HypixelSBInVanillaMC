package summonItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AtonedFlesh implements SummonItem {
	public static ItemStack getItem() {
		ItemStack atonedFlesh = new ItemStack(Material.ROTTEN_FLESH);
		atonedFlesh.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = atonedFlesh.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Atoned Flesh");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/summon/atoned_flesh");
		lore.add("");
		lore.add(ChatColor.GRAY + "A suspicious piece of flesh");
		lore.add(ChatColor.GRAY + "left behind by an unknown being.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Use this on a Zombie to");
		lore.add(ChatColor.GRAY + "summon the Atoned Horror!");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		atonedFlesh.setItemMeta(data);

		return atonedFlesh;
	}

	@Override
	public void onRightClick(LivingEntity e) {

	}
}
