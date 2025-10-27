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

	@Override
	public void whenShootingFireball(DragonFireball fireball) {

	}

	@Override
	public String onSpawn(Player p, Mob e) {
		FREEZE_LOCATION = new Location(e.getWorld(), 0.5, 80, 0.5);
		PERCHED_TNT_RAIN_LOCATIONS[0] = new Location(e.getWorld(), 3.5, 62, 0.5);
		PERCHED_TNT_RAIN_LOCATIONS[1] = new Location(e.getWorld(), 2.5, 62, 2.5);
		PERCHED_TNT_RAIN_LOCATIONS[2] = new Location(e.getWorld(), 0.5, 62, 3.5);
		PERCHED_TNT_RAIN_LOCATIONS[3] = new Location(e.getWorld(), -1.5, 62, 2.5);
		PERCHED_TNT_RAIN_LOCATIONS[4] = new Location(e.getWorld(), -2.5, 62, 0.5);
		PERCHED_TNT_RAIN_LOCATIONS[5] = new Location(e.getWorld(), -1.5, 62, -1.5);
		PERCHED_TNT_RAIN_LOCATIONS[6] = new Location(e.getWorld(), 0.5, 62, -2.5);
		PERCHED_TNT_RAIN_LOCATIONS[7] = new Location(e.getWorld(), 2.5, 62, -1.5);
		e.setAI(false);
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
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The STRONG DRAGON has arrived to pulverize you!");
		Bukkit.getLogger().info("The Primal Dragon has been summoned!");

		e.teleport(new Location(e.getWorld(), 0, 80, 0));
		dialogue("For centuries, I have seen my kind be easily killed off one by one.");
		Utils.scheduleTask(() -> dialogue("Swords, bows, maces.  Even beds, somehow, have made quick work of them."), 60);
		Utils.scheduleTask(() -> dialogue("But it all ends here.  I have been slowly gaining power to avenge them."), 120);
		Utils.scheduleTask(() -> dialogue("You will never survive what's coming, and will finally pay the price!"), 180);
		Utils.scheduleTask(() -> {
			e.setAI(true);
			Utils.spawnTNT(e, FREEZE_LOCATION, 0, 128, 50, new ArrayList<>());
		}, 240);
		return name;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		EnderDragon dragon = (EnderDragon) damagee;
		double actualDamage = unfair(dragon, originalDamage, type, data);
		double hp = dragon.getHealth();
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
			dragon.setHealth(800);
			dragon.setAI(false);
			dragon.teleport(FREEZE_LOCATION);
			dialogue("Still think it's easy?");
			Utils.scheduleTask(() -> dialogue("Try running from my TNT!"), 60);
			Utils.scheduleTask(() -> {
				dragon.removeScoreboardTag("Invulnerable");
				dragon.setAI(true);
				dragon.setPhase(EnderDragon.Phase.CIRCLING);
				Utils.spawnTNT(dragon, FREEZE_LOCATION, 0, 128, 100, new ArrayList<>());
				tntRain(dragon);
			}, 120);
			return false;
		} else if(dragon.getScoreboardTags().contains("600Trigger") && hp - actualDamage < 600) {
			Utils.changeName(dragon);
			dragon.setHealth(600);
			dragon.setAI(false);
			dragon.teleport(FREEZE_LOCATION);
			dialogue("I am amazed you survived that.");
			Utils.scheduleTask(() -> dialogue("Good luck with this one!"), 60);
			Utils.scheduleTask(() -> {
				dragon.removeScoreboardTag("Invulnerable");
				dragon.setAI(true);
				dragon.setPhase(EnderDragon.Phase.CIRCLING);
				Utils.spawnTNT(dragon, FREEZE_LOCATION, 0, 128, 150, new ArrayList<>());
				tntRain(dragon);
				fireballSpam(dragon);
			}, 120);
			return false;
		} else if(dragon.getScoreboardTags().contains("400Trigger") && hp - actualDamage < 400) {
			Utils.changeName(dragon);
			dragon.setHealth(400);
			dragon.setAI(false);
			dragon.teleport(FREEZE_LOCATION);
			dialogue("It seems that I cannot take you on alone.");
			Utils.scheduleTask(() -> dialogue("Fortunately, I have made a few friends here!"), 60);
			Utils.scheduleTask(() -> {
				dragon.removeScoreboardTag("Invulnerable");
				dragon.setAI(true);
				dragon.setPhase(EnderDragon.Phase.CIRCLING);
				Utils.spawnTNT(dragon, FREEZE_LOCATION, 0, 128, 200, new ArrayList<>());
				tntRain(dragon);
				fireballSpam(dragon);
				summonZealots(dragon);
			}, 120);
			return false;
		} else if(dragon.getScoreboardTags().contains("200Trigger") && hp - actualDamage < 200) {
			Utils.changeName(dragon);
			dragon.setHealth(200);
			dragon.setAI(false);
			dragon.teleport(FREEZE_LOCATION);
			dialogue("Enough!  I did not want to do this, but you leave me no choice!");
			Utils.scheduleTask(() -> dialogue("I have held back, hoping that you would see the error in your ways."), 60);
			Utils.scheduleTask(() -> dialogue("I was wrong.  Prepare to face my true power."), 120);
			Utils.scheduleTask(() -> dialogue("I present to you... my Final Trick."), 180);
			Utils.scheduleTask(() -> {
				dragon.removeScoreboardTag("Invulnerable");
				dragon.setAI(true);
				dragon.setPhase(EnderDragon.Phase.CIRCLING);
				Utils.spawnTNT(dragon, FREEZE_LOCATION, 0, 128, 300, new ArrayList<>());
				tntRain(dragon);
				extremeTNTRainPlayer(dragon);
				fireballSpam(dragon);
				summonZealots(dragon);
				theFinalTrick(dragon);
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
			if(damagee.getScoreboardTags().contains("800Trigger")) { // 1000 to 801 hp
				return originalDamage * 0.2;
			} else if(damagee.getScoreboardTags().contains("600Trigger")) { // 800 to 601 hp
				return originalDamage * 0.15;
			} else if(damagee.getScoreboardTags().contains("400Trigger")) { // 600 to 401 hp
				return originalDamage * 0.1;
			} else if(damagee.getScoreboardTags().contains("200Trigger")) { // 400 to 201 hp
				return originalDamage * 0.05;
			} else { // 200 to dead
				return originalDamage * 0.02;
			}
		}
		if(type == DamageType.PLAYER_MAGIC) {
			if(!damagee.getScoreboardTags().contains("600Trigger")) { // 600 to 401 hp
				return originalDamage * 0.5;
			} else if(!damagee.getScoreboardTags().contains("400Trigger")) { // 400 to 201 hp
				return originalDamage * 0.25;
			} else { // 200 to dead
				return originalDamage * 0.1;
			}
		}
		return originalDamage;
	}

	private static void tntRain(EnderDragon dragon) {
		if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
			if(dragon.getScoreboardTags().contains("600Trigger")) { // 800 to 601 hp
				Utils.scheduleTask(() -> {
					if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead() && dragon.getScoreboardTags().contains("600Trigger")) {
						if(notPerching(dragon)) {
							Location l = dragon.getLocation();
							l.setY(l.getWorld().getHighestBlockYAt(l));
							Utils.spawnTNT(dragon, l, 30, 6, 10, new ArrayList<>());
						}
						Utils.scheduleTask(() -> {
							if(dragon.getScoreboardTags().contains("600Trigger")) {
								tntRain(dragon);
							}
						}, 40);
					}
				}, 40);
			} else if(dragon.getScoreboardTags().contains("400Trigger")) { // 600 to 401 hp
				Utils.scheduleTask(() -> {
					if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead() && dragon.getScoreboardTags().contains("400Trigger")) {
						if(notPerching(dragon)) {
							Location l = dragon.getLocation();
							l.setY(l.getWorld().getHighestBlockYAt(l));
							Utils.spawnTNT(dragon, l, 20, 6, 20, new ArrayList<>());
						}
						Utils.scheduleTask(() -> {
							if(dragon.getScoreboardTags().contains("400Trigger")) {
								tntRain(dragon);
							}
						}, 40);
					}
				}, 40);
			} else if(dragon.getScoreboardTags().contains("200Trigger")) { // 400 to 201 hp
				Utils.scheduleTask(() -> {
					if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead() && dragon.getScoreboardTags().contains("200Trigger")) {
						if(notPerching(dragon)) {
							Location l = dragon.getLocation();
							l.setY(l.getWorld().getHighestBlockYAt(l));
							Utils.spawnTNT(dragon, l, 15, 6, 25, new ArrayList<>());
						}
						Utils.scheduleTask(() -> {
							if(dragon.getScoreboardTags().contains("200Trigger")) {
								tntRain(dragon);
							}
						}, 30);
					}
				}, 30);
			} else { // Extreme TNT Rain (200 hp to dead)
				Utils.scheduleTask(() -> {
					if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
						if(notPerching(dragon)) {
							Location l = dragon.getLocation();
							l.setY(l.getWorld().getHighestBlockYAt(l));
							Utils.spawnTNT(dragon, l, 15, 6, 30, new ArrayList<>());
							Utils.scheduleTask(() -> {
								if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
									tntRain(dragon);
								}
							}, 20);
						} else {
							Utils.scheduleTask(() -> {
								for(Location l : PERCHED_TNT_RAIN_LOCATIONS) {
									Utils.spawnTNT(dragon, l, 15, 6, 12, new ArrayList<>());
								}
								Utils.scheduleTask(() -> {
									if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
										tntRain(dragon);
									}
								}, 30);
							}, 30);
						}
					}
				}, 20);
			}
		}
	}

	private static void extremeTNTRainPlayer(EnderDragon dragon) {
		if(notPerching(dragon)) {
			Player p = Utils.getNearestPlayer(dragon);
			if(p.getLocation().distanceSquared(dragon.getLocation()) < 16384) {
				Utils.spawnTNT(dragon, p.getLocation(), 30, 6, 12, new ArrayList<>());
			}
			Utils.scheduleTask(() -> extremeTNTRainPlayer(dragon), 50);
		} else {
			Player p = Utils.getNearestPlayer(dragon);
			if(p.getLocation().distanceSquared(dragon.getLocation()) < 16384) {
				Utils.spawnTNT(dragon, p.getLocation(), 40, 4, 8, new ArrayList<>());
			}
			Utils.scheduleTask(() -> extremeTNTRainPlayer(dragon), 80);
		}
	}

	private static void fireballSpam(EnderDragon dragon) {
		if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
			if(dragon.getScoreboardTags().contains("400Trigger")) { // 600 to 401 hp
				Utils.scheduleTask(() -> {
					if(notPerching(dragon)) {
						if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead() && dragon.getScoreboardTags().contains("400Trigger")) {
							Player p = Utils.getNearestPlayer(dragon);
							if(p.getLocation().distanceSquared(dragon.getLocation()) < 16384) {
								DragonFireball fireball = (DragonFireball) dragon.getWorld().spawnEntity(dragon.getLocation().subtract(0, 3, 0), EntityType.DRAGON_FIREBALL);
								Vector direction = p.getLocation().add(0, 1, 0).subtract(fireball.getLocation()).toVector().normalize();
								fireball.setVelocity(direction.multiply(0.1));
								fireball.setDirection(direction);
								p.playSound(dragon, Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 1.0f);
							}
							Utils.scheduleTask(() -> {
								if(dragon.getScoreboardTags().contains("400Trigger")) {
									fireballSpam(dragon);
								}
							}, 200);
						}
					}
				}, 200);
			} else if(dragon.getScoreboardTags().contains("200Trigger")) { // 400 to 201 hp
				Utils.scheduleTask(() -> {
					if(notPerching(dragon)) {
						if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead() && dragon.getScoreboardTags().contains("200Trigger")) {
							Player p = Utils.getNearestPlayer(dragon);
							if(p.getLocation().distanceSquared(dragon.getLocation()) < 16384) {
								DragonFireball fireball = (DragonFireball) dragon.getWorld().spawnEntity(dragon.getLocation().subtract(0, 3, 0), EntityType.DRAGON_FIREBALL);
								Vector direction = p.getLocation().add(0, 1, 0).subtract(fireball.getLocation()).toVector().normalize();
								fireball.setVelocity(direction.multiply(0.1));
								fireball.setDirection(direction);
								p.playSound(dragon, Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 1.0f);
							}
							Utils.scheduleTask(() -> {
								if(dragon.getScoreboardTags().contains("200Trigger")) {
									fireballSpam(dragon);
								}
							}, 140);
						}
					}
				}, 140);
			} else { // Ludicrous Fireball Spam (200 hp to dead)
				Utils.scheduleTask(() -> {
					if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
						if(notPerching(dragon)) {
							Player p = Utils.getNearestPlayer(dragon);
							if(p.getLocation().distanceSquared(dragon.getLocation()) < 16384) {
								DragonFireball fireball = (DragonFireball) dragon.getWorld().spawnEntity(dragon.getLocation().subtract(0, 3, 0), EntityType.DRAGON_FIREBALL);
								Vector direction = p.getLocation().add(0, 1, 0).subtract(fireball.getLocation()).toVector().normalize();
								fireball.setVelocity(direction.multiply(0.1));
								fireball.setDirection(direction);
								p.playSound(dragon, Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 1.0f);
							}
							Utils.scheduleTask(() -> {
								if(dragon.getScoreboardTags().contains("200Trigger")) {
									fireballSpam(dragon);
								}
							}, 100);
						} else {
							Utils.scheduleTask(() -> {
								for(Player p : dragon.getWorld().getPlayers()) {
									if(p.getLocation().distanceSquared(dragon.getLocation()) < 16384) {
										DragonFireball fireball = (DragonFireball) dragon.getWorld().spawnEntity(p.getLocation().add(0, 40, 0), EntityType.DRAGON_FIREBALL);
										fireball.setDirection(new Vector(0, -0.2, 0));
										p.playSound(dragon, Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 1.0f);
									}
								}
								Utils.scheduleTask(() -> {
									if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
										fireballSpam(dragon);
									}
								}, 200);
							}, 200);
						}
					}
				}, 100);
			}
		}
	}

	private static void summonZealots(EnderDragon dragon) {
		if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
			if(dragon.getScoreboardTags().contains("200Trigger")) { // 400 to 201 hp
				Utils.scheduleTask(() -> {
					if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead() && dragon.getScoreboardTags().contains("200Trigger")) {
						spawnZealots(dragon, false);
						Utils.scheduleTask(() -> {
							if(dragon.getScoreboardTags().contains("200Trigger")) {
								summonZealots(dragon);
							}
						}, 600);
					}
				}, 600);
			} else { // Summon Zealot Brusiers (200 hp to dead)
				Utils.scheduleTask(() -> {
					if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
						spawnZealots(dragon, true);
						Utils.scheduleTask(() -> summonZealots(dragon), 400);
					}
				}, 400);
			}
		}
	}

	private static void spawnZealots(EnderDragon dragon, boolean spawnBruiser) {
		Player p = Utils.getNearestPlayer(dragon);
		Location spawnLoc;
		if(p.getLocation().distanceSquared(dragon.getLocation()) < 16384) {
			spawnLoc = Utils.randomLocation(p.getLocation(), 16);
		} else {
			spawnLoc = dragon.getLocation().clone();
			spawnLoc.setY(dragon.getWorld().getHighestBlockYAt(spawnLoc));
		}
		for(int i = 0; i < 2; i++) {
			Enderman enderman = (Enderman) dragon.getWorld().spawnEntity(spawnLoc, EntityType.ENDERMAN);
			Objects.requireNonNull(enderman.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(167.0);
			enderman.setHealth(167.0);
			Objects.requireNonNull(enderman.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(15.0);
			Objects.requireNonNull(enderman.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(0.4);
			enderman.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
			enderman.setTarget(Utils.getNearestPlayer(enderman));
			enderman.setCustomNameVisible(true);
			enderman.addScoreboardTag("SkyblockBoss");
			enderman.setPersistent(true);
			enderman.setRemoveWhenFarAway(false);
			enderman.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Zealot" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RESET + ChatColor.RED + "❤" + ChatColor.YELLOW + " 167/167");
		}
		if(spawnBruiser) {
			Enderman enderman = (Enderman) dragon.getWorld().spawnEntity(spawnLoc, EntityType.ENDERMAN);
			Objects.requireNonNull(enderman.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(333.0);
			enderman.setHealth(333.0);
			Objects.requireNonNull(enderman.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(25.0);
			Objects.requireNonNull(enderman.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(0.5);
			enderman.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
			enderman.setTarget(Utils.getNearestPlayer(enderman));
			enderman.setCustomNameVisible(true);
			enderman.addScoreboardTag("SkyblockBoss");
			enderman.setPersistent(true);
			enderman.setRemoveWhenFarAway(false);
			enderman.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Zealot Bruiser" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RESET + ChatColor.RED + "❤" + ChatColor.YELLOW + " 167/167");
		}
	}

	private static void theFinalTrick(EnderDragon dragon) {
		if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
			Utils.scheduleTask(() -> {
				if(!dragon.getScoreboardTags().contains("Invulnerable") && !dragon.isDead()) {
					Location spawnLoc = Utils.randomLocation(new Location(dragon.getWorld(), 0, 64, 0), 32);
					spawnLoc.setY(dragon.getWorld().getHighestBlockYAt(spawnLoc) + 15 + random.nextInt(11));
					dragon.getWorld().spawnEntity(spawnLoc, EntityType.END_CRYSTAL);
					dragon.setHealth(dragon.getHealth() + 10);
					Utils.scheduleTask(() -> theFinalTrick(dragon), 600);
				}
			}, 600);
		}
	}

	private static boolean notPerching(EnderDragon dragon) {
		return !dragon.getPhase().equals(EnderDragon.Phase.FLY_TO_PORTAL) && !dragon.getPhase().equals(EnderDragon.Phase.LAND_ON_PORTAL);
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}