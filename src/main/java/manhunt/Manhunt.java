package manhunt;

import items.misc.ManhuntCompass;
import items.weapons.ManhuntHyperion;
import items.weapons.Scylla;
import misc.Plugin;
import misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pvp.PvpJson;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manhunt: a few Speedrunners beat the game while everyone else hunts them. Off unless {@code manhunt: true}.
 * Teams, match flag and ceilings persist in {@code manhunt.json}, so a disconnect or restart mid-match keeps
 * a player's side and rung.
 */
public final class Manhunt {
	private Manhunt() {}

	private static boolean enabled;
	private static boolean started;

	/** In add order. Order is load-bearing: a compass stores an index into this list. */
	private static final List<UUID> SPEEDRUNNERS = new ArrayList<>();

	/** Last seen name per Speedrunner, for the teams list and compass lore while they're offline. */
	private static final Map<UUID, String> NAMES = new LinkedHashMap<>();

	/**
	 * Highest Hyperion rung each player has held this match; sets their intelligence cap and regen. Only goes
	 * up, even if the sword is lost, until death drops them back to the Stick. Never refunds mana.
	 */
	private static final Map<UUID, Integer> CEILINGS = new HashMap<>();

	/** Kitted this match. Without it, stashing a Hyperion and relogging printed a new kit every time. */
	private static final Set<UUID> KITTED = new HashSet<>();

	/** Each world's {@code locatorBar} at match start, keyed on world name so it survives a restart. */
	private static final Map<String, Boolean> LOCATOR_BARS = new LinkedHashMap<>();

	/** Rung above Netherite: a real Hyperion, which has no {@link ManhuntTier}. */
	public static final int FULL_RUNG = ManhuntTier.values().length;

	/** Null until {@link #load}, so always null while Manhunt is off. */
	private static Path file;

	public static void setEnabled(boolean on) {
		enabled = on;
	}

	/**
	 * Loads state from disk; saved again on every change. Run once from {@link ManhuntModule#enable} before
	 * anyone joins. Roster is keyed on UUID, so a rejoin needs no extra hook.
	 */
	static void load(JavaPlugin plugin) {
		file = plugin.getDataFolder().toPath().resolve("manhunt.json");
		State state = PvpJson.load(file, State.class, null);
		if(state == null) return;

		started = state.started;
		SPEEDRUNNERS.clear();
		NAMES.clear();
		CEILINGS.clear();
		KITTED.clear();
		// List, walked in order: a compass stores an index into it.
		if(state.speedrunners != null) {
			for(String id : state.speedrunners) {
				UUID uuid = parse(id);
				if(uuid != null && !SPEEDRUNNERS.contains(uuid)) SPEEDRUNNERS.add(uuid);
			}
		}
		if(state.names != null) {
			for(Map.Entry<String, String> entry : state.names.entrySet()) {
				UUID uuid = parse(entry.getKey());
				if(uuid != null) NAMES.put(uuid, entry.getValue());
			}
		}
		if(state.ceilings != null) {
			for(Map.Entry<String, Integer> entry : state.ceilings.entrySet()) {
				UUID uuid = parse(entry.getKey());
				if(uuid != null) CEILINGS.put(uuid, entry.getValue());
			}
		}
		if(state.kitted != null) {
			for(String id : state.kitted) {
				UUID uuid = parse(id);
				if(uuid != null) KITTED.add(uuid);
			}
		}
		LOCATOR_BARS.clear();
		if(state.locatorBars != null) LOCATOR_BARS.putAll(state.locatorBars);
		// Gamerule is already off in level.dat, but this covers a world that wasn't loaded at match start.
		// save() so a second restart can restore that new world too.
		if(started) {
			hideLocatorBar();
			save();
		}
	}

