package items.summonItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GiantZombieFlesh implements summonItems.SummonItem {
	public static ItemStack getItem() {
		ItemStack giantZombieFlesh = new ItemStack(Material.ROTTEN_FLESH);
		giantZombieFlesh.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = giantZombieFlesh.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Giant Zombie Flesh");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/summon/giant_flesh");
		lore.add("");
		lore.add(ChatColor.GRAY + "This zombie dropped a larger");
		lore.add(ChatColor.GRAY + "piece of flesh than normal.");
		lore.add(ChatColor.GRAY + "Maybe it has useful properties?");
		lore.add("");
		lore.add(ChatColor.GRAY + "Use this on a Zombie");
		lore.add(ChatColor.GRAY + "to summon Sadan!");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		giantZombieFlesh.setItemMeta(data);

		return giantZombieFlesh;
	}

	@Override
	public void onRightClick(LivingEntity e) {

	}
}