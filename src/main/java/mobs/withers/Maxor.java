package mobs.withers;

import listeners.DamageType;
import misc.DamageData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.Random;

import static misc.Utils.teleport;

public class Maxor implements CustomWither {
	@Override
	public String onSpawn(Player p, Mob e) {
		Wither wither;
		if(e instanceof Wither) {
			wither = (Wither) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		wither.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(2.0);
		wither.getAttribute(Attribute.FLYING_SPEED).setBaseValue(2.0);
		wither.addScoreboardTag("Maxor");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "MAXOR, the fastest Wither in the universe, has come to destroy you 0.01 seconds faster than all other Withers!");
		Bukkit.getLogger().info("Maxor has been summoned!");
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.LETHAL_ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}
		Random random = new Random();
		if(!type.equals(DamageType.RANGED) && random.nextDouble() < 0.1 || type.equals(DamageType.RANGED) && random.nextDouble() < 0.05) {
			teleport(damagee, 8);
		}
		return true;

	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {
		Vector zoooooooooooom = skull.getVelocity();
		zoooooooooooom = zoooooooooooom.add(zoooooooooooom).add(zoooooooooooom);
		skull.setVelocity(zoooooooooooom);
	}
}
