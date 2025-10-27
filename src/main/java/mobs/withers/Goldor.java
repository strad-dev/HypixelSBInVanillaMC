package mobs.withers;

import listeners.DamageType;
import misc.DamageData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;

public class Goldor implements CustomWither {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Goldor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		e.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(13.0);
		e.addScoreboardTag("Goldor");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "GOLDOR, the most defensive Wither in the universe, has come to stand in your way!");
		Bukkit.getLogger().info("Goldor has been summoned!");
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return (((Wither) damagee).getInvulnerabilityTicks() == 0 || type == DamageType.ABSOLUTE) && type != DamageType.IFRAME_ENVIRONMENTAL;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {

	}
}
