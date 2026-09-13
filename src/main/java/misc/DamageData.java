package misc;

import net.minecraft.world.entity.LightningBolt;
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

	public DamageData(EntityDamageByEntityEvent e) {
		this.originalDamage = e.getDamage();
		this.e = e;
		this.isBlocking = e.getEntity() instanceof Player p && p.isBlocking();
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