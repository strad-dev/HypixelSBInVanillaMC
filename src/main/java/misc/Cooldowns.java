package misc;

import net.minecraft.server.MinecraftServer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tick-timestamp ability cooldowns, ported from the M7 TAS plugin. Instead of adding a scoreboard tag
 * and scheduling a removal task per use, each (player, cooldownTag) records the server tick the ability
 * is next usable. Checks compare against {@link MinecraftServer#currentTick} (the same tick source M7
 * uses), so the remaining time is always known and no per-use scheduler task is needed.
 */
public final class Cooldowns {
	private static final Map<UUID, Map<String, Integer>> NEXT_USABLE = new ConcurrentHashMap<>();

	private Cooldowns() {}

	/** Ticks until {@code tag} is usable again for {@code p}; 0 if ready (or the tag is blank). */
	public static int remaining(Player p, String tag) {
		if (tag == null || tag.isEmpty()) return 0;
		Map<String, Integer> tags = NEXT_USABLE.get(p.getUniqueId());
		if (tags == null) return 0;
		Integer next = tags.get(tag);
		return next == null ? 0 : Math.max(0, next - MinecraftServer.currentTick);
	}

	public static boolean onCooldown(Player p, String tag) {
		return remaining(p, tag) > 0;
	}

	/** Puts {@code tag} on cooldown for {@code ticks} from now (no-op for a blank tag or ticks <= 0). */
	public static void start(Player p, String tag, int ticks) {
		if (tag == null || tag.isEmpty() || ticks <= 0) return;
		NEXT_USABLE.computeIfAbsent(p.getUniqueId(), k -> new ConcurrentHashMap<>())
				.put(tag, MinecraftServer.currentTick + ticks);
	}

	public static void clear(Player p, String tag) {
		Map<String, Integer> tags = NEXT_USABLE.get(p.getUniqueId());
		if (tags != null) tags.remove(tag);
	}

	public static void clearAll(Player p) {
		NEXT_USABLE.remove(p.getUniqueId());
	}
}
