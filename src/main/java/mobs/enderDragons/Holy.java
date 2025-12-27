package mobs.enderDragons;

import listeners.DamageType;
import misc.DamageData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Holy implements CustomDragon {

	@Override
	public void whenShootingFireball(DragonFireball fireball) {

	}

	@Override
	public String onSpawn(Player p, Mob e) {
		EnderDragon dragon;
		if(e instanceof EnderDragon) {
			dragon = (EnderDragon) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Holy Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		dragon.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, -1, 0));
		dragon.addScoreboardTag("HolyDragon");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The HOLY DRAGON has arrived to cleanse the world of evil!");
		Bukkit.getLogger().info("The Holy Dragon has been summoned!");

		return name;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}