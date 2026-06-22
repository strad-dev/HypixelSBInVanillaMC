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

public class NecronSecrets implements Ingredients {
	public static ItemStack getItem() {
		ItemStack necronSecrets = new ItemStack(Material.PAPER);

		ItemMeta data = necronSecrets.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Necron's Secrets"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/ingredient/necron_secrets"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Being the right-hand man of the"));
		lore.add(Utils.mm("<gray>Wither King, Necron knows some tricks"));
		lore.add(Utils.mm("<gray>about being able to fly with lots of armor."));
		lore.add(Utils.mm("<gray>This paper, which he dropped behind him,"));
		lore.add(Utils.mm("<gray>contains all of those secrets."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		necronSecrets.setItemMeta(data);

		return necronSecrets;
	}
}