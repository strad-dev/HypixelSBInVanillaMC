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

public class RefinedEmerald implements Ingredients {
	public static ItemStack getItem() {
		ItemStack refinedEmerald = new ItemStack(Material.EMERALD);

		ItemMeta data = refinedEmerald.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<gold><bold>Refined Emerald"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/refined_emerald"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>Villagers tremble at its"));
		lore.add(Utils.mm("<gray><italic>immense rarity and value."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold><bold><obfuscated>a</obfuscated> LEGENDARY <obfuscated>a</obfuscated>"));

		data.lore(lore);
		refinedEmerald.setItemMeta(data);

		return refinedEmerald;
	}
}