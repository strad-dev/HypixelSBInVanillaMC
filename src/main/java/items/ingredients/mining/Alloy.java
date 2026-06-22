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

public class Alloy implements Ingredients {
	public static ItemStack getItem() {
		ItemStack alloy = new ItemStack(Material.GOLD_BLOCK);

		ItemMeta data = alloy.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<gold><bold>Divan's Alloy"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/alloy"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>The legendary Divan explored"));
		lore.add(Utils.mm("<gray><italic>where no other man has set foot,"));
		lore.add(Utils.mm("<gray><italic>and came back with this"));
		lore.add(Utils.mm("<gray><italic>incredibly rare alloy."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold><bold><obfuscated>a</obfuscated> LEGENDARY <obfuscated>a</obfuscated>"));

		data.lore(lore);
		alloy.setItemMeta(data);

		return alloy;
	}
}