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

public class WardenHelmet implements Armor {
	public static ItemStack getItem() {
		ItemStack wardenHelmet = new ItemStack(Material.NETHERITE_HELMET);

		ItemMeta data = wardenHelmet.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Warden Helmet");
		AttributeModifier damage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "wardenHelmetDamage"), 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
		AttributeModifier armor = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "wardenHelmetArmor"), 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
		AttributeModifier toughness = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "wardenHelmetToughness"), 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
		AttributeModifier antiKB = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "wardenHelmetAntiKB"), 0.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
		AttributeModifier speed = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "wardenHelmetSpeed"), -0.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.HEAD);
		data.addAttributeModifier(Attribute.MOVEMENT_SPEED, speed);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.ARMOR, armor);
		data.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/warden_helmet");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+2");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+4");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "+20%");
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