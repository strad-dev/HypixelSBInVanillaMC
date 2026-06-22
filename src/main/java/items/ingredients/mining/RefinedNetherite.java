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

public class RefinedNetherite implements Ingredients {
	public static ItemStack getItem() {
		ItemStack refinedNetherite = new ItemStack(Material.NETHERITE_SCRAP);

		ItemMeta data = refinedNetherite.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<gold><bold>Refined Netherite"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/refined_netherite"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>The legendary Divan took a"));
		lore.add(Utils.mm("<gray><italic>trip to Hell and came back"));
		lore.add(Utils.mm("<gray><italic>with this interesting rock."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold><bold><obfuscated>a</obfuscated> LEGENDARY <obfuscated>a</obfuscated>"));

		data.lore(lore);
		refinedNetherite.setItemMeta(data);

		return refinedNetherite;
	}
}