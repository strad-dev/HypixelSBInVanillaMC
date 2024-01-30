package listeners;

import misc.SimilarData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Objects;

public class FireIsNotBald implements Listener {
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.getCause().equals(EntityDamageEvent.DamageCause.THORNS) || e.getCause().equals(EntityDamageEvent.DamageCause.LAVA) || e.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || e.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
			if(e.getEntity() instanceof LivingEntity entity) {
				e.setCancelled(true);
				if(entity.getNoDamageTicks() == 0) {
					double finalDamage = CustomDamage.calculateFinalDamage(entity, e.getDamage());
					double absorption = entity.getAbsorptionAmount();
					if(finalDamage > absorption) {
						entity.setAbsorptionAmount(0.0);
						finalDamage -= absorption;
					} else {
						entity.setAbsorptionAmount(absorption - finalDamage);
						finalDamage = 0.0;
					}
					double newHealth = entity.getHealth() - finalDamage;
					if(newHealth < 0.0) {
						e.setCancelled(false);
						entity.setHealth(0.1);
						e.setDamage(10);
					} else if(finalDamage > 0) {
						entity.setHealth(entity.getHealth() - finalDamage);
						entity.playHurtAnimation(0.0F);
						entity.getWorld().playSound(entity, Objects.requireNonNull(entity.getHurtSound()), 1.0F, 1.0F);
						entity.setNoDamageTicks(10);

						// change nametag health
						SimilarData.changeName(entity);
					}
				}
			}
		}
	}
}