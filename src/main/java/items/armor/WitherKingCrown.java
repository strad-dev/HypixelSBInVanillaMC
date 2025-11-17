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

public class WitherKingCrown implements Armor {
	public static ItemStack getItem() {
		ItemStack crown = new ItemStack(Material.GOLDEN_HELMET);

		ItemMeta data = crown.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Crown of the Wither King");
		AttributeModifier damage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "crownDamage"), 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
		AttributeModifier armor = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "crownArmor"), 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
		AttributeModifier toughness = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "crownToughness"), 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
		AttributeModifier antiKB = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "crownAntiKB"), 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.ARMOR, armor);
		data.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/wither_king_crown");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "+10%");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "The Wither King left behind this");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "crown after His unfortunate demise.");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC HELMET " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		crown.setItemMeta(data);

		return crown;
	}
}
