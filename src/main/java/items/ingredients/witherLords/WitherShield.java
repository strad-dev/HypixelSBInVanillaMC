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

public class WitherShield implements Ingredients {
	public static ItemStack getItem() {
		ItemStack witherShield = new ItemStack(Material.ENCHANTED_BOOK);

		ItemMeta data = witherShield.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Wither Shield"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/wither_shield"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>A rare Enchanted Book imbued"));
		lore.add(Utils.mm("<gray>with the power of Goldor."));
		lore.add(Utils.mm("<gray>Grants the ability to summon"));
		lore.add(Utils.mm("<gray>6 hearts of absorption and"));
		lore.add(Utils.mm("<gray>take 15% less damage for 5 seconds."));
		lore.add(Utils.mm("<gray>Extra absorption is converted to"));
		lore.add(Utils.mm("<gray>healing after 5 seconds."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		witherShield.setItemMeta(data);

		return witherShield;
	}
}