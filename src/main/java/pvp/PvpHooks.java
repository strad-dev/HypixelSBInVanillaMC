package pvp;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Static bridge that layers the duel/FFA system on top of SkyBlock's CustomDamage flow. CustomDamage
 * owns and recomputes all combat (it cancels the vanilla EntityDamageEvent and applies damage itself
 * via setHealth), so the PvP feature can't observe combat through ordinary Bukkit listeners. Instead
 * CustomDamage calls into these two hooks at its decision points.
 *
 * Wired by {@link PvpModule#enable} only when the PvP feature is active; otherwise the listener stays
 * null and both hooks no-op, leaving SkyBlock's combat completely unchanged on non-pvp servers.
 */
public final class PvpHooks {
	private static PvpListener listener;

	private PvpHooks() {}

	static void install(PvpListener l) {
		listener = l;
	}

	/**
	 * True if this damage should be prevented outright: a Free-For-All safezone, a duel countdown
	 * (combatants are frozen/invulnerable until FIGHT), or an outsider trying to interfere in a duel.
	 */
	public static boolean shouldBlock(LivingEntity victim, Entity attacker) {
		return listener != null && victim instanceof Player v
				&& listener.blocksDamage(v, attacker instanceof Player ? (Player) attacker : null);
	}

	/**
	 * Called when a blow would be lethal to {@code victim}. Returns true if the PvP system consumed
	 * the kill (ended the duel, or scored the FFA kill) and revived the player, so CustomDamage must
	 * NOT kill them (no death screen).
	 */
	public static boolean handleLethal(LivingEntity victim, Entity attacker, boolean absolute) {
		return listener != null && victim instanceof Player v
				&& listener.handleLethal(v, attacker instanceof Player ? (Player) attacker : null, absolute);
	}

	/**
	 * Reports a landed player-vs-player hit so the PvP layer can update damage/accuracy/combo stats.
	 * Called for every applied hit; the listener decides whether it counts (arena or armed duel).
	 */
	public static void trackHit(LivingEntity victim, Entity attacker, double finalDamage, boolean arrow, boolean crit, boolean iframe) {
		if (listener != null && victim instanceof Player v && attacker instanceof Player a) {
			listener.trackHit(v, a, finalDamage, arrow, crit, iframe);
		}
	}

	/** Reports intelligence (mana) spent on an ability; counted only while the player is in PvP combat. */
	public static void trackMana(Player p, int amount) {
		if (listener != null) listener.trackMana(p, amount);
	}

	/** Reports HP restored; counted only while the player is in PvP combat. */
	public static void trackHeal(Player p, int amount) {
		if (listener != null) listener.trackHeal(p, amount);
	}
}
