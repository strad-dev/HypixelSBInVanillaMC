package listeners;

import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

public class EditSkull implements Listener {
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		Random random = new Random();
		if(e.getEntity() instanceof WitherSkull skull) {
			String name = ((Wither) Objects.requireNonNull(skull.getShooter())).getCustomName();
			assert name != null;
			if(name.contains("Maxor")) {
				Vector zoooooooooooom = skull.getDirection();
				zoooooooooooom = zoooooooooooom.add(zoooooooooooom).add(zoooooooooooom);
				skull.setVelocity(zoooooooooooom);
			} else if(name.contains("Storm") && random.nextDouble() < 0.1) {
				CustomMobs.spawnLightning(skull);
			}
		} else if(e.getEntity() instanceof DragonFireball fireball) {
			String name = ((EnderDragon) Objects.requireNonNull(fireball.getShooter())).getCustomName();
			assert name != null;
			if(name.contains("Unstable Dragon") && random.nextDouble() < 0.667) {
				CustomMobs.spawnLightning(fireball);
			} else if(name.contains("Superior Dragon") && random.nextDouble() < 0.333) {
				CustomMobs.spawnLightning(fireball);
			} else if(name.contains("Young Dragon")) {
				Vector zoooooooooooom = fireball.getDirection();
				zoooooooooooom = zoooooooooooom.add(zoooooooooooom).add(zoooooooooooom);
				fireball.setVelocity(zoooooooooooom);
			}
		}
	}
}
