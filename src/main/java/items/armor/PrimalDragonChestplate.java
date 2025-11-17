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

public class PrimalDragonChestplate {
	public static ItemStack getItem() {
		ItemStack primalChestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);

		ItemMeta data = primalChestplate.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Primal Dragon Chestplate");
		AttributeModifier damage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "primalChestplateDamage"), 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
		AttributeModifier armor = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "primalChestplateArmor"), 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
		AttributeModifier toughness = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "primalChestplateToughness"), 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
		AttributeModifier antiKB = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "primalChestplateAntiKB"), 0.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.ARMOR, armor);
		data.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/primal_chestplate");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+2");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+8");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+4");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "+20%");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "A powerful chestplate forged from");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "the remnants of the Primal Dragon.");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "It's sturdy, but makes you too heavy to fly.");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC CHESTPLATE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		primalChestplate.setItemMeta(data);

		return primalChestplate;
	}
}