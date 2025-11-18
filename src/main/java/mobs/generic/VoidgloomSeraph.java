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

public class VoidgloomSeraph implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		Enderman enderman;
		if(e instanceof Enderman) {
			enderman = (Enderman) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Voidgloom Seraph" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		enderman.getAttribute(Attribute.MAX_HEALTH).setBaseValue(250.0);
		enderman.setHealth(250.0);
		enderman.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(25.0);
		enderman.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.4);
		enderman.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		enderman.setTarget(Utils.getNearestPlayer(enderman));
		enderman.setCustomNameVisible(true);
		enderman.addScoreboardTag("SkyblockBoss");
		enderman.addScoreboardTag("VoidgloomSeraph");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "From the ashes of the Superior Dragon rises the terrifying Voidgloom Seraph!");
		Bukkit.getLogger().info(p.getName() + " has summoned the Voidgloom Seraph.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		enderman.setPersistent(true);
		enderman.setRemoveWhenFarAway(false);

		Utils.scheduleTask(() -> dissonance(enderman), 20);

		return newName;
	}

	private static void dissonance(Enderman voidgloom) {
		if(!voidgloom.isDead()) {
			Utils.applyToAllNearbyPlayers(voidgloom, 16, p -> CustomDamage.customMobs(p, voidgloom, 12, DamageType.MELEE));
			Utils.scheduleTask(() -> dissonance(voidgloom), 20);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		Random random = new Random();
		if(random.nextDouble() < 0.1) {
			damagee.teleport(Utils.randomLocation(damager.getLocation(), 3));
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}
