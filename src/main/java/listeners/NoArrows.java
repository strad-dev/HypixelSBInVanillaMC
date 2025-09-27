package listeners;

import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class NoArrows implements Listener {
	@EventHandler
	public void onPlayerPickupArrow(PlayerPickupArrowEvent e) {
		if(!(e.getArrow() instanceof Trident)) {
			e.setCancelled(true);
		}
	}
}
