package mobs.hardmode.generic;

import listeners.DamageType;
import mobs.CustomMob;
import mobs.hardmode.withers.MasterWitherKing;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

public class IceWitherSkeleton implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		return ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Henchman of Ice" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ ";
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(damagee.getHealth() - originalDamage < 1) {
			MasterWitherKing.defeatHenchman("Ice");
			damagee.remove();
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(!damagee.getScoreboardTags().contains("IceSprayed")) {
			damagee.addScoreboardTag("IceSprayed");
			damagee.getWorld().playSound(damagee, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
			damagee.getWorld().spawnParticle(Particle.SNOWFLAKE, damagee.getLocation(), 1000);
		}
		return true;
	}
}
