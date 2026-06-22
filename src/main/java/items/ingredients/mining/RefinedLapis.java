package items.ingredients.mining;

import items.ingredients.Ingredients;
import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RefinedLapis implements Ingredients {
	public static ItemStack getItem() {
		ItemStack refinedLapis = new ItemStack(Material.LAPIS_LAZULI);

		ItemMeta data = refinedLapis.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Refined Lapis"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/refined_lapis"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>This piece of Lapis is brighter than usual."));
		lore.add(Utils.mm("<gray><italic>Maybe it has useful properties?"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		refinedLapis.setItemMeta(data);

		return refinedLapis;
	}
}