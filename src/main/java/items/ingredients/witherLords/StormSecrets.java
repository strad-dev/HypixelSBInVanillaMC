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

public class StormSecrets implements Ingredients {
	public static ItemStack getItem() {
		ItemStack stormSecrets = new ItemStack(Material.PAPER);

		ItemMeta data = stormSecrets.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Storm's Secrets"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/storm_secrets"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Despite being acquainted with the"));
		lore.add(Utils.mm("<gray>power of lightning, Storm is also"));
		lore.add(Utils.mm("<gray>acquainted with attacking players"));
		lore.add(Utils.mm("<gray>from much further than they are"));
		lore.add(Utils.mm("<gray>supposed to.  This paper contains"));
		lore.add(Utils.mm("<gray>the essence of the research."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		stormSecrets.setItemMeta(data);

		return stormSecrets;
	}
}