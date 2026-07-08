package pvp;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Per-player PvP loadout storage. One saved loadout per player. The file location is configurable
 * ({@code pvp.duel.loadouts-file}): by default it is this plugin's own folder
 * ({@code plugins/SkyBlock/pvp-loadouts.json}, per-server, standalone), but on the network it can point
 * at the shared {@code ~/data/pvp-loadouts.json} so a loadout follows the player across servers.
 *
 * Reads/writes go straight to disk (no in-memory cache) so an edit made on another server - e.g. via
 * the network's own {@code /pvploadout} editor writing the shared file - is seen here the next time a
 * duel starts. Writes are read-modify-write (load, change one player, atomic save).
 *
 * 41-slot layout (the convention the editor GUI maps to): [0..35] main inventory, [36] helmet,
 * [37] chestplate, [38] leggings, [39] boots, [40] off-hand.
 */
public final class PvpLoadouts {
	public static final int SLOTS = 41;

	private final Path file;

	public PvpLoadouts(Path file) {
		this.file = file;
	}

	private Data load() {
		Data d = PvpJson.load(file, Data.class, new Data());
		if (d.players == null) d.players = new HashMap<>();
		return d;
	}

	public boolean has(UUID uuid) {
		return load().players.containsKey(uuid.toString());
	}

	/** This player's saved 41-slot loadout, or null if they've never saved one. */
	public ItemStack[] get(UUID uuid) {
		List<String> ser = load().players.get(uuid.toString());
		return ser == null ? null : fromSer(ser);
	}

	public void set(UUID uuid, ItemStack[] arr) {
		Data data = load();
		data.players.put(uuid.toString(), toSer(arr));
		PvpJson.save(file, data);
	}

	public void clear(UUID uuid) {
		Data data = load();
		if (data.players.remove(uuid.toString()) != null) PvpJson.save(file, data);
	}

	// ===== array <-> json =====
	private static List<String> toSer(ItemStack[] arr) {
		List<String> out = new ArrayList<>(SLOTS);
		for (int i = 0; i < SLOTS; i++) out.add(PvpItemSerial.toB64(arr != null && i < arr.length ? arr[i] : null));
		return out;
	}

	private static ItemStack[] fromSer(List<String> ser) {
		ItemStack[] arr = new ItemStack[SLOTS];
		if (ser != null) for (int i = 0; i < SLOTS && i < ser.size(); i++) arr[i] = PvpItemSerial.fromB64(ser.get(i));
		return arr;
	}

	/** Equip a player with a 41-slot loadout array, replacing their inventory. Call this at duel start. */
	public static void apply(Player p, ItemStack[] arr) {
		PlayerInventory inv = p.getInventory();
		for (int i = 0; i < 36; i++) inv.setItem(i, arr[i]);
		inv.setHelmet(arr[36]);
		inv.setChestplate(arr[37]);
		inv.setLeggings(arr[38]);
		inv.setBoots(arr[39]);
		inv.setItemInOffHand(arr[40] == null ? new ItemStack(Material.AIR) : arr[40]);
		p.updateInventory();
	}

	/** On-disk shape: uuid string -> 41 base64 slots (null = empty). */
	public static class Data {
		public Map<String, List<String>> players = new HashMap<>();
	}
}
