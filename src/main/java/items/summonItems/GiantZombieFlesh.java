package items.summonItems;

import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GiantZombieFlesh implements SummonItem {
	public static ItemStack getItem() {
		ItemStack giantZombieFlesh = new ItemStack(Material.ROTTEN_FLESH);
		ItemMeta data = giantZombieFlesh.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Giant Zombie Flesh"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/summon/giant_flesh"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>This zombie dropped a larger"));
		lore.add(Utils.mm("<gray>piece of flesh than normal."));
		lore.add(Utils.mm("<gray>Maybe it has useful properties?"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Use this on a Zombie"));
		lore.add(Utils.mm("<gray>to summon Sadan!"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		giantZombieFlesh.setItemMeta(data);

		return giantZombieFlesh;
	}
}
