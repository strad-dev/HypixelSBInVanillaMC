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
 * <b>A 26.2-only backport of the 26.3 dragon neck fix. Delete this whole class, its registration in
 * {@code Plugin.onEnable} and the {@code unquarter} call in {@link CustomDamage} on 26.3.</b>
 *
 * <p>{@code EnderDragon.hurt(level, part, source, amount)} takes {@code amount / 4 + min(amount, 1)} off
 * every hit that does not land on {@code this.head} - the neck included, even though the neck hitbox is what
 * a player aiming at the head usually connects with. 26.3 counts the neck as the head; here we put the
 * quartering back.
 *
 * <p><b>MELEE ONLY.</b> Only a swing is listened for, so an arrow that lands on the neck keeps its
 * quartering: the 4x for reaching the head is a reward for being in melee range of the dragon. A shot on
 * the real head part is still worth full damage, since that is vanilla's own doing and nothing here takes
 * damage away. On 26.3, where the neck IS the head, arrows gain the neck for the same reason.
 *
 * <p><b>Why this needs a listener at all:</b> the reduction happens before Bukkit hears anything. The part
 * takes the hit, hands it to the dragon, the dragon quarters it and only then does {@code reallyHurt} fire
 * the damage event - and that event names the DRAGON, not the part, so by the time our pipeline sees the
 * blow the part it landed on is gone. Paper's {@code PrePlayerAttackEntityEvent} is the last thing that
 * still knows: it runs before {@code Player.attack}, it names the part, and it fires in the same tick and
 * the same call stack as the damage event that follows, which is what the tick stamp checks.
 */
public final class DragonNeck implements Listener {
	/** Vanilla's own name for the part, off {@code EnderDragonPart.name}. The others are head/body/tail/wing. */
	private static final String NECK = "neck";

	private record Hit(String part, int tick) {}

	/** The dragon part each attacker last swung at. Weak keys: an entry nothing reads back just goes away. */
	private static final Map<Entity, Hit> HITS = new WeakHashMap<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPrePlayerAttack(PrePlayerAttackEntityEvent e) {
		if(e.getAttacked() instanceof EnderDragonPart part) {
			HITS.put(e.getPlayer(), new Hit(((CraftEnderDragonPart) part).getHandle().name, Bukkit.getCurrentTick()));
		}
	}

	/**
	 * Vanilla's quartering undone, for a swing that landed on the neck. Anything else is handed straight back,
	 * including a hit whose part we never saw - an arrow never has a record, which is how a shot keeps its
	 * quartering. This only ever gives damage back, never takes any away.
	 *
	 * <p>The inverse of {@code amount / 4 + min(amount, 1)}, not a flat x4: above 1 damage vanilla also adds
	 * a point, so the blow that was really thrown is {@code (reduced - 1) * 4}. Below it the whole thing is
	 * {@code 1.25 * amount} instead. The two meet at 1.25, and on any hit worth talking about the difference
	 * from a flat x4 is the 4 damage the {@code +1} became.
	 */
	public static double unquarter(Entity damager, double damage) {
		Hit hit = HITS.remove(damager);
		if(hit == null || hit.tick() != Bukkit.getCurrentTick() || !NECK.equals(hit.part())) return damage;
		return damage >= 1.25 ? (damage - 1) * 4 : damage / 1.25;
	}
}
