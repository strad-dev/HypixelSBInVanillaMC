package listeners;

import misc.Plugin;
import misc.PluginUtils;
import mobs.CustomMob;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonDeathPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_21_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R4.entity.*;
import org.bukkit.craftbukkit.v1_21_R4.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.*;

public class CustomDamage implements Listener {
	private static class DamageData {
		public EntityDamageEvent e;
		public boolean isBlocking;
		public boolean flamingArrow = false;
		public int punchArrow = 0;
		public boolean isTermArrow = false;
		public double originalDamage;
		public boolean lightningInvolved = false;
		public LightningBolt lightningBolt = null;
		public boolean isTridentAttack = false;
		public boolean tridentChanneling = false;
		public Trident trident = null;

		public DamageData(EntityDamageByEntityEvent e) {
			this.originalDamage = e.getDamage();
			this.e = e;
			this.isBlocking = e.getEntity() instanceof Player p && p.isBlocking();
			if(e.getDamager() instanceof Projectile projectile) {
				// stop stupidly annoying arrows
				if(projectile instanceof Trident temp) {
					this.isTridentAttack = true;
					this.trident = temp;
					ItemStack tridentItem = trident.getItem();
					if(tridentItem.containsEnchantment(Enchantment.CHANNELING)) {
						this.tridentChanneling = true;

						// Check if conditions are right for channeling
						LivingEntity target = (LivingEntity) e.getEntity();
						World world = target.getWorld();
						if(world.hasStorm() && world.getHighestBlockYAt(target.getLocation()) <= target.getLocation().getBlockY()) {
							// Strike lightning
							LightningStrike lightning = world.strikeLightning(target.getLocation());
							this.lightningInvolved = true;
							this.lightningBolt = ((CraftLightningStrike) lightning).getHandle();
						}
					}
				} else if(projectile instanceof AbstractArrow arrow) {
					if(arrow.getWeapon().containsEnchantment(Enchantment.FLAME)) {
						this.flamingArrow = true;
					}

					if(arrow.getWeapon().containsEnchantment(Enchantment.PUNCH)) {
						this.punchArrow = arrow.getWeapon().getEnchantmentLevel(Enchantment.PUNCH);
					}

					if(arrow.getScoreboardTags().contains("TerminatorArrow")) {
						this.isTermArrow = true;
						this.originalDamage = arrow.getDamage();
					}
				}
			}

			if(e.getDamager() instanceof LightningStrike lightning) {
				this.lightningInvolved = true;
				this.lightningBolt = ((CraftLightningStrike) lightning).getHandle();
			}
		}

		public DamageData(EntityDamageEvent e) {
			this.originalDamage = e.getDamage();
			this.e = e;
			this.isBlocking = e.getEntity() instanceof Player p && p.isBlocking();
		}

		public DamageData(LivingEntity damagee, Entity damager, double originalDamage) {
			this.originalDamage = originalDamage;
			this.isBlocking = damagee instanceof Player p && p.isBlocking();
			if(damager instanceof Projectile projectile) {
				// stop stupidly annoying arrows
				if(projectile instanceof Trident temp) {
					this.isTridentAttack = true;
					this.trident = temp;
					ItemStack tridentItem = trident.getItem();
					if(tridentItem.containsEnchantment(Enchantment.CHANNELING)) {
						this.tridentChanneling = true;

						// Check if conditions are right for channeling
						World world = damagee.getWorld();
						if(world.hasStorm() && world.getHighestBlockYAt(damagee.getLocation()) <= damagee.getLocation().getBlockY()) {
							// Strike lightning
							LightningStrike lightning = world.strikeLightning(damagee.getLocation());
							this.lightningInvolved = true;
							this.lightningBolt = ((CraftLightningStrike) lightning).getHandle();
							damager.getWorld().playSound(damager, Sound.ITEM_TRIDENT_THUNDER, 1f, 1f);
						}
					}
				} else if(projectile instanceof AbstractArrow arrow) {
					if(arrow.getWeapon().containsEnchantment(Enchantment.FLAME)) {
						this.flamingArrow = true;
					}

					if(arrow.getWeapon().containsEnchantment(Enchantment.PUNCH)) {
						this.punchArrow = arrow.getWeapon().getEnchantmentLevel(Enchantment.PUNCH);
					}

					if(arrow.getScoreboardTags().contains("TerminatorArrow")) {
						this.isTermArrow = true;
						this.originalDamage = arrow.getDamage();
					}
				}
			}

			if(damager instanceof LightningStrike lightning) {
				this.lightningInvolved = true;
				this.lightningBolt = ((CraftLightningStrike) lightning).getHandle();
			}
		}
	}

	public static void customMobs(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		customMobs(damagee, damager, originalDamage, type, new DamageData(damagee, damager, originalDamage));
	}

	private static void handleTridentHit(Trident trident, DamageData data) {
		ItemStack tridentItem = trident.getItem();
		trident.setVelocity(new Vector(0, -0.1, 0)); // Small downward velocity to make it drop

		// Riptide damage bonus (if thrown during rain/water)
		if(tridentItem.containsEnchantment(Enchantment.RIPTIDE)) {
			int riptideLevel = tridentItem.getEnchantmentLevel(Enchantment.RIPTIDE);
			data.originalDamage += riptideLevel * 2; // Bonus damage for riptide
		}
	}

