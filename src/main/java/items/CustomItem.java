package items;

import items.misc.*;
import items.summonItems.HighlyInfuriatedWitherSkeletonSpawnEgg;
import items.weapons.Scylla;
import items.weapons.Terminator;
import org.bukkit.inventory.ItemStack;

/**
 * A generic interface for all custom items.
 */

public interface CustomItem {
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
		switch(id) {
			case "skyblock/combat/scylla" -> {
				return new Scylla();
			}
			case "skyblock/combat/aspect_of_the_void" -> {
				return new AOTV();
			}
			case "skyblock/combat/ice_spray_wand" -> {
				return new IceSpray();
			}
			case "skyblock/combat/terminator" -> {
				return new Terminator();
			}
			case "skyblock/combat/wand_of_restoration" -> {
				return new WandOfRestoration();
			}
			case "skyblock/combat/wand_of_atonement" -> {
				return new WandOfAtonement();
			}
			case "skyblock/combat/holy_ice" -> {
				return new HolyIce();
			}
			case "skyblock/combat/bonzo_staff" -> {
				return new BonzoStaff();
			}
			case "skyblock/summon/wither_skeleton_spawn_egg" -> {
				return new HighlyInfuriatedWitherSkeletonSpawnEgg();
			}
			default -> {
				return null;
			}
		}
	}
}