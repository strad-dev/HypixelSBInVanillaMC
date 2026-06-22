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

public class PrimalDragonChestplate {
	public static ItemStack getItem() {
		ItemStack primalChestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);

		ItemMeta data = primalChestplate.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<light_purple>Primal Dragon Chestplate"));
		AttributeModifier damage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "primalChestplateDamage"), 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
		AttributeModifier armor = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "primalChestplateArmor"), 10.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
		AttributeModifier antiKB = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "primalChestplateAntiKB"), 0.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.ARMOR, armor);
		data.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/primal_chestplate"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>+2"));
		lore.add(Utils.mm("<gray>Armor: <red>+10.5"));
		lore.add(Utils.mm("<gray>Knockback Resistance: <red>+20%"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>A powerful chestplate forged from"));
		lore.add(Utils.mm("<gray><italic>the remnants of the Primal Dragon."));
		lore.add(Utils.mm("<gray><italic>It's sturdy, but makes you too heavy to fly."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<light_purple><bold><obfuscated>a</obfuscated> MYTHIC CHESTPLATE <obfuscated>a</obfuscated>"));

		data.lore(lore);
		primalChestplate.setItemMeta(data);

		return primalChestplate;
	}
}
