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

public class AncientDragonEgg implements Ingredients  {
	public static ItemStack getItem() {
		ItemStack egg = new ItemStack(Material.DRAGON_EGG);

		ItemMeta data = egg.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<light_purple><bold>Ancient Dragon Egg"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/ancient_dragon_egg"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>An extremely rare and ancient Dragon Egg."));
		lore.add(Utils.mm("<gray>Legends say it is the last of its kind."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<light_purple><bold><obfuscated>a</obfuscated> MYTHIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		egg.setItemMeta(data);

		return egg;
	}
}