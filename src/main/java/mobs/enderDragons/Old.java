package mobs.enderDragons;

import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;

public class Old implements CustomDragon {
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

		String name = "<gold><bold>﴾ <red><bold>Old Dragon<gold><bold> ﴿";
		dragon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(300.0);
		dragon.setHealth(300.0);
		dragon.addScoreboardTag("OldDragon");
		Bukkit.broadcast(Utils.msg("<red><bold>The OLD DRAGON has arrived for one last battle!"));
		Bukkit.getLogger().info("The Old Dragon has been summoned!");
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