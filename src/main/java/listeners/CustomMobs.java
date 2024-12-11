package listeners;

import misc.Plugin;
import mobs.enderDragons.CustomDragon;
import mobs.hardmode.withers.MasterMaxor;
import mobs.withers.CustomWither;
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

public class CustomMobs implements Listener {

	public static void spawnLightning(Entity entity, int range) {
		List<Entity> entities = (List<Entity>) entity.getWorld().getNearbyEntities(entity.getLocation(), range, 320, range);
		for(Entity temp : entities) {
			if(temp instanceof LivingEntity entity1) {
				entity.getWorld().spawnEntity(entity1.getLocation(), EntityType.LIGHTNING_BOLT);
			}
		}
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		boolean hardMode = Plugin.getNearestPlayer(e.getEntity()).hasPotionEffect(PotionEffectType.BAD_OMEN);
		if(e.getEntity() instanceof LivingEntity entity) {
			String name = "";

			// MAXOR, STORM, GOLDOR, NECRON
			try {
				switch(entity) {
					case Wither wither -> {
						if(hardMode) {
							new MasterMaxor().onSpawn(Plugin.getNearestPlayer(wither), wither);
						} else {
							name = CustomWither.spawnRandom().onSpawn(Plugin.getNearestPlayer(wither), wither);
						}
						wither.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
						wither.setTarget(Plugin.getNearestPlayer(wither));
						wither.setCustomNameVisible(true);
						wither.setPersistent(true);
						wither.addScoreboardTag("SkyblockBoss");
					}
					case EnderDragon dragon -> {
						if(!dragon.getScoreboardTags().contains("WitherKingDragon")) {
							name = CustomDragon.spawnRandom().onSpawn(Plugin.getNearestPlayer(dragon), dragon);
							dragon.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
							dragon.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
							dragon.setTarget(Plugin.getNearestPlayer(dragon));
							dragon.setCustomNameVisible(true);
							dragon.setPersistent(true);
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