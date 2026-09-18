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
	// Was this melee blow a critical hit?  Decided ONCE, at the damage event, by CustomDamage.canCrit, and
	// carried from there instead of being re-derived: the damage number in originalDamage already has the
	// 1.5x in it, and the particles, the sound and the PvP hit stats all have to agree with it.  Re-asking
	// later would not, either - the mace branch in dealDamage zeroes the fall distance the answer is built on.
	public boolean isCrit = false;
	/**
	 * How much damage this blow actually LANDED, written by {@code CustomDamage.dealDamage} for an ability
	 * that wants to report its own work. Everything the pipeline does to the figure is already in it -
	 * armour, resistance, a shield, the Ice Spray modifiers - and absorption counts, since a shielded
	 * target did take the hit.
	 *
	 * <p><b>Overkill is included</b>, so it is the damage dealt and not the health removed: pass a blow of
	 * 40 into a mob with 2 HP left and this reads 40. A blow that never landed at all (i-framed, dead,
	 * soaked to nothing, suppressed by the PvP layer) leaves it 0, which is how a caller tells the
	 * difference. Reading a target's health either side of the call cannot do any of this.
	 */
	public double damageDealt = 0;
	/**
	 * Where the blow came FROM, which is what a shield's blocking cone is measured against. Vanilla's
	 * {@code DamageSource.getSourcePosition()}, so for a projectile it is the ARROW's position and not the
	 * shooter's - by the time {@code CustomDamage.customMobs} reaches the shield it has already rewritten
	 * {@code damager} to the shooter, and an arrow that curved in, or a shooter who has since moved, are
	 * exactly the cases the two disagree on. Null when there is no attacker to measure from.
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