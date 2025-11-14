package mobs.hardmode.withers;

import listeners.CustomMobs;
import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import mobs.withers.CustomWither;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static listeners.CustomDamage.customMobs;
import static misc.Utils.teleport;

public class Storm implements CustomWither {
	private static final String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Storm" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";

	@Override
	public String onSpawn(Player p, Mob e) {
		Wither wither;
		if(e instanceof Wither) {
			wither = (Wither) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		//noinspection DuplicatedCode
		List<EntityType> immune = new ArrayList<>();
		immune.add(EntityType.WITHER_SKELETON);
		Utils.spawnTNT(wither, wither.getLocation(), 0, 32, 50, immune);
		Utils.playGlobalSound(Sound.ENTITY_WITHER_SPAWN);

		wither.getAttribute(Attribute.MAX_HEALTH).setBaseValue(1000.0);
		wither.setHealth(1000.0);
		wither.setAI(false);
		wither.addScoreboardTag("Storm");
		wither.addScoreboardTag("HardMode");
		wither.addScoreboardTag("SkyblockBoss");
		wither.addScoreboardTag("Invulnerable");
		wither.addScoreboardTag("Survival1");
		wither.addScoreboardTag("Survival2Trigger");
		wither.setPersistent(true);
		wither.setRemoveWhenFarAway(false);
		wither.setCustomName(name + " " + ChatColor.RESET + ChatColor.RED + "❤" + ChatColor.YELLOW + " a");
		Utils.changeName(wither);

		spawnGuards(wither);
		spawnLightning(wither);
		for(int i = 0; i < 600; i += 15) {
			spamSkulls(wither, p, i);
		}
		Utils.scheduleTask(() -> {
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Are you enjoying the party?  I sure am while watching you suffer!");
		}, 200);
		Utils.scheduleTask(() -> {
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": When I'm not making lightning, I love creating explosions!");
		}, 480);
		Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "5", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BOOM!", 0, 21, 0)), 500);
		Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "4", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BOOM!", 0, 21, 0)), 520);
		Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "3", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BOOM!", 0, 21, 0)), 540);
		Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "2", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BOOM!", 0, 21, 0)), 560);
		Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "1", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BOOM!", 0, 21, 0)), 580);
		Utils.scheduleTask(() -> {
			Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "BOOM!", "", 0, 21, 0));
			Utils.spawnTNT(wither, wither.getLocation(), 0, 64, 200, immune);
			wither.removeScoreboardTag("Survival1");
			wither.removeScoreboardTag("Invulnerable");
			wither.setAI(true);
		}, 600);

		return name;
	}

	private void spawnGuards(Wither wither) {
		if(!wither.isDead() && !wither.getScoreboardTags().contains("Survival2") && !wither.getScoreboardTags().contains("Dead")) {
			if(wither.getScoreboardTags().contains("Survival1")) {
				Utils.spawnGuards(wither, 2);
				Utils.scheduleTask(() -> spawnGuards(wither), 201);
			} else {
				Utils.spawnGuards(wither, 2);
				Utils.scheduleTask(() -> spawnGuards(wither), 301);
			}
		}
	}

	private void spawnMoreGuards(Wither wither) {
		if(!wither.isDead() && !wither.getScoreboardTags().contains("Dead")) {
			if(wither.getScoreboardTags().contains("Survival2")) {
				Utils.spawnGuards(wither, 3);
				Utils.scheduleTask(() -> spawnMoreGuards(wither), 151);
			} else {
				Utils.spawnGuards(wither, 2);
				Utils.scheduleTask(() -> spawnMoreGuards(wither), 301);
			}
		}
	}

	private void spawnLightning(Wither wither) {
		if(!wither.isDead() && !wither.getScoreboardTags().contains("Survival2") && !wither.getScoreboardTags().contains("Dead")) {
			if(wither.getScoreboardTags().contains("Survival1")) {
				CustomMobs.spawnLightning(wither, 24);
				Utils.scheduleTask(() -> spawnLightning(wither), 100);
			} else {
				CustomMobs.spawnLightning(wither, 16);
				Utils.scheduleTask(() -> spawnLightning(wither), 200);
			}
		}
	}

	private void spawnMoreLightning(Wither wither) {
		if(!wither.isDead() && !wither.getScoreboardTags().contains("Dead")) {
			if(wither.getScoreboardTags().contains("Survival2")) {
				CustomMobs.spawnLightning(wither, 32);
				Utils.scheduleTask(() -> spawnMoreLightning(wither), 60);
			} else {
				CustomMobs.spawnLightning(wither, 16);
				Utils.scheduleTask(() -> spawnMoreLightning(wither), 200);
			}
		}
	}

	private void spamSkulls(Wither wither, Player p, int i) {
		Utils.scheduleTask(() -> {
			if(!wither.isDead()) {
				Vector directionMain = p.getLocation().toVector().subtract(wither.getLocation().toVector()).normalize();
				Vector directionLeft = p.getLocation().toVector().subtract(wither.getLocation().add(1, 0, 0).toVector()).normalize();
				Vector directionRight = p.getLocation().toVector().subtract(wither.getLocation().add(-1, 0, 0).toVector()).normalize();
				WitherSkull skullMain = (WitherSkull) wither.getWorld().spawnEntity(wither.getLocation().add(0, 1.5, 0), EntityType.WITHER_SKULL);
				skullMain.setDirection(directionMain);
				skullMain.setShooter(wither);
				WitherSkull skullLeft = (WitherSkull) wither.getWorld().spawnEntity(wither.getLocation().add(1, 1.5, 0), EntityType.WITHER_SKULL);
				skullLeft.setDirection(directionLeft);
				skullLeft.setShooter(wither);
				WitherSkull skullRight = (WitherSkull) wither.getWorld().spawnEntity(wither.getLocation().add(-1, 1.5, 0), EntityType.WITHER_SKULL);
				skullRight.setDirection(directionRight);
				skullRight.setShooter(wither);
				Utils.playGlobalSound(Sound.ENTITY_WITHER_SHOOT, 0.75f, 1.0f);
			}
		}, i);
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		CustomMobs.updateWitherLordFight(true);

		double hp = damagee.getHealth();

		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			Utils.changeName(damagee);
			if(damagee.getScoreboardTags().contains("Dead")) {
				if(damager instanceof Player p) {
					p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "IMMUNE", ChatColor.YELLOW + "You cannot damage Storm!", 0, 20, 0);
				}
				damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
			}
			return false;
		} else if(damagee.getScoreboardTags().contains("Survival2Trigger") && hp - originalDamage < 500) {
			Utils.changeName(damagee);
			damagee.setAI(false);
			damagee.removeScoreboardTag("Survival2Trigger");
			damagee.addScoreboardTag("Survival2");
			damagee.addScoreboardTag("Invulnerable");
			teleport(damagee, 0);

			Player p = Utils.getNearestPlayer(damagee);
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": You think you're funny?  Try surviving this!");

			spawnMoreGuards((Wither) damagee);
			spawnMoreLightning((Wither) damagee);
			for(int i = 0; i < 600; i += 10) {
				spamSkulls((Wither) damagee, p, i);
			}
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Still alive?  You won’t survive what’s coming!");
			}, 200);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I wasn't giving my all in that last explosion.  Good luck getting past this one!");
			}, 520);
			Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "3", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BIGGER BOOM!", 0, 21, 0)), 540);
			Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "2", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BIGGER BOOM!", 0, 21, 0)), 560);
			Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "1", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BIGGER BOOM!", 0, 21, 0)), 580);
			Utils.scheduleTask(() -> {
				Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "BIGGER BOOM!", "", 0, 21, 0));
				List<EntityType> immune = new ArrayList<>();
				immune.add(EntityType.WITHER_SKELETON);
				Utils.spawnTNT(damagee, damagee.getLocation(), 0, 64, 300, immune);
				damagee.removeScoreboardTag("Survival2");
				damagee.removeScoreboardTag("Invulnerable");
				damagee.setAI(true);
			}, 600);
			damagee.setHealth(500.0);
			return false;
		} else if(hp - originalDamage < 1) {
			damagee.addScoreboardTag("Invulnerable");
			damagee.addScoreboardTag("Dead");
			damagee.setHealth(1.0);
			damagee.setSilent(true);
			damagee.setAI(false);
			Utils.changeName(damagee);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I knew I should have prepared better.");
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Have fun dealing with the others.");
			}, 60);
			Utils.scheduleTask(() -> {
				damagee.remove();
				Utils.playGlobalSound(Sound.ENTITY_WITHER_DEATH);
				Utils.spawnTNT(damagee, damagee.getLocation(), 0, 32, 50, new ArrayList<>());
			}, 100);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Goldor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I hear some vermin prowling around my territory.");
			}, 240);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Goldor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I hope you came prepared for a long fight!");
			}, 300);
			Utils.scheduleTask(() -> {
				Wither wither = (Wither) damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.WITHER);
				CustomMob.getMob("Goldor", true).onSpawn(Utils.getNearestPlayer(damagee), wither);
			}, 340);
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(damagee instanceof Wither || damagee instanceof WitherSkeleton) {
			return false;
		}
		if(damager.getScoreboardTags().contains("Survival1")) {
			customMobs(Utils.getNearestPlayer(damagee), damagee, 12, DamageType.RANGED);
		} else if(damager.getScoreboardTags().contains("Survival2")) {
			customMobs(Utils.getNearestPlayer(damagee), damagee, 18, DamageType.RANGED);
		}
		damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.LIGHTNING_BOLT);
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {
	}
}