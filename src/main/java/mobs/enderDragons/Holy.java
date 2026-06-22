package mobs.enderDragons;

import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Holy implements CustomDragon {

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

		String name = "<gold><bold>﴾ <red><bold>Holy Dragon<gold><bold> ﴿";
		dragon.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, -1, 0));
		dragon.addScoreboardTag("HolyDragon");
		Bukkit.broadcast(Utils.msg("<red><bold>The HOLY DRAGON has arrived to cleanse the world of evil!"));
		Bukkit.getLogger().info("The Holy Dragon has been summoned!");

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