package mobs.hardmode.generic;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.DamageData;
import misc.Plugin;
import misc.Utils;
import mobs.CustomMob;
import net.minecraft.world.entity.monster.EnderMan;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftEnderman;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Random;

public class VoidcrazedSeraph implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		Enderman enderman;
		if(e instanceof Enderman) {
			enderman = (Enderman) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Voidcrazed Seraph" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		enderman.getAttribute(Attribute.MAX_HEALTH).setBaseValue(999.9);
		enderman.setHealth(999.9);
		enderman.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(40.0);
		enderman.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.5);
		enderman.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		enderman.setTarget(Utils.getNearestPlayer(enderman));
		enderman.setCustomNameVisible(true);
		enderman.addScoreboardTag("SkyblockBoss");
		enderman.addScoreboardTag("VoidcrazedSeraph");
		enderman.addScoreboardTag("HardMode");
		enderman.addScoreboardTag("450Trigger");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "From the ashes of the Superior Dragon rises the terrifying Voidcrazed Seraph!");
		Bukkit.getLogger().info(p.getName() + " has summoned the Voidcrazed Seraph.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		enderman.setPersistent(true);
		enderman.setRemoveWhenFarAway(false);
		enderman.setAware(true);
		Utils.scheduleTask(() -> dissonance(enderman), 20);
		Utils.scheduleTask(() -> yangGlyph(enderman), 600);

		EnderMan nmsEnderman = ((CraftEnderman) enderman).getHandle();
		nmsEnderman.setRemainingPersistentAngerTime(2147483647);
		nmsEnderman.setPersistentAngerTarget(p.getUniqueId());
		return newName;
	}

	private static void dissonance(Enderman voidgloom) {
		if(!voidgloom.isDead()) {
			voidgloom.getNearbyEntities(16, 16, 16).stream().filter(e -> e instanceof Player).forEach(p -> CustomDamage.customMobs((LivingEntity) p, voidgloom, 20, DamageType.MELEE));
			Utils.scheduleTask(() -> dissonance(voidgloom), 20);
		}
	}

	private static void yangGlyph(Enderman voidgloom) {
		if(!voidgloom.isDead()) {
			if(voidgloom.getHealth() < 666) {
				voidgloom.setCarriedBlock(Material.BEACON.createBlockData());
			}

			Utils.scheduleTask(() -> {
				if(!voidgloom.isDead() && voidgloom.getHealth() < 666) {
					voidgloom.setCarriedBlock(Material.AIR.createBlockData());
					Block block = Utils.randomLocation(voidgloom.getLocation(), 16).getBlock();
					block.setType(Material.BEACON);
					Utils.playGlobalSound(Sound.ENTITY_ARROW_HIT_PLAYER, 2.0F, 0.5F);
					voidgloom.getNearbyEntities(32, 32, 32).stream().filter(e -> e instanceof Player).forEach(p -> ((Player) p).sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "YANG GLYPH", ChatColor.YELLOW + "Destroy it or die!", 0, 160, 0));
					Utils.scheduleTask(() -> {
						if(!voidgloom.isDead() && block.getType() == Material.BEACON) {
							Utils.spawnTNT(voidgloom, block.getLocation(), 0, 32, 500, new ArrayList<>());
						}
						block.setType(Material.AIR);
					}, 160);
				}
			}, 60);
			Utils.scheduleTask(() -> yangGlyph(voidgloom), 600);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			if(damager instanceof Player p) {
				p.sendTitle("", ChatColor.YELLOW + "You cannot damage the Voidcrazed Seraph.", 0, 20, 0);
			}
			damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
			return false;
		} else if(damagee.getHealth() - originalDamage < 450 && damagee.getScoreboardTags().contains("450Trigger")) {
			damagee.addScoreboardTag("Invulnerable");
			damagee.removeScoreboardTag("450Trigger");
			damagee.setHealth(450.0);
			Utils.changeName(damagee);
			damagee.setAI(false);

			// Spinning flame beams
			final int[] tickCounter = {0};

			BukkitTask beamTask = Bukkit.getScheduler().runTaskTimer(Plugin.getInstance(), () -> {
				if(!damagee.isValid() || damagee.isDead()) {
					return;
				}

				Location currentCenter = damagee.getLocation();

				// Calculate rotation angle (full rotation over DURATION ticks)
				double rotationAngle = (tickCounter[0] * 2 * Math.PI) / 240;

				// Create 4 sets of beams at 90-degree intervals
				for(int set = 0; set < 4; set++) {
					double baseAngle = (set * Math.PI / 2) + rotationAngle;

					// Stack beams for each block of Enderman height (3 blocks)
					for(int height = 0; height < 3; height++) {
						double y = currentCenter.getY() + height + 0.5;

						// Create 3 beams per set (120 degrees apart within the set)
						for(int beam = 0; beam < 3; beam++) {
							double angle = baseAngle + (beam * 2 * Math.PI / 3);

							// Draw the beam with particles
							for(double distance = 0; distance < 16; distance += 0.2) {
								double x = currentCenter.getX() + Math.cos(angle) * distance;
								double z = currentCenter.getZ() + Math.sin(angle) * distance;

								Location particleLocation = new Location(currentCenter.getWorld(), x, y, z);

								// Spawn flame particle
								currentCenter.getWorld().spawnParticle(
										Particle.FLAME,
										particleLocation,
										1,  // count
										0, 0, 0,  // offset
										0  // speed
								);

								// Check for player collision every few particles to optimize
								if(distance % 1.0 < 0.2) {
									for(Player player : Bukkit.getOnlinePlayers()) {
										if(player.getWorld().equals(currentCenter.getWorld())) {
											Location playerLoc = player.getLocation();
											// Check if player is within 0.8 blocks of the beam particle
											if(playerLoc.distanceSquared(particleLocation) < 0.64) { // 0.8^2
												// Check vertical alignment (player is ~2 blocks tall)
												if(Math.abs(playerLoc.getY() - y) < 2.0 ||
														Math.abs(playerLoc.getY() + 1 - y) < 1.0) {

													// Deal absolute damage
													CustomDamage.customMobs(player, damagee, 4, DamageType.ABSOLUTE);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}, 0, 1); // Run every tick

			// Stop the beams after DURATION ticks
			Utils.scheduleTask(() -> {
				beamTask.cancel();
				// Re-enable AI after the attack
					damagee.setAI(true);
					damagee.removeScoreboardTag("Invulnerable");
			}, 240);
		}
		Random random = new Random();
		if(random.nextDouble() < 0.2) {
			damager.teleport(Utils.randomLocation(damagee.getLocation(), 3));
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}
