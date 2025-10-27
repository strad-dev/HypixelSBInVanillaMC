package listeners;

/*
 * Code obtained from OldCombatMechanics plugin
 * Github: https://github.com/kernitus/BukkitOldCombatMechanics/tree/master
 */

import misc.Utils;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.WeakHashMap;

public class OldRegen implements Listener {
	private final Map<UUID, Long> healTimes = new WeakHashMap<>();

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent e) {
		if(e.getEntityType() != EntityType.PLAYER) {
			if(e.getEntity() instanceof LivingEntity entity) {
				if(entity instanceof Wither && entity.getScoreboardTags().contains("Invulnerable")) {
					e.setCancelled(true);
					return;
				}
				StringBuilder newName;
				try {
					String[] oldName = Objects.requireNonNull(entity.getCustomName()).split(" ");
					int health = (int) Math.ceil(entity.getHealth() + entity.getAbsorptionAmount());
					int maxHealth = (int) Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).getValue();
					oldName[oldName.length - 1] = ChatColor.YELLOW + "" + health + "/" + maxHealth;
					newName = new StringBuilder(oldName[0]);
					for(int i = 1; i < oldName.length; i++) {
						newName.append(" ").append(oldName[i]);
					}
				} catch(Exception exception) {
					// add health to the entity name
					int health = (int) Math.ceil(entity.getHealth() + entity.getAbsorptionAmount());
					int maxHealth = (int) Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).getValue();
					newName = new StringBuilder(String.valueOf(ChatColor.AQUA)).append(entity.getName()).append(" ").append(ChatColor.RED).append("❤ ").append(ChatColor.YELLOW).append(health).append("/").append(maxHealth);
					// " ♥ 20/20";
					entity.setCustomName(newName.toString());
					entity.setCustomNameVisible(true);
				}
				entity.setCustomName(newName.toString());
			}
			return;
		} else if(e.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) {
			return;
		}

		Player p = (Player) e.getEntity();
		UUID playerId = p.getUniqueId();
		e.setCancelled(true);
		float previousExhaustion = p.getExhaustion();
		long currentTime = System.currentTimeMillis();
		boolean hasLastHealTime = healTimes.containsKey(playerId);
		long lastHealTime = healTimes.computeIfAbsent(playerId, id -> currentTime);

		if(hasLastHealTime && currentTime - lastHealTime <= 3990) {
			Utils.scheduleTask(() -> p.setExhaustion(previousExhaustion), 1L);
			return;
		}

		final double maxHealth = Objects.requireNonNull(p.getAttribute(Attribute.MAX_HEALTH)).getValue();
		final double playerHealth = p.getHealth();

		if(playerHealth < maxHealth) {
			p.setHealth(clamp(playerHealth + 1, 0.0, maxHealth));
			healTimes.put(playerId, currentTime);
		}

		final float exhaustionToApply = 3;

		Utils.scheduleTask(() -> {
			// We do this in the next tick because bukkit doesn't stop the exhaustion change when cancelling the event
			p.setExhaustion(previousExhaustion + exhaustionToApply);
		}, 1L);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		healTimes.remove(e.getPlayer().getUniqueId());
	}

	public static double clamp(double value, double min, double max) {
		double realMin = Math.min(min, max);
		double realMax = Math.max(min, max);

		if(value < realMin) value = realMin;

		if(value > realMax) value = realMax;

		return value;
	}
}