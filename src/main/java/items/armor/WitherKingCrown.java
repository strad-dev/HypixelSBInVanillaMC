package items.armor;

import misc.Plugin;
import misc.Utils;
import net.kyori.adventure.text.Component;
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
		data.displayName(Utils.mm("<light_purple>Crown of the Wither King"));
		AttributeModifier damage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "crownDamage"), 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
		AttributeModifier armor = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "crownArmor"), 4.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
		AttributeModifier antiKB = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "crownAntiKB"), 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.ARMOR, armor);
		data.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/wither_king_crown"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>+2"));
		lore.add(Utils.mm("<gray>Armor: <red>+4.5"));
		lore.add(Utils.mm("<gray>Knockback Resistance: <red>+10%"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>The Wither King left behind this"));
		lore.add(Utils.mm("<gray><italic>crown after His unfortunate demise."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<light_purple><bold><obfuscated>a</obfuscated> MYTHIC HELMET <obfuscated>a</obfuscated>"));

		data.lore(lore);
		crown.setItemMeta(data);

		return crown;
	}
}
