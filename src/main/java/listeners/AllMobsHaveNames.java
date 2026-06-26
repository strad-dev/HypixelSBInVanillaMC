package listeners;

import misc.Plugin;
import misc.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayDeque;
import java.util.Deque;

public class AllMobsHaveNames implements Listener {
	// Entities waiting to be named. EntitiesLoadEvent fires per chunk as its stored entities load — which
	// happens in a big burst when a player teleports (especially at high render distance). Naming every mob
	// inline ran a MiniMessage parse per entity, so a teleport into a mob-dense area meant hundreds of parses
	// in one tick → a lag spike. Instead we queue them here and drain a few per tick.
	private static final Deque<LivingEntity> pending = new ArrayDeque<>();
	private static final int PER_TICK = 20;
	private static BukkitTask drainer;

	@EventHandler
	public void onEntitiesLoad(EntitiesLoadEvent e) {
		for(Entity temp : e.getEntities()) {
			if(temp instanceof LivingEntity entity && entity.customName() == null) {
				pending.add(entity);
			}
		}
		startDrainer();
	}

	private static void startDrainer() {
		if(drainer != null) return;
		drainer = new BukkitRunnable() {
			@Override
			public void run() {
				int processed = 0;
				while(processed < PER_TICK && !pending.isEmpty()) {
					LivingEntity entity = pending.poll();
					processed++;
					// Skip anything that unloaded/died or got named some other way since being queued.
					if(entity == null || !entity.isValid() || entity.customName() != null) {
						continue;
					}
					Utils.changeName(entity, "<aqua>" + entity.getName());
					entity.setCustomNameVisible(true);
				}
				if(pending.isEmpty()) {
					cancel();
					drainer = null;
				}
			}
		}.runTaskTimer(Plugin.getInstance(), 1L, 1L);
	}
}
