package items.weapons;

import items.AbilityItem;
import misc.SkyblockId;
import misc.Utils;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static misc.Utils.shootBeam;

public class Terminator implements AbilityItem {
	private static final String COOLDOWN_TAG = "SalvationCooldown";
	private static final int COOLDOWN = 16;
	private static final String SHOT_COOLDOWN_TAG = "TerminatorShotCooldown";
	private static final int SHOT_COOLDOWN = 3;
	/** Extra foes an arrow passes through; lore quotes {@code PIERCE + 1}, the number it actually hits. */
	private static final int PIERCE = 4;

	/** Vanilla bow is 1, other high-end items 30. See {@link misc.Utils#setEnchantability}. */
	private static final int ENCHANTABILITY = 20;

	/** Before Power. */
	private static final double ARROW_BASE = 2.5;
	/** Before Power. */
	private static final double SALVATION_BASE = 4;

	/**
	 * Before Strength. Lore and {@link #onRightClick} both read this; written twice, Salvation drifted (lore 4,
	 * beam 4.5) and the beam came down to the lore. Level 7 rounds to +2, not 1.75, like melee enchants.
	 */
	public static double arrowDamage(int power) {
		if(power <= 0) return ARROW_BASE;
		return ARROW_BASE + (power == 7 ? 2.0 : power * 0.25);
	}

	/** Before Strength. See {@link #arrowDamage}. */
	public static double salvationDamage(int power) {
		if(power <= 0) return SALVATION_BASE;
		return SALVATION_BASE + (power == 7 ? 4.0 : power * 0.5);
	}

	public static ItemStack getItem() {
		return getItem(Map.of());
	}

