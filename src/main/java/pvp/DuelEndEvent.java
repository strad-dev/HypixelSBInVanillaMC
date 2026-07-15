package pvp;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a 1v1 duel ends (win, forfeit, or draw), after both players have been restored and are
 * about to be returned to their pre-duel spot. SkyBlock fires this unconditionally and depends on
 * nothing external — it is a plain notification that fires into the void when nothing is listening,
 * so SkyBlock stays fully standalone. An optional glue plugin may listen to it (e.g. to send
 * cross-server duelers back to their origin server). Either player may be null if they disconnected
 * mid-match.
 */
public class DuelEndEvent extends Event {
	private static final HandlerList HANDLERS = new HandlerList();

	private final Player a;
	private final Player b;

	public DuelEndEvent(@Nullable Player a, @Nullable Player b) {
		this.a = a;
		this.b = b;
	}

	public @Nullable Player getPlayerA() {
		return a;
	}

	public @Nullable Player getPlayerB() {
		return b;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static @NotNull HandlerList getHandlerList() {
		return HANDLERS;
	}
}
