package listeners;

import misc.Plugin;
import misc.PluginUtils;
import mobs.CustomMob;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_21_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class CustomDamage implements Listener {
	private static EntityDamageEvent e;
	private static boolean isBlocking;
	private static boolean flamingArrow;
	private static int punchArrow = 0;
	private static boolean isTermArrow;

	private static void customMobs(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		isBlocking = damagee instanceof Player p && p.isBlocking();

		if(damager instanceof Projectile projectile) {
			// stop stupidly annoying arrows
			if(projectile instanceof AbstractArrow arrow) {
				if(arrow.getPierceLevel() == 0) {
					arrow.remove();
				} else {
					arrow.setPierceLevel(arrow.getPierceLevel() - 1);
					Vector arrowSpeed = arrow.getVelocity();
					Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> arrow.setVelocity(arrowSpeed), 1L);
				}

				if(arrow.getWeapon().containsEnchantment(Enchantment.FLAME)) {
					flamingArrow = true;
				}

				if(arrow.getWeapon().containsEnchantment(Enchantment.PUNCH)) {
					punchArrow = arrow.getWeapon().getEnchantmentLevel(Enchantment.PUNCH);
				}
			}

			if(!isBlocking) {
				if(projectile instanceof SpectralArrow) {
					damagee.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0));
				}
				if(projectile instanceof Arrow a && a.hasCustomEffects()) {
					damagee.addPotionEffects(a.getCustomEffects());
				}
			}
			if(projectile.getShooter() instanceof LivingEntity temp) {
				damager = temp;
			}
		}

		// apply custom damage to special mobs before going through with general damage
		boolean doContinue = true;
		try {
			CustomMob damageeMob = CustomMob.getMob(damagee);
			CustomMob damagerMob = CustomMob.getMob(damager);

			// this section controls when bosses are damaged
			if(damageeMob != null) {
				doContinue = damageeMob.whenDamaged(damagee, damager, originalDamage, type);
			}

			// this section controls when bosses deal damage
			if(damagerMob != null) {
				doContinue = damagerMob.whenDamaging(damagee, damager, originalDamage, type);
			}
			if(!isBlocking) {
				switch(damager) {
					case Wither ignored -> damagee.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1));
					case CaveSpider ignored ->
							damagee.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 0));
					case WitherSkeleton ignored ->
							damagee.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 0));
					case Husk ignored -> damagee.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 0));
					case Shulker ignored ->
							damagee.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 200, 0));
					case null, default -> {
					}
				}
			}
		} catch(NullPointerException exception) {
			// continue
		}

		if((damagee instanceof Player p && (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR)) || (damagee instanceof Wither wither && wither.getInvulnerabilityTicks() > 0)) {
			doContinue = false;
		}

		if(type == DamageType.ABSOLUTE || doContinue) {
			calculateFinalDamage(damagee, damager, originalDamage, type);
		}
	}

	public static void calculateFinalDamage(LivingEntity damagee, Entity damager, double finalDamage, DamageType type) {
		if(type != DamageType.ABSOLUTE) {
			// ice spray logic
			if(damagee.getScoreboardTags().contains("IceSprayed")) {
				finalDamage *= 1.1;
			}

			if(damagee.getScoreboardTags().contains("WitherShield")) {
				finalDamage *= 0.9;
			}

			if(damagee.getScoreboardTags().contains("HolyIce")) {
				finalDamage *= 0.25;
			}

			if(damager instanceof LivingEntity entity1) {
				if(entity1.getScoreboardTags().contains("IceSprayed")) {
					finalDamage *= 0.8;
				}
			}


			// bonus damage to withers from hyperion
			if(damagee instanceof Wither && (type == DamageType.MELEE || type == DamageType.MELEE_SWEEP) && damager instanceof Player p &&
					p.getInventory().getItemInMainHand().hasItemMeta() &&
					p.getInventory().getItemInMainHand().getItemMeta().hasLore() &&
					p.getInventory().getItemInMainHand().getItemMeta().getLore().getFirst().equals("skyblock/combat/scylla")) {
				finalDamage += 4;
			}

			// shield logic (for weirdos)
			if(isBlocking && (type == DamageType.MELEE || type == DamageType.MELEE_SWEEP || type == DamageType.RANGED)) {
				finalDamage *= 0.5;
			}

			if(type == DamageType.MELEE || type == DamageType.MELEE_SWEEP || type == DamageType.RANGED || type == DamageType.PLAYER_MAGIC || type == DamageType.ENVIRONMENTAL || type == DamageType.IFRAME_ENVIRONMENTAL) {
				double armor = Objects.requireNonNull(damagee.getAttribute(Attribute.ARMOR)).getValue();
				finalDamage *= Math.max(0.25, 1 - armor * 0.0375);
			}

			double toughness = Math.max(Objects.requireNonNull(damagee.getAttribute(Attribute.ARMOR_TOUGHNESS)).getValue() - 8, 0); // only toughness values of 9 or more will give damage reduction
			finalDamage *= Math.max(0.2, 1 - toughness * 0.1);

			double resistance = 0;
			try {
				resistance = Objects.requireNonNull(damagee.getPotionEffect(PotionEffectType.RESISTANCE)).getAmplifier() + 1;
			} catch(Exception exception) {
				// continue
			}
			finalDamage *= Math.max(0.0, 1 - resistance * 0.2);

			// get prot levels
			double prots = 0;
			EntityEquipment eq = damagee.getEquipment();
			assert eq != null;
			try {
				prots += Objects.requireNonNull(eq.getHelmet()).getEnchantmentLevel(Enchantment.PROTECTION);
			} catch(Exception exception) {
				// continue
			}

			try {
				prots += Objects.requireNonNull(eq.getChestplate()).getEnchantmentLevel(Enchantment.PROTECTION);
			} catch(Exception exception) {
				// continue
			}

			try {
				prots += Objects.requireNonNull(eq.getLeggings()).getEnchantmentLevel(Enchantment.PROTECTION);
			} catch(Exception exception) {
				// continue
			}

			try {
				prots += Objects.requireNonNull(eq.getBoots()).getEnchantmentLevel(Enchantment.PROTECTION);
			} catch(Exception exception) {
				// continue
			}
			finalDamage *= Math.max(0.5, 1 - prots * 0.025);

			if(type == DamageType.FALL) {
				finalDamage *= 0.5;
				try {
					double featherFalling = Objects.requireNonNull(eq.getBoots()).getEnchantmentLevel(Enchantment.FEATHER_FALLING);
					finalDamage *= Math.max(0.5, 1 - featherFalling * 0.1);
				} catch(Exception exception) {
					// continue
				}
			}
		}
		dealDamage(damagee, damager, finalDamage, type);
	}

	@SuppressWarnings("DuplicateExpressions")
	public static void dealDamage(LivingEntity damagee, Entity damager, double finalDamage, DamageType type) {
		if(finalDamage > 0) {

			// sweeping edge
			if(type == DamageType.MELEE && damager instanceof LivingEntity temp && temp.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.SWEEPING_EDGE)) {
				int level = temp.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.SWEEPING_EDGE);
				List<Entity> entities = damagee.getNearbyEntities(2, 2, 2);
				List<EntityType> doNotKill = CustomItems.createList();
				for(Entity entity : entities) {
					if(!doNotKill.contains(entity.getType()) && !entity.equals(damager) && entity instanceof LivingEntity entity1 && entity1.getHealth() > 0) {
						Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(damager, entity1, EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK, org.bukkit.damage.DamageSource.builder(org.bukkit.damage.DamageType.BAD_RESPAWN_POINT).build(), e.getDamage() * 0.125 * level));
						customMobs(entity1, damager, e.getDamage() * 0.125 * level, DamageType.MELEE_SWEEP);
					}
				}
			}

			damagee.playHurtAnimation(0.0F);
			damagee.getWorld().playSound(damagee, Objects.requireNonNull(damagee.getHurtSound()), 1.0F, 1.0F);

			double absorption = damagee.getAbsorptionAmount();
			double oldHealth = damagee.getHealth();
			boolean doesDie = finalDamage >= oldHealth + absorption;

			if(doesDie) {
				damagee.setHealth(0.1);
				e.setCancelled(false);
				e.setDamage(20);
				if(damagee instanceof EnderDragon dragon) {
					if(!(dragon instanceof CraftEnderDragon)) return;

					net.minecraft.world.entity.boss.enderdragon.EnderDragon nmsDragon =
							((CraftEnderDragon) dragon).getHandle();

					// Stop all movement immediately
					nmsDragon.setDeltaMovement(Vec3.ZERO); // Stop velocity

					// Set death time to 1 to start animation immediately
					try {
						Field deathTimeField = nmsDragon.getClass().getDeclaredField("dragonDeathTime");
						deathTimeField.setAccessible(true);
						deathTimeField.setInt(nmsDragon, 1);
					} catch(Exception e) {
						// Fallback to damage
						Bukkit.getLogger().warning("Failed to force Dragon death animation.");
						ServerLevel worldServer = ((CraftWorld) dragon.getWorld()).getHandle();
						DamageSource damageSource = nmsDragon.damageSources().generic();
						EnderDragonPart dragonPart = nmsDragon.head;
						nmsDragon.hurt(worldServer, dragonPart, damageSource, Float.MAX_VALUE);
					}
					if(!dragon.getScoreboardTags().contains("WitherKingDragon")) {
						PluginUtils.playGlobalSound(Sound.ENTITY_ENDER_DRAGON_DEATH);
					}
				}
				CustomDrops.loot(damagee, damager);
			} else {
				// fire aspect - should always apply
				if(type == DamageType.MELEE && damager instanceof LivingEntity temp && temp.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.FIRE_ASPECT)) {
					int level = temp.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.FIRE_ASPECT);
					damagee.setFireTicks(level * 80);
				} else if(flamingArrow) {
					damagee.setFireTicks(100);
					flamingArrow = false;
				}

				// absorption
				if(finalDamage > absorption) {
					damagee.setAbsorptionAmount(0.0);
					finalDamage -= absorption;
				} else {
					damagee.setAbsorptionAmount(absorption - finalDamage);
					finalDamage = 0.0;
				}

				// damage
				damagee.setHealth(oldHealth - finalDamage);

				triggerNonLethalAdvancements(damagee, damager, e.getDamage(), finalDamage, type, isBlocking);

				if(type == DamageType.MELEE || type == DamageType.MELEE_SWEEP || type == DamageType.IFRAME_ENVIRONMENTAL) {
					damagee.setNoDamageTicks(9);
				}

				if(damagee instanceof Mob && damager instanceof LivingEntity) {
					((Mob) damagee).setTarget((LivingEntity) damager);
				}

				// apply knockback
				if((type == DamageType.MELEE || type == DamageType.MELEE_SWEEP || type == DamageType.RANGED) && damager != null) {
					double antiKB = 1 - Objects.requireNonNull(damagee.getAttribute(Attribute.KNOCKBACK_RESISTANCE)).getValue();
					double enchantments = 1;
					if(damager instanceof LivingEntity livingEntity) {
						if(livingEntity.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.KNOCKBACK) && (type == DamageType.MELEE || type == DamageType.MELEE_SWEEP)) {
							enchantments += 0.66667 * livingEntity.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.KNOCKBACK);
						} else if(punchArrow > 0) {
							enchantments += 0.66667 * punchArrow;
							punchArrow = 0;
						}
					}
					double factor = 0.33333 * antiKB * enchantments;
					Vector oldVelocity = damagee.getVelocity();
					double x = oldVelocity.getX();
					double y = oldVelocity.getY();
					double z = oldVelocity.getZ();
					if(type == DamageType.RANGED) {
						factor *= 0.25;
						if(isTermArrow) {
							factor *= 0.5;
							isTermArrow = false;
						}
					}

					if(isBlocking) {
						factor *= 0.5;
					}

					if(damagee instanceof Player && !(damager instanceof Player)) {
						double rawYaw = damagee.getLocation().getYaw();
						double yaw = Math.toRadians(rawYaw);
						if(rawYaw <= -90) {
							x += factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw >= 90) {
							x += factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw < 0) {
							x += factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw >= 0) {
							x += factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
						}
					} else {
						double rawYaw = damager.getLocation().getYaw();
						double yaw = Math.toRadians(rawYaw);
						if(rawYaw <= -90) {
							x += factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw >= 90) {
							x += factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw < 0) {
							x += factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw >= 0) {
							x += factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
						}
					}
					damagee.setVelocity(new Vector(x, y, z));
				}

				// change nametag health
				PluginUtils.changeName(damagee);
			}
		}
	}

	private static void triggerNonLethalAdvancements(LivingEntity victim, Entity damager,
													 double originalDamage, double finalDamage,
													 DamageType type, boolean blocked) {
		DamageSource source = createDamageSource(victim, damager, type);

		// Player hurt entity advancement
		if(damager instanceof Player player) {
			ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
			net.minecraft.world.entity.Entity nmsVictim = ((CraftEntity) victim).getHandle();

			CriteriaTriggers.PLAYER_HURT_ENTITY.trigger(
					serverPlayer,
					nmsVictim,
					source,
					(float) originalDamage,
					(float) finalDamage,
					blocked
			);
		}

		// Entity hurt player advancement
		if(victim instanceof Player player) {
			ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

			CriteriaTriggers.ENTITY_HURT_PLAYER.trigger(
					serverPlayer,
					source,
					(float) originalDamage,
					(float) finalDamage,
					blocked
			);
		}
	}

	// You'll also need the createDamageSource helper:
	private static DamageSource createDamageSource(LivingEntity victim, Entity damager, DamageType type) {
		net.minecraft.world.entity.Entity nmsDamager = damager != null ? ((CraftEntity) damager).getHandle() : null;
		var sources = ((CraftLivingEntity) victim).getHandle().damageSources();

		return switch(type) {
			case RANGED ->
					damager instanceof Player ? sources.arrow(null, nmsDamager) : sources.mobProjectile(nmsDamager, (net.minecraft.world.entity.LivingEntity) nmsDamager);
			case MAGIC, PLAYER_MAGIC ->
					sources.indirectMagic(nmsDamager, nmsDamager);
			case FALL ->
					sources.fall();
			case ABSOLUTE ->
					sources.genericKill();
			default ->
					damager instanceof Player ? sources.playerAttack((ServerPlayer)nmsDamager) : sources.mobAttack((net.minecraft.world.entity.LivingEntity) nmsDamager);
		};
	}

	public static void setEvent(EntityDamageEvent e) {
		CustomDamage.e = e;
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		CustomDamage.e = e;
		if(e.getEntity() instanceof EnderCrystal crystal && crystal.getScoreboardTags().contains("SkyblockBoss") && e.getDamager() instanceof Player p) {
			crystal.remove();
			p.addScoreboardTag("HasCrystal");
			p.sendMessage(ChatColor.YELLOW + "You have picked up an Energy Crystal!");
			e.setCancelled(true);
		} else if(e.getEntity() instanceof LivingEntity entity) {
			e.setCancelled(true);
			if(entity.getHealth() > 0 && !entity.isDead()) {
				DamageType type;
				switch(e.getCause()) {
					case BLOCK_EXPLOSION, ENTITY_ATTACK, ENTITY_EXPLOSION, THORNS -> type = DamageType.MELEE;
					case ENTITY_SWEEP_ATTACK -> {
						if(e.getDamageSource().getDamageType().equals(org.bukkit.damage.DamageType.BAD_RESPAWN_POINT)) {
							type = DamageType.MELEE_SWEEP;
						} else {
							return;
						}
					}
					case PROJECTILE, SONIC_BOOM -> type = DamageType.RANGED;
					case DRAGON_BREATH, MAGIC -> type = DamageType.MAGIC;
					case FALLING_BLOCK -> type = DamageType.ENVIRONMENTAL;
					case LIGHTNING -> type = DamageType.IFRAME_ENVIRONMENTAL;
					case KILL -> type = DamageType.PLAYER_MAGIC; // this is so that custom calls work
					default -> {
						return;
					}
				}

				if(entity.getNoDamageTicks() == 0 || e.getDamager() instanceof AbstractArrow) {
					double originalDamage;
					if(e.getDamager() instanceof AbstractArrow arrow && arrow.getScoreboardTags().contains("TerminatorArrow")) {
						originalDamage = arrow.getDamage();
						isTermArrow = true;
					} else {
						originalDamage = e.getDamage();
					}

					// apply intelligence to players
					if(e.getDamager() instanceof Player p) {
						if(type.equals(DamageType.MELEE) && (e.getEntity() instanceof Monster || e.getEntity().getScoreboardTags().contains("SkyblockBoss") || e.getEntity() instanceof Player)) {
							try {
								Score score = Objects.requireNonNull(Objects.requireNonNull(Plugin.getInstance().getServer().getScoreboardManager()).getMainScoreboard().getObjective("Intelligence")).getScore(p.getName());
								if(score.getScore() < 2500) {
									score.setScore(score.getScore() + 1);
									p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.AQUA + "Intelligence: " + score.getScore() + "/2500"));
								} else {
									p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.AQUA + "Intelligence: " + score.getScore() + "/2500 " + ChatColor.RED + ChatColor.BOLD + "MAX INTELLIGENCE"));
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

	private final Map<LivingEntity, Long> noDamageTimes = new WeakHashMap<>();

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof LivingEntity entity) {
			e.setCancelled(true);
			DamageType type;
			switch(e.getCause()) {
				case THORNS -> type = DamageType.MELEE;
				case POISON, WITHER -> type = DamageType.MAGIC;
				case CAMPFIRE, CONTACT, DROWNING, DRYOUT, FIRE, FIRE_TICK, FREEZE, HOT_FLOOR, LAVA, MELTING, STARVATION,
					 SUFFOCATION -> type = DamageType.ENVIRONMENTAL;
				case CUSTOM -> type = DamageType.IFRAME_ENVIRONMENTAL;
				case FALL, FLY_INTO_WALL -> type = DamageType.FALL;
				case CRAMMING, KILL, SUICIDE, VOID, WORLD_BORDER -> type = DamageType.ABSOLUTE;
				default -> {
					return;
				}
			}

			CustomDamage.setEvent(e);

			long currentTime = System.currentTimeMillis();
			boolean hasLastDamageTime = noDamageTimes.containsKey(entity);
			long lastDamageTime = noDamageTimes.computeIfAbsent(entity, entity2 -> currentTime);

			if(hasLastDamageTime && currentTime - lastDamageTime > 490 || e.getCause().equals(EntityDamageEvent.DamageCause.KILL)) {
				customMobs(entity, null, e.getDamage(), type);
				noDamageTimes.put(entity, currentTime);
				if (entity instanceof Player player && !entity.isDead()) {
					triggerEnvironmentalDamageAdvancements(player, e.getCause(), e.getDamage(), e.getFinalDamage());
				}
			}

			if(entity.isDead()) {
				entity.remove();
			}
		}
	}

	private void triggerEnvironmentalDamageAdvancements(Player player, EntityDamageEvent.DamageCause cause,
														double originalDamage, double finalDamage) {
		ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

		// Create appropriate damage source based on cause
		DamageSource source = switch(cause) {
			case FIRE, FIRE_TICK -> serverPlayer.damageSources().inFire();
			case LAVA -> serverPlayer.damageSources().lava();
			case DROWNING -> serverPlayer.damageSources().drown();
			case FALL -> serverPlayer.damageSources().fall();
			case VOID -> serverPlayer.damageSources().fellOutOfWorld();
			case WITHER -> serverPlayer.damageSources().wither();
			case STARVATION -> serverPlayer.damageSources().starve();
			case FREEZE -> serverPlayer.damageSources().freeze();
			case SUFFOCATION -> serverPlayer.damageSources().inWall();
			default -> serverPlayer.damageSources().generic();
		};

		// Trigger entity_hurt_player for environmental damage
		CriteriaTriggers.ENTITY_HURT_PLAYER.trigger(
				serverPlayer,
				source,
				(float) originalDamage,
				(float) finalDamage,
				false  // Environmental damage isn't blocked
		);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		noDamageTimes.remove(e.getEntity());
	}
}