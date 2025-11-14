package mobs.enderDragons;

import listeners.DamageType;
import misc.DamageData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;

import java.util.Random;

import static listeners.CustomMobs.spawnLightning;

public class Unstable implements CustomDragon {
	@Override
	public void whenShootingFireball(DragonFireball fireball) {
		Random random = new Random();
		if(random.nextBoolean()) {
			spawnLightning(fireball, 64);
		}
	}

	@Override
	public String onSpawn(Player p, Mob e) {
		EnderDragon dragon;
		if(e instanceof EnderDragon) {
			dragon = (EnderDragon) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Unstable Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		spawnLightning(dragon, 128);
		dragon.addScoreboardTag("UnstableDragon");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The UNSTABLE DRAGON has arrived to cause chaos!");
		Bukkit.getLogger().info("The Unstable Dragon has been summoned!");
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
