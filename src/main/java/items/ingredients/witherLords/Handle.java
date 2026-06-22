package items.ingredients.witherLords;

import items.ingredients.Ingredients;
import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Handle implements Ingredients {
	public static ItemStack getItem() {
		ItemStack handle = new ItemStack(Material.STICK);

		ItemMeta data = handle.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Necron's Handle"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/necron_handle"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>The hilt of the GREATEST sword"));
		lore.add(Utils.mm("<gray>to ever exist, imbued with the"));
		lore.add(Utils.mm("<gray>power of Necron."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		handle.setItemMeta(data);

		return handle;
	}
}