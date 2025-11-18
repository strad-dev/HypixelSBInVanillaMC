package mobs.hardmode.withers;

import listeners.CustomMobs;
import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import mobs.withers.CustomWither;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

import static listeners.CustomDamage.calculateFinalDamage;
import static misc.Utils.teleport;

public class Necron implements CustomWither {
	private static final String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Necron" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";

	@Override
	public String onSpawn(Player p, Mob e) {
		Wither wither;
		if(e instanceof Wither) {
			wither = (Wither) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		List<EntityType> immune = new ArrayList<>();
		immune.add(EntityType.WITHER_SKELETON);
		Utils.spawnTNT(wither, wither.getLocation(), 0, 32, 75, immune);
		p = Utils.getNearestPlayer(wither);
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);

		wither.getAttribute(Attribute.MAX_HEALTH).setBaseValue(1400.0);
		wither.setHealth(1400.0);
		wither.addScoreboardTag("Necron");
		wither.addScoreboardTag("HardMode");
		wither.addScoreboardTag("SkyblockBoss");
		wither.addScoreboardTag("1100Frenzy");
		wither.addScoreboardTag("300Frenzy");
		wither.setPersistent(true);
		wither.setRemoveWhenFarAway(false);
		wither.setCustomName(name + " " + ChatColor.RESET + ChatColor.RED + "❤" + ChatColor.YELLOW + " a");
		Utils.changeName(wither);

		return name;
	}

	private void frenzy(Wither wither, int which) {
		wither.addScoreboardTag("Invulnerable");
		wither.setAI(false);
		Bukkit.getOnlinePlayers().forEach(p -> p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0)));
		teleport(wither, 16);
		for(int i = 0; i < 161; i += 20) {
			int finalI = i;
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 0.5F);
				Utils.spawnTNT(wither, wither.getLocation(), 0, 8 + (finalI / 40), 32 + (finalI / 20) * 2, new ArrayList<>());
			}, i);
		}
		Utils.scheduleTask(() -> {
			wither.removeScoreboardTag("Invulnerable");
			wither.setAI(true);
		}, 161);

		if(which == 1100) {
			wither.removeScoreboardTag("1100Frenzy");
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": WITNESS MY RAW NUCLEAR POWER!");
			wither.setHealth(1100.0);
		} else {
			wither.removeScoreboardTag("300Frenzy");
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Sometimes when you have a problem, you just need to destroy it and start again!");
			wither.setHealth(300.0);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.LETHAL_ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		CustomMobs.updateWitherLordFight(true);

		double hp = damagee.getHealth();

		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			Utils.changeName(damagee);
			if(damager instanceof Player p && !damagee.getScoreboardTags().contains("Dead")) {
				p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "IMMUNE", ChatColor.YELLOW + "You cannot damage Necron!", 0, 20, 0);
			}
				damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
			return false;
		} else if(damagee.getScoreboardTags().contains("1100Frenzy") && hp - originalDamage < 1100) {
			frenzy((Wither) damagee, 1100);
			Utils.changeName(damagee);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			return false;
		} else if(damagee.getScoreboardTags().contains("300Frenzy") && hp - originalDamage < 300) {
			frenzy((Wither) damagee, 300);
			Utils.changeName(damagee);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			return false;
		} else if(hp - originalDamage < 1) {
			damagee.addScoreboardTag("Invulnerable");
			damagee.addScoreboardTag("Dead");
			damagee.setHealth(1.0);
			damagee.setSilent(true);
			damagee.setAI(false);
			Utils.changeName(damagee);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": You have destroyed us... but you have not destroyed our forefather.");
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": He is a very powerful being.  If you wish to defeat Him, tread carefully.");
			}, 60);
			Utils.scheduleTask(() -> {
				damagee.remove();
				Utils.playGlobalSound(Sound.ENTITY_WITHER_DEATH);
				Utils.spawnTNT(damagee, damagee.getLocation(), 0, 32, 75, new ArrayList<>());
			}, 100);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "Wither-King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Who dares wake me from my slumber?");
			}, 240);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "Wither-King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Foolish players!  You do not know who you are dealing with!");
			}, 320);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "Wither-King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I do not wish to fight, but you leave me no choice.");
			}, 400);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "Wither-King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Prepare to meet your ultimate demise.");
			}, 480);
			Utils.scheduleTask(() -> {
				Wither wither = (Wither) damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.WITHER);
				CustomMob.getMob("WitherKing", true).onSpawn(Utils.getNearestPlayer(damagee), wither);
			}, 540);
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		calculateFinalDamage(damagee, damager, 6, DamageType.RANGED);
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {

	}
}
