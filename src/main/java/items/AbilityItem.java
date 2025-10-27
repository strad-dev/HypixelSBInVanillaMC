package items;

import org.bukkit.entity.Player;

/**
 * A subinterface for all items with abilities.
 */

public interface AbilityItem extends CustomItem {
	boolean hasLeftClickAbility();

	boolean onRightClick(Player p);

	boolean onLeftClick(Player p);

	int manaCost();

	String cooldownTag();

	int cooldown();
}
