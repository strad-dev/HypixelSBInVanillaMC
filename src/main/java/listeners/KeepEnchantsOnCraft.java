package listeners;

import misc.AddRecipes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class KeepEnchantsOnCraft implements Listener {
	@EventHandler
	public void onCraftItem(CraftItemEvent e) {
		// Manhunt upgrades are SHAPELESS, so grid slot 4 is whatever went there, usually a sword whose enchants
		// don't belong on the result. Those carry enchants across in ManhuntListener.onPrepareCraft.
		if(AddRecipes.isManhuntRecipe(e.getRecipe())) return;
		try {
			e.getInventory().getResult().addUnsafeEnchantments(e.getInventory().getMatrix()[4].getEnchantments());
		} catch(Exception exception) {
			// nothing
		}
	}
}
