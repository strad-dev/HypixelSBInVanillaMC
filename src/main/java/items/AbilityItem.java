package items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A subinterface for all items with abilities.
 */

public interface AbilityItem extends CustomItem {
	boolean hasLeftClickAbility();

	boolean onRightClick(Player p);

	boolean onLeftClick(Player p);

	int manaCost();

	/**
	 * What the specific stack costs to fire. Only the Manhunt Hyperion overrides this - its cost is the rung
	 * the stack sits on - so the ability dispatcher asks this rather than {@link #manaCost()}.
	 */
	default int manaCost(ItemStack item) {
		return manaCost();
	}

	String cooldownTag();

	int cooldown();
}