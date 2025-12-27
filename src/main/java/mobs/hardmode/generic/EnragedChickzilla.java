package mobs.hardmode.generic;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EnragedChickzilla implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Enraged Chickzilla" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(1000.0);
		e.setHealth(1000.0);
		e.getAttribute(Attribute.SCALE).setBaseValue(4.0);
		e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		e.setTarget(Utils.getNearestPlayer(e));
		e.setCustomNameVisible(true);
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("EnragedChickzilla");
		e.addScoreboardTag("HardMode");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Omega Egg hatches into the Enraged Chickzilla!");
		Bukkit.getLogger().info(p.getName() + " has summoned Enraged Chickzilla.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		((Ageable) e).setAdult();
		e.setPersistent(true);
		e.setRemoveWhenFarAway(false);
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(damager instanceof LivingEntity damager1) {
			damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Chickzilla has REFLECTED " + originalDamage + " Damage back to you!");
			CustomDamage.customMobs(damager1, damagee, originalDamage, DamageType.MELEE); // damager takes 100% of their original damage
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}
