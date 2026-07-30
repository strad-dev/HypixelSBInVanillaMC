package pvp;

import listeners.ItemReloader;
import misc.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Keeps a SAVED PvP loadout ({@link PvpLoadouts}) in step with the current item definitions. A saved
 * loadout holds frozen item copies from whenever the player picked them, so an item change (new
 * lore/stats/attributes/ability text) would never reach it - only the editor's row 1 is inherently
 * current, being rebuilt from {@link PvpLoadoutMenu#palette()} on every open.
 *
 * Same job as the network plugin's {@code pvp.PvpItemRefresh}, but this side has the real items, so a
 * SkyBlock item is rebuilt through {@link ItemReloader#refreshItem} - the canonical "give me the
 * current version of this item" path, which already preserves the player's enchants and stack size
 * (and correctly keeps glint-override items free of real enchants). Only the plain vanilla palette
 * entries fall back to matching a template by material, so a change to
 * {@code PvpLoadoutMenu.preEnchant} reaches them too.
 *
 * An item that resolves to nothing is LEFT EXACTLY AS IT IS, never deleted.
 */
public final class PvpItemRefresh {
	private PvpItemRefresh() {}

	/** A refreshed loadout: the up-to-date 41-slot array (null if the player has none saved) plus
	 *  how many slots the refresh actually changed. */
	public record Result(ItemStack[] arr, int updated) {}

	/** Load a player's saved loadout, bring every item up to date, and re-save it if anything changed. */
	public static Result refreshSaved(PvpLoadouts loadouts, UUID uuid) {
		ItemStack[] arr = loadouts == null ? null : loadouts.get(uuid);
		if (arr == null) return new Result(null, 0);
		int updated = refreshAll(arr);
		if (updated > 0) loadouts.set(uuid, arr);
		return new Result(arr, updated);
	}

	/** Refresh every slot of a 41-slot loadout array in place; returns how many slots changed. */
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

	/** The current version of one saved item, or null if it is already current or resolves to nothing. */
	public static ItemStack refresh(ItemStack saved, Map<String, ItemStack> templates) {
		if (saved == null || saved.getType().isAir()) return null;
		ItemStack fresh = ItemReloader.refreshItem(saved); // custom item: rebuilt from its own getItem()
		if (fresh == null) {
			ItemStack tmpl = templates.get(key(saved));    // vanilla entry: the palette's current version
			if (tmpl == null) return null;
			fresh = tmpl.clone();
			fresh.addUnsafeEnchantments(saved.getEnchantments()); // the player's enchants win, as in refreshItem
			fresh.setAmount(saved.getAmount());
		}
		ItemReloader.modifyVanillaArmor(fresh); // no-op for a skyblock/ item; the same fixup login does
		// isSimilar compares everything but the amount, which was copied above - so this is a full compare.
		return saved.isSimilar(fresh) ? null : fresh;
	}

	/** Current version of every item a loadout can hold, keyed by {@link #key}: the palette (the
	 *  canonical version) first, the default kit filling in anything the palette doesn't offer. */
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

	/** Item identity for template matching: the custom-item ID (first lore line) if it has one,
	 *  otherwise the material. Null for an empty slot. */
	public static String key(ItemStack it) {
		if (it == null || it.getType().isAir()) return null;
		String id = it.hasItemMeta() ? Utils.firstLorePlain(it.getItemMeta()).trim() : "";
		return id.startsWith("skyblock/") ? id : "material:" + it.getType().name();
	}
}
