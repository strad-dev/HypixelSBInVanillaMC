package listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class KeepEnchantsOnCraft implements Listener {
	@EventHandler
	public void onCraftItem(CraftItemEvent e) {
		e.getInventory().getResult().addUnsafeEnchantments(e.getInventory().getMatrix()[4].getEnchantments());
	}
}