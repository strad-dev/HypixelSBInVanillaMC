package mobs.hardmode.generic;

import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ConjoinedBrood implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		e.setCanPickupItems(false);
		Spider spider;
		if(e instanceof Spider) {
			spider = (Spider) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = "<gold><bold>﴾ <red><bold>Conjoined Brood<gold><bold> ﴿";
		spider.getAttribute(Attribute.MAX_HEALTH).setBaseValue(500.0);
		spider.setHealth(500.0);
		spider.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.67);
		spider.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(35.0);
		Utils.setupBoss(spider, p, "ConjoinedBrood", "HardMode");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		Utils.changeName(spider, newName);

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
