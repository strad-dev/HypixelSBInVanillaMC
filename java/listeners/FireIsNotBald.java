package listeners;

import misc.SimilarData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class FireIsNotBald implements Listener {
	private final Map<LivingEntity, Long> noDamageTimes = new WeakHashMap<>();

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.getCause().equals(EntityDamageEvent.DamageCause.THORNS) || e.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION) || e.getCause().equals(EntityDamageEvent.DamageCause.LAVA) || e.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || e.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
			if(e.getEntity() instanceof LivingEntity entity) {
				e.setCancelled(true);

				long currentTime = System.currentTimeMillis();
				boolean hasLastDamageTime = noDamageTimes.containsKey(entity);
				long lastDamageTime = noDamageTimes.computeIfAbsent(entity, entity2 -> currentTime);

				if(hasLastDamageTime && currentTime - lastDamageTime > 490) {
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

						// change nametag health
						SimilarData.changeName(entity);
					}
					noDamageTimes.put(entity, currentTime);
				}
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		noDamageTimes.remove(e.getEntity());
	}
}