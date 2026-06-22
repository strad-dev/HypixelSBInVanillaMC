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

public class RefinedIron implements Ingredients {
	public static ItemStack getItem() {
		ItemStack refinedIron = new ItemStack(Material.IRON_INGOT);

		ItemMeta data = refinedIron.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Refined Iron"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/refined_iron"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>A shiny ingot in its purest form"));
		lore.add(Utils.mm("<gray><italic>straight from the mines."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		refinedIron.setItemMeta(data);

		return refinedIron;
	}
}