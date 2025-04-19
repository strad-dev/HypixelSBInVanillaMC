package mobs.hardmode.withers;

import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import items.armor.WitherKingCrown;
import items.ingredients.witherLords.*;
import listeners.CustomMobs;
import listeners.DamageType;
import misc.Plugin;
import misc.PluginUtils;
import mobs.withers.CustomWither;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static misc.PluginUtils.sendRareDropMessage;
import static misc.PluginUtils.teleport;

public class MasterWitherKing implements CustomWither {
	private static final String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "Wither-King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
	private static Mob witherKing;

	@Override
	public String onSpawn(Player p, Mob e) {
		witherKing = e;
		List<EntityType> immune = new ArrayList<>();
		immune.add(EntityType.WITHER_SKELETON);
		PluginUtils.spawnTNT(e, e.getLocation(), 0, 48, 100, immune);
		p = PluginUtils.getNearestPlayer(e);
		PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);

		e.getAttribute(Attribute.SCALE).setBaseValue(2.0);
		e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(2000.0);
		e.setHealth(2000.0);
		e.addScoreboardTag("WitherKing");
		e.addScoreboardTag("HardMode");
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("PowerUndefeated");
		e.addScoreboardTag("FireUndefeated");
		e.addScoreboardTag("IceUndefeated");
		e.addScoreboardTag("SoulUndefeated");
		e.addScoreboardTag("MartialUndefeated");
		e.setPersistent(true);
		e.setRemoveWhenFarAway(false);
		e.setAI(false);
		e.setCustomName(name + " " + ChatColor.RESET + ChatColor.RED + "❤" + ChatColor.YELLOW + " a");
		teleport(e, 0);
		PluginUtils.changeName(e);

