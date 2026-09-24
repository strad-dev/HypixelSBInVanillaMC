package pvp;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Static bridge from CustomDamage to duel/FFA. CustomDamage cancels EntityDamageEvent and applies damage
 * via setHealth, so ordinary listeners can't see combat; it calls these hooks at its decision points.
 * Wired by {@link PvpModule#enable} only with PvP on; otherwise the listener is null and every hook no-ops.
 */
public final class PvpHooks {
	private static PvpListener listener;

	private PvpHooks() {}

	static void install(PvpListener l) {
		listener = l;
	}

	/**
	 * Block outright: FFA safezone, duel countdown (invulnerable until FIGHT), or outsider hitting a duelist.
	 */
	public static boolean shouldBlock(LivingEntity victim, Entity attacker) {
		return listener != null && victim instanceof Player v
				&& listener.blocksDamage(v, attacker instanceof Player ? (Player) attacker : null);
	}

	/**
	 * Lethal blow skips the totem (not consumed, no revive) inside the FFA arena, where the kill is scored
	 * and the victim respawns anyway. Duels unaffected.
	 */
	public static boolean ignoresTotem(LivingEntity victim) {
		return listener != null && victim instanceof Player v && listener.ignoresTotem(v);
	}

	/**
	 * Blow would be lethal. True if PvP took the kill (duel ended or FFA kill scored) and revived them, so
	 * CustomDamage must NOT kill them.
	 */
	public static boolean handleLethal(LivingEntity victim, Entity attacker, boolean absolute) {
		return listener != null && victim instanceof Player v
				&& listener.handleLethal(v, attacker instanceof Player ? (Player) attacker : null, absolute);
	}

	/**
	 * Landed PvP hit, for damage/accuracy/combo stats. Called for every hit; listener decides if it counts.
	 */
	public static void trackHit(LivingEntity victim, Entity attacker, double finalDamage, boolean arrow, boolean crit, boolean iframe) {
		if (listener != null && victim instanceof Player v && attacker instanceof Player a) {
			listener.trackHit(v, a, finalDamage, arrow, crit, iframe);
		}
	}

	/**
	 * Holds FFA safezone immunity. Suppresses passive mana regen while parked there.
	 */
	public static boolean inSafezone(Player p) {
		return listener != null && listener.inSafezone(p);
	}

	/** Mana spent on an ability; counted only in PvP combat. */
	public static void trackMana(Player p, int amount) {
		if (listener != null) listener.trackMana(p, amount);
	}

	/** HP restored; counted only in PvP combat. */
	public static void trackHeal(Player p, int amount) {
		if (listener != null) listener.trackHeal(p, amount);
	}
}
