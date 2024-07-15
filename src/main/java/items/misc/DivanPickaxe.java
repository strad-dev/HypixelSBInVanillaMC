package items.misc;

import items.CustomItem;
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

public class DivanPickaxe implements CustomItem {
	public static ItemStack getItem() {
		ItemStack divanPickaxe = new ItemStack(Material.NETHERITE_PICKAXE);

		ItemMeta data = divanPickaxe.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.AQUA + "Divan's Pickaxe");
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "DivanModifier"), -1000, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		AttributeModifier miningSpeed = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "DivanSpeed"), 0.2, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.MAINHAND);
		AttributeModifier miningReach = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "DivanReach"), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.PLAYER_MINING_EFFICIENCY, miningSpeed);
		data.addAttributeModifier(Attribute.PLAYER_BLOCK_INTERACTION_RANGE, miningReach);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/divan_pickaxe");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "0");
		lore.add(ChatColor.GRAY + "Mining Speed: " + ChatColor.RED + "+20%");
		lore.add(ChatColor.GRAY + "Range: " + ChatColor.RED + "+1");
		lore.add("");
		lore.add(ChatColor.GOLD + "Passive Ability: Double Drops");
		lore.add(ChatColor.GRAY + "Grants " + ChatColor.RED + "x2" + ChatColor.GRAY + " drops from");
		lore.add(ChatColor.GRAY + "every ore mined.");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "An extremely powerful");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "pickaxe forged with the");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "rarest materials in existance!");
		lore.add("");
		lore.add(ChatColor.AQUA + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.AQUA + ChatColor.BOLD + " DIVINE PICKAXE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		divanPickaxe.setItemMeta(data);

		return divanPickaxe;
	}
}