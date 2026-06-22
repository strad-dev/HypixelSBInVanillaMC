package items.summonItems;

import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Antimatter implements SummonItem {
	public static ItemStack getItem() {

		ItemStack antimatter = new ItemStack(Material.WARPED_FUNGUS);
		ItemMeta data = antimatter.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Antimatter"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/summon/antimatter"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>The consequence of storing such"));
		lore.add(Utils.mm("<gray>massive amounts of Iron together."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Use this on an Iron Golem"));
		lore.add(Utils.mm("<gray>to summon a meloG norI!"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		antimatter.setItemMeta(data);

		return antimatter;
	}
}
