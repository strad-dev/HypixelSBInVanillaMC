package mobs.withers;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.DamageData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;

public class Necron implements CustomWither {
	@Override
	public String onSpawn(Player p, Mob e) {
		Wither wither;
		if(e instanceof Wither) {
			wither = (Wither) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Necron" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		wither.addScoreboardTag("Necron");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "NECRON, the strongest Wither in the universe, has come to wipe you straight off the planet!");
		Bukkit.getLogger().info("Necron has been summoned!");
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return (((Wither) damagee).getInvulnerabilityTicks() == 0 || type == DamageType.LETHAL_ABSOLUTE) && type != DamageType.IFRAME_ENVIRONMENTAL;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		CustomDamage.calculateFinalDamage(damagee, damager, 4, DamageType.RANGED);
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {

	}
}
