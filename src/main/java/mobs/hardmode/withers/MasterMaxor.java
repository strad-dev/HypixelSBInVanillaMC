package mobs.hardmode.withers;

import listeners.CustomMobs;
import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.withers.CustomWither;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MasterMaxor implements CustomWither {
	private static final String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";

	@Override
	public String onSpawn(Player p, Mob e) {
		//noinspection DuplicatedCode
		List<EntityType> immune = new ArrayList<>();
		immune.add(EntityType.WITHER_SKELETON);
		Utils.spawnTNT(e, e.getLocation(), 0, 32, 50, immune);
		Utils.playGlobalSound(Sound.ENTITY_WITHER_SPAWN);

		e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(800.0);
		e.setHealth(800.0);
		e.addScoreboardTag("Maxor");
		e.addScoreboardTag("HardMode");
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("600Crystal");
		e.addScoreboardTag("300Crystal");
		e.setPersistent(true);
		e.setRemoveWhenFarAway(false);
		e.setCustomName(name + " " + ChatColor.RESET + ChatColor.RED + "❤" + ChatColor.YELLOW + " a");
		Utils.changeName(e);

		Utils.scheduleTask(() -> spawnGuards(e), 300);

		return name;
	}

	private void spawnGuards(Mob e) {
		if(!e.isDead() && !e.getScoreboardTags().contains("Dead")) {
			Utils.spawnGuards(e, 2);
			Utils.scheduleTask(() -> spawnGuards(e), 300);
		}
	}

	private void spawnCrystal(LivingEntity wither, int which) {
		Location l = wither.getLocation();
		Random random = new Random();
		l.add(random.nextInt(32) - 16, 0, random.nextInt(32) - 16);
		for(int i = 319; i > -64; i--) {
			Block b = l.getWorld().getBlockAt((int) l.getX(), i, (int) l.getZ());
			if(b.getType() != Material.AIR && b.getType() != Material.VOID_AIR) {
				l.setY(i + 2);
				EnderCrystal crystal = (EnderCrystal) wither.getWorld().spawnEntity(l, EntityType.END_CRYSTAL);
				crystal.setCustomName(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Energy Crystal");
				crystal.addScoreboardTag("SkyblockBoss");
				if(which == 600) {
					wither.removeScoreboardTag("600Crystal");
					Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
					Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": HAHAHA!  GOOD LUCK GETTING AROUND MY TRICKS!");
					Utils.scheduleTask(() -> {
						if(wither.getScoreboardTags().contains("Invulnerable") && wither.getScoreboardTags().contains("300Crystal")) {
							Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": ARE YOU REALLY THIS BAD?!  CAN YOU NOT SEE EXPLOSIVES AROUND YOU?!");
						}
					}, 600L);
					wither.setHealth(600.0);
				} else {
					wither.removeScoreboardTag("300Crystal");
					Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
					Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": IF YOU FAIL ONCE, YOU SHOULD SIMPLY TRY AGAIN!");
					wither.setHealth(300.0);
				}
				wither.addScoreboardTag("Invulnerable");
				Utils.scheduleTask(() -> wither.addScoreboardTag("InvulnerableReminder"), 60L);
				Bukkit.broadcastMessage(ChatColor.YELLOW + "An Energy Crystal has spawned!  Maybe it is useful?");
				return;
			}
		}
		Bukkit.broadcastMessage(ChatColor.RED + "Oops!  Unable to summon a crystal!  Take it for free.");
		wither.removeScoreboardTag("600Crystal");
		wither.removeScoreboardTag("300Crystal");
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		CustomMobs.updateWitherLordFight(true);

		double hp = damagee.getHealth();

		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			if(damager instanceof Player p) {
				if(p.getScoreboardTags().contains("HasCrystal")) {
					damagee.removeScoreboardTag("Invulnerable");
					damagee.removeScoreboardTag("InvulnerableReminder");
					Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": OUCH!  HOW DID YOU FIGURE IT OUT???");
					List<EntityType> immune = new ArrayList<>();
					immune.add(EntityType.WITHER_SKELETON);
					Utils.spawnTNT(damagee, damagee.getLocation(), 0, 8, 10, immune);
					p.removeScoreboardTag("HasCrystal");
					Utils.changeName(damagee);
				} else {
					if(!damagee.getScoreboardTags().contains("Dead")) {
						damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
						p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "IMMUNE", ChatColor.YELLOW + "You cannot damage Maxor!", 0, 20, 0);
					}
				}
			}
			if(damagee.getScoreboardTags().contains("InvulnerableReminder") && !damagee.getScoreboardTags().contains("Dead")) {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": YOUR WEAK, PUNY ATTACKS CANNOT GET AROUND MY TRICKS!");
				damagee.removeScoreboardTag("InvulnerableReminder");
			}
			return false;
		} else if(damagee.getScoreboardTags().contains("600Crystal") && hp - originalDamage < 600) {
			spawnCrystal(damagee, 600);
			Utils.changeName(damagee);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			return false;
		} else if(damagee.getScoreboardTags().contains("300Crystal") && hp - originalDamage < 300) {
			spawnCrystal(damagee, 300);
			Utils.changeName(damagee);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			return false;
		} else if(hp - originalDamage < 1) {
			damagee.setAI(false);
			damagee.addScoreboardTag("Invulnerable");
			damagee.addScoreboardTag("Dead");
			damagee.setHealth(1.0);
			Utils.changeName(damagee);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": HOW DID YOU DEFEAT ME?!?!?!");
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": THIS IS ONLY THE BEGINNING!!!");
			}, 60);
			Utils.scheduleTask(() -> {
				damagee.remove();
				Utils.playGlobalSound(Sound.ENTITY_WITHER_DEATH);
				Utils.spawnTNT(damagee, damagee.getLocation(), 0, 32, 50, new ArrayList<>());
			}, 100);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Storm" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": It seems that you have defeated my brother.");
			}, 240);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Storm" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": No worries, the party is just getting started!");
			}, 300);
			Utils.scheduleTask(() -> {
				Wither wither = (Wither) damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.WITHER);
				new MasterStorm().onSpawn(Utils.getNearestPlayer(damagee), wither);
			}, 340);
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {

	}
}
