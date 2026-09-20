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
 * Minecraft Manhunt: a handful of Speedrunners running the game while everybody else hunts them. Off unless
 * {@code manhunt: true} in the config, at which point {@code /manhunt} appears and the intelligence rules
 * below take over.
 *
 * <p>The teams, the match flag and the intelligence ceilings are kept in {@code manhunt.json} in the plugin
 * folder, so nobody loses their side by dropping out: a Speedrunner who disconnects - or who is thrown off by
 * a restart mid-match - is still a Speedrunner when they come back, and still on the rung they had reached.
 */
public final class Manhunt {
	private Manhunt() {}

	private static boolean enabled;
	private static boolean started;

	/**
	 * The Speedrunners, in the order they were added - <b>the order is load-bearing</b>: a Manhunt Compass
	 * stores an index into this list, so Change Target walks it in a fixed order.
	 */
	private static final List<UUID> SPEEDRUNNERS = new ArrayList<>();

	/** Last seen name per Speedrunner, so the teams list and compass lore read properly for offline ones. */
	private static final Map<UUID, String> NAMES = new LinkedHashMap<>();

	/**
	 * Highest rung each player has held a Hyperion of this match, which is what their intelligence ceiling
	 * and regen rate are read off. It only ever goes <b>up</b> - upgrade and the ceiling stays there even if
	 * the sword is lost - until they die, when the whole entry goes and they are back on the Stick's numbers.
	 * Recovering a Hyperion off a body raises it again, but nothing here ever hands mana back.
	 */
	private static final Map<UUID, Integer> CEILINGS = new HashMap<>();

	/**
	 * Everybody who has already been handed a kit this match. A rejoin is <b>not</b> a fresh kit: without
	 * this, stashing a Hyperion in an ender chest (or handing it to somebody) and relogging paid out another
	 * bottom-rung one, and another compass, as often as a player cared to do it.
	 */
	private static final Set<UUID> KITTED = new HashSet<>();

	/**
	 * Every world's {@code locatorBar} gamerule as it was when the match started, keyed on world NAME so it
	 * can be put back after a restart mid-match. Empty outside a match.
	 */
	private static final Map<String, Boolean> LOCATOR_BARS = new LinkedHashMap<>();

	/** The rung above Netherite: a real Hyperion, which has no {@link ManhuntTier} of its own. */
	public static final int FULL_RUNG = ManhuntTier.values().length;

	/** Where the three maps above are kept between sittings. Null until {@link #load} - so, while off, never. */
	private static Path file;

	public static void setEnabled(boolean on) {
		enabled = on;
	}

	/**
	 * Reads the teams, the match flag and the ceilings back off disk, and from here on writes them out again
	 * on every change. Run once from {@link ManhuntModule#enable}, before anybody can have joined.
	 *
	 * <p>Nothing else needs a hook for a rejoin: the roster is keyed on UUID, so surviving the restart is the
	 * whole of it - {@code equip} on join then sees a Speedrunner and hands out no compass, and
	 * {@link #maxIntelligence} finds their rung where they left it.
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
		// A list, not a set, and walked in order: a compass stores an index into it.
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
		// Keyed on world name rather than UUID, so these come back verbatim.
		LOCATOR_BARS.clear();
		if(state.locatorBars != null) LOCATOR_BARS.putAll(state.locatorBars);
		// Restarting mid-match must not hand the Hunters the bar back. The gamerule is in level.dat and so
		// is already off, but this also covers a world that was not around when the match started.
		// save() because hideLocatorBar may have picked up a world the file has never seen; without it a
		// second restart would have nothing to put that one back to.
		if(started) {
			hideLocatorBar();
			save();
		}
	}

	/**
	 * Writes the lot out. Called on every change to the teams, the match flag or a ceiling - all of them
	 * rare, so there is nothing to batch: a ceiling moves on an upgrade or a death, not on the tick loop.
	 */
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

