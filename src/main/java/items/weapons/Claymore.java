package items.weapons;

import items.AbilityItem;
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

public class Claymore implements AbilityItem {
	public static ItemStack getItem(int sharpnessLevel) {

		ItemStack claymore = new ItemStack(Material.STONE_SWORD);

		ItemMeta data = claymore.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Dark Claymore");
		AttributeModifier attackSpeed = new AttributeModifier(UUID.fromString("e51c72e7-2b0d-4064-8d7f-89133cfd4b8b"), "claymoreModifier", 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		AttributeModifier attackDamage = new AttributeModifier(UUID.fromString("25cc1a90-327d-4115-a912-869e883862f4"), "claymoreModifierDmg", 9, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		AttributeModifier attackRange = new AttributeModifier(UUID.fromString("95037a43-49c4-4d9e-93a7-49c50c59e811"), "claymoreModifierRange", 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.ATTACK_SPEED, attackSpeed);
		data.addAttributeModifier(Attribute.ENTITY_INTERACTION_RANGE, attackRange);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		String loreDamage;
		switch(sharpnessLevel) {
			case 1 -> loreDamage = "10";
			case 2 -> loreDamage = "10.5";
			case 3 -> loreDamage = "11";
			case 4 -> loreDamage = "11.5";
			case 5 -> loreDamage = "12";
			case 6 -> loreDamage = "12.5";
			case 7 -> loreDamage = "13";
			default -> loreDamage = "9";
		}

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/dark_claymore");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+" + loreDamage);
		lore.add(ChatColor.GRAY + "Swing Range: " + ChatColor.RED + "+2");
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
