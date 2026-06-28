package mobs.hardmode.withers;

import listeners.CustomMobs;
import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import mobs.withers.CustomWither;
import net.kyori.adventure.title.Title;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.entity.CraftWither;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static listeners.CustomDamage.calculateFinalDamage;
import static misc.Utils.teleport;

public class Storm implements CustomWither {
	private static final String name = "<gold><bold>﴾ <red><bold>Storm<gold><bold> ﴿";

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
		Utils.changeName(wither, name);

		spawnGuards(wither);
		spawnLightning(wither);
		for(int i = 0; i < 600; i += 15) {
			spamSkulls(wither, Utils.getNearestPlayer(wither), i);
		}
		Utils.scheduleTask(() -> {
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Bukkit.broadcast(Utils.msg(name + "<red><bold>: Are you enjoying the party?  I sure am while watching you suffer!"));
		}, 200);
		Utils.scheduleTask(() -> {
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Bukkit.broadcast(Utils.msg(name + "<red><bold>: When I'm not making lightning, I love creating explosions!"));
		}, 480);
		Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(p2 -> p2.showTitle(Title.title(Utils.msg("<red><bold>5"), Utils.msg("<yellow><bold>BOOM!"), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)))), 500);
		Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(p2 -> p2.showTitle(Title.title(Utils.msg("<red><bold>4"), Utils.msg("<yellow><bold>BOOM!"), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)))), 520);
		Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(p2 -> p2.showTitle(Title.title(Utils.msg("<red><bold>3"), Utils.msg("<yellow><bold>BOOM!"), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)))), 540);
		Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(p2 -> p2.showTitle(Title.title(Utils.msg("<red><bold>2"), Utils.msg("<yellow><bold>BOOM!"), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)))), 560);
		Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(p2 -> p2.showTitle(Title.title(Utils.msg("<red><bold>1"), Utils.msg("<yellow><bold>BOOM!"), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)))), 580);
		Utils.scheduleTask(() -> {
			Bukkit.getOnlinePlayers().forEach(p2 -> p2.showTitle(Title.title(Utils.msg("<red><bold>BOOM!"), Utils.msg(""), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO))));
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

	private static final double SKULL_SPEED = 0.4;

	/**
	 * Spawns a wither skull aimed along {@code dir}. The spawn location is faced toward the target so
	 * the skull's inherited acceleration points the right way (a bare setDirection on a freshly spawned
	 * skull is ignored, leaving it to fly along the wither's facing), and an initial velocity is added.
	 */
	private static void fireSkull(Wither wither, Location spawn, Vector dir) {
		Vector d = dir.clone().normalize();
		// A fireball follows its ACCELERATION, not its velocity - setVelocity alone is overridden each
		// tick, so the skull curved back along the wither's facing. Set the acceleration in the spawn
		// consumer (before the entity ticks) so it actually flies at the target. Magnitude is tuned so
		// its terminal speed stays ~SKULL_SPEED (fireball drag is ~0.95/tick).
		WitherSkull skull = wither.getWorld().spawn(spawn, WitherSkull.class, s -> {
			s.setShooter(wither);
			s.setAcceleration(d.clone().multiply(SKULL_SPEED * 0.05));
		});
		skull.setVelocity(d.clone().multiply(SKULL_SPEED));
	}

	private void spamSkulls(Wither wither, Player p, int i) {
		Utils.scheduleTask(() -> {
			if(!wither.isDead()) {
				Vector directionMain;
				Vector directionLeft;
				Vector directionRight;
				if(p != null && !p.isDead()) {
					directionMain = p.getLocation().toVector().subtract(wither.getLocation().toVector()).normalize();
					directionLeft = p.getLocation().toVector().subtract(wither.getLocation().add(1, 0, 0).toVector()).normalize();
					directionRight = p.getLocation().toVector().subtract(wither.getLocation().add(-1, 0, 0).toVector()).normalize();
				} else {
					directionMain = wither.getLocation().getDirection();
					directionLeft = wither.getLocation().add(1, 0, 0).getDirection();
					directionRight = wither.getLocation().add(-1, 0, 0).getDirection();
				}
				fireSkull(wither, wither.getLocation().add(0, 1.5, 0), directionMain);
				fireSkull(wither, wither.getLocation().add(1, 1.5, 0), directionLeft);
				fireSkull(wither, wither.getLocation().add(-1, 1.5, 0), directionRight);
				Utils.playGlobalSound(Sound.ENTITY_WITHER_SHOOT, 0.75f, 1.0f);
			}
		}, i);
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
			if(damagee.getScoreboardTags().contains("Dead")) {
				if(damager instanceof Player p) {
					p.showTitle(Title.title(Utils.msg("<red><bold>IMMUNE"), Utils.msg("<yellow>You cannot damage Storm!"), Title.Times.times(Duration.ZERO, Duration.ofMillis(20L * 50L), Duration.ZERO)));
				}
				damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
			}
			return false;
		} else if(damagee.getScoreboardTags().contains("Survival2Trigger") && hp - originalDamage < 500) {
			damagee.setHealth(500.0);
			Utils.changeName(damagee);
			damagee.setAI(false);
			damagee.removeScoreboardTag("Survival2Trigger");
			damagee.addScoreboardTag("Survival2");
			damagee.addScoreboardTag("Invulnerable");
			teleport(damagee, 0, false);

			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			Bukkit.broadcast(Utils.msg(name + "<red><bold>: You think you're funny?  Try surviving this!"));

			spawnMoreGuards((Wither) damagee);
			spawnMoreLightning((Wither) damagee);
			for(int i = 0; i < 600; i += 10) {
				spamSkulls((Wither) damagee, Utils.getNearestPlayer(damagee), i);
			}
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcast(Utils.msg(name + "<red><bold>: Still alive?  You won’t survive what’s coming!"));
			}, 200);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcast(Utils.msg(name + "<red><bold>: I wasn't giving my all in that last explosion.  Good luck getting past this one!"));
			}, 520);
			Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(p2 -> p2.showTitle(Title.title(Utils.msg("<red><bold>3"), Utils.msg("<yellow><bold>BIGGER BOOM!"), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)))), 540);
			Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(p2 -> p2.showTitle(Title.title(Utils.msg("<red><bold>2"), Utils.msg("<yellow><bold>BIGGER BOOM!"), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)))), 560);
			Utils.scheduleTask(() -> Bukkit.getOnlinePlayers().forEach(p2 -> p2.showTitle(Title.title(Utils.msg("<red><bold>1"), Utils.msg("<yellow><bold>BIGGER BOOM!"), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)))), 580);
			Utils.scheduleTask(() -> {
				Bukkit.getOnlinePlayers().forEach(p2 -> p2.showTitle(Title.title(Utils.msg("<red><bold>BIGGER BOOM!"), Utils.msg(""), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO))));
				List<EntityType> immune = new ArrayList<>();
				immune.add(EntityType.WITHER_SKELETON);
				Utils.spawnTNT(damagee, damagee.getLocation(), 0, 64, 300, immune);
				damagee.removeScoreboardTag("Survival2");
				damagee.removeScoreboardTag("Invulnerable");
				damagee.setAI(true);
			}, 600);
			WitherBoss nmsWither = ((CraftWither) damagee).getHandle();
			nmsWither.bossEvent.setProgress(nmsWither.getHealth() / 1000);
			return false;
		} else if(hp - originalDamage < 1) {
			damagee.addScoreboardTag("Invulnerable");
			damagee.addScoreboardTag("Dead");
			damagee.setHealth(1.0);
			damagee.setSilent(true);
			damagee.setAI(false);
			Utils.changeName(damagee);
			WitherBoss nmsWither = ((CraftWither) damagee).getHandle();
			nmsWither.bossEvent.setProgress(nmsWither.getHealth() / 1000);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			Bukkit.broadcast(Utils.msg(name + "<red><bold>: I knew I should have prepared better."));
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcast(Utils.msg(name + "<red><bold>: Have fun dealing with the others."));
			}, 60);
			Utils.scheduleTask(() -> {
				damagee.remove();
				Utils.playGlobalSound(Sound.ENTITY_WITHER_DEATH);
				Utils.spawnTNT(damagee, damagee.getLocation(), 0, 32, 50, new ArrayList<>());
			}, 100);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcast(Utils.msg("<gold><bold>﴾ <red><bold>Goldor<gold><bold> ﴿<red><bold>: I hear some vermin prowling around my territory."));
			}, 240);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcast(Utils.msg("<gold><bold>﴾ <red><bold>Goldor<gold><bold> ﴿<red><bold>: I hope you came prepared for a long fight!"));
			}, 300);
			Utils.scheduleTask(() -> {
				Wither wither = (Wither) damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.WITHER);
				CustomMob.getMob("Goldor", true).onSpawn((damager instanceof Player p ? p : Utils.getNearestPlayer(damagee)), wither);
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
			calculateFinalDamage(damagee, damager, 12, DamageType.RANGED);
		} else if(damager.getScoreboardTags().contains("Survival2")) {
			calculateFinalDamage(damagee, damager, 18, DamageType.RANGED);
		}
		damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.LIGHTNING_BOLT);
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {
	}
}