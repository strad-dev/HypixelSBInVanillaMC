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

public class NecronElytra implements Armor {
	public static ItemStack getItem() {
		ItemStack necronElytra = new ItemStack(Material.ELYTRA);

		ItemMeta data = necronElytra.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<light_purple>Necron's Elytra"));
		AttributeModifier damage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "necronElytraDamage"), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
		AttributeModifier armor = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "necronElytraArmor"), 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
		AttributeModifier antiKB = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "necronElytraAntiKB"), 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.ARMOR, armor);
		data.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/necron_elytra"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>+1"));
		lore.add(Utils.mm("<gray>Armor: <red>+10"));
		lore.add(Utils.mm("<gray>Knockback Resistance: <red>+10%"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>This Elytra has the stats of a Netherite"));
		lore.add(Utils.mm("<gray><italic>Chestplate, while still allowing you to fly!"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<light_purple><bold><obfuscated>a</obfuscated> MYTHIC CHESTPLATE <obfuscated>a</obfuscated>"));

		data.lore(lore);
		necronElytra.setItemMeta(data);

		return necronElytra;
	}
}
