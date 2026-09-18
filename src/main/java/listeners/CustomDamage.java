package listeners;

import items.misc.HolyIce;
import items.misc.IceSpray;
import items.weapons.Scylla;
import items.weapons.SwordOfBadHealth;
import misc.DamageData;
import misc.Plugin;
import misc.Utils;
import mobs.CustomMob;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonDeathPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.*;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import com.destroystokyo.paper.event.player.PlayerAttackEntityCooldownResetEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Filter;
import java.util.logging.Logger;

public class CustomDamage implements Listener {

	static net.kyori.adventure.text.Component nextDeathMessage = null;

	// Last server tick each entity took Terminator-arrow knockback, so a same-tick volley of the 3
	// arrows accumulates horizontally instead of each setVelocity overwriting the last.
	private static final Map<Entity, Integer> lastTermKnockbackTick = new WeakHashMap<>();

	// Stamped on an Ender Dragon the moment its death branch runs, and the one way to tell a corpse from a live
	// dragon: vanilla's own death animation runs at 1 HP (EnderDragon.handleKillingBlow pins it there and hands
	// the phase to DYING), so isDead() and getHealth() both read as alive for the whole 200-tick animation.
	public static final String DYING_DRAGON_TAG = "DyingDragon";

	/** True while an Ender Dragon is playing its death animation, i.e. it is a corpse and must take no damage. */
	public static boolean isDyingDragon(Entity e) {
		return e instanceof EnderDragon && e.getScoreboardTags().contains(DYING_DRAGON_TAG);
	}

	public static void customMobs(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		customMobs(damagee, damager, originalDamage, type, new DamageData(damagee, damager, originalDamage));
	}

	// ================================ melee crits and weapon enchantments ================================
	//
	// Vanilla's critical hit is worth very little here and is unavailable exactly when a player is fighting.
	// Two things about it:
	//
	//   * it multiplies only the ATTRIBUTE damage.  The enchantment bonus is added afterwards, untouched
	//     (Player.attack: `f *= 1.5f` and only then `f + g`), so a Sharpness VII Claymore's crit is worth
	//     1.5x of 10 and a flat +7 on top rather than 1.5x of 17.
	//   * Player.canCriticalAttack ends with `&& !isSprinting()`, so you cannot crit while sprinting, which
	//     in practice means you cannot crit while chasing anyone.
	//
	// So the crit is decided and applied HERE instead: the same conditions minus the sprint clause, and a
	// 1.5x over the WHOLE blow, enchantments included.  That is a large buff to enchanted weapons, so
	// Sharpness and Smite/Bane are retuned downwards below to pay for it.  The whole exchange is meant to
	// make the jump-crit worth doing rather than make gear hit harder.

	/** Sharpness, per level, replacing vanilla's {@code 0.5 * level + 0.5}.  Level 7 is the one the duel kit
	 *  and the palette hand out, so it gets a round 5.5 rather than 5.25. */
	public static double sharpnessBonus(int level) {
		if(level <= 0) return 0;
		return level == 7 ? 5.5 : level * 0.75;
	}

	/** Smite / Bane of Arthropods against a target of the type they apply to, replacing vanilla's
	 *  {@code 2.5 * level}.  Level 7 gets 11 rather than 10.5, for the same reason as Sharpness. */
	public static double smiteBonus(int level) {
		if(level <= 0) return 0;
		return level == 7 ? 11 : level * 1.5;
	}

	/**
	 * What Strength has to give back. Vanilla's is <b>+3 melee damage per level</b> (an {@code effect.strength}
	 * {@code ADD_VALUE 3.0} modifier on ATTACK_DAMAGE, read off the 26.2 jar); this plugin pays <b>+2</b>, so
	 * one point per level comes off every melee blow.
	 *
	 * <p>It rides on the attribute, so it is already inside anything that reads ATTACK_DAMAGE - vanilla's own
	 * damage number, and {@code Utils.meleeDamageWith}. Both take this off rather than trying to keep the
	 * modifier itself out, which is not ours to change: vanilla applies it transiently when the effect lands.
	 */
	public static double strengthPenalty(LivingEntity attacker) {
		PotionEffect strength = attacker.getPotionEffect(PotionEffectType.STRENGTH);
		return strength == null ? 0 : strength.getAmplifier() + 1;
	}

	/** What vanilla would have given, which is what has to come back off before ours goes on. */
	private static double vanillaSharpnessBonus(int level) {
		return level <= 0 ? 0 : level * 0.5 + 0.5;
	}

	/** @see #vanillaSharpnessBonus */
	private static double vanillaSmiteBonus(int level) {
		return level <= 0 ? 0 : level * 2.5;
	}

	// ================================ the swing's charge ================================
	//
	// Paper zeroes the attack-strength ticker INSIDE Player.attack, before the blow lands: onAttack(target)
	// fires PlayerAttackEntityCooldownResetEvent and then calls resetOnlyAttackStrengthTicker(), and only
	// after that does hurtOrSimulate fire the damage event this class listens to.  Vanilla does not care -
	// Player.attack read getAttackStrengthScale ONCE at the top and kept it in a local, and both its crit
	// verdict and its enchantment bonus ride on that copy - but anything re-reading the LIVE ticker later
	// gets 0.5/delay instead of what vanilla judged: 0.025 on a 20-tick weapon where vanilla had 1.
	//
	// It hid behind the plugin's own weapons, which all carry ATTACK_SPEED +100: that puts full charge at
	// about 0.19 ticks, so even a zeroed ticker still clamps to 1 and they behaved.  Every VANILLA weapon
	// silently lost both its crit (no 1.5x, no particles, no sound) and its whole Sharpness retune, since
	// rebuildMelee scales our enchantment delta by the same number.
	//
	// So the charge is taken from the event that fires immediately before the reset, for this exact attack,
	// and canCrit and rebuildMelee read it from there rather than off the ticker.

	/** One swing's charge: the tick it was taken on, and the scale vanilla judged that swing by. */
	private record SwingCharge(int tick, float scale) {}

	private static final Map<Player, SwingCharge> swingCharges = new WeakHashMap<>();

	/**
	 * Snapshot the charge while it is still there.  Paper fires this from {@code Player.onAttack}, one call
	 * before it zeroes the ticker and two before our damage event, so the live read here is still the number
	 * {@code Player.attack} cached for itself.  Read at {@code 0.5f} rather than taken off the event, whose
	 * own figure is {@code getAttackStrengthScale(0.0f)} - half a tick short of vanilla's.
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onAttackCooldownReset(PlayerAttackEntityCooldownResetEvent e) {
		ServerPlayer sp = ((CraftPlayer) e.getPlayer()).getHandle();
		swingCharges.put(e.getPlayer(), new SwingCharge(Bukkit.getCurrentTick(), sp.getAttackStrengthScale(0.5f)));
	}

	/**
	 * The charge vanilla judged this swing by - {@link #onAttackCooldownReset}'s snapshot when it is from
	 * this tick, the live ticker otherwise.
	 *
	 * <p>The fallback is for a blow that never went through {@code Player.attack} at all (a boss calling
	 * into the pipeline, {@code /damage}), where nothing has been reset and the live value is correct.  An
	 * older snapshot is refused rather than trusted: it describes a different swing.
	 */
	private static float attackCharge(Player p) {
		SwingCharge charge = swingCharges.get(p);
		if(charge != null && charge.tick() == Bukkit.getCurrentTick()) return charge.scale();
		return ((CraftPlayer) p).getHandle().getAttackStrengthScale(0.5f);
	}

	/**
	 * Can this player land a critical hit on that target?  {@code Player.canCriticalAttack} copied out of the
	 * 26.2 jar with {@code !isSprinting()} dropped - the rule change, so a crit is available while chasing
	 * someone - and {@code !onGround()} dropped as redundant, plus the attack-cooldown gate that lives in
	 * {@code Player.attack} beside the call rather than inside it ({@code getAttackStrengthScale > 0.9}).
	 *
	 * <p>The cooldown comes from {@link #attackCharge}, not from the live ticker - Paper has already zeroed
	 * that by the time this runs, so reading it here refused every crit a vanilla weapon earned.
	 */
	public static boolean canCrit(Player p, Entity target) {
		if(!(target instanceof LivingEntity)) return false;
		ServerPlayer sp = ((CraftPlayer) p).getHandle();
		return attackCharge(p) > 0.9f
				// Falling ALREADY means airborne, so vanilla's companion !onGround() clause is dropped:
				// fallDistance only grows while y-velocity is negative, and Entity.checkFallDamage ends its
				// onGround branch with an unconditional resetFallDistance(), so on the ground it is 0.
				// What this really asks is that you connect on the way DOWN - the rising half of a jump
				// does not crit, which is the vanilla jump-crit and is deliberate.
				&& p.getFallDistance() > 0
				&& !sp.onClimbable()
				&& !sp.isInWater()
				&& !sp.isMobilityRestricted()
				&& !sp.isPassenger()
				&& critsEnabled(p.getWorld());
	}

	/**
	 * Half of vanilla's {@code horizontal_blocking_angle}: the shield's cone reaches this many degrees to
	 * either side of where the defender is looking, so 90 is the 180-degree arc in front. It is the default
	 * on vanilla's {@code minecraft:blocks_attacks} component, and it is read per-item there - we apply the
	 * one number to every shield rather than reading the component, since nothing here varies it.
	 */
	private static final double SHIELD_BLOCKING_ANGLE = 90;

