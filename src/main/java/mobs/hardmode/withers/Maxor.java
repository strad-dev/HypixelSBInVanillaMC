package mobs.hardmode.withers;

import listeners.CustomMobs;
import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import mobs.withers.CustomWither;
import net.kyori.adventure.title.Title;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftWither;
import org.bukkit.entity.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Maxor implements CustomWither {
	private static final String name = "<gold><bold>﴾ <red><bold>Maxor<gold><bold> ﴿";

	@Override
	public String onSpawn(Player p, Mob e) {
		e.setCanPickupItems(false);
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

		wither.getAttribute(Attribute.MAX_HEALTH).setBaseValue(800.0);
		wither.setHealth(800.0);
		wither.addScoreboardTag("Maxor");
		wither.addScoreboardTag("HardMode");
		wither.addScoreboardTag("SkyblockBoss");
		wither.addScoreboardTag("600Crystal");
		wither.addScoreboardTag("300Crystal");
		wither.setPersistent(true);
		wither.setRemoveWhenFarAway(false);
		Utils.changeName(wither, name);

		Utils.scheduleTask(() -> spawnGuards(wither), 300);

		return name;
	}

	private void spawnGuards(Wither wither) {
		if(!wither.isDead() && !wither.getScoreboardTags().contains("Dead")) {
			Utils.spawnGuards(wither, 2);
			Utils.scheduleTask(() -> spawnGuards(wither), 300);
		}
	}

	private void spawnCrystal(Wither wither, int which) {
		Location l = wither.getLocation();
		Random random = new Random();
		l.add(random.nextInt(32) - 16, 0, random.nextInt(32) - 16);
		for(int i = 319; i > -64; i--) {
			Block b = l.getWorld().getBlockAt((int) l.getX(), i, (int) l.getZ());
			if(b.getType() != Material.AIR && b.getType() != Material.VOID_AIR) {
				l.setY(i + 2);
				EnderCrystal crystal = (EnderCrystal) wither.getWorld().spawnEntity(l, EntityType.END_CRYSTAL);
				crystal.customName(Utils.msg("<red><bold>Energy Crystal"));
				crystal.addScoreboardTag("SkyblockBoss");
				if(which == 600) {
					wither.removeScoreboardTag("600Crystal");
					Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
					Bukkit.broadcast(Utils.msg(name + "<red><bold>: HAHAHA!  GOOD LUCK GETTING AROUND MY TRICKS!"));
					Utils.scheduleTask(() -> {
						if(wither.getScoreboardTags().contains("Invulnerable") && wither.getScoreboardTags().contains("300Crystal")) {
							Bukkit.broadcast(Utils.msg(name + "<red><bold>: ARE YOU REALLY THIS BAD?!  CAN YOU NOT SEE EXPLOSIVES AROUND YOU?!"));
						}
					}, 600L);
					wither.setHealth(600.0);
				} else {
					wither.removeScoreboardTag("300Crystal");
					Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
					Bukkit.broadcast(Utils.msg(name + "<red><bold>: IF YOU FAIL ONCE, YOU SHOULD SIMPLY TRY AGAIN!"));
					wither.setHealth(300.0);
				}
				wither.addScoreboardTag("Invulnerable");
				Utils.scheduleTask(() -> wither.addScoreboardTag("InvulnerableReminder"), 60L);
				Bukkit.broadcast(Utils.msg("<yellow>An Energy Crystal has spawned!  Maybe it is useful?"));
				return;
			}
		}
		WitherBoss nmsWither = ((CraftWither) wither).getHandle();
		nmsWither.bossEvent.setProgress(nmsWither.getHealth() / 800);
		Bukkit.broadcast(Utils.msg("<red>Oops!  Unable to summon a crystal!  Take it for free."));
		wither.removeScoreboardTag("600Crystal");
		wither.removeScoreboardTag("300Crystal");
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(((Wither) damagee).getInvulnerableTicks() != 0 && type != DamageType.LETHAL_ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		CustomMobs.updateWitherLordFight(true);

		double hp = damagee.getHealth();

		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			if(damager instanceof Player p && p.getScoreboardTags().contains("HasCrystal")) {
				damagee.removeScoreboardTag("Invulnerable");
				damagee.removeScoreboardTag("InvulnerableReminder");
				Bukkit.broadcast(Utils.msg(name + "<red><bold>: OUCH!  HOW DID YOU FIGURE IT OUT???"));
				List<EntityType> immune = new ArrayList<>();
				immune.add(EntityType.WITHER_SKELETON);
				Utils.spawnTNT(damagee, damagee.getLocation(), 0, 8, 10, immune);
				p.removeScoreboardTag("HasCrystal");
				Utils.changeName(damagee);
			} else {
				if(!damagee.getScoreboardTags().contains("Dead")) {
					if(damagee instanceof Player p) {
						p.showTitle(Title.title(Utils.msg("<red><bold>IMMUNE"), Utils.msg("<yellow>You cannot damage Maxor!"), Title.Times.times(Duration.ZERO, Duration.ofMillis(20L * 50L), Duration.ZERO)));
					}
					damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
				}
			}
			if(damagee.getScoreboardTags().contains("InvulnerableReminder") && !damagee.getScoreboardTags().contains("Dead")) {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcast(Utils.msg(name + "<red><bold>: YOUR WEAK, PUNY ATTACKS CANNOT GET AROUND MY TRICKS!"));
				damagee.removeScoreboardTag("InvulnerableReminder");
			}
			return false;
		} else if(damagee.getScoreboardTags().contains("600Crystal") && hp - originalDamage < 600) {
			spawnCrystal((Wither) damagee, 600);
			Utils.changeName(damagee);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			return false;
		} else if(damagee.getScoreboardTags().contains("300Crystal") && hp - originalDamage < 300) {
			spawnCrystal((Wither) damagee, 300);
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
			nmsWither.bossEvent.setProgress(nmsWither.getHealth() / 800);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			Bukkit.broadcast(Utils.msg(name + "<red><bold>: HOW DID YOU DEFEAT ME?!?!?!"));
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcast(Utils.msg(name + "<red><bold>: THIS IS ONLY THE BEGINNING!!!"));
			}, 60);
			Utils.scheduleTask(() -> {
				damagee.remove();
				Utils.playGlobalSound(Sound.ENTITY_WITHER_DEATH);
				Utils.spawnTNT(damagee, damagee.getLocation(), 0, 32, 50, new ArrayList<>());
			}, 100);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcast(Utils.msg("<gold><bold>﴾ <red><bold>Storm<gold><bold> ﴿<red><bold>: It seems that you have defeated my brother."));
			}, 240);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcast(Utils.msg("<gold><bold>﴾ <red><bold>Storm<gold><bold> ﴿<red><bold>: No worries, the party is just getting started!"));
			}, 300);
			Utils.scheduleTask(() -> {
				Wither wither = (Wither) damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.WITHER);
				CustomMob.getMob("Storm", true).onSpawn((damager instanceof Player p ? p : Utils.getNearestPlayer(damagee)), wither);
			}, 340);
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {

	}
}
