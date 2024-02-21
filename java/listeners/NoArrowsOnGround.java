package listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class NoArrowsOnGround implements Listener {
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		if(!(e.getHitBlock() == null)) {
			if(e.getEntity() instanceof Arrow arrow) {
				arrow.remove();
			}
		}

	}
}
