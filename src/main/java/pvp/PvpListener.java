package pvp;

import misc.Utils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * FFA + safe zone + duel damage handling and stat tracking. A single damage handler covers it so
 * an entity-damage event is never processed twice. Inert unless FFA or duels are enabled (so
 * SkyBlock on survival/creative is unaffected).
 */
public class PvpListener implements Listener {
	private static final long CREDIT_MILLIS = 10_000L;
	private static final long COMBO_WINDOW_MILLIS = 3_000L;

	private final PvpConfig cfg;
	private final PvpStats stats;
	private final DuelManager duels;

	private final Map<UUID, UUID> attackerOf = new HashMap<>();          // victim -> attacker
	private final Map<UUID, Long> attackerAt = new HashMap<>();          // victim -> time
	private final Map<UUID, Combo> combos = new HashMap<>();             // attacker -> combo

	public PvpListener(PvpConfig cfg, PvpStats stats, DuelManager duels) {
		this.cfg = cfg;
		this.stats = stats;
		this.duels = duels;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player victim)) return;

		Player attacker = resolveAttacker(e);
		boolean lethal = e.getFinalDamage() >= victim.getHealth();

		// ===== duel =====
		if (duels.inDuel(victim.getUniqueId())) {
			if (!duels.armed(victim.getUniqueId())) {
				e.setCancelled(true); // frozen during countdown
				return;
			}
			if (attacker != null && !duels.areOpponents(victim.getUniqueId(), attacker.getUniqueId())) {
				e.setCancelled(true); // outsiders can't interfere
				return;
			}
			if (attacker != null) trackHit(attacker, victim, e);
			if (lethal) {
				e.setCancelled(true);
				duels.handleDeath(victim);
			}
			return;
		}

		// ===== FFA =====
		if (cfg.ffaEnabled() && inFfa(victim)) {
			if (attacker != null && cfg.safezoneEnabled()) {
				Region sz = cfg.safezone();
				if (sz != null && (sz.contains(victim.getLocation()) || sz.contains(attacker.getLocation()))) {
					e.setCancelled(true);
					return;
				}
			}
			if (attacker != null) trackHit(attacker, victim, e);
			if (lethal) {
				e.setCancelled(true);
				ffaDeath(victim);
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		duels.handleQuit(e.getPlayer());
		clear(e.getPlayer().getUniqueId());
	}

	// ===== helpers =====
	private void trackHit(Player attacker, Player victim, EntityDamageEvent e) {
		boolean arrow = e instanceof EntityDamageByEntityEvent ee && ee.getDamager() instanceof Projectile;
		stats.addDamage(attacker, victim, e.getFinalDamage());
		stats.addHit(attacker, arrow);

		attackerOf.put(victim.getUniqueId(), attacker.getUniqueId());
		attackerAt.put(victim.getUniqueId(), System.currentTimeMillis());

		combos.remove(victim.getUniqueId()); // getting hit breaks your combo
		Combo c = combos.get(attacker.getUniqueId());
		long now = System.currentTimeMillis();
		if (c == null || !c.victim.equals(victim.getUniqueId()) || now - c.last > COMBO_WINDOW_MILLIS) {
			c = new Combo(victim.getUniqueId());
			combos.put(attacker.getUniqueId(), c);
		}
		c.count++;
		c.last = now;
		stats.reportCombo(attacker, c.count);
	}

	private void ffaDeath(Player victim) {
		Player killer = recentAttacker(victim);
		if (killer != null && !killer.equals(victim)) {
			stats.recordKill(killer, victim);
			Bukkit.broadcast(Utils.msg("<red><k></red> <gray>killed</gray> <red><v></red>",
					Placeholder.unparsed("k", killer.getName()), Placeholder.unparsed("v", victim.getName())));
		} else {
			stats.recordDeath(victim);
		}
		clear(victim.getUniqueId());
		healFull(victim);
		victim.setFoodLevel(20);
		victim.setFireTicks(0);
		Location spawn = cfg.ffaSpawn();
		if (spawn != null) victim.teleport(spawn);
	}

	private boolean inFfa(Player p) {
		Region b = cfg.ffaBounds();
		return b == null || b.contains(p.getLocation());
	}

	private Player recentAttacker(Player victim) {
		Long t = attackerAt.get(victim.getUniqueId());
		UUID a = attackerOf.get(victim.getUniqueId());
		if (t == null || a == null || System.currentTimeMillis() - t > CREDIT_MILLIS) return null;
		return Bukkit.getPlayer(a);
	}

	private void clear(UUID id) {
		attackerOf.remove(id);
		attackerAt.remove(id);
		combos.remove(id);
	}

	private static Player resolveAttacker(EntityDamageEvent e) {
		if (!(e instanceof EntityDamageByEntityEvent ee)) return null;
		if (ee.getDamager() instanceof Player p) return p;
		if (ee.getDamager() instanceof Projectile pr && pr.getShooter() instanceof Player p2) return p2;
		return null;
	}

	private static void healFull(Player p) {
		var attr = p.getAttribute(Attribute.MAX_HEALTH);
		if (attr != null) p.setHealth(attr.getValue());
	}

	private static final class Combo {
		final UUID victim;
		int count;
		long last;

		Combo(UUID victim) {
			this.victim = victim;
		}
	}
}
