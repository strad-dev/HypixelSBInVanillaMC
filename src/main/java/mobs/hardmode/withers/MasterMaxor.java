package mobs.hardmode.withers;

import listeners.DamageType;
import misc.Plugin;
import mobs.withers.CustomWither;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

import static listeners.CustomDamage.teleport;

public class MasterMaxor implements CustomWither {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";

		e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(2.0);
		e.getAttribute(Attribute.GENERIC_FLYING_SPEED).setBaseValue(2.0);
		e.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(2);
		e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1000.0);
		e.setHealth(1000.0);
		e.addScoreboardTag("Maxor");
		e.addScoreboardTag("HardMode");
		Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": You thought my normal version was too weak, huh?");
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": You fool!"), 40);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I was just getting warmed up."), 80);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Now you shall face my ULTIMATE FORM."), 120);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": PREPARE TO DIE!!!"), 160);
		Bukkit.getLogger().info("MASTER Maxor has been summoned!");
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}
		Random random = new Random();
		if(!type.equals(DamageType.RANGED) && random.nextDouble() < 0.12 || type.equals(DamageType.RANGED) && random.nextDouble() < 0.06) {
			teleport(damagee, damager, 12);
		}
		return true;

	}

	@Override
	public boolean whenDamaging(LivingEntity damagee) {
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {
		Vector zoooooooooooom = skull.getVelocity();
		zoooooooooooom = zoooooooooooom.add(zoooooooooooom).add(zoooooooooooom);
		skull.setVelocity(zoooooooooooom);
	}
}
