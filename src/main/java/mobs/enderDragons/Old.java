package mobs.enderDragons;

import listeners.DamageType;
import misc.DamageData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;

import java.util.Objects;

public class Old implements CustomDragon {
	@Override
	public void whenShootingFireball(DragonFireball fireball) {

	}

	@Override
	public String onSpawn(Player p, Mob e) {
		String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Old Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		Objects.requireNonNull(e.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(300.0);
		e.setHealth(300.0);
		e.addScoreboardTag("OldDragon");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The OLD DRAGON has arrived for one last battle!");
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