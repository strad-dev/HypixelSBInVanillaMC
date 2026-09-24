package pvp;

import org.bukkit.inventory.ItemStack;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Exports palette + default kit to shared {@code pvp-item-catalog.json} so servers WITHOUT SkyBlock (the
 * network plugin on lobby etc.) offer the same items in their {@code /pvploadout}. Only with
 * {@code pvp.duel.catalog-file} set.
 *
 * Shape: {@code { "palette": [b64...], "defaultKit": [41 b64 slots], "ffaEnabled": bool,
 * "duelEnabled": bool }}, b64 = Base64 of {@link ItemStack#serializeAsBytes()} as everywhere else. The flags
 * let the network's {@code /pvptop} gate each board on whether SkyBlock runs that mode.
 */
public final class PvpCatalogExport {
	private PvpCatalogExport() {}

	public static void write(Path file, List<ItemStack> palette, ItemStack[] defaultKit,
			boolean ffaEnabled, boolean duelEnabled) {
		Data d = new Data();
		for (ItemStack it : palette) {
			String b64 = PvpItemSerial.toB64(it);
			if (b64 != null) d.palette.add(b64);
		}
		for (int i = 0; i < PvpLoadouts.SLOTS; i++) {
			d.defaultKit.add(PvpItemSerial.toB64(defaultKit != null && i < defaultKit.length ? defaultKit[i] : null));
		}
		d.ffaEnabled = ffaEnabled;
		d.duelEnabled = duelEnabled;
		PvpJson.save(file, d);
	}

	/** Field names must match the network's PvpCatalog reader. */
	public static final class Data {
		public List<String> palette = new ArrayList<>();
		public List<String> defaultKit = new ArrayList<>();
		public boolean ffaEnabled;
		public boolean duelEnabled;
	}
}
