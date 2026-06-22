package items.summonItems;

import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SuperiorRemnant implements SummonItem {
	public static ItemStack getItem() {
		ItemStack supRemnant = new ItemStack(Material.QUARTZ);
		ItemMeta data = supRemnant.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<gold><bold>Remnant of the Superior Dragon"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/summon/superior_remnant"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>The remains of the strongest"));
		lore.add(Utils.mm("<gray>Dragon to ever exist."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Use this on an Enderman to summon"));
		lore.add(Utils.mm("<gray>the terrifying Voidgloom Seraph!"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold><bold><obfuscated>a</obfuscated> LEGENDARY <obfuscated>a</obfuscated>"));

		data.lore(lore);
		supRemnant.setItemMeta(data);

		return supRemnant;
	}
}
