package pvp;

import listeners.CreativeMenu;
import misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code /pvploadout} - a 54-slot chest editor for the player's single PvP loadout, laid out like a
 * real inventory (mirrors the network M7 loadout editor):
 *   row 1 (0-8)    : item palette (click to copy)
 *   row 2 (9-12)   : helmet, chestplate, leggings, boots | (13) off-hand | (14) trash | (15) prev | (16) next | (17) clear
 *   rows 3-5 (18-44): inventory storage
 *   row 6 (45-53)  : hotbar
 * GUI slots map to a 41-slot array via {@link #arrIndex(int)}; on close the editable slots are saved.
 * Nothing can be taken OUT of the menu (the trash slot deletes a held item). The command (declared in
 * plugin.yml) refuses unless 1v1 duels are enabled, so the menu only opens then.
 */
public final class PvpLoadoutMenu implements CommandExecutor, Listener {
	private static final int PALETTE_START = 0, PALETTE_COUNT = 9;
	private static final int OFFHAND_SLOT = 13, TRASH_SLOT = 14, PREV_SLOT = 15, NEXT_SLOT = 16, RESET_SLOT = 17;

	private final PvpConfig cfg;
	private final PvpLoadouts loadouts;

	public PvpLoadoutMenu(PvpConfig cfg, PvpLoadouts loadouts) {
		this.cfg = cfg;
		this.loadouts = loadouts;
	}

	/** GUI slot -> loadout array index (0-35 main, 36 helmet, 37 chest, 38 legs, 39 boots), or -1. */
	private static int arrIndex(int gui) {
		switch (gui) {
			case 9: return 36;   // helmet
			case 10: return 37;  // chestplate
			case 11: return 38;  // leggings
			case 12: return 39;  // boots
			case OFFHAND_SLOT: return 40; // off-hand
			default: // fall through
		}
		if (gui >= 18 && gui <= 26) return 9 + (gui - 18);   // storage row 1 -> arr 9..17
		if (gui >= 27 && gui <= 35) return 18 + (gui - 27);  // storage row 2 -> arr 18..26
		if (gui >= 36 && gui <= 44) return 27 + (gui - 36);  // storage row 3 -> arr 27..35
		if (gui >= 45 && gui <= 53) return gui - 45;         // hotbar       -> arr 0..8
		return -1;
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
		EditorHolder holder = new EditorHolder(palette());
		Inventory gui = Bukkit.createInventory(holder, 54, Utils.msg("<dark_gray>PvP Loadout"));
		holder.inv = gui;

		// First open (no saved loadout yet) starts from the default kit.
		ItemStack[] arr = loadouts.get(p.getUniqueId());
		if (arr == null) arr = DuelKit.defaultLoadout();
		for (int g = 0; g < 54; g++) {
			int idx = arrIndex(g);
			if (idx >= 0 && arr[idx] != null) gui.setItem(g, arr[idx]);
		}
		gui.setItem(TRASH_SLOT, button(Material.LAVA_BUCKET, "<red>Trash <gray>(click with an item to delete it)"));
		refreshPalette(gui, holder);

		p.openInventory(gui);
		p.sendMessage(Utils.msg("<gray>Editing your PvP loadout. Row 1 = palette (click to copy); row 2 = armor + off-hand + trash + page/reset; rows 3-5 = inventory; bottom = hotbar. Close to save."));
	}

	/** (Re)draw the palette items + page/clear buttons for the holder's current page. */
	private void refreshPalette(Inventory gui, EditorHolder holder) {
		List<ItemStack> pal = holder.palette;
		int pages = Math.max(1, (pal.size() + PALETTE_COUNT - 1) / PALETTE_COUNT);
		if (holder.page < 0) holder.page = 0;
		if (holder.page >= pages) holder.page = pages - 1;
		for (int i = 0; i < PALETTE_COUNT; i++) {
			int idx = holder.page * PALETTE_COUNT + i;
			gui.setItem(PALETTE_START + i, idx < pal.size() ? pal.get(idx).clone() : null);
		}
		gui.setItem(PREV_SLOT, holder.page > 0 ? button(Material.ARROW, "<yellow>Previous page") : filler());
		gui.setItem(NEXT_SLOT, holder.page < pages - 1 ? button(Material.ARROW, "<yellow>Next page") : filler());
		gui.setItem(RESET_SLOT, button(Material.BARRIER, "<red>Reset to default kit"));
	}

	// ===== events =====
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!(e.getView().getTopInventory().getHolder() instanceof EditorHolder holder)) return;
		int raw = e.getRawSlot();
		boolean top = raw < e.getView().getTopInventory().getSize();

		if (!top) {
			e.setCancelled(true); // lock the player's own inventory - nothing can be moved out of the editor
			return;
		}
		if (arrIndex(raw) >= 0) {
			// Editable slot: allow rearranging WITHIN the editor, but never let an item move out to the player.
			switch (e.getAction()) {
				case MOVE_TO_OTHER_INVENTORY, HOTBAR_SWAP, HOTBAR_MOVE_AND_READD, COLLECT_TO_CURSOR -> { e.setCancelled(true); return; }
				default -> { }
			}
			// One-totem limit: reject placing a totem when the loadout already holds one in another slot.
			ItemStack cur = e.getCursor();
			if (cur != null && cur.getType() == Material.TOTEM_OF_UNDYING && totemCount(holder.inv, raw) >= 1) {
				e.setCancelled(true);
				if (e.getWhoClicked() instanceof Player pl) pl.sendMessage(Utils.msg("<red>You are limited to only one totem"));
			}
			return;
		}

		e.setCancelled(true);
		if (isPalette(raw)) {
			ItemStack tmpl = e.getCurrentItem();
			ItemStack cursor = e.getCursor();
			if (tmpl != null && !tmpl.getType().isAir() && (cursor == null || cursor.getType().isAir())
					&& e.getWhoClicked() instanceof Player p) {
				p.setItemOnCursor(tmpl.clone());
			}
			return;
		}
		if (raw == PREV_SLOT) { holder.page--; refreshPalette(holder.inv, holder); return; }
		if (raw == NEXT_SLOT) { holder.page++; refreshPalette(holder.inv, holder); return; }
		if (raw == RESET_SLOT) {
			ItemStack[] def = DuelKit.defaultLoadout();
			for (int g = 0; g < 54; g++) {
				int idx = arrIndex(g);
				if (idx >= 0) holder.inv.setItem(g, def[idx]);
			}
			return;
		}
		if (raw == TRASH_SLOT && e.getWhoClicked() instanceof Player tp) {
			ItemStack held = tp.getItemOnCursor();
			if (held != null && !held.getType().isAir()) tp.setItemOnCursor(null); // delete the held item
		}
	}

	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		if (!(e.getView().getTopInventory().getHolder() instanceof EditorHolder holder)) return;
		int topSize = e.getView().getTopInventory().getSize();
		for (int raw : e.getRawSlots()) {
			if (raw >= topSize || arrIndex(raw) < 0) { // bottom inventory, or a non-editable top slot
				e.setCancelled(true);
				return;
			}
		}
		// One-totem limit: reject dragging a totem in when the loadout already holds one.
		ItemStack dragged = e.getOldCursor();
		if (dragged != null && dragged.getType() == Material.TOTEM_OF_UNDYING && totemCount(holder.inv, -1) >= 1) {
			e.setCancelled(true);
			if (e.getWhoClicked() instanceof Player pl) pl.sendMessage(Utils.msg("<red>You are limited to only one totem"));
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (!(e.getView().getTopInventory().getHolder() instanceof EditorHolder holder)) return;
		if (!(e.getPlayer() instanceof Player p)) return;
		ItemStack[] arr = new ItemStack[PvpLoadouts.SLOTS];
		boolean totemKept = false;
		for (int g = 0; g < 54; g++) {
			int idx = arrIndex(g);
			if (idx < 0) continue;
			ItemStack it = holder.inv.getItem(g);
			if (it != null && it.getType() == Material.TOTEM_OF_UNDYING) {
				if (totemKept) it = null;             // only one totem of undying allowed per loadout
				else { it.setAmount(1); totemKept = true; }
			}
			arr[idx] = it;
		}
		loadouts.set(p.getUniqueId(), arr);
		p.setItemOnCursor(null); // don't let a held palette copy leak into the player's inventory
		p.sendMessage(Utils.msg("<green>Saved your PvP loadout"));
	}

	// ===== helpers =====
	/** Palette = the SkyBlock "Items" catalog (weapons/armor/tools) plus a few vanilla PvP essentials. */
	private static List<ItemStack> palette() {
		List<ItemStack> out = new ArrayList<>(CreativeMenu.loadoutPalette());
		out.add(new ItemStack(Material.GOLDEN_CARROT, 64));
		out.add(new ItemStack(Material.WATER_BUCKET));
		out.add(new ItemStack(Material.TOTEM_OF_UNDYING));
		out.forEach(PvpLoadoutMenu::preEnchant);
		return out;
	}

	/** Pre-enchant a palette item with sensible max PvP enchants for its type (no-op for non-gear). */
	private static void preEnchant(ItemStack it) {
		if (it == null) return;
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
	}

	/** Totems of undying in the editable loadout slots, excluding GUI slot {@code excludeGui} (-1 = none). */
	private static int totemCount(Inventory inv, int excludeGui) {
		int c = 0;
		for (int g = 0; g < 54; g++) {
			if (g == excludeGui || arrIndex(g) < 0) continue;
			ItemStack it = inv.getItem(g);
			if (it != null && it.getType() == Material.TOTEM_OF_UNDYING) c++;
		}
		return c;
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
