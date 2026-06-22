package items.ingredients.misc;

import items.ingredients.Ingredients;
import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Core implements Ingredients {
	public static ItemStack getItem() {
		ItemStack core = new ItemStack(Material.CHISELED_QUARTZ_BLOCK);

		ItemMeta data = core.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<gold><bold>Judgement Core"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/judgement_core"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>A core so powerful that even"));
		lore.add(Utils.mm("<gray>the most dedicated players"));
		lore.add(Utils.mm("<gray>tremble at it's power."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold><bold><obfuscated>a</obfuscated> LEGENDARY <obfuscated>a</obfuscated>"));

		data.lore(lore);
		core.setItemMeta(data);

		return core;
	}
}