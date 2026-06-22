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

public class NecromancerBrooch implements Ingredients  {
	public static ItemStack getItem() {
		ItemStack brooch = new ItemStack(Material.AMETHYST_SHARD);

		ItemMeta data = brooch.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<light_purple><bold>Necromancer's Brooch"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/necromancer_brooch"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>This legendary brooch dropped out of"));
		lore.add(Utils.mm("<gray>Sadan's pocket when he died.  Perhaps"));
		lore.add(Utils.mm("<gray>it can be used to create something?"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<light_purple><bold><obfuscated>a</obfuscated> MYTHIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		brooch.setItemMeta(data);

		return brooch;
	}
}