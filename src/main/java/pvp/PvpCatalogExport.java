package pvp;

import org.bukkit.inventory.ItemStack;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Exports the duel item palette + default kit to a shared JSON file ({@code pvp-item-catalog.json}) so
 * a server WITHOUT SkyBlock (i.e. the network plugin on the lobby, etc.) can offer the exact same
 * selectable items in its own {@code /pvploadout} editor. Only written when the network catalog path is
 * configured ({@code pvp.duel.catalog-file}); a standalone server exports nothing.
 *
 * On-disk shape: {@code { "palette": [b64...], "defaultKit": [41 b64 slots] }} - the same Base64 of
 * {@link ItemStack#serializeAsBytes()} used everywhere else, so the network reads it with no translation.
 */
public final class PvpCatalogExport {
	private PvpCatalogExport() {}

	public static void write(Path file, List<ItemStack> palette, ItemStack[] defaultKit) {
		Data d = new Data();
		for (ItemStack it : palette) {
			String b64 = PvpItemSerial.toB64(it);
			if (b64 != null) d.palette.add(b64);
		}
		for (int i = 0; i < PvpLoadouts.SLOTS; i++) {
			d.defaultKit.add(PvpItemSerial.toB64(defaultKit != null && i < defaultKit.length ? defaultKit[i] : null));
		}
		PvpJson.save(file, d);
	}

	/** On-disk shape (field names must match the network's PvpCatalog reader). */
	public static final class Data {
		public List<String> palette = new ArrayList<>();
		public List<String> defaultKit = new ArrayList<>();
	}
}
