package mobs.withers;

import listeners.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;

import static listeners.CustomDamage.calculateFinalDamage;

public class Necron implements CustomWither {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Necron" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		e.addScoreboardTag("Necron");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "NECRON, the strongest Wither in the universe, has come to wipe you straight off the planet!");
		Bukkit.getLogger().info("Necron has been summoned!");
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return (((Wither) damagee).getInvulnerabilityTicks() == 0 || type == DamageType.ABSOLUTE) && type != DamageType.IFRAME_ENVIRONMENTAL;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		calculateFinalDamage(damagee, damager, 4, DamageType.RANGED);
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {

	}
}
