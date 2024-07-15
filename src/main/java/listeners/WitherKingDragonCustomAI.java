package listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;

public class WitherKingDragonCustomAI implements Listener {
	@EventHandler
	public void onEnderDragonChangePhase(EnderDragonChangePhaseEvent e) {
		if(e.getEntity().getScoreboardTags().contains("WitherKingDragon")) {
			e.setCancelled(true);
		}
	}
}
