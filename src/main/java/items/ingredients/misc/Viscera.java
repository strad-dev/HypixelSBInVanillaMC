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

public class Viscera implements Ingredients {
	public static ItemStack getItem() {
		ItemStack viscera = new ItemStack(Material.COOKED_PORKCHOP);

		ItemMeta data = viscera.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<blue><bold>Revenant Viscera"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/revenant_viscera"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>The disgusting remains"));
		lore.add(Utils.mm("<gray>of a horror that once"));
		lore.add(Utils.mm("<gray>walked this world."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<blue><bold><obfuscated>a</obfuscated> RARE <obfuscated>a</obfuscated>"));

		data.lore(lore);
		viscera.setItemMeta(data);

		return viscera;
	}
}