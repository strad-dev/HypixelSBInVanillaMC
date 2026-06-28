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

public class WardenHelmet implements Armor {
	public static ItemStack getItem() {
		ItemStack wardenHelmet = new ItemStack(Material.NETHERITE_HELMET);

		ItemMeta data = wardenHelmet.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<light_purple>Warden Helmet"));
		AttributeModifier damage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "wardenHelmetDamage"), 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
		AttributeModifier armor = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "wardenHelmetArmor"), 5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
		AttributeModifier antiKB = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "wardenHelmetAntiKB"), 0.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
		AttributeModifier speed = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "wardenHelmetSpeed"), -0.33333, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.HEAD);
		data.addAttributeModifier(Attribute.MOVEMENT_SPEED, speed);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.ARMOR, armor);
		data.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/warden_helmet"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>+1"));
		lore.add(Utils.mm("<gray>Armor: <red>+5"));
		lore.add(Utils.mm("<gray>Knockback Resistance: <red>+20%"));
		lore.add(Utils.mm("<gray>Speed: <red>-33%"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>The brute force of the Warden"));
		lore.add(Utils.mm("<gray><italic>packed into a single helmet."));
		lore.add(Utils.mm("<gray><italic>It grants you a lot of strength,"));
		lore.add(Utils.mm("<gray><italic>but makes you quite a bit sluggish."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<light_purple><bold><obfuscated>a</obfuscated> MYTHIC HELMET <obfuscated>a</obfuscated>"));

		data.lore(lore);
		wardenHelmet.setItemMeta(data);

		return wardenHelmet;
	}
}