	/** Called on every change. All rare (a ceiling moves on upgrade or death), so no batching. */
	private static void save() {
		if(file == null) return;
		State state = new State();
		state.started = started;
		state.speedrunners = new ArrayList<>();
		for(UUID uuid : SPEEDRUNNERS) {
			state.speedrunners.add(uuid.toString());
		}
		state.names = new LinkedHashMap<>();
		for(Map.Entry<UUID, String> entry : NAMES.entrySet()) {
			state.names.put(entry.getKey().toString(), entry.getValue());
		}
		state.ceilings = new LinkedHashMap<>();
		for(Map.Entry<UUID, Integer> entry : CEILINGS.entrySet()) {
			state.ceilings.put(entry.getKey().toString(), entry.getValue());
		}
		state.kitted = new ArrayList<>();
		for(UUID uuid : KITTED) {
			state.kitted.add(uuid.toString());
		}
		state.locatorBars = new LinkedHashMap<>(LOCATOR_BARS);
		PvpJson.save(file, state);
	}

	@Nullable
	private static UUID parse(String id) {
		try {
			return UUID.fromString(id);
		} catch(IllegalArgumentException exception) {
			return null;
		}
	}

	/** File shape. Gson fills by name; anything missing stays null. */
	private static final class State {
		boolean started;
		List<String> speedrunners;
		Map<String, String> names;
		Map<String, Integer> ceilings;
		List<String> kitted;
		Map<String, Boolean> locatorBars;
	}

	public static boolean enabled() {
		return enabled;
	}

	public static boolean started() {
		return started;
	}

	/**
	 * Not the same as {@link #enabled()}: enabled gives the command, items and recipes, but the mana rules only
	 * apply between {@code /manhunt start} and {@code reset}. Outside a match it's the standard 80 ticks and 2500.
	 */
	public static boolean active() {
		return enabled && started;
	}

	// ---- teams ----------------------------------------------------------------------------------------

	public static List<UUID> speedrunners() {
		return List.copyOf(SPEEDRUNNERS);
	}

	public static boolean isSpeedrunner(Player p) {
		return SPEEDRUNNERS.contains(p.getUniqueId());
	}

	/** Hunters are everyone not a Speedrunner, so this also works for a late joiner never assigned a team. */
	public static boolean opposingTeams(Player a, Player b) {
		return isSpeedrunner(a) != isSpeedrunner(b);
	}

