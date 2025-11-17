package mobs.hardmode.generic;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.BossBarManager;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Sadan implements CustomMob {
	private static final String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Sadan" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";

	@Override
	public String onSpawn(Player p, Mob e) {
		Zombie zombie;
		if(e instanceof Zombie) {
			zombie = (Zombie) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		sword.addEnchantment(Enchantment.KNOCKBACK, 2);

		EntityEquipment equipment = zombie.getEquipment();
		equipment.setItemInMainHand(sword);
		equipment.setItem(EquipmentSlot.HEAD, setArmorColor(new ItemStack(Material.LEATHER_HELMET)));
		equipment.setItem(EquipmentSlot.CHEST, setArmorColor(new ItemStack(Material.LEATHER_CHESTPLATE)));
		equipment.setItem(EquipmentSlot.LEGS, setArmorColor(new ItemStack(Material.LEATHER_LEGGINGS)));
		equipment.setItem(EquipmentSlot.FEET, setArmorColor(new ItemStack(Material.LEATHER_BOOTS)));
		equipment.setItemInMainHandDropChance(0);
		equipment.setHelmetDropChance(0);
		equipment.setChestplateDropChance(0);
		equipment.setLeggingsDropChance(0);
		equipment.setBootsDropChance(0);

		Location l = zombie.getLocation();
		l.setY(512.0);
		zombie.teleport(l);
		zombie.getAttribute(Attribute.ARMOR).setBaseValue(-7.0);
		zombie.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
		zombie.getAttribute(Attribute.SCALE).setBaseValue(6.0);
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		zombie.setTarget(Utils.getNearestPlayer(zombie));
		zombie.setCustomNameVisible(true);
		zombie.addScoreboardTag("SkyblockBoss");
		zombie.addScoreboardTag("DummySadan");
		zombie.addScoreboardTag("HardMode");
		zombie.addScoreboardTag("Invulnerable");
		zombie.setAdult();
		Bukkit.getLogger().info(p.getName() + " has summoned the M6 Bossfight.");
		zombie.setPersistent(true);
		zombie.setRemoveWhenFarAway(false);
		zombie.setAI(false);
		BossBarManager.createBossBar(zombie, BarColor.RED, BarStyle.SOLID);

		sendChatMessage("So you have decided to awaken my true form... now you wish to defy me?");
		Utils.scheduleTask(() -> sendChatMessage("The audacity!  I have been the ruler of this world for a hundred years!"), 60);
		Utils.scheduleTask(() -> sendChatMessage("I am the bridge between this realm and the world below!  You shall not pass!"), 120);
		Utils.scheduleTask(() -> {
			sendChatMessage("My Terracotta Army is using some of the finest souls: old warriors and wizards!");
			spawnSwarmMobs(zombie);
		}, 180);
		return name;
	}

	private static void spawnSwarmMobs(Zombie sadan) {
		List<Entity> players = sadan.getNearbyEntities(64, 64, 64).stream().filter(e -> e instanceof Player).toList();
		List<Husk> terracottas = new ArrayList<>();
		List<IronGolem> golems = new ArrayList<>();

		int terracottaCount = players.size() * 4 + 16;
		int golemCount = players.size() + 5;

		if(players.isEmpty()) {
			Location l = sadan.getLocation();
			for(int i = 0; i < 20; i++) {
				terracottas.add(spawnTerracotta(l));
			}

			for(int i = 0; i < 6; i++) {
				golems.add(spawnGolem(l));
			}
		} else {
			int index = 0;
			for(int i = 0; i < terracottaCount; i++) {
				Location l = Utils.randomLocation(players.get(index).getLocation(), 16);
				terracottas.add(spawnTerracotta(l));
				if(index + 1 == players.size()) {
					index = 0;
				} else {
					index++;
				}
			}
			index = 0;
			for(int i = 0; i < golemCount; i++) {
				Location l = Utils.randomLocation(players.get(index).getLocation(), 16);
				golems.add(spawnGolem(l));
				if(index + 1 == players.size()) {
					index = 0;
				} else {
					index++;
				}
			}
		}

		Utils.playGlobalSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0F, 2.0F);
		double health = terracottas.size() * 250 + golems.size() * 400;
		sadan.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
		sadan.setHealth(health);
		updateHealthSwarm(sadan, terracottas, golems);
	}

	private static Husk spawnTerracotta(Location l) {
		Husk terracotta = (Husk) l.getWorld().spawnEntity(l, EntityType.HUSK);

		terracotta.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Terracotta" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RED + "❤ " + ChatColor.YELLOW + "250/250");

		EntityEquipment equipment = terracotta.getEquipment();
		equipment.setItemInMainHand(new ItemStack(Material.POPPY));

		terracotta.getAttribute(Attribute.MAX_HEALTH).setBaseValue(250.0);
		terracotta.setHealth(250.0);
		terracotta.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(20.0);
		terracotta.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.5);
		terracotta.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		terracotta.setTarget(Utils.getNearestPlayer(terracotta));
		terracotta.setCustomNameVisible(true);
		terracotta.setAdult();
		terracotta.addScoreboardTag("SkyblockBoss");
		terracotta.addScoreboardTag("Terracotta");
		terracotta.addScoreboardTag("HardMode");
		terracotta.setPersistent(true);
		terracotta.setRemoveWhenFarAway(false);

		return terracotta;
	}

	private static IronGolem spawnGolem(Location l) {
		IronGolem ironGolem = (IronGolem) l.getWorld().spawnEntity(l, EntityType.IRON_GOLEM);

		ironGolem.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Woke Golem" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RED + "❤ " + ChatColor.YELLOW + "400/400");

		EntityEquipment equipment = ironGolem.getEquipment();
		equipment.setItemInMainHand(new ItemStack(Material.POPPY));

		ironGolem.getAttribute(Attribute.MAX_HEALTH).setBaseValue(400.0);
		ironGolem.setHealth(400.0);
		ironGolem.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(30.0);
		ironGolem.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.5);
		ironGolem.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		ironGolem.setTarget(Utils.getNearestPlayer(ironGolem));
		ironGolem.setCustomNameVisible(true);
		ironGolem.addScoreboardTag("SkyblockBoss");
		ironGolem.addScoreboardTag("WokeGolem");
		ironGolem.addScoreboardTag("HardMode");
		ironGolem.setPersistent(true);
		ironGolem.setRemoveWhenFarAway(false);

		return ironGolem;
	}

	private static void updateHealthSwarm(Zombie sadan, List<Husk> terracottas, List<IronGolem> golems) {
		if(terracottas.isEmpty() && golems.isEmpty()) {
			sendChatMessage("ENOUGH!");
			sadan.getAttribute(Attribute.MAX_HEALTH).setBaseValue(2400.0);
			sadan.setHealth(2400.0);
			Utils.scheduleTask(() -> sendChatMessage("My giants!  Unleashed!"), 60);
			Utils.scheduleTask(() -> spawnGiants(sadan), 120);
		} else {
			double currentHP = 0;
			for(Husk terracotta : terracottas) {
				if(terracotta.getHealth() <= 0 || terracotta.isDead()) {
					terracottas.remove(terracotta);
				} else {
					currentHP += terracotta.getHealth();
				}
			}
			for(IronGolem golem : golems) {
				if(golem.getHealth() <= 0 || golem.isDead()) {
					golems.remove(golem);
				} else {
					currentHP += golem.getHealth();
				}
			}
			sadan.setHealth(Math.max(1, currentHP));
			Utils.scheduleTask(() -> updateHealthSwarm(sadan, terracottas, golems), 1);
		}
	}

	private static void spawnGiants(Zombie sadan) {
		List<Zombie> giants = new ArrayList<>();

		for(int i = 0; i < 4; i++) {
			Zombie giant = spawnGiant(sadan);
			giants.add(giant);
			switch(i) {
				case 0 -> Utils.scheduleTask(() -> jollyPinkGiant(giant, false), 80);
				case 1 -> Utils.scheduleTask(() -> diamondGiant(giant, false), 160);
				case 2 -> Utils.scheduleTask(() -> bigfoot(giant, false), 240);
				case 3 -> Utils.scheduleTask(() -> lasr(giant, false), 320);
			}
		}
		updateHealthGiants(sadan, giants);
	}

	private static Zombie spawnGiant(Zombie sadan) {
		Zombie zombie = (Zombie) sadan.getWorld().spawnEntity(Utils.randomLocation(sadan.getLocation(), 16), EntityType.ZOMBIE);

		zombie.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Mutant Giant" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RED + "❤ " + ChatColor.YELLOW + "600/600");

		EntityEquipment equipment = zombie.getEquipment();
		equipment.setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
		equipment.setItem(EquipmentSlot.HEAD, setArmorColor(new ItemStack(Material.LEATHER_HELMET)));
		equipment.setItem(EquipmentSlot.CHEST, setArmorColor(new ItemStack(Material.LEATHER_CHESTPLATE)));
		equipment.setItem(EquipmentSlot.LEGS, setArmorColor(new ItemStack(Material.LEATHER_LEGGINGS)));
		equipment.setItem(EquipmentSlot.FEET, setArmorColor(new ItemStack(Material.LEATHER_BOOTS)));
		equipment.setItemInMainHandDropChance(0);
		equipment.setHelmetDropChance(0);
		equipment.setChestplateDropChance(0);
		equipment.setLeggingsDropChance(0);
		equipment.setBootsDropChance(0);

		zombie.getAttribute(Attribute.MAX_HEALTH).setBaseValue(600.0);
		zombie.setHealth(600.0);
		zombie.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(50.0);
		zombie.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.2);
		zombie.getAttribute(Attribute.ARMOR).setBaseValue(-7.0);
		zombie.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
		zombie.getAttribute(Attribute.SCALE).setBaseValue(6.0);
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		zombie.setTarget(Utils.getNearestPlayer(zombie));
		zombie.setCustomNameVisible(true);
		zombie.setAdult();
		zombie.addScoreboardTag("SkyblockBoss");
		zombie.addScoreboardTag("MutantGiant");
		zombie.addScoreboardTag("HardMode");
		zombie.setPersistent(true);
		zombie.setRemoveWhenFarAway(false);

		return zombie;
	}

	private static void jollyPinkGiant(Zombie giant, boolean finalPhase) {
		if(!giant.isDead()) {
			Player p = Utils.getNearestPlayer(giant);
			Block b = p.getLocation().getBlock();
			int x = b.getX() - (finalPhase ? 3 : 2);
			int y = b.getY() + 21;
			int z = b.getY() - (finalPhase ? 3 : 2);

			for(int i = x; i < x + (finalPhase ? 7 : 5); i++) {
				for(int j = z; j < z + (finalPhase ? 7 : 5); j++) {
					Block temp = p.getWorld().getBlockAt(i, y, j);
					if(temp.getType().equals(Material.AIR)) {
						temp.setType(Material.DAMAGED_ANVIL);
					}
					if(finalPhase) {
						temp = temp.getRelative(0, 21, 0);
						if(temp.getType().equals(Material.AIR)) {
							temp.setType(Material.DAMAGED_ANVIL);
						}
					}
				}
			}
			Utils.scheduleTask(() -> jollyPinkGiant(giant, finalPhase), finalPhase ? 160 : 320);
			p.playSound(p, Sound.BLOCK_ANVIL_PLACE, 1.0F, 1.0F);
		}
	}

	private static void diamondGiant(Zombie giant, boolean finalPhase) {
		if(!giant.isDead()) {
			Player p = Utils.getNearestPlayer(giant);
			p.playSound(p, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
			giant.swingMainHand();
			CustomDamage.customMobs(p, giant, finalPhase ? 150 : 100, DamageType.MELEE);
			Utils.scheduleTask(() -> diamondGiant(giant, finalPhase), finalPhase ? 160 : 320);
		}
	}

	private static void bigfoot(Zombie giant, boolean finalPhase) {
		if(!giant.isDead()) {
			giant.getNearbyEntities(64, 64, 64).stream().filter(e -> e instanceof Player).forEach(p -> {
				Location l = p.getLocation();
				l.setY(l.getY() - 1);
				p.teleport(l);
				CustomDamage.customMobs((LivingEntity) p, giant, finalPhase ? 80 : 50, DamageType.MELEE);
			});
			Utils.playGlobalSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 2.0F);
			Utils.scheduleTask(() -> bigfoot(giant, finalPhase), finalPhase ? 160 : 320);
		}
	}

	private static void lasr(Zombie giant, boolean finalPhase) {
		if(!giant.isDead()) {
			for(int i = 0; i < (finalPhase ? 40 : 80); i += (finalPhase ? 4 : 10)) {
				Utils.shootBeam(giant, Utils.getNearestPlayer(giant), Color.RED, 64, 1, finalPhase ? 10 : 8);
			}
			Utils.scheduleTask(() -> bigfoot(giant, finalPhase), finalPhase ? 160 : 320);
		}
	}

	private static void updateHealthGiants(Zombie sadan, List<Zombie> giants) {
		if(giants.isEmpty()) {
			sadan.getAttribute(Attribute.MAX_HEALTH).setBaseValue(1200.0);
			sadan.setHealth(1200.0);
			sadan.getAttribute(Attribute.ARMOR).setBaseValue(-20.0);
			sadan.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(-8.0);
			sadan.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(80.0);

			Location l = sadan.getLocation();
			int y = sadan.getWorld().getHighestBlockYAt(l);
			l.setY(y + 24);
			sadan.teleport(l);
			Utils.playGlobalSound(Sound.ENTITY_HORSE_ARMOR, 2.0F, 0.5F);
			for(int i = 10; i < 240; i += 10) {
				Utils.scheduleTask(() -> {
					l.setY(l.getY() - 1);
					sadan.teleport(l);
					Utils.playGlobalSound(Sound.ENTITY_HORSE_ARMOR, 2.0F, 0.5F);
				}, i);
			}

			sendChatMessage("You did it.  I understand you now, you have earned my respect.");
			Utils.scheduleTask(() -> sendChatMessage("If only you had become my disciples instead of this incompetent bunch."), 60);
			Utils.scheduleTask(() -> sendChatMessage("Maybe in a nother life.  Until then, meet my ultimate corpse."), 120);
			Utils.scheduleTask(() -> sendChatMessage("I'm sorry but I need to concentrate.  I wish it didn't have to come to this."), 180);
			Utils.scheduleTask(() -> {
				sadan.setAI(true);
				sadan.setTarget(Utils.getNearestPlayer(sadan));
				sadan.removeScoreboardTag("Invulnerable");
				sadan.removeScoreboardTag("DummySadan");
				sadan.addScoreboardTag("TheGiantOne");

				Utils.scheduleTask(() -> jollyPinkGiant(sadan, true), 40);
				Utils.scheduleTask(() -> diamondGiant(sadan, true), 80);
				Utils.scheduleTask(() -> bigfoot(sadan, true), 120);
				Utils.scheduleTask(() -> lasr(sadan, true), 160);
			}, 300);
		} else {
			double currentHP = 0;
			for(Zombie zombie : giants) {
				if(zombie.getHealth() <= 0 || zombie.isDead()) {
					giants.remove(zombie);
				} else {
					currentHP += zombie.getHealth();
				}
			}
			sadan.setHealth(Math.max(1, currentHP));
			Utils.scheduleTask(() -> updateHealthGiants(sadan, giants), 1);
		}
	}

	private static ItemStack setArmorColor(ItemStack helmet) {
		LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
		helmetMeta.setColor(Color.BLACK);
		helmet.setItemMeta(helmetMeta);
		return helmet;
	}

	private static void sendChatMessage(String message) {
		Bukkit.broadcastMessage(name + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": " + message);
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return !damagee.getScoreboardTags().contains("Invulnerable");
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}
