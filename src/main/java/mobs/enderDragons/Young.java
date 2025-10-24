package mobs.enderDragons;

import listeners.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.Random;

import static misc.PluginUtils.teleport;

public class Young implements CustomDragon {
	@Override
	public void whenShootingFireball(DragonFireball fireball) {
		Vector zoooooooooooom = fireball.getVelocity();
		zoooooooooooom = zoooooooooooom.add(zoooooooooooom).add(zoooooooooooom);
		fireball.setVelocity(zoooooooooooom);
	}

	@Override
	public String onSpawn(Player p, Mob e) {
		String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Young Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		e.addScoreboardTag("YoungDragon");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The YOUNG DRAGON has arrived to destroy nons 1 second faster!");
		Bukkit.getLogger().info("The Young Dragon has been summoned!");
		return name;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		Random random = new Random();
		if(random.nextDouble() < 0.05 && damagee.getLocation().distanceSquared(new Location(damagee.getWorld(), 0, 60, 0)) > 75) {
			teleport(damagee, 16);
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return true;
	}
}