	/**
	 * Did this blow come from inside the defender's shield cone?  <b>The rule our pipeline was missing
	 * entirely</b>: a shield used to reduce damage from any direction, back included, because cancelling the
	 * vanilla damage event skips {@code LivingEntity.applyItemBlocking} where the test lives.
	 *
	 * <p>Copied from the 26.2 jar.  Vanilla builds a HORIZONTAL look vector -
	 * {@code calculateViewVector(0.0F, getYHeadRot())}, so pitch is ignored and looking at your feet does not
	 * drop your guard - flattens the vector to the attacker the same way, and refuses the block when
	 * {@code acos(dot)} exceeds the cone ({@code DamageReduction.resolve} returns 0 above it).
	 *
	 * <p>Two deliberate departures.  Vanilla uses {@code acos} on the dot product and compares radians; a
	 * 90-degree cone is exactly {@code dot >= 0}, but the {@code acos} is kept so the angle stays a readable
	 * number to change.  And where vanilla treats a source with NO position as angle PI (outside every cone,
	 * so no block at all), we keep the block: a missing position here means our own pipeline lost the
	 * attacker, not that the damage genuinely came from nowhere, and the reduction should only be taken away
	 * when we positively know the blow came from behind.
	 */
	private static boolean blockedFromFront(LivingEntity damagee, Location source) {
		if(source == null || !source.getWorld().equals(damagee.getWorld())) return true;

		double yaw = Math.toRadians(damagee.getLocation().getYaw());
		Vector look = new Vector(-Math.sin(yaw), 0, Math.cos(yaw));

		Vector toSource = source.toVector().subtract(damagee.getLocation().toVector()).setY(0);
		// Vanilla's Vec3.normalize returns ZERO below 1e-4, making the dot 0 and the angle exactly 90 - which
		// its own `angle > cone` test then lets through.  Standing in the defender's own column blocks.
		if(toSource.lengthSquared() < 1.0E-8) return true;

		double dot = Math.max(-1, Math.min(1, toSource.normalize().dot(look)));
		return Math.acos(dot) <= Math.toRadians(SHIELD_BLOCKING_ANGLE);
	}

	/** Paper can switch player crits off per world.  If it ever is, vanilla did not crit either, so both the
	 *  strip in {@link #rebuildMelee} and our own multiplier have to go quiet with it. */
	private static boolean critsEnabled(World world) {
		return !((CraftWorld) world).getHandle().paperConfig().entities.behavior.disablePlayerCrits;
	}

	/**
	 * Rebuild one melee blow.  Takes vanilla's number back apart - its enchantment bonus off, its own crit
	 * off - puts our enchantment values on instead, and then multiplies the WHOLE thing by the crit.
	 *
	 * <p>It unwinds rather than recomputing from the attribute, so everything else vanilla folded in stays
	 * in: the mace's smash bonus, the attack-cooldown scaling, and the boss difficulty normalisation applied
	 * to {@code e.getDamage()} a few lines above the call.
	 *
	 * <p>The enchantment bonus is asked of {@code EnchantmentHelper}, the same helper vanilla used, so an
	 * enchantment we have not modelled (Impaling on a trident, anything a datapack adds) survives the strip
	 * as itself instead of being silently dropped.  Only the three levels below are swapped out.
	 *
	 * <p><b>Mobs take the enchantment retune and nothing else.</b>  They cannot crit, and their damage does
	 * not always come from a vanilla swing, so the delta is added the way the old Sharpness patch did rather
	 * than the whole number being taken apart.
	 */
	private static double rebuildMelee(LivingEntity attacker, LivingEntity target, double vanillaDamage, boolean crit) {
		ItemStack weapon = attacker.getEquipment() == null ? null : attacker.getEquipment().getItemInMainHand();
		if(weapon == null) return vanillaDamage;

		int sharpness = weapon.getEnchantmentLevel(Enchantment.SHARPNESS);
		int smite = weapon.getEnchantmentLevel(Enchantment.SMITE);
		int bane = weapon.getEnchantmentLevel(Enchantment.BANE_OF_ARTHROPODS);
		net.minecraft.world.entity.Entity nmsTarget = ((CraftEntity) target).getHandle();
		// Smite and Bane only ever applied if the target is of the type they are for, so the delta is zero
		// otherwise - asked of the same tags the enchantments' own conditions use.
		if(!nmsTarget.is(net.minecraft.tags.EntityTypeTags.SENSITIVE_TO_SMITE)) smite = 0;
		if(!nmsTarget.is(net.minecraft.tags.EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS)) bane = 0;

		double delta = (sharpnessBonus(sharpness) - vanillaSharpnessBonus(sharpness))
				+ (smiteBonus(smite) - vanillaSmiteBonus(smite))
				+ (smiteBonus(bane) - vanillaSmiteBonus(bane));

		if(!(attacker instanceof Player p)) {
			// A mob's blow is not always a charged swing, so its Strength comes off unscaled.
			return Math.max(0, vanillaDamage + delta - strengthPenalty(attacker));
		}

		ServerPlayer sp = ((CraftPlayer) p).getHandle();
		ServerLevel level = ((CraftWorld) p.getWorld()).getHandle();
		// The enchantment bonus is scaled by the swing's charge and the base damage by its square; the crit
		// multiplies neither of the two, it multiplies what they add up to.
		float scale = attackCharge(p);
		float attributeDamage = (float) sp.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
		DamageSource source = level.damageSources().playerAttack(sp);
		double vanillaEnchant = scale * (net.minecraft.world.item.enchantment.EnchantmentHelper.modifyDamage(
				level, sp.getWeaponItem(), nmsTarget, source, attributeDamage) - attributeDamage);

		// 1. the blow with no enchantments and no crit on it.  Vanilla's own crit is ours minus the sprint
		// clause, so the caller's answer settles it and the conditions are only evaluated once.
		double base = vanillaDamage - vanillaEnchant;
		if(crit && !p.isSprinting()) {
			base /= 1.5; // vanilla's crit, which it applied to this part alone
		}

		// Strength down from +3 to +2 per level.  It sits in the attribute, so it is inside `base` and was
		// scaled by the charge the same way the rest of the attribute damage was (Player.attack does
		// `attributeDamage * (0.2 + scale^2 * 0.8)`); the penalty is scaled to match, and the crit below
		// then multiplies the nerfed blow rather than the vanilla one.
		base -= strengthPenalty(p) * (0.2 + scale * scale * 0.8);

		// 2. our enchantment bonus, then 3. the crit over both.
		double enchant = vanillaEnchant + scale * delta;
		return Math.max(0, (base + enchant) * (crit ? 1.5 : 1));
	}

	private static void handleTridentHit(Trident trident, DamageData data) {
		ItemStack tridentItem = trident.getItemStack();
		trident.setVelocity(new Vector(0, -0.1, 0)); // Small downward velocity to make it drop

		// Riptide damage bonus (if thrown during rain/water)
		if(tridentItem.containsEnchantment(Enchantment.RIPTIDE)) {
			int riptideLevel = tridentItem.getEnchantmentLevel(Enchantment.RIPTIDE);
			data.originalDamage += riptideLevel * 2; // Bonus damage for riptide
		}
	}

	public static void customMobs(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		// A dragon mid-death animation is a corpse.  Hitting one used to re-run the whole death branch below - the
		// phase flip, dragonDeathTime back to 1, another death sound, another XP drop, another pin ticker - so a
		// party still swinging restarted the 200-tick animation every hit and the dragon simply never finished
		// dying.  It also re-ran the boss's own whenDamaged (the Primal Dragon replayed its death dialogue and
		// re-granted the advancement) and drew hit particles off a dead target.  Refuse at both entry points.
		if(isDyingDragon(damagee)) return;
		if(damager instanceof Projectile projectile) {
			originalDamage = data.originalDamage;
			// stop stupidly annoying arrows
			if(projectile instanceof Trident trident) {
				handleTridentHit(trident, data);
			} else if(projectile instanceof AbstractArrow arrow) {
				if(arrow.getPierceLevel() == 0) {
					arrow.remove();
				} else {
					// DO NOT decrement the pierce level. Vanilla counts its own hits, in
					// AbstractArrow.onHitEntity: it keeps the entity ids it has already pierced in
					// piercingIgnoreEntityIds and discards the arrow once that set reaches
					// getPierceLevel() + 1. Lowering the level here counted every hit TWICE, so the two
					// counters met early - a Terminator arrow (pierce 4, lore "up to 5 foes") stopped
					// dealing damage after THREE, and the fourth hit was swallowed without a scratch,
					// which is exactly what a shot phasing through a target looks like.
					//
					// The velocity IS ours to put back. Cancelling the damage event makes hurtOrSimulate
					// return false, so vanilla takes its miss branch and deflects the arrow REVERSE at 0.2
					// of its speed - the bounce you see off a mob. Scheduler tasks run before entities
					// tick, so restoring it next tick lands before that reversed vector can move the
					// arrow, though the client still renders one frame of the flip.
					Vector arrowSpeed = arrow.getVelocity();
					Utils.scheduleTask(() -> arrow.setVelocity(arrowSpeed), 1L);
				}
			}

			if(projectile instanceof SpectralArrow) {
				damagee.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0));
			}

			if(projectile instanceof Arrow a && a.hasCustomEffects()) {
				damagee.addPotionEffects(a.getCustomEffects());
			}

