package items.armor;

import misc.Plugin;
import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NecromancerLordLeggings implements Armor {
	public static ItemStack getItem() {
		ItemStack necromancerLordLeggings = new ItemStack(Material.NETHERITE_LEGGINGS);

		ItemMeta data = necromancerLordLeggings.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<light_purple>Necromancer Lord Leggings"));
		AttributeModifier damage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "necromancerLeggingsDamage"), 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
		AttributeModifier armor = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "necromancerLeggingsArmor"), 7, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
		AttributeModifier antiKB = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "necromancerLeggingsAntiKB"), 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.ARMOR, armor);
		data.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/necromancer_pants"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>+2"));
		lore.add(Utils.mm("<gray>Armor: <red>+7"));
		lore.add(Utils.mm("<gray>Knockback Resistance: <red>+10%"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>A powerful pair of pants that allow"));
		lore.add(Utils.mm("<gray><italic>the wearer to punch slightly harder."));
		lore.add(Utils.mm("<gray><italic>However, it is not as sturdy."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<light_purple><bold><obfuscated>a</obfuscated> MYTHIC LEGGINGS <obfuscated>a</obfuscated>"));

		data.lore(lore);
		necromancerLordLeggings.setItemMeta(data);

		return necromancerLordLeggings;
	}
}
