package listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.Objects;

public class NoArrowsOnGround implements Listener {
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		if(!(e.getHitBlock() == null)) {
			if(e.getEntity() instanceof Arrow arrow) {
				arrow.remove();
			}
		}
		if(e.getHitEntity() instanceof IronGolem golem) {
			try {
				if(Objects.requireNonNull(golem.getCustomName()).contains("meloG norI")) {
					e.setCancelled(true);
					if(e.getEntity().getShooter() instanceof Player p) {
						p.sendMessage(ChatColor.BOLD + String.valueOf(ChatColor.RED) + "You cannot deal ranged damage to the meloG norI.");
					}
				}
			} catch(NullPointerException exception) {
				// do nothing
			}
		}
	}
}
