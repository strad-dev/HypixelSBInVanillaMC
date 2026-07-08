package listeners;

import misc.Utils;
import mobs.enderDragons.CustomDragon;
import mobs.hardmode.enderDragons.PrimalDragon;
import mobs.hardmode.withers.Maxor;
import mobs.withers.CustomWither;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

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
			if(p2.hasPotionEffect(PotionEffectType.BAD_OMEN)) {
				return p2;
			}
		}
		return null;
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		if(e.getEntity() instanceof LivingEntity entity) {
			// Blanket every boss + subentity (all tagged "SkyblockBoss") with Depth Strider 3 so they
			// don't crawl through water. Delayed a tick because the tag is added during/after this event.
			Utils.scheduleTask(() -> {
				if(entity.isValid() && entity.getScoreboardTags().contains("SkyblockBoss")) {
					Utils.applyDepthStrider(entity);
				}
			}, 1);

			String name = "";

			// MAXOR, STORM, GOLDOR, NECRON
			try {
				switch(entity) {
					case Wither wither -> {
						Player p = Utils.getNearestPlayer(e.getEntity(), 64);
						Player hardModePlayer = getHardModePlayer(p, e);
						wither.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
						if(!isWitherLordFightActive) {
							if(hardModePlayer != null) {
								hardModePlayer.removePotionEffect(PotionEffectType.BAD_OMEN);
								Utils.scheduleTask(wither::remove, 1);
								Utils.scheduleTask(() -> {
									Wither wither2 = (Wither) wither.getWorld().spawnEntity(wither.getLocation(), EntityType.WITHER);
									new Maxor().onSpawn(p, wither2);
								}, 240);
								isWitherLordFightActive = true;
								Utils.scheduleTask(() -> {
									Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
									Bukkit.broadcast(Utils.msg("<gold><bold>﴾ <red><bold>Maxor<gold><bold> ﴿<red><bold>: WELL WELL WELL LOOK WHO'S BACK FOR A REMATCH!"));
								}, 20);
								Utils.scheduleTask(() -> {
									Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
									Bukkit.broadcast(Utils.msg("<gold><bold>﴾ <red><bold>Maxor<gold><bold> ﴿<red><bold>: I HAVE BEEN PRACTISING 40 HOURS A DAY SINCE WE LAST MET!"));
								}, 80);
								Utils.scheduleTask(() -> {
									Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
									Bukkit.broadcast(Utils.msg("<gold><bold>﴾ <red><bold>Maxor<gold><bold> ﴿<red><bold>: MY TRICKS ARE MORE SOPHISTICATED THAN EVER; YOU WILL NEVER GET AROUND THEM!"));
								}, 140);
								Utils.scheduleTask(() -> {
									Utils.playGlobalSound(Sound.ENTITY_WITHER_AMBIENT);
									Bukkit.broadcast(Utils.msg("<gold><bold>﴾ <red><bold>Maxor<gold><bold> ﴿<red><bold>: NOW LET'S HAVE SOME FUN HERE!"));
								}, 200);
								Bukkit.getLogger().info("A player has initiated the Wither Lords fight!");
							} else {
								name = CustomWither.spawnRandom().onSpawn(p, wither);
								Utils.setupBoss(wither, p);
							}
						} else {
							return;
						}
					}
					case EnderDragon dragon -> {
						Player p = Utils.getNearestPlayer(e.getEntity(), 128);
						Player hardModePlayer = getHardModePlayer(p, e);
						if(hardModePlayer != null) {
							hardModePlayer.removePotionEffect(PotionEffectType.BAD_OMEN);
							name = new PrimalDragon().onSpawn(hardModePlayer, dragon);
						} else {
							name = CustomDragon.spawnRandom().onSpawn(p, dragon);
							dragon.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
							Utils.setupBoss(dragon, p);
						}
					}
					default -> {
					}
				}
			} catch(Exception exception) {
				// do nothing
			}
			// add health to the entity name if it doesn't exist already
			if(name.isEmpty()) {
				// Already named on a previous pass (e.g. a CustomPig that copied its name across the swap,
				// or another plugin)? Leave it. Rebuilding from the legacy-serialized getName() loses the
				// colors - the red ❤ would turn <aqua> - and MiniMessage can't parse the § codes. HP stays
				// current via Utils.changeName(entity) on hits.
				if(entity.customName() != null) {
					entity.setCustomNameVisible(true);
					return;
				}
				name = "<aqua>" + entity.getName();
			}
			if(!name.contains("❤")) {
				Utils.changeName(entity, name);
			} else {
				entity.customName(Utils.msg(name));
			}
			entity.setCustomNameVisible(true);
		}
	}
}