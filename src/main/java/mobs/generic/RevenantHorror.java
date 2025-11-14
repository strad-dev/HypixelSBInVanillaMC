package mobs.generic;

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
import java.util.Random;

public class RevenantHorror implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Revenant Horror" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		sword.addEnchantment(Enchantment.KNOCKBACK, 2);

		EntityEquipment equipment = e.getEquipment();
		equipment.setItemInMainHand(sword);
		equipment.setItem(EquipmentSlot.HEAD, new ItemStack(Material.CHAINMAIL_HELMET));
		equipment.setItem(EquipmentSlot.CHEST, new ItemStack(Material.CHAINMAIL_CHESTPLATE));
		equipment.setItem(EquipmentSlot.LEGS, new ItemStack(Material.CHAINMAIL_LEGGINGS));
		equipment.setItem(EquipmentSlot.FEET, new ItemStack(Material.CHAINMAIL_BOOTS));

		Objects.requireNonNull(e.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(100.0);
		e.setHealth(100.0);
		Objects.requireNonNull(e.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(12.0);
		Objects.requireNonNull(e.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(0.4);
		e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		e.setTarget(p);
		e.setCustomNameVisible(true);
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("RevenantHorror");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Revenant Horror has risen from the depths!");
		Bukkit.getLogger().info(p.getName() + " has summoned the Revenant Horror.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		((Ageable) e).setAdult();
		e.setPersistent(true);
		e.setRemoveWhenFarAway(false);
		return newName;
	}

	private static void aoe(Zombie zombie) {

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
