package listeners;

import misc.Plugin;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.Objects;
import java.util.Random;

public class EditSkull implements Listener {
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if(e.getEntity() instanceof WitherSkull skull) {
			String name = ((Wither) Objects.requireNonNull(skull.getShooter())).getName();
			Random random = new Random();
			if(name.contains("Maxor")) {
				Location l = Objects.requireNonNull(Plugin.getNearestPlayer((Entity) skull.getShooter())).getLocation();
				l.add(skull.getDirection().multiply(-1));
				skull.teleport(l);
			} else if(name.contains("Storm")) {
				if(random.nextDouble() > 0.9) {
					skull.getWorld().spawnEntity(Objects.requireNonNull(Plugin.getNearestPlayer((Entity) skull.getShooter())).getLocation(), EntityType.LIGHTNING);
				}
			} else if(name.contains("Necron")) {
				Objects.requireNonNull(Plugin.getNearestPlayer((Entity) skull.getShooter())).damage(3);
			}
		}
	}
}
