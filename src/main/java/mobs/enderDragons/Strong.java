package mobs.enderDragons;

import listeners.DamageType;
import misc.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;

import static listeners.CustomDamage.calculateFinalDamage;

public class Strong implements CustomDragon {
	@Override
	public void whenShootingFireball(DragonFireball fireball) {

	}

	@Override
	public String onSpawn(Player p, Mob e) {
		String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Strong Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		e.addScoreboardTag("StrongDragon");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The STRONG DRAGON has arrived to pulverize you!");
		Bukkit.getLogger().info("The Strong Dragon has been summoned!");
		return name;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		calculateFinalDamage(damagee, PluginUtils.getNearestPlayer(damagee), 6, DamageType.RANGED);
		return true;
	}
}
