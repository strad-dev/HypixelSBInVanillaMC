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

	/** Per-stack cost; the dispatcher calls this. Only the Manhunt Hyperion overrides it (cost depends on rung). */
	default int manaCost(ItemStack item) {
		return manaCost();
	}

	/**
	 * True = a cast refused by cooldown says nothing, no chat or sound. For rapid-fire abilities: Salvation is
	 * left click with a 0.8s cooldown and spammed chat all fight.
	 */
	default boolean quietCooldown() {
		return false;
	}

	String cooldownTag();

	int cooldown();
}