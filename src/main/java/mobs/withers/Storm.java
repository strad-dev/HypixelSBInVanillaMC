package mobs.withers;

import listeners.CustomMobs;
import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;

import java.util.Random;

import static listeners.CustomMobs.spawnLightning;

public class Storm implements CustomWither {
	@Override
	public String onSpawn(Player p, Mob e) {
		Wither wither;
		if(e instanceof Wither) {
			wither = (Wither) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Storm" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		wither.addScoreboardTag("Storm");
		wither.getWorld().setThundering(true);
		wither.getWorld().setWeatherDuration(1000000);
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "STORM, most explosive Wither in the universe, has come to smite you with his Lightning!");
		Bukkit.getLogger().info("Storm has been summoned!");
		Utils.scheduleTask(() -> spawnLightning(wither, 64), 200);
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return (((Wither) damagee).getInvulnerabilityTicks() == 0 || type == DamageType.LETHAL_ABSOLUTE) && type != DamageType.IFRAME_ENVIRONMENTAL;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		Random random = new Random();
		if(random.nextBoolean()) {
			damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.LIGHTNING_BOLT);
		}
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {
		CustomMobs.spawnLightning(skull, 32);
	}
}