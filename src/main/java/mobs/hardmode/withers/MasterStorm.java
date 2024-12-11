package mobs.hardmode.withers;

import listeners.CustomMobs;
import listeners.DamageType;
import misc.Plugin;
import misc.PluginUtils;
import mobs.withers.CustomWither;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static listeners.CustomDamage.calculateFinalDamage;

public class MasterStorm implements CustomWither {
	private static final String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Storm" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD;

	@Override
	public String onSpawn(Player p, Mob e) {
		List<EntityType> immune = new ArrayList<>();
		immune.add(EntityType.WITHER_SKELETON);
		PluginUtils.spawnTNT(e, e.getLocation(), 0, 64, 300, immune);

		e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(500.0);
		e.setHealth(500.0);
		e.setAI(false);
		e.addScoreboardTag("Storm");
		e.addScoreboardTag("HardMode");
		e.addScoreboardTag("Invulnerable");
		e.addScoreboardTag("Survival1");
		e.addScoreboardTag("Survival2Trigger");

		spawnGuards(e);
		spawnLightning(e);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(name + ": When I'm not making lightning, I love creating explosions!"), 480);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("5", "BOOM!", 0, 21, 0)), 500);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("4", "BOOM!", 0, 21, 0)), 520);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("3", "BOOM!", 0, 21, 0)), 540);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("2", "BOOM!", 0, 21, 0)), 560);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("1", "BOOM!", 0, 21, 0)), 580);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("BOOM!", "", 0, 21, 0));
			PluginUtils.spawnTNT(e, e.getLocation(), 0, 64, 300, new ArrayList<>());
			e.removeScoreboardTag("Survival1");
			e.removeScoreboardTag("Invulnerable");
			e.setAI(true);
		}, 600);

		return name;
	}

	private void spawnGuards(LivingEntity e) {
		if(!e.isDead() && !e.getScoreboardTags().contains("Survival2")) {
			if(e.getScoreboardTags().contains("Survival1")) {
				PluginUtils.spawnGuards(e, 15);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnGuards(e), 201);
			} else {
				PluginUtils.spawnGuards(e, 10);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnGuards(e), 301);
			}
		}
	}

	private void spawnMoreGuards(LivingEntity e) {
		if(!e.isDead()) {
			if(e.getScoreboardTags().contains("Survival2")) {
				PluginUtils.spawnGuards(e, 20);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnGuards(e), 151);
			} else {
				PluginUtils.spawnGuards(e, 10);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnGuards(e), 301);
			}
		}
	}

	private void spawnLightning(LivingEntity e) {
		if(!e.isDead() && !e.getScoreboardTags().contains("Survival2")) {
			if(e.getScoreboardTags().contains("Survival1")) {
				CustomMobs.spawnLightning(e, 24);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnGuards(e), 100);
			} else {
				CustomMobs.spawnLightning(e, 16);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnGuards(e), 200);
			}
		}
	}

	private void spawnMoreLightning(LivingEntity e) {
		if(!e.isDead()) {
			if(e.getScoreboardTags().contains("Survival2")) {
				CustomMobs.spawnLightning(e, 32);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnGuards(e), 60);
			} else {
				CustomMobs.spawnLightning(e, 16);
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnGuards(e), 200);
			}
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		double hp = damagee.getHealth();

		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			return false;
		} else if(damagee.getScoreboardTags().contains("Survival2Trigger") && hp - originalDamage < 250) {
			damagee.removeScoreboardTag("Survival2Trigger");
			damagee.addScoreboardTag("Survival2");
			damagee.addScoreboardTag("Invulnerable");

			Bukkit.broadcastMessage(name + ": You think you're funny?  Try surviving this!");

			spawnMoreGuards(damagee);
			spawnMoreLightning(damagee);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(name + ": I wasn't giving my all in that last explosion.  Good luck surviving this one!"), 480);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("3", "BIGGER BOOM!", 0, 21, 0)), 540);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("2", "BIGGER BOOM!", 0, 21, 0)), 560);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("1", "BIGGER BOOM!", 0, 21, 0)), 580);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("BIGGER BOOM!", "", 0, 21, 0));
				PluginUtils.spawnTNT(damagee, damagee.getLocation(), 0, 64, 300, new ArrayList<>());
				damagee.removeScoreboardTag("Survival2");
				damagee.removeScoreboardTag("Invulnerable");
				damagee.setAI(true);
			}, 600);
		} else if(hp - originalDamage < 1) {
			damagee.addScoreboardTag("Invulnerable");
			Bukkit.broadcastMessage(name + ": I knew I should have prepared better.");
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(name + ": Have fun dealing with the others."), 50);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				damagee.remove();
				PluginUtils.spawnTNT(damagee, damagee.getLocation(), 0, 32, 50, new ArrayList<>());
			}, 100);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Goldor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I hear some vermin prowling around my territory."), 250);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Goldor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I hope you're prepared for a long fight!"), 300);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				Wither wither = (Wither) damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.WITHER);
				new MasterGoldor().onSpawn(Plugin.getNearestPlayer(damagee), wither);
			}, 320);
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(damager.getScoreboardTags().contains("Survival1")) {
			calculateFinalDamage(damagee, Plugin.getNearestPlayer(damagee), 12, DamageType.RANGED);
		} else if(damager.getScoreboardTags().contains("Survival2")) {
			calculateFinalDamage(damagee, Plugin.getNearestPlayer(damagee), 18, DamageType.RANGED);
		}
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {
		CustomMobs.spawnLightning(skull, 32);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> CustomMobs.spawnLightning(skull, 32), 12);
	}
}