	/** The file's shape. Gson fills these by name, so anything the file is missing simply stays null. */
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
	 * Whether the Manhunt rules are in force right now. <b>Not the same as {@link #enabled()}</b>: with
	 * {@code manhunt: true} the command, the items and the upgrade recipes all exist, but the intelligence
	 * rules and the players-only mana rule only apply between {@code /manhunt start} and
	 * {@code /manhunt reset}. Outside a match everybody is back on the standard 80 ticks and 2500.
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

	/**
	 * Whether these two are on opposite sides of the Manhunt. Hunters are defined as everyone who is not a
	 * Speedrunner, so a team is one boolean and this is one comparison rather than a roster lookup - which
	 * also means it answers correctly for a player who joined mid-match and was never assigned anything.
	 */
	public static boolean opposingTeams(Player a, Player b) {
		return isSpeedrunner(a) != isSpeedrunner(b);
	}

	/** Everyone online who is not a Speedrunner. Hunters are the default, so this is a subtraction. */
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

	/**
	 * Wraps a compass's stored index into the list. Wrapping rather than rejecting, because taking somebody
	 * off the Speedrunner team shortens the list under every compass already pointed past them - they should
	 * fall back onto a real Speedrunner, not onto nobody.
	 */
	private static int index(int i) {
		return SPEEDRUNNERS.isEmpty() ? -1 : Math.floorMod(i, SPEEDRUNNERS.size());
	}

	/** The Speedrunner a compass pointed at index {@code i} is after, or null if they are not online. */
	@Nullable
	public static Player speedrunnerAt(int i) {
		int at = index(i);
		return at < 0 ? null : Bukkit.getPlayer(SPEEDRUNNERS.get(at));
	}

	/** A name for the Speedrunner at {@code i}, online or not, for lore and chat. */
	public static String speedrunnerName(int i) {
		int at = index(i);
		return at < 0 ? "nobody" : NAMES.getOrDefault(SPEEDRUNNERS.get(at), "unknown");
	}

	/**
	 * Puts {@code target} on the Speedrunner team. Mid-match they lose their compasses on the way across -
	 * they are being hunted now, not hunting.
	 *
	 * @return false if they were already a Speedrunner
	 */
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

	/**
	 * Puts {@code target} back on the Hunter team, handing them a compass mid-match since they are hunting
	 * again.
	 *
	 * @return false if they were not a Speedrunner
	 */
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

	/**
	 * Notes a Speedrunner's current name on the way in. The roster now outlives the server, so a name taken
	 * down at {@code /manhunt speedrunner add} time can be any age by the time the teams list quotes it.
	 */
	public static void noteName(Player p) {
		if(!isSpeedrunner(p)) return;
		String previous = NAMES.put(p.getUniqueId(), p.getName());
		if(!p.getName().equals(previous)) save();
	}

	// ---- match ----------------------------------------------------------------------------------------

	/**
	 * Hands everybody a Stick Manhunt Hyperion and every Hunter a compass, and puts every ceiling back on
	 * the bottom rung - so a match always opens on 160 ticks and 250, whatever the last one ended on.
	 *
	 * <p><b>Everybody starts on nothing.</b> Mana is zeroed outright rather than left to
	 * {@code Plugin.passiveIntel} to clamp: that only pulls a player down to the new 250 cap, so whoever
	 * happened to be sitting on a full 2500 would open the match with 250 banked and everybody else with
	 * whatever they walked in on. The banked regen ticks go with it, so nobody is paid a point on tick one.
	 *
	 * <p>The locator bar goes off in every dimension too - the whole game is the Hunters not knowing where
	 * the Speedrunners are, and the bar hands them that for free. {@link #reset} puts it back.
	 *
	 * <p>Every piglin brute already loaded is swapped for a piglin here; the rest are caught as they spawn
	 * or as their chunk loads. Unlike the locator bar this one does not come back - see
	 * {@link ManhuntPiglins#demote}.
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
	 * Takes every Manhunt Hyperion and Manhunt Compass back. Teams are left alone, so a rematch is one
	 * command.
	 *
	 * <p>Every ceiling is dropped to the bottom rung <b>in memory only</b>: with the match over
	 * {@link #active} is false, so what everybody actually plays on is the standard 80 ticks and 2500 - and
	 * nobody's mana is clamped on the way out, because the cap went up rather than down.
	 *
	 * <p>Every world's locator bar goes back to whatever it was set to before the match, on or off.
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
	 * Turns {@code locatorBar} off in every loaded world, noting first what each one was set to. Loaded is
	 * every dimension in practice - Paper brings the overworld, the nether and the end up at boot and they
	 * stay up; a world something else loads mid-match would not be covered.
	 *
	 * <p><b>{@code putIfAbsent}, not {@code put}</b>: {@code /manhunt start} run twice must not record the
	 * off we ourselves set as the value to go back to.
	 */
	private static void hideLocatorBar() {
		for(World world : Bukkit.getWorlds()) {
			LOCATOR_BARS.putIfAbsent(world.getName(), Boolean.TRUE.equals(world.getGameRuleValue(GameRule.LOCATOR_BAR)));
			world.setGameRule(GameRule.LOCATOR_BAR, false);
		}
	}

	/**
	 * Puts every world's {@code locatorBar} back where the match found it. A world that has since been
	 * unloaded is skipped rather than waited for: its gamerule lives in its own level.dat and nothing has
	 * touched it since the match started.
	 */
	private static void restoreLocatorBar() {
		for(Map.Entry<String, Boolean> entry : LOCATOR_BARS.entrySet()) {
			World world = Bukkit.getWorld(entry.getKey());
			if(world != null) world.setGameRule(GameRule.LOCATOR_BAR, entry.getValue());
		}
		LOCATOR_BARS.clear();
	}

	/**
	 * Gives {@code p} whatever their team is owed, skipping anything they already hold. Run by
	 * {@code /manhunt start} and again when somebody joins a match already under way, so a late arrival is
	 * not left empty-handed.
	 */
	public static void equip(Player p) {
		if(!started) return;
		KITTED.add(p.getUniqueId());
		// A real Hyperion is NOT one of these, deliberately: it is the ladder's reward, not a rung on it, and
		// it has no Change Target, no rung to upgrade and no part in the intelligence ceiling's bottom end.
		// Gating on bestRungIn meant anyone who walked into a match already carrying one started with no
		// Manhunt Hyperion at all.  It still sets their ceiling - observe() reads bestRungIn, not this.
		if(!hasManhuntHyperion(p)) {
			give(p, ManhuntHyperion.getItem(ManhuntTier.BASE));
		}
		if(!isSpeedrunner(p) && !hasCompass(p)) {
			give(p, ManhuntCompass.getItem());
		}
		save();
	}

	/**
	 * The join-time kit, which is <b>once per match</b>: somebody turning up partway through a Manhunt is
	 * not left empty-handed, but somebody rejoining it gets nothing, whatever they are carrying. Losing a
	 * Hyperion is meant to cost - it is on the ground where they died, to be picked back up or lost - and a
	 * rejoin used to be a way to be handed a new one, or a second one on top of a stashed first.
	 *
	 * <p>Respawning still goes through {@link #equip} unconditionally: dying is the one way back to a kit.
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

	/** Strips Manhunt gear out of every slot {@code p} owns, including their cursor. */
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

	/** Whether {@code p} is carrying a Manhunt Hyperion on any rung. A full Hyperion is not one. */
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
	 * How long a player is immune to Manhunt Hyperion implosions for after one lands on them.
	 *
	 * <p>The implosion has no hit cooldown and no cast time worth the name, so without this anyone cornered
	 * by four Hyperions is deleted by four simultaneous right-clicks with nothing they can do about it. One
	 * second per victim, shared across every attacker, so piling on stops paying.
	 */
	public static final long IMPLOSION_IMMUNITY_TICKS = 20;

	/** The tick each player last took an implosion on. Match state, not worth persisting. */
	private static final Map<UUID, Long> LAST_IMPLOSION = new HashMap<>();

	/**
	 * Whether a Manhunt Hyperion's implosion may damage {@code target} right now, <b>claiming the window if
	 * it may</b> - so call it exactly once per target per implosion, where the damage is decided.
	 *
	 * <p>Every player is on the clock, whichever side they are on; mobs are not, so a Hyperion clears a
	 * crowd as fast as ever. The window is claimed on the attempt rather than once the damage has landed,
	 * which is the cheap end of the trade: a blow the pipeline soaks to nothing still spends it.
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
	 * Raises {@code p}'s ceiling to the best Hyperion they are carrying. Called every tick off the
	 * intelligence loop, which covers every way one can arrive - crafted, picked up off a body, pulled out
	 * of a chest - without a hook per route. Cheap: the material rules out all but eight stacks before any
	 * lore is read.
	 */
	public static void observe(Player p) {
		if(!active()) return;
		// Not while they are lying there: the death drop is not always out of the inventory by the time this
		// runs, and a corpse re-raising its own ceiling would undo what onDeath just did.
		if(p.isDead()) return;
		int best = bestRungIn(p.getInventory());
		if(best > rung(p)) {
			CEILINGS.put(p.getUniqueId(), best);
			save();
		}
	}

	/** Back to the Stick's numbers, with nothing left in the tank - the banked regen ticks included. */
	public static void onDeath(Player p) {
		if(!active()) return;
		if(CEILINGS.remove(p.getUniqueId()) != null) save();
		Plugin.zeroIntelligence(p);
	}

	/** The rung {@code p}'s intelligence rules are read off: their high-water mark, the Stick at worst. */
	private static int rung(Player p) {
		return CEILINGS.getOrDefault(p.getUniqueId(), 0);
	}

	/** The best rung anywhere in {@code inventory}, {@link #FULL_RUNG} for a real Hyperion, -1 for none. */
	private static int bestRungIn(Inventory inventory) {
		int best = -1;
		for(ItemStack item : inventory.getContents()) {
			if(item == null) continue;
			// Material first: every rung is a sword (or a stick), so this is the whole check for most slots.
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
