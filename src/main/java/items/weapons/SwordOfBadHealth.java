package items.weapons;

import items.AbilityItem;
import listeners.CustomDamage;
import listeners.DamageType;
import misc.Plugin;
import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SwordOfBadHealth implements AbilityItem {
	private static final String COOLDOWN_TAG = "BadHealthCooldown";
	private static final int COOLDOWN = 100;

	public static ItemStack getItem(Enchantment ench, int enchLevel) {
		ItemStack swordOfBadHealth = new ItemStack(Material.WOODEN_SWORD);

		ItemMeta data = swordOfBadHealth.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<blue>Sword of Bad Health"));
		AttributeModifier attackSpeed = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "badHealthModifier"), 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "badHealthModifierDmg"), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.ATTACK_SPEED, attackSpeed);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		String loreDamage = "1";
		if(ench.equals(Enchantment.SHARPNESS)) {
			loreDamage = String.valueOf(1 + enchLevel);
		}

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/dark_claymore"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>+" + loreDamage));
		if(ench.equals(Enchantment.SMITE) || ench.equals(Enchantment.BANE_OF_ARTHROPODS)) {
			lore.add(Utils.mm(""));
			loreDamage = String.valueOf(enchLevel * 2);
			if(ench.equals(Enchantment.SMITE)) {
				lore.add(Utils.mm("<gray>Bonus Undead Damage: <red>+" + loreDamage));
			} else {
				lore.add(Utils.mm("<gray>Bonus Arthropod Damage: <red>+" + loreDamage));
			}
		}
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold>Ability: Bad Health <green><bold>RIGHT CLICK"));
		lore.add(Utils.mm("<gray>Use <red>5%<gray> of your max health"));
		lore.add(Utils.mm("<gray>to gain <red>+10% Damage<gray> for <green>5s<gray>."));
		lore.add(Utils.mm("<dark_gray>Health Cost: <red>1"));
		lore.add(Utils.mm("<dark_gray>Cooldown: <green>" + COOLDOWN / 20 + "s"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<blue><bold><obfuscated>a</obfuscated> RARE SWORD <obfuscated>a</obfuscated>"));

		data.lore(lore);
		swordOfBadHealth.setItemMeta(data);

		return swordOfBadHealth;
	}

	@Override
	public boolean hasLeftClickAbility() {
		return false;
	}

	@Override
	public boolean onRightClick(Player p) {
		if(p.getHealth() > 1) {
			CustomDamage.calculateFinalDamage(p, p, 1, DamageType.ABSOLUTE);
			p.addScoreboardTag("BadHealthBuffed");
			Utils.scheduleTask(() -> p.removeScoreboardTag("BadHealthBuffed"), 100);
			p.playSound(p, Sound.ENTITY_GENERIC_EAT, 2.0F, 1.0F);
			p.sendMessage("<red>Ouch!  That hurt!  But you have buffed your damage by 10% for 5 seconds!");
		} else {
			p.sendMessage("<red>You do not have enough Health to use this ability!");
		}
		return true;
	}

	@Override
	public boolean onLeftClick(Player p) {
		return false;
	}

	public int manaCost() {
		return 0;
	}

	@Override
	public String cooldownTag() {
		return COOLDOWN_TAG;
	}

	@Override
	public int cooldown() {
		return COOLDOWN;
	}
}
