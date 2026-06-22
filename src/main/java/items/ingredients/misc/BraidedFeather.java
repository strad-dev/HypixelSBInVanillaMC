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

public class BraidedFeather implements Ingredients {
	public static ItemStack getItem() {
		ItemStack braidedFeather = new ItemStack(Material.FEATHER);

		ItemMeta data = braidedFeather.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Braided Feather"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/braided_feather"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>A Feather so sturdy that even"));
		lore.add(Utils.mm("<gray>the most powerful players"));
		lore.add(Utils.mm("<gray>cannot destroy it."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		braidedFeather.setItemMeta(data);

		return braidedFeather;
	}
}