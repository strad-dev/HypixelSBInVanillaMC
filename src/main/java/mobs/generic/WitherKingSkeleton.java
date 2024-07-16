package mobs.generic;

import listeners.DamageType;
import mobs.CustomMob;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

public class WitherKingSkeleton implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		return "";
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(type.equals(DamageType.PLAYER_MAGIC)) {
			damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You cannot deal " + type + " to the Wither King's Guards.");
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee) {
		return true;
	}
}
