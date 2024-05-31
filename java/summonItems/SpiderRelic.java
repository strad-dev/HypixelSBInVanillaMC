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

public class SpiderRelic implements SummonItem {
	public static ItemStack getItem() {
		ItemStack spiderRelic = new ItemStack(Material.FERMENTED_SPIDER_EYE);
		spiderRelic.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = spiderRelic.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Spider Relic");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/summon/spider_relic");
		lore.add("");
		lore.add(ChatColor.GRAY + "An ancient artifact left");
		lore.add(ChatColor.GRAY + "by the Broodfather itself.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Use this on a Spider to summon");
		lore.add(ChatColor.GRAY + "the Tarantula Broodfather!");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		spiderRelic.setItemMeta(data);

		return spiderRelic;
	}

	@Override
	public void onRightClick(LivingEntity e) {

	}
}
