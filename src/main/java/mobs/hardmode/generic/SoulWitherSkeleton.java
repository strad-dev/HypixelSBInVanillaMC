package mobs.hardmode.generic;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.Plugin;
import misc.PluginUtils;
import mobs.CustomMob;
import mobs.hardmode.withers.MasterWitherKing;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

public class SoulWitherSkeleton implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		e.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Henchman of the Soul" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ ");
		PluginUtils.changeName(e);

		e.setAI(false);

		teleport(e);

		return "";
	}

	private void teleport(Mob e) {
		if(!e.isDead()) {
			CustomDamage.teleport(e, Plugin.getNearestPlayer(e), 16);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> teleport(e), 300);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(damagee.getHealth() - originalDamage < 1) {
			MasterWitherKing.defeatHenchman("Soul");
			damagee.remove();
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return true;
	}
}
