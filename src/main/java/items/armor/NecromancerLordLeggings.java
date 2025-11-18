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

public class NecromancerLordLeggings implements Armor {
	public static ItemStack getItem() {
		ItemStack necromancerLordLeggings = new ItemStack(Material.NETHERITE_LEGGINGS);

		ItemMeta data = necromancerLordLeggings.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Necromancer Lord Leggings");
		AttributeModifier damage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "necromancerLeggingsDamage"), 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
		AttributeModifier armor = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "necromancerLeggingsArmor"), 6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
		AttributeModifier toughness = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "necromancerLeggingsToughness"), 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
		AttributeModifier antiKB = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "necromancerLeggingsAntiKB"), 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.ARMOR, armor);
		data.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/necromancer_pants");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+2");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+6");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "+10%");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "A powerful pair of pants that allow");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "the wearer to punch slightly harder.");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "However, it is not as sturdy.");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC LEGGINGS " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		necromancerLordLeggings.setItemMeta(data);

		return necromancerLordLeggings;
	}
}