package items.armor;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WardenHelmet implements Armor {
	public static ItemStack getItem() {
		ItemStack wardenHelmet = new ItemStack(Material.NETHERITE_HELMET);

		ItemMeta data = wardenHelmet.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Warden Helmet");
		AttributeModifier damage = new AttributeModifier(UUID.fromString("4b5a478b-3b58-42ab-917d-8194a1a6d679"), "wardenHelmetDamage", 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
		AttributeModifier armor = new AttributeModifier(UUID.fromString("b9f30162-1921-4cc6-ad60-91cc43234ba3"), "wardenHelmetArmor", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
		AttributeModifier toughness = new AttributeModifier(UUID.fromString("c1474682-574f-472c-a611-62da34f6e1f1"), "wardenHelmetToughness", 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
		AttributeModifier antiKB = new AttributeModifier(UUID.fromString("4e16c033-3349-4fb7-a0c6-0fac98ef6c6a"), "wardenHelmetAntiKB", 0.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
		AttributeModifier speed = new AttributeModifier(UUID.fromString("f15d0a37-e366-4c5a-982d-57995d036a6c"), "wardenHelmetSpeed", -0.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HEAD);
		data.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, speed);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/warden_helmet");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+2");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+4");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "-20%");
		lore.add(ChatColor.GRAY + "Speed: " + ChatColor.RED + "x0.5");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "The brute force of the Warden");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "packed into a single helmet.");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "It grants you a lot of strength,");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "but makes you quite a bit sluggish.");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC HELMET " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		wardenHelmet.setItemMeta(data);

		return wardenHelmet;
	}
}