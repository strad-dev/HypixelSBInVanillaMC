package misc;

import net.minecraft.world.entity.LightningBolt;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftLightningStrike;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class DamageData {
	public EntityDamageEvent e;
	public boolean isBlocking;
	public boolean flamingArrow = false;
	public int punchArrow = 0;
	public boolean isTermArrow = false;
	public double originalDamage;
	public boolean lightningInvolved = false;
	public LightningBolt lightningBolt = null;
	public boolean isTridentAttack = false;
	public boolean tridentChanneling = false;
	public Trident trident = null;
	// Melee crit? Decided ONCE at the damage event by CustomDamage.canCrit and carried, not re-derived:
	// originalDamage already has the 1.5x, and particles, sound and PvP stats must agree. Re-asking later
	// wouldn't: the mace branch in dealDamage zeroes the fall distance it's built on.
	public boolean isCrit = false;
	/**
	 * Damage this blow actually LANDED, written by {@code CustomDamage.dealDamage} for abilities that report their
	 * own work. Includes everything the pipeline did (armour, resistance, shield, Ice Spray) and absorption.
	 *
	 * <p><b>Overkill included</b>: damage dealt, not health removed, so 40 into a 2 HP mob reads 40. A blow that
	 * never landed (i-framed, dead, soaked to 0, PvP-suppressed) leaves it 0. Reading health before/after can't do this.
	 */
	public double damageDealt = 0;
	/**
	 * Where the blow came FROM, for the shield's blocking cone. Vanilla's {@code DamageSource.getSourcePosition()},
	 * so for a projectile it's the ARROW's position: {@code CustomDamage.customMobs} has already rewritten
	 * {@code damager} to the shooter, and a curved arrow or a shooter who moved is where they differ. Null when
	 * there's no attacker.
	 */
	public Location sourcePosition = null;

	public DamageData(EntityDamageByEntityEvent e) {
		this.originalDamage = e.getDamage();
		this.e = e;
		this.isBlocking = e.getEntity() instanceof Player p && p.isBlocking();
		this.sourcePosition = e.getDamager().getLocation();
		if(e.getDamager() instanceof Projectile projectile) {
			// stop stupidly annoying arrows
			if(projectile instanceof Trident temp) {
				this.isTridentAttack = true;
				this.trident = temp;
				ItemStack tridentItem = trident.getItem();
				if(tridentItem.containsEnchantment(Enchantment.CHANNELING)) {
					this.tridentChanneling = true;

					// Check if conditions are right for channeling
					LivingEntity target = (LivingEntity) e.getEntity();
					World world = target.getWorld();
					if(world.hasStorm() && Utils.underOpenSky(target.getLocation())) {
						// Strike lightning
						LightningStrike lightning = world.strikeLightning(target.getLocation());
						this.lightningInvolved = true;
						this.lightningBolt = ((CraftLightningStrike) lightning).getHandle();
					}
				}
			} else //noinspection ConstantValue
				if(projectile instanceof AbstractArrow arrow && arrow.getWeapon() != null) {
					if(arrow.getWeapon().containsEnchantment(Enchantment.FLAME)) {
						this.flamingArrow = true;
					}

					if(arrow.getWeapon().containsEnchantment(Enchantment.PUNCH)) {
						this.punchArrow = arrow.getWeapon().getEnchantmentLevel(Enchantment.PUNCH);
					}

					if(arrow.getScoreboardTags().contains("TerminatorArrow")) {
						this.isTermArrow = true;
						this.originalDamage = arrow.getDamage();
					}
				}
		}

		if(e.getDamager() instanceof LightningStrike lightning) {
			this.lightningInvolved = true;
			this.lightningBolt = ((CraftLightningStrike) lightning).getHandle();
		}
	}

	public DamageData(EntityDamageEvent e) {
		this.originalDamage = e.getDamage();
		this.e = e;
		this.isBlocking = e.getEntity() instanceof Player p && p.isBlocking();
	}

	public DamageData(LivingEntity damagee, Entity damager, double originalDamage) {
		this.originalDamage = originalDamage;
		this.isBlocking = damagee instanceof Player p && p.isBlocking();
		this.sourcePosition = damager == null ? null : damager.getLocation();
		if(damager instanceof Projectile projectile) {
			// stop stupidly annoying arrows
			if(projectile instanceof Trident temp) {
				this.isTridentAttack = true;
				this.trident = temp;
				ItemStack tridentItem = trident.getItem();
				if(tridentItem.containsEnchantment(Enchantment.CHANNELING)) {
					this.tridentChanneling = true;

					// Check if conditions are right for channeling
					World world = damagee.getWorld();
					if(world.hasStorm() && Utils.underOpenSky(damagee.getLocation())) {
						// Strike lightning
						LightningStrike lightning = world.strikeLightning(damagee.getLocation());
						this.lightningInvolved = true;
						this.lightningBolt = ((CraftLightningStrike) lightning).getHandle();
						damager.getWorld().playSound(damager, Sound.ITEM_TRIDENT_THUNDER, 1f, 1f);
					}
				}
			} else if(projectile instanceof AbstractArrow arrow) {
				if(arrow.getWeapon().containsEnchantment(Enchantment.FLAME)) {
					this.flamingArrow = true;
				}

				if(arrow.getWeapon().containsEnchantment(Enchantment.PUNCH)) {
					this.punchArrow = arrow.getWeapon().getEnchantmentLevel(Enchantment.PUNCH);
				}

				if(arrow.getScoreboardTags().contains("TerminatorArrow")) {
					this.isTermArrow = true;
					this.originalDamage = arrow.getDamage();
				}
			}
		}

		if(damager instanceof LightningStrike lightning) {
			this.lightningInvolved = true;
			this.lightningBolt = ((CraftLightningStrike) lightning).getHandle();
		}
	}
}