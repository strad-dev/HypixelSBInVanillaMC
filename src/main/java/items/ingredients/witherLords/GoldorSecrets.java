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

public class GoldorSecrets implements Ingredients {
	public static ItemStack getItem() {

		ItemStack goldorSecrets = new ItemStack(Material.PAPER);

		ItemMeta data = goldorSecrets.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Goldor's Secrets"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/goldor_secrets"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>A piece of research that has over three"));
		lore.add(Utils.mm("<gray>centuries of work, studying how to grant"));
		lore.add(Utils.mm("<gray>just one more of a stat deemed to already"));
		lore.add(Utils.mm("<gray>have been completely maxed out.  Unfortunately,"));
		lore.add(Utils.mm("<gray>Goldor was a but flustered at being defeated,"));
		lore.add(Utils.mm("<gray>and dropped this research behind him."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		goldorSecrets.setItemMeta(data);

		return goldorSecrets;
	}
}