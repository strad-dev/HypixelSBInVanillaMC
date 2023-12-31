package listeners;

import misc.Plugin;
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
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

public class CustomDamage implements Listener {
	private LivingEntity entity;
	private Entity damager;

	@SuppressWarnings("DuplicateExpressions")
	public void dealDamage(double finalDamage, double antiKB) {
		double absorption = entity.getAbsorptionAmount();
		if(finalDamage > absorption) {
			entity.setAbsorptionAmount(0.0);
			finalDamage -= absorption;
		} else {
			entity.setAbsorptionAmount(absorption - finalDamage);
			finalDamage = 0.0;
		}
		double newHealth = entity.getHealth() - finalDamage;
		if(newHealth < 0.0) {
			entity.setHealth(0.0);
		} else {
			entity.setHealth(entity.getHealth() - finalDamage);
			entity.playHurtAnimation(0.0F);
			entity.getWorld().playSound(entity, Objects.requireNonNull(entity.getHurtSound()), 1.0F, 1.0F);
			entity.setNoDamageTicks(10);
			if(entity instanceof Mob && damager instanceof LivingEntity) {
				((Mob) entity).setTarget((LivingEntity) damager);
			}

			// apply knockback
			double rawYaw = damager.getLocation().getYaw();
			double yaw = Math.toRadians(rawYaw);
			double factor = 0.5 * Math.max(0.5, 1 - antiKB);
			if(rawYaw <= -90) {
				entity.setVelocity(new Vector(factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw)), factor, factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw))));
			} else if(rawYaw >= 90) {
				entity.setVelocity(new Vector(factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw)), factor, factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw))));
			} else if(rawYaw < 0) {
				entity.setVelocity(new Vector(factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw)), factor, factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw))));
			} else if(rawYaw >= 0) {
				entity.setVelocity(new Vector(factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw)), factor, factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw))));
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		// make armor less unintuitive
		if(e.getEntity() instanceof LivingEntity temp) {
			entity = temp;
			e.setCancelled(true);
			if(entity.getNoDamageTicks() == 0) {
				damager = e.getDamager();
				if(entity.hasPotionEffect(PotionEffectType.WEAKNESS)) {
					e.setDamage(e.getDamage() + 1);
				}

				double originalDamage = e.getDamage();
				double armor = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ARMOR)).getValue();
				double toughness = Math.max(Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)).getValue() - 8, 0); // only toughness values of 9 or more will give damage reduction
				double antiKB = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).getValue();

				double resistance = 0;
				try {
					resistance = Objects.requireNonNull(entity.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)).getAmplifier() + 1;
				} catch(Exception exception) {
					// continue
				}

				// get prot levels
				double prots = 0;
				if(e.getEntity() instanceof LivingEntity entity1) {
					EntityEquipment eq = entity1.getEquipment();
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
				}

				//									-4% per armor						-10% per toughenss above 8			-4% per protection level			-20% per resistance level
				double finalDamage = originalDamage * Math.max(0.2, 1 - armor * 0.04) * Math.max(0.2, 1 - toughness * 0.1) * Math.max(0.2, 1 - prots * 0.04) * Math.max(0.0, 1 - resistance * 0.2);

				// apply intelligence to players
				if(e.getDamager() instanceof Player p) {
					try {
						Score score = Objects.requireNonNull(Objects.requireNonNull(Plugin.getInstance().getServer().getScoreboardManager()).getMainScoreboard().getObjective("Intelligence")).getScore(p.getName());
						if(score.getScore() < 1000) {
							score.setScore(score.getScore() + 1);
							Plugin.getInstance().getLogger().info(p.getName() + " damaged " + e.getEntity().getName() + ", gaining 1 Intelligence.  Current intelligence: " + score.getScore());
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Intelligence: " + score.getScore() + "/1000", ChatColor.AQUA.asBungee()));
						} else {
							Plugin.getInstance().getLogger().info(p.getName() + " damaged " + e.getEntity().getName() + ", but they are at max Intelligence.  Current intelligence: " + score.getScore());
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Intelligence: " + score.getScore() + "/1000 " + ChatColor.RED + ChatColor.BOLD + "MAX INTELLIGENCE", ChatColor.AQUA.asBungee()));
						}
					} catch(Exception exception) {
						Plugin.getInstance().getLogger().info("Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
						Bukkit.broadcastMessage(ChatColor.RED + "Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
						return;
					}
				}

				Random random = new Random();
				// apply custom damage to special mobs before going through with general damage
				try {
					// melog noris
					if(e.getEntity() instanceof IronGolem golem && Objects.requireNonNull(e.getEntity().getCustomName()).contains("meloG norI")) {
						if(damager instanceof LivingEntity entity1) {
							if(finalDamage > 5.0) {
								golem.setTarget(entity1);
								if(golem.getHealth() + (originalDamage - 5.0) > 200) {
									golem.setHealth(200);
									entity1.damage(originalDamage / 2, golem);
									damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The meloG norI is at full health and has REFLECTED " + originalDamage / 2 + " + Damage back to you!");
								} else {
									golem.setHealth(golem.getHealth() + (originalDamage - 5.0));
									damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You have done too much damage to the meloG norI!  It has HEALED ITSELF!");
								}
							} else {
								dealDamage(finalDamage, antiKB);
							}
						}

						// tarantula broodfathers
					} else if(e.getEntity() instanceof Spider spider && Objects.requireNonNull(e.getEntity().getCustomName()).contains("Tarantula Broodfather")) {
						spider.damage(Math.min(finalDamage, 2.0));
						Location l = spider.getLocation();
						Location l2 = spider.getLocation();
						Vector added = new Vector(random.nextInt(17) - 8, 0, random.nextInt(17) - 8);
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

						// chickzillas
					} else if(e.getEntity() instanceof Chicken chicken && Objects.requireNonNull(e.getEntity().getCustomName()).contains("Chickzilla")) {
						if(damager instanceof LivingEntity entity1) {
							entity1.damage(originalDamage / 4, chicken);
							damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Chickzilla has REFLECTED " + originalDamage / 4 + " + Damage back to you!");
							dealDamage(finalDamage, antiKB);
						}

						// apply general damage
					} else {
						dealDamage(finalDamage, antiKB);
					}
				} catch(NullPointerException exception) {
					dealDamage(finalDamage, antiKB);
				}
			}
		}
	}
}