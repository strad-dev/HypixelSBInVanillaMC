package items.weapons;

import items.CustomItem;
import misc.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Claymore implements CustomItem {
	public static ItemStack getItem(Enchantment ench, int enchLevel) {
		ItemStack claymore = new ItemStack(Material.STONE_SWORD);

		ItemMeta data = claymore.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Dark Claymore");
		AttributeModifier attackSpeed = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "claymoreModifier"), 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "claymoreModifierDmg"), 9, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		AttributeModifier attackRange = new AttributeModifier(new NamespacedKey(Plugin.getInstance(),  "claymoreModifierRange"), 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.ATTACK_SPEED, attackSpeed);
		data.addAttributeModifier(Attribute.ENTITY_INTERACTION_RANGE, attackRange);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		String loreDamage = "9";
		if(ench.equals(Enchantment.SHARPNESS)) {
			switch(enchLevel) {
				case 1 -> loreDamage = "10";
				case 2 -> loreDamage = "11";
				case 3 -> loreDamage = "12";
				case 4 -> loreDamage = "13";
				case 5 -> loreDamage = "14";
				case 6 -> loreDamage = "15";
				case 7 -> loreDamage = "16";
				default -> loreDamage = "9";
			}
		}

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/dark_claymore");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+" + loreDamage);
		lore.add(ChatColor.GRAY + "Swing Range: " + ChatColor.RED + "+2");
		if(ench.equals(Enchantment.SMITE) || ench.equals(Enchantment.BANE_OF_ARTHROPODS)) {
			lore.add("");
			switch(enchLevel) {
				case 1 -> loreDamage = "2.5";
				case 2 -> loreDamage = "5";
				case 3 -> loreDamage = "7.5";
				case 4 -> loreDamage = "10";
				case 5 -> loreDamage = "12.5";
				case 6 -> loreDamage = "15";
				default -> loreDamage = "0";
			}
			if(ench.equals(Enchantment.SMITE)) {
				lore.add(ChatColor.GRAY + "Bonus Undead Damage: " + ChatColor.RED + "+" + loreDamage);
			} else {
				lore.add(ChatColor.GRAY + "Bonus Arthropod Damage: " + ChatColor.RED + "+" + loreDamage);
			}
		}
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "That thing was too big to be");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "called a sword, it was more like");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "a large hunk of stone.");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC SWORD " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		claymore.setItemMeta(data);

		return claymore;
	}
}
