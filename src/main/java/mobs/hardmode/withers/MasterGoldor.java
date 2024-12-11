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

import java.util.ArrayList;
import java.util.List;

public class MasterGoldor implements CustomWither {
	private static final String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Goldor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";

	@Override
	public String onSpawn(Player p, Mob e) {
		List<EntityType> immune = new ArrayList<>();
		immune.add(EntityType.WITHER_SKELETON);
		PluginUtils.spawnTNT(e, e.getLocation(), 0, 32, 50, immune);
		p = Plugin.getNearestPlayer(e);
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);

		e.getAttribute(Attribute.ARMOR).setBaseValue(20.0);
		e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(500.0);
		e.setHealth(500.0);
		e.addScoreboardTag("Goldor");
		e.addScoreboardTag("HardMode");
		e.addScoreboardTag("SkyblockBoss");
		e.setPersistent(true);
		e.setCustomName(name);
		PluginUtils.changeName(e);

		damageAll(e);

		return name;
	}

	private void damageAll(Mob e) {
		if(!e.isDead()) {
			double bonus = (500 - e.getHealth()) / 50.0;
			List<Entity> entities = (List<Entity>) e.getWorld().getNearbyEntities(e.getLocation(), 4, 8, 4);
			for(Entity temp : entities) {
				if(temp instanceof Player p) {
					CustomDamage.calculateFinalDamage(p, e, 4 + bonus, DamageType.PLAYER_MAGIC);
				}
			}
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> damageAll(e), 20);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}
		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			return false;
		} else if(damagee.getHealth() - (originalDamage / 4) < 1) {
			damagee.setAI(false);
			damagee.addScoreboardTag("Invulnerable");
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": How did you break through my shield???");
			Player p = Plugin.getNearestPlayer(damagee);
			p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Anyway I don't think you're surviving what's coming next.");
			}, 50);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				damagee.remove();
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0F, 1.0F);
				PluginUtils.spawnTNT(damagee, damagee.getLocation(), 0, 32, 50, new ArrayList<>());
			}, 100);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Necron" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I have heard a lot about you.");
			}, 250);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				p.getWorld().playSound(p, Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Necron" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Your perseverence is immeasurable, but your journey ends here.  Goodbye.");
			}, 300);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				Wither wither = (Wither) damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.WITHER);
				new MasterNecron().onSpawn(Plugin.getNearestPlayer(damagee), wither);
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
