package mobs.hardmode.withers;

import listeners.CustomMobs;
import listeners.DamageType;
import misc.Plugin;
import misc.PluginUtils;
import mobs.withers.CustomWither;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static listeners.CustomDamage.customMobs;
import static misc.PluginUtils.teleport;

public class MasterStorm implements CustomWither {
	private static final String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Storm" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";

	@Override
	public String onSpawn(Player p, Mob e) {
		//noinspection DuplicatedCode
		List<EntityType> immune = new ArrayList<>();
		immune.add(EntityType.WITHER_SKELETON);
		PluginUtils.spawnTNT(e, e.getLocation(), 0, 32, 50, immune);
		PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_SPAWN);

		e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(1000.0);
		e.setHealth(1000.0);
		e.setAI(false);
		e.addScoreboardTag("Storm");
		e.addScoreboardTag("HardMode");
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("Invulnerable");
		e.addScoreboardTag("Survival1");
		e.addScoreboardTag("Survival2Trigger");
		e.setPersistent(true);
		e.setRemoveWhenFarAway(false);
		e.setCustomName(name + " " + ChatColor.RESET + ChatColor.RED + "❤" + ChatColor.YELLOW + " a");
		PluginUtils.changeName(e);

		spawnGuards(e);
		spawnLightning(e);
		for(int i = 0; i < 600; i += 15) {
			spamSkulls(e, p, i);
		}
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Are you enjoying the party?  I sure am while watching you suffer!");
		}, 200);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": When I'm not making lightning, I love creating explosions!");
		}, 480);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "5", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BOOM!", 0, 21, 0)), 500);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "4", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BOOM!", 0, 21, 0)), 520);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "3", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BOOM!", 0, 21, 0)), 540);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "2", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BOOM!", 0, 21, 0)), 560);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "1", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BOOM!", 0, 21, 0)), 580);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "BOOM!", "", 0, 21, 0));
			PluginUtils.spawnTNT(e, e.getLocation(), 0, 64, 200, immune);
			e.removeScoreboardTag("Survival1");
			e.removeScoreboardTag("Invulnerable");
			e.setAI(true);
		}, 600);

		return name;
	}

	private void spawnGuards(LivingEntity e) {
		if(!e.isDead() && !e.getScoreboardTags().contains("Survival2") && !e.getScoreboardTags().contains("Dead")) {
			if(e.getScoreboardTags().contains("Survival1")) {
				PluginUtils.spawnGuards(e, 2);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnGuards(e), 201);
			} else {
				PluginUtils.spawnGuards(e, 2);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnGuards(e), 301);
			}
		}
	}

	private void spawnMoreGuards(LivingEntity e) {
		if(!e.isDead() && !e.getScoreboardTags().contains("Dead")) {
			if(e.getScoreboardTags().contains("Survival2")) {
				PluginUtils.spawnGuards(e, 3);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnMoreGuards(e), 151);
			} else {
				PluginUtils.spawnGuards(e, 2);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnMoreGuards(e), 301);
			}
		}
	}

	private void spawnLightning(LivingEntity e) {
		if(!e.isDead() && !e.getScoreboardTags().contains("Survival2") && !e.getScoreboardTags().contains("Dead")) {
			if(e.getScoreboardTags().contains("Survival1")) {
				CustomMobs.spawnLightning(e, 24);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnLightning(e), 100);
			} else {
				CustomMobs.spawnLightning(e, 16);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnLightning(e), 200);
			}
		}
	}

	private void spawnMoreLightning(LivingEntity e) {
		if(!e.isDead() && !e.getScoreboardTags().contains("Dead")) {
			if(e.getScoreboardTags().contains("Survival2")) {
				CustomMobs.spawnLightning(e, 32);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnMoreLightning(e), 60);
			} else {
				CustomMobs.spawnLightning(e, 16);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnMoreLightning(e), 200);
			}
		}
	}

	private void spamSkulls(LivingEntity damagee, Player p, int i) {
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			if(!damagee.isDead()) {
				Vector directionMain = p.getLocation().toVector().subtract(damagee.getLocation().toVector()).normalize();
				Vector directionLeft = p.getLocation().toVector().subtract(damagee.getLocation().add(1, 0, 0).toVector()).normalize();
				Vector directionRight = p.getLocation().toVector().subtract(damagee.getLocation().add(-1, 0, 0).toVector()).normalize();
				WitherSkull skullMain = (WitherSkull) damagee.getWorld().spawnEntity(damagee.getLocation().add(0, 1.5, 0), EntityType.WITHER_SKULL);
				skullMain.setDirection(directionMain);
				skullMain.setShooter(damagee);
				WitherSkull skullLeft = (WitherSkull) damagee.getWorld().spawnEntity(damagee.getLocation().add(1, 1.5, 0), EntityType.WITHER_SKULL);
				skullLeft.setDirection(directionLeft);
				skullLeft.setShooter(damagee);
				WitherSkull skullRight = (WitherSkull) damagee.getWorld().spawnEntity(damagee.getLocation().add(-1, 1.5, 0), EntityType.WITHER_SKULL);
				skullRight.setDirection(directionRight);
				skullRight.setShooter(damagee);
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_SHOOT, 0.75f, 1.0f);
			}
		}, i);
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		CustomMobs.updateWitherLordFight(true);

		double hp = damagee.getHealth();

		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			PluginUtils.changeName(damagee);
			if(damager instanceof Player p && !damagee.getScoreboardTags().contains("Dead")) {
				damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
				p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "IMMUNE", ChatColor.YELLOW + "You cannot damage Storm!", 0, 20, 0);
			}
			return false;
		} else if(damagee.getScoreboardTags().contains("Survival2Trigger") && hp - originalDamage < 500) {
			PluginUtils.changeName(damagee);
			damagee.setAI(false);
			damagee.removeScoreboardTag("Survival2Trigger");
			damagee.addScoreboardTag("Survival2");
			damagee.addScoreboardTag("Invulnerable");
			teleport(damagee, 0);

			Player p = PluginUtils.getNearestPlayer(damagee);
			PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": You think you're funny?  Try surviving this!");

			spawnMoreGuards(damagee);
			spawnMoreLightning(damagee);
			for(int i = 0; i < 600; i += 10) {
				spamSkulls(damagee, p, i);
			}
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Still alive?  You won’t survive what’s coming!");
			}, 200);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I wasn't giving my all in that last explosion.  Good luck getting past this one!");
			}, 520);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "3", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BIGGER BOOM!", 0, 21, 0)), 540);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "2", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BIGGER BOOM!", 0, 21, 0)), 560);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "1", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BIGGER BOOM!", 0, 21, 0)), 580);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "BIGGER BOOM!", "", 0, 21, 0));
				List<EntityType> immune = new ArrayList<>();
				immune.add(EntityType.WITHER_SKELETON);
				PluginUtils.spawnTNT(damagee, damagee.getLocation(), 0, 64, 300, immune);
				damagee.removeScoreboardTag("Survival2");
				damagee.removeScoreboardTag("Invulnerable");
				damagee.setAI(true);
			}, 600);
			damagee.setHealth(500.0);
			return false;
		} else if(hp - originalDamage < 1) {
			damagee.setAI(false);
			damagee.addScoreboardTag("Invulnerable");
			damagee.addScoreboardTag("Dead");
			damagee.setHealth(1.0);
			PluginUtils.changeName(damagee);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I knew I should have prepared better.");
			PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Have fun dealing with the others.");
			}, 60);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				damagee.remove();
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_DEATH);
				PluginUtils.spawnTNT(damagee, damagee.getLocation(), 0, 32, 50, new ArrayList<>());
			}, 100);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Goldor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I hear some vermin prowling around my territory.");
			}, 240);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Goldor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I hope you came prepared for a long fight!");
			}, 300);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				Wither wither = (Wither) damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.WITHER);
				new MasterGoldor().onSpawn(PluginUtils.getNearestPlayer(damagee), wither);
			}, 340);
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(damagee instanceof Wither || damagee instanceof WitherSkeleton) {
			return false;
		}
		if(damager.getScoreboardTags().contains("Survival1")) {
			Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(damager, damagee, EntityDamageEvent.DamageCause.PROJECTILE, DamageSource.builder(org.bukkit.damage.DamageType.WITHER_SKULL).build(), 18));
		} else if(damager.getScoreboardTags().contains("Survival2")) {
			Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(damager, damagee, EntityDamageEvent.DamageCause.PROJECTILE, DamageSource.builder(org.bukkit.damage.DamageType.WITHER_SKULL).build(), 18));
		}
		damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.LIGHTNING_BOLT);
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {
	}
}