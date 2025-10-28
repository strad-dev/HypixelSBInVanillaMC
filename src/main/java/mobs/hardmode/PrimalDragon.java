package mobs.hardmode;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.enderDragons.CustomDragon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class PrimalDragon implements CustomDragon {
	private static final String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Primal Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
	private static Location FREEZE_LOCATION;
	private static final Location[] PERCHED_TNT_RAIN_LOCATIONS = new Location[8];
	private static final Random random = new Random();
	private static final int PERCH_MAX_DAMAGE = 50;
	private static final int SEARCH_RADIUS_SQUARED = 16384; // 128^2
	private static double perchedDamageAccumulated = 0;

	@Override
	public void whenShootingFireball(DragonFireball fireball) {

	}

	@Override
	public String onSpawn(Player p, Mob e) {
		FREEZE_LOCATION = new Location(e.getWorld(), 0.5, 70, 0.5);
		PERCHED_TNT_RAIN_LOCATIONS[0] = new Location(e.getWorld(), 3.5, 62, 0.5);
		PERCHED_TNT_RAIN_LOCATIONS[1] = new Location(e.getWorld(), 2.5, 62, 2.5);
		PERCHED_TNT_RAIN_LOCATIONS[2] = new Location(e.getWorld(), 0.5, 62, 3.5);
		PERCHED_TNT_RAIN_LOCATIONS[3] = new Location(e.getWorld(), -1.5, 62, 2.5);
		PERCHED_TNT_RAIN_LOCATIONS[4] = new Location(e.getWorld(), -2.5, 62, 0.5);
		PERCHED_TNT_RAIN_LOCATIONS[5] = new Location(e.getWorld(), -1.5, 62, -1.5);
		PERCHED_TNT_RAIN_LOCATIONS[6] = new Location(e.getWorld(), 0.5, 62, -2.5);
		PERCHED_TNT_RAIN_LOCATIONS[7] = new Location(e.getWorld(), 2.5, 62, -1.5);
		Utils.scheduleTask(() -> e.setAI(false), 2);
		e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(1000.0);
		e.setHealth(1000.0);
		e.addScoreboardTag("PrimalDragon");
		e.addScoreboardTag("HardMode");
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("Invulnerable");
		e.addScoreboardTag("800Trigger");
		e.addScoreboardTag("600Trigger");
		e.addScoreboardTag("400Trigger");
		e.addScoreboardTag("200Trigger");
		e.setPersistent(true);
		e.setRemoveWhenFarAway(false);
		e.setSilent(true);
		Bukkit.getLogger().info("The Primal Dragon has been summoned!");

		e.teleport(FREEZE_LOCATION);
		dialogue("For centuries, I have seen my kind be easily killed off one by one.");
		Utils.scheduleTask(() -> dialogue("Swords, bows, maces.  Even beds, somehow, have made quick work of them."), 60);
		Utils.scheduleTask(() -> dialogue("But it all ends here.  I have been slowly gaining power to avenge them."), 120);
		Utils.scheduleTask(() -> dialogue("You will never survive what's coming, and will finally pay the price!"), 180);
		Utils.scheduleTask(() -> {
			e.setAI(true);
			e.setSilent(false);
			e.removeScoreboardTag("Invulnerable");
			Utils.spawnTNT(e, FREEZE_LOCATION, 0, 128, 50, new ArrayList<>());
		}, 240);
		return name;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		EnderDragon dragon = (EnderDragon) damagee;
		double actualDamage = unfair(dragon, originalDamage, type, data);
		double hp = dragon.getHealth();
		if(dragon.getPhase().equals(EnderDragon.Phase.SEARCH_FOR_BREATH_ATTACK_TARGET) || dragon.getPhase().equals(EnderDragon.Phase.ROAR_BEFORE_ATTACK) || dragon.getPhase().equals(EnderDragon.Phase.BREATH_ATTACK)) {
			perchedDamageAccumulated += actualDamage;
			if(perchedDamageAccumulated >= PERCH_MAX_DAMAGE) {
				dragon.setPhase(EnderDragon.Phase.LEAVE_PORTAL);
				perchedDamageAccumulated = 0;
			}
		} else {
			perchedDamageAccumulated = 0;
		}
		if(dragon.getScoreboardTags().contains("Invulnerable")) {
			if(damager instanceof Player p) {
				if(!dragon.getScoreboardTags().contains("Dead")) {
					dragon.getWorld().playSound(dragon, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
					p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "IMMUNE", ChatColor.YELLOW + "You cannot damage Primal Dragon!", 0, 20, 0);
				}
			}
			return false;
		} else if(dragon.getScoreboardTags().contains("800Trigger") && hp - actualDamage < 800) {
			Utils.changeName(dragon);
			dragon.teleport(FREEZE_LOCATION);
			dragon.addScoreboardTag("Invulnerable");
			dragon.removeScoreboardTag("800Trigger");
			dragon.setHealth(800);
			Utils.scheduleTask(() -> dragon.setAI(false), 2);
			dragon.setSilent(true);
			dialogue("Still think it's easy?");
			Utils.scheduleTask(() -> dialogue("Try running from my fireballs!"), 60);
			Utils.scheduleTask(() -> {
				dragon.removeScoreboardTag("Invulnerable");
				dragon.setAI(true);
				dragon.setPhase(EnderDragon.Phase.CIRCLING);
				Utils.spawnTNT(dragon, FREEZE_LOCATION, 0, 128, 100, new ArrayList<>());
				fireballSpam(dragon);
				dragon.setSilent(false);
			}, 120);
			return false;
		} else if(dragon.getScoreboardTags().contains("600Trigger") && hp - actualDamage < 600) {
			Utils.changeName(dragon);
			dragon.teleport(FREEZE_LOCATION);
			dragon.addScoreboardTag("Invulnerable");
			dragon.removeScoreboardTag("600Trigger");
			dragon.setHealth(600);
			Utils.scheduleTask(() -> dragon.setAI(false), 2);
			dragon.setSilent(true);
			dialogue("I am amazed you survived that.");
			Utils.scheduleTask(() -> dialogue("Good luck escaping my explosives!"), 60);
			Utils.scheduleTask(() -> {
				dragon.removeScoreboardTag("Invulnerable");
				dragon.setAI(true);
				dragon.setPhase(EnderDragon.Phase.CIRCLING);
				Utils.spawnTNT(dragon, FREEZE_LOCATION, 0, 128, 150, new ArrayList<>());
				fireballSpam(dragon);
				tntRain(dragon);
				dragon.setSilent(false);
			}, 120);
			return false;
		} else if(dragon.getScoreboardTags().contains("400Trigger") && hp - actualDamage < 400) {
			Utils.changeName(dragon);
			dragon.teleport(FREEZE_LOCATION);
			dragon.addScoreboardTag("Invulnerable");
			dragon.removeScoreboardTag("400Trigger");
			dragon.setHealth(400);
			Utils.scheduleTask(() -> dragon.setAI(false), 2);
			dragon.setSilent(true);
			dialogue("It seems that I cannot take you on alone.");
			Utils.scheduleTask(() -> dialogue("Fortunately, I have made a few friends here!"), 60);
			Utils.scheduleTask(() -> {
				dragon.removeScoreboardTag("Invulnerable");
				dragon.setAI(true);
				dragon.setPhase(EnderDragon.Phase.CIRCLING);
				Utils.spawnTNT(dragon, FREEZE_LOCATION, 0, 128, 200, new ArrayList<>());
				fireballSpam(dragon);
				tntRain(dragon);
				summonZealots(dragon);
				dragon.setSilent(false);
			}, 120);
			return false;
		} else if(dragon.getScoreboardTags().contains("200Trigger") && hp - actualDamage < 200) {
			Utils.changeName(dragon);
			dragon.teleport(FREEZE_LOCATION);
			dragon.addScoreboardTag("Invulnerable");
			dragon.removeScoreboardTag("200Trigger");
			dragon.setHealth(200);
			Utils.scheduleTask(() -> dragon.setAI(false), 2);
			dragon.setSilent(true);
			dialogue("Enough!  I did not want to do this, but you leave me no choice!");
			Utils.scheduleTask(() -> dialogue("I have held back, hoping that you would see the error in your ways."), 60);
			Utils.scheduleTask(() -> dialogue("I was wrong.  Prepare to face my true power."), 120);
			Utils.scheduleTask(() -> dialogue("I present to you... my Final Trick."), 180);
			Utils.scheduleTask(() -> {
				dragon.removeScoreboardTag("Invulnerable");
				dragon.setAI(true);
				dragon.setPhase(EnderDragon.Phase.CIRCLING);
				Utils.spawnTNT(dragon, FREEZE_LOCATION, 0, 128, 300, new ArrayList<>());
				fireballSpam(dragon);
				tntRain(dragon);
				extremeTNTRainPlayer(dragon);
				summonZealots(dragon);
				theFinalTrick(dragon);
				dragon.setSilent(false);
			}, 240);
			return false;
		} else if(hp - actualDamage <= 0) {
			dialogue("Centuries of training down the drain...");
			Utils.scheduleTask(() -> dialogue("Your power is unmatched.  I commend you for that."), 60);
			Utils.scheduleTask(() -> dialogue("I hope you will stop killing my kind, but when has asking nicely ever worked?"), 120);
			return true;
		}
		CustomDamage.calculateFinalDamage(dragon, damager, actualDamage, type, data);
		return false;
	}

	private static void dialogue(String message) {
		Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": " + message);
		Utils.playGlobalSound(Sound.ENTITY_ENDER_DRAGON_GROWL);
	}

	/**
	 * Calculates the damage the Dragon should take after being affected by the Unfair! ability
	 */
	private static double unfair(LivingEntity damagee, double originalDamage, DamageType type, DamageData data) {
		if(data.isTermArrow || type == DamageType.RANGED_SPECIAL) {
			return originalDamage * getRangedMultiplier(damagee);
		} else if(type == DamageType.PLAYER_MAGIC) {
			return originalDamage * getMagicMultiplier(damagee);
		} else {
			if(!damagee.getScoreboardTags().contains("200Trigger")) {
				return originalDamage * 0.5;
			}
		}
		return originalDamage;
	}

	private static double getRangedMultiplier(LivingEntity damagee) {
		if(damagee.getScoreboardTags().contains("800Trigger")) return 0.25;
		if(damagee.getScoreboardTags().contains("600Trigger")) return 0.2;
		if(damagee.getScoreboardTags().contains("400Trigger")) return 0.15;
		if(damagee.getScoreboardTags().contains("200Trigger")) return 0.1;
		return 0.05;
	}

	private static double getMagicMultiplier(LivingEntity damagee) {
		if(damagee.getScoreboardTags().contains("600Trigger")) return 1;
		if(damagee.getScoreboardTags().contains("400Trigger")) return 0.5;
		if(damagee.getScoreboardTags().contains("200Trigger")) return 0.25;
		return 0.1;
	}

	private static void tntRain(EnderDragon dragon) {
		if(dragon.getScoreboardTags().contains("Invulnerable") || dragon.isDead()) return;

		// Determine parameters based on HP phase
		int delay, fuse, damage;
		String currentPhase = getCurrentPhase(dragon);

		switch(currentPhase) {
			case "400Trigger" -> {
				delay = 40;
				fuse = 20;
				damage = 30;
			}
			case "200Trigger" -> {
				delay = 30;
				fuse = 15;
				damage = 45;
			}
			default -> {
				delay = notPerching(dragon) ? 20 : 30;
				fuse = notPerching(dragon) ? 10 : 15;
				damage = notPerching(dragon) ? 60 : 30;
			}
		}

		Utils.scheduleTask(() -> {
			// Re-check conditions
			if(dragon.getScoreboardTags().contains("Invulnerable") || dragon.isDead()) return;

			String phase = getCurrentPhase(dragon);
			if(!phase.equals(currentPhase) && !currentPhase.equals("final")) return; // Phase changed

			if(notPerching(dragon)) {
				// Flying TNT rain
				Location l = dragon.getLocation();
				l.setY(Utils.highestBlock(l));
				Utils.spawnTNT(dragon, l, fuse, 8, damage, new ArrayList<>());
			} else if(currentPhase.equals("final")) {
				// Perched TNT only in final phase
				for(Location l : PERCHED_TNT_RAIN_LOCATIONS) {
					Utils.spawnTNT(dragon, l, fuse, 4, damage, new ArrayList<>());
				}
			}

			tntRain(dragon); // Recursive call
		}, delay);
	}

	private static String getCurrentPhase(EnderDragon dragon) {
		if(dragon.getScoreboardTags().contains("800Trigger")) return "800Trigger";
		if(dragon.getScoreboardTags().contains("600Trigger")) return "600Trigger";
		if(dragon.getScoreboardTags().contains("400Trigger")) return "400Trigger";
		if(dragon.getScoreboardTags().contains("200Trigger")) return "200Trigger";
		return "final";
	}

	private static void extremeTNTRainPlayer(EnderDragon dragon) {
		Utils.scheduleTask(() -> {
			if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
				Player p = Utils.getNearestPlayer(dragon);
				if(p != null && p.getLocation().distanceSquared(dragon.getLocation()) < SEARCH_RADIUS_SQUARED) {
					if(notPerching(dragon)) {
						Utils.spawnTNT(dragon, p.getLocation(), 20, 6, 40, new ArrayList<>());
					} else {
						Utils.spawnTNT(dragon, p.getLocation(), 20, 6, 25, new ArrayList<>());
					}
				}
				extremeTNTRainPlayer(dragon);
			}
		}, notPerching(dragon) ? 50 : 80);
	}

	private static void fireballSpam(EnderDragon dragon) {
		if(dragon.getScoreboardTags().contains("Invulnerable") || dragon.isDead()) return;

		String phase = getCurrentPhase(dragon);
		int delay = getFireballDelay(phase);

		Utils.scheduleTask(() -> {
			if(dragon.getScoreboardTags().contains("Invulnerable") || dragon.isDead()) return;

			// Check if phase changed (except for final phase which should continue)
			String currentPhase = getCurrentPhase(dragon);
			if(!phase.equals(currentPhase) && !phase.equals("final")) return;

			if(notPerching(dragon)) {
				shootFireballAtNearestPlayer(dragon);
				scheduleNextFireball(dragon, delay);
			} else if(phase.equals("final")) {
				// Perched fireballs only in final phase
				dropFireballsOnAllPlayers(dragon);
				scheduleNextFireball(dragon, 160);
			}
		}, delay);
	}

	private static int getFireballDelay(String phase) {
		return switch(phase) {
			case "600Trigger" -> 200;
			case "400Trigger" -> 160;
			case "200Trigger" -> 120;
			case "final" -> 80;
			default -> Integer.MAX_VALUE;
		};
	}

	private static void shootFireballAtNearestPlayer(EnderDragon dragon) {
		Player p = Utils.getNearestPlayer(dragon);
		if(p != null && p.getLocation().distanceSquared(dragon.getLocation()) < SEARCH_RADIUS_SQUARED) {
			DragonFireball fireball = (DragonFireball) dragon.getWorld().spawnEntity(dragon.getLocation().subtract(0, p.getLocation().getY() > dragon.getLocation().getY() ? 3 : -3, 0), EntityType.DRAGON_FIREBALL);
			Vector direction = p.getLocation().add(0, 1, 0).subtract(fireball.getLocation()).toVector().normalize();
			fireball.setVelocity(direction.multiply(0.1));
			fireball.setDirection(direction);
			p.playSound(dragon, Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 1.0f);
		}
	}

	private static void dropFireballsOnAllPlayers(EnderDragon dragon) {
		for(Player p : dragon.getWorld().getPlayers()) {
			if(p != null && p.getLocation().distanceSquared(dragon.getLocation()) < SEARCH_RADIUS_SQUARED) {
				DragonFireball fireball = (DragonFireball) dragon.getWorld().spawnEntity(p.getLocation().add(0, 40, 0), EntityType.DRAGON_FIREBALL);
				fireball.setDirection(new Vector(0, -0.2, 0));
				p.playSound(dragon, Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 1.0f);
			}
		}
	}

	private static void scheduleNextFireball(EnderDragon dragon, int delay) {
		Utils.scheduleTask(() -> {
			String phase = getCurrentPhase(dragon);
			if(!phase.equals("800Trigger")) {
				fireballSpam(dragon);
			}
		}, delay);
	}

	private static void summonZealots(EnderDragon dragon) {
		if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
			if(dragon.getScoreboardTags().contains("200Trigger")) { // 400 to 201 hp
				Utils.scheduleTask(() -> {
					if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead() && dragon.getScoreboardTags().contains("200Trigger")) {
						spawnZealots(dragon, false);
						summonZealots(dragon);
					}
				}, 400);
			} else { // Summon Zealot Brusiers (200 hp to dead)
				Utils.scheduleTask(() -> {
					if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
						spawnZealots(dragon, true);
						summonZealots(dragon);
					}
				}, 300);
			}
		}
	}

	private static void spawnZealots(EnderDragon dragon, boolean spawnBruiser) {
		Player p = Utils.getNearestPlayer(dragon);
		Location spawnLoc;
		if(p != null && p.getLocation().distanceSquared(dragon.getLocation()) < SEARCH_RADIUS_SQUARED) {
			spawnLoc = Utils.randomLocation(p.getLocation(), 16);
		} else {
			spawnLoc = dragon.getLocation().clone();
			spawnLoc.setY(Utils.highestBlock(spawnLoc));
		}
		for(int i = 0; i < 2; i++) {
			Enderman enderman = (Enderman) dragon.getWorld().spawnEntity(spawnLoc, EntityType.ENDERMAN);
			Objects.requireNonNull(enderman.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(66.0);
			enderman.setHealth(66.0);
			Objects.requireNonNull(enderman.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(15.0);
			Objects.requireNonNull(enderman.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(0.4);
			enderman.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
			enderman.setTarget(Utils.getNearestPlayer(enderman));
			enderman.setCustomNameVisible(true);
			enderman.addScoreboardTag("SkyblockBoss");
			enderman.setPersistent(true);
			enderman.setRemoveWhenFarAway(false);
			enderman.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Zealot" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RESET + ChatColor.RED + "❤" + ChatColor.YELLOW + " 66/66");
		}
		if(spawnBruiser) {
			Enderman enderman = (Enderman) dragon.getWorld().spawnEntity(spawnLoc, EntityType.ENDERMAN);
			Objects.requireNonNull(enderman.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(133.0);
			enderman.setHealth(133.0);
			Objects.requireNonNull(enderman.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(25.0);
			Objects.requireNonNull(enderman.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(0.5);
			enderman.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
			enderman.setTarget(Utils.getNearestPlayer(enderman));
			enderman.setCustomNameVisible(true);
			enderman.addScoreboardTag("SkyblockBoss");
			enderman.setPersistent(true);
			enderman.setRemoveWhenFarAway(false);
			enderman.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Zealot Bruiser" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RESET + ChatColor.RED + "❤" + ChatColor.YELLOW + " 133/133");
		}
	}

	private static void theFinalTrick(EnderDragon dragon) {
		if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
			Utils.scheduleTask(() -> {
				if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
					Location spawnLoc = Utils.randomLocation(new Location(dragon.getWorld(), 0, 64, 0), 24);
					spawnLoc.setY(Utils.highestBlock(spawnLoc) + 10 + random.nextInt(6));
					dragon.getWorld().spawnEntity(spawnLoc, EntityType.END_CRYSTAL);
					dragon.setHealth(dragon.getHealth() + 15);
					Utils.changeName(dragon);
					Utils.scheduleTask(() -> theFinalTrick(dragon), 600);
				}
			}, 600);
		}
	}

	private static boolean notPerching(EnderDragon dragon) {
		return !dragon.getPhase().equals(EnderDragon.Phase.LAND_ON_PORTAL) && !dragon.getPhase().equals(EnderDragon.Phase.SEARCH_FOR_BREATH_ATTACK_TARGET) && !dragon.getPhase().equals(EnderDragon.Phase.ROAR_BEFORE_ATTACK) && !dragon.getPhase().equals(EnderDragon.Phase.BREATH_ATTACK);
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}