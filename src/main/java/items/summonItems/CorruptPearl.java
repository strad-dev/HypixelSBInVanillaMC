package items.summonItems;

import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CorruptPearl implements SummonItem {
	public static ItemStack getItem() {
		ItemStack corruptPearl = new ItemStack(Material.ENDER_EYE);
		ItemMeta data = corruptPearl.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<blue><bold>Corrupted Pearl"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/summon/corrupt_pearl"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>An interesting mutation"));
		lore.add(Utils.mm("<gray>of Ender Pearls."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Use this on an Enderman to"));
		lore.add(Utils.mm("<gray>summon a Mutant Enderman!"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<blue><bold><obfuscated>a</obfuscated> RARE <obfuscated>a</obfuscated>"));

		data.lore(lore);
		corruptPearl.setItemMeta(data);

		return corruptPearl;
	}
}
