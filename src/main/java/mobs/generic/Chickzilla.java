package mobs.generic;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Chickzilla implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		e.setCanPickupItems(false);
		String newName = "<gold><bold>﴾ <red><bold>Chickzilla<gold><bold> ﴿";
		e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(500.0);
		e.setHealth(500.0);
		e.getAttribute(Attribute.SCALE).setBaseValue(2.0);
		e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		e.setTarget(p);
		e.setCustomNameVisible(true);
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("Chickzilla");
		p.sendMessage(Utils.msg("<red><bold>The Omega Egg hatches into the Chickzilla!"));
		Bukkit.getLogger().info(p.getName() + " has summoned Chickzilla.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		((Ageable) e).setAdult();
		e.setPersistent(true);
		e.setRemoveWhenFarAway(false);
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(damager instanceof LivingEntity damager1) {
			damager.sendMessage(Utils.msg("<red><bold>Chickzilla has REFLECTED " + originalDamage / 2 + " Damage back to you!"));
			CustomDamage.customMobs(damager1, damagee, originalDamage / 2, DamageType.MELEE); // damager takes 50% of their original damage
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}
