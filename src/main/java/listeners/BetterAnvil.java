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
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.Repairable;

import java.util.HashMap;
import java.util.Map;

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
	public void onPrepareAnvil(PrepareAnvilEvent e) {
		AnvilInventory anvil = e.getInventory();
		ItemStack first = anvil.getItem(0);
		ItemStack second = anvil.getItem(1);

		if(first == null || first.getType() == Material.AIR) return;
		if(second == null || second.getType() == Material.AIR) return;

		boolean firstIsBook = first.getType() == Material.ENCHANTED_BOOK;
		boolean secondIsBook = second.getType() == Material.ENCHANTED_BOOK;

		ItemStack result;
		if(secondIsBook && !firstIsBook) {
			result = handleBookToItem(first, second);
		} else if(firstIsBook && secondIsBook) {
			result = handleBookToBook(first, second);
		} else if(!firstIsBook && !secondIsBook) {
			result = handleItemToItem(first, second);
		} else {
			return;
		}

		if(result == null) {
			e.setResult(null);
			return;
		}

		// Preserve rename from anvil text
		String renameText = anvil.getRenameText();
		if(renameText != null && !renameText.isEmpty()) {
			ItemMeta rm = result.getItemMeta();
			if(rm != null) {
				rm.setDisplayName(renameText);
				result.setItemMeta(rm);
			}
		}

		// Cap repair cost
		ItemMeta resultMeta = result.getItemMeta();
		if(resultMeta instanceof Repairable repairable) {
			if(repairable.getRepairCost() > 50) {
				repairable.setRepairCost(50);
			}
			result.setItemMeta(resultMeta);
		}

		e.setResult(result);
	}

	@EventHandler
	public void onAnvilClick(InventoryClickEvent e) {
		if(e.getInventory().getType() != InventoryType.ANVIL) return;
		if(!(e.getWhoClicked() instanceof Player player)) return;

		Utils.scheduleTask(() -> {
			ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
			if(serverPlayer.containerMenu instanceof AnvilMenu anvilMenu) {
				if(anvilMenu.cost.get() > 50) {
					anvilMenu.cost.set(50);
				}
			}
			player.updateInventory();
		}, 1);
	}

	private ItemStack handleBookToItem(ItemStack item, ItemStack book) {
		ItemStack result = item.clone();
		EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) book.getItemMeta();
		if(bookMeta == null) return null;

		Map<Enchantment, Integer> bookEnchants = bookMeta.getStoredEnchants();
		boolean anyApplied = false;

		for(Map.Entry<Enchantment, Integer> entry : bookEnchants.entrySet()) {
			Enchantment enchantment = entry.getKey();
			int bookLevel = entry.getValue();

			if(!canApplyToItem(item, enchantment)) continue;

			// Remove conflicting enchants from result
			removeConflicting(result, enchantment);

			int itemLevel = result.getEnchantmentLevel(enchantment);

			if(itemLevel == 0) {
				// Item doesn't have this enchant — apply it
				result.addUnsafeEnchantment(enchantment, bookLevel);
				anyApplied = true;
			} else if(bookLevel > itemLevel) {
				// Book level is higher — upgrade
				result.addUnsafeEnchantment(enchantment, bookLevel);
				anyApplied = true;
			} else if(bookLevel == itemLevel) {
				int combined = bookLevel + 1;
				if(!isOverleveled(enchantment, combined)) {
					result.addUnsafeEnchantment(enchantment, combined);
					anyApplied = true;
				}
				// else: would create overleveled — skip this enchant
			}
			// bookLevel < itemLevel — skip (no downgrade)
		}

		if(!anyApplied) return null;
		return result;
	}

	private ItemStack handleBookToBook(ItemStack book1, ItemStack book2) {
		EnchantmentStorageMeta meta1 = (EnchantmentStorageMeta) book1.getItemMeta();
		EnchantmentStorageMeta meta2 = (EnchantmentStorageMeta) book2.getItemMeta();
		if(meta1 == null || meta2 == null) return null;

		Map<Enchantment, Integer> enchants1 = new HashMap<>(meta1.getStoredEnchants());
		Map<Enchantment, Integer> enchants2 = meta2.getStoredEnchants();

		boolean anyChanged = false;

		for(Map.Entry<Enchantment, Integer> entry : enchants2.entrySet()) {
			Enchantment enchantment = entry.getKey();
			int level2 = entry.getValue();

			// Remove conflicting enchants from result
			enchants1.entrySet().removeIf(e -> {
				Enchantment existing = e.getKey();
				return existing != enchantment && existing.conflictsWith(enchantment);
			});

			int level1 = enchants1.getOrDefault(enchantment, 0);

			if(level1 == 0) {
				enchants1.put(enchantment, level2);
				anyChanged = true;
			} else if(level2 > level1) {
				enchants1.put(enchantment, level2);
				anyChanged = true;
			} else if(level2 == level1) {
				int combined = level2 + 1;
				if(!isOverleveled(enchantment, combined)) {
					enchants1.put(enchantment, combined);
					anyChanged = true;
				}
				// else: would create overleveled — skip
			}
			// level2 < level1 — skip
		}

		if(!anyChanged) return null;

		ItemStack result = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta resultMeta = (EnchantmentStorageMeta) result.getItemMeta();
		if(resultMeta == null) return null;

		for(Map.Entry<Enchantment, Integer> entry : enchants1.entrySet()) {
			resultMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
		}
		result.setItemMeta(resultMeta);
		return result;
	}

	private ItemStack handleItemToItem(ItemStack item1, ItemStack item2) {
		if(item1.getType() != item2.getType()) return null;

		ItemStack result = item1.clone();
		Map<Enchantment, Integer> sourceEnchants = item2.getEnchantments();
		boolean anyApplied = false;

		for(Map.Entry<Enchantment, Integer> entry : sourceEnchants.entrySet()) {
			Enchantment enchantment = entry.getKey();
			int sourceLevel = entry.getValue();

			// Remove conflicting enchants from result
			removeConflicting(result, enchantment);

			int targetLevel = result.getEnchantmentLevel(enchantment);

			if(targetLevel == 0) {
				result.addUnsafeEnchantment(enchantment, sourceLevel);
				anyApplied = true;
			} else if(sourceLevel > targetLevel) {
				result.addUnsafeEnchantment(enchantment, sourceLevel);
				anyApplied = true;
			} else if(sourceLevel == targetLevel) {
				int combined = sourceLevel + 1;
				if(!isOverleveled(enchantment, combined)) {
					result.addUnsafeEnchantment(enchantment, combined);
					anyApplied = true;
				}
			}
		}

		// Allow vanilla durability repair even if no enchants changed
		int maxDurability = item1.getType().getMaxDurability();
		if(maxDurability > 0 && result.getItemMeta() instanceof Damageable dmg1 && item2.getItemMeta() instanceof Damageable dmg2) {
			int remaining1 = maxDurability - dmg1.getDamage();
			int remaining2 = maxDurability - dmg2.getDamage();
			int repaired = Math.min(remaining1 + remaining2 + (int)(maxDurability * 0.12), maxDurability);
			dmg1.setDamage(maxDurability - repaired);
			result.setItemMeta((ItemMeta) dmg1);
			return result;
		}

		if(!anyApplied) return null;
		return result;
	}

	private boolean isOverleveled(Enchantment enchantment, int level) {
		return level > enchantment.getMaxLevel();
	}

	private boolean canApplyToItem(ItemStack item, Enchantment enchantment) {
		if(item.getType() == Material.ELYTRA && enchantment == Enchantment.PROTECTION) {
			return true;
		}
		return enchantment.canEnchantItem(item);
	}

	private void removeConflicting(ItemStack item, Enchantment enchantment) {
		for(Enchantment existing : new HashMap<>(item.getEnchantments()).keySet()) {
			if(existing != enchantment && existing.conflictsWith(enchantment)) {
				item.removeEnchantment(existing);
			}
		}
	}
}
