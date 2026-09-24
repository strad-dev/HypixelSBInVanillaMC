package pvp;

import misc.Utils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * FFA arena enter/exit detection and quit cleanup. Damage/death is driven by CustomDamage through
 * {@link PvpHooks} ({@link #blocksDamage}, {@link #handleLethal}), since CustomDamage cancels the vanilla
 * event. Inert unless FFA or duels are on.
 */
public class PvpListener implements Listener {

	// TODO(user): final wording for the four Free-For-All enter/exit messages below.
	private static final String ARENA_ENTER_MSG = "<green>You entered the Free-For-All arena.";
	private static final String ARENA_EXIT_MSG = "<gray>You left the Free-For-All arena.";
	private static final String SAFEZONE_ENTER_MSG = "<green>You entered the safe zone.";
	private static final String SAFEZONE_EXIT_MSG = "<red>You left the safe zone.";

	// Resistance V = full immunity under CustomDamage (20% per level).
	private static final int MAX_RESISTANCE = 4;
	private static final long COMBO_WINDOW_MILLIS = 3_000L;

	private final PvpConfig cfg;
	private final PvpStats stats;
	private final DuelManager duels;

	private final Set<UUID> inArena = new HashSet<>();    // inside FFA bounds
	private final Set<UUID> inSafezone = new HashSet<>(); // granted safezone immunity
	private final Map<UUID, Combo> combos = new HashMap<>(); // attacker -> current combo

	public PvpListener(PvpConfig cfg, PvpStats stats, DuelManager duels) {
		this.cfg = cfg;
		this.stats = stats;
		this.duels = duels;
	}

	public void start(JavaPlugin plugin) {
		plugin.getServer().getScheduler().runTaskTimer(plugin, this::tickSafezone, 20L, 20L);
	}

	/** Max Resistance for anyone in the FFA safezone, stripped as they leave. */
	private void tickSafezone() {
		if (!cfg.ffaEnabled() || !cfg.safezoneEnabled()) return;
		Region sz = cfg.safezone();
		if (sz == null) return;
		for (Player p : Bukkit.getOnlinePlayers()) {
			boolean immune = sz.contains(p.getLocation()) && !duels.inDuel(p.getUniqueId());
			if (immune) {
				if (inSafezone.add(p.getUniqueId())) p.sendMessage(Utils.msg(SAFEZONE_ENTER_MSG));
				// Short, refreshed each second, so it drops on its own after they leave.
				p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, MAX_RESISTANCE, false, false));
			} else if (inSafezone.remove(p.getUniqueId())) {
				p.removePotionEffect(PotionEffectType.RESISTANCE);
				p.sendMessage(Utils.msg(SAFEZONE_EXIT_MSG));
			}
		}
	}

	/**
	 * Holds safezone immunity per the tick above (already accounts for FFA/safezone on and duellists excluded).
	 */
	public boolean inSafezone(Player p) {
		return inSafezone.contains(p.getUniqueId());
	}

	// ===== CustomDamage hooks (called from PvpHooks) =====

	/**
	 * PvP rules forbid this damage: duel countdown, outsider hitting a duelist, or FFA safezone.
	 */
	public boolean blocksDamage(Player victim, Player attacker) {
		if (duels.inDuel(victim.getUniqueId())) {
			if (!duels.armed(victim.getUniqueId())) return true; // countdown
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
	 * Lethal blow on {@code victim}: end the duel or score the FFA kill and revive (no death screen). True if
	 * handled, so CustomDamage skips the kill.
	 */
	/**
	 * Landed PvP hit for stats, only in real combat: armed duel between the two, or both inside the FFA arena.
	 */
	public void trackHit(Player victim, Player attacker, double finalDamage, boolean arrow, boolean crit, boolean iframe) {
		if (attacker.equals(victim)) return;
		boolean duelHit = duels.inDuel(victim.getUniqueId()) && duels.armed(victim.getUniqueId())
				&& duels.areOpponents(victim.getUniqueId(), attacker.getUniqueId());
		boolean ffaHit = !duels.inDuel(victim.getUniqueId()) && cfg.ffaEnabled() && inFfa(victim) && inFfa(attacker);
		if (!duelHit && !ffaHit) return;

		stats.addDamage(attacker, victim, finalDamage);
		stats.addHit(attacker, arrow);
		stats.addHitFlags(attacker, crit, iframe);

		// Combo = consecutive hits on one victim within the window; taking a hit breaks yours.
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

		if (duelHit) duels.recordHit(attacker, victim, finalDamage, crit, iframe);
	}

	/** Live PvP combat: armed duel, or inside the FFA arena. */
	private boolean inPvpContext(Player p) {
		if (duels.inDuel(p.getUniqueId())) return duels.armed(p.getUniqueId());
		return cfg.ffaEnabled() && inFfa(p);
	}

	/** No dropping items in a duel (incl. countdown) or inside the FFA arena. */
	@EventHandler(ignoreCancelled = true)
	public void onDropItem(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if (duels.inDuel(p.getUniqueId()) || (cfg.ffaEnabled() && inFfa(p))) e.setCancelled(true);
	}

	/** Mana spent on an ability (PvP only). Via PvpHooks from CustomItems. */
	public void trackMana(Player p, int amount) {
		duels.recordMana(p, amount);                            // 1v1 summary, no-op outside a duel
		if (inPvpContext(p)) stats.addIntelligenceUsed(p, amount);
	}

	/** HP restored (PvP only). Via PvpHooks from regen + wands. */
	public void trackHeal(Player p, int amount) {
		duels.recordHeal(p, amount);                      // 1v1 summary, no-op outside a duel
		if (inPvpContext(p)) stats.addHealed(p, amount);  // lifetime /pvpstats
	}

	/**
	 * No totem in the FFA arena: the kill is scored and they respawn anyway, so a totem would only deny the kill.
	 * Duels keep theirs, a totem is a legal loadout item.
	 */
	public boolean ignoresTotem(Player victim) {
		return !duels.inDuel(victim.getUniqueId()) && cfg.ffaEnabled() && inFfa(victim);
	}

	public boolean handleLethal(Player victim, Player attacker, boolean absolute) {
		if (duels.inDuel(victim.getUniqueId())) {
			if (absolute) duels.draw(victim);   // /kill, void, border = draw
			else duels.handleDeath(victim);      // heals both, restores intelligence, home after 5s
			return true;
		}
		if (cfg.ffaEnabled() && inFfa(victim)) {
			if (absolute) ffaRespawn(victim);    // /kill etc: respawn, no death counted
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

	/** Heal to full and back to FFA spawn instead of dying (no stat change). */
	private void ffaRespawn(Player victim) {
		healFull(victim);
		victim.setFoodLevel(20);
		victim.setFireTicks(0);
		// Mana topped up TO the floor, never down, so a respawn can't cost mana. -1 leaves it alone.
		int intel = cfg.ffaRespawnIntelligence();
		if (intel >= 0) {
			int current = DuelManager.readIntelligence(victim);  // -1 = no Intelligence objective
			if (current >= 0 && current < intel) DuelManager.setIntelligence(victim, intel);
		}
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
		if (e.getItem().getType().isEdible() && inPvpContext(e.getPlayer())) stats.addFoodEaten(e.getPlayer());
	}

	@EventHandler(ignoreCancelled = true)
	public void onSwing(PlayerAnimationEvent e) {
		duels.recordAttempt(e.getPlayer()); // arm swing = melee attempt
		if (inPvpContext(e.getPlayer())) stats.addHitAttempt(e.getPlayer());
	}

	@EventHandler(ignoreCancelled = true)
	public void onShootBow(EntityShootBowEvent e) {
		if (e.getEntity() instanceof Player p) {
			duels.recordAttempt(p); // bow shot = ranged attempt
			if (inPvpContext(p)) stats.addHitAttempt(p);
		}
	}

	// ===== arena protection =====
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		// Creative players exempt so they can edit the arena.
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		if (inArenaRegion(e.getBlock().getLocation())) e.setCancelled(true);
	}

	/** Inside FFA or duel arena bounds. */
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
		// Quit mid-duel: out of the arena first.
		duels.restoreOnJoin(e.getPlayer());
		// Seed silently so relogging inside the arena/safezone doesn't fire a spurious "entered".
		if (cfg.ffaEnabled()) {
			Region b = cfg.ffaBounds();
			if (b != null && b.contains(e.getPlayer().getLocation())) inArena.add(e.getPlayer().getUniqueId());
			if (cfg.safezoneEnabled() && !duels.inDuel(e.getPlayer().getUniqueId())) {
				Region sz = cfg.safezone();
				if (sz != null && sz.contains(e.getPlayer().getLocation())) inSafezone.add(e.getPlayer().getUniqueId());
			}
		}
	}

	/** Once per crossing of the FFA boundary (skipped in a duel). */
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
