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

public class WardenHeart implements Ingredients {
	public static ItemStack getItem() {

		ItemStack wardenHeart = new ItemStack(Material.REDSTONE_BLOCK);

		ItemMeta data = wardenHeart.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<gold><bold>Warden Heart"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/warden_heart"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>The heart of a powerful creature,"));
		lore.add(Utils.mm("<gray>dropped by the Warden."));
		lore.add(Utils.mm("<gray>(NOT the Atoned Horror)"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold><bold><obfuscated>a</obfuscated> LEGENDARY <obfuscated>a</obfuscated>"));

		data.lore(lore);
		wardenHeart.setItemMeta(data);

		return wardenHeart;
	}
}