			if(projectile.getShooter() instanceof LivingEntity temp) {
				damager = temp;
			}
		}

		// apply custom damage to special mobs before going through with general damage
		boolean doContinue = true;
		try {
			CustomMob damageeMob = CustomMob.getMob(damagee);
			CustomMob damagerMob = CustomMob.getMob(damager);

			// this section controls when bosses are damaged
			if(damageeMob != null) {
				doContinue = damageeMob.whenDamaged(damagee, damager, originalDamage, type, data);
			}

			// this section controls when bosses deal damage
			if(damagerMob != null) {
				doContinue = damagerMob.whenDamaging(damagee, damager, originalDamage, type, data);
			}
			if(!data.isBlocking) {
				switch(damager) {
					case Wither ignored -> damagee.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1));
					case CaveSpider ignored ->
							damagee.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 0));
					case WitherSkeleton ignored ->
							damagee.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 0));
					case Husk ignored -> damagee.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 0));
					case Shulker ignored ->
							damagee.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 200, 0));
					case null, default -> {
					}
				}
			}
		} catch(NullPointerException exception) {
			// continue
		}

		if((damagee instanceof Player p && (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR)) || (damagee instanceof Wither wither && wither.getInvulnerableTicks() > 0)) {
			doContinue = false;
		}

		// PvP layer (config-gated, inert off the pvp server): suppress damage in a Free-For-All
		// safezone, during a duel countdown, or from an outsider interfering in a duel.
		if(pvp.PvpHooks.shouldBlock(damagee, damager)) {
			doContinue = false;
		}

		if(type == DamageType.LETHAL_ABSOLUTE || doContinue) {
			calculateFinalDamage(damagee, damager, originalDamage, type, data);
		}
	}

	public static void calculateFinalDamage(LivingEntity damagee, Entity damager, double finalDamage, DamageType type) {
		calculateFinalDamage(damagee, damager, finalDamage, type, new DamageData(damagee, damager, finalDamage));
	}

	public static void calculateFinalDamage(LivingEntity damagee, Entity damager, double finalDamage, DamageType type, DamageData data) {
		if(isDyingDragon(damagee)) return; // see customMobs; a boss's whenDamaged calls in here directly
		if(!(DamageType.isAbsoluteDamage(type))) {
			// bonus damage to withers from hyperion
			if(damagee instanceof Wither && (type == DamageType.MELEE || type == DamageType.MELEE_SWEEP) && damager instanceof Player p && p.getInventory().getItemInMainHand().hasItemMeta() && Utils.firstLorePlain(p.getInventory().getItemInMainHand().getItemMeta()).equals("skyblock/combat/scylla")) {
				finalDamage += Scylla.WITHER_BONUS;
			}

			// ice spray logic
			if(damagee.getScoreboardTags().contains("IceSprayed")) {
				finalDamage *= IceSpray.DAMAGE_TAKEN_BONUS;
			}

			// The share is the Hyperion's, not the tag's - a Manhunt Hyperion shields for as little as 5%.
			if(damagee.getScoreboardTags().contains("WitherShield")) {
				finalDamage *= 1 - Scylla.witherShieldReduction(damagee);
			}

			if(damagee.getScoreboardTags().contains("HolyIce")) {
				finalDamage *= HolyIce.DAMAGE_TAKEN;
			}

			if(damager instanceof LivingEntity entity1) {
				if(entity1.getScoreboardTags().contains("IceSprayed")) {
					finalDamage *= IceSpray.DAMAGE_DEALT_PENALTY;
				}

				if(entity1.getScoreboardTags().contains("BadHealthBuffed")) {
					finalDamage *= SwordOfBadHealth.DAMAGE_BONUS;
				}
			}

			// Golden-sword piglins hit disproportionately hard for how commonly they spawn holding one;
			// shave 25% off. Multiplicative here, so the sword's +3 and any vanilla difficulty scaling
			// baked into the incoming damage are both scaled proportionally.
			if(type == DamageType.MELEE && damager instanceof org.bukkit.entity.Piglin piglin
					&& piglin.getEquipment().getItemInMainHand().getType() == Material.GOLDEN_SWORD) {
				finalDamage *= 0.75;
			}

			// shield logic (for weirdos).  A raised shield only counts against a blow from the FRONT - see
			// blockedFromFront.  The 0.4 itself is ours and is NOT vanilla's number: a vanilla shield is
			// base 0 / factor 1, i.e. a blocked hit is reduced to nothing.
			// The projectile's own position where we have it, the attacker's otherwise - the fallback is for
			// the DamageData built off a plain EntityDamageEvent, which never saw an attacker.
			Location shieldSource = data.sourcePosition != null ? data.sourcePosition
					: (damager == null ? null : damager.getLocation());
			if(data.isBlocking && (type == DamageType.MELEE || type == DamageType.MELEE_SWEEP || type == DamageType.RANGED || type == DamageType.RANGED_SPECIAL)
					&& blockedFromFront(damagee, shieldSource)) {
				finalDamage *= 0.4;
			}

			double breach = 0;
			if(damager instanceof LivingEntity entity && entity.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.BREACH)) {
				breach = entity.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.BREACH);
			}

			boolean affectedByArmor = type == DamageType.MELEE || type == DamageType.MELEE_SWEEP || type == DamageType.RANGED || type == DamageType.RANGED_SPECIAL || type == DamageType.PLAYER_MAGIC || type == DamageType.ENVIRONMENTAL || type == DamageType.IFRAME_ENVIRONMENTAL;
			if(affectedByArmor) {
				double armor = Objects.requireNonNull(damagee.getAttribute(Attribute.ARMOR)).getValue();
				armor = Math.max(0, armor - breach * 2.5);
				double reduction;
				if(armor < 15) {
					reduction = armor * 0.04;
				} else {
					reduction = 1.0 - 1.0 / (1.0 + 0.00014666 * Math.pow(armor, 3.409));
				}
				finalDamage *= (1.0 - reduction);
			}

			// The Resistance status effect reduces damage by 20% per level.
			// At level 5 and higher, the damagee is immune to damage.
			double resistance = 0;
			if(damagee.hasPotionEffect(PotionEffectType.RESISTANCE)) {
				resistance = damagee.getPotionEffect(PotionEffectType.RESISTANCE).getAmplifier() + 1;
			}
			finalDamage *= Math.max(0.0, 1 - resistance * 0.2);

			// The Protection enchantment reduces damage taken by 2.5% per level.
			// A full set of Protection IV armor reduces damage by 40%
			// A full set of Protection V armor reduces damage by 50%
			double prots = 0;
			EntityEquipment eq = damagee.getEquipment();
			if(eq != null) {
				ItemStack helmet = eq.getHelmet();
				ItemStack chestplate = eq.getChestplate();
				ItemStack pants = eq.getLeggings();
				ItemStack boots = eq.getBoots();

				prots += helmet.getEnchantmentLevel(Enchantment.PROTECTION);
				if(affectedByArmor) {
					Utils.damageItem(damagee, helmet, Math.max(0.25, data.originalDamage / 33.33));
				}

				prots += chestplate.getEnchantmentLevel(Enchantment.PROTECTION);
				if(affectedByArmor && chestplate.getType() != Material.ELYTRA) {
					Utils.damageItem(damagee, chestplate, Math.max(0.25, data.originalDamage / 12.5));
				}

				prots += pants.getEnchantmentLevel(Enchantment.PROTECTION);
				if(affectedByArmor) {
					Utils.damageItem(damagee, pants, Math.max(0.25, data.originalDamage / 16.67));
				}

				prots += boots.getEnchantmentLevel(Enchantment.PROTECTION);
				if(affectedByArmor) {
					Utils.damageItem(damagee, boots, Math.max(0.25, data.originalDamage / 33.33));
				}

				finalDamage *= Math.max(0.5, 1 - prots * 0.025);

				if(type == DamageType.FALL) {
					double featherFalling = boots.getEnchantmentLevel(Enchantment.FEATHER_FALLING);
					finalDamage *= Math.max(0.2, (1 - featherFalling * 0.16) * damagee.getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER).getValue());
					if(finalDamage < 1) {
						finalDamage = 0;
					}
				}
			}
		}
		dealDamage(damagee, damager, finalDamage, type, data);
	}

	private static void dealDamage(LivingEntity damagee, Entity damager, double finalDamage, DamageType type, DamageData data) {
		if(!damagee.isDead() && (finalDamage > 0 || type == DamageType.RANGED || type == DamageType.RANGED_SPECIAL) && (damagee.getNoDamageTicks() == 0 || type == DamageType.RANGED || type == DamageType.RANGED_SPECIAL || type == DamageType.MAGIC || type == DamageType.PLAYER_MAGIC || type == DamageType.ABSOLUTE || type == DamageType.LETHAL_ABSOLUTE)) {
			// sweeping edge
			if(type == DamageType.MELEE && damager instanceof LivingEntity temp && temp.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.SWEEPING_EDGE)) {
				int level = temp.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.SWEEPING_EDGE);
				List<Entity> entities = damagee.getNearbyEntities(2, 2, 2);
				List<EntityType> doNotKill = CustomItems.createList();
				for(Entity entity : entities) {
					if(!doNotKill.contains(entity.getType()) && !entity.equals(damager) && entity instanceof LivingEntity entity1 && entity1.getHealth() > 0) {
						customMobs(entity1, damager, data.originalDamage * 0.125 * level, DamageType.MELEE_SWEEP);
					}
				}
			}

			damagee.playHurtAnimation(0.0F);
			damagee.getWorld().playSound(damagee, Objects.requireNonNull(damagee.getHurtSound()), 1.0F, 1.0F);

			double absorption = damagee.getAbsorptionAmount();
			double oldHealth = damagee.getHealth();

			// What the blow landed, for an ability that reports its own damage (the Hyperion's implosion
			// counter). Here rather than lower down because this is the last word on the figure: the
			// absorption split below spends part of it, and the health it removes is capped by whatever the
			// target had left, neither of which is the damage dealt.
			data.damageDealt = finalDamage;

			// PvP layer (config-gated, inert off the pvp server): record this hit for arena/duel combat stats.
			pvp.PvpHooks.trackHit(damagee, damager, finalDamage,
					type == DamageType.RANGED || type == DamageType.RANGED_SPECIAL,
					data.isCrit, // critical - decided once, at the damage event, and already priced into finalDamage
					damagee.getNoDamageTicks() > 0); // landed during the victim's i-frames

			// Intelligence for landing a melee blow. Granted here, at the end of the pipeline, rather than
			// at the damage event, so a swing that ends up dealing nothing (armor/resistance soaking it to
			// 0, a blocked hit, an i-framed target, or a blow the PvP layer suppressed) pays out nothing.
			// Any living target pays out - except during a Manhunt, where it has to be a player on the
			// OPPOSING side.  Mobs paying out would let a Hunter farm mana off a cow instead of hunting, and
			// hitting your own team for it is worse still: two Hunters stood in a corner punching each other
			// would out-regen anybody actually playing, and a Speedrunner could feed a teammate the same way.
			if(finalDamage > 0 && type == DamageType.MELEE && damager instanceof Player p
					&& (!manhunt.Manhunt.active()
							|| (damagee instanceof Player victim && manhunt.Manhunt.opposingTeams(p, victim)))) {
				try {
					Score score = Plugin.getIntelligence(p);
					if(score.getScore() < Plugin.maxIntelligence(p)) {
						score.setScore(score.getScore() + 1);
					}
					Plugin.sendIntelligenceBar(p, score);
				} catch(Exception exception) {
					Plugin.getInstance().getLogger().info("Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
					Bukkit.broadcast(Utils.msg("<red>Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin"));
					return;
				}
			}

			boolean isPhysicalHit = type == DamageType.MELEE || type == DamageType.MELEE_SWEEP || type == DamageType.RANGED || type == DamageType.RANGED_SPECIAL;
			// handle particles and wind burst
			if(damager instanceof Player p) {
				Location particleLoc = damagee.getLocation().add(0, damagee.getHeight() / 2, 0);
				ItemStack weapon = p.getEquipment().getItemInMainHand();

				// Critical hit particles and sound, placed the way vanilla places them: the particles on the
				// TARGET (Player.crit) but the sound on the ATTACKER, in the PLAYERS category
				// (Player.playServerSideSound plays it at the attacker's own x/y/z, at getSoundSource()).
				// It used to come from the victim with no category, so it panned to the wrong place and
				// ignored the players slider.
				if(data.isCrit) {
					damagee.getWorld().spawnParticle(Particle.CRIT, particleLoc, 24);
					p.getWorld().playSound(p, Sound.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1.0F, 1.0F);
				}

				// Enchanted hit particles
				if(!weapon.getEnchantments().isEmpty() && isPhysicalHit) {
					damagee.getWorld().spawnParticle(Particle.ENCHANTED_HIT, particleLoc, Math.min((int) (data.originalDamage * 8), 24));
				}

				// Damage Indicator particles
				if(data.originalDamage > 2 && isPhysicalHit) {
					damagee.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, particleLoc, Math.min((int) (data.originalDamage / 2), 24));
				}

				// Density enchantment - bonus smash damage on falling hits
				if(weapon.containsEnchantment(Enchantment.DENSITY) && p.getFallDistance() >= 1.5) {
					int densityLevel = weapon.getEnchantmentLevel(Enchantment.DENSITY);
					data.originalDamage += densityLevel * 0.5 * p.getFallDistance();
				}

				// Reset fall distance on Mace smash attack and spawn smash particles
				if(weapon.getType() == Material.MACE && p.getFallDistance() >= 1.5) {
					Location smashLoc = damagee.getLocation();
					damagee.getWorld().spawnParticle(Particle.EXPLOSION, smashLoc, 1);
					damagee.getWorld().spawnParticle(Particle.DUST_PLUME, smashLoc, 20, 0.5, 0.2, 0.5, 0.1);
					p.setFallDistance(0);
				}

				// Wind Burst mechanics
				if(weapon.containsEnchantment(Enchantment.WIND_BURST) && p.getFallDistance() >= 1.5) {
					int level = weapon.getEnchantmentLevel(Enchantment.WIND_BURST);

					// Launch player upward (scales with level and fall distance)
					double upwardVelocity = Math.min(0.5 + level * 0.5, 3.0);
					ServerPlayer serverPlayer = ((CraftPlayer) p).getHandle();
					serverPlayer.setOnGround(false);
					p.setVelocity(new Vector(p.getVelocity().getX(), upwardVelocity, p.getVelocity().getZ()));

					// Knockback nearby entities (radial burst)
					double radius = 3.0 + level;
					for(Entity nearby : damagee.getNearbyEntities(radius, radius, radius)) {
						if(nearby instanceof LivingEntity && nearby != p) {
							Vector knockback = nearby.getLocation().toVector().subtract(p.getLocation().toVector());
							knockback.setY(0);
							double dist = knockback.length();
							if(dist > 0) {
								knockback.normalize().multiply(1.0 + level * 0.5);
								knockback.setY(0.4);
								nearby.setVelocity(knockback);
							}
						}
					}
					// Also knockback the hit target
					Vector targetKB = damagee.getLocation().toVector().subtract(p.getLocation().toVector());
					targetKB.setY(0);
					if(targetKB.length() > 0) {
						targetKB.normalize().multiply(1.0 + level * 0.5);
						targetKB.setY(0.4);
						damagee.setVelocity(targetKB);
					}

					// Effects
					p.getWorld().spawnParticle(Particle.GUST_EMITTER_LARGE, p.getLocation(), 1);
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WIND_CHARGE_WIND_BURST, 1.0F, 1.0F);
					p.setFallDistance(0);
				}

				// damage weapon if direct melee attack
				if(type == DamageType.MELEE) {
					Utils.damageItem(p, weapon, 1);
				}
			}

			// handle thorns
			if(damager instanceof LivingEntity && (type == DamageType.MELEE || type == DamageType.MELEE_SWEEP)) {
				EntityEquipment eq = damagee.getEquipment();
				if(eq != null) {
					Random random = new Random();
					int totalThornsLevel = 0;
					List<ItemStack> thornsArmor = new ArrayList<>();

					// Check all armor pieces for thorns
					ItemStack[] armorPieces = {eq.getHelmet(), eq.getChestplate(), eq.getLeggings(), eq.getBoots()};

					for(ItemStack armor : armorPieces) {
						if(armor == null || armor.getType() == Material.AIR) continue;

						int thornsLevel = armor.getEnchantmentLevel(Enchantment.THORNS);
						if(thornsLevel > 0) {
							totalThornsLevel += thornsLevel;
							thornsArmor.add(armor);
						}
					}

					if(totalThornsLevel > 0) {
						double activationChance = Math.min(totalThornsLevel * 0.15, 1.0);

						if(random.nextDouble() < activationChance) {
							// Calculate thorns damage (1-4 damage, higher levels increase max)
							int thornsDamage = random.nextInt(4) + 1;

							// Higher thorns levels can do more damage
							if(totalThornsLevel > 10) {
								thornsDamage += 2;
							} else if(totalThornsLevel > 5) {
								thornsDamage += 1;
							}

							customMobs((LivingEntity) damager, damagee, thornsDamage, DamageType.PLAYER_MAGIC);

							if(!thornsArmor.isEmpty()) {
								ItemStack armorToDamage = thornsArmor.get(random.nextInt(thornsArmor.size()));
								Utils.damageItem(damagee, armorToDamage, 1);
							}
						}
					}
				}
			}

			// armor stand override
			if(damagee instanceof ArmorStand) {
				if(type == DamageType.MELEE || type == DamageType.MELEE_SWEEP || type == DamageType.RANGED) {
					finalDamage = 11;
					type = DamageType.LETHAL_ABSOLUTE;
				} else {
					return;
				}
			}

			// fire aspect - should always apply if not blocking
			if(!data.isBlocking) {
				if(type == DamageType.MELEE && damager instanceof LivingEntity temp && temp.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.FIRE_ASPECT)) {
					int level = temp.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.FIRE_ASPECT);
					damagee.setFireTicks(level * 80);
				} else if(data.flamingArrow) {
					damagee.setFireTicks(100);
				}
			}

			// handle raid mechanics
			if(damagee instanceof Raider raider) {
				net.minecraft.world.entity.raid.Raider nmsRaider = ((CraftRaider) raider).getHandle();
				net.minecraft.world.entity.raid.Raid nmsRaid = nmsRaider.getCurrentRaid();

				if(nmsRaid != null) {
					if(damager instanceof Player player) {
						ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
						nmsRaid.addHeroOfTheVillage(nmsPlayer);
					}
				} else {
					ServerLevel world = ((CraftWorld) raider.getWorld()).getHandle();
					Raids raids = world.getRaids();

					BlockPos pos = new BlockPos(raider.getLocation().getBlockX(), raider.getLocation().getBlockY(), raider.getLocation().getBlockZ());
					net.minecraft.world.entity.raid.Raid raidAtPos = raids.getNearbyRaid(pos, 128);

					if(raidAtPos != null) {
						if(damager instanceof Player player) {
							ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
							raidAtPos.addHeroOfTheVillage(nmsPlayer);
						}
					}
				}
			}
			if(finalDamage >= oldHealth + absorption) {
				// PvP layer: totems are ignored (kept, not consumed) inside the FFA arena, where the kill is
				// scored and the victim respawns anyway. Always false off the pvp server / outside the arena.
				if(type != DamageType.LETHAL_ABSOLUTE && !pvp.PvpHooks.ignoresTotem(damagee) && (damagee.getEquipment().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING) || damagee.getEquipment().getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING))) {
					if(damagee.getEquipment().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING)) {
						damagee.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
					} else if(damagee.getEquipment().getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING)) {
						damagee.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
					}

					if(damagee instanceof Player p) {
						ServerPlayer serverPlayer = ((CraftPlayer) p).getHandle();
						serverPlayer.level().broadcastEntityEvent(serverPlayer, (byte) 35);

						ItemStack totemStack = new ItemStack(Material.TOTEM_OF_UNDYING);
						net.minecraft.world.item.ItemStack nmsTotem = CraftItemStack.asNMSCopy(totemStack);
						CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, nmsTotem);
						serverPlayer.awardStat(Stats.ITEM_USED.get(nmsTotem.getItem()));
					}

					damagee.setHealth(1.0);
					// Play the totem sound. Send it straight to the player (and to nearby players via the
					// world location) since the entity-event 35 animation doesn't reliably carry the sound here.
					Location totemLoc = damagee.getLocation();
					damagee.getWorld().playSound(totemLoc, Sound.ITEM_TOTEM_USE, 1.0F, 1.0F);
					if(damagee instanceof Player totemUser) {
						totemUser.playSound(totemLoc, Sound.ITEM_TOTEM_USE, 1.0F, 1.0F);
					}
					damagee.getActivePotionEffects().forEach(effect -> damagee.removePotionEffect(effect.getType()));
					damagee.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 900, 1));
					damagee.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 800, 0));
					damagee.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 1));
					triggerAllRelevantAdvancements(damagee, damager, type, data.originalDamage, finalDamage, data.isBlocking, false, data);
				} else {
					// PvP layer (config-gated, inert off the pvp server): with no totem to save them, a
					// lethal blow ends the duel / scores the FFA kill and revives the player instead of
					// killing them. Placed AFTER the totem branch so arena kills don't bypass totems.
					// LETHAL_ABSOLUTE (e.g. /kill, void) is a draw in 1v1 and doesn't count a death in FFA.
					if(pvp.PvpHooks.handleLethal(damagee, damager, type == DamageType.LETHAL_ABSOLUTE)) {
						return;
					}
					if(damagee instanceof EnderDragon dragon && data.e != null && data.e.getCause() == DamageCause.BLOCK_EXPLOSION) {
						if(damager == null) {
							damager = Utils.getNearestPlayer(dragon, 16);
						}
					}
					triggerAllRelevantAdvancements(damagee, damager, type, data.originalDamage, finalDamage, data.isBlocking, true, data);
					if(damagee instanceof Villager villager && damager instanceof Zombie) {
						villager.zombify();
						Utils.changeName(villager);
					} else {
						CustomDrops.loot(damagee, damager);

						// handle ender dragons specially
						if(damagee instanceof EnderDragon dragon) {
							if(!(dragon instanceof CraftEnderDragon)) return;
							dragon.addScoreboardTag(DYING_DRAGON_TAG); // from here on it is a corpse - see isDyingDragon
							net.minecraft.world.entity.boss.enderdragon.EnderDragon nmsDragon = ((CraftEnderDragon) dragon).getHandle();
							nmsDragon.getPhaseManager().setPhase(EnderDragonPhase.DYING);
							DragonPhaseInstance phase = nmsDragon.getPhaseManager().getCurrentPhase();

							// force dragon's target location to its location
							if(phase instanceof DragonDeathPhase deathPhase) {
								try {
									Field targetField = DragonDeathPhase.class.getDeclaredField("targetLocation");
									targetField.setAccessible(true);
									Location l = dragon.getLocation();
									targetField.set(deathPhase, new Vec3(l.getX(), l.getY(), l.getZ()));

								} catch(Exception e) {
									e.printStackTrace();
								}
							}

							nmsDragon.setDeltaMovement(Vec3.ZERO);
							nmsDragon.setHealth(1.0F);

							// Set death time to 1 to start animation immediately
							try {
								Field deathTimeField = nmsDragon.getClass().getDeclaredField("dragonDeathTime");
								deathTimeField.setAccessible(true);
								deathTimeField.setInt(nmsDragon, 1);
							} catch(Exception e) {
								// Fallback
								Bukkit.getLogger().warning("Failed to force Dragon death animation.");
							}
							Utils.playGlobalSound(Sound.ENTITY_ENDER_DRAGON_DEATH);
							dragon.setSilent(true);
							Utils.scheduleTask(() -> spawnDragonXP(dragon.getLocation(), dragon.getScoreboardTags().contains("HardMode") ? 640000 : 64000), 190);

							// Pin the dragon at its kill spot for the death animation. The DYING phase otherwise
							// paths it toward the exit portal first, so it visibly flies off before the death
							// beams/ascent play. Cancel horizontal movement each tick; keep the upward death rise.
							final double dragonPinX = dragon.getLocation().getX();
							final double dragonPinZ = dragon.getLocation().getZ();
							final int[] dragonPinTicks = {0};
							Bukkit.getScheduler().runTaskTimer(Plugin.getInstance(), task -> {
								if(nmsDragon.isRemoved() || ++dragonPinTicks[0] > 220) {
									task.cancel();
									return;
								}
								Vec3 m = nmsDragon.getDeltaMovement();
								nmsDragon.setDeltaMovement(0.0, Math.max(0.0, m.y), 0.0);
								nmsDragon.setPos(dragonPinX, nmsDragon.getY(), dragonPinZ);
							}, 1L, 1L);
						} else {
							// build death message before setHealth(0) so the PlayerDeathEvent handler can use it
							if(damagee instanceof Player p) {
								if(data.e != null) {
									DamageSource damageSource = convertBukkitDamageSource(data.e.getDamageSource(), p);
									ServerPlayer nmsPlayer = ((CraftPlayer) p).getHandle();
									Component message = damageSource.getLocalizedDeathMessage(nmsPlayer);
									nextDeathMessage = io.papermc.paper.adventure.PaperAdventure.asAdventure(message);
								} else {
									// Build a Component from the killer's formatted display name (its live magic shimmer)
									// rather than the plain getName(), so a boss keeps its colours/shimmer in the death message.
									net.kyori.adventure.text.Component killer = damager instanceof LivingEntity dle && dle.customName() != null ? dle.customName() : net.kyori.adventure.text.Component.text(damager != null ? damager.getName() : "absolutely no one");
									net.kyori.adventure.text.Component who = net.kyori.adventure.text.Component.text(p.getName());
									nextDeathMessage = switch(type) {
										case MELEE, MELEE_SWEEP -> who.append(net.kyori.adventure.text.Component.text(" was slain by ")).append(killer);
										case RANGED -> who.append(net.kyori.adventure.text.Component.text(" was shot by ")).append(killer);
										case RANGED_SPECIAL -> who.append(net.kyori.adventure.text.Component.text(" was killed by ")).append(killer).append(net.kyori.adventure.text.Component.text("'s lasers"));
										case MAGIC, PLAYER_MAGIC -> who.append(net.kyori.adventure.text.Component.text(" was killed by ")).append(killer).append(net.kyori.adventure.text.Component.text("'s magic"));
										case ENVIRONMENTAL, IFRAME_ENVIRONMENTAL -> who.append(net.kyori.adventure.text.Component.text(" was killed by the world"));
										case FALL -> who.append(net.kyori.adventure.text.Component.text(" fell to their death"));
										case ABSOLUTE, LETHAL_ABSOLUTE -> who.append(net.kyori.adventure.text.Component.text(" fell out of the world"));
										default -> who.append(net.kyori.adventure.text.Component.text(" died"));
									};
								}
							}
							damagee.setHealth(0.0);
						}
					}
				}
			} else {
				// absorption
				if(finalDamage > absorption) {
					damagee.setAbsorptionAmount(0.0);
					finalDamage -= absorption;
				} else {
					damagee.setAbsorptionAmount(absorption - finalDamage);
					finalDamage = 0.0;
				}

				// damage
				damagee.setHealth(oldHealth - finalDamage);
				if(type == DamageType.MELEE || type == DamageType.MELEE_SWEEP || type == DamageType.IFRAME_ENVIRONMENTAL) {
					damagee.setNoDamageTicks(9);
				}

				if(damagee instanceof Mob && damager instanceof LivingEntity) {
					// Endermen shouldn't swarm the dragon just for standing in its breath/fireball AoE around
					// the fountain; only a direct flying hit (MELEE) should pull their aggro onto it.
					boolean dragonAoeOnEnderman = damagee instanceof Enderman && damager instanceof EnderDragon && type != DamageType.MELEE;
					if(!(damagee instanceof Wolf wolf && damager instanceof Player player && wolf.getOwner().getUniqueId().equals(player.getUniqueId())) && !dragonAoeOnEnderman) {
						((Mob) damagee).setTarget((LivingEntity) damager);
					}
				}

				if(damagee instanceof Shulker shulker && damager instanceof Shulker) {
					handleShulkerDuplication(shulker);
				}

				if(damagee instanceof ArmorStand armorStand) {
					armorStand.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, -1, 255, true, false));
				}

				// special ender dragon knockback to make zero- and one-cycling possible
				if(damagee instanceof EnderDragon dragon) {
					if(data.e != null && data.e.getCause() == DamageCause.BLOCK_EXPLOSION) {
						Vector v = dragon.getVelocity();
						if(dragon.getPhase() == EnderDragon.Phase.LAND_ON_PORTAL) {
							dragon.setVelocity(new Vector(v.getX(), 0.25, v.getZ()));
						} else {
							dragon.setVelocity(new Vector(v.getX(), 0.333333, v.getZ()));
						}
						damager = Utils.getNearestPlayer(dragon, 16);
					}
				} else if(isPhysicalHit && damager != null) {
					// apply knockback
					double antiKB = 1 - Objects.requireNonNull(damagee.getAttribute(Attribute.KNOCKBACK_RESISTANCE)).getValue();
					// Skip everything for a fully knockback-resistant target (antiKB <= 0): its motion is
					// left untouched (vanilla behavior) and we avoid a sqrt of a non-positive amount.
					if(antiKB > 0) {
						double enchantments = 1;

						if(damager instanceof LivingEntity livingEntity) {
							if(livingEntity.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.KNOCKBACK) && (type == DamageType.MELEE || type == DamageType.MELEE_SWEEP)) {
								enchantments += 0.33333 * livingEntity.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.KNOCKBACK);
							} else if(data.punchArrow > 0) {
								enchantments += 0.33333 * data.punchArrow;
							}
						}

						// All non-resistance knockback modifiers (enchant, sprint, fall, block, term).
						double modifiers = enchantments;

						if(type == DamageType.MELEE) {
							if(damager.getFallDistance() > 0) {
								modifiers *= 1.2;
							}

							if(damager instanceof Player p && p.isSprinting()) {
								modifiers *= 1.2;
							}
						}

						if(data.isTermArrow) {
							modifiers *= 0.33333;
						}

						if(data.isBlocking) {
							modifiers *= 0.5;
						}

						// Vertical knockback is a sqrt function of knockback resistance ONLY (enchant/
						// sprint/blocking never change the pop height). Horizontal uses antiKB^0.75 to
						// compensate for the longer air-time of that higher pop, keeping distance ~linear
						// in resistance (full netherite still lands ~60%) and exactly linear in the modifiers.
						double vertical = 0.4 * Math.sqrt(Math.min(antiKB, 1.0));
						double horizontal = 0.4 * (antiKB <= 1 ? Math.pow(antiKB, 0.75) : antiKB) * modifiers;

						// Calculate knockback direction from damager to damagee
						Vector knockbackDir = damagee.getLocation().toVector().subtract(damager.getLocation().toVector());

						// Normalize horizontal direction only (preserve Y=0 for horizontal KB)
						double horizontalDist = Math.sqrt(knockbackDir.getX() * knockbackDir.getX() + knockbackDir.getZ() * knockbackDir.getZ());
						if(horizontalDist > 0) {
							knockbackDir.setX(knockbackDir.getX() / horizontalDist);
							knockbackDir.setZ(knockbackDir.getZ() / horizontalDist);
						}

						// Apply knockback
						Vector oldVelocity = damagee.getVelocity();
						Vector newVelocity;
						if(data.isTermArrow && Integer.valueOf(MinecraftServer.currentTick).equals(lastTermKnockbackTick.get(damagee))) {
							// Same-tick Terminator volley: stack horizontally onto the prior arrow(s) so the
							// three 1/3-strength hits add up, keeping the single-hit pop (don't launch 3x high).
							newVelocity = new Vector(oldVelocity.getX() + knockbackDir.getX() * horizontal, (damagee.isOnGround() ? Math.max(oldVelocity.getY(), vertical) : oldVelocity.getY()), oldVelocity.getZ() + knockbackDir.getZ() * horizontal);
						} else {
							// First hit this tick: damp the target's own momentum (retain 10%) as usual. Only apply the
							// vertical pop when grounded; an airborne target keeps its current Y (like vanilla) so hits
							// mid-air don't cancel its fall/jump arc.
							newVelocity = new Vector(oldVelocity.getX() * 0.1 + knockbackDir.getX() * horizontal, (damagee.isOnGround() ? vertical : oldVelocity.getY()), oldVelocity.getZ() * 0.1 + knockbackDir.getZ() * horizontal);
						}

						damagee.setVelocity(newVelocity);

						if(data.isTermArrow) {
							lastTermKnockbackTick.put(damagee, MinecraftServer.currentTick);
						}
					}
				}

				// change nametag health
				Utils.changeName(damagee);
				triggerAllRelevantAdvancements(damagee, damager, type, data.originalDamage, finalDamage, data.isBlocking, false, data);
			}
		}
	}

	public static void spawnDragonXP(Location deathLocation, int totalXP) {
		ServerLevel level = ((CraftWorld) deathLocation.getWorld()).getHandle();
		Vec3 pos = new Vec3(deathLocation.getX(), deathLocation.getY(), deathLocation.getZ());

		// 10 waves of 8%, every 5 ticks starting at tick 0
		for(int wave = 0; wave < 10; wave++) {
			int delay = wave * 5;
			Utils.scheduleTask(() -> net.minecraft.world.entity.ExperienceOrb.award(level, pos, (int) Math.floor((float) totalXP * 0.08F)), delay);
		}

		// Final wave of 20% at tick 50
		Utils.scheduleTask(() -> net.minecraft.world.entity.ExperienceOrb.award(level, pos, (int) Math.floor((float) totalXP * 0.2F)), 50);
	}

	private static void triggerAllRelevantAdvancements(LivingEntity victim, Entity attacker, DamageType type,
	                                                   double originalDamage, double finalDamage, boolean wasBlocked, boolean wasKilled, DamageData data) {
		DamageSource nmsSource;
		Entity causingEntity;
		if(data.e != null) {
			causingEntity = data.e.getDamageSource().getCausingEntity();
			nmsSource = convertBukkitDamageSource(data.e.getDamageSource(), victim);
		} else {
			org.bukkit.damage.DamageType bukkitType = switch(type) {
				case MELEE -> org.bukkit.damage.DamageType.MOB_ATTACK;
				case MELEE_SWEEP -> org.bukkit.damage.DamageType.PLAYER_ATTACK;
				case RANGED -> org.bukkit.damage.DamageType.ARROW;
				case RANGED_SPECIAL -> org.bukkit.damage.DamageType.SONIC_BOOM;
				case MAGIC, PLAYER_MAGIC -> org.bukkit.damage.DamageType.MAGIC;
				case ENVIRONMENTAL -> org.bukkit.damage.DamageType.FALLING_BLOCK;
				case IFRAME_ENVIRONMENTAL -> org.bukkit.damage.DamageType.ON_FIRE;
				case FALL -> org.bukkit.damage.DamageType.FALL;
				case ABSOLUTE -> org.bukkit.damage.DamageType.GENERIC;
				case LETHAL_ABSOLUTE -> org.bukkit.damage.DamageType.GENERIC_KILL;
			};
			causingEntity = attacker;
			nmsSource = convertBukkitDamageSource(org.bukkit.damage.DamageSource.builder(bukkitType).build(), victim);
		}

		if(attacker instanceof Player || causingEntity instanceof Player) {
			ServerPlayer serverPlayer;
			if(causingEntity instanceof Player p) {
				serverPlayer = ((CraftPlayer) p).getHandle();
			} else {
				serverPlayer = ((CraftPlayer) attacker).getHandle();
			}
			net.minecraft.world.entity.LivingEntity nmsVictim = ((CraftLivingEntity) victim).getHandle();


			if(wasKilled) {
				// 2. PLAYER_KILLED_ENTITY - Main kill advancement
				CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(serverPlayer, nmsVictim, nmsSource);

				// 5 & 6. Sculk catalyst triggers - only if sculk catalyst is nearby
				if(isSculkCatalystNearby(victim.getLocation())) {
					triggerVanillaSculkSpread(victim);
					CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.trigger(serverPlayer, nmsVictim, nmsSource);
					CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.trigger(serverPlayer, nmsVictim, nmsSource);
				}

				// 11. LIGHTNING_STRIKE - Improved lightning handling
				if(data.lightningInvolved && data.lightningBolt != null) {
					List<net.minecraft.world.entity.Entity> victims = List.of(nmsVictim);
					CriteriaTriggers.LIGHTNING_STRIKE.trigger(serverPlayer, data.lightningBolt, victims);
				}

			}
			// 1. PLAYER_HURT_ENTITY - Non-lethal damage
			CriteriaTriggers.PLAYER_HURT_ENTITY.trigger(serverPlayer, nmsVictim, nmsSource, (float) originalDamage, (float) finalDamage, wasBlocked);

			// 12. CHANNELED_LIGHTNING - Trident channeling
			if(data.isTridentAttack && data.tridentChanneling && data.lightningInvolved) {
				List<net.minecraft.world.entity.LivingEntity> victims = List.of(nmsVictim);
				CriteriaTriggers.CHANNELED_LIGHTNING.trigger(serverPlayer, victims);
			}
		}

		if(victim instanceof Player player) {
			ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
			net.minecraft.world.entity.Entity nmsKiller = attacker != null ? ((CraftEntity) attacker).getHandle() : null;

			if(wasKilled) {
				// 4. ENTITY_KILLED_PLAYER - Player death
				CriteriaTriggers.ENTITY_KILLED_PLAYER.trigger(serverPlayer, nmsKiller, nmsSource);

				// 7. USED_TOTEM - Already handled in your existing totem logic
				// (Keep your existing totem advancement trigger)

			} else {
				// 3. ENTITY_HURT_PLAYER - Player taking damage
				CriteriaTriggers.ENTITY_HURT_PLAYER.trigger(serverPlayer, nmsSource, (float) originalDamage, (float) finalDamage, wasBlocked);
			}
		}
		updatePlayerStatistics(victim, attacker, causingEntity, type, finalDamage, wasKilled);
	}

	private static void updatePlayerStatistics(LivingEntity victim, Entity attacker, Entity
			causingEntity, DamageType type, double finalDamage, boolean wasKilled) {
		// Player as attacker statistics
		Player attackingPlayer = null;
		if(causingEntity instanceof Player) {
			attackingPlayer = (Player) causingEntity;
		} else if(attacker instanceof Player) {
			attackingPlayer = (Player) attacker;
		}

		if(attackingPlayer != null) {
			ServerPlayer serverPlayer = ((CraftPlayer) attackingPlayer).getHandle();

			// Damage dealt
			serverPlayer.awardStat(Stats.DAMAGE_DEALT, Math.round((float) finalDamage * 10));

			if(wasKilled) {
				// Mob kills
				serverPlayer.awardStat(Stats.MOB_KILLS);

				// Specific entity kills
				net.minecraft.world.entity.EntityType<?> entityType = getEntityType(victim);
				serverPlayer.awardStat(Stats.ENTITY_KILLED.get(entityType));

				// Player kills (if victim is player)
				if(victim instanceof Player) {
					serverPlayer.awardStat(Stats.PLAYER_KILLS);
				}
			}

			// Weapon-specific statistics
			updateWeaponStatistics(serverPlayer, attacker, type);
		}

		// Player as victim statistics
		if(victim instanceof Player player) {
			ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

			// Damage taken
			serverPlayer.awardStat(Stats.DAMAGE_TAKEN, Math.round((float) finalDamage * 10));

			if(wasKilled) {
				// Deaths
				serverPlayer.awardStat(Stats.DEATHS);

				// Death by specific entity
				if(attacker != null) {
					net.minecraft.world.entity.EntityType<?> entityType = getEntityType(attacker);
					serverPlayer.awardStat(Stats.ENTITY_KILLED_BY.get(entityType));
				}
			}
		}
	}

	private static void updateWeaponStatistics(ServerPlayer player, Entity attacker, DamageType type) {
		net.minecraft.world.item.ItemStack weapon = player.getMainHandItem();

		switch(type) {
			case RANGED -> {
				if(attacker instanceof Arrow) {
					// Bow/Crossbow usage
					if(weapon.getItem() == Items.BOW) {
						player.awardStat(Stats.ITEM_USED.get(Items.BOW));
					} else if(weapon.getItem() == Items.CROSSBOW) {
						player.awardStat(Stats.ITEM_USED.get(Items.CROSSBOW));
					}
				} else if(attacker instanceof Trident) {
					player.awardStat(Stats.ITEM_USED.get(Items.TRIDENT));
				}
			}

			case MELEE, MELEE_SWEEP -> {
				// Melee weapon usage
				if(!weapon.isEmpty()) {
					player.awardStat(Stats.ITEM_USED.get(weapon.getItem()));
				}
			}
		}
	}

	private static net.minecraft.world.entity.EntityType<?> getEntityType(Entity entity) {
		return ((CraftEntity) entity).getHandle().getType();
	}

	private static boolean isSculkCatalystNearby(Location location) {
		World world = location.getWorld();
		if(world == null) return false;

		// Check for sculk catalyst within 8 blocks (vanilla range)
		for(int x = -8; x <= 8; x++) {
			for(int y = -8; y <= 8; y++) {
				for(int z = -8; z <= 8; z++) {
					Location checkLoc = location.clone().add(x, y, z);
					if(checkLoc.getBlock().getType() == Material.SCULK_CATALYST) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static void triggerVanillaSculkSpread(LivingEntity victim) {
		Location deathLoc = victim.getLocation();
		ServerLevel level = ((CraftWorld) deathLoc.getWorld()).getHandle();
		BlockPos deathPos = new BlockPos(deathLoc.getBlockX(), deathLoc.getBlockY(), deathLoc.getBlockZ());

		Logger logger = Bukkit.getLogger();
		Filter originalFilter = logger.getFilter();

		logger.setFilter(record -> {
			if(record.getMessage().contains("PostProcessing")) {
				return false;
			}
			return originalFilter == null || originalFilter.isLoggable(record);
		});

		try {
			// Create a sculk spreader instance
			SculkSpreader spreader = SculkSpreader.createWorldGenSpreader();

			// Add charge at death location
			int experience = ((CraftLivingEntity) victim).getHandle().getExperienceReward(level, ((CraftLivingEntity) victim).getHandle());
			spreader.addCursors(deathPos, experience);

			// Update the spreader (this triggers the actual spreading)
			spreader.updateCursors(level, deathPos, level.getRandom(), true);
		} finally {
			logger.setFilter(originalFilter);
		}
	}

	private static void handleShulkerDuplication(Shulker victim) {
		// Check if health is below 50% threshold (vanilla requirement)
		if(victim.getHealth() > victim.getAttribute(Attribute.MAX_HEALTH).getValue() * 0.5) return;

		// Use NMS to trigger vanilla duplication logic
		net.minecraft.world.entity.monster.Shulker nmsShulker = ((CraftShulker) victim).getHandle();

		// This method handles all the vanilla logic:
		// - Finding valid teleport location
		// - Spawning new shulker with correct color
		// - Teleport attempts
		try {
			Method hitByShulkerBulletMethod = net.minecraft.world.entity.monster.Shulker.class.getDeclaredMethod("hitByShulkerBullet");
			hitByShulkerBulletMethod.setAccessible(true);
			hitByShulkerBulletMethod.invoke(nmsShulker);
		} catch(Exception exception) {
			// nothing here
		}
	}

	private static DamageSource convertBukkitDamageSource(org.bukkit.damage.DamageSource bukkitSource, LivingEntity
			victim) {
		DamageSources sources = ((CraftLivingEntity) victim).getHandle().damageSources();

		org.bukkit.damage.DamageType damageType = bukkitSource.getDamageType();
		Entity directEntity = bukkitSource.getDirectEntity();
		Entity causingEntity = bukkitSource.getCausingEntity();

		net.minecraft.world.entity.Entity nmsDirectEntity = directEntity != null ? ((CraftEntity) directEntity).getHandle() : null;
		net.minecraft.world.entity.Entity nmsCausingEntity = causingEntity != null ? ((CraftEntity) causingEntity).getHandle() : null;

		// Handle entity-based damage types first with proper fallbacks
		if(damageType == org.bukkit.damage.DamageType.PLAYER_ATTACK) {
			if(nmsCausingEntity instanceof ServerPlayer player) {
				return sources.playerAttack(player);
			}
			// Fallback if causing entity isn't a ServerPlayer
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.MOB_ATTACK) {
			if(nmsCausingEntity instanceof net.minecraft.world.entity.LivingEntity living) {
				return sources.mobAttack(living);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.ARROW) {
			if(nmsDirectEntity instanceof net.minecraft.world.entity.projectile.arrow.AbstractArrow arrow) {
				return sources.arrow(arrow, nmsCausingEntity);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.FIREBALL) {
			if(nmsDirectEntity instanceof Fireball fireball) {
				return sources.fireball(fireball, nmsCausingEntity);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.UNATTRIBUTED_FIREBALL) {
			if(nmsDirectEntity instanceof Fireball fireball) {
				return sources.fireball(fireball, nmsCausingEntity);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.TRIDENT) {
			return sources.trident(nmsDirectEntity, nmsCausingEntity);

		} else if(damageType == org.bukkit.damage.DamageType.THORNS) {
			if(nmsCausingEntity != null) {
				return sources.thorns(nmsCausingEntity);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.EXPLOSION) {
			return sources.explosion(null, nmsCausingEntity);

		} else if(damageType == org.bukkit.damage.DamageType.PLAYER_EXPLOSION) {
			return sources.explosion(nmsCausingEntity, nmsCausingEntity);

		} else if(damageType == org.bukkit.damage.DamageType.MOB_PROJECTILE) {
			if(nmsCausingEntity instanceof net.minecraft.world.entity.LivingEntity living) {
				return sources.mobProjectile(nmsDirectEntity, living);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.FIREWORKS) {
			if(nmsDirectEntity instanceof FireworkRocketEntity firework) {
				return sources.fireworks(firework, nmsCausingEntity);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.WITHER_SKULL) {
			if(nmsDirectEntity instanceof WitherSkull witherSkull) {
				return sources.witherSkull(witherSkull, nmsCausingEntity);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.THROWN) {
			return sources.thrown(nmsDirectEntity, nmsCausingEntity);

		} else if(damageType == org.bukkit.damage.DamageType.INDIRECT_MAGIC) {
			return sources.indirectMagic(nmsDirectEntity, nmsCausingEntity);

		} else if(damageType == org.bukkit.damage.DamageType.FALLING_BLOCK) {
			if(nmsDirectEntity != null) {
				return sources.fallingBlock(nmsDirectEntity);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.FALLING_ANVIL) {
			if(nmsDirectEntity != null) {
				return sources.anvil(nmsDirectEntity);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.FALLING_STALACTITE) {
			if(nmsDirectEntity != null) {
				return sources.fallingStalactite(nmsDirectEntity);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.STING) {
			if(nmsCausingEntity instanceof net.minecraft.world.entity.LivingEntity living) {
				return sources.sting(living);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.MOB_ATTACK_NO_AGGRO) {
			if(nmsCausingEntity instanceof net.minecraft.world.entity.LivingEntity living) {
				return sources.noAggroMobAttack(living);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.SONIC_BOOM) {
			if(nmsCausingEntity != null) {
				return sources.sonicBoom(nmsCausingEntity);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.WIND_CHARGE) {
			if(nmsDirectEntity instanceof AbstractWindCharge windCharge) {
				if(nmsCausingEntity instanceof net.minecraft.world.entity.LivingEntity living) {
					return sources.windCharge(windCharge, living);
				}
				return sources.windCharge(windCharge, null);
			}
			return sources.generic();
		} else if(damageType == org.bukkit.damage.DamageType.MACE_SMASH) {
			if(nmsCausingEntity != null) {
				return sources.mace(nmsCausingEntity);
			}
			return sources.generic();
		}

		// Simple damage types (no entity requirements)
		else if(damageType == org.bukkit.damage.DamageType.BAD_RESPAWN_POINT) {
			return sources.badRespawnPointExplosion(new Vec3(0, 0, 0));
		} else if(damageType == org.bukkit.damage.DamageType.IN_FIRE) {
			return sources.inFire();
		} else if(damageType == org.bukkit.damage.DamageType.LIGHTNING_BOLT) {
			return sources.lightningBolt();
		} else if(damageType == org.bukkit.damage.DamageType.ON_FIRE) {
			return sources.onFire();
		} else if(damageType == org.bukkit.damage.DamageType.LAVA) {
			return sources.lava();
		} else if(damageType == org.bukkit.damage.DamageType.HOT_FLOOR) {
			return sources.hotFloor();
		} else if(damageType == org.bukkit.damage.DamageType.CAMPFIRE) {
			return sources.campfire();
		} else if(damageType == org.bukkit.damage.DamageType.IN_WALL) {
			return sources.inWall();
		} else if(damageType == org.bukkit.damage.DamageType.CRAMMING) {
			return sources.cramming();
		} else if(damageType == org.bukkit.damage.DamageType.DROWN) {
			return sources.drown();
		} else if(damageType == org.bukkit.damage.DamageType.STARVE) {
			return sources.starve();
		} else if(damageType == org.bukkit.damage.DamageType.CACTUS) {
			return sources.cactus();
		} else if(damageType == org.bukkit.damage.DamageType.FALL) {
			return sources.fall();
		} else if(damageType == org.bukkit.damage.DamageType.FLY_INTO_WALL) {
			return sources.flyIntoWall();
		} else if(damageType == org.bukkit.damage.DamageType.OUT_OF_WORLD) {
			return sources.fellOutOfWorld();
		} else if(damageType == org.bukkit.damage.DamageType.GENERIC) {
			return sources.generic();
		} else if(damageType == org.bukkit.damage.DamageType.MAGIC) {
			return sources.magic();
		} else if(damageType == org.bukkit.damage.DamageType.WITHER) {
			return sources.wither();
		} else if(damageType == org.bukkit.damage.DamageType.DRAGON_BREATH) {
			return sources.dragonBreath();
		} else if(damageType == org.bukkit.damage.DamageType.DRY_OUT) {
			return sources.dryOut();
		} else if(damageType == org.bukkit.damage.DamageType.SWEET_BERRY_BUSH) {
			return sources.sweetBerryBush();
		} else if(damageType == org.bukkit.damage.DamageType.FREEZE) {
			return sources.freeze();
		} else if(damageType == org.bukkit.damage.DamageType.STALAGMITE) {
			return sources.stalagmite();
		} else if(damageType == org.bukkit.damage.DamageType.OUTSIDE_BORDER) {
			return sources.outOfBorder();
		} else if(damageType == org.bukkit.damage.DamageType.GENERIC_KILL) {
			return sources.genericKill();
		} else if(damageType == org.bukkit.damage.DamageType.ENDER_PEARL) {
			return sources.enderPearl();
		} else if(damageType == org.bukkit.damage.DamageType.SPIT) {
			if(nmsDirectEntity instanceof net.minecraft.world.entity.projectile.LlamaSpit spit
					&& nmsCausingEntity instanceof net.minecraft.world.entity.LivingEntity living) {
				return sources.spit(spit, living);
			}
			return sources.generic();
		} else if(damageType == org.bukkit.damage.DamageType.SPEAR) {
			try {
				java.lang.reflect.Method sourceMethod = DamageSources.class.getDeclaredMethod("source",
						net.minecraft.resources.ResourceKey.class,
						net.minecraft.world.entity.Entity.class,
						net.minecraft.world.entity.Entity.class);
				sourceMethod.setAccessible(true);
				return (DamageSource) sourceMethod.invoke(sources,
						net.minecraft.world.damagesource.DamageTypes.SPEAR, nmsDirectEntity, nmsCausingEntity);
			} catch(Exception ex) {
				ex.printStackTrace();
				return sources.generic();
			}
		}
		// Fallback for any unmapped damage types
		else {
			System.err.println("Unmapped damage type: " + damageType);
			System.err.println("Causing entity: " + (causingEntity != null ? causingEntity.getType() : "null"));
			System.err.println("Direct entity: " + (directEntity != null ? directEntity.getType() : "null"));
			return sources.generic();
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof EnderCrystal crystal && crystal.getScoreboardTags().contains("SkyblockBoss")) {
			e.setCancelled(true);
			if(e.getDamager() instanceof Player p) {
				crystal.remove();
				p.addScoreboardTag("HasCrystal");
				p.sendMessage(Utils.msg("<yellow>You have picked up an Energy Crystal!"));
			}

		} else if(e.getEntity() instanceof LivingEntity entity) {
			e.setCancelled(true);
			// Wither skulls are hurting projectiles: cancelling their damage event makes vanilla DEFLECT
			// them (they visibly bounce off) instead of consuming them. Remove the skull on any entity
			// hit so it disappears like a normal impact; the custom damage below still lands.
			if(e.getDamager() instanceof org.bukkit.entity.WitherSkull skull) {
				skull.remove();
			}
			if(!entity.isDead()) {
				// Prevent boss-on-boss friendly fire (e.g. Sadan golems vs terracottas, giants vs giants)
				if(e.getDamager() instanceof Mob damagerMob && entity instanceof Mob entityMob
						&& damagerMob.getScoreboardTags().contains("SkyblockBoss")
						&& entityMob.getScoreboardTags().contains("SkyblockBoss")) {
					damagerMob.setTarget(Utils.getNearestPlayer(damagerMob));
					entityMob.setTarget(Utils.getNearestPlayer(entityMob));
					return;
				}
				DamageType type;
				switch(e.getCause()) {
					case ENTITY_ATTACK, ENTITY_EXPLOSION, THORNS -> type = DamageType.MELEE;
					case PROJECTILE, SONIC_BOOM -> type = DamageType.RANGED;
					case DRAGON_BREATH, MAGIC -> type = DamageType.MAGIC;
					case FALLING_BLOCK -> type = DamageType.ENVIRONMENTAL;
					case LIGHTNING -> type = DamageType.IFRAME_ENVIRONMENTAL;
					default -> {
						return;
					}
				}

				// A blow the PvP layer suppresses (Free-For-All safezone, duel countdown, an outsider
				// interfering in a duel) never lands - customMobs stops at the same shouldBlock check
				// below - so it must not pay out either: no hit against the victim's stats (and no
				// intelligence, which dealDamage grants). Always false off the pvp server / with PvP disabled.
				boolean pvpBlocked = pvp.PvpHooks.shouldBlock(entity, e.getDamager());

				if(entity.getNoDamageTicks() == 0 || e.getDamager() instanceof AbstractArrow) {
					Entity damager = e.getDamager();

					// Normalize custom boss damage to remove vanilla difficulty scaling
					// Easy = 0.5x, Normal = 1x, Hard = 1.5x
					if(damager instanceof Mob && damager.getScoreboardTags().contains("SkyblockBoss") && (type == DamageType.MELEE || type == DamageType.RANGED)) {
						Difficulty difficulty = damager.getWorld().getDifficulty();
						double difficultyMultiplier = switch(difficulty) {
							case EASY -> 0.5;
							case HARD -> 1.5;
							default -> 1.0;
						};
						if(difficultyMultiplier != 1.0) {
							e.setDamage(e.getDamage() / difficultyMultiplier);
						}
					}

					// Melee blows are recomputed here rather than taken as vanilla left them: vanilla's crit
					// multiplies only the attribute damage and refuses to happen while sprinting, and its
					// Sharpness/Smite/Bane numbers are not the ones this plugin wants.  See rebuildMelee.
					// ENTITY_ATTACK and not DamageType.MELEE, which also covers thorns and explosions - this
					// is about a swing, and neither of those is one.
					boolean crit = false;
					if(e.getCause() == DamageCause.ENTITY_ATTACK && damager instanceof LivingEntity attacker) {
						crit = damager instanceof Player p && canCrit(p, entity);
						e.setDamage(rebuildMelee(attacker, entity, e.getDamage(), crit));
					}

					// The crit goes on the DamageData, not back through a second getFallDistance() test: it
					// is already priced into the damage above, and the particles, the sound and the PvP hit
					// stats must all name the same blow.
					DamageData data = new DamageData(e);
					data.isCrit = crit;
					customMobs(entity, damager, e.getDamage(), type, data);
				} else if(!pvpBlocked && type == DamageType.MELEE && e.getDamager() instanceof Player) {
					// Melee blow connected but the victim's i-frames negate it: it deals no damage and
					// never reaches dealDamage, yet must still count toward PvP "total hits" as an
					// i-frame hit (never a crit). Inert off the pvp server / outside a duel (PvpHooks gates it).
					pvp.PvpHooks.trackHit(entity, e.getDamager(), 0.0, false, false, true);
				}
			}
		}
	}

	private final Map<LivingEntity, Long> noDamageTimes = new WeakHashMap<>();

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof LivingEntity entity) {
			e.setCancelled(true);
			// Enderman bosses are immune to water: skip water/rain (drown) damage entirely. The teleport
			// side is handled in StopBossesTeleporting.
			if(entity instanceof Enderman && entity.getScoreboardTags().contains("SkyblockBoss") && e.getCause() == DamageCause.DROWNING) {
				return;
			}
			DamageType type;
			switch(e.getCause()) {
				case BLOCK_EXPLOSION -> type = DamageType.MELEE;
				case POISON, WITHER -> type = DamageType.MAGIC;
				case CAMPFIRE, CONTACT, CRAMMING, DROWNING, DRYOUT, FIRE, FIRE_TICK, FREEZE, HOT_FLOOR, LAVA,
				     MELTING, STARVATION, SUFFOCATION -> type = DamageType.ENVIRONMENTAL;
				case CUSTOM -> type = DamageType.IFRAME_ENVIRONMENTAL;
				case FALL, FLY_INTO_WALL -> type = DamageType.FALL;
				case KILL, SUICIDE, VOID, WORLD_BORDER -> type = DamageType.LETHAL_ABSOLUTE;
				default -> {
					return;
				}
			}

			double damage = e.getDamage();
			if(type == DamageType.FALL) {
				damage--;
			}

			long currentTime = System.currentTimeMillis();
			if(!noDamageTimes.containsKey(entity)) {
				noDamageTimes.put(entity, 0L);
			}
			long lastDamageTime = noDamageTimes.get(entity);

			if(currentTime - lastDamageTime > 490 || e.getCause().equals(DamageCause.KILL)) {
				customMobs(entity, null, damage, type, new DamageData(e));
				noDamageTimes.put(entity, currentTime);
			}

			if(entity.isDead()) {
				try {
					entity.remove();
				} catch(Exception exception) {
					// nothing
				}
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		noDamageTimes.remove(e.getEntity());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(nextDeathMessage != null) {
			e.deathMessage(nextDeathMessage);
			nextDeathMessage = null;
		} else {
			e.deathMessage(null);
		}
	}
}