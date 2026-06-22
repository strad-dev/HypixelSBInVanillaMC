package mobs.enderDragons;

import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;

public class Wise implements CustomDragon {
	@Override
	public void whenShootingFireball(DragonFireball fireball) {

	}

	@Override
	public String onSpawn(Player p, Mob e) {
		e.setCanPickupItems(false);
		EnderDragon dragon;
		if(e instanceof EnderDragon) {
			dragon = (EnderDragon) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String name = "<gold><bold>﴾ <red><bold>Wise Dragon<gold><bold> ﴿";
		dragon.addScoreboardTag("WiseDragon");
		Bukkit.broadcast(Utils.msg("<red><bold>The WISE DRAGON has arrived to destroy you using smart tactics!"));
		Bukkit.getLogger().info("The Wise Dragon has been summoned!");
		return name;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}
