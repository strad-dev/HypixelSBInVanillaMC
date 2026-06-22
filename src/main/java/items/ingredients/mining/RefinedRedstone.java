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

public class RefinedRedstone implements Ingredients {
	public static ItemStack getItem() {
		ItemStack refinedRedstone = new ItemStack(Material.REDSTONE);

		ItemMeta data = refinedRedstone.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Refined Redstone"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/refined_redstone"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>Redstone of the highest quality."));
		lore.add(Utils.mm("<gray><italic>Use it wisely."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		refinedRedstone.setItemMeta(data);

		return refinedRedstone;
	}
}