package listeners;

import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;

import java.util.Objects;

public class StopBossesTeleporting implements Listener {
	@EventHandler
	public void onEntityTeleport(EntityTeleportEvent e) {
		if(e.getEntity() instanceof Enderman enderman) {
			String name = enderman.getCustomName();
			try {
				if(enderman.getFireTicks() > 0 || enderman.getScoreboardTags().contains("IceSprayed") || Objects.requireNonNull(name).contains("Voidgloom Seraph") || name.contains("Mutant Enderman")) {
					e.setCancelled(true);
				}
			} catch(Exception exception) {
				// nothing here
			}
		}
	}
}