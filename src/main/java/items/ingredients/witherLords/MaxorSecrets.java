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

public class MaxorSecrets implements Ingredients {
	public static ItemStack getItem() {
		ItemStack maxorSecrets = new ItemStack(Material.PAPER);

		ItemMeta data = maxorSecrets.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Maxor's Secrets"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/maxor_secrets"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Maxor has some secrets.  Being"));
		lore.add(Utils.mm("<gray>the youngest Wither, he was always"));
		lore.add(Utils.mm("<gray>able to snoop around and find"));
		lore.add(Utils.mm("<gray>a fair few bits of information"));
		lore.add(Utils.mm("<gray>that he otherwise was not supposed"));
		lore.add(Utils.mm("<gray>to know about until he was much older."));
		lore.add(Utils.mm("<gray>This meant that he was also chased"));
		lore.add(Utils.mm("<gray>around a lot, which prompted him to"));
		lore.add(Utils.mm("<gray>learn how to fly faster.  The paper"));
		lore.add(Utils.mm("<gray>is ripped after this point, but"));
		lore.add(Utils.mm("<gray>it still has some useful properties."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		maxorSecrets.setItemMeta(data);

		return maxorSecrets;
	}
}