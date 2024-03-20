package listeners;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.Vector;

import java.util.*;

import static listeners.CustomDamage.customMobs;

public class NonEntityDamage implements Listener {
	private final Map<LivingEntity, Long> noDamageTimes = new WeakHashMap<>();

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof LivingEntity entity) {
			e.setCancelled(true);
			DamageType type;
			switch(e.getCause()) {
				case BLOCK_EXPLOSION, THORNS -> type = DamageType.MELEE;
				case POISON, WITHER -> type = DamageType.MAGIC;
				case CONTACT, DROWNING, DRYOUT, FIRE, FIRE_TICK, FLY_INTO_WALL, FREEZE, HOT_FLOOR, LAVA, MELTING, STARVATION, SUFFOCATION ->
						type = DamageType.ENVIRONMENTAL;
				case FALL -> type = DamageType.FALL;
				case CRAMMING, KILL, SUICIDE, VOID, WORLD_BORDER -> type = DamageType.ABSOLUTE;
				case CUSTOM -> type = DamageType.IFRAME_ENVIRONMENTAL;
				default -> {
					return;
				}
			}

			CustomDamage.setEvent(e);

			long currentTime = System.currentTimeMillis();
			boolean hasLastDamageTime = noDamageTimes.containsKey(entity);
			long lastDamageTime = noDamageTimes.computeIfAbsent(entity, entity2 -> currentTime);

			if(hasLastDamageTime && currentTime - lastDamageTime > 490 || type == DamageType.ABSOLUTE) {
				customMobs(entity, null, e.getDamage(), type);
				noDamageTimes.put(entity, currentTime);
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		noDamageTimes.remove(e.getEntity());
	}

	public static void shootBeam(Entity origin, Color color, long distance, long pierce, double damage) {
		Location l = origin.getLocation();
		l.add(0, 1.62, 0);
		Vector v = l.getDirection();
		v.setX(v.getX() / 5);
		v.setY(v.getY() / 5);
		v.setZ(v.getZ() / 5);
		World world = origin.getWorld();
		Set<Entity> damagedEntities = new HashSet<>();
		damagedEntities.add(origin);
		for(int i = 0; i < distance * 5 && pierce > 0; i++) {
			if(!l.getBlock().isEmpty()) {
				break;
			}
			ArrayList<Entity> entities = (ArrayList<Entity>) world.getNearbyEntities(l, 1, 1, 1);
			for(Entity entity : entities) {
				if(entity instanceof LivingEntity temp && !damagedEntities.contains(entity)) {
					damagedEntities.add(entity);
					customMobs(temp, origin, damage, DamageType.RANGED);
					pierce --;
				}
			}
			Particle.DustOptions particle = new Particle.DustOptions(color, 1.0F) ;
			world.spawnParticle(Particle.REDSTONE, l, 1, particle);
			l.add(v);
		}
	}
}