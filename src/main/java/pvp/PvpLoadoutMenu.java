package pvp;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import listeners.CreativeMenu;
import listeners.ItemReloader;
import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * {@code /pvploadout} - the editor for the player's single PvP loadout.
 *
 * The player's OWN inventory is the loadout's four inventory rows: on open we snapshot the real inventory,
 * write the loadout into it and let them arrange it exactly as they would in a duel; on close we save those 36
 * slots and hand their own items back. The chest holds the rest:
 *   rows 1-4 (0-35) : item palette, one page (click to copy, shift-click to drop it in a free slot)
 *   row 5    (36)   : previous page          (37-43): the labelled seam          (44): next page
 *   row 6    (45-48): helmet, chestplate, leggings, boots
 *            (49)   : off-hand
 *            (52)   : trash - click with an item on the cursor to delete it
 *            (53)   : load the default kit
 * An empty armour or off-hand slot shows a white pane named for what belongs there; those panes are
 * placeholders, never contents.
 *
 * Nothing may escape and nothing may be lost: the palette hands out COPIES, so a copy that reaches the world is
 * a free item spawn, and the player's real inventory is parked in a field, so any path that ends the editor
 * without restoring eats their items. finish() is the one way an editor ends - save, restore, clear the cursor -
 * reached from close, quit, death and plugin disable alike, and idempotent. Clicks are an allowlist that
 * defaults to cancel. No advancement can be earned mid-session either (onCriterionGrant). Mirrors StradDevHub's
 * PvpLoadoutCommand - keep in sync.
 */
public final class PvpLoadoutMenu implements CommandExecutor, Listener {
	private static final int PALETTE_START = 0, PALETTE_COUNT = 36;   // rows 1-4 of the chest
	/** Row 5 is the seam: the page arrows at each end, a labelled bar between them. */
	private static final int PREV_SLOT = 36, DIVIDER_START = 37, DIVIDER_END = 43, NEXT_SLOT = 44;
	private static final int HELMET_SLOT = 45, CHEST_SLOT = 46, LEGS_SLOT = 47, BOOTS_SLOT = 48;
	private static final int OFFHAND_SLOT = 49, TRASH_SLOT = 52, RESET_SLOT = 53;
	/** The last gear slot in the bottom row; everything between it and the trash is dead space. */
	private static final int GEAR_END = OFFHAND_SLOT;
	/** The 36 loadout slots the player's own inventory stands in for: [0..8] hotbar, [9..35] storage. */
	private static final int MAIN_SLOTS = 36;

	private final PvpConfig cfg;
	private final PvpLoadouts loadouts;
	/** Open editors: player -> the 36 inventory slots we took from them. Present == an editor is live. */
	private final Map<UUID, ItemStack[]> snapshots = new HashMap<>();

	public PvpLoadoutMenu(PvpConfig cfg, PvpLoadouts loadouts) {
		this.cfg = cfg;
		this.loadouts = loadouts;
	}

	/** Chest slot -> loadout array index, for the armour and off-hand row only (the 36 are in the player's own). */
	private static int arrIndex(int gui) {
		return switch (gui) {
			case HELMET_SLOT -> 36;
			case CHEST_SLOT -> 37;
			case LEGS_SLOT -> 38;
			case BOOTS_SLOT -> 39;
			case OFFHAND_SLOT -> 40;
			default -> -1;
		};
	}

