package listeners;

import misc.Utils;
import mobs.enderDragons.CustomDragon;
import mobs.hardmode.enderDragons.PrimalDragon;
import mobs.hardmode.withers.Maxor;
import mobs.withers.CustomWither;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
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

	/**
	 * Returns the player that triggers hard mode for this spawn, or null if not hard mode.
	 */
	private Player getHardModePlayer(Player nearest, EntitySpawnEvent e) {
		if(nearest == null) return null;
		if(nearest.hasPotionEffect(PotionEffectType.BAD_OMEN)) return nearest;
		for(Player p2 : e.getEntity().getWorld().getPlayers()) {
			if(p2.getLocation().distanceSquared(e.getLocation()) <= 4096 && p2.hasPotionEffect(PotionEffectType.BAD_OMEN)) {
				return p2;
			}
		}
		return null;
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		if(e.getEntity() instanceof LivingEntity entity) {
			String name = "";

			// MAXOR, STORM, GOLDOR, NECRON
			try {
				switch(entity) {
					case Wither wither -> {
						Player p = Utils.getNearestPlayer(e.getEntity());
						Player hardModePlayer = getHardModePlayer(p, e);
						wither.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
						if(!isWitherLordFightActive) {
							if(hardModePlayer != null) {
								hardModePlayer.removePotionEffect(PotionEffectType.BAD_OMEN);
								Utils.scheduleTask(wither::remove, 1);
								Utils.scheduleTask(() -> {
									Wither wither2 = (Wither) wither.getWorld().spawnEntity(wither.getLocation(), EntityType.WITHER);
									new Maxor().onSpawn(Utils.getNearestPlayer(wither2), wither2);
								}, 240);
								isWitherLordFightActive = true;
								Utils.scheduleTask(() -> {
									Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
									Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": WELL WELL WELL LOOK WHO'S BACK FOR A REMATCH!");
								}, 20);
								Utils.scheduleTask(() -> {
									Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
									Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": I HAVE BEEN PRACTISING 40 HOURS A DAY SINCE WE LAST MET!");
								}, 80);
								Utils.scheduleTask(() -> {
									Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
									Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": MY TRICKS ARE MORE SOPHISTICATED THAN EVER; YOU WILL NEVER GET AROUND THEM!");
								}, 140);
								Utils.scheduleTask(() -> {
									Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
									Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": NOW LET'S HAVE SOME FUN HERE!");
								}, 200);
								Bukkit.getLogger().info("A player has initiated the Wither Lords fight!");
							} else {
								name = CustomWither.spawnRandom().onSpawn(Utils.getNearestPlayer(wither), wither);
								wither.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
								wither.setTarget(Utils.getNearestPlayer(wither));
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
						if(!dragon.getScoreboardTags().contains("WitherKingDragon")) {
							Player p = Utils.getNearestPlayer(e.getEntity());
							Player hardModePlayer = getHardModePlayer(p, e);
							if(hardModePlayer != null) {
								hardModePlayer.removePotionEffect(PotionEffectType.BAD_OMEN);
								name = new PrimalDragon().onSpawn(hardModePlayer, dragon);
							} else {
								name = CustomDragon.spawnRandom().onSpawn(Utils.getNearestPlayer(dragon), dragon);
								dragon.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
								dragon.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
								dragon.setTarget(Utils.getNearestPlayer(dragon));
								dragon.setCustomNameVisible(true);
								dragon.setPersistent(true);
								dragon.setRemoveWhenFarAway(false);
								dragon.addScoreboardTag("SkyblockBoss");
							}
						}
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