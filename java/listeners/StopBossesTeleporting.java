package listeners;

import org.bukkit.entity.Enderman;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.potion.PotionEffectType;

public class StopBossesTeleporting implements Listener {
	@EventHandler
	public void onEntityTeleport(EntityTeleportEvent e) {
		if(e.getEntity() instanceof Enderman enderman) {
			String name = enderman.getCustomName();
			if(enderman.hasPotionEffect(PotionEffectType.UNLUCK) || name.contains("Voidgloom Seraph") || name.contains("Mutant Enderman")) {
				e.setCancelled(true);
			}
		}
	}
}