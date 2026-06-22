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

public class GoldorLeggings implements Armor {
	public static ItemStack getItem() {
		ItemStack goldorLeggings = new ItemStack(Material.NETHERITE_LEGGINGS);

		ItemMeta data = goldorLeggings.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<light_purple>Goldor's Leggings"));
		AttributeModifier damage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "goldorLeggingsDamage"), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
		AttributeModifier armor = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "goldorLeggingsArmor"), 7.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
		AttributeModifier antiKB = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "goldorLeggingsAntiKB"), 0.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.ARMOR, armor);
		data.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/goldor_pants"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>+1"));
		lore.add(Utils.mm("<gray>Armor: <red>+7.5"));
		lore.add(Utils.mm("<gray>Knockback Resistance: <red>+20%"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>Goldor has spent centuries"));
		lore.add(Utils.mm("<gray><italic>researching how to make Netherite"));
		lore.add(Utils.mm("<gray><italic>Leggings that are even tougher!"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<light_purple><bold><obfuscated>a</obfuscated> MYTHIC LEGGINGS <obfuscated>a</obfuscated>"));

		data.lore(lore);
		goldorLeggings.setItemMeta(data);

		return goldorLeggings;
	}
}
