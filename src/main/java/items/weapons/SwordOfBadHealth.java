package items.weapons;

import items.AbilityItem;
import listeners.CustomDamage;
import listeners.DamageType;
import misc.Plugin;
import misc.SkyblockId;
import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import java.util.Map;

public class SwordOfBadHealth implements AbilityItem {
	private static final String COOLDOWN_TAG = "BadHealthCooldown";
	private static final int COOLDOWN = 100;

	/** This weapon's own attack damage, before any enchantment. Quoted on the lore line. */
	private static final double BASE_DAMAGE = 1;

	public static ItemStack getItem() {
		return getItem(Map.of());
	}

	/**
	 * The item, carrying {@code enchants} and with lore that says so. <b>The enchantments go on here rather
	 * than being applied by the caller afterwards</b> - that was the desync: the caller built the item, got
	 * lore for whatever it named, and then enchanted the stack by material type.
	 */
	public static ItemStack getItem(Map<Enchantment, Integer> enchants) {
		ItemStack swordOfBadHealth = new ItemStack(Material.WOODEN_SWORD);

		ItemMeta data = swordOfBadHealth.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<blue>Sword of Bad Health"));
		AttributeModifier attackSpeed = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "badHealthModifier"), 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "badHealthModifierDmg"), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.ATTACK_SPEED, attackSpeed);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/sword_of_bad_health"));
		lore.add(Utils.mm(""));
		lore.add(Utils.damageLore(BASE_DAMAGE, enchants));
		lore.addAll(Utils.bonusDamageLore(enchants));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold>Ability: Bad Health <green><bold>RIGHT CLICK"));
		lore.add(Utils.mm("<gray>Use <red>10%<gray> of your max health"));
		lore.add(Utils.mm("<gray>to gain <red>+10% Damage<gray> for <green>5s<gray>."));
		lore.add(Utils.mm("<dark_gray>Health Cost: <red>2"));
		lore.add(Utils.mm("<dark_gray>Cooldown: <green>" + COOLDOWN / 20 + "s"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<blue><bold><obfuscated>a</obfuscated> RARE SWORD <obfuscated>a</obfuscated>"));

		data.lore(lore);
		swordOfBadHealth.setItemMeta(data);
		swordOfBadHealth.addUnsafeEnchantments(enchants);

		return SkyblockId.stamp(swordOfBadHealth);
	}

	@Override
	public boolean hasLeftClickAbility() {
		return false;
	}

	@Override
	public boolean onRightClick(Player p) {
		if(p.getHealth() > 2) {
			CustomDamage.calculateFinalDamage(p, p, 2, DamageType.ABSOLUTE);
			p.addScoreboardTag("BadHealthBuffed");
			Utils.scheduleTask(() -> p.removeScoreboardTag("BadHealthBuffed"), 100);
			p.playSound(p, Sound.ENTITY_GENERIC_EAT, 2.0F, 1.0F);
			p.sendMessage(Utils.msg("<red>Ouch!  That hurt!  But you have buffed your damage by 10% for 5 seconds!"));
			return true;
		} else {
			p.sendMessage(Utils.msg("<red>You do not have enough Health to use this ability!"));
			return false;
		}
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
