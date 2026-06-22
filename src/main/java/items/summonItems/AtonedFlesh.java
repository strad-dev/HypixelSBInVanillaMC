package items.summonItems;

import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AtonedFlesh implements SummonItem {
	public static ItemStack getItem() {
		ItemStack atonedFlesh = new ItemStack(Material.ROTTEN_FLESH);
		ItemMeta data = atonedFlesh.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<blue><bold>Atoned Flesh"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/summon/atoned_flesh"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>A suspicious piece of flesh"));
		lore.add(Utils.mm("<gray>left behind by an unknown being."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Use this on a Zombie to"));
		lore.add(Utils.mm("<gray>summon the Atoned Horror!"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<blue><bold><obfuscated>a</obfuscated> RARE <obfuscated>a</obfuscated>"));

		data.lore(lore);
		atonedFlesh.setItemMeta(data);

		return atonedFlesh;
	}
}
