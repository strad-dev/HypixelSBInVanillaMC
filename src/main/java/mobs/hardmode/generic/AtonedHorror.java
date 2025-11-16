package mobs.hardmode.generic;

import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import java.util.ArrayList;
import java.util.Objects;

public class AtonedHorror implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		Zombie zombie;
		if(e instanceof Zombie) {
			zombie = (Zombie) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Atoned Horror" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		sword.addEnchantment(Enchantment.KNOCKBACK, 2);

		EntityEquipment equipment = zombie.getEquipment();
		equipment.setItemInMainHand(sword);
		equipment.setItem(EquipmentSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET));
		equipment.setItem(EquipmentSlot.CHEST, new ItemStack(Material.DIAMOND_CHESTPLATE));
		equipment.setItem(EquipmentSlot.LEGS, new ItemStack(Material.DIAMOND_LEGGINGS));
		equipment.setItem(EquipmentSlot.FEET, new ItemStack(Material.DIAMOND_BOOTS));

		zombie.getAttribute(Attribute.MAX_HEALTH).setBaseValue(150.0);
		zombie.setHealth(150.0);
		zombie.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(12.0);
		zombie.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.5);
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, -1, 2));
		zombie.setTarget(Utils.getNearestPlayer(zombie));
		zombie.setCustomNameVisible(true);
		zombie.addScoreboardTag("SkyblockBoss");
		zombie.addScoreboardTag("AtonedHorror");
		zombie.addScoreboardTag("HardMode");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Atoned Horror has risen from the depths!");
		Bukkit.getLogger().info(p.getName() + " has summoned the Atoned Horror.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		zombie.setAdult();
		zombie.setPersistent(true);
		zombie.setRemoveWhenFarAway(false);

		Utils.scheduleTask(() -> summonTNT(zombie), 60);
		Utils.scheduleTask(() -> nuclearExplosion(zombie), 600);

		return newName;
	}

	private static void summonTNT(Zombie zombie) {
		if(!zombie.isDead()) {
			Player p = Utils.getNearestPlayer(zombie);
			Utils.spawnTNT(zombie, p.getLocation(), 20, 5, 25, new ArrayList<>());
			Utils.scheduleTask(() -> summonTNT(zombie), 60);
		}
	}

	private static void nuclearExplosion(Zombie zombie) {
		if(!zombie.isDead()) {
			zombie.setAI(false);
			zombie.addScoreboardTag("Invulnerable");
			Utils.changeName(zombie);
			zombie.getNearbyEntities(64, 64, 64).stream().filter(entity -> entity instanceof Player).map(Player.class::cast).forEach(p -> {
				p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "7", "", 0, 21, 0);
				Utils.scheduleTask(() -> {
					if(!zombie.isDead()) {
						p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "6", "", 0, 21, 0);
					}
				}, 20);
				Utils.scheduleTask(() -> {
					if(!zombie.isDead()) {
						p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "5", "", 0, 21, 0);
					}
				}, 40);
				Utils.scheduleTask(() -> {
					if(!zombie.isDead()) {
						p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "4", "", 0, 21, 0);
					}
				}, 60);
				Utils.scheduleTask(() -> {
					if(!zombie.isDead()) {
						p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "3", "", 0, 21, 0);
					}
				}, 80);
				Utils.scheduleTask(() -> {
					if(!zombie.isDead()) {
						p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "2", "", 0, 21, 0);
					}
				}, 100);
				Utils.scheduleTask(() -> {
					if(!zombie.isDead()) {
						p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "1", "", 0, 21, 0);
					}
				}, 120);
				Utils.scheduleTask(() -> {
					if(!zombie.isDead()) {
						p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "BOOM", "", 0, 21, 0);
					}
				}, 140);
			});
			Utils.scheduleTask(() -> {
				if(!zombie.isDead()) {
					Utils.spawnTNT(zombie, zombie.getLocation(), 0, 64, 150, new ArrayList<>());
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
				p.sendTitle("", ChatColor.YELLOW + "You cannot damage the Atoned Horror.", 0, 20, 0);
			}
			damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
			return false;
		} else if(type == DamageType.PLAYER_MAGIC || type == DamageType.RANGED || type == DamageType.RANGED_SPECIAL) {
			if(damager instanceof Player p) {
				p.sendTitle("", ChatColor.YELLOW + "You cannot deal " + DamageType.toString(type) + " to the Atoned Horror.", 0, 20, 0);
			}
			damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(type == DamageType.MELEE) {
			Utils.spawnTNT(damager, damagee.getLocation(), 20, 5, 25, new ArrayList<>());
		}
		return true;
	}
}