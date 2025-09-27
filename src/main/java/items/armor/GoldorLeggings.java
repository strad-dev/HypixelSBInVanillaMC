package items.armor;

import misc.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
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
		AttributeModifier damage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "goldorLeggingsDamage"), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
		AttributeModifier armor = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "goldorLeggingsArmor"), 6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
		AttributeModifier toughness = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "goldorLeggingsToughness"), 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
		AttributeModifier antiKB = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "goldorLeggingsAntiKB"), 0.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.ARMOR, armor);
		data.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, antiKB);
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