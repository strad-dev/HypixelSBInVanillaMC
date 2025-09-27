package mobs.enderDragons;

import listeners.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;

import static listeners.CustomDamage.calculateFinalDamage;

public class Wise implements CustomDragon {
	@Override
	public void whenShootingFireball(DragonFireball fireball) {

	}

	@Override
	public String onSpawn(Player p, Mob e) {
		String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Wise Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		e.addScoreboardTag("WiseDragon");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The WISE DRAGON has arrived to destroy you using smart tactics!");
		Bukkit.getLogger().info("The Wise Dragon has been summoned!");
		return name;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return true;
	}
}
