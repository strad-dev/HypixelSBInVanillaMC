package mobs.withers;

import listeners.DamageType;
import misc.DamageData;
import org.bukkit.entity.*;

public class Default implements CustomWither {
	// avoid null pointers if a default wither is spawned
	@Override
	public void whenShootingSkull(WitherSkull skull) {
	}

	@Override
	public String onSpawn(Player p, Mob e) {
		return "";
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
