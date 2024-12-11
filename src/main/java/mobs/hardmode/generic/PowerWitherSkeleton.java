package mobs.hardmode.generic;

import listeners.DamageType;
import misc.PluginUtils;
import mobs.CustomMob;
import mobs.hardmode.withers.MasterWitherKing;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PowerWitherSkeleton implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		return ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Henchman of Power" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ ";
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(damagee.getHealth() - originalDamage < 1) {
			MasterWitherKing.defeatHenchman("Power");
			damagee.remove();
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		PluginUtils.spawnTNT(damager, damagee.getLocation(), 20, 6, 10, new ArrayList<>());
		return true;
	}
}
