package mobs.generic;

import listeners.DamageType;
import misc.DamageData;
import mobs.CustomMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class InfuriatedWitherSkeleton implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		WitherSkeleton witherSkeleton;
		if(e instanceof WitherSkeleton) {
			witherSkeleton = (WitherSkeleton) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Highly Infuriated Wither Skeleton" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RED + "❤ " + ChatColor.YELLOW + 100 + "/" + 100;
		ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
		sword.addEnchantment(Enchantment.KNOCKBACK, 2);

		Objects.requireNonNull(witherSkeleton.getEquipment()).setItemInMainHand(sword);
		witherSkeleton.getEquipment().setItemInMainHandDropChance(0.0F);
		witherSkeleton.getEquipment().setItemInOffHand(sword);
		witherSkeleton.getEquipment().setItemInOffHandDropChance(0.0F);

		witherSkeleton.getAttribute(Attribute.MAX_HEALTH).setBaseValue(100.0);
		witherSkeleton.setHealth(100.0);
		witherSkeleton.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		witherSkeleton.setTarget(p);
		witherSkeleton.teleport(p);
		witherSkeleton.setCustomNameVisible(true);
		witherSkeleton.addScoreboardTag("SkyblockBoss");
		witherSkeleton.addScoreboardTag("InfuriatedSkeleton");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "From the ashes of the Wither Skeleton rises its reincarnation: a HIGHLY INFURIATED Wither Skeleton");
		Bukkit.getLogger().info(p.getName() + " has found a Highly Infuriated Wither Skeleton!.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		witherSkeleton.setPersistent(true);
		witherSkeleton.setRemoveWhenFarAway(false);
		return newName;
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
