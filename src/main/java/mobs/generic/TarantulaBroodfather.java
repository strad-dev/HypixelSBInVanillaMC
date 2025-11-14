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

import java.util.Objects;

import static misc.Utils.teleport;

public class TarantulaBroodfather implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		Spider spider;
		if(e instanceof Spider) {
			spider = (Spider) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Tarantula Broodfather" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		spider.getAttribute(Attribute.MAX_HEALTH).setBaseValue(50.0);
		spider.setHealth(50.0);
		spider.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.5);
		spider.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(5.0);
		spider.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		spider.setTarget(Utils.getNearestPlayer(spider));
		spider.setCustomNameVisible(true);
		spider.addScoreboardTag("SkyblockBoss");
		spider.addScoreboardTag("TarantulaBroodfather");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Spider Relic draws the attention of the Tarantula Broodfather!");
		Bukkit.getLogger().info(p.getName() + " has summoned the Tarantula Broodfather.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		spider.setPersistent(true);
		spider.setRemoveWhenFarAway(false);
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(damager instanceof LivingEntity livingEntity) {
			CustomDamage.customMobs(livingEntity, damagee, 1, DamageType.MAGIC);
		}
		if(originalDamage > 5) {
			double finalDamage = 5 + (originalDamage - 5) / 5;
			if(finalDamage < damagee.getHealth()) {
				teleport(damagee, 12);
			}
			CustomDamage.calculateFinalDamage(damagee, damager, finalDamage, type);
			return false;
		}
		if(originalDamage < damagee.getHealth()) {
			teleport(damagee, 12);
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}