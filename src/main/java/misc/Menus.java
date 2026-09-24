package misc;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Shared rules for this plugin's custom menus.
 *
 * <p><b>Workspace rule: a custom menu IGNORES double-clicks.</b> Every menu click handler calls
 * {@link #ignoreDoubleClick} first; no per-menu note.
 *
 * <p>Why: {@code ClickType.DOUBLE_CLICK} is not a second click. With an empty cursor the first press arrives as a
 * plain {@code LEFT}, then the client sends a SECOND event for the same gesture (vanilla collect-to-cursor), so a
 * menu acting on both does one action twice (found via a Same Color pane stepping two colours).
 * <b>{@code isLeftClick()} can't be the test</b>: Bukkit counts {@code DOUBLE_CLICK} as a left click.
 */
public final class Menus {
	private Menus() {}

	/**
	 * Eat a double-click. Call FIRST in every menu click handler, right after the holder check; before it, this
	 * would cancel collects in a real chest, which players use.
	 *
	 * @return true if the caller should return immediately; the event is already cancelled.
	 */
	public static boolean ignoreDoubleClick(InventoryClickEvent e) {
		if (e.getClick() != ClickType.DOUBLE_CLICK) return false;
		e.setCancelled(true);
		return true;
	}
}
