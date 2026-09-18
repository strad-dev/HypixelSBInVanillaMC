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

	/**
	 * Whether a cast refused because the ability is still on cooldown should say NOTHING - no chat line, no
	 * sound. Default false: a player who pressed the button is owed an explanation for nothing happening.
	 *
	 * <p>True for a RAPID-FIRE ability, where the explanation is noise. The Terminator's Salvation is a left
	 * click carrying a 0.8s cooldown, i.e. the attack button on a bow, so a player holding it down collected
	 * a chat line every few ticks for the whole fight. A cooldown that short is its own feedback - the beam
	 * visibly does not fire.
	 */
	default boolean quietCooldown() {
		return false;
	}

	String cooldownTag();

	int cooldown();
}