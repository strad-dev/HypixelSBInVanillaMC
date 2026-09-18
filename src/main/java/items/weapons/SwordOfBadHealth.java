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
	/** What the ability costs and what it buys. The cost is a SHARE of max health, not a flat figure, so
	 *  it keeps hurting a player whose health pool has grown. <b>{@code DAMAGE_BONUS} is read by
	 *  {@link CustomDamage#calculateFinalDamage} and all three are quoted by the lore.</b> */
	public static final double HEALTH_SHARE = 0.10;
	public static final double DAMAGE_BONUS = 1.1;
	public static final long BUFF_TICKS = 100L;

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
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "badHealthModifierDmg"), BASE_DAMAGE, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.ATTACK_SPEED, attackSpeed);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/sword_of_bad_health"));
		lore.add(Utils.mm(""));
		lore.addAll(Utils.statLore(data, enchants));
		lore.addAll(Utils.bonusDamageLore(enchants));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold>Ability: Bad Health <green><bold>RIGHT CLICK"));
		lore.add(Utils.mm("<gray>Use <red>" + Utils.percent(HEALTH_SHARE) + "<gray> of your max health"));
		lore.add(Utils.mm("<gray>to gain <red>+" + Utils.percent(DAMAGE_BONUS - 1)
				+ " Damage<gray> for <green>" + BUFF_TICKS / 20 + "s<gray>."));
		lore.add(Utils.mm("<dark_gray>Health Cost: <red>" + Utils.percent(HEALTH_SHARE) + " of max"));
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
		// A SHARE of max health, not the flat 2 this used to take: the lore always claimed 10%, and a flat
		// 2 was only ever that for a player on the vanilla 20.  Absolute damage, so armour cannot soak it.
		double cost = p.getAttribute(Attribute.MAX_HEALTH).getValue() * HEALTH_SHARE;
		if(p.getHealth() > cost) {
			CustomDamage.calculateFinalDamage(p, p, cost, DamageType.ABSOLUTE);
			p.addScoreboardTag("BadHealthBuffed");
			Utils.scheduleTask(() -> p.removeScoreboardTag("BadHealthBuffed"), BUFF_TICKS);
			p.playSound(p, Sound.ENTITY_GENERIC_EAT, 2.0F, 1.0F);
			p.sendMessage(Utils.msg("<red>Ouch!  That hurt!  But you have buffed your damage by "
					+ Utils.percent(DAMAGE_BONUS - 1) + " for " + BUFF_TICKS / 20 + " seconds!"));
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
