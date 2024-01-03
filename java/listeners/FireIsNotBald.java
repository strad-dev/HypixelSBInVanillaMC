package listeners;

import misc.SimilarData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FireIsNotBald implements Listener {
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.getCause().equals(EntityDamageEvent.DamageCause.THORNS) || e.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || e.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
			if(e.getEntity() instanceof LivingEntity entity) {
				entity.setNoDamageTicks(1);
				SimilarData.changeName(entity);
			}
		}
	}
}