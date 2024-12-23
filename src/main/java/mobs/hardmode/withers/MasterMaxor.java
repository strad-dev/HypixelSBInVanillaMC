package mobs.hardmode.withers;

import listeners.DamageType;
import misc.Plugin;
import misc.PluginUtils;
import mobs.withers.CustomWither;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.Random;

public class MasterMaxor implements CustomWither {
	private static final String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";

	@Override
	public String onSpawn(Player p, Mob e) {
		PluginUtils.spawnTNT(e, e.getLocation(), 0, 64, 300, new ArrayList<>());
		
		p = Plugin.getNearestPlayer(e);

		e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(500.0);
		e.setHealth(500.0);
		e.addScoreboardTag("Maxor");
		e.addScoreboardTag("HardMode");
		e.addScoreboardTag("400Crystal");
		e.addScoreboardTag("200Crystal");
		Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": It seems to me that you think we are weak.");
		p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
		Player finalP = p;
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			finalP.getWorld().playSound(finalP, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Foolish desicion!  You have not seen us at our most powerful!");
		}, 50);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			finalP.getWorld().playSound(finalP, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Centuries of rumination, only to be rudely awaken by you unworthy scum!");
		}, 100);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			finalP.getWorld().playSound(finalP, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": You are brave for attempting to fight us.");
		}, 150);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			finalP.getWorld().playSound(finalP, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": WE WILL WIPE THE FLOOR WITH YOUR REMAINS!!!");
		}, 200);
		Bukkit.getLogger().info("A player has initiated THE GAUNTLET!");

		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> PluginUtils.spawnTNT(e, e.getLocation(), 0, 32, 50, new ArrayList<>()), 220);

		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnGuards(e), 300);

		return name;
	}

	private void spawnGuards(Mob e) {
		if(!e.isDead()) {
			PluginUtils.spawnGuards(e, 15);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnGuards(e), 300);
		}
	}

	private void spawnCrystal(LivingEntity wither, int which) {
		Location l = wither.getLocation();
		Player p = Plugin.getNearestPlayer(wither);
		Random random = new Random();
		l.add(random.nextInt(48) - 24, 0, random.nextInt(48) - 24);
		for(int i = 319; i > -64; i --) {
			Block b = l.getWorld().getBlockAt((int) l.getX(), i, (int) l.getZ());
			if(b.getType() != Material.AIR && b.getType() != Material.VOID_AIR) {
				l.setY(i + 1);
				EnderCrystal crystal = (EnderCrystal) wither.getWorld().spawnEntity(l, EntityType.END_CRYSTAL);
				crystal.setInvulnerable(true);
				crystal.setCustomName(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Energy Crystal");
				crystal.addScoreboardTag("SkyblockBoss");
				if(which == 400) {
					wither.removeScoreboardTag("400Crystal");
					p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
					Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": HAHAHA!  Good luck getting around my tricks!");
					wither.setHealth(400.0);
				} else {
					wither.removeScoreboardTag("200Crystal");
					p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
					Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": If you fail, you should try and try again!");
					wither.setHealth(200.0);
				}
				wither.addScoreboardTag("Invulnerable");
				return;
			}
		}
		Bukkit.broadcastMessage(ChatColor.RED + "Oops!  Unable to summon a crystal!  Take it for free.");
		wither.removeScoreboardTag("400Crystal");
		wither.removeScoreboardTag("200Crystal");
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		double hp = damagee.getHealth();

		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			return false;
		} else if(damagee.getScoreboardTags().contains("400Crystal") && hp - originalDamage < 400) {
			spawnCrystal(damagee, 400);
			return false;
		} else if(damagee.getScoreboardTags().contains("200Crystal") && hp - originalDamage < 200) {
			spawnCrystal(damagee, 200);
			return false;
		} else if(hp - originalDamage < 1) {
			damagee.setAI(false);
			damagee.addScoreboardTag("Invulnerable");
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Well looks like you defeated me.");
			Player p = Plugin.getNearestPlayer(damagee);
			p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": This is only the beginning!");
			}, 50);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				damagee.remove();
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0F, 1.0F);
				PluginUtils.spawnTNT(damagee, damagee.getLocation(), 0, 32, 50, new ArrayList<>());
			}, 100);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Storm" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": It seems that you have defeated my brother.");
			}, 250);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Storm" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": No worries, the party is just getting started!");
			}, 300);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				Wither wither = (Wither) damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.WITHER);
				new MasterStorm().onSpawn(Plugin.getNearestPlayer(damagee), wither);
			}, 320);
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {

	}
}
