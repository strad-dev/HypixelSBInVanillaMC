package pvp;

import listeners.ItemReloader;
import misc.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Keeps a SAVED loadout ({@link PvpLoadouts}) in step with current item definitions; saved items are frozen
 * copies, and only the palette is rebuilt ({@link PvpLoadoutMenu#palette()}) every open.
 *
 * Same job as the network's {@code pvp.PvpItemRefresh}, but with the real items: a SkyBlock item goes through
 * {@link ItemReloader#refreshItem}, which keeps enchants and stack size (and glint items free of real
 * enchants). Plain vanilla entries match a template by material, so {@code PvpLoadoutMenu.preEnchant}
 * changes reach them too. An item that resolves to nothing is LEFT AS IS, never deleted.
 */
public final class PvpItemRefresh {
	private PvpItemRefresh() {}

	/** Up-to-date 41-slot array (null if none saved) plus how many slots changed. */
	public record Result(ItemStack[] arr, int updated) {}

	/** Load, refresh every item, re-save if anything changed. */
	public static Result refreshSaved(PvpLoadouts loadouts, UUID uuid) {
		ItemStack[] arr = loadouts == null ? null : loadouts.get(uuid);
		if (arr == null) return new Result(null, 0);
		int updated = refreshAll(arr);
		if (updated > 0) loadouts.set(uuid, arr);
		return new Result(arr, updated);
	}

	/** Refresh a 41-slot array in place; returns slots changed. */
	public static int refreshAll(ItemStack[] arr) {
		if (arr == null) return 0;
		Map<String, ItemStack> templates = templates();
		int changed = 0;
		for (int i = 0; i < arr.length; i++) {
			ItemStack fresh = refresh(arr[i], templates);
			if (fresh != null) {
				arr[i] = fresh;
				changed++;
			}
		}
		return changed;
	}

	/** Current version of a saved item, or null if already current or it resolves to nothing. */
	public static ItemStack refresh(ItemStack saved, Map<String, ItemStack> templates) {
		if (saved == null || saved.getType().isAir()) return null;
		ItemStack fresh = ItemReloader.refreshItem(saved); // custom item: rebuilt from its getItem()
		if (fresh == null) {
			ItemStack tmpl = templates.get(key(saved));    // vanilla: palette's current version
			if (tmpl == null) return null;
			fresh = tmpl.clone();
			fresh.addUnsafeEnchantments(saved.getEnchantments()); // player's enchants win, as in refreshItem
			fresh.setAmount(saved.getAmount());
		}
		ItemReloader.modifyVanillaArmor(fresh); // no-op for skyblock/ items; same fixup as login
		// isSimilar skips amount, copied above, so this is a full compare.
		return saved.isSimilar(fresh) ? null : fresh;
	}

	/** Current version of every loadout item by {@link #key}: palette first, default kit fills the rest. */
	private static Map<String, ItemStack> templates() {
		Map<String, ItemStack> out = new HashMap<>();
		for (ItemStack it : PvpLoadoutMenu.palette()) put(out, it);
		for (ItemStack it : DuelKit.defaultLoadout()) put(out, it);
		return out;
	}

	private static void put(Map<String, ItemStack> out, ItemStack it) {
		String k = key(it);
		if (k != null) out.putIfAbsent(k, it);
	}

	/** Template key: custom-item ID (first lore line), else material. Null for an empty slot. */
	public static String key(ItemStack it) {
		if (it == null || it.getType().isAir()) return null;
		String id = it.hasItemMeta() ? Utils.firstLorePlain(it.getItemMeta()).trim() : "";
		return id.startsWith("skyblock/") ? id : "material:" + it.getType().name();
	}
}
