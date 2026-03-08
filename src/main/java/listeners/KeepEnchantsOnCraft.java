package listeners;

import items.misc.DivanPickaxe;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class KeepEnchantsOnCraft implements Listener {

	// Expected lore keys for the Divan Pickaxe recipe (shape: DAE / GPI / LNR)
	private static final String[] DIVAN_RECIPE_KEYS = {
			"skyblock/ingredient/refined_diamond",   // D
			"skyblock/ingredient/alloy",             // A
			"skyblock/ingredient/refined_emerald",   // E
			"skyblock/ingredient/refined_gold",      // G
			null,                                    // P (netherite pickaxe, matched by material)
			"skyblock/ingredient/refined_iron",      // I
			"skyblock/ingredient/refined_lapis",     // L
			"skyblock/ingredient/refined_netherite", // N
			"skyblock/ingredient/refined_redstone"   // R
	};

	/**
	 * Fallback for ExactChoice recipes that fail due to item component mismatches in 1.21+.
	 * Matches ingredients by their lore key instead.
	 */
	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent e) {
		if(e.getRecipe() != null) return;

		ItemStack[] matrix = e.getInventory().getMatrix();
		if(matrix.length != 9) return;

		if(matchesDivanRecipe(matrix)) {
			e.getInventory().setResult(DivanPickaxe.getItem());
		}
	}

	private boolean matchesDivanRecipe(ItemStack[] matrix) {
		for(int i = 0; i < 9; i++) {
			ItemStack item = matrix[i];
			if(DIVAN_RECIPE_KEYS[i] == null) {
				// Center slot: must be a netherite pickaxe
				if(item == null || item.getType() != Material.NETHERITE_PICKAXE) return false;
			} else {
				if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) return false;
				if(!DIVAN_RECIPE_KEYS[i].equals(item.getItemMeta().getLore().getFirst())) return false;
			}
		}
		return true;
	}

	@EventHandler
	public void onCraftItem(CraftItemEvent e) {
		try {
			e.getInventory().getResult().addUnsafeEnchantments(e.getInventory().getMatrix()[4].getEnchantments());
		} catch(Exception exception) {
			// nothing
		}
	}
}
