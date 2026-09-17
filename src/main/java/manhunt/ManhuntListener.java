package manhunt;

import items.weapons.ManhuntHyperion;
import misc.AddRecipes;
import misc.Plugin;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 * The Manhunt rules that hang off events: kitting players out, resetting a dead one's intelligence, and
 * vetting the Hyperion upgrade craft. Registered only when Manhunt is on.
 */
public class ManhuntListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.discoverRecipes(AddRecipes.manhuntRecipeKeys(Plugin.getInstance()));
		// Somebody arriving into a match already under way still gets their kit.
		Manhunt.equip(p);
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
	 * Vets a Manhunt upgrade. The recipe can only ask for the right MATERIAL (a Hyperion carries
	 * enchantments and a live damage figure, so nothing exact would match it), so a bare stick plus eight
	 * wooden swords matches just as well - the result is thrown away here unless the grid really holds the
	 * rung below. On the way through, the result is rebuilt with that Hyperion's enchantments and for the
	 * player looking at it, which is also the only place those enchantments survive: the recipes are
	 * shapeless, so {@code KeepEnchantsOnCraft} skips them rather than copy whatever sits in slot 4.
	 */
	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent e) {
		Recipe recipe = e.getRecipe();
		if(!(recipe instanceof Keyed keyed)) return;
		if(!keyed.getKey().getNamespace().equals(Plugin.getInstance().getName().toLowerCase(java.util.Locale.ROOT))) return;

		ManhuntTier tier = tierOf(keyed.getKey().getKey());
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
