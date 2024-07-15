package items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MaxorBoots implements CustomItem {
	public static ItemStack getItem() {
		ItemStack maxorBoots = new ItemStack(Material.NETHERITE_BOOTS);

		ItemMeta data = maxorBoots.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Maxor's Boots");
		AttributeModifier damage = new AttributeModifier(UUID.fromString("c5be199a-6b3f-4860-b7b8-36e45fef60a3"), "maxorBootsDamage", 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
		AttributeModifier armor = new AttributeModifier(UUID.fromString("d8fe7972-aa27-42b4-aa5a-9eac4fb3b608"), "maxorBootsArmor", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
		AttributeModifier toughness = new AttributeModifier(UUID.fromString("f3058f46-630f-4cb5-8682-1141c5317e47"), "maxorBootsToughness", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
		AttributeModifier antiKB = new AttributeModifier(UUID.fromString("0c759c56-bd52-41a4-803f-f15275e61e4c"), "maxorBootsAntiKB", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
		AttributeModifier speed = new AttributeModifier(UUID.fromString("02307a42-a70a-4e55-a915-a71264f7455d"), "maxorBootsSpeed", 3, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.FEET);
		data.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, speed);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/maxor_boots");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+1");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "-10%");
		lore.add(ChatColor.GRAY + "Speed: " + ChatColor.RED + "x4");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "Zoooooooooooooooooooooooooooooom");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC BOOTS " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		maxorBoots.setItemMeta(data);

		return maxorBoots;
	}

	@Override
	public void onRightClick(Player p) {

	}

	@Override
	public void onLeftClick(Player p) {

	}

	@Override
	public int manaCost() {
		return 0;
	}

	@Override
	public String cooldownTag() {
		return "";
	}

	@Override
	public int cooldown() {
		return 0;
	}
}