	public static List<Player> onlineHunters() {
		List<Player> out = new ArrayList<>();
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(!isSpeedrunner(p)) out.add(p);
		}
		return out;
	}

	public static List<Player> onlineSpeedrunners() {
		List<Player> out = new ArrayList<>();
		for(UUID uuid : SPEEDRUNNERS) {
			Player p = Bukkit.getPlayer(uuid);
			if(p != null) out.add(p);
		}
		return out;
	}

	/** Wraps rather than rejects: removing a Speedrunner shortens the list under compasses pointed past them. */
	private static int index(int i) {
		return SPEEDRUNNERS.isEmpty() ? -1 : Math.floorMod(i, SPEEDRUNNERS.size());
	}

	/** Null if offline. */
	@Nullable
	public static Player speedrunnerAt(int i) {
		int at = index(i);
		return at < 0 ? null : Bukkit.getPlayer(SPEEDRUNNERS.get(at));
	}

	/** Online or not, for lore and chat. */
	public static String speedrunnerName(int i) {
		int at = index(i);
		return at < 0 ? "nobody" : NAMES.getOrDefault(SPEEDRUNNERS.get(at), "unknown");
	}

	/** Mid-match they lose their compasses. False if already a Speedrunner. */
	public static boolean addSpeedrunner(OfflinePlayer target) {
		if(SPEEDRUNNERS.contains(target.getUniqueId())) return false;
		SPEEDRUNNERS.add(target.getUniqueId());
		if(target.getName() != null) NAMES.put(target.getUniqueId(), target.getName());

		Player online = target.getPlayer();
		if(started && online != null) {
			removeManhuntItems(online, false, true);
		}
		save();
		return true;
	}

	/** Back to Hunter, with a compass mid-match. False if not a Speedrunner. */
	public static boolean removeSpeedrunner(OfflinePlayer target) {
		if(!SPEEDRUNNERS.remove(target.getUniqueId())) return false;
		NAMES.remove(target.getUniqueId());

		Player online = target.getPlayer();
		if(started && online != null && !hasCompass(online)) {
			give(online, ManhuntCompass.getItem());
		}
		save();
		return true;
	}

	/** Refreshes the name on join; the roster outlives restarts, so the stored one can be stale. */
	public static void noteName(Player p) {
		if(!isSpeedrunner(p)) return;
		String previous = NAMES.put(p.getUniqueId(), p.getName());
		if(!p.getName().equals(previous)) save();
	}

	// ---- match ----------------------------------------------------------------------------------------

	/**
	 * Everyone gets a Stick Hyperion, Hunters a compass, and every ceiling resets, so a match opens on 200
	 * ticks and 250. Mana is zeroed (banked regen ticks too), not left to passiveIntel's clamp, which would let
	 * anyone on 2500 start with 250. Locator bar goes off everywhere ({@link #reset} restores it). Loaded
	 * piglin brutes become piglins, permanently: see {@link ManhuntPiglins#demote}.
	 */
	public static void start() {
		started = true;
		CEILINGS.clear();
		KITTED.clear();
		LAST_IMPLOSION.clear();
		hideLocatorBar();
		ManhuntPiglins.demoteLoaded();
		for(Player p : Bukkit.getOnlinePlayers()) {
			Plugin.zeroIntelligence(p);
			equip(p);
		}
		save();
	}

	/**
	 * Takes back every Manhunt Hyperion and compass; teams stay, so a rematch is one command. Ceilings clear,
	 * but with {@link #active} false everyone plays on 80 ticks and 2500 anyway. Locator bars go back to what
	 * they were.
	 */
	public static void reset() {
		started = false;
		CEILINGS.clear();
		KITTED.clear();
		LAST_IMPLOSION.clear();
		restoreLocatorBar();
		for(Player p : Bukkit.getOnlinePlayers()) {
			removeManhuntItems(p, true, true);
		}
		save();
	}

	/**
	 * Loaded worlds only; Paper keeps all three dimensions up, but a world loaded mid-match is missed.
	 * {@code putIfAbsent} so a second {@code start} doesn't record our own off as the original.
	 */
	private static void hideLocatorBar() {
		for(World world : Bukkit.getWorlds()) {
			LOCATOR_BARS.putIfAbsent(world.getName(), Boolean.TRUE.equals(world.getGameRuleValue(GameRule.LOCATOR_BAR)));
			world.setGameRule(GameRule.LOCATOR_BAR, false);
		}
	}

	/** Skips unloaded worlds: their level.dat hasn't been touched since the match started. */
	private static void restoreLocatorBar() {
		for(Map.Entry<String, Boolean> entry : LOCATOR_BARS.entrySet()) {
			World world = Bukkit.getWorld(entry.getKey());
			if(world != null) world.setGameRule(GameRule.LOCATOR_BAR, entry.getValue());
		}
		LOCATOR_BARS.clear();
	}

	/** Gives the team kit, skipping anything already held. Run on start, respawn and late join. */
	public static void equip(Player p) {
		if(!started) return;
		KITTED.add(p.getUniqueId());
		// A real Hyperion doesn't count: it's the ladder's reward, not a rung. Gating on bestRungIn left anyone
		// carrying one with no Manhunt Hyperion. It still sets their ceiling via observe().
		if(!hasManhuntHyperion(p)) {
			give(p, ManhuntHyperion.getItem(ManhuntTier.BASE));
		}
		if(!isSpeedrunner(p) && !hasCompass(p)) {
			give(p, ManhuntCompass.getItem());
		}
		save();
	}

	/**
	 * Once per match: a late joiner gets a kit, a rejoin gets nothing. Losing a Hyperion is meant to cost, and
	 * rejoining used to hand out another. Respawn still calls {@link #equip} directly.
	 */
	public static void equipOnJoin(Player p) {
		if(!started || KITTED.contains(p.getUniqueId())) return;
		equip(p);
	}

	private static void give(Player p, ItemStack item) {
		Map<Integer, ItemStack> left = p.getInventory().addItem(item);
		for(ItemStack over : left.values()) {
			p.getWorld().dropItemNaturally(p.getLocation(), over);
		}
	}

	/** Includes the cursor. */
	public static void removeManhuntItems(Player p, boolean hyperions, boolean compasses) {
		Inventory inventory = p.getInventory();
		for(int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if(item == null) continue;
			if((hyperions && ManhuntTier.of(item) != null) || (compasses && isCompass(item))) {
				inventory.setItem(i, null);
			}
		}
		ItemStack cursor = p.getItemOnCursor();
		if((hyperions && ManhuntTier.of(cursor) != null) || (compasses && isCompass(cursor))) {
			p.setItemOnCursor(null);
		}
	}

	/** A full Hyperion doesn't count. */
	private static boolean hasManhuntHyperion(Player p) {
		for(ItemStack item : p.getInventory().getContents()) {
			if(ManhuntTier.of(item) != null) return true;
		}
		return false;
	}

	private static boolean hasCompass(Player p) {
		for(ItemStack item : p.getInventory().getContents()) {
			if(isCompass(item)) return true;
		}
		return false;
	}

	public static boolean isCompass(@Nullable ItemStack item) {
		if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) return false;
		return ManhuntCompass.ID.equals(Utils.firstLorePlain(item.getItemMeta()));
	}

	// ---- implosion ------------------------------------------------------------------------------------

	/**
	 * Implosion immunity after one lands. The implosion has no real cooldown, so four Hyperions deleted anyone
	 * instantly. Shared across all attackers, so piling on stops paying.
	 */
	public static final long IMPLOSION_IMMUNITY_TICKS = 20;

	/** Tick of each player's last implosion. Not persisted. */
	private static final Map<UUID, Long> LAST_IMPLOSION = new HashMap<>();

	/**
	 * Tests AND claims the window, so call once per target per implosion. Players only, either team; mobs are
	 * exempt. Claimed on the attempt, so a blow soaked to 0 still spends it.
	 */
	public static boolean claimImplosion(LivingEntity target) {
		if(!(target instanceof Player victim)) return true;
		long now = Bukkit.getCurrentTick();
		Long last = LAST_IMPLOSION.get(victim.getUniqueId());
		if(last != null && now - last < IMPLOSION_IMMUNITY_TICKS) return false;
		LAST_IMPLOSION.put(victim.getUniqueId(), now);
		return true;
	}

	// ---- intelligence ---------------------------------------------------------------------------------

	/**
	 * Raises the ceiling to the best Hyperion carried. Runs every tick off the intelligence loop, which covers
	 * craft, pickup and chest without a hook each. Cheap: material is checked before lore.
	 */
	public static void observe(Player p) {
		if(!active()) return;
		// Death drop isn't always out of the inventory yet; a corpse would re-raise what onDeath just cleared.
		if(p.isDead()) return;
		int best = bestRungIn(p.getInventory());
		if(best > rung(p)) {
			CEILINGS.put(p.getUniqueId(), best);
			save();
		}
	}

	/** Back to the Stick, mana and banked regen ticks zeroed. */
	public static void onDeath(Player p) {
		if(!active()) return;
		if(CEILINGS.remove(p.getUniqueId()) != null) save();
		Plugin.zeroIntelligence(p);
	}

	/** High-water mark, Stick at worst. */
	private static int rung(Player p) {
		return CEILINGS.getOrDefault(p.getUniqueId(), 0);
	}

	/** {@link #FULL_RUNG} for a real Hyperion, -1 for none. */
	private static int bestRungIn(Inventory inventory) {
		int best = -1;
		for(ItemStack item : inventory.getContents()) {
			if(item == null) continue;
			if(item.getType() == ManhuntTier.NETHERITE.material() && Scylla.isScylla(item)) {
				return FULL_RUNG;
			}
			ManhuntTier tier = ManhuntTier.of(item);
			if(tier != null && tier.ordinal() > best) best = tier.ordinal();
		}
		return best;
	}

	public static int maxIntelligence(Player p) {
		if(!active()) return ManhuntTier.FULL_MAX_INTELLIGENCE;
		int rung = rung(p);
		return rung >= FULL_RUNG ? ManhuntTier.FULL_MAX_INTELLIGENCE : ManhuntTier.values()[rung].maxIntelligence();
	}

	public static int ticksPerMana(Player p) {
		if(!active()) return ManhuntTier.FULL_TICKS_PER_MANA;
		int rung = rung(p);
		return rung >= FULL_RUNG ? ManhuntTier.FULL_TICKS_PER_MANA : ManhuntTier.values()[rung].ticksPerMana();
	}
}
