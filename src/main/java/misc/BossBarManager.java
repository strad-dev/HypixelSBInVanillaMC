package misc;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class BossBarManager {
	private static final Map<UUID, ManagedBossBar> activeBossBars = new HashMap<>();
	private static final double DEFAULT_VISIBILITY_RANGE = 64.0;

	public static UUID createBossBar(LivingEntity entity, BarColor color, BarStyle style) {
		if(hasBossBar(entity)) {
			removeBossBar(entity);
		}

		UUID bossId = entity.getUniqueId();
		ManagedBossBar managedBar = new ManagedBossBar(entity, color, style);
		activeBossBars.put(bossId, managedBar);
		managedBar.start();

		return bossId;
	}

	public static UUID createBossBar(LivingEntity entity) {
		return createBossBar(entity, BarColor.RED, BarStyle.SOLID);
	}

	public static boolean hasBossBar(LivingEntity entity) {
		return activeBossBars.containsKey(entity.getUniqueId());
	}

	public static void removeBossBar(LivingEntity entity) {
		ManagedBossBar bar = activeBossBars.remove(entity.getUniqueId());
		if(bar != null) {
			bar.cleanup();
		}
	}

	public static BossBar getBossBar(LivingEntity entity) {
		ManagedBossBar managed = activeBossBars.get(entity.getUniqueId());
		return managed != null ? managed.bossBar : null;
	}

	public static void cleanupAll() {
		activeBossBars.values().forEach(ManagedBossBar::cleanup);
		activeBossBars.clear();
	}

	public static void addPlayerToActiveBars(Player player) {
		activeBossBars.values().forEach(bar -> {
			if(bar.bossBar != null && bar.shouldBeVisible(player)) {
				bar.bossBar.addPlayer(player);
			}
		});
	}

	private static class ManagedBossBar {
		private final LivingEntity entity;
		private final BossBar bossBar;
		private BukkitTask updateTask;
		private final double maxHealth;

		ManagedBossBar(LivingEntity entity, BarColor color, BarStyle style) {
			this.entity = entity;
			this.maxHealth = entity.getAttribute(Attribute.MAX_HEALTH).getValue();

			// Use the entity's existing custom name or default name
			String title = buildTitle();
			this.bossBar = Bukkit.createBossBar(title, color, style);
			this.bossBar.setProgress(1.0);

			// Add all nearby players
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(shouldBeVisible(player)) {
					bossBar.addPlayer(player);
				}
			}
		}

		private String buildTitle() {

			// The name already has formatting, just append health
			return entity.getCustomName() != null ? entity.getCustomName() : entity.getName();
		}

		void start() {
			updateTask = new BukkitRunnable() {
				@Override
				public void run() {
					if(entity.isDead() || !entity.isValid()) {
						BossBarManager.removeBossBar(entity);
						cancel();
						return;
					}

					update();
				}
			}.runTaskTimer(Plugin.getInstance(), 0L, 1L);
		}

		void update() {
			double currentHealth = Math.max(0, entity.getHealth());

			// Update title with current name and health
			bossBar.setTitle(buildTitle());

			// Update progress
			double progress = Math.max(0.0, Math.min(1.0, currentHealth / maxHealth));
			bossBar.setProgress(progress);

			// Update player visibility
			for(Player player : Bukkit.getOnlinePlayers()) {
				boolean shouldSee = shouldBeVisible(player);
				boolean currentlySees = bossBar.getPlayers().contains(player);

				if(shouldSee && !currentlySees) {
					bossBar.addPlayer(player);
				} else if(!shouldSee && currentlySees) {
					bossBar.removePlayer(player);
				}
			}
		}

		boolean shouldBeVisible(Player player) {
			return player.getWorld().equals(entity.getWorld()) && player.getLocation().distanceSquared(entity.getLocation()) <= Math.pow(DEFAULT_VISIBILITY_RANGE, 2);
		}

		void cleanup() {
			if(updateTask != null) {
				updateTask.cancel();
			}
			if(bossBar != null) {
				bossBar.removeAll();
			}
		}
	}
}