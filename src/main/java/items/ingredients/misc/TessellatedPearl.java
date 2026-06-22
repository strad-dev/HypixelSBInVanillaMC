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

public class TessellatedPearl implements Ingredients {
	public static ItemStack getItem() {
		ItemStack tessellated = new ItemStack(Material.ENDER_PEARL);

		ItemMeta data = tessellated.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Tessellated Ender Pearl"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/tessellated_pearl"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>An Ender Pearl so dense that even"));
		lore.add(Utils.mm("<gray>the most knowledgeable players"));
		lore.add(Utils.mm("<gray>are mystified by it."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		tessellated.setItemMeta(data);

		return tessellated;
	}
}