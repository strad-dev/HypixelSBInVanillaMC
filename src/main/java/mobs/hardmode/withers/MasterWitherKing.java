package mobs.hardmode.withers;

import listeners.CustomMobs;
import listeners.DamageType;
import misc.Plugin;
import mobs.withers.CustomWither;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

import static listeners.CustomDamage.calculateFinalDamage;
import static listeners.CustomDamage.teleport;
import static listeners.CustomMobs.spawnLightning;

public class MasterWitherKing implements CustomWither {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "MASTER Wither King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿";

		e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(2.0);
		e.getAttribute(Attribute.GENERIC_FLYING_SPEED).setBaseValue(2.0);
		e.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(6.0);
		e.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(14.0);
		e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2000.0);
		e.setHealth(2000.0);
		e.addScoreboardTag("WitherKing");
		e.addScoreboardTag("HardMode");
		e.addScoreboardTag("WitherKing90");
		e.addScoreboardTag("WitherKing80");
		e.addScoreboardTag("WitherKing70");
		e.addScoreboardTag("WitherKing60");
		e.addScoreboardTag("WitherKing50");
		e.addScoreboardTag("WitherKing40");
		e.addScoreboardTag("WitherKing30");
		e.addScoreboardTag("WitherKing20");
		e.addScoreboardTag("WitherKing10");
		e.getWorld().setThundering(true);
		e.getWorld().setWeatherDuration(1000000);
		Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Oh?  How long has it been?");
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Awakening my true form, what an interesting choice."), 40);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I suppose defeating my reduced form was not enough for you."), 80);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I could convince you to retreat, but I know that will not stop you."), 120);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": If you can defeat me, you will have earned it.  Good luck."), 160);
		Bukkit.getLogger().info("The MASTER WITHER KING has been summoned!");

		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			spawnLightning(e, 128);
			e.getWorld().createExplosion(e.getLocation(), 15);

			EnderDragon dragon = (EnderDragon) e.getWorld().spawnEntity(e.getLocation().add(0, 12, 0), EntityType.ENDER_DRAGON);
			dragon.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Wither King's Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RED + "❤ " + ChatColor.YELLOW + 1024 + "/" + 1024);
			dragon.setCustomNameVisible(true);
			dragon.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1.0);
			dragon.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(20.0);
			dragon.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(20.0);
			dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1024.0);
			dragon.setHealth(1024.0);
			dragon.addScoreboardTag("SkyblockBoss");
			dragon.addScoreboardTag("WitherKingDragon");
			dragon.addScoreboardTag("HardMode");
			dragon.setTarget(p);
			dragon.setPersistent(true);

			p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Wither King summons His Dragon!");
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> teleportDragon(dragon, e), 200);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> shootFireball(dragon, e), 50);
		}, 200);

		return newName;
	}

	public void teleportDragon(EnderDragon dragon, Mob e) {
		if(!dragon.isDead()) {
			Player p = Plugin.getNearestPlayer(e);
			dragon.teleport(p);
			p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Wither King's Dragon teleports itself to you!");
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> teleportDragon(dragon, e), 200);
		}
	}

	public void shootFireball(EnderDragon dragon, Mob e) {
		if(!dragon.isDead()) {
			Location pLocation = Plugin.getNearestPlayer(e).getLocation();
			Location dLocation = dragon.getLocation();
			DragonFireball fireball = (DragonFireball) dragon.getWorld().spawnEntity(dragon.getLocation(), EntityType.DRAGON_FIREBALL);
			fireball.setVelocity(pLocation.toVector().subtract(dLocation.toVector()).normalize().multiply(5));
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> shootFireball(dragon, e), 100);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		Random random = new Random();
		if(!type.equals(DamageType.RANGED) && random.nextDouble() < 0.12 || type.equals(DamageType.RANGED) && random.nextDouble() < 0.06) {
			teleport(damagee, damager, 16);
		}

		checkSkeletons(damagee, damager);
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.LIGHTNING_BOLT);
		calculateFinalDamage(damagee, Plugin.getNearestPlayer(damagee), 8, DamageType.RANGED);
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {
		Random random = new Random();
		if(random.nextDouble() < 0.1) {
			CustomMobs.spawnLightning(skull, 64);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> CustomMobs.spawnLightning(skull, 64), 12L);
		}
	}

	public static void checkSkeletons(LivingEntity damagee, Entity damager) {
		if(damagee.getHealth() <= damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.9 && damagee.getScoreboardTags().contains("WitherKing90")) {
			damagee.removeScoreboardTag("WitherKing90");
			spawnSkeletons(damager);
			Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "MASTER Wither King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿"
					+ ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Ouch!  That hurts!  BRING IN THE GUARDS.");
		}
		if(damagee.getHealth() <= damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.80 && damagee.getScoreboardTags().contains("WitherKing80")) {
			damagee.removeScoreboardTag("WitherKing80");
			spawnSkeletons(damager);
			Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "MASTER Wither King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿"
					+ ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Wow.  You are still alive?  Take more guards then.");
		}
		if(damagee.getHealth() <= damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.70 && damagee.getScoreboardTags().contains("WitherKing70")) {
			damagee.removeScoreboardTag("WitherKing70");
			spawnSkeletons(damager);
			Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "MASTER Wither King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿"
					+ ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Your persistence is immesureable.  I applaud you.");
		}
		if(damagee.getHealth() <= damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.60 && damagee.getScoreboardTags().contains("WitherKing60")) {
			damagee.removeScoreboardTag("WitherKing60");
			spawnSkeletons(damager);
			Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "MASTER Wither King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿"
					+ ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Still not tired yet?  I hope not, I was just getting the party started.");
		}
		if(damagee.getHealth() <= damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.50 && damagee.getScoreboardTags().contains("WitherKing50")) {
			damagee.removeScoreboardTag("WitherKing50");
			spawnSkeletons(damager);
			Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "MASTER Wither King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿"
					+ ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": You're halfway there.  Time to step up my game.");
		}
		if(damagee.getHealth() <= damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.40 && damagee.getScoreboardTags().contains("WitherKing40")) {
			damagee.removeScoreboardTag("WitherKing40");
			spawnSkeletons(damager);
			Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "MASTER Wither King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿"
					+ ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I thought I could get rid of you by forcing you to come near me.  I guess I was wrong.");
		}
		if(damagee.getHealth() <= damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.30 && damagee.getScoreboardTags().contains("WitherKing30")) {
			damagee.removeScoreboardTag("WitherKing30");
			spawnSkeletons(damager);
			Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "MASTER Wither King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿"
					+ ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Enjoying this, are you?");
		}
		if(damagee.getHealth() <= damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.20 && damagee.getScoreboardTags().contains("WitherKing20")) {
			damagee.removeScoreboardTag("WitherKing20");
			spawnSkeletons(damager);
			Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "MASTER Wither King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿"
					+ ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Not tired yet?  I applaud you, most warriors are usually dead by now.");
		}
		if(damagee.getHealth() <= damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.10 && damagee.getScoreboardTags().contains("WitherKing10")) {
			damagee.removeScoreboardTag("WitherKing10");
			spawnSkeletons(damager);
			Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "MASTER Wither King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿"
					+ ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": You are getting close.  I feel my strength depleting.");
		}
	}

	private static void spawnSkeletons(Entity damager) {
		if(damager instanceof LivingEntity entity) {
			Location l = entity.getLocation();
			for(int i = 0; i < 25; i++) {
				WitherSkeleton e = (WitherSkeleton) entity.getWorld().spawnEntity(l, EntityType.WITHER_SKELETON);
				e.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Wither King's Guard" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RED + "❤ " + ChatColor.YELLOW + 20 + "/" + 20);
				ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
				sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 7);
				sword.addEnchantment(Enchantment.KNOCKBACK, 2);
				ItemStack shield = new ItemStack(Material.SHIELD);

				Objects.requireNonNull(e.getEquipment()).setItemInMainHand(sword);
				e.getEquipment().setItemInMainHandDropChance(0.0F);
				e.getEquipment().setItemInOffHand(shield);
				e.getEquipment().setItemInOffHandDropChance(0.0F);

				e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5);
				e.getAttribute(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER).setBaseValue(0.0);
				e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50.0);
				e.setHealth(50.0);
				e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
				e.setTarget(entity);
				e.teleport(entity);
				e.setCustomNameVisible(true);
				e.addScoreboardTag("SkyblockBoss");
				e.addScoreboardTag("GuardSkeleton");
				e.addScoreboardTag("HardMode");
				e.setPersistent(true);
			}
			l.getWorld().playSound(l, Sound.ITEM_GOAT_HORN_SOUND_2, 2.0F, 1.0F);
		}
	}
}