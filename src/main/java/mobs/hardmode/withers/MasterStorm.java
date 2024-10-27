package mobs.hardmode.withers;

import listeners.CustomMobs;
import listeners.DamageType;
import misc.Plugin;
import mobs.withers.CustomWither;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;

import java.util.Random;

import static listeners.CustomMobs.spawnLightning;

public class MasterStorm implements CustomWither {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Storm" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";

		e.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(2);
		e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1000.0);
		e.setHealth(1000.0);
		e.addScoreboardTag("Storm");
		e.addScoreboardTag("HardMode");
		e.getWorld().setThundering(true);
		e.getWorld().setWeatherDuration(1000000);
		Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Awakening my true form, what an interesting proposition.");
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Have you not faced Maxor yet?  He would have warned you of this."), 40);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": However, I suppose you have made up your mind to fight me."), 80);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": In that case, I cannot do anything but enjoy your suffering."), 120);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Good Luck."), 160);
		Bukkit.getLogger().info("MASTER Storm has been summoned!");
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnLightning(e, 64), 200);
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return (((Wither) damagee).getInvulnerabilityTicks() == 0 || type == DamageType.ABSOLUTE) && type != DamageType.IFRAME_ENVIRONMENTAL;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee) {
		Random random = new Random();
		if(random.nextBoolean()) {
			damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.LIGHTNING_BOLT);
		}
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {
		CustomMobs.spawnLightning(skull, 32);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> CustomMobs.spawnLightning(skull, 32),12);
	}
}