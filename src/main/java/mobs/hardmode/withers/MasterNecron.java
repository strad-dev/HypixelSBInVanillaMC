package mobs.hardmode.withers;

import listeners.DamageType;
import misc.Plugin;
import mobs.withers.CustomWither;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;

import static listeners.CustomDamage.calculateFinalDamage;

public class MasterNecron implements CustomWither {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Necron" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		e.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(2);
		e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1000.0);
		e.setHealth(1000.0);
		e.addScoreboardTag("Necron");
		e.addScoreboardTag("HardMode");
		Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": So you've finally decided to take on Hard Mode.");
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Impressive.  I didn't know you could come this far."), 40);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": But it all ends here: I am stronger than you can ever imagine!"), 80);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I have spent centuries building up my power, never showing it."), 120);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Until today.  Goodbye."), 160);
		Bukkit.getLogger().info("MASTER Necron has been summoned!");
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return (((Wither) damagee).getInvulnerabilityTicks() == 0 || type == DamageType.ABSOLUTE) && type != DamageType.IFRAME_ENVIRONMENTAL;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		calculateFinalDamage(damagee, Plugin.getNearestPlayer(damagee), 6, DamageType.RANGED);
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {

	}
}
