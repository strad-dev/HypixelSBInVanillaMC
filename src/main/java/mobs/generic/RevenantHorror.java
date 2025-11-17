package mobs.generic;

import listeners.CustomDamage;
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

import java.util.List;
import java.util.Objects;

public class RevenantHorror implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		Zombie zombie;
		if(e instanceof Zombie) {
			zombie = (Zombie) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Revenant Horror" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		sword.addEnchantment(Enchantment.KNOCKBACK, 2);

		EntityEquipment equipment = zombie.getEquipment();
		equipment.setItemInMainHand(sword);
		equipment.setItem(EquipmentSlot.HEAD, new ItemStack(Material.CHAINMAIL_HELMET));
		equipment.setItem(EquipmentSlot.CHEST, new ItemStack(Material.CHAINMAIL_CHESTPLATE));
		equipment.setItem(EquipmentSlot.LEGS, new ItemStack(Material.CHAINMAIL_LEGGINGS));
		equipment.setItem(EquipmentSlot.FEET, new ItemStack(Material.CHAINMAIL_BOOTS));
		equipment.setItemInMainHandDropChance(0);
		equipment.setHelmetDropChance(0);
		equipment.setChestplateDropChance(0);
		equipment.setLeggingsDropChance(0);
		equipment.setBootsDropChance(0);

		zombie.getAttribute(Attribute.MAX_HEALTH).setBaseValue(100.0);
		zombie.setHealth(100.0);
		zombie.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(12.0);
		zombie.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.4);
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		zombie.setTarget(p);
		zombie.setCustomNameVisible(true);
		zombie.addScoreboardTag("SkyblockBoss");
		zombie.addScoreboardTag("RevenantHorror");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Revenant Horror has risen from the depths!");
		Bukkit.getLogger().info(p.getName() + " has summoned the Revenant Horror.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		zombie.setAdult();
		zombie.setPersistent(true);
		zombie.setRemoveWhenFarAway(false);
		Utils.scheduleTask(() -> aoe(zombie), 20);
		return newName;
	}

	private static void aoe(Zombie zombie) {
		if(!zombie.isDead()) {
			List<Entity> entities = zombie.getNearbyEntities(8, 8, 8);
			entities.forEach(entity -> {
				if(entity instanceof Player p) {
					CustomDamage.customMobs(p, zombie, zombie.getHealth() / zombie.getAttribute(Attribute.MAX_HEALTH).getValue() < 0.5 ? 12 : 6, DamageType.MELEE);
				}
			});
			Utils.scheduleTask(() -> aoe(zombie), 20);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}
