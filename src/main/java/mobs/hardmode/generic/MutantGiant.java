package mobs.hardmode.generic;

import listeners.DamageType;
import misc.DamageData;
import mobs.CustomMob;
import org.bukkit.entity.*;

public class MutantGiant implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		return "";
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return !(damager instanceof FallingBlock);
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}