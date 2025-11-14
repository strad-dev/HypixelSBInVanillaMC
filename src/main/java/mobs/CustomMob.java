package mobs;

import listeners.DamageType;
import misc.DamageData;
import mobs.enderDragons.*;
import mobs.generic.*;
import mobs.hardmode.AtonedHorror;
import mobs.hardmode.PrimalDragon;
import mobs.hardmode.withers.*;
import mobs.withers.*;
import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface CustomMob {
	class MobRegistry {
		private static final Map<String, CustomMob> MOBS = new HashMap<>();
		private static final Map<String, CustomMob> HARDMODE_MOBS = new HashMap<>();

		static {
			// Initialize all mob singletons
			// Generic mobs
			MOBS.put("RevenantHorror", new RevenantHorror());
			MOBS.put("AtonedHorror", new AtonedHorror());
			MOBS.put("Sadan", new Sadan());
			MOBS.put("Voidgloom", new VoidgloomSeraph());
			MOBS.put("MutantEnderman", new MutantEnderman());
			MOBS.put("meloGnorI", new meloGnorI());
			MOBS.put("Broodfather", new Broodfather());
			MOBS.put("Chickzilla", new Chickzilla());
			MOBS.put("InfuriatedSkeleton", new InfuriatedWitherSkeleton());

			// Normal mode withers
			MOBS.put("Maxor", new mobs.withers.Maxor());
			MOBS.put("Storm", new mobs.withers.Storm());
			MOBS.put("Goldor", new mobs.withers.Goldor());
			MOBS.put("Necron", new mobs.withers.Necron());

			// Hard mode withers (separate registry to avoid key conflicts)
			HARDMODE_MOBS.put("Maxor", new mobs.hardmode.withers.Maxor());
			HARDMODE_MOBS.put("Storm", new mobs.hardmode.withers.Storm());
			HARDMODE_MOBS.put("Goldor", new mobs.hardmode.withers.Goldor());
			HARDMODE_MOBS.put("Necron", new mobs.hardmode.withers.Necron());
			HARDMODE_MOBS.put("WitherKing", new mobs.hardmode.withers.WitherKing());

			// Dragons
			MOBS.put("HolyDragon", new Holy());
			MOBS.put("OldDragon", new Old());
			MOBS.put("ProtectorDragon", new Protector());
			MOBS.put("StrongDragon", new Strong());
			MOBS.put("SuperiorDragon", new Superior());
			MOBS.put("UnstableDragon", new Unstable());
			MOBS.put("WiseDragon", new Wise());
			MOBS.put("YoungDragon", new Young());
			MOBS.put("WitherKingDragon", new WitherKingDragon());
			MOBS.put("PrimalDragon", new PrimalDragon());

			// Wither Skeletons
			MOBS.put("Power", new WitherSkeletonPower());
			MOBS.put("Fire", new WitherSkeletonFire());
			MOBS.put("Ice", new WitherSkeletonIce());
			MOBS.put("Soul", new WitherSkeletonSoul());
			MOBS.put("Martial", new WitherSkeletonMartial());
			MOBS.put("GuardSkeleton", new WitherKingSkeleton());

			// Default wither handler
			MOBS.put("DefaultWither", new Default());
		}

		public static CustomMob get(String tag, boolean hardMode) {
			if(hardMode && HARDMODE_MOBS.containsKey(tag)) {
				return HARDMODE_MOBS.get(tag);
			}
			return MOBS.get(tag);
		}
	}

	static CustomMob getMob(Entity e) {
		if (e != null) {
			Set<String> tags = e.getScoreboardTags();
			boolean isHardMode = tags.contains("HardMode");

			// Check each tag against the registry keys
			for (String tag : tags) {
				CustomMob mob = MobRegistry.get(tag, isHardMode);
				if (mob != null) {
					return mob;
				}
			}

			// Special case for default withers
			if (e instanceof Wither) {
				return MobRegistry.get("DefaultWither", false);
			}
		}
		return null;
	}

	String onSpawn(Player p, Mob e);

	/**
	 * Handles custom entity behavior when the mob is damaged
	 *
	 * @param damagee        the custom entity
	 * @param damager        the entity dealing damage
	 * @param originalDamage the original damage amount
	 * @param type           the original damage type
	 * @param data			 extra data associated with the damage
	 * @return true if the original calculation can proceed; false if not
	 */
	boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data);

	/**
	 * Handles custom entity behavior when the mob deals damage
	 *
	 * @param damagee        the entity taking the damage
	 * @param damager        the custom entity
	 * @param originalDamage the original damage amount
	 * @param type           the original damage type
	 * @param data			 extra data associated with the damage
	 * @return true if the original calculation can proceed; false if not
	 */
	boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data);
}