	/** Enchants go on here, not by the caller after; that was the lore desync. */
	public static ItemStack getItem(Map<Enchantment, Integer> enchants) {
		int powerLevel = enchants == null ? 0 : enchants.getOrDefault(Enchantment.POWER, 0);
		ItemStack term = new ItemStack(Material.BOW);

		ItemMeta data = term.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<light_purple>Terminator"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

		String loreDamage = Utils.damageNumber(arrowDamage(powerLevel));
		String salvationDamage = Utils.damageNumber(salvationDamage(powerLevel));

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/terminator"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>+" + loreDamage));
		// +1: spamming can fire at 3 ticks, but holding right click lands at 4, which is what most get.
		lore.add(Utils.mm("<gray>Shot Cooldown: <green>" + Utils.damageNumber((SHOT_COOLDOWN + 1) / 20.0) + "s"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold>Shortbow: Instantly Shoots!"));
		lore.add(Utils.mm("<gray>Shoots <aqua>3<gray> arrows at once."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold>Ability: Salvation <green><bold>LEFT CLICK"));
		lore.add(Utils.mm("<gray>Shoot a beam, penetrating up to"));
		lore.add(Utils.mm("<yellow>" + (PIERCE + 1) + "<gray> foes and dealing <red>" + salvationDamage));
		lore.add(Utils.mm("<gray>damage to each enemy."));
		lore.add(Utils.mm("<gray>Cooldown: <green>" + Utils.damageNumber(COOLDOWN / 20.0) + "s"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<light_purple><bold><obfuscated>a</obfuscated> MYTHIC BOW <obfuscated>a</obfuscated>"));

		data.lore(lore);
		term.setItemMeta(data);
		term.addUnsafeEnchantments(enchants);
		// Under 30 on purpose: 20 puts Power V (modified level 41) in reach of a level-30 table without making it
		// near-certain.
		Utils.setEnchantability(term, ENCHANTABILITY);

		return SkyblockId.stamp(term);
	}

	@Override
	public boolean hasLeftClickAbility() {
		return true;
	}

	/** Salvation is left click with a 0.8s cooldown. See {@link AbilityItem#quietCooldown}. */
	@Override
	public boolean quietCooldown() {
		return true;
	}

	@Override
	public boolean onRightClick(Player p) {
		// Once per 3 ticks. Own tag, not the dispatcher's cooldown, so shooting never puts Salvation on cooldown.
		if(p.getScoreboardTags().contains(SHOT_COOLDOWN_TAG)) {
			return false;
		}
		p.addScoreboardTag(SHOT_COOLDOWN_TAG);
		Utils.scheduleTask(() -> p.removeScoreboardTag(SHOT_COOLDOWN_TAG), SHOT_COOLDOWN);

		// you don't need arrows
		p.getInventory().remove(Material.ARROW);
		p.getInventory().remove(Material.TIPPED_ARROW);
		p.getInventory().remove(Material.SPECTRAL_ARROW);

		// Get NMS world and player
		ServerLevel nmsWorld = ((CraftWorld) p.getWorld()).getHandle();
		ServerPlayer nmsPlayer = ((CraftPlayer) p).getHandle();

		// Calculate directions
		Vector baseDirection = p.getEyeLocation().getDirection().normalize();
		Vector leftDirection = baseDirection.clone().rotateAroundY(Math.toRadians(-5));
		Vector rightDirection = baseDirection.clone().rotateAroundY(Math.toRadians(5));

		// Calculate spawn position
		Location spawnLoc = p.getEyeLocation().add(baseDirection.clone());

		// 26.2 removed EntityType.ARROW, so use the position+item constructor with a null weapon.
		// TerminatorArrow so our cancelled damage event can't deflect them.
		TerminatorArrow nmsLeft = new TerminatorArrow(nmsWorld, 0, 0, 0, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ARROW), null);
		TerminatorArrow nmsMiddle = new TerminatorArrow(nmsWorld, 0, 0, 0, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ARROW), null);
		TerminatorArrow nmsRight = new TerminatorArrow(nmsWorld, 0, 0, 0, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ARROW), null);

		// Set positions
		nmsLeft.setPos(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());
		nmsMiddle.setPos(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());
		nmsRight.setPos(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());

		// shoot() sets velocity AND derives rotation from the direction vector
		float speed = 3.175f;
		nmsLeft.shoot(leftDirection.getX(), leftDirection.getY(), leftDirection.getZ(), speed, 0);
		nmsMiddle.shoot(baseDirection.getX(), baseDirection.getY(), baseDirection.getZ(), speed, 0);
		nmsRight.shoot(rightDirection.getX(), rightDirection.getY(), rightDirection.getZ(), speed, 0);

		// Set other properties
		nmsLeft.setOwner(nmsPlayer);
		nmsMiddle.setOwner(nmsPlayer);
		nmsRight.setOwner(nmsPlayer);

		// Add to world
		nmsWorld.addFreshEntity(nmsLeft);
		nmsWorld.addFreshEntity(nmsMiddle);
		nmsWorld.addFreshEntity(nmsRight);

		// Get Bukkit wrappers for further modification
		Arrow left = (Arrow) nmsLeft.getBukkitEntity();
		Arrow middle = (Arrow) nmsMiddle.getBukkitEntity();
		Arrow right = (Arrow) nmsRight.getBukkitEntity();

		double damage = arrowDamage(p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.POWER));

		double strengthBonus;
		try {
			strengthBonus = 0.75 + 0.75 * p.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier();
		} catch(Exception exception) {
			strengthBonus = 0;
		}

		// Set Bukkit properties
		for(Arrow arrow : Arrays.asList(left, middle, right)) {
			arrow.setDamage(damage + strengthBonus);
			arrow.setPierceLevel(PIERCE);
			arrow.setShooter(p);
			arrow.setWeapon(p.getInventory().getItemInMainHand());
			arrow.addScoreboardTag("TerminatorArrow");
		}

		p.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);

		return false;
	}

	@Override
	public boolean onLeftClick(Player p) {
		// Main hand. getItem(getHeldItemSlot()) could return null, and its try/catch fired an unenchanted beam.
		double damage = salvationDamage(p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.POWER));

		double strengthBonus;
		try {
			strengthBonus = 1 + p.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier();
		} catch(Exception exception) {
			strengthBonus = 0;
		}

		shootBeam(p, p, Color.RED, 64, 5, damage + strengthBonus);
		p.playSound(p.getLocation(), Sound.ENTITY_GUARDIAN_DEATH, 1.0F, 2.0F);
		return true;
	}

	@Override
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