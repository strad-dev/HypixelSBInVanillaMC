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

public class NullBlade implements Ingredients {
	public static ItemStack getItem() {
		ItemStack nullBlade = new ItemStack(Material.SHEARS);

		ItemMeta data = nullBlade.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<gold><bold>Null Blade"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/null_blade"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>A pair of Shears so null that even"));
		lore.add(Utils.mm("<gray>the most intelligent players"));
		lore.add(Utils.mm("<gray>are confused by it."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold><bold><obfuscated>a</obfuscated> LEGENDARY <obfuscated>a</obfuscated>"));

		data.lore(lore);
		nullBlade.setItemMeta(data);

		return nullBlade;
	}
}