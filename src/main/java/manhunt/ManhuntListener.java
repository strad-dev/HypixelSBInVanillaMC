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
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;

/** Event-driven Manhunt rules: kits, death reset, upgrade crafts, nether rules. Registered only when on. */
public class ManhuntListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.discoverRecipes(AddRecipes.manhuntRecipeKeys(Plugin.getInstance()));
		// Roster is on disk by UUID; this only refreshes the name.
		Manhunt.noteName(p);
		Manhunt.equipOnJoin(p);
	}

	/**
	 * A Hunter keeps a copy of their best Hyperion one rung down and still drops the original; a Speedrunner drops
	 * everything and goes back to the Stick. Compasses are kept: a Hunter walking back to their body can't track
	 * anything, and a dropped compass is one a Speedrunner can pick up.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Manhunt.onDeath(e.getEntity(), demoteHyperion(e));
		keepCompasses(e);
	}

	/**
	 * Keeps the rung below the Hunter's best Manhunt Hyperion, unenchanted; the original still drops, for anyone to
	 * pick up ({@link #onPickup}). A Stick is kept, not dropped. Null for a Speedrunner, or a Hunter who had none
	 * (respawn hands out a Stick).
	 */
	@Nullable
	private static ManhuntTier demoteHyperion(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if(!Manhunt.active() || Manhunt.isSpeedrunner(p)) return null;
		ItemStack best = null;
		ManhuntTier bestTier = null;
		for(ItemStack item : e.getDrops()) {
			ManhuntTier tier = ManhuntTier.of(item);
			if(tier != null && (bestTier == null || tier.ordinal() > bestTier.ordinal())) {
				best = item;
				bestTier = tier;
			}
		}
		if(best == null) return null;
		if(bestTier == ManhuntTier.BASE) e.getDrops().remove(best);
		ManhuntTier kept = ManhuntTier.values()[Math.max(bestTier.ordinal() - 1, 0)];
		e.getItemsToKeep().add(ManhuntHyperion.getItem(kept, Map.of(), p));
		return kept;
	}

	/**
	 * Moves compasses from drops to getItemsToKeep (Paper's per-item keepInventory). Must leave the drops too,
	 * or it's kept AND dropped. Whole stack, so target and last fix survive. Default priority, after
	 * {@code PvpLoadoutMenu.onDeath}, which rebuilds the drop list at LOWEST.
	 */
	private static void keepCompasses(PlayerDeathEvent e) {
		Iterator<ItemStack> drops = e.getDrops().iterator();
		while(drops.hasNext()) {
			ItemStack item = drops.next();
			if(Manhunt.isCompass(item)) {
				drops.remove();
				e.getItemsToKeep().add(item);
			}
		}
	}

	/**
	 * Walking over a Manhunt Hyperion above your best one upgrades yours to it, keeping your enchants, and the drop
	 * is used up. Anything else, or no Manhunt Hyperion to upgrade, is a normal pickup.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPickup(EntityPickupItemEvent e) {
		if(!Manhunt.active() || !(e.getEntity() instanceof Player p)) return;
		ManhuntTier dropped = ManhuntTier.of(e.getItem().getItemStack());
		if(dropped == null) return;
		PlayerInventory inventory = p.getInventory();
		int slot = -1;
		ManhuntTier own = null;
		for(int i = 0; i < inventory.getSize(); i++) {
			ManhuntTier tier = ManhuntTier.of(inventory.getItem(i));
			if(tier != null && (own == null || tier.ordinal() > own.ordinal())) {
				own = tier;
				slot = i;
			}
		}
		if(own == null || dropped.ordinal() <= own.ordinal()) return;
		e.setCancelled(true);
		inventory.setItem(slot, ManhuntHyperion.getItem(dropped, inventory.getItem(slot).getEnchantments(), p));
		p.playPickupItemAnimation(e.getItem());
		e.getItem().remove();
	}

	/**
	 * Re-kits on the Stick after respawn unless a Hyperion was kept (without it a death left a player with only
	 * their compass), then puts the Hyperion in slot 0 and the compass in slot 1. A tick late: the inventory isn't
	 * settled during the event, and overflow would drop at the death spot.
	 */
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		Utils.scheduleTask(() -> {
			if(p.isOnline()) {
				Manhunt.equip(p);
				Manhunt.arrangeKit(p);
			}
		}, 1);
	}

	/** Pearls at weight 25 for 4-8, glowstone dust at 20 for 5-12. See {@link ManhuntPiglins#reroll}. */
	@EventHandler(ignoreCancelled = true)
	public void onPiglinBarter(PiglinBarterEvent e) {
		if(!Manhunt.active()) return;
		ManhuntPiglins.reroll(e.getOutcome());
	}

	/** No brutes during a match: their axe two-shots a Speedrunner. Swapped for a plain piglin. */
	@EventHandler(ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		if(!Manhunt.active() || !(e.getEntity() instanceof PiglinBrute brute)) return;
		e.setCancelled(true);
		ManhuntPiglins.replace(e.getLocation(), brute.isImmuneToZombification(), !brute.getRemoveWhenFarAway());
	}

	/**
	 * Brutes from bastions generated before the match never fire a spawn event, so catch them on load. A tick
	 * late: entities are still being added while this runs.
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
	 * Recipes can only match MATERIAL (no ExactChoice matches a live Hyperion), so the result is voided unless
	 * the grid holds the real rung below, then rebuilt with its enchants. Only place they survive:
	 * {@code KeepEnchantsOnCraft} skips shapeless recipes.
	 * The Stick is also a stick, so it's barred from every recipe but its WOOD upgrade. Higher rungs are swords
	 * no vanilla recipe wants; Netherite deliberately still crafts into a real Hyperion.
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

		// Rung below and the added swords are always different materials, so this finds the Hyperion anywhere.
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

	private static boolean holdsStick(ItemStack[] matrix) {
		for(ItemStack item : matrix) {
			if(ManhuntTier.of(item) == ManhuntTier.BASE) return true;
		}
		return false;
	}

	/** Rung a {@code manhunt_*} key upgrades to, else null. */
	private static ManhuntTier tierOf(String key) {
		if(!key.startsWith("manhunt_")) return null;
		try {
			return ManhuntTier.valueOf(key.substring("manhunt_".length()).toUpperCase(java.util.Locale.ROOT));
		} catch(IllegalArgumentException exception) {
			return null;
		}
	}
}
