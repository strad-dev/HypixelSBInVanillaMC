package mobs.generic;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class Zealot implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		e.setCanPickupItems(false);
		Enderman enderman;
		if(e instanceof Enderman) {
			enderman = (Enderman) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = "<gold><bold>﴾ <red><bold>Zealot<gold><bold> ﴿";
		enderman.getAttribute(Attribute.MAX_HEALTH).setBaseValue(130.0);
		enderman.setHealth(130.0);
		enderman.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(20.0);
		enderman.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.4);
		enderman.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		enderman.setTarget(p);
		enderman.setCustomNameVisible(true);
		enderman.addScoreboardTag("SkyblockBoss");
		enderman.addScoreboardTag("Zealot");
		p.sendMessage(Utils.msg("<red><bold>The Pearl corrupts the Enderman.  It has become a Zealot!"));
		Bukkit.getLogger().info(p.getName() + " has summoned the Zealot.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		enderman.setPersistent(true);
		enderman.setRemoveWhenFarAway(false);
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		Random random = new Random();
		if(damager instanceof LivingEntity entity1) {
			if(random.nextDouble() < 0.2) {
				damager.teleport(damagee);
				CustomDamage.customMobs(entity1, damagee, 12, DamageType.MELEE);
				damager.sendMessage(Utils.msg("<red><bold>The Zealot's Dark Magic has caused you to teleport to it!  It also deals 12 damage to you!"));
			}
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}
