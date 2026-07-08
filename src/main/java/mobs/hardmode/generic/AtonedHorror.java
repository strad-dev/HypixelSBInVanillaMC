package mobs.hardmode.generic;

import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.util.ArrayList;

public class AtonedHorror implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		e.setCanPickupItems(false);
		Zombie zombie;
		if(e instanceof Zombie) {
			zombie = (Zombie) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = "<gold><bold>﴾ <red><bold>Atoned Horror<gold><bold> ﴿";
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		sword.addEnchantment(Enchantment.KNOCKBACK, 2);

		EntityEquipment equipment = zombie.getEquipment();
		equipment.setItemInMainHand(sword);
		equipment.setItem(EquipmentSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET));
		equipment.setItem(EquipmentSlot.CHEST, new ItemStack(Material.DIAMOND_CHESTPLATE));
		equipment.setItem(EquipmentSlot.LEGS, new ItemStack(Material.DIAMOND_LEGGINGS));
		equipment.setItem(EquipmentSlot.FEET, new ItemStack(Material.DIAMOND_BOOTS));
		equipment.setItemInMainHandDropChance(0);
		equipment.setHelmetDropChance(0);
		equipment.setChestplateDropChance(0);
		equipment.setLeggingsDropChance(0);
		equipment.setBootsDropChance(0);

		zombie.getAttribute(Attribute.MAX_HEALTH).setBaseValue(200.0);
		zombie.setHealth(200.0);
		zombie.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(-6.0);
		zombie.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.5);
		zombie.getAttribute(Attribute.ARMOR).setBaseValue(0.0);
		Utils.setupBoss(zombie, p, "AtonedHorror", "HardMode");
		p.sendMessage(Utils.msg("<red><bold>The Atoned Horror has risen from the depths!"));
		Bukkit.getLogger().info(p.getName() + " has summoned the Atoned Horror!");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		zombie.setAdult();

		Utils.scheduleTask(() -> healing(zombie), 7);
		Utils.scheduleTask(() -> summonTNT(zombie), 60);
		Utils.scheduleTask(() -> nuclearExplosion(zombie), 600);

		return newName;
	}

	private static void healing(Zombie zombie) {
		if(!zombie.isDead()) {
			if(!zombie.getScoreboardTags().contains("Invulnerable")) {
				zombie.setHealth(Math.min(zombie.getHealth() + 1, 200));
				Utils.changeName(zombie);
			}
			Utils.scheduleTask(() -> healing(zombie), 7);
		}
	}

	private static void summonTNT(Zombie zombie) {
		if(!zombie.isDead()) {
			Player p = Utils.getNearestPlayer(zombie, 64);
			if(p != null) {
				Utils.spawnTNT(zombie, p.getLocation(), 20, 5, 30, new ArrayList<>());
			}
			Utils.scheduleTask(() -> summonTNT(zombie), 60);
		}
	}

	private static void nuclearExplosion(Zombie zombie) {
		if(!zombie.isDead()) {
			zombie.setAI(false);
			zombie.addScoreboardTag("Invulnerable");
			Utils.changeName(zombie);
			Utils.applyToAllNearbyPlayers(zombie, 64,p -> {
				p.showTitle(Title.title(Utils.msg("<red><bold>7"), Utils.msg(""), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)));
				Utils.scheduleTask(() -> {
					if(!zombie.isDead()) {
						p.showTitle(Title.title(Utils.msg("<red><bold>6"), Utils.msg(""), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)));
					}
				}, 20);
				Utils.scheduleTask(() -> {
					if(!zombie.isDead()) {
						p.showTitle(Title.title(Utils.msg("<red><bold>5"), Utils.msg(""), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)));
					}
				}, 40);
				Utils.scheduleTask(() -> {
					if(!zombie.isDead()) {
						p.showTitle(Title.title(Utils.msg("<red><bold>4"), Utils.msg(""), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)));
					}
				}, 60);
				Utils.scheduleTask(() -> {
					if(!zombie.isDead()) {
						p.showTitle(Title.title(Utils.msg("<red><bold>3"), Utils.msg(""), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)));
					}
				}, 80);
				Utils.scheduleTask(() -> {
					if(!zombie.isDead()) {
						p.showTitle(Title.title(Utils.msg("<red><bold>2"), Utils.msg(""), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)));
					}
				}, 100);
				Utils.scheduleTask(() -> {
					if(!zombie.isDead()) {
						p.showTitle(Title.title(Utils.msg("<red><bold>1"), Utils.msg(""), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)));
					}
				}, 120);
				Utils.scheduleTask(() -> {
					if(!zombie.isDead()) {
						p.showTitle(Title.title(Utils.msg("<red><bold>BOOM"), Utils.msg(""), Title.Times.times(Duration.ZERO, Duration.ofMillis(21L * 50L), Duration.ZERO)));
					}
				}, 140);
			});
			Utils.scheduleTask(() -> {
				if(!zombie.isDead()) {
					Utils.spawnTNT(zombie, zombie.getLocation(), 0, 16, 250, new ArrayList<>());
				}
				zombie.setAI(true);
				zombie.removeScoreboardTag("Invulnerable");
			}, 140);
			Utils.scheduleTask(() -> nuclearExplosion(zombie), 600);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			if(damager instanceof Player p) {
				p.showTitle(Title.title(Utils.msg(""), Utils.msg("<yellow>You cannot damage the Atoned Horror."), Title.Times.times(Duration.ZERO, Duration.ofMillis(20L * 50L), Duration.ZERO)));
			}
			damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
			return false;
		} else if(type == DamageType.PLAYER_MAGIC || type == DamageType.RANGED || type == DamageType.RANGED_SPECIAL) {
			if(damager instanceof Player p) {
				p.showTitle(Title.title(Utils.msg(""), Utils.msg("<yellow>You cannot deal " + DamageType.toString(type) + " to the Atoned Horror."), Title.Times.times(Duration.ZERO, Duration.ofMillis(20L * 50L), Duration.ZERO)));
			}
			damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(type == DamageType.MELEE) {
			Utils.spawnTNT(damager, damagee.getLocation(), 20, 5, 39, new ArrayList<>());
		}
		return true;
	}
}