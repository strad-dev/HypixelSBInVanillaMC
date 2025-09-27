package mobs.enderDragons;

import listeners.DamageType;
import misc.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.Random;

import static listeners.CustomMobs.spawnLightning;

public class Superior implements CustomDragon {
	@Override
	public void whenShootingFireball(DragonFireball fireball) {
		Random random = new Random();
		if(random.nextDouble() < 0.25) {
			spawnLightning(fireball, 64);
		}
	}

	@Override
	public String onSpawn(Player p, Mob e) {
		String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Superior Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		Objects.requireNonNull(e.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(300.0);
		e.setHealth(300.0);
		Objects.requireNonNull(e.getAttribute(Attribute.ARMOR)).setBaseValue(6.25);
		Objects.requireNonNull(e.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(0.875);
		e.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, -1, 0));
		e.addScoreboardTag("SuperiorDragon");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The SUPERIOR DRAGON has arrived to utterly destroy you!");
		Bukkit.getLogger().info("The Superior Dragon has been summoned!");
		return name;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(!type.equals(DamageType.PLAYER_MAGIC)) {
			PluginUtils.dealCustomDamage(damagee, damager, 3f, false);
		}
		return true;
	}
}
