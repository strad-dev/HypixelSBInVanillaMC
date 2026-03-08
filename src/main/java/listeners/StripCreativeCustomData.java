package listeners;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_21_R7.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class StripCreativeCustomData implements Listener {

	@EventHandler
	public void onCreativeInventory(InventoryCreativeEvent e) {
		stripEmpty(e.getCursor(), e::setCursor, "InventoryCreativeEvent/cursor");
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e instanceof InventoryCreativeEvent) return; // already handled by onCreativeInventory
		if (!(e.getWhoClicked() instanceof Player p) || p.getGameMode() != GameMode.CREATIVE) return;

		stripEmpty(e.getCurrentItem(), e::setCurrentItem, "InventoryClickEvent/currentItem");
		stripEmpty(e.getCursor(), item -> e.getView().setCursor(item), "InventoryClickEvent/cursor");
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		if (!(e.getWhoClicked() instanceof Player p) || p.getGameMode() != GameMode.CREATIVE) return;

		stripEmpty(e.getCursor(), e::setCursor, "InventoryDragEvent/cursor");
		for (int slot : e.getNewItems().keySet()) {
			ItemStack slotItem = e.getView().getItem(slot);
			stripEmpty(slotItem, item -> e.getView().setItem(slot, item), "InventoryDragEvent/slot" + slot);
		}
	}

	@EventHandler
	public void onItemPickup(EntityPickupItemEvent e) {
		if (!(e.getEntity() instanceof Player p)) return;

		ItemStack item = e.getItem().getItemStack();
		ItemStack stripped = stripEmptyCustomData(item, "EntityPickupItemEvent");
		if (stripped != null) {
			e.getItem().setItemStack(stripped);
		}
	}

	/**
	 * Strips empty custom_data from the given item and applies the result via the setter.
	 */
	private void stripEmpty(ItemStack item, java.util.function.Consumer<ItemStack> setter, String context) {
		ItemStack stripped = stripEmptyCustomData(item, context);
		if (stripped != null) {
			setter.accept(stripped);
		}
	}

	/**
	 * Returns a copy with empty custom_data removed, or null if no stripping was needed.
	 */
	public static ItemStack stripEmptyCustomData(ItemStack item, String context) {
		if (item == null || item.getType().isAir()) {
			return null;
		}

		net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
		CustomData customData = nms.get(DataComponents.CUSTOM_DATA);

		if (customData != null && customData.isEmpty()) {
			nms.remove(DataComponents.CUSTOM_DATA);
			return CraftItemStack.asBukkitCopy(nms);
		}

		return null;
	}

	/**
	 * Overload without context for backwards compatibility with PlayerLoginHandler.
	 */
	public static ItemStack stripEmptyCustomData(ItemStack item) {
		return stripEmptyCustomData(item, "unknown");
	}
}
