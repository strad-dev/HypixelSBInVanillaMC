package mobs.hardmode.withers;

import listeners.CustomMobs;
import listeners.DamageType;
import misc.DamageData;
import misc.Plugin;
import misc.Utils;
import mobs.withers.CustomWither;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;

public class MasterGoldor implements CustomWither {
	private static final String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Goldor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";

	@Override
	public String onSpawn(Player p, Mob e) {
		//noinspection DuplicatedCode
		List<EntityType> immune = new ArrayList<>();
		immune.add(EntityType.WITHER_SKELETON);
		Utils.spawnTNT(e, e.getLocation(), 0, 32, 50, immune);
		Utils.playGlobalSound(Sound.ENTITY_WITHER_SPAWN);

		e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(1200.0);
		e.setHealth(1200.0);
		e.addScoreboardTag("Goldor");
		e.addScoreboardTag("HardMode");
		e.addScoreboardTag("SkyblockBoss");
		e.setPersistent(true);
		e.setRemoveWhenFarAway(false);
		e.setCustomName(name + " " + ChatColor.RESET + ChatColor.RED + "❤" + ChatColor.YELLOW + " a");
		Utils.changeName(e);

		damageAll(e);

		return name;
	}

	private void damageAll(Mob e) {
		if(!e.isDead() && !e.getScoreboardTags().contains("Dead")) {
			Utils.spawnTNT(e, e.getLocation(), 0, 6, Math.min(25, 5 + (int) Math.floor((1200.0 - e.getHealth()) / 50.0)), new ArrayList<>());
			Utils.scheduleTask(() -> damageAll(e), 20);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		CustomMobs.updateWitherLordFight(true);

		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			Utils.changeName(damagee);
			return false;
		} else if(damagee.getHealth() - originalDamage < 1) {
			damagee.setAI(false);
			damagee.addScoreboardTag("Invulnerable");
			damagee.addScoreboardTag("Dead");
			damagee.setHealth(1.0);
			Utils.changeName(damagee);
			damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": How did you break through my shield???");
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Either way I don't think you're getting past Necron.");
			}, 60);
			Utils.scheduleTask(() -> {
				damagee.remove();
				Utils.playGlobalSound(Sound.ENTITY_WITHER_DEATH);
				Utils.spawnTNT(damagee, damagee.getLocation(), 0, 32, 50, new ArrayList<>());
			}, 100);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Necron" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I have heard a lot about you.");
			}, 240);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Necron" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Your perseverence is immeasurable, but your journey ends here.  Goodbye.");
			}, 300);
			Utils.scheduleTask(() -> {
				Wither wither = (Wither) damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.WITHER);
				new MasterNecron().onSpawn(Utils.getNearestPlayer(damagee), wither);
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
