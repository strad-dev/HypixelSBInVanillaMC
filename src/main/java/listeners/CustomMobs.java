package listeners;

import misc.Plugin;
import misc.PluginUtils;
import mobs.enderDragons.CustomDragon;
import mobs.hardmode.withers.MasterMaxor;
import mobs.withers.CustomWither;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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

public class CustomMobs implements Listener {
	private static boolean isWitherLordFightActive = false;

	public static void updateWitherLordFight(boolean isWitherLordFightActive) {
		CustomMobs.isWitherLordFightActive = isWitherLordFightActive;
	}

	/**
	 * Summons lightning on every entity in a given radius
	 *
	 * @param entity the entity at the center
	 * @param radius the radius to spawn lightning on
	 */
	public static void spawnLightning(Entity entity, int radius) {
		List<Entity> entities = (List<Entity>) entity.getWorld().getNearbyEntities(entity.getLocation(), radius, 320, radius);
		for(Entity temp : entities) {
			if(temp instanceof LivingEntity entity1) {
				entity.getWorld().spawnEntity(entity1.getLocation(), EntityType.LIGHTNING_BOLT);
			}
		}
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		Player p = PluginUtils.getNearestPlayer(e.getEntity());
		boolean hardMode = false;
		if(p != null) {
			// Hard Mode will apply if any player in a 64-block radius of the mob has the effect
			hardMode = p.hasPotionEffect(PotionEffectType.BAD_OMEN);
			if(!hardMode) {
				for(Player p2 : e.getEntity().getWorld().getPlayers()) {
					if(p2.getLocation().distanceSquared(e.getLocation()) <= 4096 && p2.hasPotionEffect(PotionEffectType.BAD_OMEN)) {
						hardMode = true;
						break;
					}
				}
			}
		}
		if(e.getEntity() instanceof LivingEntity entity) {
			String name = "";

			// MAXOR, STORM, GOLDOR, NECRON
			try {
				switch(entity) {
					case Wither wither -> {
						wither.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
						if(!isWitherLordFightActive) {
							if(hardMode) {
								Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), wither::remove, 1);
								Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
									Wither wither2 = (Wither) wither.getWorld().spawnEntity(wither.getLocation(), EntityType.WITHER);
									new MasterMaxor().onSpawn(PluginUtils.getNearestPlayer(wither2), wither2);
								}, 240);
								isWitherLordFightActive = true;
								Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
									PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
									Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": WELL WELL WELL LOOK WHO'S BACK FOR A REMATCH!");
								}, 20);
								Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
									PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
									Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I HAVE BEEN PRACTISING 40 HOURS A DAY SINCE WE LAST MET!");
								}, 80);
								Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
									PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
									Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": MY TRICKS ARE MORE SOPHISTICATED THAN EVER; YOU WILL NEVER GET AROUND THEM!");
								}, 140);
								Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
									PluginUtils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
									Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": NOW LET'S HAVE SOME FUN HERE!");
								}, 200);
								Bukkit.getLogger().info("A player has initiated THE GAUNTLET!");
							} else {
								name = CustomWither.spawnRandom().onSpawn(PluginUtils.getNearestPlayer(wither), wither);
								wither.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
								wither.setTarget(PluginUtils.getNearestPlayer(wither));
								wither.setCustomNameVisible(true);
								wither.setPersistent(true);
								wither.setRemoveWhenFarAway(false);
								wither.addScoreboardTag("SkyblockBoss");
							}
						} else {
							return;
						}
					}
					case EnderDragon dragon -> {
						if(!dragon.getScoreboardTags().contains("WitherKingDragon") && !isWitherLordFightActive) {
							name = CustomDragon.spawnRandom().onSpawn(PluginUtils.getNearestPlayer(dragon), dragon);
							dragon.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
							dragon.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
							dragon.setTarget(PluginUtils.getNearestPlayer(dragon));
							dragon.setCustomNameVisible(true);
							dragon.setPersistent(true);
							dragon.setRemoveWhenFarAway(false);
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
			int maxHealth = (int) Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).getValue();
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