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

public class ShadowWarp implements Ingredients {
	public static ItemStack getItem() {
		ItemStack shadowWarp = new ItemStack(Material.ENCHANTED_BOOK);

		ItemMeta data = shadowWarp.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Shadow Warp"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/shadow_warp"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>A rare Enchanted Book imbued"));
		lore.add(Utils.mm("<gray>with the power of Maxor."));
		lore.add(Utils.mm("<gray>Grants the ability to teleport"));
		lore.add(Utils.mm("<gray>10 blocks using Wither Impact."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		shadowWarp.setItemMeta(data);

		return shadowWarp;
	}
}