	private static void customMobs(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(damager instanceof Projectile projectile) {
			originalDamage = data.originalDamage;
			// stop stupidly annoying arrows
			if(projectile instanceof Trident trident) {
				handleTridentHit(trident, data);
			} else if(projectile instanceof AbstractArrow arrow) {
				if(arrow.getPierceLevel() == 0) {
					arrow.remove();
				} else {
					arrow.setPierceLevel(arrow.getPierceLevel() - 1);
					Vector arrowSpeed = arrow.getVelocity();
					Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> arrow.setVelocity(arrowSpeed), 1L);
				}
			}

			if(!data.isBlocking) {
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
			if(!data.isBlocking) {
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
			calculateFinalDamage(damagee, damager, originalDamage, type, data);
		}
	}

	public static void calculateFinalDamage(LivingEntity damagee, Entity damager, double finalDamage, DamageType type) {
		calculateFinalDamage(damagee, damager, finalDamage, type, new DamageData(damagee, damager, finalDamage));
	}

	private static void calculateFinalDamage(LivingEntity damagee, Entity damager, double finalDamage, DamageType type, DamageData data) {
		if(type != DamageType.ABSOLUTE) {
			// bonus damage to withers from hyperion
			if(damagee instanceof Wither && (type == DamageType.MELEE || type == DamageType.MELEE_SWEEP) && damager instanceof Player p && p.getInventory().getItemInMainHand().hasItemMeta() && p.getInventory().getItemInMainHand().getItemMeta().hasLore() && p.getInventory().getItemInMainHand().getItemMeta().getLore().getFirst().equals("skyblock/combat/scylla")) {
				finalDamage += 4;
			}

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

			// shield logic (for weirdos)
			if(data.isBlocking && (type == DamageType.MELEE || type == DamageType.MELEE_SWEEP || type == DamageType.RANGED || type == DamageType.RANGED_SPECIAL)) {
				finalDamage *= 0.5;
			}

			double breach = 0;
			if(damager instanceof LivingEntity entity && entity.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.BREACH)) {
				breach = entity.getItemInUse().getEnchantmentLevel(Enchantment.BREACH);
			}

			if(type == DamageType.MELEE || type == DamageType.MELEE_SWEEP || type == DamageType.RANGED || type == DamageType.RANGED_SPECIAL || type == DamageType.PLAYER_MAGIC || type == DamageType.ENVIRONMENTAL || type == DamageType.IFRAME_ENVIRONMENTAL) {
				double armor = Objects.requireNonNull(damagee.getAttribute(Attribute.ARMOR)).getValue();
				armor *= 1 - breach * 0.125;
				finalDamage *= Math.max(0.25, 1 - armor * 0.0375);
			}

			double toughness = Math.max(Objects.requireNonNull(damagee.getAttribute(Attribute.ARMOR_TOUGHNESS)).getValue() - 8, 0); // only toughness values of 9 or more will give damage reduction
			toughness *= 1 - breach * 0.125;
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
				try {
					double featherFalling = Objects.requireNonNull(eq.getBoots()).getEnchantmentLevel(Enchantment.FEATHER_FALLING);
					finalDamage *= Math.max(0.5, (1 - featherFalling * 0.1) * damagee.getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER).getValue());
				} catch(Exception exception) {
					// continue
				}
			}
		}
		dealDamage(damagee, damager, finalDamage, type, data);
	}

	private static void dealDamage(LivingEntity damagee, Entity damager, double finalDamage, DamageType type, DamageData data) {
		if(finalDamage > 0) {

			// sweeping edge
			if(type == DamageType.MELEE && damager instanceof LivingEntity temp && temp.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.SWEEPING_EDGE)) {
				int level = temp.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.SWEEPING_EDGE);
				List<Entity> entities = damagee.getNearbyEntities(2, 2, 2);
				List<EntityType> doNotKill = CustomItems.createList();
				for(Entity entity : entities) {
					if(!doNotKill.contains(entity.getType()) && !entity.equals(damager) && entity instanceof LivingEntity entity1 && entity1.getHealth() > 0) {
						customMobs(entity1, damager, data.originalDamage * 0.125 * level, DamageType.MELEE_SWEEP);
					}
				}
			}

			damagee.playHurtAnimation(0.0F);
			damagee.getWorld().playSound(damagee, Objects.requireNonNull(damagee.getHurtSound()), 1.0F, 1.0F);

			double absorption = damagee.getAbsorptionAmount();
			double oldHealth = damagee.getHealth();
			boolean doesDie = finalDamage >= oldHealth + absorption;

			// fire aspect - should always apply
			if(type == DamageType.MELEE && damager instanceof LivingEntity temp && temp.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.FIRE_ASPECT)) {
				int level = temp.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.FIRE_ASPECT);
				damagee.setFireTicks(level * 80);
			} else if(data.flamingArrow) {
				damagee.setFireTicks(100);
			}

			boolean isPhysicalHit = type == DamageType.MELEE || type == DamageType.MELEE_SWEEP || type == DamageType.RANGED || type == DamageType.RANGED_SPECIAL;
			// handle particles and wind burst
			if(damager instanceof Player p) {
				Location particleLoc = damagee.getLocation().add(0, damagee.getHeight() / 2, 0);
				ItemStack weapon = p.getEquipment().getItemInMainHand();
				boolean isCrit = p.getFallDistance() > 0 && type == DamageType.MELEE;

				// Critical hit particles
				if(isCrit) {
					damagee.getWorld().spawnParticle(Particle.CRIT, particleLoc, 80);
				}

				// Enchanted hit particles
				if(!weapon.getEnchantments().isEmpty() && isPhysicalHit) {
					damagee.getWorld().spawnParticle(Particle.ENCHANTED_HIT, particleLoc, Math.min((int) (data.originalDamage * 8), 80));
				}

				// Damage Indicator particles
				if(data.originalDamage > 2 && isPhysicalHit) {
					damagee.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, particleLoc, Math.min((int) (data.originalDamage / 2), 32));
				}

				// Wind Burst mechanics
				if(weapon.containsEnchantment(Enchantment.WIND_BURST) && p.getFallDistance() >= 1.5) {
					// TODO implement wind burst mechanics
				}
			}

			// handle raid mechanics
			if(damagee instanceof Raider raider) {
				net.minecraft.world.entity.raid.Raider nmsRaider = ((CraftRaider) raider).getHandle();
				net.minecraft.world.entity.raid.Raid nmsRaid = nmsRaider.getCurrentRaid();

				if(nmsRaid != null) {
					if(damager instanceof Player player) {
						ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
						nmsRaid.addHeroOfTheVillage(nmsPlayer);
					}
				} else {
					ServerLevel world = ((CraftWorld) raider.getWorld()).getHandle();
					Raids raids = world.getRaids();

					BlockPos pos = new BlockPos(raider.getLocation().getBlockX(), raider.getLocation().getBlockY(), raider.getLocation().getBlockZ());
					net.minecraft.world.entity.raid.Raid raidAtPos = raids.getNearbyRaid(pos, 128);

					if(raidAtPos != null) {
						if(damager instanceof Player player) {
							ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
							raidAtPos.addHeroOfTheVillage(nmsPlayer);
						}
					}
				}
			}

			if(doesDie) {
				if(type != DamageType.ABSOLUTE && (damagee.getEquipment().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING) || damagee.getEquipment().getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING))) {
					if(damagee.getEquipment().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING)) {
						damagee.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
					} else if(damagee.getEquipment().getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING)) {
						damagee.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
					}

					if(damagee instanceof Player p) {
						p.sendTitle(ChatColor.BOLD + "" + ChatColor.YELLOW + "\uD83D\uDC7C", ChatColor.DARK_GREEN + "Totem of Undying Used!", 2, 36, 2);
						ServerPlayer serverPlayer = ((CraftPlayer) p).getHandle();
						ItemStack totemStack = new ItemStack(Material.TOTEM_OF_UNDYING);
						net.minecraft.world.item.ItemStack nmsTotem = CraftItemStack.asNMSCopy(totemStack);
						CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, nmsTotem);
					}
					damagee.getWorld().playSound(damagee, Sound.ITEM_TOTEM_USE, 1.0F, 1.0F);
					damagee.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, damagee.getLocation(), 512);

					damagee.setHealth(1.0);
					damagee.getActivePotionEffects().forEach(effect -> damagee.removePotionEffect(effect.getType()));
					damagee.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 900, 1));
					damagee.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 800, 0));
					damagee.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 1));
					triggerAllRelevantAdvancements(damagee, damager, type, data.originalDamage, finalDamage, data.isBlocking, false, data);
				} else {
					if(damagee instanceof Villager villager && damager instanceof Zombie) {
						villager.zombify();
						PluginUtils.changeName(villager);
					} else {
						CustomDrops.loot(damagee, damager);

						// handle ender dragons specially
						if(damagee instanceof EnderDragon dragon) {
							if(!(dragon instanceof CraftEnderDragon)) return;
							net.minecraft.world.entity.boss.enderdragon.EnderDragon nmsDragon = ((CraftEnderDragon) dragon).getHandle();
							nmsDragon.getPhaseManager().setPhase(EnderDragonPhase.DYING);
							DragonPhaseInstance phase = nmsDragon.getPhaseManager().getCurrentPhase();

							// force dragon's target location to its location
							if(phase instanceof DragonDeathPhase deathPhase) {
								try {
									Field targetField = DragonDeathPhase.class.getDeclaredField("targetLocation");
									targetField.setAccessible(true);
									Location l = dragon.getLocation();
									targetField.set(deathPhase, new Vec3(l.getX(), l.getY(), l.getZ()));

								} catch(Exception e) {
									e.printStackTrace();
								}
							}

							nmsDragon.setDeltaMovement(Vec3.ZERO);
							nmsDragon.setHealth(1.0F);

							// Set death time to 1 to start animation immediately
							try {
								Field deathTimeField = nmsDragon.getClass().getDeclaredField("dragonDeathTime");
								deathTimeField.setAccessible(true);
								deathTimeField.setInt(nmsDragon, 1);
							} catch(Exception e) {
								// Fallback
								Bukkit.getLogger().warning("Failed to force Dragon death animation.");
							}
							if(!dragon.getScoreboardTags().contains("WitherKingDragon")) {
								PluginUtils.playGlobalSound(Sound.ENTITY_ENDER_DRAGON_DEATH);
							}
							dragon.setSilent(true);
							if(damager == null) {
								damager = PluginUtils.getNearestPlayer(dragon);
							}
							Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
								ExperienceOrb orb = (ExperienceOrb) dragon.getWorld().spawnEntity(dragon.getLocation(), EntityType.EXPERIENCE_ORB);
								orb.setExperience(64000);
							}, 200);
						} else {
							damagee.setHealth(0.0);
						}


					}
					if(damagee instanceof Player p) {
						if(data.e != null) {
							DamageSource damageSource = convertBukkitDamageSource(data.e.getDamageSource(), p);
							ServerPlayer nmsPlayer = ((CraftPlayer) p).getHandle();
							Component message = damageSource.getLocalizedDeathMessage(nmsPlayer);
							Bukkit.broadcastMessage(message.getString());
						} else {
							String damagerName;
							if(damager != null) {
								damagerName = damager.getCustomName();
							} else {
								damagerName = "absolutely no one";
							}
							if(type == DamageType.MELEE || type == DamageType.MELEE_SWEEP) {
								Bukkit.broadcastMessage(p.getName() + " was slain by " + damagerName);
							} else if(type == DamageType.RANGED) {
								Bukkit.broadcastMessage(p.getName() + " was shot by " + damagerName);
							} else if(type == DamageType.RANGED_SPECIAL) {
								Bukkit.broadcastMessage(p.getName() + " was killed by " + damagerName + "'s lasers");
							} else if(type == DamageType.MAGIC || type == DamageType.PLAYER_MAGIC) {
								Bukkit.broadcastMessage(p.getName() + " was killed by " + damagerName + "'s magic");
							} else if(type == DamageType.ENVIRONMENTAL || type == DamageType.IFRAME_ENVIRONMENTAL) {
								Bukkit.broadcastMessage(p.getName() + " was killed by the world");
							} else if(type == DamageType.FALL) {
								Bukkit.broadcastMessage(p.getName() + " fell to their death");
							} else if(type == DamageType.ABSOLUTE) {
								Bukkit.broadcastMessage(p.getName() + " fell out of the world");
							}
						}
					}
					triggerAllRelevantAdvancements(damagee, damager, type, data.originalDamage, finalDamage, data.isBlocking, true, data);
				}
			} else {
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
				if(type == DamageType.MELEE || type == DamageType.MELEE_SWEEP) {
					damagee.setNoDamageTicks(9);
				}

				if(damagee instanceof Mob && damager instanceof LivingEntity) {
					((Mob) damagee).setTarget((LivingEntity) damager);
				}

				// special ender dragon knockback to make zero- and one-cycling possible
				if(damagee instanceof EnderDragon dragon && data.e != null && data.e.getCause() == DamageCause.BLOCK_EXPLOSION) {
					Vector v = dragon.getVelocity();
					if(dragon.getPhase() == EnderDragon.Phase.LAND_ON_PORTAL) {
						dragon.setVelocity(new Vector(v.getX(), 0.25, v.getZ()));
					} else {
						dragon.setVelocity(new Vector(v.getX(), 0.333333, v.getZ()));
					}
					damager = PluginUtils.getNearestPlayer(dragon);
				} else if(isPhysicalHit && damager != null) {
				// apply knockback
					double antiKB = 1 - Objects.requireNonNull(damagee.getAttribute(Attribute.KNOCKBACK_RESISTANCE)).getValue();
					double enchantments = 1;

					if(damager instanceof LivingEntity livingEntity) {
						if(livingEntity.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.KNOCKBACK) && (type == DamageType.MELEE || type == DamageType.MELEE_SWEEP)) {
							enchantments += 0.66667 * livingEntity.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.KNOCKBACK);
						} else if(data.punchArrow > 0) {
							enchantments += 0.66667 * data.punchArrow;
						}
					}

					double factor = 0.33333 * antiKB * enchantments;

					// Apply type-specific modifiers
					if(damager.getFallDistance() > 0) {
						factor *= 1.2;
					}

					if(type == DamageType.RANGED || type == DamageType.RANGED_SPECIAL) {
						factor *= 0.25;
						if(data.isTermArrow) {
							factor *= 0.5;
						}
					}

					if(data.isBlocking) {
						factor *= 0.5;
					}

					// Calculate knockback direction from damager to damagee
					Vector knockbackDir = damagee.getLocation().toVector().subtract(damager.getLocation().toVector());

					// Normalize horizontal direction only (preserve Y=0 for horizontal KB)
					double horizontalDist = Math.sqrt(knockbackDir.getX() * knockbackDir.getX() + knockbackDir.getZ() * knockbackDir.getZ());
					if(horizontalDist > 0) {
						knockbackDir.setX(knockbackDir.getX() / horizontalDist);
						knockbackDir.setZ(knockbackDir.getZ() / horizontalDist);
					}

					// Apply knockback
					Vector oldVelocity = damagee.getVelocity();
					Vector newVelocity = new Vector(oldVelocity.getX() + knockbackDir.getX() * factor + factor * damager.getVelocity().getX(), 0.2 * antiKB, oldVelocity.getZ() + knockbackDir.getZ() * factor + factor * damager.getVelocity().getZ());

					damagee.setVelocity(newVelocity);
				}

				// change nametag health
				PluginUtils.changeName(damagee);
				triggerAllRelevantAdvancements(damagee, damager, type, data.originalDamage, finalDamage, data.isBlocking, false, data);
			}
		}
	}

	private static void triggerAllRelevantAdvancements(LivingEntity victim, Entity attacker, DamageType type, double originalDamage, double finalDamage, boolean wasBlocked, boolean wasKilled, DamageData data) {
		DamageSource nmsSource;
		Entity causingEntity;
		if(data.e != null) {
			causingEntity = data.e.getDamageSource().getCausingEntity();
			nmsSource = convertBukkitDamageSource(data.e.getDamageSource(), victim);
		} else {
			org.bukkit.damage.DamageType bukkitType = switch(type) {
				case MELEE -> org.bukkit.damage.DamageType.MOB_ATTACK;
				case MELEE_SWEEP -> org.bukkit.damage.DamageType.PLAYER_ATTACK;
				case RANGED -> org.bukkit.damage.DamageType.ARROW;
				case RANGED_SPECIAL -> org.bukkit.damage.DamageType.SONIC_BOOM;
				case MAGIC, PLAYER_MAGIC -> org.bukkit.damage.DamageType.MAGIC;
				case ENVIRONMENTAL -> org.bukkit.damage.DamageType.FALLING_BLOCK;
				case IFRAME_ENVIRONMENTAL -> org.bukkit.damage.DamageType.ON_FIRE;
				case FALL -> org.bukkit.damage.DamageType.FALL;
				case ABSOLUTE -> org.bukkit.damage.DamageType.GENERIC_KILL;
			};
			causingEntity = attacker;
			nmsSource = convertBukkitDamageSource(org.bukkit.damage.DamageSource.builder(bukkitType).build(), victim);
		}

		if(attacker instanceof Player || causingEntity instanceof Player) {
			ServerPlayer serverPlayer;
			if(causingEntity instanceof Player p) {
				serverPlayer = ((CraftPlayer) p).getHandle();
			} else {
				serverPlayer = ((CraftPlayer) attacker).getHandle();
			}
			net.minecraft.world.entity.LivingEntity nmsVictim = ((CraftLivingEntity) victim).getHandle();


			if(wasKilled) {
				// 2. PLAYER_KILLED_ENTITY - Main kill advancement
				CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(serverPlayer, nmsVictim, nmsSource);

				// 5 & 6. Sculk catalyst triggers - only if sculk catalyst is nearby
				if(isSculkCatalystNearby(victim.getLocation())) {
					triggerSculkSpread(victim);
					CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.trigger(serverPlayer, nmsVictim, nmsSource);
					CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.trigger(serverPlayer, nmsVictim, nmsSource);
				}

				// 11. LIGHTNING_STRIKE - Improved lightning handling
				if(data.lightningInvolved && data.lightningBolt != null) {
					List<net.minecraft.world.entity.Entity> victims = List.of(nmsVictim);
					CriteriaTriggers.LIGHTNING_STRIKE.trigger(serverPlayer, data.lightningBolt, victims);
				}

			}
			// 1. PLAYER_HURT_ENTITY - Non-lethal damage
			CriteriaTriggers.PLAYER_HURT_ENTITY.trigger(serverPlayer, nmsVictim, nmsSource, (float) originalDamage, (float) finalDamage, wasBlocked);

			// 12. CHANNELED_LIGHTNING - Trident channeling
			if(data.isTridentAttack && data.tridentChanneling && data.lightningInvolved) {
				List<net.minecraft.world.entity.LivingEntity> victims = List.of(nmsVictim);
				CriteriaTriggers.CHANNELED_LIGHTNING.trigger(serverPlayer, victims);
			}
		}

		if(victim instanceof Player player) {
			ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
			net.minecraft.world.entity.Entity nmsKiller = attacker != null ? ((CraftEntity) attacker).getHandle() : null;

			if(wasKilled) {
				// 4. ENTITY_KILLED_PLAYER - Player death
				CriteriaTriggers.ENTITY_KILLED_PLAYER.trigger(serverPlayer, nmsKiller, nmsSource);

				// 7. USED_TOTEM - Already handled in your existing totem logic
				// (Keep your existing totem advancement trigger)

			} else {
				// 3. ENTITY_HURT_PLAYER - Player taking damage
				CriteriaTriggers.ENTITY_HURT_PLAYER.trigger(serverPlayer, nmsSource, (float) originalDamage, (float) finalDamage, wasBlocked);
			}
		}
		updatePlayerStatistics(victim, attacker, causingEntity, type, finalDamage, wasKilled);
	}

	private static void updatePlayerStatistics(LivingEntity victim, Entity attacker, Entity causingEntity, DamageType type, double finalDamage, boolean wasKilled) {
		// Player as attacker statistics
		Player attackingPlayer = null;
		if(causingEntity instanceof Player) {
			attackingPlayer = (Player) causingEntity;
		} else if(attacker instanceof Player) {
			attackingPlayer = (Player) attacker;
		}

		if(attackingPlayer != null) {
			ServerPlayer serverPlayer = ((CraftPlayer) attackingPlayer).getHandle();

			// Damage dealt
			serverPlayer.awardStat(Stats.DAMAGE_DEALT, Math.round((float) finalDamage * 10));

			if(wasKilled) {
				// Mob kills
				serverPlayer.awardStat(Stats.MOB_KILLS);

				// Specific entity kills
				net.minecraft.world.entity.EntityType<?> entityType = getEntityType(victim);
				if(entityType != null) {
					serverPlayer.awardStat(Stats.ENTITY_KILLED.get(entityType));
				}

				// Player kills (if victim is player)
				if(victim instanceof Player) {
					serverPlayer.awardStat(Stats.PLAYER_KILLS);
				}
			}

			// Weapon-specific statistics
			updateWeaponStatistics(serverPlayer, attacker, type);
		}

		// Player as victim statistics
		if(victim instanceof Player player) {
			ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

			// Damage taken
			serverPlayer.awardStat(Stats.DAMAGE_TAKEN, Math.round((float) finalDamage * 10));

			if(wasKilled) {
				// Deaths
				serverPlayer.awardStat(Stats.DEATHS);

				// Death by specific entity
				if(attacker != null) {
					net.minecraft.world.entity.EntityType<?> entityType = getEntityType(attacker);
					if(entityType != null) {
						serverPlayer.awardStat(Stats.ENTITY_KILLED_BY.get(entityType));
					}
				}
			}
		}
	}

	private static void updateWeaponStatistics(ServerPlayer player, Entity attacker, DamageType type) {
		net.minecraft.world.item.ItemStack weapon = player.getMainHandItem();

		switch(type) {
			case RANGED -> {
				if(attacker instanceof org.bukkit.entity.Arrow) {
					// Bow/Crossbow usage
					if(weapon.getItem() == Items.BOW) {
						player.awardStat(Stats.ITEM_USED.get(Items.BOW));
					} else if(weapon.getItem() == Items.CROSSBOW) {
						player.awardStat(Stats.ITEM_USED.get(Items.CROSSBOW));
					}
				} else if(attacker instanceof org.bukkit.entity.Trident) {
					player.awardStat(Stats.ITEM_USED.get(Items.TRIDENT));
				}
			}

			case MELEE, MELEE_SWEEP -> {
				// Melee weapon usage
				if(!weapon.isEmpty()) {
					player.awardStat(Stats.ITEM_USED.get(weapon.getItem()));
				}
			}
		}
	}

	private static net.minecraft.world.entity.EntityType<?> getEntityType(Entity entity) {
		return ((CraftEntity) entity).getHandle().getType();
	}

	private static boolean isSculkCatalystNearby(Location location) {
		World world = location.getWorld();
		if(world == null) return false;

		// Check for sculk catalyst within 8 blocks (vanilla range)
		for(int x = -8; x <= 8; x++) {
			for(int y = -8; y <= 8; y++) {
				for(int z = -8; z <= 8; z++) {
					Location checkLoc = location.clone().add(x, y, z);
					if(checkLoc.getBlock().getType() == Material.SCULK_CATALYST) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static void triggerSculkSpread(LivingEntity victim) {
		Location deathLocation = victim.getLocation();
		World world = deathLocation.getWorld();
		if(world == null) return;

		// Find nearby sculk catalysts (within 8 blocks)
		for(int x = -8; x <= 8; x++) {
			for(int y = -8; y <= 8; y++) {
				for(int z = -8; z <= 8; z++) {
					Location catalystLoc = deathLocation.clone().add(x, y, z);
					if(catalystLoc.getBlock().getType() == Material.SCULK_CATALYST) {
						// Calculate experience points (similar to vanilla)
						int xpAmount = CustomDrops.calculateMobXP(victim);

						// Spread sculk blocks around the catalyst
						spreadSculkFromCatalyst(catalystLoc, deathLocation, xpAmount);
						break; // Only spread from the first catalyst found
					}
				}
			}
		}
	}

	private static void spreadSculkFromCatalyst(Location catalyst, Location deathSite, int experience) {
		World world = catalyst.getWorld();
		Random random = new Random();

		// Number of blocks to spread based on experience (vanilla behavior)
		int blocksToSpread = Math.min(experience, 32); // Cap at 32 like vanilla

		for(int i = 0; i < blocksToSpread; i++) {
			// Random spread around the death location (within 9 blocks)
			int spreadX = random.nextInt(19) - 9; // -9 to +9
			int spreadY = random.nextInt(19) - 9;
			int spreadZ = random.nextInt(19) - 9;

			Location spreadLocation = deathSite.clone().add(spreadX, spreadY, spreadZ);
			Block targetBlock = spreadLocation.getBlock();

			// Check if block can be converted to sculk
			if(canConvertToSculk(targetBlock)) {
				// Convert based on block type and surrounding blocks
				convertToSculk(targetBlock, random);

				// Play sculk spread sound
				world.playSound(spreadLocation, Sound.BLOCK_SCULK_CATALYST_BLOOM, 0.8f, random.nextFloat() * 0.4f + 0.8f);

				// Spawn particles
				world.spawnParticle(Particle.SCULK_SOUL, spreadLocation.add(0.5, 0.5, 0.5), 1, 0.25, 0.25, 0.25, 0.05);
			}
		}
	}

	private static boolean canConvertToSculk(Block block) {
		Material type = block.getType();
		return type == Material.STONE || type == Material.COBBLESTONE || type == Material.DEEPSLATE || type == Material.DIRT || type == Material.GRASS_BLOCK || type == Material.GRAVEL || type == Material.SAND || type == Material.CLAY || type.name().contains("TERRACOTTA") || type == Material.AIR; // Can place sculk in air
	}

	private static void convertToSculk(Block block, Random random) {
		Material currentType = block.getType();

		// Conversion logic (simplified version of vanilla)
		if(currentType == Material.AIR) {
			// Small chance to place sculk vein in air
			if(random.nextFloat() < 0.1f) {
				block.setType(Material.SCULK_VEIN);
			}
		} else {
			// Convert solid blocks to sculk or sculk vein
			if(random.nextFloat() < 0.7f) {
				block.setType(Material.SCULK);
			} else {
				block.setType(Material.SCULK_VEIN);
			}
		}
	}

	private static DamageSource convertBukkitDamageSource(org.bukkit.damage.DamageSource bukkitSource, LivingEntity victim) {
		DamageSources sources = ((CraftLivingEntity) victim).getHandle().damageSources();

		org.bukkit.damage.DamageType damageType = bukkitSource.getDamageType();
		Entity directEntity = bukkitSource.getDirectEntity();
		Entity causingEntity = bukkitSource.getCausingEntity();

		net.minecraft.world.entity.Entity nmsDirectEntity = directEntity != null ? ((CraftEntity) directEntity).getHandle() : null;
		net.minecraft.world.entity.Entity nmsCausingEntity = causingEntity != null ? ((CraftEntity) causingEntity).getHandle() : null;

		// Handle entity-based damage types first with proper fallbacks
		if(damageType == org.bukkit.damage.DamageType.PLAYER_ATTACK) {
			if(nmsCausingEntity instanceof ServerPlayer player) {
				return sources.playerAttack(player);
			}
			// Fallback if causing entity isn't a ServerPlayer
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.MOB_ATTACK) {
			if(nmsCausingEntity instanceof net.minecraft.world.entity.LivingEntity living) {
				return sources.mobAttack(living);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.ARROW) {
			if(nmsDirectEntity instanceof net.minecraft.world.entity.projectile.AbstractArrow arrow) {
				return sources.arrow(arrow, nmsCausingEntity);
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

		} else if(damageType == org.bukkit.damage.DamageType.TRIDENT) {
			return sources.trident(nmsDirectEntity, nmsCausingEntity);

		} else if(damageType == org.bukkit.damage.DamageType.THORNS) {
			if(nmsCausingEntity != null) {
				return sources.thorns(nmsCausingEntity);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.EXPLOSION) {
			return sources.explosion(null, nmsCausingEntity);

		} else if(damageType == org.bukkit.damage.DamageType.PLAYER_EXPLOSION) {
			return sources.explosion(nmsCausingEntity, nmsCausingEntity);

		} else if(damageType == org.bukkit.damage.DamageType.MOB_PROJECTILE) {
			if(nmsCausingEntity instanceof net.minecraft.world.entity.LivingEntity living) {
				return sources.mobProjectile(nmsDirectEntity, living);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.FIREWORKS) {
			if(nmsDirectEntity instanceof FireworkRocketEntity firework) {
				return sources.fireworks(firework, nmsCausingEntity);
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

		} else if(damageType == org.bukkit.damage.DamageType.FALLING_BLOCK) {
			if(nmsDirectEntity != null) {
				return sources.fallingBlock(nmsDirectEntity);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.FALLING_ANVIL) {
			if(nmsDirectEntity != null) {
				return sources.anvil(nmsDirectEntity);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.FALLING_STALACTITE) {
			if(nmsDirectEntity != null) {
				return sources.fallingStalactite(nmsDirectEntity);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.STING) {
			if(nmsCausingEntity instanceof net.minecraft.world.entity.LivingEntity living) {
				return sources.sting(living);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.MOB_ATTACK_NO_AGGRO) {
			if(nmsCausingEntity instanceof net.minecraft.world.entity.LivingEntity living) {
				return sources.noAggroMobAttack(living);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.SONIC_BOOM) {
			if(nmsCausingEntity != null) {
				return sources.sonicBoom(nmsCausingEntity);
			}
			return sources.generic();

		} else if(damageType == org.bukkit.damage.DamageType.WIND_CHARGE) {
			if(nmsDirectEntity instanceof AbstractWindCharge windCharge) {
				if(nmsCausingEntity instanceof net.minecraft.world.entity.LivingEntity living) {
					return sources.windCharge(windCharge, living);
				}
				return sources.windCharge(windCharge, null);
			}
			return sources.generic();
		}

		// Simple damage types (no entity requirements)
		else if(damageType == org.bukkit.damage.DamageType.BAD_RESPAWN_POINT) {
			return sources.badRespawnPointExplosion(new Vec3(0, 0, 0));
		} else if(damageType == org.bukkit.damage.DamageType.IN_FIRE) {
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
		} else if(damageType == org.bukkit.damage.DamageType.ENDER_PEARL) {
			return sources.enderPearl();
		}

		// Fallback for any unmapped damage types
		else {
			System.err.println("Unmapped damage type: " + damageType);
			System.err.println("Causing entity: " + (causingEntity != null ? causingEntity.getType() : "null"));
			System.err.println("Direct entity: " + (directEntity != null ? directEntity.getType() : "null"));
			return sources.generic();
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof EnderCrystal crystal && crystal.getScoreboardTags().contains("SkyblockBoss")) {
			e.setCancelled(true);
			if(e.getDamager() instanceof Player p) {
				crystal.remove();
				p.addScoreboardTag("HasCrystal");
				p.sendMessage(ChatColor.YELLOW + "You have picked up an Energy Crystal!");
			}

		} else if(e.getEntity() instanceof LivingEntity entity) {
			e.setCancelled(true);
			if(!entity.isDead()) {
				DamageType type;
				switch(e.getCause()) {
					case ENTITY_ATTACK, ENTITY_EXPLOSION, THORNS -> type = DamageType.MELEE;
					case PROJECTILE, SONIC_BOOM -> type = DamageType.RANGED;
					case DRAGON_BREATH, MAGIC -> type = DamageType.MAGIC;
					case FALLING_BLOCK -> type = DamageType.ENVIRONMENTAL;
					case LIGHTNING -> type = DamageType.IFRAME_ENVIRONMENTAL;
					default -> {
						return;
					}
				}

				if(entity.getNoDamageTicks() == 0 || e.getDamager() instanceof AbstractArrow) {
					// apply intelligence to players
					if(e.getDamager() instanceof Player p) {
						if(type.equals(DamageType.MELEE) && (e.getEntity() instanceof Monster || e.getEntity().getScoreboardTags().contains("SkyblockBoss") || e.getEntity() instanceof Player)) {
							try {
								Score score = Objects.requireNonNull(Objects.requireNonNull(Plugin.getInstance().getServer().getScoreboardManager()).getMainScoreboard().getObjective("Intelligence")).getScore(p.getName());
								if(score.getScore() < 2500) {
									score.setScore(score.getScore() + 1);
									p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.AQUA + "Intelligence: " + score.getScore() + "/2500"));
								} else {
									p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.AQUA + "Intelligence: " + score.getScore() + "/2500 " + ChatColor.RED + ChatColor.BOLD + "MAX INTELLIGENCE"));
								}
							} catch(Exception exception) {
								Plugin.getInstance().getLogger().info("Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
								Bukkit.broadcastMessage(ChatColor.RED + "Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
								return;
							}
						}
					}

					Entity damager = e.getDamager();
					customMobs(entity, damager, e.getDamage(), type, new DamageData(e));
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
				case BLOCK_EXPLOSION, THORNS -> type = DamageType.MELEE;
				case POISON, WITHER -> type = DamageType.MAGIC;
				case CONTACT, DROWNING, DRYOUT, FIRE, FIRE_TICK, FREEZE, HOT_FLOOR, LAVA, MELTING, STARVATION,
					 SUFFOCATION -> type = DamageType.ENVIRONMENTAL;
				case CUSTOM -> type = DamageType.IFRAME_ENVIRONMENTAL;
				case FALL, FLY_INTO_WALL -> type = DamageType.FALL;
				case CRAMMING, KILL, SUICIDE, VOID, WORLD_BORDER -> type = DamageType.ABSOLUTE;
				default -> {
					return;
				}
			}

			long currentTime = System.currentTimeMillis();
			if(!noDamageTimes.containsKey(entity)) {
				noDamageTimes.put(entity, 0L);
			}
			long lastDamageTime = noDamageTimes.get(entity);

			if(currentTime - lastDamageTime > 490 || e.getCause().equals(DamageCause.KILL)) {
				customMobs(entity, null, e.getDamage(), type, new DamageData(e));
				noDamageTimes.put(entity, currentTime);
			}

			if(entity.isDead()) {
				try {
					entity.remove();
				} catch(Exception exception) {
					// nothing
				}
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		noDamageTimes.remove(e.getEntity());
	}
}