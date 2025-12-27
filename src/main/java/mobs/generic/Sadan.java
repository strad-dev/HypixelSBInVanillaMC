package mobs.generic;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.BossBarManager;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

import static misc.Utils.shootBeam;

public class Sadan implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		Zombie zombie;
		if(e instanceof Zombie) {
			zombie = (Zombie) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Sadan" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
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

		zombie.getAttribute(Attribute.MAX_HEALTH).setBaseValue(400.0);
		zombie.setHealth(400.0);
		zombie.getAttribute(Attribute.ARMOR).setBaseValue(-20.0);
		zombie.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(-8.0);
		zombie.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(25.0);
		zombie.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
		zombie.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.5);
		zombie.getAttribute(Attribute.SCALE).setBaseValue(6.0);
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		zombie.setTarget(Utils.getNearestPlayer(zombie));
		zombie.setCustomNameVisible(true);
		zombie.addScoreboardTag("SkyblockBoss");
		zombie.addScoreboardTag("Sadan");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Sadan has arrived from the bowels of The Catacombs to destroy you!");
		Bukkit.getLogger().info(p.getName() + " has summoned Sadan.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		zombie.setAdult();
		zombie.setPersistent(true);
		zombie.setRemoveWhenFarAway(false);
		BossBarManager.createBossBar(zombie, BarColor.RED, BarStyle.SOLID);
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(!(damager instanceof FallingBlock)) {
			Random random = new Random();
			if(damager instanceof LivingEntity entity && random.nextDouble() < 0.25) {
				switch(random.nextInt(7)) {
					case 0 -> {
						damager.teleport(damager.getLocation().subtract(0, 1, 0));
						CustomDamage.customMobs(entity, damagee, 25, DamageType.MELEE);
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Sadan has stomped you into the ground!");
						damager.getWorld().playSound(damager.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 2.0F);
					}
					case 1, 2 -> {
						shootBeam(damagee, damager, Color.RED, 32, 1, 25);
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Sadan has shot you with Laser Eyes!");
						damager.getWorld().playSound(damager.getLocation(), Sound.ENTITY_GUARDIAN_DEATH, 1.0F, 2.0F);
					}
					case 3, 4 -> {
						damagee.swingMainHand();
						CustomDamage.customMobs(entity, damagee, 25, DamageType.MELEE);
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Sadan attacks you violently with his Diamond Sword!");
						damager.getWorld().playSound(damager.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
					}
					case 5, 6 -> {
						Block b = damager.getLocation().add(0, 20, 0).getBlock();
						if(b.getType().equals(Material.AIR)) {
							b.setType(Material.DAMAGED_ANVIL);
						}
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Sadan rains boulders on top of your head!!");
						damager.getWorld().playSound(damager.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0F, 1.0F);
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}
