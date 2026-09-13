package items.weapons;

import items.CustomItem;
import misc.Plugin;
import misc.Utils;
import net.kyori.adventure.text.Component;
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
import java.util.Map;

public class Claymore implements CustomItem {
	/** This weapon's own attack damage, before any enchantment. Quoted on the lore line. */
	private static final double BASE_DAMAGE = 9;

	public static ItemStack getItem() {
		return getItem(Map.of());
	}

	/**
	 * The item, carrying {@code enchants} and with lore that says so. <b>The enchantments go on here rather
	 * than being applied by the caller afterwards</b> - that was the desync: the caller built the item, got
	 * lore for whatever it named, and then enchanted the stack by material type.
	 */
	public static ItemStack getItem(Map<Enchantment, Integer> enchants) {
		ItemStack claymore = new ItemStack(Material.STONE_SWORD);

		ItemMeta data = claymore.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<light_purple>Dark Claymore"));
		AttributeModifier attackSpeed = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "claymoreModifier"), 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "claymoreModifierDmg"), 9, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		AttributeModifier attackRange = new AttributeModifier(new NamespacedKey(Plugin.getInstance(),  "claymoreModifierRange"), 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.ATTACK_SPEED, attackSpeed);
		data.addAttributeModifier(Attribute.ENTITY_INTERACTION_RANGE, attackRange);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/dark_claymore"));
		lore.add(Utils.mm(""));
		lore.add(Utils.damageLore(BASE_DAMAGE, enchants));
		lore.add(Utils.mm("<gray>Swing Range: <red>+2"));
		lore.addAll(Utils.bonusDamageLore(enchants));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>That thing was too big to be"));
		lore.add(Utils.mm("<gray><italic>called a sword, it was more like"));
		lore.add(Utils.mm("<gray><italic>a large hunk of stone."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<light_purple><bold><obfuscated>a</obfuscated> MYTHIC SWORD <obfuscated>a</obfuscated>"));

		data.lore(lore);
		claymore.setItemMeta(data);
		claymore.addUnsafeEnchantments(enchants);

		return claymore;
	}
}
