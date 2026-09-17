package manhunt;

import items.misc.ManhuntCompass;
import items.weapons.ManhuntHyperion;
import items.weapons.Scylla;
import misc.Plugin;
import misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Minecraft Manhunt: a handful of Speedrunners running the game while everybody else hunts them. Off unless
 * {@code manhunt: true} in the config, at which point {@code /manhunt} appears and the intelligence rules
 * below take over.
 *
 * <p>Everything here is in memory - a Manhunt is one sitting, and a restart mid-match means re-running
 * {@code /manhunt speedrunner add} and {@code /manhunt start}.
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

	/** The rung above Netherite: a real Hyperion, which has no {@link ManhuntTier} of its own. */
	public static final int FULL_RUNG = ManhuntTier.values().length;

	public static void setEnabled(boolean on) {
		enabled = on;
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
		return true;
	}

	// ---- match ----------------------------------------------------------------------------------------

	/**
	 * Hands everybody a Stick Manhunt Hyperion and every Hunter a compass, and puts every ceiling back on
	 * the bottom rung - so a match always opens on 160 ticks and 250, whatever the last one ended on. A
	 * player sitting above the new cap is clamped down to it by {@code Plugin.passiveIntel}.
	 */
	public static void start() {
		started = true;
		CEILINGS.clear();
		for(Player p : Bukkit.getOnlinePlayers()) {
			equip(p);
		}
	}

	/**
	 * Takes every Manhunt Hyperion and Manhunt Compass back. Teams are left alone, so a rematch is one
	 * command.
	 *
	 * <p>Every ceiling is dropped to the bottom rung <b>in memory only</b>: with the match over
	 * {@link #active} is false, so what everybody actually plays on is the standard 80 ticks and 2500 - and
	 * nobody's mana is clamped on the way out, because the cap went up rather than down.
	 */
	public static void reset() {
		started = false;
		CEILINGS.clear();
		for(Player p : Bukkit.getOnlinePlayers()) {
			removeManhuntItems(p, true, true);
		}
	}

	/**
	 * Gives {@code p} whatever their team is owed, skipping anything they already hold. Run by
	 * {@code /manhunt start} and again when somebody joins a match already under way, so a late arrival is
	 * not left empty-handed.
	 */
	public static void equip(Player p) {
		if(!started) return;
		if(bestRungIn(p.getInventory()) < 0) {
			give(p, ManhuntHyperion.getItem(ManhuntTier.BASE));
		}
		if(!isSpeedrunner(p) && !hasCompass(p)) {
			give(p, ManhuntCompass.getItem());
		}
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
		}
	}

	/** Back to the Stick's numbers, with nothing left in the tank. */
	public static void onDeath(Player p) {
		if(!active()) return;
		CEILINGS.remove(p.getUniqueId());
		try {
			Plugin.getIntelligence(p).setScore(0);
		} catch(Exception exception) {
			// the objective is gone; passiveIntel already broadcasts about that
		}
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
