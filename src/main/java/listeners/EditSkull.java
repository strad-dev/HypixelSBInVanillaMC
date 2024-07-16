package listeners;

import mobs.CustomMob;
import mobs.enderDragons.CustomDragon;
import mobs.withers.CustomWither;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class EditSkull implements Listener {
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if(e.getEntity() instanceof WitherSkull skull) {
			CustomWither wither = (CustomWither) CustomMob.getMob((Wither) skull.getShooter());
			wither.whenShootingSkull(skull);
		} else if(e.getEntity() instanceof DragonFireball fireball) {
			CustomDragon dragon = (CustomDragon) CustomMob.getMob((EnderDragon) fireball.getShooter());
			dragon.whenShootingFireball(fireball);
		}
	}
}
