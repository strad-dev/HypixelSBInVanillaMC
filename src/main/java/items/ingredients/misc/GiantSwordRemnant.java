package items.ingredients.misc;

import items.ingredients.Ingredients;
import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GiantSwordRemnant implements Ingredients {
	public static ItemStack getItem() {
		ItemStack giantSwordRemnant = new ItemStack(Material.STICK);

		ItemMeta data = giantSwordRemnant.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Remnant of the Giant's Sword"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/giant_sword_remnant"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>A hilt with very strange properties."));
		lore.add(Utils.mm("<gray>Legend has it that it once belonged to"));
		lore.add(Utils.mm("<gray>Sadan, but historians disagree on this."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		giantSwordRemnant.setItemMeta(data);

		return giantSwordRemnant;
	}
}