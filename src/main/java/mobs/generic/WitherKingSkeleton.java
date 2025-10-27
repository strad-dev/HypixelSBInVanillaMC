package mobs.generic;

import listeners.DamageType;
import misc.DamageData;
import mobs.CustomMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

public class WitherKingSkeleton implements CustomMob {
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
