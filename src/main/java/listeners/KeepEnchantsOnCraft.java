package listeners;

import misc.AddRecipes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class KeepEnchantsOnCraft implements Listener {
	@EventHandler
	public void onCraftItem(CraftItemEvent e) {
		// The Manhunt upgrades are SHAPELESS, so grid slot 4 is whatever the player happened to drop there -
		// usually one of the swords going in, whose enchantments have no business on the result. Those
		// recipes carry their own enchantments across in ManhuntListener.onPrepareCraft.
		if(AddRecipes.isManhuntRecipe(e.getRecipe())) return;
		try {
			e.getInventory().getResult().addUnsafeEnchantments(e.getInventory().getMatrix()[4].getEnchantments());
		} catch(Exception exception) {
			// nothing
		}
	}
}