		ArrayList<String> ordering = new ArrayList<>();
		ordering.add("Power");
		ordering.add("Fire");
		ordering.add("Ice");
		ordering.add("Soul");
		ordering.add("Martial");
		Collections.shuffle(ordering);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": My henchmen are the best in the land.  They will defeat you swiftly!");
			spawnHenchman(e, ordering.getFirst());
			spawnHenchman(e, ordering.get(1));
		}, 40);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": One more to join the fray.  I hope you are having fun!");
			spawnHenchman(e, ordering.get(2));
		}, 640);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Another one can't hurt, can it?");
			spawnHenchman(e, ordering.get(3));
		}, 1240);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": My last Henchman.  Go forth and destroy the insolent players!");
			spawnHenchman(e, ordering.get(4));
		}, 1840);

		spawnGuards(e);
		boom(e);

		return name;
	}

	public static Entity getEntity() {
		return witherKing;
	}

	private void spawnHenchman(Mob mob, String which) {
		WitherSkeleton e = (WitherSkeleton) mob.getWorld().spawnEntity(mob.getLocation(), EntityType.WITHER_SKELETON);
		Player p = PluginUtils.getNearestPlayer(mob);

		ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
		sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 7);

		Objects.requireNonNull(e.getEquipment()).setItemInMainHand(sword);
		e.getEquipment().setItemInMainHandDropChance(0.0F);
		Objects.requireNonNull(e.getEquipment()).setItemInOffHand(sword);
		e.getEquipment().setItemInOffHandDropChance(0.0F);

		e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(666.0);
		e.setHealth(666.0);
		Objects.requireNonNull(e.getAttribute(Attribute.SCALE)).setBaseValue(1.333);
		//noinspection DuplicatedCode
		Objects.requireNonNull(e.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(0.5);
		Objects.requireNonNull(e.getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER)).setBaseValue(0.0);
		e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		e.setTarget(p);
		e.teleport(mob);
		e.setCustomNameVisible(true);
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("GuardSkeleton");
		e.addScoreboardTag("HardMode");
		e.setPersistent(true);
		e.setRemoveWhenFarAway(false);
		switch(which) {
			case "Power" -> {
				e.addScoreboardTag("Power");
				new PowerWitherSkeleton().onSpawn(PluginUtils.getNearestPlayer(mob), e);
			}
			case "Fire" -> {
				e.addScoreboardTag("Fire");
				new FireWitherSkeleton().onSpawn(PluginUtils.getNearestPlayer(mob), e);
			}
			case "Ice" -> {
				e.addScoreboardTag("Ice");
				new IceWitherSkeleton().onSpawn(PluginUtils.getNearestPlayer(mob), e);
			}
			case "Soul" -> {
				e.addScoreboardTag("Soul");
				new SoulWitherSkeleton().onSpawn(PluginUtils.getNearestPlayer(mob), e);
			}
			case "Martial" -> {
				e.addScoreboardTag("Martial");
				new MartialWitherSkeleton().onSpawn(PluginUtils.getNearestPlayer(mob), e);
			}
		}
	}

	private static int countHenchmenLeft() {
		int count = 0;
		Set<String> set = witherKing.getScoreboardTags();
		if(set.contains("PowerUndefeated")) {
			count++;
		}
		if(set.contains("FireUndefeated")) {
			count++;
		}
		if(set.contains("IceUndefeated")) {
			count++;
		}
		if(set.contains("SoulUndefeated")) {
			count++;
		}
		if(set.contains("MartialUndefeated")) {
			count++;
		}
		return count;
	}

	public static void defeatHenchman(String which) {
		witherKing.removeScoreboardTag(which + "Undefeated");
		int left = countHenchmenLeft();
		PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
		switch(left) {
			case 4 ->
					Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": My most loyal henchman, what have they done to you?");
			case 3 ->
					Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": It seems my henchmen are not as powerful as I thought they were.  I suppose i must help them out.");
			case 2 ->
					Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Are you this heartless?  Murdering my defenseless followers for no good reason.");
			case 1 ->
					Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": You are getting on my nerves.  Quit being annoying!");
			case 0 ->
					Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": My energy is waning...  I must use my last hurrah.");
		}
	}

	private void spawnGuards(Mob mob) {
		if(!mob.isDead() && !(mob.getScoreboardTags().contains("Dead"))) {
			Player p = PluginUtils.getNearestPlayer(mob);
			int health = 150 - countHenchmenLeft() * 10;
			for(int i = 0; i < 4 - countHenchmenLeft() / 2; i++) {
				WitherSkeleton e = (WitherSkeleton) mob.getWorld().spawnEntity(mob.getLocation(), EntityType.WITHER_SKELETON);
				e.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Wither Guard" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RED + "❤ " + ChatColor.YELLOW + health + "/" + health);
				ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
				sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 5);
				ItemStack shield = new ItemStack(Material.SHIELD);

				Objects.requireNonNull(e.getEquipment()).setItemInMainHand(sword);
				e.getEquipment().setItemInMainHandDropChance(0.0F);
				e.getEquipment().setItemInOffHand(shield);
				e.getEquipment().setItemInOffHandDropChance(0.0F);

				e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
				e.setHealth(health);
				//noinspection DuplicatedCode
				Objects.requireNonNull(e.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(0.5);
				Objects.requireNonNull(e.getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER)).setBaseValue(0.0);
				e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
				e.setTarget(p);
				e.setCustomNameVisible(true);
				e.addScoreboardTag("SkyblockBoss");
				e.addScoreboardTag("GuardSkeleton");
				e.addScoreboardTag("HardMode");
				e.setPersistent(true);
				e.setRemoveWhenFarAway(false);
			}
			PluginUtils.playGlobalSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0F, 2.0F);

			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> spawnGuards(mob), 300 + countHenchmenLeft() * 20L);
		}
	}

	private void boom(Mob e) {
		if(!e.isDead() && !e.getScoreboardTags().contains("Dead")) {
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				if(!e.isDead() && !e.getScoreboardTags().contains("Dead")) {
					Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "2", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BOOM!", 0, 21, 0));
				}
			}, 360 + countHenchmenLeft() * 40L);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				if(!e.isDead() && !e.getScoreboardTags().contains("Dead")) {
					Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "1", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BOOM!", 0, 21, 0));
				}
			}, 380 + countHenchmenLeft() * 40L);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				if(!e.isDead() && !e.getScoreboardTags().contains("Dead")) {
					Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "BOOM!", "", 0, 21, 0));
					List<EntityType> immune = new ArrayList<>();
					immune.add(EntityType.WITHER_SKELETON);
					PluginUtils.spawnTNT(e, e.getLocation(), 0, 48, 200 - countHenchmenLeft() * 20, immune);
					boom(e);
				}
			}, 400 + countHenchmenLeft() * 40L);
		}
	}


	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		CustomMobs.updateWitherLordFight(true);

		double hp = damagee.getHealth();
		double minHealth = countHenchmenLeft() * 400;

		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			PluginUtils.changeName(damagee);
			return false;
		} else if(hp - originalDamage < minHealth && countHenchmenLeft() != 0) {
			if(hp == minHealth) {
				damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
				if(damager instanceof Player p) {
					p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "IMMUNE", ChatColor.YELLOW + "You cannot damage " + ChatColor.MAGIC + "Wither-King" + ChatColor.RESET + ChatColor.GREEN + "!", 0, 20, 0);
				}
			} else {
				damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			}
			damagee.setHealth(minHealth);
			PluginUtils.changeName(damagee);
			return false;
		} else if(hp - originalDamage < 1) {
			damagee.addScoreboardTag("Invulnerable");
			damagee.addScoreboardTag("Dead");
			damagee.setHealth(1.0);
			PluginUtils.changeName(damagee);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": You have defeated me...  Centuries of preparation down the drain...");
			PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Congratulations, you have proven yourself as a mighty warrior.");
			}, 80);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": My strength slips away... I can see the light at the end of the tunnel.");
			}, 160);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Goodbye cruel world!  I hope to never see it again!");
			}, 240);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				damagee.remove();
				PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_DEATH, 1.0F, 1.0F);
			}, 320);
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> dropLoot(damagee, damager), 420);
			return false;
		}
		return true;
	}

	private void dropLoot(LivingEntity damagee, Entity damager) {
		ItemStack item;
		World world = damagee.getWorld();
		Location l = damagee.getLocation();
		Player p;
		if(damager instanceof Player p1) {
			p = p1;
		} else {
			p = PluginUtils.getNearestPlayer(damagee);
		}
		item = MaxorSecrets.getItem();
		world.dropItemNaturally(l, item);
		sendRareDropMessage(p, "Maxor's Secrets");
		item = ShadowWarp.getItem();
		world.dropItemNaturally(l, item);
		sendRareDropMessage(p, "Shadow Warp");
		item = StormSecrets.getItem();
		world.dropItemNaturally(l, item);
		sendRareDropMessage(p, "Storm's Secrets");
		item = Implosion.getItem();
		world.dropItemNaturally(l, item);
		sendRareDropMessage(p, "Implosion");
		item = GoldorSecrets.getItem();
		world.dropItemNaturally(l, item);
		sendRareDropMessage(p, "Goldor's Secrets");
		item = WitherShield.getItem();
		world.dropItemNaturally(l, item);
		sendRareDropMessage(p, "Wither Shield");
		item = NecronSecrets.getItem();
		world.dropItemNaturally(l, item);
		sendRareDropMessage(p, "Necron's Secrets");
		item = Handle.getItem();
		world.dropItemNaturally(l, item);
		sendRareDropMessage(p, "Necron's Handle");
		item = WitherKingCrown.getItem();
		world.dropItemNaturally(l, item);
		sendRareDropMessage(p, "Crown of the Wither King");

		CustomMobs.updateWitherLordFight(false);

		PluginUtils.playGlobalSound(Sound.UI_TOAST_CHALLENGE_COMPLETE);
		UltimateAdvancementAPI.getInstance(Plugin.getInstance()).getAdvancement("skyblock:defeat_wither_lords").revoke(p);
		UltimateAdvancementAPI.getInstance(Plugin.getInstance()).getAdvancement("skyblock:defeat_wither_lords").grant(p);
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {
	}
}