package listeners;

import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class SomeTNTDoNoDamage implements Listener {
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		if(e.getEntity() instanceof TNTPrimed tnt && tnt.getScoreboardTags().contains("AtonedHorror")) {
			e.setCancelled(true);
		}
	}
}