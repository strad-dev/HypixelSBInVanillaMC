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

		e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(800.0);
		e.setHealth(800.0);
		e.addScoreboardTag("Maxor");
		e.addScoreboardTag("HardMode");
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("600Crystal");
		e.addScoreboardTag("300Crystal");
		Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": WELL WELL WELL LOOK WHO'S BACK FOR A REMATCH!");
		PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I HAVE BEEN PRACTISING 40 HOURS A DAY SINCE WE LAST MET!");
		}, 60);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": MY TRICKS ARE MORE SOPHISTICATED THAN EVER; YOU WILL NEVER GET AROUND THEM!");
		}, 120);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": NOW LET'S HAVE SOME FUN HERE!");
		}, 180);
		Bukkit.getLogger().info("A player has initiated THE GAUNTLET!");

		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> PluginUtils.spawnTNT(e, e.getLocation(), 0, 32, 50, new ArrayList<>()), 220);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnGuards(e), 520);

		return name;
	}

	private void spawnGuards(Mob e) {
		if(!e.isDead() && !e.getScoreboardTags().contains("Dead")) {
			PluginUtils.spawnGuards(e, 3);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnGuards(e), 300);
		}
	}

	private void spawnCrystal(LivingEntity wither, int which) {
		Location l = wither.getLocation();
		Random random = new Random();
		l.add(random.nextInt(32) - 16, 0, random.nextInt(32) - 16);
		for(int i = 319; i > -64; i --) {
			Block b = l.getWorld().getBlockAt((int) l.getX(), i, (int) l.getZ());
			if(b.getType() != Material.AIR && b.getType() != Material.VOID_AIR) {
				l.setY(i + 2);
				EnderCrystal crystal = (EnderCrystal) wither.getWorld().spawnEntity(l, EntityType.END_CRYSTAL);
				crystal.setInvulnerable(true);
				crystal.setCustomName(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Energy Crystal");
				crystal.addScoreboardTag("SkyblockBoss");
				if(which == 600) {
					wither.removeScoreboardTag("600Crystal");
					PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
					Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": HAHAHA!  GOOD LUCK GETTING AROUND MY TRICKS!");
					Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
						if(wither.getScoreboardTags().contains("Invulnerable") && wither.getScoreboardTags().contains("300Crystal")) {
							Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": ARE YOU REALLY THIS BAD?!  CAN YOU NOT SEE EXPLOSIVES AROUND YOU?!");
						}
					}, 600L);
					wither.setHealth(600.0);
				} else {
					wither.removeScoreboardTag("300Crystal");
					PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
					Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": IF YOU FAIL ONCE, YOU SHOULD SIMPLY TRY AGAIN!");
					wither.setHealth(300.0);
				}
				wither.addScoreboardTag("Invulnerable");
				Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> wither.addScoreboardTag("InvulnerableReminder"), 60L);
				Bukkit.broadcastMessage(ChatColor.YELLOW + "You have picked up an Energy Crystal!  Maybe it is useful?");
				return;
			}
		}
		Bukkit.broadcastMessage(ChatColor.RED + "Oops!  Unable to summon a crystal!  Take it for free.");
		wither.removeScoreboardTag("600Crystal");
		wither.removeScoreboardTag("300Crystal");
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		double hp = damagee.getHealth();

		if(damager instanceof Player && damagee.getScoreboardTags().contains("Invulnerable")) {
			if(damagee.getScoreboardTags().contains("InvulnerableReminder")) {
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": YOUR WEAK, PUNY ATTACKS CANNOT GET AROUND MY TRICKS!");
				damagee.removeScoreboardTag("InvulnerableReminder");
			}
			return false;
		} else if(damagee.getScoreboardTags().contains("600Crystal") && hp - originalDamage < 600) {
			spawnCrystal(damagee, 600);
			PluginUtils.changeName(damagee);
			return false;
		} else if(damagee.getScoreboardTags().contains("300Crystal") && hp - originalDamage < 300) {
			spawnCrystal(damagee, 300);
			PluginUtils.changeName(damagee);
			return false;
		} else if(hp - originalDamage < 1) {
			damagee.setAI(false);
			damagee.addScoreboardTag("Invulnerable");
			damagee.addScoreboardTag("Dead");
			damagee.setHealth(1.0);
			PluginUtils.changeName(damagee);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": HOW DID YOU DEFEAT ME?!?!?!");
			PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": THIS IS ONLY THE BEGINNING!!!");
			}, 60);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				damagee.remove();
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_DEATH);
				PluginUtils.spawnTNT(damagee, damagee.getLocation(), 0, 32, 50, new ArrayList<>());
			}, 100);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Storm" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": It seems that you have defeated my brother.");
			}, 240);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Storm" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": No worries, the party is just getting started!");
			}, 300);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				Wither wither = (Wither) damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.WITHER);
				new MasterStorm().onSpawn(Plugin.getNearestPlayer(damagee), wither);
			}, 340);
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
