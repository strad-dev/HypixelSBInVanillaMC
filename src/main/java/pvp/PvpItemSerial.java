package pvp;

import org.bukkit.inventory.ItemStack;

import java.util.Base64;

/**
 * Base64 of Paper's {@link ItemStack#serializeAsBytes()} - round-trips custom-item lore IDs,
 * enchants, attributes, etc. cleanly. A null/air slot serialises to {@code null}.
 */
public final class PvpItemSerial {
	private PvpItemSerial() {}

	public static String toB64(ItemStack item) {
		if (item == null || item.getType().isAir()) return null;
		return Base64.getEncoder().encodeToString(item.serializeAsBytes());
	}

	public static ItemStack fromB64(String s) {
		if (s == null) return null;
		try {
			return ItemStack.deserializeBytes(Base64.getDecoder().decode(s));
		} catch (Exception e) {
			return null;
		}
	}
}
