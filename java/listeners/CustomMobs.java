package listeners;

import misc.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CustomMobs implements Listener {
	static Random random = new Random();

	public static void spawnLightning(Entity entity) {
		List<Entity> entities = (List<Entity>) entity.getWorld().getNearbyEntities(entity.getLocation(), 64, 64, 64);
		for(Entity temp : entities) {
			if(temp instanceof LivingEntity entity1) {
				entity.getWorld().spawnEntity(entity1.getLocation(), EntityType.LIGHTNING_BOLT);
				entity1.damage(10);
			}
		}
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		if(e.getEntity() instanceof LivingEntity entity) {
			String name = "";

			// MAXOR, STORM, GOLDOR, NECRON
			try {
				switch(entity) {
					case Wither wither -> {
						switch(random.nextInt(4)) {
							// MAXOR
							case 0 -> {
								name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
								Objects.requireNonNull(wither.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(2.0);
								Objects.requireNonNull(wither.getAttribute(Attribute.GENERIC_FLYING_SPEED)).setBaseValue(2.0);
								Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "MAXOR, the fastest Wither in the universe, has come to destroy you 0.01 seconds faster than all other Withers!");
							}

							// STORM
							case 1 -> {
								name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Storm" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
								wither.getServer().dispatchCommand(Bukkit.getConsoleSender(), "weather thunder 1000000");
								spawnLightning(wither);
								Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "STORM, most explosive Wither in the universe, has come to smite you with his Lightning!");
							}

							// GOLDOR
							case 2 -> {
								name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Goldor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
								Objects.requireNonNull(wither.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(12.5);
								Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "GOLDOR, the most defensive Wither in the universe, has come to stand in your way!");
							}

							// NECRON
							case 3 -> {
								name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Necron" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
								Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "NECRON, the strongest Wither in the universe, has come to wipe you straight off the planet!");
							}
						}
						wither.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
						wither.setTarget(Plugin.getNearestPlayer(wither));
						wither.setCustomNameVisible(true);
						wither.addScoreboardTag("SkyblockBoss");
					}
					case EnderDragon dragon -> {
						Objects.requireNonNull(dragon.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(1.0);

						// SUPERIOR DRAGON
						if(random.nextDouble() < 0.04) {
							name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Superior Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
							Objects.requireNonNull(dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(300.0);
							dragon.setHealth(300.0);
							Objects.requireNonNull(dragon.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(6.25);
							Objects.requireNonNull(dragon.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.875);
							dragon.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, -1, 0));
							Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The SUPERIOR DRAGON has arrived to utterly destroy you!");
						} else {
							int pick = random.nextInt(7);
							switch(pick) {
								// UNSTABLE DRAGON
								case 0 -> {
									name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Unstable Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
									spawnLightning(dragon);
									Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The UNSTABLE DRAGON has arrived to cause chaos!");
								}
								case 1 -> {
									name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Strong Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
									Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The STRONG DRAGON has arrived to pulverize you!");
								}
								case 2 -> {
									name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Holy Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
									dragon.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, -1, 0));
									Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The HOLY DRAGON has arrived to carry out Strad's bidding!");
								}
								case 3 -> {
									name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Protector Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
									Objects.requireNonNull(dragon.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(12.5);
									Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The PROTECTOR DRAGON has arrived to protect the End from Nons!");
								}
								case 4 -> {
									name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Young Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
									Objects.requireNonNull(dragon.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(2.00);
									Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The YOUNG DRAGON has arrived to practise yeeting Nons into the Void!");
								}
								case 5 -> {
									name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Old Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
									Objects.requireNonNull(dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(400.0);
									dragon.setHealth(400.0);
									Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The OLD DRAGON has arrived for one last battle!");
								}
								case 6 -> {
									name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Wise Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
									Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The WISE DRAGON has arrived to destroy you using smart tactics!");
								}
							}
							dragon.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
							dragon.setTarget(Plugin.getNearestPlayer(dragon));
							dragon.setCustomNameVisible(true);
							dragon.addScoreboardTag("SkyblockBoss");
						}
					}
					case Drowned drowned -> {
						EntityEquipment equipment = drowned.getEquipment();
						assert equipment != null;
						equipment.setItemInMainHand(new ItemStack(Material.AIR));
						equipment.setItemInOffHand(new ItemStack(Material.AIR));
						equipment.setItemInMainHandDropChance(0.0F);
						equipment.setItemInOffHandDropChance(0.0F);
					}
					default -> {
					}
				}
			} catch(Exception exception) {
				// do nothing
			}

			// add health to the entity name if it doesn't exist already
			int health = (int) (entity.getHealth() + entity.getAbsorptionAmount());
			int maxHealth = (int) Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
			if(name.isEmpty()) {
				name = ChatColor.AQUA + entity.getName();
			}
			if(!name.contains("❤")) {
				name += " " + ChatColor.RED + "❤ " + ChatColor.YELLOW + health + "/" + maxHealth;
			}
			// " ♥ 20/20";
			entity.setCustomName(name);
			entity.setCustomNameVisible(true);
		}
	}
}