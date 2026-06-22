package items.summonItems;

import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SpiderRelic implements SummonItem {
	public static ItemStack getItem() {
		ItemStack spiderRelic = new ItemStack(Material.FERMENTED_SPIDER_EYE);
		ItemMeta data = spiderRelic.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<blue><bold>Spider Relic"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/summon/spider_relic"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>An ancient artifact left"));
		lore.add(Utils.mm("<gray>by the Broodfather itself."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Use this on a Spider to summon"));
		lore.add(Utils.mm("<gray>the Tarantula Broodfather!"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<blue><bold><obfuscated>a</obfuscated> RARE <obfuscated>a</obfuscated>"));

		data.lore(lore);
		spiderRelic.setItemMeta(data);

		return spiderRelic;
	}
}
