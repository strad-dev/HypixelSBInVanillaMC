package misc;

import listeners.CustomDamage;
import listeners.DamageType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R4.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.*;

import static listeners.CustomDamage.customMobs;

public class Utils {
	private static final Random random = new Random();

	/**
	 * Updates the HP display of the given entity.
	 *
	 * @param entity The entity in question.
	 */
	public static void changeName(LivingEntity entity) {
		if(!(entity instanceof Player)) {
			String[] oldName;
			int health = (int) Math.ceil(entity.getHealth() + entity.getAbsorptionAmount());
			int maxHealth = (int) Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).getValue();
			try {
				oldName = Objects.requireNonNull(entity.getCustomName()).split(" ");
			} catch(Exception exception) {
				oldName = (entity.getName() + " " + ChatColor.YELLOW + health + "/" + maxHealth).split(" ");
			}
			oldName[oldName.length - 1] = ChatColor.YELLOW + "" + health + "/" + maxHealth;
			StringBuilder newName = new StringBuilder(oldName[0]);
			for(int i = 1; i < oldName.length; i++) {
				newName.append(" ").append(oldName[i]);
			}
			entity.setCustomName(newName.toString());
		}
	}

	/**
	 * Gets the closest non-Spectator Player to the provided Entity<br>If all players on the server are in Spectator, a semi-random Player will be returned.
	 *
	 * @param e the entity
	 * @return the closest non-spectator player
	 */
	public static @Nullable Player getNearestPlayer(Entity e) {
		World world = e.getWorld();
		Location location = e.getLocation();
		ArrayList<Player> playersInWorld = new ArrayList<>(world.getEntitiesByClass(Player.class));
		if(playersInWorld.isEmpty()) {
			return null;
		}
		for(int i = 0; i < playersInWorld.size(); i++) {
			if(playersInWorld.get(i).getGameMode().equals(GameMode.SPECTATOR) && playersInWorld.size() > 1) {
				playersInWorld.remove(i);
				i--;
			}
		}
		playersInWorld.sort(Comparator.comparingDouble(o -> o.getLocation().distanceSquared(location)));
		return playersInWorld.getFirst();
	}

	/**
	 * Plays a sound for every player on the server
	 *
	 * @param s The sound to play
	 */
	public static void playGlobalSound(Sound s) {
		Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, s, 1.0F, 1.0F));
	}

	/**
	 * Plays a sound for every player on the server
	 *
	 * @param s      The sound to play
	 * @param volume The volume of the sound
	 * @param pitch  The pitch of the sound
	 */
	public static void playGlobalSound(Sound s, float volume, float pitch) {
		Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, s, volume, pitch));
	}

	/**
	 * Notifies the player that they received a rare drop
	 *
	 * @param p       The Player that received the drop
	 * @param message The name of the item
	 */
	public static void sendRareDropMessage(Player p, String message) {
		if(p != null) {
			p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + message);
			Bukkit.getLogger().info(p.getName() + " dropped a " + message);
			p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
		}
	}

	/**
	 * Shoot a beam dealing damage to everything in its path!
	 *
	 * @param origin      The Entity shooting the beam
	 * @param destination The Entity that is being targeted<br>If this is the same Entity as the origin, the beam is shot in the direction the Entity is looking at.
	 * @param color       The color of the beam
	 * @param distance    How far the beam should go
	 * @param pierce      How many enemies should be pierced
	 * @param damage      The damage of the beam
	 */
	public static void shootBeam(Entity origin, Entity destination, Color color, long distance, long pierce, double damage) {
		Location l = origin.getLocation();
		if(origin instanceof LivingEntity entity) {
			l = entity.getEyeLocation();
		}
		Vector v;
		if(origin.equals(destination)) {
			v = l.getDirection();
		} else {
			Location destinationLocation = destination.getLocation().add(0, destination.getHeight() / 2, 0);
			double x = destinationLocation.getX() - l.getX();
			double y = destinationLocation.getY() - l.getY();
			double z = destinationLocation.getZ() - l.getZ();
			v = new Vector(x, y, z);
		}
		v.setX(v.getX() / 5);
		v.setY(v.getY() / 5);
		v.setZ(v.getZ() / 5);
		World world = origin.getWorld();
		Set<Entity> damagedEntities = new HashSet<>();
		damagedEntities.add(origin);
		for(int i = 0; i < distance * 5 && pierce > 0; i++) {
			if(l.getBlock().getType().isSolid()) {
				break;
			}
			ArrayList<Entity> entities = (ArrayList<Entity>) world.getNearbyEntities(l, 1, 1, 1);
			for(Entity entity : entities) {
				if(entity instanceof LivingEntity temp && !damagedEntities.contains(entity)) {
					damagedEntities.add(entity);
					customMobs(temp, origin, damage, DamageType.RANGED_SPECIAL);
					pierce--;
				}
			}
			Particle.DustOptions particle = new Particle.DustOptions(color, 1.0F);
			world.spawnParticle(Particle.DUST, l, 1, particle);
			l.add(v);
		}
	}

	/**
	 * Spawns a custom TNT<br>If the fuse is 0 ticks, the TNT entity will not be spawned
	 *
	 * @param spawner stores the spawner of the TNT; this entity is ALWAYS immune to the TNT's damage
	 * @param l       the location the TNT should be spawned at
	 * @param fuse    the duration before the TNT explodes - the TNT entity's fuse is 20 ticks longer
	 * @param radius  the radius in which the damage is effective
	 * @param damage  the amount of damage to deal
	 * @param immune  represents which entity types are immune to damage; by default, no entity types are immune and the spawner is always immune
	 */
	public static void spawnTNT(Entity spawner, Location l, int fuse, int radius, int damage, List<EntityType> immune) {
		if(fuse == 0) {
			List<Entity> entities = (List<Entity>) l.getWorld().getNearbyEntities(l, radius, radius, radius);
			for(Entity entity : entities) {
				if(!entity.equals(spawner) && entity instanceof LivingEntity entity1 && !immune.contains(entity.getType()) && (entity instanceof Player p && p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR)) {
					CustomDamage.customMobs(entity1, spawner, damage, DamageType.PLAYER_MAGIC);
				}
			}
			spawner.getWorld().spawnParticle(Particle.EXPLOSION, spawner.getLocation(), Math.min((int) Math.pow(radius, 3), 65536), radius, radius / 2.0, radius);
			spawner.getWorld().playSound(spawner.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 0.6F);
		} else {
			TNTPrimed tnt = (TNTPrimed) l.getWorld().spawnEntity(l, EntityType.TNT);
			tnt.setFuseTicks(fuse + 20);
			spawner.getWorld().playSound(spawner.getLocation(), Sound.ENTITY_TNT_PRIMED, 2.0F, 1.0F);

			Utils.scheduleTask(() -> {
				List<Entity> entities = tnt.getNearbyEntities(radius, radius, radius);
				for(Entity entity : entities) {
					if(!entity.equals(spawner) && entity instanceof LivingEntity entity1 && !immune.contains(entity.getType()) && (entity instanceof Player p && p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR)) {
						CustomDamage.customMobs(entity1, spawner, damage, DamageType.PLAYER_MAGIC);
					}
				}
				tnt.getWorld().spawnParticle(Particle.EXPLOSION, tnt.getLocation(), Math.min((int) Math.pow(radius, 3), 65536), radius, radius / 2.0, radius);
				tnt.getWorld().playSound(tnt.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 0.6F);
				tnt.remove();
			}, fuse);
		}
	}

	/**
	 * Teleports the entity to a random position in a given radius from its current location.<br>The entity will always be teleported to the highest block.
	 *
	 * @param e      The entity to be teleported
	 * @param radius The radius of the randomness
	 */
	public static void teleport(Entity e, int radius) {
		teleport(e, e.getLocation(), radius);
	}

	/**
	 * Teleports the entity to a random position in a given radius from the given location.<br>The entity will always be teleported to the highest block.
	 *
	 * @param e      The entity to be teleported
	 * @param radius The radius of the randomness
	 */
	public static void teleport(Entity e, Location center, int radius) {
		e.teleport(randomLocation(center, radius));
		e.getWorld().playSound(e.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
	}

	/**
	 * Finds a random location given a center and a radius.<br>The Location will always be the highest block.
	 *
	 * @param center The center
	 * @param radius How far away at most
	 * @return The randomized Location
	 */
	public static Location randomLocation(Location center, int radius) {
		Vector added = new Vector(random.nextInt(radius * 2 + 1) - radius, 0, random.nextInt(radius * 2 + 1) - radius);
		Location newLoc = center.clone().add(added);
		newLoc.setY(highestBlock(center));
		return newLoc;
	}

	public static int highestBlock(Location l) {
		for(int i = 319; i > -64; i--) {
			Block b = l.getWorld().getBlockAt((int) l.getX(), i, (int) l.getZ());
			if(b.getType() != Material.AIR && b.getType() != Material.VOID_AIR) {
				return i + 1;
			}
		}
		return -64;
	}

	public static void spawnGuards(LivingEntity entity, int num) {
		for(int i = 0; i < num; i++) {
			WitherSkeleton e = (WitherSkeleton) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.WITHER_SKELETON);
			e.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Wither Guard" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RED + "❤ " + ChatColor.YELLOW + 50 + "/" + 50);
			ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
			sword.addEnchantment(Enchantment.KNOCKBACK, 1);
			ItemStack shield = new ItemStack(Material.SHIELD);

			Objects.requireNonNull(e.getEquipment()).setItemInMainHand(sword);
			e.getEquipment().setItemInMainHandDropChance(0.0F);
			e.getEquipment().setItemInOffHand(shield);
			e.getEquipment().setItemInOffHandDropChance(0.0F);

			//noinspection DuplicatedCode
			e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(50.0);
			e.setHealth(50.0);
			e.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.5);
			e.getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER).setBaseValue(0.0);
			e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
			e.setTarget(Utils.getNearestPlayer(entity));
			e.setCustomNameVisible(true);
			e.addScoreboardTag("SkyblockBoss");
			e.addScoreboardTag("GuardSkeleton");
			e.setPersistent(true);
			e.setRemoveWhenFarAway(false);
		}
		entity.getWorld().playSound(entity, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0F, 2.0F);
	}

	public static void damageItem(Entity user, ItemStack item, double damage) {
		int maxDurability = item.getType().getMaxDurability();
		if(item.getItemMeta() instanceof Damageable d && !d.isUnbreakable() && maxDurability != 0) {
			if(!(user instanceof Player p) || (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE)) {
				double finalDamage = damage / (double) (d.getEnchantLevel(Enchantment.UNBREAKING) + 1);
				int guaranteedDamage = (int) finalDamage;
				if(finalDamage % 1 > random.nextDouble()) {
					guaranteedDamage++;
				}
				int newDamage = d.getDamage() + guaranteedDamage;

				if(newDamage >= maxDurability) {
					if(user instanceof Player p) {
						// Fire the break event for advancements
						PlayerItemBreakEvent breakEvent = new PlayerItemBreakEvent(p, item.clone());
						Bukkit.getPluginManager().callEvent(breakEvent);

						// Update statistics
						p.incrementStatistic(Statistic.BREAK_ITEM, item.getType());

						// Play break sound and show animation manually
						p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);

						// advancements
						ServerPlayer serverPlayer = ((CraftPlayer) p).getHandle();
						net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
						nmsItem.setDamageValue(maxDurability);
						CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(serverPlayer, nmsItem, maxDurability);
					}

					// Remove the item
					item.setAmount(0);
				} else {
					// advancements
					if(user instanceof Player p) {
						ServerPlayer serverPlayer = ((CraftPlayer) p).getHandle();
						net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
						nmsItem.setDamageValue(newDamage);
						CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(serverPlayer, nmsItem, newDamage);
					}

					// Item survives
					d.setDamage(newDamage);
					item.setItemMeta(d);
				}
			}
		}
	}
	
	public static void scheduleTask(Runnable task, long delay) {
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), task, delay);
	}

	/**
	 * Sends a packet to every player on the server.
	 *
	 * @param pkt Packet to send
	 */
	public static void broadcastPacket(Packet<?> pkt) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.isOnline()) {
				((CraftPlayer) p).getHandle().connection.send(pkt);
			}
		}
	}
}