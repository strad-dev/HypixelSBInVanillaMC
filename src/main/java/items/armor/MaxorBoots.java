package items.armor;

import misc.Plugin;
import org.bukkit.ChatColor;
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
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Maxor's Boots");
		AttributeModifier damage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "maxorBootsDamage"), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
		AttributeModifier armor = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "maxorBootsArmor"), 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
		AttributeModifier toughness = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "maxorBootsToughness"), 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
		AttributeModifier antiKB = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "maxorBootsAntiKB"), 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
		AttributeModifier speed = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "maxorBootsSpeed"), 3, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.FEET);
		AttributeModifier antiFall = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "maxorBootsAntiFall"), -0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
		data.addAttributeModifier(Attribute.MOVEMENT_SPEED, speed);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.ARMOR, armor);
		data.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, antiKB);
		data.addAttributeModifier(Attribute.FALL_DAMAGE_MULTIPLIER, antiFall);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/maxor_boots");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+1");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "-10%");
		lore.add(ChatColor.GRAY + "Speed: " + ChatColor.RED + "x4");
		lore.add(ChatColor.GRAY + "Fall Damage: " + ChatColor.RED + "-10%");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "Zoooooooooooooooooooooooooooooom");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC BOOTS " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		maxorBoots.setItemMeta(data);

		return maxorBoots;
	}
}
