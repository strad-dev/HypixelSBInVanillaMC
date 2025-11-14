package mobs.hardmode.withers;

import items.armor.WitherKingCrown;
import items.ingredients.witherLords.*;
import listeners.CustomMobs;
import listeners.DamageType;
import misc.DamageData;
import misc.Plugin;
import misc.Utils;
import mobs.withers.CustomWither;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftWither;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static misc.Utils.sendRareDropMessage;
import static misc.Utils.teleport;

public class WitherKing implements CustomWither {
	private static final String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + ChatColor.MAGIC + "Wither-King" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
	private static Mob witherKing;

	@Override
	public String onSpawn(Player p, Mob e) {
		Wither wither;
		if(e instanceof Wither) {
			wither = (Wither) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		witherKing = wither;
		List<EntityType> immune = new ArrayList<>();
		immune.add(EntityType.WITHER_SKELETON);
		Utils.spawnTNT(wither, wither.getLocation(), 0, 48, 100, immune);
		p = Utils.getNearestPlayer(wither);
		Utils.playGlobalSound(Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);

		wither.getAttribute(Attribute.SCALE).setBaseValue(2.0);
		wither.getAttribute(Attribute.MAX_HEALTH).setBaseValue(2000.0);
		wither.setHealth(2000.0);
		wither.addScoreboardTag("WitherKing");
		wither.addScoreboardTag("HardMode");
		wither.addScoreboardTag("SkyblockBoss");
		wither.addScoreboardTag("PowerUndefeated");
		wither.addScoreboardTag("FireUndefeated");
		wither.addScoreboardTag("IceUndefeated");
		wither.addScoreboardTag("SoulUndefeated");
		wither.addScoreboardTag("MartialUndefeated");
		wither.setPersistent(true);
		wither.setRemoveWhenFarAway(false);
		wither.setAI(false);
		wither.setSilent(true);
		wither.setCustomName(name + " " + ChatColor.RESET + ChatColor.RED + "❤" + ChatColor.YELLOW + " a");
		teleport(wither, 0);
		Utils.changeName(wither);

		ArrayList<String> ordering = new ArrayList<>();
		ordering.add("Power");
		ordering.add("Fire");
		ordering.add("Ice");
		ordering.add("Soul");
		ordering.add("Martial");
		Collections.shuffle(ordering);
		Utils.scheduleTask(() -> {
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": My henchmen are the best in the land.  They will defeat you swiftly!");
			spawnHenchman(wither, ordering.getFirst());
			spawnHenchman(wither, ordering.get(1));
		}, 40);
		Utils.scheduleTask(() -> {
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": One more to join the fray.  I hope you are having fun!");
			spawnHenchman(wither, ordering.get(2));
		}, 640);
		Utils.scheduleTask(() -> {
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Another one can't hurt, can it?");
			spawnHenchman(wither, ordering.get(3));
		}, 1240);
		Utils.scheduleTask(() -> {
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": My last Henchman.  Go forth and destroy the insolent players!");
			spawnHenchman(wither, ordering.get(4));
		}, 1840);

		spawnGuards(wither);
		boom(wither);

		return name;
	}

	public static Entity getEntity() {
		return witherKing;
	}

	private void spawnHenchman(Wither wither, String which) {
		WitherSkeleton witherSkeleton = (WitherSkeleton) wither.getWorld().spawnEntity(wither.getLocation(), EntityType.WITHER_SKELETON);
		Player p = Utils.getNearestPlayer(wither);

		ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
		sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 7);

		Objects.requireNonNull(witherSkeleton.getEquipment()).setItemInMainHand(sword);
		witherSkeleton.getEquipment().setItemInMainHandDropChance(0.0F);
		Objects.requireNonNull(witherSkeleton.getEquipment()).setItemInOffHand(sword);
		witherSkeleton.getEquipment().setItemInOffHandDropChance(0.0F);

		witherSkeleton.getAttribute(Attribute.MAX_HEALTH).setBaseValue(666.0);
		witherSkeleton.setHealth(666.0);
		Objects.requireNonNull(witherSkeleton.getAttribute(Attribute.SCALE)).setBaseValue(1.333);
		//noinspection DuplicatedCode
		Objects.requireNonNull(witherSkeleton.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(0.5);
		Objects.requireNonNull(witherSkeleton.getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER)).setBaseValue(0.0);
		witherSkeleton.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		witherSkeleton.setTarget(p);
		witherSkeleton.teleport(wither);
		witherSkeleton.setCustomNameVisible(true);
		witherSkeleton.addScoreboardTag("SkyblockBoss");
		witherSkeleton.addScoreboardTag("GuardSkeleton");
		witherSkeleton.addScoreboardTag("HardMode");
		witherSkeleton.setPersistent(true);
		witherSkeleton.setRemoveWhenFarAway(false);
		switch(which) {
			case "Power" -> {
				witherSkeleton.addScoreboardTag("Power");
				new WitherSkeletonPower().onSpawn(Utils.getNearestPlayer(wither), witherSkeleton);
			}
			case "Fire" -> {
				witherSkeleton.addScoreboardTag("Fire");
				new WitherSkeletonFire().onSpawn(Utils.getNearestPlayer(wither), witherSkeleton);
			}
			case "Ice" -> {
				witherSkeleton.addScoreboardTag("Ice");
				new WitherSkeletonIce().onSpawn(Utils.getNearestPlayer(wither), witherSkeleton);
			}
			case "Soul" -> {
				witherSkeleton.addScoreboardTag("Soul");
				new WitherSkeletonSoul().onSpawn(Utils.getNearestPlayer(wither), witherSkeleton);
			}
			case "Martial" -> {
				witherSkeleton.addScoreboardTag("Martial");
				new WitherSkeletonMartial().onSpawn(Utils.getNearestPlayer(wither), witherSkeleton);
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
		Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
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
			Player p = Utils.getNearestPlayer(mob);
			int health = 150 - countHenchmenLeft() * 10;
			for(int i = 0; i < 4 - countHenchmenLeft() / 2; i++) {
				WitherSkeleton e = (WitherSkeleton) mob.getWorld().spawnEntity(mob.getLocation(), EntityType.WITHER_SKELETON);
				e.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Wither Guard" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RED + "❤ " + ChatColor.YELLOW + health + "/" + health);
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
			Utils.playGlobalSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0F, 2.0F);

			Utils.scheduleTask(() -> spawnGuards(mob), 300 + countHenchmenLeft() * 20L);
		}
	}

	private void boom(Mob e) {
		if(!e.isDead() && !e.getScoreboardTags().contains("Dead")) {
			Utils.scheduleTask(() -> {
				if(!e.isDead() && !e.getScoreboardTags().contains("Dead")) {
					Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "2", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BOOM!", 0, 21, 0));
				}
			}, 360 + countHenchmenLeft() * 40L);
			Utils.scheduleTask(() -> {
				if(!e.isDead() && !e.getScoreboardTags().contains("Dead")) {
					Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "1", ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "BOOM!", 0, 21, 0));
				}
			}, 380 + countHenchmenLeft() * 40L);
			Utils.scheduleTask(() -> {
				if(!e.isDead() && !e.getScoreboardTags().contains("Dead")) {
					Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "BOOM!", "", 0, 21, 0));
					List<EntityType> immune = new ArrayList<>();
					immune.add(EntityType.WITHER_SKELETON);
					Utils.spawnTNT(e, e.getLocation(), 0, 48, 200 - countHenchmenLeft() * 20, immune);
					boom(e);
				}
			}, 400 + countHenchmenLeft() * 40L);
		}
	}


	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}

		CustomMobs.updateWitherLordFight(true);

		double hp = damagee.getHealth();
		double minHealth = countHenchmenLeft() * 400;

		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			Utils.changeName(damagee);
			return false;
		} else if(hp - originalDamage < minHealth && countHenchmenLeft() != 0) {
			if(hp == minHealth) {
				if(damager instanceof Player p) {
					p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "IMMUNE", ChatColor.YELLOW + "You cannot damage " + ChatColor.MAGIC + "Wither-King" + ChatColor.RESET + ChatColor.GREEN + "!", 0, 20, 0);
				}
				damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
			} else {
				damager.getWorld().playSound(damager, Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
			}
			damagee.setHealth(minHealth);
			Utils.changeName(damagee);
			return false;
		} else if(hp - originalDamage < 1) {
			damagee.addScoreboardTag("Invulnerable");
			damagee.addScoreboardTag("Dead");
			damagee.setHealth(1.0);
			damagee.setSilent(true);
			Utils.changeName(damagee);
			Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": You have defeated me...  Centuries of preparation down the drain...");
			Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Congratulations, you have proven yourself as a mighty warrior.");
			}, 80);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": My strength slips away... I can see the light at the end of the tunnel.");
			}, 160);
			Utils.scheduleTask(() -> {
				Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT, 1.0F, 0.667F);
				Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": Goodbye cruel world!  I hope to never see it again!");
			}, 240);
			Utils.scheduleTask(() -> {
				damagee.remove();
				Utils.playGlobalSound(Sound.ENTITY_WITHER_DEATH, 1.0F, 1.0F);
			}, 320);
			Utils.scheduleTask(() -> dropLoot(damagee, damager), 420);
			return false;
		}

		WitherBoss nmsWither = ((CraftWither) damagee).getHandle();
		nmsWither.bossEvent.setProgress((float) (witherKing.getHealth() / 2000));
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
			p = Utils.getNearestPlayer(damagee);
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

		p.sendMessage(ChatColor.GOLD + "You have defeated the Wither Lords.  Congratulations!");
		Utils.playGlobalSound(Sound.UI_TOAST_CHALLENGE_COMPLETE);
		Plugin.getAdvancementAPI().getAdvancement("skyblock:defeat_wither_lords").incrementProgression(p);
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {
	}
}