package items.weapons;

import items.CustomItem;
import misc.Plugin;
import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Claymore implements CustomItem {
	public static ItemStack getItem(Enchantment ench, int enchLevel) {
		ItemStack claymore = new ItemStack(Material.STONE_SWORD);

		ItemMeta data = claymore.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<light_purple>Dark Claymore"));
		AttributeModifier attackSpeed = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "claymoreModifier"), 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "claymoreModifierDmg"), 9, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		AttributeModifier attackRange = new AttributeModifier(new NamespacedKey(Plugin.getInstance(),  "claymoreModifierRange"), 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.ATTACK_SPEED, attackSpeed);
		data.addAttributeModifier(Attribute.ENTITY_INTERACTION_RANGE, attackRange);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		String loreDamage = "9";
		if(ench.equals(Enchantment.SHARPNESS)) {
			loreDamage = String.valueOf(9 + enchLevel);
		}

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/dark_claymore"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>+" + loreDamage));
		lore.add(Utils.mm("<gray>Swing Range: <red>+2"));
		if(ench.equals(Enchantment.SMITE) || ench.equals(Enchantment.BANE_OF_ARTHROPODS)) {
			lore.add(Utils.mm(""));
			loreDamage = String.valueOf(enchLevel * 2.5);
			if(ench.equals(Enchantment.SMITE)) {
				lore.add(Utils.mm("<gray>Bonus Undead Damage: <red>+" + loreDamage));
			} else {
				lore.add(Utils.mm("<gray>Bonus Arthropod Damage: <red>+" + loreDamage));
			}
		}
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>That thing was too big to be"));
		lore.add(Utils.mm("<gray><italic>called a sword, it was more like"));
		lore.add(Utils.mm("<gray><italic>a large hunk of stone."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<light_purple><bold><obfuscated>a</obfuscated> MYTHIC SWORD <obfuscated>a</obfuscated>"));

		data.lore(lore);
		claymore.setItemMeta(data);

		return claymore;
	}
}
