package mobs.hardmode.withers;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.Plugin;
import misc.PluginUtils;
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
import static listeners.CustomDamage.teleport;

public class MasterNecron implements CustomWither {
	private static final String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Necron" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";

	@Override
	public String onSpawn(Player p, Mob e) {
		List<EntityType> immune = new ArrayList<>();
		immune.add(EntityType.WITHER_SKELETON);
		PluginUtils.spawnTNT(e, e.getLocation(), 0, 32, 75, immune);
		p = Plugin.getNearestPlayer(e);
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);

		e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(500.0);
		e.setHealth(500.0);
		e.addScoreboardTag("Necron");
		e.addScoreboardTag("HardMode");
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("400Frenzy");
		e.addScoreboardTag("200Frenzy");
		e.setCustomName(name);
		PluginUtils.changeName(e);

		return name;
	}

	private void frenzy(LivingEntity wither, int which) {
		wither.addScoreboardTag("Invulnerable");
		wither.setAI(false);
		Bukkit.getOnlinePlayers().forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0)));
		teleport(wither, Plugin.getNearestPlayer(wither), 32);
		for(int i = 0; i < 161; i += 20) {
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				wither.getWorld().playSound(wither, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 0.5F);
				List<Entity> entities = (List<Entity>) wither.getWorld().getNearbyEntities(wither.getLocation(), 8, 8, 8);
				for(Entity temp : entities) {
					if(temp instanceof LivingEntity entity && !entity.getScoreboardTags().contains("Skyblock Boss")) {
						CustomDamage.calculateFinalDamage(entity, wither, 50, DamageType.PLAYER_MAGIC);
					}
				}
			}, i);
		}
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			wither.removeScoreboardTag("Invulnerable");
			wither.setAI(true);
		}, 161);

		if(which == 400) {
			wither.removeScoreboardTag("400Frenzy");
			wither.getWorld().playSound(wither, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": WITNESS MY RAW NUCLEAR POWER!");
			wither.setHealth(400.0);
		} else {
			wither.removeScoreboardTag("200Frenzy");
			wither.getWorld().playSound(wither, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Sometimes when you have a problem, you just need to destroy it and start again!");
			wither.setHealth(200.0);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		double hp = damagee.getHealth();

		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			return false;
		} else if(damagee.getScoreboardTags().contains("400Frenzy") && hp - originalDamage < 400) {
			frenzy(damagee, 400);
			return false;
		} else if(damagee.getScoreboardTags().contains("200Frenzy") && hp - originalDamage < 200) {
			frenzy(damagee, 200);
			return false;
		} else if(hp - originalDamage < 1) {
			damagee.setAI(false);
			damagee.addScoreboardTag("Invulnerable");
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": You have destroyed us... but you have not destroyed our forefathers.");
			Player p = Plugin.getNearestPlayer(damagee);
			p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": He is a very powerful being.  If you wish to defeat him, tread carefully.");
			}, 50);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				damagee.remove();
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0F, 1.0F);
				PluginUtils.spawnTNT(damagee, damagee.getLocation(), 0, 32, 75, new ArrayList<>());
			}, 100);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC +  "MASTER-Wither-King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Who dares wake me from my slumber?");
			}, 250);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "MASTER-Wither-King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Foolish players!  You do not know who you are dealing with!");
			}, 300);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "MASTER-Wither-King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I do not wish to fight, but you leave me no choice.");
			}, 350);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "MASTER-Wither-King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Prepare to meet your ultimate demise.");
			}, 400);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				Wither wither = (Wither) damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.WITHER);
				new MasterWitherKing().onSpawn(Plugin.getNearestPlayer(damagee), wither);
			}, 420);
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		calculateFinalDamage(damagee, damager, 6, DamageType.RANGED);
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {

	}
}
