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

public class GoldorLeggings implements Armor {
	public static ItemStack getItem() {
		ItemStack goldorLeggings = new ItemStack(Material.NETHERITE_LEGGINGS);

		ItemMeta data = goldorLeggings.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Goldor's Leggings");
		AttributeModifier damage = new AttributeModifier(UUID.fromString("dc60c4c3-18ee-4e60-a580-7e3604e720e9"), "goldorLeggingsDamage", 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
		AttributeModifier armor = new AttributeModifier(UUID.fromString("80b8389f-f018-48c9-b3c0-c576bce5520a"), "goldorLeggingsArmor", 6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
		AttributeModifier toughness = new AttributeModifier(UUID.fromString("307e9660-858e-4ba3-9e60-2e0debf02345"), "goldorLeggingsToughness", 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
		AttributeModifier antiKB = new AttributeModifier(UUID.fromString("e4fd0260-7d71-4881-b47b-4c1bc68ac440"), "goldorLeggingsAntiKB", 0.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/goldor_pants");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+1");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+6");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+4");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "-20%");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "Goldor has spent centuries");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "researching how to make Netherite");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "Leggings that are even tougher!");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC LEGGINGS " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		goldorLeggings.setItemMeta(data);

		return goldorLeggings;
	}
}