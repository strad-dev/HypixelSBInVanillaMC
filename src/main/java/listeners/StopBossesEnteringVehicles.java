package listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;

public class StopBossesEnteringVehicles implements Listener {
	@EventHandler
	public void onEntityMount(EntityMountEvent event) {
		if (event.getEntity().getScoreboardTags().contains("SkyblockBoss")) {
			event.setCancelled(true);
		}
	}
}
