package mobs.hardmode.generic;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class ZealotBrusier implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		Enderman enderman;
		if(e instanceof Enderman) {
			enderman = (Enderman) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Zealot Brusier" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		enderman.getAttribute(Attribute.MAX_HEALTH).setBaseValue(650.0);
		enderman.setHealth(650.0);
		enderman.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(30.0);
		enderman.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.5);
		enderman.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		enderman.setTarget(Utils.getNearestPlayer(enderman));
		enderman.setCustomNameVisible(true);
		enderman.addScoreboardTag("SkyblockBoss");
		enderman.addScoreboardTag("ZealotBrusier");
		enderman.addScoreboardTag("HardMode");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Pearl corrupts the Enderman.  It has become a Zealot Brusier!");
		Bukkit.getLogger().info(p.getName() + " has summoned the Voidgloom Seraph.");
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
				CustomDamage.calculateFinalDamage(entity1, damagee, 20, DamageType.MELEE);
				damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Zealot Brusier's Dark Magic has caused you to teleport to it!  It also deals 20 damage to you!");
			}
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}