package pvp;

import misc.Utils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * The non-combat half of the PvP feature: Free-For-All arena enter/exit detection and quit cleanup.
 * All damage/death handling is layered on top of SkyBlock's CustomDamage via {@link PvpHooks} (which
 * calls {@link #blocksDamage} and {@link #handleLethal}), because CustomDamage cancels the vanilla
 * damage event and applies its own. Inert unless FFA or duels are enabled.
 */
public class PvpListener implements Listener {

	// TODO(user): final wording for the two Free-For-All arena enter/exit messages below.
	private static final String ARENA_ENTER_MSG = "<green>You entered the Free-For-All arena.";
	private static final String ARENA_EXIT_MSG = "<gray>You left the Free-For-All arena.";

	// Resistance V = full immunity under CustomDamage (it reduces damage by 20% per level).
	private static final int MAX_RESISTANCE = 4;
	private static final long COMBO_WINDOW_MILLIS = 3_000L;

	private final PvpConfig cfg;
	private final PvpStats stats;
	private final DuelManager duels;

	private final Set<UUID> inArena = new HashSet<>();    // players currently inside FFA bounds
	private final Set<UUID> inSafezone = new HashSet<>(); // players we've granted safezone immunity
	private final Map<UUID, Combo> combos = new HashMap<>(); // attacker -> current combo

	public PvpListener(PvpConfig cfg, PvpStats stats, DuelManager duels) {
		this.cfg = cfg;
		this.stats = stats;
		this.duels = duels;
	}

	/** Starts the per-second safezone-immunity refresher. */
	public void start(JavaPlugin plugin) {
		plugin.getServer().getScheduler().runTaskTimer(plugin, this::tickSafezone, 20L, 20L);
	}

	/** Keeps max Resistance on anyone standing in the FFA safezone and strips it as they leave. */
	private void tickSafezone() {
		if (!cfg.ffaEnabled() || !cfg.safezoneEnabled()) return;
		Region sz = cfg.safezone();
		if (sz == null) return;
		for (Player p : Bukkit.getOnlinePlayers()) {
			boolean immune = sz.contains(p.getLocation()) && !duels.inDuel(p.getUniqueId());
			if (immune) {
				inSafezone.add(p.getUniqueId());
				// Short duration, refreshed each second, so it drops on its own shortly after they leave.
				p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, MAX_RESISTANCE, false, false));
			} else if (inSafezone.remove(p.getUniqueId())) {
				p.removePotionEffect(PotionEffectType.RESISTANCE);
			}
		}
	}

	// ===== CustomDamage hooks (called from PvpHooks) =====

	/**
	 * True if PvP rules forbid this damage: a duel countdown (frozen until FIGHT), an outsider hitting
	 * a duelist, or a Free-For-All safezone.
	 */
	public boolean blocksDamage(Player victim, Player attacker) {
		if (duels.inDuel(victim.getUniqueId())) {
			if (!duels.armed(victim.getUniqueId())) return true; // countdown: nobody takes damage
			return attacker != null && !duels.areOpponents(victim.getUniqueId(), attacker.getUniqueId()); // outsider
		}
		if (cfg.ffaEnabled() && inFfa(victim) && cfg.safezoneEnabled()) {
			Region sz = cfg.safezone();
			return sz != null && (sz.contains(victim.getLocation())
					|| (attacker != null && sz.contains(attacker.getLocation())));
		}
		return false;
	}

	/**
	 * A lethal blow landed on {@code victim}. Ends the duel or scores the FFA kill and revives the
	 * player (no death screen). Returns true if PvP handled it so CustomDamage skips the kill.
	 */
	/**
	 * Records a landed player-vs-player hit for combat stats, but only when it's real PvP combat: an
	 * armed duel between the two opponents, or both players inside the FFA arena.
	 */
	public void trackHit(Player victim, Player attacker, double finalDamage, boolean arrow) {
		if (attacker.equals(victim)) return;
		boolean duelHit = duels.inDuel(victim.getUniqueId()) && duels.armed(victim.getUniqueId())
				&& duels.areOpponents(victim.getUniqueId(), attacker.getUniqueId());
		boolean ffaHit = !duels.inDuel(victim.getUniqueId()) && cfg.ffaEnabled() && inFfa(victim) && inFfa(attacker);
		if (!duelHit && !ffaHit) return;

		stats.addDamage(attacker, victim, finalDamage);
		stats.addHit(attacker, arrow);

		// Combo = consecutive hits on the same victim within the window; taking a hit breaks yours.
		combos.remove(victim.getUniqueId());
		long now = System.currentTimeMillis();
		Combo c = combos.get(attacker.getUniqueId());
		if (c == null || !c.victim.equals(victim.getUniqueId()) || now - c.last > COMBO_WINDOW_MILLIS) {
			c = new Combo(victim.getUniqueId());
			combos.put(attacker.getUniqueId(), c);
		}
		c.count++;
		c.last = now;
		stats.reportCombo(attacker, c.count);

		if (duelHit) duels.recordHit(attacker, victim, finalDamage);
	}

	public boolean handleLethal(Player victim, Player attacker, boolean absolute) {
		if (duels.inDuel(victim.getUniqueId())) {
			if (absolute) duels.draw(victim);   // /kill (and void/border) end a 1v1 as a draw
			else duels.handleDeath(victim);      // heals both, restores intelligence, returns home after 5s
			return true;
		}
		if (cfg.ffaEnabled() && inFfa(victim)) {
			if (absolute) ffaRespawn(victim);    // /kill etc. - respawn but don't count a death
			else ffaDeath(victim, attacker);
			return true;
		}
		return false;
	}

	private void ffaDeath(Player victim, Player killer) {
		if (killer != null && !killer.equals(victim)) {
			stats.recordKill(killer, victim);
			Bukkit.broadcast(Utils.msg("<red><k></red> <gray>killed</gray> <red><v></red>",
					Placeholder.unparsed("k", killer.getName()), Placeholder.unparsed("v", victim.getName())));
		} else {
			stats.recordDeath(victim);
		}
		ffaRespawn(victim);
	}

	/** Heal to full and send the player back to the FFA spawn instead of dying (no stat change). */
	private void ffaRespawn(Player victim) {
		healFull(victim);
		victim.setFoodLevel(20);
		victim.setFireTicks(0);
		Location spawn = cfg.ffaSpawn();
		if (spawn != null) victim.teleport(spawn);
	}

	// ===== quit cleanup =====
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		duels.handleQuit(e.getPlayer());
		inArena.remove(e.getPlayer().getUniqueId());
		inSafezone.remove(e.getPlayer().getUniqueId());
		combos.remove(e.getPlayer().getUniqueId());
	}

	// ===== per-match duel stats (no-op outside a duel) =====
	@EventHandler(ignoreCancelled = true)
	public void onRegainHealth(EntityRegainHealthEvent e) {
		if (e.getEntity() instanceof Player p) duels.recordHeal(p, e.getAmount());
	}

	@EventHandler(ignoreCancelled = true)
	public void onConsume(PlayerItemConsumeEvent e) {
		duels.recordFood(e.getPlayer());
	}

	@EventHandler(ignoreCancelled = true)
	public void onSwing(PlayerAnimationEvent e) {
		duels.recordAttempt(e.getPlayer()); // arm swing = a melee attempt
	}

	@EventHandler(ignoreCancelled = true)
	public void onShootBow(EntityShootBowEvent e) {
		if (e.getEntity() instanceof Player p) duels.recordAttempt(p); // bow shot = a ranged attempt
	}

	// ===== arena protection =====
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		if (inArenaRegion(e.getBlock().getLocation())) e.setCancelled(true);
	}

	/** True if the location is inside the FFA bounds or the duel arena bounds. */
	private boolean inArenaRegion(Location loc) {
		if (cfg.ffaEnabled()) {
			Region b = cfg.ffaBounds();
			if (b != null && b.contains(loc)) return true;
		}
		if (cfg.duelEnabled()) {
			Region a = cfg.duelArena();
			if (a != null && a.contains(loc)) return true;
		}
		return false;
	}

	// ===== Free-For-All arena enter/exit detection =====
	@EventHandler(ignoreCancelled = true)
	public void onMove(PlayerMoveEvent e) {
		if (crossedBlock(e.getFrom(), e.getTo())) checkArena(e.getPlayer(), e.getTo());
	}

	@EventHandler(ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent e) {
		checkArena(e.getPlayer(), e.getTo());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		// Seed membership silently so relogging inside the arena doesn't fire a spurious "entered".
		if (cfg.ffaEnabled()) {
			Region b = cfg.ffaBounds();
			if (b != null && b.contains(e.getPlayer().getLocation())) inArena.add(e.getPlayer().getUniqueId());
		}
	}

	/** Fires once on each crossing of the FFA arena boundary (skipped while in a duel). */
	private void checkArena(Player p, Location to) {
		if (to == null || !cfg.ffaEnabled() || duels.inDuel(p.getUniqueId())) return;
		Region b = cfg.ffaBounds();
		if (b == null) return;
		boolean nowIn = b.contains(to);
		boolean wasIn = inArena.contains(p.getUniqueId());
		if (nowIn && !wasIn) {
			inArena.add(p.getUniqueId());
			p.sendMessage(Utils.msg(ARENA_ENTER_MSG));
		} else if (!nowIn && wasIn) {
			inArena.remove(p.getUniqueId());
			p.sendMessage(Utils.msg(ARENA_EXIT_MSG));
		}
	}

	private static boolean crossedBlock(Location a, Location b) {
		if (b == null) return false;
		return a.getBlockX() != b.getBlockX() || a.getBlockY() != b.getBlockY() || a.getBlockZ() != b.getBlockZ();
	}

	private boolean inFfa(Player p) {
		Region b = cfg.ffaBounds();
		return b == null || b.contains(p.getLocation());
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
