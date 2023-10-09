package listeners;

import misc.Plugin;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class CustomMobs implements Listener {
	static Random random = new Random();

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {

		// MAXOR, STORM, GOLDOR, NECRON
		if(e.getEntity() instanceof Wither wither) {
			switch(random.nextInt(4)) {
				// MAXOR
				case 0 -> {
					wither.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
					wither.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 9));
					Objects.requireNonNull(Plugin.getNearestPlayer(e.getEntity())).sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "MAXOR, the fastest Wither in the universe, has come to destroy you 0.01 seconds faster than all other Withers!");
				}

				// STORM
				case 1 -> {
					wither.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Storm" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
					wither.getServer().dispatchCommand(Bukkit.getConsoleSender(), "weather thunder 1000000");
					List<Entity> entities = (List<Entity>) wither.getWorld().getNearbyEntities(wither.getLocation(), 64, 64, 64);
					for(Entity entity : entities) {
						if(entity instanceof LivingEntity entity1) {
							wither.getWorld().spawnEntity(entity1.getLocation(), EntityType.LIGHTNING);
							entity1.damage(10);
						}
					}
					Objects.requireNonNull(Plugin.getNearestPlayer(e.getEntity())).sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "STORM, most explosive Wither in the universe, has come to smite you with his Lightning!");
				}

				// GOLDOR
				case 2 -> {
					wither.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Goldor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
					wither.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, -1, 1));
					Objects.requireNonNull(Plugin.getNearestPlayer(e.getEntity())).sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "GOLDOR, the most defensive Wither in the universe, has come to stand in your way!");
				}

				// NECRON
				case 3 -> {
					wither.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Necron" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
					Objects.requireNonNull(Plugin.getNearestPlayer(e.getEntity())).sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "NECRON, the strongest Wither in the universe, has come to wipe you straight off the planet!");
				}
			}
			wither.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
			wither.setTarget(Plugin.getNearestPlayer(wither));
			wither.setCustomNameVisible(true);

			// SUPERIOR DRAGON
		} else if(e.getEntity() instanceof EnderDragon dragon) {
			if(random.nextDouble() > 0.875) {
				dragon.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Superior Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
				dragon.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, -1, 0));
				dragon.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
				dragon.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, -1, 0));
				dragon.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, -1, 0));
				dragon.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 0));
				dragon.setTarget(Plugin.getNearestPlayer(dragon));
				dragon.setCustomNameVisible(true);
				Objects.requireNonNull(Plugin.getNearestPlayer(e.getEntity())).sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The SUPERIOR DRAGON has arrived to test your strength!");
			}

			// MUTANT ENDERMAN
		} else if(e.getEntity() instanceof Enderman enderman) {
			double chance;
			if(e.getEntity().getWorld().getEnvironment().equals(World.Environment.THE_END)) {
				chance = 0.995;
			} else {
				chance = 0.96;
			}
			if(random.nextDouble() > chance && enderman.getCustomName() == null) {
				enderman.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Mutant Enderman" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
				enderman.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, -1, 1));
				enderman.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
				enderman.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, -1, 1));
				enderman.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 4));
				teleportToPlayer(enderman);
				Objects.requireNonNull(Plugin.getNearestPlayer(e.getEntity())).sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "A MUTANT ENDERMAN has spawned nearby!  It seems to be a little more agitated than it's friends...");
				enderman.setTarget(Plugin.getNearestPlayer(enderman));
				enderman.setCustomNameVisible(true);
			}
			// NULL GOLEM
		} else if(e.getEntity() instanceof IronGolem golem) {
			golem.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
			if(random.nextDouble() > 0.98) {
				golem.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "meloG norI" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
				golem.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, -1, 24));
				golem.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5, 255));
				golem.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
				golem.setTarget(Plugin.getNearestPlayer(golem));
				golem.setHealth(200.0);
				golem.setCustomNameVisible(true);
				teleportToPlayer(golem);
				Objects.requireNonNull(Plugin.getNearestPlayer(e.getEntity())).sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Generating masses of Iron of thin air has broken the Laws of Physics!  An MELOG NORI has spawned as a result!");
			}

			// CHICKZILLA
		} else if(e.getEntity() instanceof Chicken chicken) {
			if(random.nextDouble() > 0.98) {
				Map<Enchantment, Integer> enchantments = new HashMap<>();
				enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
				enchantments.put(Enchantment.THORNS, 25);

				ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
				helmet.addUnsafeEnchantments(enchantments);

				ItemStack chestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
				chestplate.addUnsafeEnchantments(enchantments);

				ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
				leggings.addUnsafeEnchantments(enchantments);

				ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
				boots.addUnsafeEnchantments(enchantments);

				Objects.requireNonNull(chicken.getEquipment()).setArmorContents(new ItemStack[]{helmet, chestplate, leggings, boots});

				chicken.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Chickzilla" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
				chicken.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, -1, 3));
				chicken.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
				chicken.setTarget(Plugin.getNearestPlayer(chicken));
				chicken.setCustomNameVisible(true);
				teleportToPlayer(chicken);
				Objects.requireNonNull(Plugin.getNearestPlayer(e.getEntity())).sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The death of harmless animals has drawn the WRATH OF CHICKZILLA!");
			}

			// TARANTULA BROODFATHER
		} else if(e.getEntity() instanceof Spider spider) {
			if(random.nextDouble() > 0.97) {
				spider.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Tarantula Broodfather" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
				spider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 14));
				spider.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
				spider.setTarget(Plugin.getNearestPlayer(spider));
				spider.setCustomNameVisible(true);
				teleportToPlayer(spider);
				Objects.requireNonNull(Plugin.getNearestPlayer(e.getEntity())).sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "As if Spiders weren't scary enough, the TARANTULA BROODFATHER has emerged from the depths, ready to snack on a nice juicy meal!");
			}
		}
	}

	public static void teleportToPlayer(Entity e) {
		Location l;
		Location l2;
		Player p = Plugin.getNearestPlayer(e);
		try {
			assert p != null;
			l = p.getLocation();
			l2 = p.getLocation();
		} catch(Exception exception) {
			return;
		}
		org.bukkit.util.Vector added = new Vector(random.nextInt(33) - 16, 0, random.nextInt(33) - 16);
		l.add(added);
		l2.add(added);
		while(true) {
			if(l.getY() > 319 || l2.getY() > -63) {
				break;
			} else if(l.getBlock().isEmpty()) {
				e.teleport(l);
				break;
			} else if(l2.getBlock().isEmpty()) {
				e.teleport(l2);
				break;
			}
			l.add(0, 1, 0);
			l2.add(0, -1, 0);
		}
	}
}