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

public class EnchantmentUpgrader implements Ingredients {
	// HIDE_ADDITIONAL_TOOLTIP is deprecated on 26.2 (tooltip control moved to the tooltip_display data
	// component), but there's no direct component equivalent for hiding a smithing template's built-in
	// "Applies to / Ingredients" lines, so we keep the still-functional flag and suppress the warning.
	@SuppressWarnings("deprecation")
	public static ItemStack getItem() {
		ItemStack upgrader = new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);

		ItemMeta data = upgrader.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<gold><bold>Exceedingly Rare Enchantment Upgrader"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/enchantment_upgrader"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>The item name is long both as a reference"));
		lore.add(Utils.mm("<gray><italic>but also because the devs found it funny."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold><bold><obfuscated>a</obfuscated> LEGENDARY <obfuscated>a</obfuscated>"));

		data.lore(lore);
		upgrader.setItemMeta(data);

		return upgrader;
	}
}