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

public class TarantulaSilk implements Ingredients {
	public static ItemStack getItem() {

		ItemStack taraSilk = new ItemStack(Material.COBWEB);

		ItemMeta data = taraSilk.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<blue><bold>Tarantula Silk"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/tarantula_silk"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>A Web so perfect that even"));
		lore.add(Utils.mm("<gray>the most powerful players"));
		lore.add(Utils.mm("<gray>cannot escape it."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<blue><bold><obfuscated>a</obfuscated> RARE <obfuscated>a</obfuscated>"));

		data.lore(lore);
		taraSilk.setItemMeta(data);

		return taraSilk;
	}
}