package items.misc;

import items.CustomItem;
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

public class DivanPickaxe implements CustomItem {
	public static ItemStack getItem() {
		ItemStack divanPickaxe = new ItemStack(Material.NETHERITE_PICKAXE);

		ItemMeta data = divanPickaxe.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<aqua>Divan's Pickaxe"));
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "DivanModifier"), -1000, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		AttributeModifier miningSpeed = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "DivanSpeed"), 0.333, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.MAINHAND);
		AttributeModifier miningReach = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "DivanReach"), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.BLOCK_BREAK_SPEED, miningSpeed);
		data.addAttributeModifier(Attribute.BLOCK_INTERACTION_RANGE, miningReach);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/divan_pickaxe"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>0"));
		lore.add(Utils.mm("<gray>Mining Speed: <red>x1.33"));
		lore.add(Utils.mm("<gray>Range: <red>+1"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold>Passive Ability: Double Drops"));
		lore.add(Utils.mm("<gray>Grants <red>x2<gray> drops from"));
		lore.add(Utils.mm("<gray>every ore mined."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray><italic>An extremely powerful"));
		lore.add(Utils.mm("<gray><italic>pickaxe forged with the"));
		lore.add(Utils.mm("<gray><italic>rarest materials in existance!"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<aqua><bold><obfuscated>a</obfuscated> DIVINE PICKAXE <obfuscated>a</obfuscated>"));

		data.lore(lore);
		divanPickaxe.setItemMeta(data);

		return divanPickaxe;
	}
}