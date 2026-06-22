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

public class NullOvoid implements Ingredients  {
	public static ItemStack getItem() {
		ItemStack ovoid = new ItemStack(Material.ENDERMAN_SPAWN_EGG);

		ItemMeta data = ovoid.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<blue><bold>Null Ovoid"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/null_ovoid"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>The Zealots have utiliezed this"));
		lore.add(Utils.mm("<gray>item to its fullest potential."));
		lore.add(Utils.mm("<gray>Maybe you can as well?"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<blue><bold><obfuscated>a</obfuscated> RARE <obfuscated>a</obfuscated>"));

		data.lore(lore);
		ovoid.setItemMeta(data);

		return ovoid;
	}
}