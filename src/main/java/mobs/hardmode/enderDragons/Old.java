package mobs.hardmode.enderDragons;

import listeners.DamageType;
import mobs.enderDragons.CustomDragon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;

import java.util.Objects;

import static listeners.CustomDamage.calculateFinalDamage;

public class Old implements CustomDragon {
	@Override
	public void whenShootingFireball(DragonFireball fireball) {

	}

	@Override
	public String onSpawn(Player p, Mob e) {
		String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Old Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(400.0);
		e.setHealth(400.0);
		e.addScoreboardTag("OldDragon");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The OLD DRAGON has arrived for one last battle!");
		Bukkit.getLogger().info("The Old Dragon has been summoned!");
		return name;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(type == DamageType.RANGED) {
			calculateFinalDamage(damagee, damager, originalDamage / 3, DamageType.RANGED);
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee) {
		return true;
	}
}
