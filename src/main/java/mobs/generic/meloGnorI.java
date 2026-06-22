package mobs.generic;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.util.Random;

public class meloGnorI implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		e.setCanPickupItems(false);
		IronGolem ironGolem;
		if(e instanceof IronGolem) {
			ironGolem = (IronGolem) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = "<gold><bold>﴾ <red><bold>meloG norI<gold><bold> ﴿";
		ironGolem.getAttribute(Attribute.MAX_HEALTH).setBaseValue(50.0);
		ironGolem.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		ironGolem.setTarget(p);
		ironGolem.setHealth(50.0);
		ironGolem.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(25.0);
		ironGolem.setCustomNameVisible(true);
		ironGolem.addScoreboardTag("SkyblockBoss");
		ironGolem.addScoreboardTag("meloGnorI");
		p.sendMessage(Utils.msg("<red><bold>The Antimatter has done strange things to this Iron Golem..."));
		Bukkit.getLogger().info(p.getName() + " has summoned the meloG norI.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		ironGolem.setPersistent(true);
		ironGolem.setRemoveWhenFarAway(false);
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(type == DamageType.MELEE) {
			if(damager instanceof LivingEntity entity1) {
				if(originalDamage > 5) {
					if(damagee.getHealth() + ((originalDamage - 5) / 2) > 50) {
						damagee.setHealth(50);
						CustomDamage.customMobs(entity1, damagee, (originalDamage - 5) / 2, DamageType.MELEE); // damager takes 50% of their original damage, -5
						damager.sendMessage(Utils.msg("<red><bold>You have done too much damage to the meloG norI!\nIt is at full health and has REFLECTED " + (originalDamage - 5) / 2 + " Damage back to you!"));
					} else {
						damagee.setHealth(damagee.getHealth() + (originalDamage - 5) / 2);
						Utils.changeName(damagee);
						damager.sendMessage(Utils.msg("<red><bold>You have done too much damage to the meloG norI!\nIt has HEALED ITSELF by " + (originalDamage - 5) / 2 + " HP!"));
					}
					damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
					damagee.setNoDamageTicks(9);
					return false;
				} else {
					Random random = new Random();
					if(random.nextDouble() < 0.15) {
						CustomDamage.customMobs(entity1, damagee, 20, DamageType.MELEE);
						damagee.swingMainHand();
						damagee.swingOffHand();
						damager.sendMessage(Utils.msg("<red><bold>The meloG norI becomes enraged and deals extra damage to you!"));
					} else if(random.nextDouble() < 0.25) {
						damagee.teleport(damager);
						damager.sendMessage(Utils.msg("<red><bold>The meloG norI's unstableness has caused it to teleport to you!"));
					}
					return true;
				}
			}
		} else {
			if(damager instanceof Player p) {
				p.showTitle(Title.title(Utils.msg(""), Utils.msg("<yellow>You cannot deal " + DamageType.toString(type) + " to the meloG norI."), Title.Times.times(Duration.ZERO, Duration.ofMillis(20L * 50L), Duration.ZERO)));
			}
			damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
		}
		return false;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}
