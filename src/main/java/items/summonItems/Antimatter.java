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

public class Antimatter implements SummonItem {
	public static ItemStack getItem() {

		ItemStack antimatter = new ItemStack(Material.WARPED_FUNGUS);
		antimatter.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = antimatter.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Antimatter");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/summon/antimatter");
		lore.add("");
		lore.add(ChatColor.GRAY + "The consequence of storing such");
		lore.add(ChatColor.GRAY + "massive amounts of Iron together.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Use this on an Iron Golem");
		lore.add(ChatColor.GRAY + "to summon a meloG norI!");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		antimatter.setItemMeta(data);

		return antimatter;
	}

	@Override
	public void onRightClick(LivingEntity e) {

	}
}
