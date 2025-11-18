package mobs.hardmode.generic;

import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ConjoinedBrood implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		Spider spider;
		if(e instanceof Spider) {
			spider = (Spider) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Conjoined Brood" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		spider.getAttribute(Attribute.MAX_HEALTH).setBaseValue(500.0);
		spider.setHealth(500.0);
		spider.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.67);
		spider.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(30.0);
		spider.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		spider.setTarget(Utils.getNearestPlayer(spider));
		spider.setCustomNameVisible(true);
		spider.addScoreboardTag("SkyblockBoss");
		spider.addScoreboardTag("ConjoinedBrood");
		spider.addScoreboardTag("HardMode");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		spider.setPersistent(true);
		spider.setRemoveWhenFarAway(false);
		spider.setCustomName(newName + " " + ChatColor.RESET + ChatColor.RED + "❤" + ChatColor.YELLOW + " a");
		Utils.changeName(spider);

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
