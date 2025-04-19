package mobs.hardmode.withers;

import listeners.DamageType;
import misc.Plugin;
import misc.PluginUtils;
import mobs.CustomMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;

public class SoulWitherSkeleton implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		e.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Henchman of the Soul" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ a");
		PluginUtils.changeName(e);
		e.addScoreboardTag("Soul");

		e.setAI(false);

		teleport(e);

		return "";
	}

	private void teleport(Mob e) {
		if(!e.isDead()) {
			PluginUtils.teleport(e, MasterWitherKing.getEntity().getLocation(), 16);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> teleport(e), 300);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(damagee.getHealth() - originalDamage < 1) {
			List<EntityType> immune = new ArrayList<>();
			immune.add(EntityType.WITHER_SKELETON);
			immune.add(EntityType.WITHER);
			PluginUtils.spawnTNT(damagee, damagee.getLocation(), 0, 12, 25, immune);
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