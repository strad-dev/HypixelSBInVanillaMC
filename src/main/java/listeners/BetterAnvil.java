package listeners;

import misc.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AnvilMenu;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.concurrent.atomic.AtomicBoolean;

public class BetterAnvil implements Listener {

	@EventHandler
	public void onAnvilOpen(InventoryOpenEvent e) {
		if(e.getInventory().getType() != InventoryType.ANVIL) return;
		if(!(e.getPlayer() instanceof Player player)) return;

		Utils.scheduleTask(() -> {
			ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
			if(!(serverPlayer.containerMenu instanceof AnvilMenu anvilMenu)) return;
			anvilMenu.maximumRepairCost = Integer.MAX_VALUE;
		}, 1);
	}

	@EventHandler
	public void onAnvilClick(InventoryClickEvent e) {
		if(e.getInventory().getType() != InventoryType.ANVIL) return;
		if(!(e.getWhoClicked() instanceof Player player)) return;

		Inventory anvil = e.getInventory();

		// After vanilla processes the click, check if we need to override the result
		Utils.scheduleTask(() -> {
			ItemStack first = anvil.getItem(0);
			ItemStack second = anvil.getItem(1);

			if(first == null || first.getType() == Material.AIR) return;
			if(second == null || second.getType() == Material.AIR) return;
			if(second.getType() != Material.ENCHANTED_BOOK) return;
			if(first.getType() == Material.ENCHANTED_BOOK) return;

			ItemStack result = first.clone();
			ItemMeta bookMeta = second.getItemMeta();
			if(bookMeta == null) return;

			for(Enchantment enchantment : ((EnchantmentStorageMeta) bookMeta).getStoredEnchants().keySet()) {
				if(canEnchant(first, enchantment)) {
					int bookLevel = ((EnchantmentStorageMeta) bookMeta).getStoredEnchantLevel(enchantment);
					int itemLevel = first.getEnchantmentLevel(enchantment);
					if(itemLevel < bookLevel) {
						result.addUnsafeEnchantment(enchantment, bookLevel);
					}
				}
			}

			// Preserve any rename from vanilla's result
			ItemStack vanillaResult = anvil.getItem(2);
			if(vanillaResult != null && vanillaResult.hasItemMeta()) {
				ItemMeta vanillaMeta = vanillaResult.getItemMeta();
				if(vanillaMeta != null && vanillaMeta.hasDisplayName()) {
					ItemMeta rm = result.getItemMeta();
					if(rm != null) {
						rm.setDisplayName(vanillaMeta.getDisplayName());
						result.setItemMeta(rm);
					}
				}
			}

			ItemMeta resultMeta = result.getItemMeta();
			if(resultMeta instanceof Repairable repairable) {
				if(repairable.getRepairCost() > 50) {
					repairable.setRepairCost(50);
				}
				result.setItemMeta(resultMeta);
			}

			anvil.setItem(2, result);

			ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
			if(serverPlayer.containerMenu instanceof AnvilMenu anvilMenu) {
				if(anvilMenu.cost.get() > 50) {
					anvilMenu.cost.set(50);
				}
			}

			player.updateInventory();
		}, 1);
	}

	public boolean canEnchant(ItemStack itemStack, Enchantment enchantment) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		AtomicBoolean canEnchant = new AtomicBoolean(enchantment.canEnchantItem(itemStack));

		if(itemMeta != null) {
			if(itemStack.getType() == Material.ELYTRA && enchantment == Enchantment.PROTECTION) {
				canEnchant.set(true);
			}
			itemMeta.getEnchants().keySet().forEach(ench -> {
				if(ench != enchantment && ench.conflictsWith(enchantment)) {
					canEnchant.set(false);
				}
			});
		}
		return canEnchant.get();
	}
}
