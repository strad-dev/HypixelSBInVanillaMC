package listeners;

import misc.Plugin;
import misc.SimilarData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

public class CustomDamage implements Listener {
	private static EntityDamageEvent e;

	public static void customMobs(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		// apply custom damage to special mobs before going through with general damage
		Random random = new Random();
		try {
			// melog noris
			if(damagee instanceof IronGolem golem && Objects.requireNonNull(damagee.getCustomName()).contains("meloG norI")) {
				if(type.equals(DamageType.MELEE)) {
					if(damager instanceof LivingEntity entity1) {
						if(originalDamage > 10.0) {
							golem.setTarget(entity1);
							if(golem.getHealth() + (originalDamage - 10.0) > 200) {
								golem.setHealth(200);
								calculateFinalDamage(entity1, damagee, (originalDamage - 10) / 2, DamageType.MELEE); // damager takes 50% of their original damage, -10
								damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You have done too much damage to the meloG norI!\nIt is at full health and has REFLECTED " + (originalDamage - 10) / 2 + " Damage back to you!");
							} else {
								golem.setHealth(golem.getHealth() + (originalDamage - 10.0));
								SimilarData.changeName(golem);
								damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You have done too much damage to the meloG norI!\nIt has HEALED ITSELF by " + (originalDamage - 10.0) + " HP!");
							}
							return;
						}
					}
				} else {
					if(damager instanceof Arrow a) {
						Entity shooter = (Entity) a.getShooter();
						try {
							assert shooter != null;
							shooter.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You cannot deal " + type + " damage to the meloG norI.");
						} catch(Exception exception) {
							damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You cannot deal " + type + " damage to the meloG norI.");
						}
					} else {
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You cannot deal " + type + " damage to the meloG norI.");
					}
					return;
				}

				// tarantula broodfathers
			} else if(damagee instanceof Spider spider && Objects.requireNonNull(damagee.getCustomName()).contains("Tarantula Broodfather")) {
				Location l = spider.getLocation();
				Location l2 = spider.getLocation();
				Vector added = new Vector(random.nextInt(25) - 12, 0, random.nextInt(25) - 12);
				l.add(added);
				l2.add(added);
				while(true) {
					if(l.getY() > 319 || l2.getY() < -63) {
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Tarantula Broodfather could not find a spot to teleport to!");
						break;
					} else if(l.getBlock().isEmpty()) {
						spider.teleport(l);
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Tarantula Broodfather has scurried away!");
						break;
					} else if(l2.getBlock().isEmpty()) {
						spider.teleport(l2);
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Tarantula Broodfather has scurried away!");
						break;
					}
					l.add(0, 1, 0);
					l2.add(0, -1, 0);
				}
				spider.getWorld().playSound(spider.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
				calculateFinalDamage(damagee, damager, 2, DamageType.ABSOLUTE);
				return;

				// chickzillas
			} else if(damagee instanceof Chicken && Objects.requireNonNull(damagee.getCustomName()).contains("Chickzilla")) {
				String message = ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Chickzilla has REFLECTED " + originalDamage / 2 + " Damage back to you!";
				if(damager instanceof LivingEntity damager1) {
					damager.sendMessage(message);
					calculateFinalDamage(damager1, damagee, originalDamage / 2, DamageType.MELEE); // damager takes 50% of their original damage
				} else if(damager instanceof Arrow arrow) {
					if(arrow.getShooter() instanceof Player p) {
						p.sendMessage(message);
						calculateFinalDamage(p, damagee, originalDamage / 2, DamageType.MELEE); // damager takes 50% of their original damage
					}
				}
				// deal with wither skeleton skull custom damage
			} else if(damager instanceof WitherSkull skull) {
				damagee.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1));
				String shooter = ((Wither) Objects.requireNonNull(skull.getShooter())).getCustomName();
				assert shooter != null;
				if(shooter.contains("Storm") && random.nextBoolean()) {
					skull.getWorld().spawnEntity(damagee.getLocation(), EntityType.LIGHTNING);
				} else if(shooter.contains("Necron")) {
					calculateFinalDamage(damagee, damager, originalDamage + 4, DamageType.RANGED);
					return;
				}
				// deal with dragon fireball custom damage
			} else if(damager instanceof DragonFireball fireball) {
				String shooter = ((Wither) Objects.requireNonNull(fireball.getShooter())).getCustomName();
				assert shooter != null;
				if(shooter.contains("Strong Dragon")) {
					calculateFinalDamage(damagee, damager, originalDamage + 6, DamageType.RANGED);
					return;
				} else if(shooter.contains("Superior Dragon") && random.nextBoolean()) {
					calculateFinalDamage(damagee, damager, originalDamage + 3, DamageType.RANGED);
					return;
				}
				// atoned horror tnt
			} else if(damager instanceof Zombie zombie && Objects.requireNonNull(damager.getCustomName()).contains("Atoned Horror")) {
				TNTPrimed tnt = (TNTPrimed) zombie.getWorld().spawnEntity(damagee.getLocation(), EntityType.PRIMED_TNT);
				tnt.addScoreboardTag("AtonedHorror");
				tnt.setFuseTicks(10);
				// no magic damage to atoned horrors
			} else if(damagee instanceof Zombie && Objects.requireNonNull(damagee.getCustomName()).contains("Atoned Horror") && !(damager instanceof TNTPrimed)) {
				if(type.equals(DamageType.PLAYER_MAGIC)) {
					damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You cannot deal " + type + " damage to the Atoned Horror.");
					return;
				}
				// deal with atoned horror tnt
			} else if(damager instanceof TNTPrimed tnt && tnt.getScoreboardTags().contains("AtonedHorror")) {
				if(damagee instanceof Player) {
					calculateFinalDamage(damagee, damager, originalDamage / 2, DamageType.MELEE);
				}
				return;
				// not allowed to damage withers in spawning animation
			} else if(damagee instanceof Wither wither) {
				if(wither.getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE) {
					return;
				}
				// deal with vanilla mobs
			} else if(damager instanceof CaveSpider) {
				damagee.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 0));
			} else if(damager instanceof WitherSkeleton) {
				damagee.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 0));
			} else if(damager instanceof Husk) {
				damagee.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 0));
			} else if(damager instanceof ShulkerBullet) {
				damagee.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 200, 0));
			}

			// apply general damage
		} catch(NullPointerException exception) {
			calculateFinalDamage(damagee, damager, originalDamage, type);
		}
		calculateFinalDamage(damagee, damager, originalDamage, type);
	}

	public static void calculateFinalDamage(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		double finalDamage = originalDamage;

		if(!type.equals(DamageType.ABSOLUTE)) {
			// ice spray logic
			if(damagee.getScoreboardTags().contains("IceSprayed")) {
				finalDamage++;
			}

			if(damager instanceof LivingEntity entity1) {
				if(entity1.getScoreboardTags().contains("IceSprayed")) {
					finalDamage *= 0.8;
				}
			}

			// shield logic (for weirdos)
			if(damagee instanceof Player p && p.isBlocking() && (type == DamageType.MELEE || type == DamageType.RANGED || type == DamageType.PLAYER_MAGIC)) {
				finalDamage *= 0.5;
			}

			if(type.equals(DamageType.MELEE) || type.equals(DamageType.RANGED) || type.equals(DamageType.PLAYER_MAGIC) || type.equals(DamageType.ENVIRONMENTAL) || type.equals(DamageType.IFRAMEENVIRONMENTAL)) {
				double armor = Objects.requireNonNull(damagee.getAttribute(Attribute.GENERIC_ARMOR)).getValue();
				finalDamage *= Math.max(0.2, 1 - armor * 0.04);
			}

			double toughness = Math.max(Objects.requireNonNull(damagee.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)).getValue() - 8, 0); // only toughness values of 9 or more will give damage reduction
			finalDamage *= Math.max(0.2, 1 - toughness * 0.08);

			double resistance = 0;
			try {
				resistance = Objects.requireNonNull(damagee.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)).getAmplifier() + 1;
			} catch(Exception exception) {
				// continue
			}
			finalDamage *= Math.max(0.0, 1 - resistance * 0.2);

			// get prot levels
			double prots = 0;
			EntityEquipment eq = damagee.getEquipment();
			assert eq != null;
			try {
				prots += Objects.requireNonNull(eq.getHelmet()).getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
			} catch(Exception exception) {
				// continue
			}

			try {
				prots += Objects.requireNonNull(eq.getChestplate()).getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
			} catch(Exception exception) {
				// continue
			}

			try {
				prots += Objects.requireNonNull(eq.getLeggings()).getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
			} catch(Exception exception) {
				// continue
			}

			try {
				prots += Objects.requireNonNull(eq.getBoots()).getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
			} catch(Exception exception) {
				// continue
			}
			finalDamage *= Math.max(0.2, 1 - prots * 0.03125);

			if(type.equals(DamageType.FALL)) {
				try {
					double featherFalling = Objects.requireNonNull(eq.getBoots()).getEnchantmentLevel(Enchantment.PROTECTION_FALL);
					finalDamage *= Math.max(0.5, 1 - featherFalling * 0.125);
				} catch(Exception exception) {
					// continue
				}
			}
		}

		dealDamage(damagee, damager, finalDamage, type);
	}

	@SuppressWarnings("DuplicateExpressions")
	public static void dealDamage(LivingEntity damagee, Entity damager, double finalDamage, DamageType type) {
		double absorption = damagee.getAbsorptionAmount();
		if(finalDamage > absorption) {
			damagee.setAbsorptionAmount(0.0);
			finalDamage -= absorption;
		} else {
			damagee.setAbsorptionAmount(absorption - finalDamage);
			finalDamage = 0.0;
		}
		double newHealth = damagee.getHealth() - finalDamage;
		if(newHealth < 0.0) {
			if(damagee instanceof EnderDragon) {
				damagee.teleport(new Location(damagee.getWorld(), 0.5, 70.0, 0.5));
			}
			if(type.equals(DamageType.PLAYER_MAGIC) || damagee instanceof Player p && p.isBlocking()) {
				damagee.setHealth(0.0);
			} else {
				e.setCancelled(false);
				damagee.setHealth(0.1);
				e.setDamage(10);
			}
		} else if(finalDamage > 0) {
			damagee.setHealth(damagee.getHealth() - finalDamage);
			damagee.playHurtAnimation(0.0F);
			damagee.getWorld().playSound(damagee, Objects.requireNonNull(damagee.getHurtSound()), 1.0F, 1.0F);
			if(type.equals(DamageType.MELEE) || type.equals(DamageType.MAGIC) || type.equals(DamageType.IFRAMEENVIRONMENTAL) || type.equals(DamageType.FALL)) {
				damagee.setNoDamageTicks(10);
			}
			if(damagee instanceof Mob && damager instanceof LivingEntity) {
				((Mob) damagee).setTarget((LivingEntity) damager);
			}

			// apply knockback
			if(type.equals(DamageType.MELEE) || type.equals(DamageType.RANGED)) {
				double antiKB = Objects.requireNonNull(damagee.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).getValue();
				double factor = 0.33 * (1 - antiKB);
				Vector oldVelocity = damagee.getVelocity();
				double x = oldVelocity.getX();
				double y = oldVelocity.getY();
				double z = oldVelocity.getZ();
				if(type.equals(DamageType.RANGED)) {
					factor *= 0.25;
				}

				if(damagee instanceof Player p && p.isBlocking()) {
					factor *= 0.5;
				}

				if(damagee instanceof Player && !(damager instanceof Player)) {
					double rawYaw = damagee.getLocation().getYaw();
					double yaw = Math.toRadians(rawYaw);
					if(rawYaw <= -90) {
						x += factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
						y += 1.33 * factor;
						z += factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
					} else if(rawYaw >= 90) {
						x += factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
						y += 1.33 * factor;
						z += factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
					} else if(rawYaw < 0) {
						x += factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
						y += 1.33 * factor;
						z += factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
					} else if(rawYaw >= 0) {
						x += factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
						y += 1.33 * factor;
						z += factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
					}
				} else {
					double rawYaw = damager.getLocation().getYaw();
					double yaw = Math.toRadians(rawYaw);
					if(rawYaw <= -90) {
						x += factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
						y += 1.33 * factor;
						z += factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
					} else if(rawYaw >= 90) {
						x += factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
						y += 1.33 * factor;
						z += factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
					} else if(rawYaw < 0) {
						x += factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
						y += 1.33 * factor;
						z += factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
					} else if(rawYaw >= 0) {
						x += factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
						y += 1.33 * factor;
						z += factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
					}
				}
				damagee.setVelocity(new Vector(x, y, z));
			}

			// change nametag health
			SimilarData.changeName(damagee);

			// stop stupidly annoying arrows
			if(damager instanceof Arrow arrow) {
				if(arrow.getPierceLevel() == 0) {
					arrow.remove();
				} else {
					arrow.setPierceLevel(arrow.getPierceLevel() - 1);
					Vector arrowSpeed = arrow.getVelocity();
					Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> arrow.setVelocity(arrowSpeed), 1L);
				}
			}
		}
	}

	public static void setEvent(EntityDamageEvent e) {
		CustomDamage.e = e;
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		CustomDamage.e = e;
		// make armor less unintuitive
		if(e.getEntity() instanceof LivingEntity entity) {
			e.setCancelled(true);

			DamageType type;
			switch(e.getCause()) {
				case ENTITY_ATTACK, ENTITY_EXPLOSION, THORNS -> type = DamageType.MELEE;
				case PROJECTILE, SONIC_BOOM -> type = DamageType.RANGED;
				case DRAGON_BREATH, MAGIC -> type = DamageType.MAGIC;
				case FALLING_BLOCK -> type = DamageType.ENVIRONMENTAL;
				case LIGHTNING -> type = DamageType.IFRAMEENVIRONMENTAL;
				default -> {
					return;
				}
			}

			if(entity.getNoDamageTicks() == 0 || e.getDamager() instanceof Arrow) {
				double originalDamage;
				if(e.getDamager() instanceof Arrow arrow && arrow.getScoreboardTags().contains("TerminatorArrow")) {
					originalDamage = arrow.getDamage();
				} else {
					originalDamage = e.getDamage();
				}

				// apply intelligence to players
				if(e.getDamager() instanceof Player p) {
					if(e.getEntity() instanceof Monster || e.getEntity() instanceof Player) {
						try {
							Score score = Objects.requireNonNull(Objects.requireNonNull(Plugin.getInstance().getServer().getScoreboardManager()).getMainScoreboard().getObjective("Intelligence")).getScore(p.getName());
							if(score.getScore() < 2500) {
								score.setScore(score.getScore() + 1);
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Intelligence: " + score.getScore() + "/2500", ChatColor.AQUA.asBungee()));
							} else {
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Intelligence: " + score.getScore() + "/2500 " + ChatColor.RED + ChatColor.BOLD + "MAX INTELLIGENCE", ChatColor.AQUA.asBungee()));
							}
						} catch(Exception exception) {
							Plugin.getInstance().getLogger().info("Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
							Bukkit.broadcastMessage(ChatColor.RED + "Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
							return;
						}
					}
				}

				Entity damager = e.getDamager();
				customMobs(entity, damager, originalDamage, type);
			}
		}
	}
}