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

import static misc.Utils.teleport;

public class PrimordialBroodfather implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		Spider spider;
		if(e instanceof Spider) {
			spider = (Spider) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Primordial Broodfather" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		spider.getAttribute(Attribute.MAX_HEALTH).setBaseValue(100.0);
		spider.setHealth(100.0);
		spider.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.67);
		spider.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(10.0);
		spider.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		spider.setTarget(Utils.getNearestPlayer(spider));
		spider.setCustomNameVisible(true);
		spider.addScoreboardTag("SkyblockBoss");
		spider.addScoreboardTag("PrimordialBroodfather");
		spider.addScoreboardTag("HardMode");
		spider.addScoreboardTag("50Trigger");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Spider Relic draws the attention of the Primordial Broodfather!");
		Bukkit.getLogger().info(p.getName() + " has summoned the Primordial Broodfather.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		spider.setPersistent(true);
		spider.setRemoveWhenFarAway(false);

		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		teleport(damagee, 12);
		if(damager instanceof LivingEntity livingEntity) {
			CustomDamage.customMobs(livingEntity, damagee, 1, DamageType.ABSOLUTE);
		}
		double finalDamage = originalDamage;
		if(originalDamage > 5) {
			finalDamage = 5 + (originalDamage - 5) / 10;
		}

		double health = damagee.getHealth();

		if(health - finalDamage < 0 && !damagee.isDead()) {
			damagee.remove();


		} else if(health - finalDamage < 50 && damagee.getScoreboardTags().contains("50Trigger")) {

		}

		CustomDamage.calculateFinalDamage(damagee, damager, finalDamage, type);
		return false;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}
