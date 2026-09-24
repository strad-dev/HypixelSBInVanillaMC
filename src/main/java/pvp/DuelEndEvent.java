package pvp;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1v1 ended (win, forfeit, draw), after both are restored and before they go back to their pre-duel spot.
 * Plain event fired unconditionally so SkyBlock stays standalone; a glue plugin may listen to send
 * cross-server duelers home. Either player may be null if they quit mid-match.
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
