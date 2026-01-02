package listeners;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class ArrowMechanicsHandler implements Listener {

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		// Handle arrows hitting blocks - remove Terminator arrows
		if(e.getEntity() instanceof Arrow arrow) {
			if(e.getHitBlock() != null) {
				if(arrow.getScoreboardTags().contains("TerminatorArrow")) {
					arrow.remove();
				}
			} else {
				// Handle arrows hitting entities - prevent self-hits
				if(e.getHitEntity() instanceof Player player && arrow.getShooter() instanceof Player shooter && player.equals(shooter)) {
					e.setCancelled(true);
				}
			}
		} else if(e.getEntity() instanceof WindCharge windCharge && windCharge.getScoreboardTags().contains("Bonzo")) {
			e.setCancelled(true);
			windCharge.remove();

			if(windCharge.getShooter() instanceof Player p) {
				double distance = p.getLocation().distanceSquared(windCharge.getLocation());

				if(distance <= 16) {
					e.setCancelled(true);
					windCharge.remove();

					// Calculate direction vector from wind charge to shooter
					Vector direction = p.getLocation().toVector().subtract(windCharge.getLocation().toVector()).normalize();

					// Scale to 1.52552 on X/Z plane with Y at 0.5
					direction.setY(0);
					direction.normalize();
					direction.multiply(1.52552);
					direction.setY(0.5);

					// Apply velocity to the shooter
					p.setVelocity(direction);
				}
			}
		}
	}

	@EventHandler
	public void onDragonArrowHit(EntityDamageByEntityEvent e) {
		// Check if this is an arrow hitting a dragon or dragon part
		if(!(e.getDamager() instanceof Arrow arrow)) {
			return;
		}

		Entity damaged = e.getEntity();

		// Check if we hit a dragon or dragon part
		boolean isDragonHit = (damaged instanceof EnderDragon) || (damaged instanceof EnderDragonPart);

		if(isDragonHit) {
			// Consume all pierce levels so the arrow stops after hitting the first part
			arrow.setPierceLevel(0);
			arrow.remove();

			// Optional: You could also remove the arrow entirely after a short delay
			// to ensure it doesn't continue flying
			// Bukkit.getScheduler().runTaskLater(plugin, arrow::remove, 1L);
		}
	}
}