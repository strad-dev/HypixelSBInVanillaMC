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

/**
 * The two nether rules a Manhunt turns on: piglins barter on a more generous pearl table, and piglin brutes
 * are swapped for ordinary piglins. Both are gated on {@link Manhunt#active()} by the listener, so they are
 * in force between {@code /manhunt start} and {@code /manhunt reset} and at no other time.
 */
public final class ManhuntPiglins {
	private ManhuntPiglins() {}

	private static final Random RANDOM = new Random();

	/**
	 * Total weight of the vanilla 26.2 bartering table
	 * ({@code data/minecraft/loot_table/gameplay/piglin_bartering.json}). The only figure we need out of it:
	 * {@link #reroll} re-weights the whole table against this number without listing the other eighteen
	 * entries, so nothing here has to be revisited when vanilla shuffles gravel or spectral arrows around.
	 * A vanilla total that drifts only moves the two weights below slightly off their nominal share.
	 */
	private static final int VANILLA_WEIGHT = 469;

	/** Pearls go from vanilla's weight 10 to 25, so 15 rides on top of the vanilla roll. */
	private static final int EXTRA_PEARL_WEIGHT = 15;

	/** Glowstone dust back at its pre-1.16.2 weight - vanilla dropped it from the table in 20w28a. */
	private static final int GLOWSTONE_WEIGHT = 20;

	private static final int PEARLS_MIN = 4;
	private static final int PEARLS_MAX = 8;
	private static final int GLOWSTONE_MIN = 5;
	private static final int GLOWSTONE_MAX = 12;

	/**
	 * Re-rolls one barter onto the Manhunt table: ender pearls at weight 25 for 4-8 (vanilla is 10 for 2-4)
	 * and glowstone dust back in at weight 20 for 5-12, every other entry untouched.
	 *
	 * <p>Vanilla has already rolled by the time this runs and there is no second draw to be had, so the two
	 * changed entries are sampled as a DELTA over it rather than by rebuilding the table. Roll over
	 * {@code 469 + 15 + 20 = 504}: the first 15 pay pearls, the next 20 pay glowstone, and the remaining 469
	 * keep whatever vanilla rolled. Pearls therefore land on {@code 15/504} plus vanilla's own
	 * {@code 469/504 x 10/469}, which is exactly the 25/504 the table wants, and every other entry keeps its
	 * vanilla weight over the new total. Vanilla's own pearls come in 2-4, so that stack is resized on the
	 * way through.
	 *
	 * <p>The outcome list is the only handle the event gives us, so it is edited in place.
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

	/** A uniform count in {@code [min, max]}, both ends included, the way a loot table's uniform roll reads. */
	private static int between(int min, int max) {
		return min + RANDOM.nextInt(max - min + 1);
	}

	/**
	 * Takes a brute that is already in the world out and stands a piglin where it stood. The brute is gone
	 * for good: {@code /manhunt reset} does not put one back, because nothing records where they were.
	 */
	public static void demote(PiglinBrute brute) {
		if(!brute.isValid()) return;
		Location where = brute.getLocation();
		boolean immune = brute.isImmuneToZombification();
		boolean staysPut = !brute.getRemoveWhenFarAway();
		brute.remove();
		replace(where, immune, staysPut);
	}

	/**
	 * Spawns the piglin that stands in for a brute, carrying over the two flags a bastion guard depends on:
	 * its zombification immunity and whether it despawns. Everything else is a default piglin, gear included
	 * - the point is a mob the Speedrunners can actually run past.
	 */
	public static void replace(Location where, boolean immune, boolean staysPut) {
		World world = where.getWorld();
		if(world == null) return;
		world.spawn(where, Piglin.class, CreatureSpawnEvent.SpawnReason.CUSTOM, piglin -> {
			piglin.setImmuneToZombification(immune);
			piglin.setRemoveWhenFarAway(!staysPut);
		});
	}

	/**
	 * Swaps out every brute in a loaded chunk. Run by {@code /manhunt start}, since a bastion somebody had
	 * already walked into is loaded and past both the spawn event and the chunk load that would have caught
	 * it.
	 */
	public static void demoteLoaded() {
		for(World world : Bukkit.getWorlds()) {
			for(PiglinBrute brute : world.getEntitiesByClass(PiglinBrute.class)) {
				demote(brute);
			}
		}
	}
}
