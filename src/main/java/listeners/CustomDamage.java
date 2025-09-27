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
	private static class DamageData {
		public EntityDamageEvent e;
		public LivingEntity damagee;
		public Entity damager;
		public double originalDamage;
		public double finalDamage;
		public DamageType type;
		public boolean isBlocking = false;
		public boolean flamingArrow = false;
		public int punchArrow = 0;
		public boolean isTermArrow;

		DamageData(EntityDamageEvent e, LivingEntity damagee, Entity damager, double originalDamage, DamageType type, boolean isTermArrow) {
			this.e = e;
			this.damagee = damagee;
			this.damager = damager;
			this.originalDamage = originalDamage;
			this.finalDamage = originalDamage;
			this.type = type;
			this.isTermArrow = isTermArrow;
		}
	}

	private static void customMobs(DamageData data) {
		data.isBlocking = data.damagee instanceof Player p && p.isBlocking();

		if(data.damager instanceof Projectile projectile) {
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
					data.flamingArrow = true;
				}

				if(arrow.getWeapon().containsEnchantment(Enchantment.PUNCH)) {
					data.punchArrow = arrow.getWeapon().getEnchantmentLevel(Enchantment.PUNCH);
				}
			}

			if(!data.isBlocking) {
				if(projectile instanceof SpectralArrow) {
					data.damagee.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0));
				}
				if(projectile instanceof Arrow a && a.hasCustomEffects()) {
					data.damagee.addPotionEffects(a.getCustomEffects());
				}
			}
			if(projectile.getShooter() instanceof LivingEntity temp) {
				data.damager = temp;
			}
		}

		// apply custom damage to special mobs before going through with general damage
		boolean doContinue = true;
		if(!data.type.equals(DamageType.ABSOLUTE)) {
			try {
				CustomMob damageeMob = CustomMob.getMob(data.damagee);
				CustomMob damagerMob = CustomMob.getMob(data.damager);

				// this section controls when bosses are damaged
				if(damageeMob != null) {
					doContinue = damageeMob.whenDamaged(data.damagee, data.damager, data.originalDamage, data.type);
				}

				// this section controls when bosses deal damage
				if(damagerMob != null) {
					doContinue = damagerMob.whenDamaging(data.damagee, data.damager, data.originalDamage, data.type);
				}
			} catch(NullPointerException exception) {
				// continue
			}
		}

		if(!data.isBlocking) {
			switch(data.damager) {
				case Wither ignored -> data.damagee.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1));
				case CaveSpider ignored ->
						data.damagee.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 0));
				case WitherSkeleton ignored ->
						data.damagee.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 0));
				case Husk ignored -> data.damagee.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 0));
				case Shulker ignored ->
						data.damagee.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 200, 0));
				case null, default -> {
				}
			}
		}

		if((data.damagee instanceof Player p && (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR)) || (data.damagee instanceof Wither wither && wither.getInvulnerabilityTicks() > 0)) {
			doContinue = false;
		}

		if(data.type == DamageType.ABSOLUTE || doContinue) {
			calculateFinalDamage(data);
		}
	}

	private static void calculateFinalDamage(DamageData data) {
		if(data.type != DamageType.ABSOLUTE) {
			// bonus damage to withers from hyperion
			if(data.damagee instanceof Wither && (data.type == DamageType.MELEE || data.type == DamageType.MELEE_SWEEP) && data.damager instanceof Player p && p.getInventory().getItemInMainHand().hasItemMeta() && p.getInventory().getItemInMainHand().getItemMeta().hasLore() && p.getInventory().getItemInMainHand().getItemMeta().getLore().getFirst().equals("skyblock/combat/scylla")) {
				data.finalDamage += 4;
			}

			// ice spray logic
			if(data.damagee.getScoreboardTags().contains("IceSprayed")) {
				data.finalDamage *= 1.1;
			}

			if(data.damagee.getScoreboardTags().contains("WitherShield")) {
				data.finalDamage *= 0.9;
			}

			if(data.damagee.getScoreboardTags().contains("HolyIce")) {
				data.finalDamage *= 0.25;
			}

			if(data.damager instanceof LivingEntity entity1) {
				if(entity1.getScoreboardTags().contains("IceSprayed")) {
					data.finalDamage *= 0.8;
				}
			}

			// shield logic (for weirdos)
			if(data.isBlocking && (data.type == DamageType.MELEE || data.type == DamageType.MELEE_SWEEP || data.type == DamageType.RANGED)) {
				data.finalDamage *= 0.5;
			}

			if(data.type == DamageType.MELEE || data.type == DamageType.MELEE_SWEEP || data.type == DamageType.RANGED || data.type == DamageType.PLAYER_MAGIC || data.type == DamageType.ENVIRONMENTAL || data.type == DamageType.IFRAME_ENVIRONMENTAL) {
				double armor = Objects.requireNonNull(data.damagee.getAttribute(Attribute.ARMOR)).getValue();
				data.finalDamage *= Math.max(0.25, 1 - armor * 0.0375);
			}

			double toughness = Math.max(Objects.requireNonNull(data.damagee.getAttribute(Attribute.ARMOR_TOUGHNESS)).getValue() - 8, 0); // only toughness values of 9 or more will give damage reduction
			data.finalDamage *= Math.max(0.2, 1 - toughness * 0.1);

			double resistance = 0;
			try {
				resistance = Objects.requireNonNull(data.damagee.getPotionEffect(PotionEffectType.RESISTANCE)).getAmplifier() + 1;
			} catch(Exception exception) {
				// continue
			}
			data.finalDamage *= Math.max(0.0, 1 - resistance * 0.2);

			// get prot levels
			double prots = 0;
			EntityEquipment eq = data.damagee.getEquipment();
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
			data.finalDamage *= Math.max(0.5, 1 - prots * 0.025);

			if(data.type == DamageType.FALL) {
				data.finalDamage *= 0.5;
				try {
					double featherFalling = Objects.requireNonNull(eq.getBoots()).getEnchantmentLevel(Enchantment.FEATHER_FALLING);
					data.finalDamage *= Math.max(0.5, 1 - featherFalling * 0.1);
				} catch(Exception exception) {
					// continue
				}
			}
		}
		dealDamage(data);
	}

	@SuppressWarnings("DuplicateExpressions")
	private static void dealDamage(DamageData data) {
		if(data.finalDamage > 0) {
			// sweeping edge
			if(data.type == DamageType.MELEE && data.damager instanceof LivingEntity temp && temp.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.SWEEPING_EDGE)) {
				int enchLevel = temp.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.SWEEPING_EDGE);
				List<Entity> entities = data.damagee.getNearbyEntities(2, 2, 2);
				List<EntityType> doNotKill = CustomItems.createList();
				for(Entity entity : entities) {
					if(!doNotKill.contains(entity.getType()) && !entity.equals(data.damager) && entity instanceof LivingEntity entity1 && entity1.getHealth() > 0) {
						net.minecraft.world.entity.Entity nmsAttacker = ((CraftEntity) data.damager).getHandle();
						net.minecraft.world.entity.LivingEntity nmsVictim = ((CraftLivingEntity) entity1).getHandle();
						ServerLevel level = ((CraftWorld) entity1.getWorld()).getHandle();
						net.minecraft.world.damagesource.DamageSource sweepSource = nmsVictim.damageSources().thorns(nmsAttacker);
						double sweepDamage = data.e.getDamage() * 0.125 * enchLevel;
						nmsVictim.hurtServer(level, sweepSource, (float) sweepDamage);
					}
				}
			}

			data.damagee.playHurtAnimation(0.0F);
			data.damagee.getWorld().playSound(data.damagee, Objects.requireNonNull(data.damagee.getHurtSound()), 1.0F, 1.0F);

			double absorption = data.damagee.getAbsorptionAmount();
			double oldHealth = data.damagee.getHealth();
			boolean doesDie = data.finalDamage >= oldHealth + absorption;

			// fire aspect - should always apply
			if(data.type == DamageType.MELEE && data.damager instanceof LivingEntity temp && temp.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.FIRE_ASPECT)) {
				int level = temp.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.FIRE_ASPECT);
				data.damagee.setFireTicks(level * 80);
			} else if(data.flamingArrow) {
				data.damagee.setFireTicks(100);
			}

			if(doesDie) {
				data.damagee.setHealth(0.1);
				data.e.setDamage(data.e.getDamage());
				if(data.damagee instanceof EnderDragon dragon) {
					if(!(dragon instanceof CraftEnderDragon)) return;

					net.minecraft.world.entity.boss.enderdragon.EnderDragon nmsDragon = ((CraftEnderDragon) dragon).getHandle();

					// Stop all movement immediately
					nmsDragon.setDeltaMovement(Vec3.ZERO); // Stop velocity

					// Set death time to 1 to start animation immediately
					try {
						Field deathTimeField = nmsDragon.getClass().getDeclaredField("dragonDeathTime");
						deathTimeField.setAccessible(true);
						deathTimeField.setInt(nmsDragon, 1);
					} catch(Exception exception) {
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
				CustomDrops.loot(data.damagee, data.damager);
			} else {
				data.e.setCancelled(true);

				// absorption
				if(data.finalDamage > absorption) {
					data.damagee.setAbsorptionAmount(0.0);
					data.finalDamage -= absorption;
				} else {
					data.damagee.setAbsorptionAmount(absorption - data.finalDamage);
					data.finalDamage = 0.0;
				}

				// damage
				data.damagee.setHealth(oldHealth - data.finalDamage);

				triggerNonLethalAdvancements(data.e, data.finalDamage, data.isBlocking);

				if(data.type == DamageType.MELEE || data.type == DamageType.MELEE_SWEEP || data.type == DamageType.IFRAME_ENVIRONMENTAL) {
					data.damagee.setNoDamageTicks(9);
				}

				if(data.damagee instanceof Mob && data.damager instanceof LivingEntity) {
					((Mob) data.damagee).setTarget((LivingEntity) data.damager);
				}

				// apply knockback
				if((data.type == DamageType.MELEE || data.type == DamageType.MELEE_SWEEP || data.type == DamageType.RANGED) && data.damager != null) {
					double antiKB = 1 - Objects.requireNonNull(data.damagee.getAttribute(Attribute.KNOCKBACK_RESISTANCE)).getValue();
					double enchantments = 1;
					if(data.damager instanceof LivingEntity livingEntity) {
						if(livingEntity.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.KNOCKBACK) && (data.type == DamageType.MELEE || data.type == DamageType.MELEE_SWEEP)) {
							enchantments += 0.66667 * livingEntity.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.KNOCKBACK);
						} else if(data.punchArrow > 0) {
							enchantments += 0.66667 * data.punchArrow;
						}
					}
					double factor = 0.33333 * antiKB * enchantments;
					Vector oldVelocity = data.damagee.getVelocity();
					double x = oldVelocity.getX();
					double y = oldVelocity.getY();
					double z = oldVelocity.getZ();
					if(data.type == DamageType.RANGED) {
						factor *= 0.25;
						if(data.isTermArrow) {
							factor *= 0.5;
						}
					}

					if(data.isBlocking) {
						factor *= 0.5;
					}

					// player damaged by non-player: use player's direction to determine KB
					if(data.damagee instanceof Player && !(data.damager instanceof Player)) {
						double rawYaw = data.damagee.getLocation().getYaw();
						double yaw = Math.toRadians(rawYaw);
						if(rawYaw <= -90) {
							x += factor * data.damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * data.damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw >= 90) {
							x += factor * data.damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * data.damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw < 0) {
							x += factor * data.damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * data.damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw >= 0) {
							x += factor * data.damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * data.damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
						}
					} else {
						// any entity damaged by a player: use attacker's directivn to determine kb
						double rawYaw = data.damager.getLocation().getYaw();
						double yaw = Math.toRadians(rawYaw);
						if(rawYaw <= -90) {
							x += factor * data.damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * data.damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw >= 90) {
							x += factor * data.damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * data.damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw < 0) {
							x += factor * data.damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * data.damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw >= 0) {
							x += factor * data.damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
							y = 0.2 * antiKB;
							z += factor * data.damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
						}
					}
					data.damagee.setVelocity(new Vector(x, y, z));
				}

				// change nametag health
				PluginUtils.changeName(data.damagee);
			}
		}
	}

	private static void triggerNonLethalAdvancements(EntityDamageEvent e, double finalDamage, boolean blocked) {
		if(!(e.getEntity() instanceof LivingEntity victim)) return;

		// Extract damager from the original event
		Entity damager = null;
		if(e instanceof EntityDamageByEntityEvent entityEvent) {
			damager = entityEvent.getDamager();
		}

		// Convert Bukkit DamageSource to NMS DamageSource
		net.minecraft.world.damagesource.DamageSource nmsSource = convertBukkitDamageSource(e.getDamageSource(), victim);

		// Player hurt entity advancement
		if(damager instanceof Player player) {
			ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
			net.minecraft.world.entity.Entity nmsVictim = ((CraftEntity) victim).getHandle();

			CriteriaTriggers.PLAYER_HURT_ENTITY.trigger(serverPlayer, nmsVictim, nmsSource, (float) e.getDamage(), (float) finalDamage, blocked);
		}

		// Entity hurt player advancement
		if(victim instanceof Player player) {
			ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
			CriteriaTriggers.ENTITY_HURT_PLAYER.trigger(serverPlayer, nmsSource, (float) e.getDamage(), (float) finalDamage, blocked);
		}
	}

	private static net.minecraft.world.damagesource.DamageSource convertBukkitDamageSource(org.bukkit.damage.DamageSource bukkitSource, LivingEntity victim) {

		net.minecraft.world.damagesource.DamageSources sources = ((CraftLivingEntity) victim).getHandle().damageSources();

		org.bukkit.damage.DamageType damageType = bukkitSource.getDamageType();
		Entity directEntity = bukkitSource.getDirectEntity();
		Entity causingEntity = bukkitSource.getCausingEntity();

		net.minecraft.world.entity.Entity nmsDirectEntity = directEntity != null ? ((CraftEntity) directEntity).getHandle() : null;
		net.minecraft.world.entity.Entity nmsCausingEntity = causingEntity != null ? ((CraftEntity) causingEntity).getHandle() : null;

		// Complete mapping of all damage types
		if(damageType == org.bukkit.damage.DamageType.IN_FIRE) {
			return sources.inFire();
		} else if(damageType == org.bukkit.damage.DamageType.LIGHTNING_BOLT) {
			return sources.lightningBolt();
		} else if(damageType == org.bukkit.damage.DamageType.ON_FIRE) {
			return sources.onFire();
		} else if(damageType == org.bukkit.damage.DamageType.LAVA) {
			return sources.lava();
		} else if(damageType == org.bukkit.damage.DamageType.HOT_FLOOR) {
			return sources.hotFloor();
		} else if(damageType == org.bukkit.damage.DamageType.IN_WALL) {
			return sources.inWall();
		} else if(damageType == org.bukkit.damage.DamageType.CRAMMING) {
			return sources.cramming();
		} else if(damageType == org.bukkit.damage.DamageType.DROWN) {
			return sources.drown();
		} else if(damageType == org.bukkit.damage.DamageType.STARVE) {
			return sources.starve();
		} else if(damageType == org.bukkit.damage.DamageType.CACTUS) {
			return sources.cactus();
		} else if(damageType == org.bukkit.damage.DamageType.FALL) {
			return sources.fall();
		} else if(damageType == org.bukkit.damage.DamageType.FLY_INTO_WALL) {
			return sources.flyIntoWall();
		} else if(damageType == org.bukkit.damage.DamageType.OUT_OF_WORLD) {
			return sources.fellOutOfWorld();
		} else if(damageType == org.bukkit.damage.DamageType.GENERIC) {
			return sources.generic();
		} else if(damageType == org.bukkit.damage.DamageType.MAGIC) {
			return sources.magic();
		} else if(damageType == org.bukkit.damage.DamageType.WITHER) {
			return sources.wither();
		} else if(damageType == org.bukkit.damage.DamageType.DRAGON_BREATH) {
			return sources.dragonBreath();
		} else if(damageType == org.bukkit.damage.DamageType.DRY_OUT) {
			return sources.dryOut();
		} else if(damageType == org.bukkit.damage.DamageType.SWEET_BERRY_BUSH) {
			return sources.sweetBerryBush();
		} else if(damageType == org.bukkit.damage.DamageType.FREEZE) {
			return sources.freeze();
		} else if(damageType == org.bukkit.damage.DamageType.STALAGMITE) {
			return sources.stalagmite();
		} else if(damageType == org.bukkit.damage.DamageType.OUTSIDE_BORDER) {
			return sources.outOfBorder();
		} else if(damageType == org.bukkit.damage.DamageType.GENERIC_KILL) {
			return sources.genericKill();
		}

		// Entity-based damage types
		else if(damageType == org.bukkit.damage.DamageType.ARROW) {
			if(nmsDirectEntity instanceof net.minecraft.world.entity.projectile.AbstractArrow arrow) {
				return sources.arrow(arrow, nmsCausingEntity);
			}
			return sources.generic();
		} else if(damageType == org.bukkit.damage.DamageType.TRIDENT) {
			return sources.trident(nmsDirectEntity, nmsCausingEntity);
		} else if(damageType == org.bukkit.damage.DamageType.MOB_ATTACK && nmsCausingEntity instanceof net.minecraft.world.entity.LivingEntity living) {
			return sources.mobAttack(living);
		} else if(damageType == org.bukkit.damage.DamageType.PLAYER_ATTACK && nmsCausingEntity instanceof ServerPlayer player) {
			return sources.playerAttack(player);
		} else if(damageType == org.bukkit.damage.DamageType.THORNS && nmsCausingEntity != null) {
			return sources.thorns(nmsCausingEntity);
		} else if(damageType == org.bukkit.damage.DamageType.EXPLOSION) {
			return sources.explosion(null, nmsCausingEntity);
		} else if(damageType == org.bukkit.damage.DamageType.MOB_PROJECTILE && nmsCausingEntity instanceof net.minecraft.world.entity.LivingEntity living) {
			return sources.mobProjectile(nmsDirectEntity, living);
		} else if(damageType == org.bukkit.damage.DamageType.FIREWORKS) {
			if(nmsDirectEntity instanceof net.minecraft.world.entity.projectile.FireworkRocketEntity firework) {
				return sources.fireworks(firework, nmsCausingEntity);
			}
			return sources.generic();
		} else if(damageType == org.bukkit.damage.DamageType.FIREBALL) {
			if(nmsDirectEntity instanceof net.minecraft.world.entity.projectile.Fireball fireball) {
				return sources.fireball(fireball, nmsCausingEntity);
			}
			return sources.generic();
		} else if(damageType == org.bukkit.damage.DamageType.UNATTRIBUTED_FIREBALL) {
			if(nmsDirectEntity instanceof net.minecraft.world.entity.projectile.Fireball fireball) {
				return sources.fireball(fireball, nmsCausingEntity);
			}
			return sources.generic();
		} else if(damageType == org.bukkit.damage.DamageType.WITHER_SKULL) {
			if(nmsDirectEntity instanceof net.minecraft.world.entity.projectile.WitherSkull witherSkull) {
				return sources.witherSkull(witherSkull, nmsCausingEntity);
			}
			return sources.generic();
		} else if(damageType == org.bukkit.damage.DamageType.THROWN) {
			return sources.thrown(nmsDirectEntity, nmsCausingEntity);
		} else if(damageType == org.bukkit.damage.DamageType.INDIRECT_MAGIC) {
			return sources.indirectMagic(nmsDirectEntity, nmsCausingEntity);
		} else if(damageType == org.bukkit.damage.DamageType.FALLING_BLOCK && nmsDirectEntity != null) {
			return sources.fallingBlock(nmsDirectEntity);
		} else if(damageType == org.bukkit.damage.DamageType.FALLING_ANVIL && nmsDirectEntity != null) {
			return sources.anvil(nmsDirectEntity);
		} else if(damageType == org.bukkit.damage.DamageType.FALLING_STALACTITE && nmsDirectEntity != null) {
			return sources.fallingStalactite(nmsDirectEntity);
		} else if(damageType == org.bukkit.damage.DamageType.STING && nmsCausingEntity instanceof net.minecraft.world.entity.LivingEntity living) {
			return sources.sting(living);
		} else if(damageType == org.bukkit.damage.DamageType.MOB_ATTACK_NO_AGGRO && nmsCausingEntity instanceof net.minecraft.world.entity.LivingEntity living) {
			return sources.noAggroMobAttack(living);
		} else if(damageType == org.bukkit.damage.DamageType.PLAYER_EXPLOSION && nmsCausingEntity instanceof ServerPlayer player) {
			return sources.explosion(player, player);
		} else if(damageType == org.bukkit.damage.DamageType.SONIC_BOOM && nmsCausingEntity != null) {
			return sources.sonicBoom(nmsCausingEntity);
		} else if(damageType == org.bukkit.damage.DamageType.BAD_RESPAWN_POINT) {
			return sources.badRespawnPointExplosion(new Vec3(0, 0, 0));
		}

		// Fallback for any unmapped or new damage types
		else {
			System.err.println("Unmapped damage type: " + damageType);
			return sources.generic();
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof EnderCrystal crystal && crystal.getScoreboardTags().contains("SkyblockBoss") && e.getDamager() instanceof Player p) {
			crystal.remove();
			p.addScoreboardTag("HasCrystal");
			p.sendMessage(ChatColor.YELLOW + "You have picked up an Energy Crystal!");
		} else if(e.getEntity() instanceof LivingEntity entity) {
			if(entity.getHealth() > 0 && !entity.isDead()) {
				DamageType type;
				switch(e.getCause()) {
					case BLOCK_EXPLOSION, ENTITY_ATTACK -> type = DamageType.MELEE;
					case ENTITY_SWEEP_ATTACK, THORNS -> type = DamageType.MELEE_SWEEP; // thorns is here to make sweep attacks work without causing stack overflows
					case PROJECTILE, SONIC_BOOM -> type = DamageType.RANGED;
					case DRAGON_BREATH, MAGIC -> type = DamageType.MAGIC;
					case FALLING_BLOCK -> type = DamageType.ENVIRONMENTAL;
					case LIGHTNING -> type = DamageType.IFRAME_ENVIRONMENTAL;
					case CUSTOM, ENTITY_EXPLOSION -> type = DamageType.PLAYER_MAGIC; // this is so that custom calls work, and avoiding an infinite loop on TNT explosions
					case KILL, SUICIDE, VOID -> type = DamageType.ABSOLUTE; // this is so that custom calls work
					default -> {
						return;
					}
				}

				boolean isTermArrow = false;
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
					customMobs(new DamageData(e, entity, damager, originalDamage, type, isTermArrow));
				}
			}
		}
	}

	private final Map<LivingEntity, Long> noDamageTimes = new WeakHashMap<>();

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof LivingEntity entity) {
			DamageType type;
			switch(e.getCause()) {
//				case THORNS -> type = DamageType.MELEE;
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

			long currentTime = System.currentTimeMillis();
			boolean hasLastDamageTime = noDamageTimes.containsKey(entity);
			long lastDamageTime = noDamageTimes.computeIfAbsent(entity, entity2 -> currentTime);

			if(hasLastDamageTime && currentTime - lastDamageTime > 490 || e.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM)) {
				customMobs(new DamageData(e, entity, null, e.getDamage(), type, false));
				noDamageTimes.put(entity, currentTime);
				if(entity instanceof Player player && !entity.isDead()) {
					triggerEnvironmentalDamageAdvancements(player, e.getCause(), e.getDamage(), e.getFinalDamage());
				}
			}

			if(entity.isDead()) {
				entity.remove();
			}
		}
	}

	private void triggerEnvironmentalDamageAdvancements(Player player, EntityDamageEvent.DamageCause cause, double originalDamage, double finalDamage) {
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
		CriteriaTriggers.ENTITY_HURT_PLAYER.trigger(serverPlayer, source, (float) originalDamage, (float) finalDamage, false  // Environmental damage isn't blocked
		);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		noDamageTimes.remove(e.getEntity());
	}
}

