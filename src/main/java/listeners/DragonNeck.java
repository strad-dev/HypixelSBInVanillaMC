package listeners;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEnderDragonPart;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * <b>26.2-only backport of the 26.3 dragon neck fix. On 26.3 delete this class, its registration in
 * {@code Plugin.onEnable} and the {@code unquarter} call in {@link CustomDamage}.</b>
 *
 * <p>{@code EnderDragon.hurt(level, part, source, amount)} takes {@code amount / 4 + min(amount, 1)} off every
 * hit not on {@code this.head}, the neck included, though the neck is what a player aiming at the head usually
 * hits. 26.3 counts the neck as the head; here the quartering is undone.
 *
 * <p><b>MELEE ONLY.</b> Only swings are listened for, so an arrow on the neck stays quartered: the 4x is a reward
 * for being in melee range. A shot on the real head is still full damage (vanilla). On 26.3 arrows gain the neck.
 *
 * <p>Needs a listener because the reduction happens before Bukkit hears anything, and the damage event names the
 * DRAGON, not the part. Paper's {@code PrePlayerAttackEntityEvent} still knows: it runs before
 * {@code Player.attack}, names the part, and fires in the same tick and call stack as the damage event, which is
 * what the tick stamp checks.
 */
public final class DragonNeck implements Listener {
	/** Vanilla's name for the part ({@code EnderDragonPart.name}); others are head/body/tail/wing. */
	private static final String NECK = "neck";

	private record Hit(String part, int tick) {}

	/** Dragon part each attacker last swung at. Weak keys, so unread entries go away. */
	private static final Map<Entity, Hit> HITS = new WeakHashMap<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPrePlayerAttack(PrePlayerAttackEntityEvent e) {
		if(e.getAttacked() instanceof EnderDragonPart part) {
			HITS.put(e.getPlayer(), new Hit(((CraftEnderDragonPart) part).getHandle().name, Bukkit.getCurrentTick()));
		}
	}

	/**
	 * Undoes the quartering for a swing on the neck; anything else, including an arrow (never recorded), comes back
	 * unchanged. Only ever gives damage back.
	 *
	 * <p>Inverse of {@code amount / 4 + min(amount, 1)}, not a flat x4: above 1 vanilla also adds a point, so the real
	 * blow is {@code (reduced - 1) * 4}; below it, {@code 1.25 * amount}. The two meet at 1.25.
	 */
	public static double unquarter(Entity damager, double damage) {
		Hit hit = HITS.remove(damager);
		if(hit == null || hit.tick() != Bukkit.getCurrentTick() || !NECK.equals(hit.part())) return damage;
		return damage >= 1.25 ? (damage - 1) * 4 : damage / 1.25;
	}
}
