package mobs.hardmode.withers;

import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;

public class WitherSkeletonSoul implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		WitherSkeleton witherSkeleton;
		if(e instanceof WitherSkeleton) {
			witherSkeleton = (WitherSkeleton) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		witherSkeleton.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Henchman of the Soul" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ a");
		Utils.changeName(witherSkeleton);
		witherSkeleton.addScoreboardTag("Soul");

		witherSkeleton.setAI(false);

		teleport(witherSkeleton);

		return "";
	}

	private void teleport(Mob e) {
		if(!e.isDead()) {
			Utils.teleport(e, WitherKing.getEntity().getLocation(), 16);
			Utils.scheduleTask(() -> teleport(e), 300);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(damagee.getHealth() - originalDamage < 1) {
			List<EntityType> immune = new ArrayList<>();
			immune.add(EntityType.WITHER_SKELETON);
			immune.add(EntityType.WITHER);
			Utils.spawnTNT(damagee, damagee.getLocation(), 0, 12, 25, immune);
			WitherKing.defeatHenchman("Soul");
			damagee.remove();
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}