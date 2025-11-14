package items;

import items.misc.*;
import items.summonItems.HighlyInfuriatedWitherSkeletonSpawnEgg;
import items.weapons.Scylla;
import items.weapons.Terminator;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * A generic interface for all custom items.
 */

public interface CustomItem {
	class ItemRegistry {
		private static final Map<String, CustomItem> ITEMS = new HashMap<>();

		static {
			// Initialize all items once
			ITEMS.put("skyblock/combat/scylla", new Scylla());
			ITEMS.put("skyblock/combat/aspect_of_the_void", new AOTV());
			ITEMS.put("skyblock/combat/ice_spray_wand", new IceSpray());
			ITEMS.put("skyblock/combat/terminator", new Terminator());
			ITEMS.put("skyblock/combat/wand_of_restoration", new WandOfRestoration());
			ITEMS.put("skyblock/combat/wand_of_atonement", new WandOfAtonement());
			ITEMS.put("skyblock/combat/holy_ice", new HolyIce());
			ITEMS.put("skyblock/combat/bonzo_staff", new BonzoStaff());
			ITEMS.put("skyblock/combat/tactical_insertion", new TacticalInsertion());
			ITEMS.put("skyblock/combat/gyro", new GyrokineticWand());
			ITEMS.put("skyblock/summon/wither_skeleton_spawn_egg", new HighlyInfuriatedWitherSkeletonSpawnEgg());
		}

		static CustomItem get(String id) {
			return ITEMS.get(id);
		}
	}

	/**
	 * Returns an instance of the item given the item
	 *
	 * @param item the item
	 * @return the item
	 */
	static CustomItem getItem(ItemStack item) {
		if(!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
			return null;
		}
		return getItem(item.getItemMeta().getLore().getFirst());
	}

	/**
	 * Returns an instance of the item given an ID
	 *
	 * @param id the ID of the item
	 * @return the item
	 */
	static CustomItem getItem(String id) {
		return ItemRegistry.get(id);
	}
}