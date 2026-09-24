package manhunt;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

/** Manhunt nether rules: better pearl barters, no brutes. Listener gates both on {@link Manhunt#active()}. */
public final class ManhuntPiglins {
	private ManhuntPiglins() {}

	private static final Random RANDOM = new Random();

	/**
	 * Total weight of vanilla 26.2's {@code loot_table/gameplay/piglin_bartering.json}. Only figure needed, so
	 * the other entries are never listed; if it drifts the two weights below are just slightly off.
	 */
	private static final int VANILLA_WEIGHT = 469;

	/** Vanilla pearls are weight 10; +15 makes 25. */
	private static final int EXTRA_PEARL_WEIGHT = 15;

	/** Pre-1.16.2 weight; vanilla removed it in 20w28a. */
	private static final int GLOWSTONE_WEIGHT = 20;

	private static final int PEARLS_MIN = 4;
	private static final int PEARLS_MAX = 8;
	private static final int GLOWSTONE_MIN = 5;
	private static final int GLOWSTONE_MAX = 12;

	/**
	 * Pearls weight 25 for 4-8 (vanilla 10 for 2-4), glowstone 20 for 5-12, rest untouched. Vanilla already
	 * rolled, so sample a DELTA over 469 + 15 + 20 = 504: 15 pay pearls, 20 glowstone, 469 keep vanilla's roll.
	 * Pearls land on 15/504 + 469/504 x 10/469 = 25/504. Vanilla's own pearl stack is resized to 4-8.
	 * Edits the outcome list in place; it's the only handle the event gives.
	 */
	public static void reroll(List<ItemStack> outcome) {
		int roll = RANDOM.nextInt(VANILLA_WEIGHT + EXTRA_PEARL_WEIGHT + GLOWSTONE_WEIGHT);

		if(roll < EXTRA_PEARL_WEIGHT) {
			outcome.clear();
			outcome.add(new ItemStack(Material.ENDER_PEARL, between(PEARLS_MIN, PEARLS_MAX)));
			return;
		}

		if(roll < EXTRA_PEARL_WEIGHT + GLOWSTONE_WEIGHT) {
			outcome.clear();
			outcome.add(new ItemStack(Material.GLOWSTONE_DUST, between(GLOWSTONE_MIN, GLOWSTONE_MAX)));
			return;
		}

		for(ItemStack item : outcome) {
			if(item != null && item.getType() == Material.ENDER_PEARL) {
				item.setAmount(between(PEARLS_MIN, PEARLS_MAX));
			}
		}
	}

	/** Uniform in {@code [min, max]}, inclusive. */
	private static int between(int min, int max) {
		return min + RANDOM.nextInt(max - min + 1);
	}

	/** Permanent: reset doesn't restore brutes, nothing records where they were. */
	public static void demote(PiglinBrute brute) {
		if(!brute.isValid()) return;
		Location where = brute.getLocation();
		boolean immune = brute.isImmuneToZombification();
		boolean staysPut = !brute.getRemoveWhenFarAway();
		brute.remove();
		replace(where, immune, staysPut);
	}

	/** Keeps the brute's zombification immunity and despawn flag; otherwise a default piglin, gear included. */
	public static void replace(Location where, boolean immune, boolean staysPut) {
		World world = where.getWorld();
		if(world == null) return;
		world.spawn(where, Piglin.class, CreatureSpawnEvent.SpawnReason.CUSTOM, piglin -> {
			piglin.setImmuneToZombification(immune);
			piglin.setRemoveWhenFarAway(!staysPut);
		});
	}

	/** Run on start: already-loaded brutes are past both the spawn event and the chunk load. */
	public static void demoteLoaded() {
		for(World world : Bukkit.getWorlds()) {
			for(PiglinBrute brute : world.getEntitiesByClass(PiglinBrute.class)) {
				demote(brute);
			}
		}
	}
}
