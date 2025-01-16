package items;

import org.bukkit.entity.Player;

/**
 * A subinterface for all items with abilities.
 */

public interface AbilityItem extends CustomItem {

	void onRightClick(Player p);

	void onLeftClick(Player p);

	int manaCost();

	String cooldownTag();

	int cooldown();
}
