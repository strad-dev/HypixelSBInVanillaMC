package listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NoArrowsOnGround implements Listener {
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		if(!(e.getHitBlock() == null)) {
			if(e.getEntity() instanceof Arrow arrow && arrow.getScoreboardTags().contains("TerminatorArrow")) {
				arrow.remove();
			}
		} else {
			if(!(e.getEntity() instanceof Arrow arrow) || !(e.getHitEntity() instanceof Player player) || !(arrow.getShooter() instanceof Player shooter) || !player.equals(shooter)) {
				return;
			}
			e.setCancelled(true);
		}
	}
}