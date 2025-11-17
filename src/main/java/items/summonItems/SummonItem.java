package items.summonItems;

import mobs.CustomMob;
import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.Map;

public interface SummonItem {
	// Registry mapping item IDs to mob registry keys
	class SummonRegistry {
		private static final Map<String, SummonMapping> SUMMONS = new HashMap<>();

		// Inner class to hold mob keys and entity type requirement
		private record SummonMapping(String normalMobKey, String hardModeMobKey, Class<? extends Mob> requiredEntity) {
			// Constructor for mobs with same key in both modes
			SummonMapping(String mobKey, Class<? extends Mob> requiredEntity) {
				this(mobKey, mobKey, requiredEntity);
			}
		}

		static {
			// Map item IDs to their corresponding mob registry keys
			// Format: (normal mode key, hard mode key, required entity type)
			SUMMONS.put("skyblock/summon/superior_remnant", new SummonMapping("Voidgloom", "Voidgloom", Enderman.class));
			SUMMONS.put("skyblock/summon/corrupt_pearl", new SummonMapping("Zealot", "ZealotBrusier", Enderman.class));
			SUMMONS.put("skyblock/summon/antimatter", new SummonMapping("meloGnorI", "ObfuscatedmeloGnorI", IronGolem.class));
			SUMMONS.put("skyblock/summon/omega_egg", new SummonMapping("Chickzilla", "EnragedChickzilla", Chicken.class));
			SUMMONS.put("skyblock/summon/spider_relic", new SummonMapping("TarantulaBroodfather", "PrimordialBroodfather", Spider.class));
			SUMMONS.put("skyblock/summon/atoned_flesh", new SummonMapping("RevenantHorror", "AtonedHorror", Zombie.class));
			SUMMONS.put("skyblock/summon/giant_flesh", new SummonMapping("Sadan", "TheGiantOne", Zombie.class));

			// Wither Skeleton doesn't change between modes
			SUMMONS.put("skyblock/summon/wither_skeleton_spawn_egg", new SummonMapping("InfuriatedSkeleton", WitherSkeleton.class));
		}

		static CustomMob spawn(Mob entity, String id, boolean hardMode) {
			SummonMapping mapping = SUMMONS.get(id);
			if(mapping == null || !mapping.requiredEntity.isInstance(entity)) {
				return null;
			}

			// Get the appropriate mob key based on hard mode
			String mobKey = hardMode ? mapping.hardModeMobKey : mapping.normalMobKey;

			// Use the MobRegistry to get the singleton instance
			// Since we're using specific keys, we always pass false for hardMode parameter
			// (the hard mode distinction is already in the key itself)
			return CustomMob.MobRegistry.get(mobKey, false);
		}
	}

	static CustomMob spawnABoss(Mob entity, String id, boolean hardMode) {
		return SummonRegistry.spawn(entity, id, hardMode);
	}
}