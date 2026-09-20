package manhunt;

import items.weapons.ManhuntHyperion;
import misc.AddRecipes;
import misc.Plugin;
import misc.Utils;
import org.bukkit.Keyed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 * The Manhunt rules that hang off events: kitting players out on join and on respawn, resetting a dead
 * one's intelligence, vetting the Hyperion upgrade craft, and the nether rules in {@link ManhuntPiglins}.
 * Registered only when Manhunt is on.
 */
public class ManhuntListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.discoverRecipes(AddRecipes.manhuntRecipeKeys(Plugin.getInstance()));
		// A rejoining Speedrunner is still a Speedrunner - the roster is on disk, keyed on UUID - so this is
		// only about the name the teams list quotes for them.
		Manhunt.noteName(p);
		// Somebody arriving into a match already under way still gets their kit - once.
		Manhunt.equipOnJoin(p);
	}

	/**
	 * Death costs a player their whole intelligence ceiling, not just what was in the tank: they are back on
	 * the Stick's numbers until they get their hands on a Hyperion again.
	 */
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Manhunt.onDeath(e.getEntity());
	}

	/**
	 * Kits a player back out after they respawn. Death drops their Hyperion and compass on the floor, and
	 * {@code equip} otherwise only runs on {@code /manhunt start} and on join - so without this a death put
	 * a Hunter out of the match for good, with no way back to a compass short of a rejoin.
	 *
	 * <p>They come back on the <b>Stick</b>, which is the same demotion {@link Manhunt#onDeath} applies to
	 * their intelligence ceiling: whatever rung they had reached is on the ground with the rest of their
	 * things, to be picked back up or lost.
	 *
	 * <p>A tick late, for two reasons: with keepInventory off the inventory is not settled while the event
	 * runs, and {@code equip} drops anything that will not fit at {@code p.getLocation()} - which is still
	 * the DEATH spot during the event, so an overflow would land back where they died.
	 */
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		Utils.scheduleTask(() -> {
			if(p.isOnline()) Manhunt.equip(p);
		}, 1);
	}

	/**
	 * Piglins barter on the Manhunt table while a match is running: ender pearls at weight 25 for 4-8 and
	 * glowstone dust back in at weight 20 for 5-12. {@link ManhuntPiglins#reroll} does the arithmetic.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPiglinBarter(PiglinBarterEvent e) {
		if(!Manhunt.active()) return;
		ManhuntPiglins.reroll(e.getOutcome());
	}

	/**
	 * No piglin brutes during a match - one walks in wearing an axe that two-shots a Speedrunner through a
	 * bastion they have to loot. The spawn is cancelled and an ordinary piglin takes its place, which covers
	 * anything spawned while the chunk is up: a spawner, a command, or a bastion generating under a player.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		if(!Manhunt.active() || !(e.getEntity() instanceof PiglinBrute brute)) return;
		e.setCancelled(true);
		ManhuntPiglins.replace(e.getLocation(), brute.isImmuneToZombification(), !brute.getRemoveWhenFarAway());
	}

	/**
	 * The other half of the same rule: a bastion generated before the match, or before this server ever ran
	 * a Manhunt, has its brutes sitting in the chunk file and they never go through a spawn event. They are
	 * caught as the chunk's entities load instead.
	 *
	 * <p>A tick late, because the chunk's entities are still being added to the world while this runs and
	 * the swap both removes one and spawns another.
	 */
	@EventHandler
	public void onEntitiesLoad(EntitiesLoadEvent e) {
		if(!Manhunt.active()) return;
		for(Entity entity : e.getEntities()) {
			if(entity instanceof PiglinBrute brute) {
				Utils.scheduleTask(() -> ManhuntPiglins.demote(brute), 1);
			}
		}
	}

	/**
	 * Vets a Manhunt upgrade, and keeps the bottom rung out of every other recipe in the game.
	 *
	 * <p>The upgrade recipe can only ask for the right MATERIAL (a Hyperion carries enchantments and a live
	 * damage figure, so nothing exact would match it), so a bare stick plus eight wooden swords matches just
	 * as well - the result is thrown away here unless the grid really holds the rung below. On the way
	 * through, the result is rebuilt with that Hyperion's enchantments and for the player looking at it,
	 * which is also the only place those enchantments survive: the recipes are shapeless, so
	 * {@code KeepEnchantsOnCraft} skips them rather than copy whatever sits in slot 4.
	 *
	 * <p>The other half is the Stick. It is a Hyperion, but it is also a stick, so vanilla will happily take
	 * it for torches, ladders and every tool in the game - a player starting a Manhunt could burn their
	 * weapon on a crafting table without noticing. Any recipe but its own upgrade (the WOOD rung) is refused
	 * while one sits in the grid. The rungs above are swords and no vanilla recipe wants one; the Netherite
	 * rung deliberately still crafts into a real Hyperion.
	 */
	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent e) {
		Recipe recipe = e.getRecipe();
		ManhuntTier tier = recipe instanceof Keyed keyed
				&& keyed.getKey().getNamespace().equals(Plugin.getInstance().getName().toLowerCase(java.util.Locale.ROOT))
				? tierOf(keyed.getKey().getKey())
				: null;

		if(tier != ManhuntTier.WOOD && holdsStick(e.getInventory().getMatrix())) {
			e.getInventory().setResult(null);
			return;
		}

		if(tier == null) return;
		ManhuntTier from = ManhuntTier.values()[tier.ordinal() - 1];

		// The rung below and the swords going on are always different materials, so the one stack of the
		// lower material is the Hyperion slot wherever in the grid it was dropped.
		ItemStack input = null;
		for(ItemStack item : e.getInventory().getMatrix()) {
			if(item != null && item.getType() == from.material()) input = item;
		}

		if(ManhuntTier.of(input) != from) {
			e.getInventory().setResult(null);
			return;
		}

		Player viewer = e.getView().getPlayer() instanceof Player p ? p : null;
		e.getInventory().setResult(ManhuntHyperion.getItem(tier, input.getEnchantments(), viewer));
	}

	/** Whether any stack in the grid is a bottom-rung (Stick) Manhunt Hyperion. */
	private static boolean holdsStick(ItemStack[] matrix) {
		for(ItemStack item : matrix) {
			if(ManhuntTier.of(item) == ManhuntTier.BASE) return true;
		}
		return false;
	}

	/** The rung a {@code manhunt_*} recipe key upgrades to, or null for any other recipe of ours. */
	private static ManhuntTier tierOf(String key) {
		if(!key.startsWith("manhunt_")) return null;
		try {
			return ManhuntTier.valueOf(key.substring("manhunt_".length()).toUpperCase(java.util.Locale.ROOT));
		} catch(IllegalArgumentException exception) {
			return null;
		}
	}
}
