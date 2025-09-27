package mobs.enderDragons;

import listeners.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;

import java.util.Objects;

import static listeners.CustomDamage.calculateFinalDamage;

public class Holy implements CustomDragon {

	@Override
	public void whenShootingFireball(DragonFireball fireball) {

	}

	@Override
	public String onSpawn(Player p, Mob e) {
		String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Young Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		Objects.requireNonNull(e.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(2.00);
		e.addScoreboardTag("HolyDragon");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The YOUNG DRAGON has arrived to practise yeeting Nons into the Void!");
		Bukkit.getLogger().info("The Holy Dragon has been summoned!");

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