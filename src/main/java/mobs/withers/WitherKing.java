package mobs.withers;

import listeners.CustomMobs;
import listeners.DamageType;
import misc.Plugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.Random;

import static listeners.CustomDamage.calculateFinalDamage;
import static listeners.CustomDamage.teleport;
import static listeners.CustomMobs.spawnLightning;

public class WitherKing implements CustomWither {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "Wither King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿";

		e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(2.0);
		e.getAttribute(Attribute.GENERIC_FLYING_SPEED).setBaseValue(2.0);
		e.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(3.0);
		e.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(13.0);
		e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(600.0);
		e.setHealth(600.0);
		e.addScoreboardTag("WitherKing");
		e.addScoreboardTag("WitherKing75");
		e.addScoreboardTag("WitherKing50");
		e.addScoreboardTag("WitherKing25");
		e.getWorld().setThundering(true);
		e.getWorld().setWeatherDuration(1000000);
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "A giant rumble occurs.  The ground beneath you cracks and crumbles as a mysterious being emerges.  Could it be?  No...  He had been sleeping for centuries!  How could He b- *mic cuts off*");
		Bukkit.getLogger().info("The WITHER KING has been summoned!");

		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			spawnLightning(e, 128);
			e.getWorld().createExplosion(e.getLocation(), 10);

			EnderDragon dragon = (EnderDragon) e.getWorld().spawnEntity(e.getLocation().add(0, 12, 0), EntityType.ENDER_DRAGON);
			dragon.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Wither King's Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RED + "❤ " + ChatColor.YELLOW + 1024 + "/" + 1024);
			dragon.setCustomNameVisible(true);
			dragon.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1.0);
			dragon.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(17.9);
			dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1024.0);
			dragon.setHealth(1024.0);
			dragon.addScoreboardTag("SkyblockBoss");
			dragon.addScoreboardTag("WitherKingDragon");
			dragon.setTarget(p);
			dragon.setPersistent(true);

			p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Wither King summons His Dragon!");
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> teleportDragon(dragon, e), 300);
		}, 200);

		return newName;
	}

	public void teleportDragon(EnderDragon dragon, Mob e) {
		if(!dragon.isDead()) {
			Player p = Plugin.getNearestPlayer(e);
			dragon.teleport(p);
			p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Wither King's Dragon teleports itself to you!");
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> teleportDragon(dragon, e), 300);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		Random random = new Random();
		if(!type.equals(DamageType.RANGED) && random.nextDouble() < 0.1 || type.equals(DamageType.RANGED) && random.nextDouble() < 0.05) {
			teleport(damagee, damager, 16);
		}

		checkSkeletons(damagee, damager);
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee) {
		damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.LIGHTNING_BOLT);
		calculateFinalDamage(damagee, Plugin.getNearestPlayer(damagee), 6, DamageType.RANGED);
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {
		Random random = new Random();
		if(random.nextDouble() < 0.1) {
			CustomMobs.spawnLightning(skull, 64);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> CustomMobs.spawnLightning(skull, 64), 20L);
		}
	}

	public static void checkSkeletons(LivingEntity damagee, Entity damager) {
		if(damagee.getHealth() <= damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.75 && damagee.getScoreboardTags().contains("WitherKing75")) {
			damagee.removeScoreboardTag("WitherKing75");
			spawnSkeletons(damager);
		}
		if(damagee.getHealth() <= damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.50 && damagee.getScoreboardTags().contains("WitherKing50")) {
			damagee.removeScoreboardTag("WitherKing50");
			spawnSkeletons(damager);
		}
		if(damagee.getHealth() <= damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.25 && damagee.getScoreboardTags().contains("WitherKing25")) {
			damagee.removeScoreboardTag("WitherKing25");
			spawnSkeletons(damager);
		}
	}

	private static void spawnSkeletons(Entity damager) {
		if(damager instanceof LivingEntity entity) {
			Location l = entity.getLocation();
			for(int i = 0; i < 15; i++) {
				WitherSkeleton e = (WitherSkeleton) entity.getWorld().spawnEntity(l, EntityType.WITHER_SKELETON);
				e.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Wither King's Guard" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RED + "❤ " + ChatColor.YELLOW + 20 + "/" + 20);
				ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
				sword.addEnchantment(Enchantment.KNOCKBACK, 2);
				ItemStack shield = new ItemStack(Material.SHIELD);

				Objects.requireNonNull(e.getEquipment()).setItemInMainHand(sword);
				e.getEquipment().setItemInMainHandDropChance(0.0F);
				e.getEquipment().setItemInOffHand(shield);
				e.getEquipment().setItemInOffHandDropChance(0.0F);

				Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.5);
				Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER)).setBaseValue(0.0);
				e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
				e.setTarget(entity);
				e.teleport(entity);
				e.setCustomNameVisible(true);
				e.addScoreboardTag("SkyblockBoss");
				e.addScoreboardTag("GuardSkeleton");
				e.setPersistent(true);
			}
			entity.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Wither King summons His Guards to defend Him!");
			l.getWorld().playSound(l, Sound.ITEM_GOAT_HORN_SOUND_2, 2.0F, 1.0F);
		}
	}
}