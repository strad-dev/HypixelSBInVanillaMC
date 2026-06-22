package items.summonItems;

import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class OmegaEgg implements SummonItem {
	public static ItemStack getItem() {

		ItemStack omegaEgg = new ItemStack(Material.EGG);
		ItemMeta data = omegaEgg.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<blue><bold>Omega Egg"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/summon/omega_egg"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>A strange specimen used to"));
		lore.add(Utils.mm("<gray>create the strongest Chickens."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Use this on a Chicken"));
		lore.add(Utils.mm("<gray>to summon Chickzilla!"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<blue><bold><obfuscated>a</obfuscated> RARE <obfuscated>a</obfuscated>"));

		data.lore(lore);
		omegaEgg.setItemMeta(data);

		return omegaEgg;
	}
}
