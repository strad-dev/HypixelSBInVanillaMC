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

public class ConcentratedStone implements Ingredients {
	public static ItemStack getItem() {
		ItemStack heavyStone = new ItemStack(Material.STONE);

		ItemMeta data = heavyStone.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<blue><bold>Concentrated Stone"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/concentrated_stone"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>The purest form of stone.  How can"));
		lore.add(Utils.mm("<gray><italic>something so simple, be so heavy?"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<blue><bold><obfuscated>a</obfuscated> RARE <obfuscated>a</obfuscated>"));

		data.lore(lore);
		heavyStone.setItemMeta(data);

		return heavyStone;
	}
}