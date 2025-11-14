package mobs.hardmode.withers;

import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class WitherSkeletonIce implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		e.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Henchman of Ice" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ a");
		Utils.changeName(e);
		e.addScoreboardTag("Ice");
		return "";
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(damagee.getHealth() - originalDamage < 1) {
			List<EntityType> immune = new ArrayList<>();
			immune.add(EntityType.WITHER_SKELETON);
			immune.add(EntityType.WITHER);
			Utils.spawnTNT(damagee, damagee.getLocation(), 0, 12, 25, immune);
			WitherKing.defeatHenchman("Ice");
			damagee.remove();
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(!damagee.getScoreboardTags().contains("IceSprayed")) {
			damagee.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 201, 3));
			damagee.addScoreboardTag("IceSprayed");
			Utils.scheduleTask(() -> damagee.removeScoreboardTag("IceSprayed"), 201);
			damagee.getWorld().playSound(damagee, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
			damagee.getWorld().spawnParticle(Particle.SNOWFLAKE, damagee.getLocation(), 1000);
			if(damagee instanceof Player p) {
				p.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "❄ ❅ ❆", ChatColor.BLUE + "Brrrr...", 0, 201, 0);
				p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "The Henchman of Ice Ice Sprayed you for 10 seconds!");
			}
		}
		return true;
	}
}