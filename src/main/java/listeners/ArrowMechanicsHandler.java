package listeners;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ArrowMechanicsHandler implements Listener {

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		// Handle arrows hitting blocks - remove Terminator arrows
		if (e.getHitBlock() != null) {
			if (e.getEntity() instanceof Arrow arrow && arrow.getScoreboardTags().contains("TerminatorArrow")) {
				arrow.remove();
			}
		} else {
			// Handle arrows hitting entities - prevent self-hits
			if (!(e.getEntity() instanceof Arrow arrow) ||
					!(e.getHitEntity() instanceof Player player) ||
					!(arrow.getShooter() instanceof Player shooter) ||
					!player.equals(shooter)) {
				return;
			}
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDragonArrowHit(EntityDamageByEntityEvent e) {
		// Check if this is an arrow hitting a dragon or dragon part
		if (!(e.getDamager() instanceof Arrow arrow)) {
			return;
		}

		Entity damaged = e.getEntity();

		// Check if we hit a dragon or dragon part
		boolean isDragonHit = (damaged instanceof EnderDragon) || (damaged instanceof EnderDragonPart);

		if (isDragonHit) {
			// Consume all pierce levels so the arrow stops after hitting the first part
			arrow.setPierceLevel(0);

			// Optional: You could also remove the arrow entirely after a short delay
			// to ensure it doesn't continue flying
			// Bukkit.getScheduler().runTaskLater(plugin, arrow::remove, 1L);
		}
	}
}