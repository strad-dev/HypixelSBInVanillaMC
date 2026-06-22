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

public class MaxorBoots implements Armor {
	public static ItemStack getItem() {
		ItemStack maxorBoots = new ItemStack(Material.NETHERITE_BOOTS);

		ItemMeta data = maxorBoots.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<light_purple>Maxor's Boots"));
		AttributeModifier damage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "maxorBootsDamage"), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
		AttributeModifier armor = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "maxorBootsArmor"), 4.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
		AttributeModifier antiKB = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "maxorBootsAntiKB"), 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
		AttributeModifier speed = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "maxorBootsSpeed"), 3, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.FEET);
		AttributeModifier antiFall = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "maxorBootsAntiFall"), -0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
		data.addAttributeModifier(Attribute.MOVEMENT_SPEED, speed);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.ARMOR, armor);
		data.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, antiKB);
		data.addAttributeModifier(Attribute.FALL_DAMAGE_MULTIPLIER, antiFall);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/maxor_boots"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>+1"));
		lore.add(Utils.mm("<gray>Armor: <red>+4.5"));
		lore.add(Utils.mm("<gray>Knockback Resistance: <red>+10%"));
		lore.add(Utils.mm("<gray>Speed: <red>x4"));
		lore.add(Utils.mm("<gray>Fall Damage: <red>-10%"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>Zoooooooooooooooooooooooooooooom"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<light_purple><bold><obfuscated>a</obfuscated> MYTHIC BOOTS <obfuscated>a</obfuscated>"));

		data.lore(lore);
		maxorBoots.setItemMeta(data);

		return maxorBoots;
	}
}
