package mobs.hardmode.withers;

import listeners.DamageType;
import misc.Plugin;
import mobs.withers.CustomWither;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;

public class MasterGoldor implements CustomWither {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Goldor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";

		e.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(14.0);
		e.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(2);
		e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1000.0);
		e.setHealth(1000.0);
		e.addScoreboardTag("Goldor");
		e.addScoreboardTag("HardMode");
		Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": It seems that you want to take me on in my purest form.");
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Trust me, attacking me won't do you much good."), 40);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Running is your best option, I take basically no damage."), 80);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": How do you think you will deal with me???"), 120);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.broadcastMessage(newName + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Answer: You don't."), 160);

		Bukkit.getLogger().info("MASTER Goldor has been summoned!");
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return (((Wither) damagee).getInvulnerabilityTicks() == 0 || type == DamageType.ABSOLUTE) && type != DamageType.IFRAME_ENVIRONMENTAL;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee) {
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {

	}
}
