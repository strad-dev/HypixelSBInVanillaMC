package items;

import items.misc.*;
import items.weapons.Scylla;
import items.weapons.Terminator;
import org.bukkit.entity.Player;

/**
 * A subinterface for all items with abilities.
 */

public interface AbilityItem extends CustomItem {

	/**
	 * Returns an instance of the item
	 *
	 * @param id the ID of the item
	 * @return the item
	 */
	static AbilityItem getItem(String id) {
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
			default -> {
				return null;
			}
		}
	}

	void onRightClick(Player p);

	void onLeftClick(Player p);

	int manaCost();

	String cooldownTag();

	int cooldown();
}
