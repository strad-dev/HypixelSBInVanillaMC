package mobs.hardmode.withers;

import listeners.CustomDamage;
import listeners.CustomMobs;
import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import mobs.withers.CustomWither;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.entity.CraftWither;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static misc.Utils.teleport;

public class Necron implements CustomWither {
	private static final String name = "<gold><bold>﴾ <red><bold>Necron<gold><bold> ﴿";

	@Override
	public String onSpawn(Player p, Mob e) {
		e.setCanPickupItems(false);
		Wither wither;
		if(e instanceof Wither) {
			wither = (Wither) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		List<EntityType> immune = new ArrayList<>();
		immune.add(EntityType.WITHER_SKELETON);
		Utils.spawnTNT(wither, wither.getLocation(), 0, 32, 75, immune);
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);

		wither.getAttribute(Attribute.MAX_HEALTH).setBaseValue(1400.0);
		wither.setHealth(1400.0);
		wither.addScoreboardTag("Necron");
		wither.addScoreboardTag("HardMode");
		wither.addScoreboardTag("SkyblockBoss");
		wither.addScoreboardTag("1100Frenzy");
		wither.addScoreboardTag("300Frenzy");
		wither.setPersistent(true);
		wither.setRemoveWhenFarAway(false);
		Utils.changeName(wither, name);

		return name;
	}

	private void frenzy(Wither wither, int which) {
		wither.addScoreboardTag("Invulnerable");
		wither.setAI(false);
		Bukkit.getOnlinePlayers().forEach(p -> p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0)));
		teleport(wither, 16, false);
		for(int i = 0; i < 161; i += 20) {
			int finalI = i;
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 0.5F);
				Utils.spawnTNT(wither, wither.getLocation(), 0, 8 + (finalI / 40), 32 + (finalI / 20) * 2, new ArrayList<>());
			}, i);
		}
		Utils.scheduleTask(() -> {
			wither.removeScoreboardTag("Invulnerable");
			wither.setAI(true);
		}, 161);

		if(which == 1100) {
			wither.removeScoreboardTag("1100Frenzy");
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Bukkit.broadcast(Utils.msg(name + "<red><bold>: WITNESS MY RAW NUCLEAR POWER!"));
			wither.setHealth(1100.0);
		} else {
			wither.removeScoreboardTag("300Frenzy");
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Bukkit.broadcast(Utils.msg(name + "<red><bold>: Sometimes when you have a problem, you just need to destroy it and start again!"));
			wither.setHealth(300.0);
		}
		WitherBoss nmsWither = ((CraftWither) wither).getHandle();
		nmsWither.bossEvent.setProgress(nmsWither.getHealth() / 1400);
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(((Wither) damagee).getInvulnerableTicks() != 0 && type != DamageType.LETHAL_ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		CustomMobs.updateWitherLordFight(true);

		double hp = damagee.getHealth();

		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			Utils.changeName(damagee);
			if(damager instanceof Player p && !damagee.getScoreboardTags().contains("Dead")) {
				p.showTitle(Title.title(Utils.msg("<red><bold>IMMUNE"), Utils.msg("<yellow>You cannot damage Necron!"), Title.Times.times(Duration.ZERO, Duration.ofMillis(20L * 50L), Duration.ZERO)));
			}
				damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
			return false;
		} else if(damagee.getScoreboardTags().contains("1100Frenzy") && hp - originalDamage < 1100) {
			frenzy((Wither) damagee, 1100);
			Utils.changeName(damagee);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			return false;
		} else if(damagee.getScoreboardTags().contains("300Frenzy") && hp - originalDamage < 300) {
			frenzy((Wither) damagee, 300);
			Utils.changeName(damagee);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			return false;
		} else if(hp - originalDamage < 1) {
			damagee.addScoreboardTag("Invulnerable");
			damagee.addScoreboardTag("Dead");
			damagee.setHealth(1.0);
			damagee.setSilent(true);
			damagee.setAI(false);
			Utils.changeName(damagee);
			WitherBoss nmsWither = ((CraftWither) damagee).getHandle();
			nmsWither.bossEvent.setProgress(nmsWither.getHealth() / 1400);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			Bukkit.broadcast(Utils.msg(name + "<red><bold>: You have destroyed us... but you have not destroyed our forefather."));
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcast(Utils.msg(name + "<red><bold>: He is a very powerful being.  If you wish to defeat Him, tread carefully."));
			}, 60);
			Utils.scheduleTask(() -> {
				damagee.remove();
				Utils.playGlobalSound(Sound.ENTITY_WITHER_DEATH);
				Utils.spawnTNT(damagee, damagee.getLocation(), 0, 32, 75, new ArrayList<>());
			}, 100);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
				Bukkit.broadcast(Utils.msg("<gold><bold>﴾ <red><bold>Wither-King<gold><bold> ﴿<red><bold>: Who dares wake me from my slumber?"));
			}, 240);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
				Bukkit.broadcast(Utils.msg("<gold><bold>﴾ <red><bold>Wither-King<gold><bold> ﴿<red><bold>: Foolish players!  You do not know who you are dealing with!"));
			}, 320);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
				Bukkit.broadcast(Utils.msg("<gold><bold>﴾ <red><bold>Wither-King<gold><bold> ﴿<red><bold>: I do not wish to fight, but you leave me no choice."));
			}, 400);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
				Bukkit.broadcast(Utils.msg("<gold><bold>﴾ <red><bold>Wither-King<gold><bold> ﴿<red><bold>: Prepare to meet your ultimate demise."));
			}, 480);
			Utils.scheduleTask(() -> {
				Wither wither = (Wither) damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.WITHER);
				CustomMob.getMob("WitherKing", true).onSpawn((damager instanceof Player p ? p : Utils.getNearestPlayer(damagee)), wither);
			}, 540);
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		CustomDamage.calculateFinalDamage(damagee, damager, 6, DamageType.RANGED);
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {

	}
}