	private static boolean isPalette(int gui) {
		return gui >= PALETTE_START && gui < PALETTE_START + PALETTE_COUNT;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player p)) {
			sender.sendMessage(Utils.msg("<red>Only players can edit a loadout"));
			return true;
		}
		if (!cfg.duelEnabled()) {
			p.sendMessage(Utils.msg("<red>PvP loadouts aren't enabled on this server"));
			return true;
		}
		if (args.length >= 1 && args[0].equalsIgnoreCase("reset")) {
			loadouts.set(p.getUniqueId(), DuelKit.defaultLoadout());
			p.sendMessage(Utils.msg("<yellow>Reset your PvP loadout to the default kit"));
			return true;
		}
		open(p);
		return true;
	}

	private void open(Player p) {
		if (snapshots.containsKey(p.getUniqueId())) return; // already editing; a second open would snapshot the copies

		EditorHolder holder = new EditorHolder(palette());
		Inventory gui = Bukkit.createInventory(holder, 54, Utils.msg("<dark_gray>PvP Loadout"));
		holder.inv = gui;

		// The palette is inherently current - it's rebuilt on every open; the SAVED slots are frozen copies, so
		// re-sync them against the current item definitions too, or an item change never reaches a loadout that
		// already holds that item. refreshSaved also re-saves, so the fresh items reach duel start even if this
		// editor is closed untouched. First open (nothing saved yet) starts from the default kit.
		PvpItemRefresh.Result saved = PvpItemRefresh.refreshSaved(loadouts, p.getUniqueId());
		ItemStack[] arr = saved.arr() != null ? saved.arr() : DuelKit.defaultLoadout();
		// Take the player's own inventory BEFORE anything is written into it: this array is the only copy.
		snapshots.put(p.getUniqueId(), takeInventory(p));
		writeMain(p, arr);
		for (int g = HELMET_SLOT; g <= GEAR_END; g++) gui.setItem(g, orPlaceholder(arr[arrIndex(g)], g));
		for (int g = GEAR_END + 1; g < TRASH_SLOT; g++) gui.setItem(g, filler()); // dead space before the buttons
		drawDivider(gui);
		gui.setItem(TRASH_SLOT, button(Material.LAVA_BUCKET, "<red>Trash <gray>(click with an item to delete it)"));
		refreshPalette(gui, holder);

		p.openInventory(gui);
		p.sendMessage(Utils.msg("<gray>Editing your PvP loadout"));
		if (saved.updated() > 0) {
			p.sendMessage(Utils.msg("<gray>Updated <yellow>" + saved.updated()
					+ "<gray> item" + (saved.updated() == 1 ? "" : "s") + " in your loadout to the latest version"));
		}
	}

	/** (Re)draw the palette items + page/reset buttons for the holder's current page. */
	private void refreshPalette(Inventory gui, EditorHolder holder) {
		List<ItemStack> pal = holder.palette;
		int pages = Math.max(1, (pal.size() + PALETTE_COUNT - 1) / PALETTE_COUNT);
		if (holder.page < 0) holder.page = 0;
		if (holder.page >= pages) holder.page = pages - 1;
		for (int i = 0; i < PALETTE_COUNT; i++) {
			int idx = holder.page * PALETTE_COUNT + i;
			gui.setItem(PALETTE_START + i, idx < pal.size() ? pal.get(idx).clone() : null);
		}
		// A missing arrow falls back to the seam pane, not a plain filler, so row 5 reads as one unbroken bar.
		gui.setItem(PREV_SLOT, holder.page > 0 ? button(Material.ARROW, "<yellow>Previous page") : divider());
		gui.setItem(NEXT_SLOT, holder.page < pages - 1 ? button(Material.ARROW, "<yellow>Next page") : divider());
		gui.setItem(RESET_SLOT, button(Material.BARRIER, "<red>Reset to default kit"));
	}

	// ===== events =====
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!(e.getView().getTopInventory().getHolder() instanceof EditorHolder holder)) return;
		if (!(e.getWhoClicked() instanceof Player p)) return;
		int raw = e.getRawSlot();

		if (raw >= e.getView().getTopInventory().getSize()) {
			// The player's own inventory, which IS rows 1-4 of the loadout right now. Everything that keeps the
			// items inside these 36 slots is allowed; anything that could move one OUT (shift into the chest, a
			// drop, a double-click collect that would also vacuum palette copies) is denied, default-cancel.
			switch (e.getAction()) {
				case NOTHING, PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE,
						PLACE_ALL, PLACE_SOME, PLACE_ONE, SWAP_WITH_CURSOR,
						HOTBAR_SWAP, HOTBAR_MOVE_AND_READD,
						PICKUP_FROM_BUNDLE, PICKUP_ALL_INTO_BUNDLE, PICKUP_SOME_INTO_BUNDLE,
						PLACE_FROM_BUNDLE, PLACE_ALL_INTO_BUNDLE, PLACE_SOME_INTO_BUNDLE -> { }
				default -> { e.setCancelled(true); return; }
			}
			if (placesCursor(e.getAction()) && refuseSecondTotem(p, holder.inv, e.getCursor(), e.getSlot(), -1)) {
				e.setCancelled(true);
			}
			return;
		}

		e.setCancelled(true); // every chest slot is driven by hand below, so nothing vanilla moves anything here
		if (arrIndex(raw) >= 0) {
			if (refuseSecondTotem(p, holder.inv, p.getItemOnCursor(), -1, raw)) return;
			swapArmour(p, holder.inv, raw);
			return;
		}
		if (isPalette(raw)) {
			ItemStack tmpl = e.getCurrentItem();
			if (tmpl == null || tmpl.getType().isAir()) return;
			if (e.isShiftClick()) {
				if (refuseSecondTotem(p, holder.inv, tmpl, -1, -1)) return;
				giveToLoadout(p, tmpl.clone()); // straight into the first free inventory slot
				return;
			}
			ItemStack cursor = e.getCursor();
			if (cursor == null || cursor.getType().isAir()) p.setItemOnCursor(tmpl.clone());
			return;
		}
		if (raw == PREV_SLOT) { holder.page--; refreshPalette(holder.inv, holder); return; }
		if (raw == NEXT_SLOT) { holder.page++; refreshPalette(holder.inv, holder); return; }
		if (raw == RESET_SLOT) {
			ItemStack[] def = DuelKit.defaultLoadout();
			writeMain(p, def);
			for (int g = HELMET_SLOT; g <= GEAR_END; g++) holder.inv.setItem(g, orPlaceholder(def[arrIndex(g)], g));
			return;
		}
		if (raw == TRASH_SLOT) {
			ItemStack held = p.getItemOnCursor();
			if (held != null && !held.getType().isAir()) p.setItemOnCursor(null); // delete the held item
		}
	}

	// One armour / off-hand slot, done by hand so the slot is never left blank: taking an item back puts the
	// placeholder pane there, and placing one swaps out whatever was underneath (a placeholder swaps out as
	// nothing). Doing this through the vanilla click would leave the pane in the loadout or the slot empty.
	private void swapArmour(Player p, Inventory gui, int slot) {
		ItemStack current = gui.getItem(slot);
		ItemStack held = isPlaceholder(current, slot) ? null : current;
		ItemStack cursor = p.getItemOnCursor();
		if (cursor == null || cursor.getType().isAir()) {
			if (held == null) return;
			p.setItemOnCursor(held);
			gui.setItem(slot, placeholder(slot));
		} else {
			gui.setItem(slot, cursor.clone());
			p.setItemOnCursor(held);
		}
		p.updateInventory();
	}

	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		if (!(e.getView().getTopInventory().getHolder() instanceof EditorHolder holder)) return;
		if (!(e.getWhoClicked() instanceof Player p)) return;
		int topSize = e.getView().getTopInventory().getSize();
		for (int raw : e.getRawSlots()) {
			if (raw < topSize) { // chest slots are all hand-driven
				e.setCancelled(true);
				return;
			}
		}
		if (refuseSecondTotem(p, holder.inv, e.getOldCursor(), -1, -1)) e.setCancelled(true);
	}

	// Backstop for every drop path, whatever the click was classified as: nothing leaves an open menu as a
	// world drop. Keeps the guarantee even if the click allowlist above misses an action.
	@EventHandler(ignoreCancelled = true)
	public void onDropItem(PlayerDropItemEvent e) {
		if (e.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof EditorHolder) e.setCancelled(true);
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (!(e.getView().getTopInventory().getHolder() instanceof EditorHolder holder)) return;
		if (!(e.getPlayer() instanceof Player p)) return;
		finish(p, holder, true);
	}

	// A quit with the editor still open. Whichever of this and onClose fires first ends it; finish is idempotent.
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		finish(e.getPlayer(), holderOf(e.getPlayer()), true);
	}

	// Dying with the editor open: the drop list the server just built is made of PALETTE COPIES, so throw it away
	// and refill it from the snapshot, i.e. drop the items they actually had. LOWEST so the list is still ours.
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		ItemStack[] snap = snapshots.get(p.getUniqueId());
		if (snap == null) return;
		finish(p, holderOf(p), true);
		e.getDrops().clear();
		for (ItemStack it : snap) if (it != null && !it.getType().isAir()) e.getDrops().add(it);
	}

	// No advancement may be earned while the editor is open. Filling the player's REAL inventory with palette
	// copies fires minecraft:inventory_changed, and a large slice of the early tree hangs off that - Diamonds!,
	// Cover Me With Diamonds, Suit Up, Stone Age - so opening /pvploadout paid most of it out for items the player
	// never had and does not keep. Registered unconditionally, unlike StradDevHub's separate EditorAdvancementGuard
	// (which gates on the server persisting playerdata and covers two editors): this plugin is standalone and knows
	// nothing about server roles, and on a server where advancements are already dead nothing reaches the event.
	// Cancelling is the right tool rather than emptying the criterion map - it has to be reversible on close.
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCriterionGrant(PlayerAdvancementCriterionGrantEvent e) {
		if (snapshots.containsKey(e.getPlayer().getUniqueId())) e.setCancelled(true);
	}

	/** Shut every open editor down, saving as we go. Called from the plugin's onDisable. */
	public void restoreAll() {
		for (UUID id : new ArrayList<>(snapshots.keySet())) {
			Player p = Bukkit.getPlayer(id);
			if (p == null) {
				snapshots.remove(id); // offline with a snapshot left: nothing to restore it into
				continue;
			}
			finish(p, holderOf(p), true);
			p.closeInventory();
		}
	}

	// The ONE way an editor ends: save the 41 slots, hand the player their own inventory back, drop the cursor.
	// Idempotent - the snapshot is removed first, so the second caller does nothing. The totem clamp lives here
	// rather than only on the click, so the invariant holds however an extra one got in.
	private void finish(Player p, EditorHolder holder, boolean save) {
		ItemStack[] snap = snapshots.remove(p.getUniqueId());
		if (snap == null) return;
		try {
			if (save && holder != null) {
				ItemStack[] arr = new ItemStack[PvpLoadouts.SLOTS];
				for (int i = 0; i < MAIN_SLOTS; i++) {
					ItemStack it = p.getInventory().getItem(i);
					arr[i] = it == null ? null : it.clone(); // a live mirror, and we are about to overwrite the slot
				}
				for (int g = HELMET_SLOT; g <= GEAR_END; g++) {
					ItemStack it = holder.inv.getItem(g);
					arr[arrIndex(g)] = isPlaceholder(it, g) ? null : it;
				}
				boolean totemKept = false;
				for (int i = 0; i < arr.length; i++) {
					if (arr[i] == null || arr[i].getType() != Material.TOTEM_OF_UNDYING) continue;
					if (totemKept) arr[i] = null;             // only one totem of undying allowed per loadout
					else { arr[i].setAmount(1); totemKept = true; }
				}
				loadouts.set(p.getUniqueId(), arr);
				p.sendMessage(Utils.msg("<green>Saved your PvP loadout"));
			}
		} finally {
			writeMain(p, snap);
			p.setItemOnCursor(null); // don't let a held palette copy leak into the player's inventory
			p.updateInventory();
		}
	}

	/** The editor this player has open, or null. */
	private static EditorHolder holderOf(Player p) {
		return p.getOpenInventory().getTopInventory().getHolder() instanceof EditorHolder h ? h : null;
	}

	// ===== the player's inventory as the loadout's 36 slots =====
	/** A deep copy of the player's 36 inventory slots. The ONLY copy of their real items while an editor is open. */
	private static ItemStack[] takeInventory(Player p) {
		ItemStack[] out = new ItemStack[MAIN_SLOTS];
		for (int i = 0; i < MAIN_SLOTS; i++) {
			ItemStack it = p.getInventory().getItem(i);
			out[i] = it == null ? null : it.clone();
		}
		return out;
	}

	/** Write the first 36 entries of a 41-slot array (or a bare 36-slot snapshot) into the player's inventory. */
	private static void writeMain(Player p, ItemStack[] arr) {
		for (int i = 0; i < MAIN_SLOTS; i++) p.getInventory().setItem(i, arr != null && i < arr.length ? arr[i] : null);
		p.updateInventory();
	}

	/** Shift-click from the palette: a copy into the first free inventory slot. */
	private static void giveToLoadout(Player p, ItemStack copy) {
		for (int i = 0; i < MAIN_SLOTS; i++) {
			ItemStack at = p.getInventory().getItem(i);
			if (at == null || at.getType().isAir()) {
				p.getInventory().setItem(i, copy);
				p.updateInventory();
				return;
			}
		}
	}

	// ===== the one-totem limit =====
	/** Actions that move the cursor's stack INTO the clicked slot, i.e. the ones that can add a second totem. */
	private static boolean placesCursor(InventoryAction a) {
		return a == InventoryAction.PLACE_ALL || a == InventoryAction.PLACE_SOME
				|| a == InventoryAction.PLACE_ONE || a == InventoryAction.SWAP_WITH_CURSOR;
	}

	// True (and tells the player) when incoming is a totem and the loadout already holds one somewhere else; the
	// caller then refuses the move. The destination is excluded, since totem-for-totem leaves the count alone.
	private boolean refuseSecondTotem(Player p, Inventory gui, ItemStack incoming, int exceptMain, int exceptGui) {
		if (incoming == null || incoming.getType() != Material.TOTEM_OF_UNDYING) return false;
		int found = 0;
		for (int i = 0; i < MAIN_SLOTS; i++) {
			if (i == exceptMain) continue;
			ItemStack it = p.getInventory().getItem(i);
			if (it != null && it.getType() == Material.TOTEM_OF_UNDYING) found++;
		}
		for (int g = HELMET_SLOT; g <= OFFHAND_SLOT; g++) {
			if (g == exceptGui) continue;
			ItemStack it = gui.getItem(g);
			if (it != null && it.getType() == Material.TOTEM_OF_UNDYING) found++;
		}
		if (found < 1) return false;
		p.sendMessage(Utils.msg("<red>You are limited to only one totem"));
		return true;
	}

	// ===== helpers =====
	/** Palette = the SkyBlock "Items" catalog (weapons/armor/tools) plus a few vanilla PvP essentials.
	 *  Public so {@link PvpModule} can export it to the shared data folder for network-side editors. */
	public static List<ItemStack> palette() {
		List<ItemStack> out = new ArrayList<>(CreativeMenu.loadoutPalette());
		out.add(new ItemStack(Material.GOLDEN_CARROT, 64));
		out.add(new ItemStack(Material.WATER_BUCKET));
		out.add(new ItemStack(Material.TOTEM_OF_UNDYING));
		out.replaceAll(PvpLoadoutMenu::preEnchant);
		return out;
	}

	/**
	 * Pre-enchant a palette item with sensible max PvP enchants for its type (no-op for non-gear), then hand
	 * back the version whose LORE says so.
	 *
	 * <p>Enchanting a custom item out here left its lore describing the item before the enchants went on, so
	 * the palette offered a Claymore reading {@code Damage: +9} that was already carrying Sharpness VII - and
	 * a player who picked it kept that lore in their saved loadout. {@link ItemReloader#refreshItem} is the
	 * canonical rebuild and regenerates the lore from the enchantments now on the stack.
	 */
	private static ItemStack preEnchant(ItemStack it) {
		if (it == null) return null;
		String n = it.getType().name();
		if (n.endsWith("_SWORD") || n.endsWith("_AXE")) {
			it.addUnsafeEnchantment(Enchantment.SHARPNESS, 7);
			it.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
		} else if (it.getType() == Material.BOW || it.getType() == Material.CROSSBOW) {
			it.addUnsafeEnchantment(Enchantment.POWER, 7);
		} else if (n.endsWith("_PICKAXE")) {
			it.addUnsafeEnchantment(Enchantment.EFFICIENCY, 6);
			it.addUnsafeEnchantment(Enchantment.FORTUNE, 4);
		} else if (n.endsWith("_HELMET") || n.endsWith("_CHESTPLATE") || n.endsWith("_LEGGINGS") || n.endsWith("_BOOTS") || it.getType() == Material.ELYTRA) {
			it.addUnsafeEnchantment(Enchantment.PROTECTION, 5);
			if (n.endsWith("_BOOTS")) it.addUnsafeEnchantment(Enchantment.FEATHER_FALLING, 5);
		}
		ItemStack refreshed = ItemReloader.refreshItem(it);
		return refreshed != null ? refreshed : it;
	}

	/** The white pane that stands in for an empty armour / off-hand slot, named for what belongs there. */
	private static ItemStack placeholder(int gui) {
		return button(Material.WHITE_STAINED_GLASS_PANE, "<gray>" + slotName(gui));
	}

	private static ItemStack orPlaceholder(ItemStack it, int gui) {
		return it == null || it.getType().isAir() ? placeholder(gui) : it;
	}

	// Is this the placeholder pane for that slot rather than a real item? Built the same way every time, so an
	// exact match is the test - and a player cannot forge one, because the palette holds no white pane.
	private static boolean isPlaceholder(ItemStack it, int gui) {
		return it != null && it.isSimilar(placeholder(gui));
	}

	private static String slotName(int gui) {
		return switch (gui) {
			case HELMET_SLOT -> "Helmet";
			case CHEST_SLOT -> "Chestplate";
			case LEGS_SLOT -> "Leggings";
			case BOOTS_SLOT -> "Boots";
			case OFFHAND_SLOT -> "Off-hand";
			default -> " ";
		};
	}

	/**
	 * Row 5: the seam between the palette and the loadout.  It exists because the two halves of this window look
	 * identical and mean opposite things - above is a catalogue you copy FROM, below is the kit you are building -
	 * and a labelled bar between them is cheaper than explaining it in chat every time.
	 * <p>
	 * <b>Only the first line can be the NAME.</b>  An item display name is a single tooltip line and a newline in
	 * it does nothing, so the rule and the second label are lore - in the name's own colour, so the three still
	 * read as one block.
	 */
	private static void drawDivider(Inventory gui) {
		for (int g = DIVIDER_START; g <= DIVIDER_END; g++) gui.setItem(g, divider());
	}

	private static ItemStack divider() {
		return button(Material.GRAY_STAINED_GLASS_PANE, "<gray>▲ Item Palette", List.of("<gray>--------------------------", "<gray>▼ Your Armor & Inventory"));
	}

	private static ItemStack filler() {
		return button(Material.GRAY_STAINED_GLASS_PANE, " ");
	}

	private static ItemStack button(Material mat, String name) {
		ItemStack it = new ItemStack(mat);
		ItemMeta m = it.getItemMeta();
		if (m != null) {
			m.displayName(Utils.mm(name)); // Utils.mm suppresses the default item italic
			it.setItemMeta(m);
		}
		return it;
	}

	/**
	 * A button with lore.  Only the seam uses it, but it lives beside {@link #button(Material, String)} so both
	 * styles of item in this window are built the same way.
	 */
	private static ItemStack button(Material mat, String name, List<String> lore) {
		ItemStack it = button(mat, name);
		ItemMeta m = it.getItemMeta();
		if (m != null) {
			List<Component> rendered = new ArrayList<>();
			for (String line : lore) rendered.add(Utils.mm(line));
			m.lore(rendered);
			it.setItemMeta(m);
		}
		return it;
	}


	/** Marker holder carrying the editor's palette paging state. */
	public static final class EditorHolder implements InventoryHolder {
		final List<ItemStack> palette;
		int page;
		Inventory inv;

		EditorHolder(List<ItemStack> palette) {
			this.palette = palette;
		}

		@Override
		public Inventory getInventory() {
			return inv;
		}
	}
}
