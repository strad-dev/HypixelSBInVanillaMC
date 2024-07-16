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

public class NecronElytra implements Armor {
	public static ItemStack getItem() {
		ItemStack necronElytra = new ItemStack(Material.ELYTRA);

		ItemMeta data = necronElytra.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Necron's Elytra");
		AttributeModifier damage = new AttributeModifier(UUID.fromString("636ece54-f65c-4275-8ab7-916a1a1056ca"), "necronElytraDamage", 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
		AttributeModifier armor = new AttributeModifier(UUID.fromString("92166a58-d64d-465b-9810-3f8966f33566"), "necronElytraArmor", 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
		AttributeModifier toughness = new AttributeModifier(UUID.fromString("e0f20d73-639a-4530-937e-c7f298b268ec"), "necronElytraToughness", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
		AttributeModifier antiKB = new AttributeModifier(UUID.fromString("2855bb5e-6e64-4dea-a5c1-63725ec18c9a"), "necronElytraAntiKB", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, antiKB);
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