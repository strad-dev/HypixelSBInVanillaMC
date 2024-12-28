package misc;

import listeners.CustomDamage;
import listeners.DamageType;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class PluginUtils {
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
	 * Plays a sound for every player on the server
	 * @param s The sound to play
	 */
	public static void playGlobalSound(Sound s) {
		Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, s, 1.0F, 1.0F));
	}

	/**
	 * Plays a sound for every player on the server
	 * @param s The sound to play
	 * @param volume The volume of the sound
	 * @param pitch The pitch of the sound
	 */
	public static void playGlobalSound(Sound s, float volume, float pitch) {
		Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, s, volume, pitch));
	}

	/**
	 * Spawns a custom TNT
	 * If the fuse is 0 ticks, the TNT entity will not be spawned
	 *
	 * @param spawner stores the spawner of the TNT; this entity is ALWAYS immune to the TNT's damage
	 * @param l       the location the TNT should be spawned at
	 * @param fuse    the duration before the TNT explodes - the TNT entity's fuse is 20 ticks longer
	 * @param radius  the radius in which the damage is effective
	 * @param damage  the amount of damage to deal
	 * @param immune  represents which entity types are immune to damage; by default, no entity types are immune
	 */
	public static void spawnTNT(Entity spawner, Location l, int fuse, int radius, int damage, List<EntityType> immune) {
		if(fuse == 0) {
			List<Entity> entities = (List<Entity>) l.getWorld().getNearbyEntities(l, radius, radius, radius);
			for(Entity entity : entities) {
				if(!entity.equals(spawner) && entity instanceof LivingEntity entity1 && !immune.contains(entity.getType())) {
					CustomDamage.customMobs(entity1, spawner, damage, DamageType.PLAYER_MAGIC);
				}
			}
			spawner.getWorld().spawnParticle(Particle.EXPLOSION, spawner.getLocation(), (int) Math.pow(radius, 3), radius, radius / 2.0, radius);
			spawner.getWorld().playSound(spawner.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 0.6F);
		} else {
			TNTPrimed tnt = (TNTPrimed) l.getWorld().spawnEntity(l, EntityType.TNT);
			tnt.setFuseTicks(fuse + 20);
			spawner.getWorld().playSound(spawner.getLocation(), Sound.ENTITY_TNT_PRIMED, 2.0F, 1.0F);

			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				List<Entity> entities = tnt.getNearbyEntities(radius, radius, radius);
				for(Entity entity : entities) {
					if(!entity.equals(spawner) && entity instanceof LivingEntity entity1 && !immune.contains(entity.getType())) {
						CustomDamage.customMobs(entity1, tnt, damage, DamageType.PLAYER_MAGIC);
					}
				}
				tnt.remove();
				spawner.getWorld().spawnParticle(Particle.EXPLOSION, spawner.getLocation(), (int) Math.pow(radius, 3), radius, radius / 2.0, radius);
				spawner.getWorld().playSound(spawner.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 0.6F);
			}, fuse);
		}
	}

	/**
	 * Teleports the entity to a random position in a given radius from its current location.
	 * <br>
	 * The entity will always be teleported to the highest block.
	 *
	 * @param e The entity to be teleported
	 * @param radius The radius of the randomness
	 */
	public static void teleport(Entity e, int radius) {
		Random random = new Random();
		Location l = e.getLocation();
		Location l2 = e.getLocation();
		Vector added = new Vector(random.nextInt(radius * 2 + 1) - radius, 0, random.nextInt(radius * 2 + 1) - radius);
		l.add(added);
		l2.add(added);
		for(int i = 319; i > -64; i--) {
			Block b = l.getWorld().getBlockAt((int) l.getX(), i, (int) l.getZ());
			if(b.getType() != Material.AIR && b.getType() != Material.VOID_AIR) {
				l.setY(i + 1);
				e.teleport(l);
				e.getWorld().playSound(e.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
				return;
			}
		}
		e.getWorld().playSound(e.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
	}

	public static void spawnGuards(LivingEntity entity, int num) {
		for(int i = 0; i < num; i++) {
			WitherSkeleton e = (WitherSkeleton) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.WITHER_SKELETON);
			e.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Wither Guard" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RED + "❤ " + ChatColor.YELLOW + 75 + "/" + 75);
			ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
			sword.addEnchantment(Enchantment.KNOCKBACK, 1);
			ItemStack shield = new ItemStack(Material.SHIELD);

			Objects.requireNonNull(e.getEquipment()).setItemInMainHand(sword);
			e.getEquipment().setItemInMainHandDropChance(0.0F);
			e.getEquipment().setItemInOffHand(shield);
			e.getEquipment().setItemInOffHandDropChance(0.0F);

			//noinspection DuplicatedCode
			Objects.requireNonNull(e.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(0.5);
			Objects.requireNonNull(e.getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER)).setBaseValue(0.0);
			e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
			e.setTarget(entity);
			e.teleport(entity);
			e.setCustomNameVisible(true);
			e.addScoreboardTag("SkyblockBoss");
			e.addScoreboardTag("GuardSkeleton");
			e.setPersistent(true);
		}
		entity.getWorld().playSound(entity, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0F, 2.0F);
	}
}