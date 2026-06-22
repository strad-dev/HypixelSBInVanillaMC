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

public class RefinedGold implements Ingredients {
	public static ItemStack getItem() {
		ItemStack refinedGold = new ItemStack(Material.GOLD_INGOT);

		ItemMeta data = refinedGold.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Refined Gold"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/refined_gold"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>Piglins tremble at its"));
		lore.add(Utils.mm("<gray><italic>immense rarity and value."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		refinedGold.setItemMeta(data);

		return refinedGold;
	}
}