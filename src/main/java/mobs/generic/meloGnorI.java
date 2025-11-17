package mobs.generic;

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

public class meloGnorI implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		IronGolem ironGolem;
		if(e instanceof IronGolem) {
			ironGolem = (IronGolem) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "meloG norI" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		ironGolem.getAttribute(Attribute.MAX_HEALTH).setBaseValue(150.0);
		ironGolem.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		ironGolem.setTarget(Utils.getNearestPlayer(ironGolem));
		ironGolem.setHealth(150.0);
		ironGolem.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(25.0);
		ironGolem.setCustomNameVisible(true);
		ironGolem.addScoreboardTag("SkyblockBoss");
		ironGolem.addScoreboardTag("meloGnorI");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Antimatter has done strange things to this Iron Golem...");
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
				if(originalDamage > 10.0) {
					if(damagee.getHealth() + ((originalDamage - 10.0) / 2) > 150) {
						damagee.setHealth(150);
						CustomDamage.calculateFinalDamage(entity1, damagee, (originalDamage - 10) / 2, DamageType.MELEE); // damager takes 50% of their original damage, -10
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You have done too much damage to the meloG norI!\nIt is at full health and has REFLECTED " + (originalDamage - 10) / 2 + " Damage back to you!");
					} else {
						damagee.setHealth(damagee.getHealth() + (originalDamage - 10.0) / 2);
						Utils.changeName(damagee);
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You have done too much damage to the meloG norI!\nIt has HEALED ITSELF by " + (originalDamage - 10.0) / 2 + " HP!");
					}
					damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
					damagee.setNoDamageTicks(9);
					return false;
				} else {
					Random random = new Random();
					if(random.nextDouble() < 0.15) {
						CustomDamage.calculateFinalDamage(entity1, damagee, 20, DamageType.MELEE);
						damagee.swingMainHand();
						damagee.swingOffHand();
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The meloG norI becomes enraged and deals extra damage to you!");
					} else if(random.nextDouble() < 0.25) {
						damagee.teleport(damager);
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The meloG norI's unstableness has caused it to teleport to you!");
					}
					return true;
				}
			}
		} else {
			if(damager instanceof Player p) {
				p.sendTitle("", ChatColor.YELLOW + "You cannot deal " + DamageType.toString(type) + " to the meloG norI.", 0, 20, 0);
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
