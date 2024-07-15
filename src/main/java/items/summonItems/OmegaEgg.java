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

public class OmegaEgg implements summonItems.SummonItem {
	public static ItemStack getItem() {

		ItemStack omegaEgg = new ItemStack(Material.EGG);
		omegaEgg.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = omegaEgg.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Omega Egg");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/summon/omega_egg");
		lore.add("");
		lore.add(ChatColor.GRAY + "A strange specimen used to");
		lore.add(ChatColor.GRAY + "create the strongest Chickens.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Use this on a Chicken");
		lore.add(ChatColor.GRAY + "to summon Chickzilla!");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		omegaEgg.setItemMeta(data);

		return omegaEgg;
	}

	@Override
	public void onRightClick(LivingEntity e) {

	}
}
