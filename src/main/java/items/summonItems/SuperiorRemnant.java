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

public class SuperiorRemnant implements SummonItem {
	public static ItemStack getItem() {
		ItemStack supRemnant = new ItemStack(Material.QUARTZ);
		supRemnant.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = supRemnant.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Remnant of the Superior Dragon");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/summon/superior_remnant");
		lore.add("");
		lore.add(ChatColor.GRAY + "The remains of the strongest");
		lore.add(ChatColor.GRAY + "Dragon to ever exist.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Use this on an Enderman to summon");
		lore.add(ChatColor.GRAY + "the terrifying Voidgloom Seraph!");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		supRemnant.setItemMeta(data);

		return supRemnant;
	}

	@Override
	public void onRightClick(LivingEntity e) {
	}
}
