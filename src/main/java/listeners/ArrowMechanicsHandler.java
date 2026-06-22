package listeners;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import io.papermc.paper.event.entity.EntityPushedByEntityAttackEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class ArrowMechanicsHandler implements Listener {
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		if(e.getEntity() instanceof WindCharge windCharge && windCharge.getScoreboardTags().contains("Bonzo")) {
			e.setCancelled(true);
		}
	}

	// 26.2: Bukkit's EntityKnockbackByEntityEvent is deprecated for removal. Paper's unified EntityKnockbackEvent
	// delivers the by-entity case as EntityPushedByEntityAttackEvent, whose getPushedBy() replaces getSourceEntity().
	@EventHandler
	public void onEntityKnockback(EntityKnockbackEvent e) {
		if(e instanceof EntityPushedByEntityAttackEvent pushed
				&& pushed.getPushedBy() instanceof WindCharge windCharge
				&& windCharge.getScoreboardTags().contains("Bonzo")) {
			e.setCancelled(true);
		}
	}

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
				p.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, windCharge.getLocation(), 350, 0, 0, 0, 0.75);
				p.getWorld().spawnParticle(Particle.CRIT, p.getLocation(), 150, 0, 0, 0, 2);
				p.getWorld().playSound(windCharge.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2.0F, 1.0F);

				if(distance <= 16) {
					if(!(p instanceof CraftPlayer craftPlayer)) return;
					ServerPlayer serverPlayer = craftPlayer.getHandle();

					Vector direction = p.getLocation().toVector().subtract(windCharge.getLocation().toVector()).normalize();
					direction.setY(0);
					direction.normalize();
					direction.multiply(1.52552);
					direction.setY(0.5);

					if(!Double.isFinite(direction.getX())) {
						direction.setX(0);
					}
					if(!Double.isFinite(direction.getZ())) {
						direction.setZ(0);
					}

					serverPlayer.setOnGround(false);
					p.setVelocity(direction);
					// Send the motion packet NOW instead of waiting for hurtMarked to be serviced on the
					// player's next aiStep — that deferral ships it a tick late (this fires in the windcharge's
					// entity tick, after the player's own tick that frame), so the client integrates a tick late
					// and the full first-tick rise is lost. Immediate send matches Hypixel (full 0.5 on tick 1).
					serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
					serverPlayer.hurtMarked = false;
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