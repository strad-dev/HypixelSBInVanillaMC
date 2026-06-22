package mobs.hardmode.withers;

import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;

public class WitherSkeletonPower implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		e.setCanPickupItems(false);
		WitherSkeleton witherSkeleton;
		if(e instanceof WitherSkeleton) {
			witherSkeleton = (WitherSkeleton) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		Utils.changeName(witherSkeleton, "<gold><bold>﴾ <red><bold>Henchman of Power<gold><bold> ﴿");
		witherSkeleton.addScoreboardTag("Power");
		return "";
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(damagee.getHealth() - originalDamage < 1) {
			List<EntityType> immune = new ArrayList<>();
			immune.add(EntityType.WITHER_SKELETON);
			immune.add(EntityType.WITHER);
			Utils.spawnTNT(damagee, damagee.getLocation(), 0, 12, 25, immune);
			WitherKing.defeatHenchman("Power");
			damagee.remove();
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(type == DamageType.MELEE) {
			Utils.spawnTNT(damager, damagee.getLocation(), 20, 6, 10, new ArrayList<>());
		}
		return true;
	}
}
