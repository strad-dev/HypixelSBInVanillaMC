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

public class NecronElytra implements Armor {
	public static ItemStack getItem() {
		ItemStack necronElytra = new ItemStack(Material.ELYTRA);

		ItemMeta data = necronElytra.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Necron's Elytra");
		AttributeModifier damage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "necronElytraDamage"), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
		AttributeModifier armor = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "necronElytraArmor"), 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
		AttributeModifier toughness = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "necronElytraToughness"), 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
		AttributeModifier antiKB = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "necronElytraAntiKB"), 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.ARMOR, armor);
		data.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/necron_elytra");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+1");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+8");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "-10%");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "This Elytra has the stats of a Netherite");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "Chestplate, while still allowing you to fly!");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC CHESTPLATE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		necronElytra.setItemMeta(data);

		return necronElytra;
	}
}