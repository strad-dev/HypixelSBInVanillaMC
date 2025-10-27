package mobs.withers;

import listeners.CustomMobs;
import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;

import java.util.Random;

import static listeners.CustomDamage.calculateFinalDamage;
import static listeners.CustomMobs.spawnLightning;
import static misc.Utils.teleport;

public class WitherKing implements CustomWither {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "Wither-King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿";

		e.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(2.0);
		e.getAttribute(Attribute.FLYING_SPEED).setBaseValue(2.0);
		e.getAttribute(Attribute.SCALE).setBaseValue(2.0);
		e.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(13.0);
		e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(600.0);
		e.setHealth(600.0);
		e.addScoreboardTag("WitherKing");
		e.addScoreboardTag("WitherKing75");
		e.addScoreboardTag("WitherKing50");
		e.addScoreboardTag("WitherKing25");
		e.getWorld().setThundering(true);
		e.getWorld().setWeatherDuration(1000000);
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "A giant rumble occurs.  The ground beneath you cracks and crumbles as a mysterious being emerges.  Could it be?  No...  He had been sleeping for centuries!  How could He b- *mic cuts off*");
		Bukkit.getLogger().info("The WITHER KING has been summoned!");

		Utils.scheduleTask(() -> {
			spawnLightning(e, 128);
			e.getWorld().createExplosion(e.getLocation(), 10);

			EnderDragon dragon = (EnderDragon) e.getWorld().spawnEntity(e.getLocation().add(0, 12, 0), EntityType.ENDER_DRAGON);
			dragon.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Wither King's Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RED + "❤ " + ChatColor.YELLOW + 1024 + "/" + 1024);
			dragon.setCustomNameVisible(true);
			dragon.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
			dragon.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(17.9);
			dragon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(1024.0);
			dragon.setHealth(1024.0);
			dragon.addScoreboardTag("SkyblockBoss");
			dragon.addScoreboardTag("WitherKingDragon");
			dragon.setTarget(p);
			dragon.setPersistent(true);
			e.setRemoveWhenFarAway(false);

			p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Wither King summons His Dragon!");
			Utils.scheduleTask(() -> teleportDragon(dragon, e), 300);
		}, 200);

		return newName;
	}

	public void teleportDragon(EnderDragon dragon, Mob e) {
		if(!dragon.isDead()) {
			Player p = Utils.getNearestPlayer(e);
			dragon.teleport(p);
			p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Wither King's Dragon teleports itself to you!");
			Utils.scheduleTask(() -> teleportDragon(dragon, e), 300);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		Random random = new Random();
		if(!type.equals(DamageType.RANGED) && random.nextDouble() < 0.1 || type.equals(DamageType.RANGED) && random.nextDouble() < 0.05) {
			teleport(damagee, 16);
		}

		checkSkeletons(damagee, damager);
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.LIGHTNING_BOLT);
		calculateFinalDamage(damagee, Utils.getNearestPlayer(damagee), 6, DamageType.RANGED);
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {
		Random random = new Random();
		if(random.nextDouble() < 0.1) {
			CustomMobs.spawnLightning(skull, 64);
			Utils.scheduleTask(() -> CustomMobs.spawnLightning(skull, 64), 20L);
		}
	}

	public static void checkSkeletons(LivingEntity damagee, Entity damager) {
		if(damagee.getHealth() <= damagee.getAttribute(Attribute.MAX_HEALTH).getValue() * 0.75 && damagee.getScoreboardTags().contains("WitherKing75")) {
			damagee.removeScoreboardTag("WitherKing75");
			spawnSkeletons(damager);
		}
		if(damagee.getHealth() <= damagee.getAttribute(Attribute.MAX_HEALTH).getValue() * 0.50 && damagee.getScoreboardTags().contains("WitherKing50")) {
			damagee.removeScoreboardTag("WitherKing50");
			spawnSkeletons(damager);
		}
		if(damagee.getHealth() <= damagee.getAttribute(Attribute.MAX_HEALTH).getValue() * 0.25 && damagee.getScoreboardTags().contains("WitherKing25")) {
			damagee.removeScoreboardTag("WitherKing25");
			spawnSkeletons(damager);
		}
	}

	private static void spawnSkeletons(Entity damager) {
		if(damager instanceof LivingEntity entity) {
			Location l = entity.getLocation();
			Utils.spawnGuards(entity, 15);
			entity.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Wither King summons His Guards to defend Him!");
			l.getWorld().playSound(l, Sound.ITEM_GOAT_HORN_SOUND_2, 2.0F, 1.0F);
		}
	}
}