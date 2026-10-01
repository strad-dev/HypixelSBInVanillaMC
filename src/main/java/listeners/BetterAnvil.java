package listeners;

import misc.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AnvilMenu;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
		int penalties = penalty(first) + penalty(second);
		if(second == null || second.getType() == Material.AIR) {
			ItemStack renamed = e.getResult();
			if(renamed == null || renamed.isEmpty()) return;
			e.setResult(withoutPenalty(renamed));
			fixCost(e, penalties, 0, 1);
			return;
		}

		boolean firstIsBook = first.getType() == Material.ENCHANTED_BOOK;
		boolean secondIsBook = second.getType() == Material.ENCHANTED_BOOK;

		ItemStack result;
		if(secondIsBook && !firstIsBook) {
			result = handleBookToItem(first, second);
		} else if(firstIsBook && secondIsBook) {
			result = handleBookToBook(first, second);
		} else if(!firstIsBook) {
			result = handleItemToItem(first, second);
		} else {
			return;
		}

		if(result == null) {
			e.setResult(null);
			return;
		}

		e.setResult(withoutPenalty(result));

		// Fix XP cost, since vanilla may not compute a valid cost for custom results
		int surcharge = overlevelSurcharge(first, second, result);
		int fallbackCost = computeCost(first, result) + surcharge;
		fixCost(e, penalties, surcharge, fallbackCost);
	}

	private void fixCost(PrepareAnvilEvent e, int penalties, int surcharge, int fallbackCost) {
		if(!(e.getView().getPlayer() instanceof Player player)) return;
		int ticket = costTickets.merge(player.getUniqueId(), 1, Integer::sum);
		Utils.scheduleTask(() -> {
			if(costTickets.getOrDefault(player.getUniqueId(), 0) != ticket) return;
			ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
			if(serverPlayer.containerMenu instanceof AnvilMenu anvilMenu) {
				int vanillaCost = anvilMenu.cost.get() - penalties;
				anvilMenu.cost.set(vanillaCost <= 0 ? fallbackCost : vanillaCost + surcharge);
			}
			player.updateInventory();
		}, 1);
	}

	private int penalty(ItemStack item) {
		return item != null && item.getItemMeta() instanceof Repairable r ? r.getRepairCost() : 0;
	}

	private ItemStack withoutPenalty(ItemStack item) {
		if(item.getItemMeta() instanceof Repairable r && r.hasRepairCost()) {
			r.setRepairCost(0);
			item.setItemMeta(r);
		}
		return item;
	}

	private static final Map<UUID, Integer> costTickets = new HashMap<>();

	private static final Map<Enchantment, int[]> OVERLEVEL_COSTS = Map.of(
			Enchantment.SHARPNESS, new int[]{20, 30},
			Enchantment.POWER, new int[]{20, 30},
			Enchantment.SMITE, new int[]{15, 25},
			Enchantment.BANE_OF_ARTHROPODS, new int[]{15, 25},
			Enchantment.PROTECTION, new int[]{10},
			Enchantment.FEATHER_FALLING, new int[]{10},
			Enchantment.LOOTING, new int[]{30, 50},
			Enchantment.FORTUNE, new int[]{25},
			Enchantment.EFFICIENCY, new int[]{15},
			Enchantment.SWEEPING_EDGE, new int[]{15}
	);

	private int overlevelSurcharge(ItemStack first, ItemStack second, ItemStack result) {
		boolean fromBook = second.getType() == Material.ENCHANTED_BOOK;
		Map<Enchantment, Integer> firstEnchants = getEnchants(first);
		int surcharge = 0;
		for(Map.Entry<Enchantment, Integer> entry : getEnchants(result).entrySet()) {
			Enchantment ench = entry.getKey();
			int level = entry.getValue();
			int max = ench.getMaxLevel();
			if(level <= max || level <= firstEnchants.getOrDefault(ench, 0)) continue;
			int perLevel = fromBook ? Math.max(1, ench.getAnvilCost() / 2) : ench.getAnvilCost();
			int[] fixed = OVERLEVEL_COSTS.get(ench);
			int tier = level - max - 1;
			int cost = fixed != null && tier < fixed.length ? fixed[tier] : perLevel * level * (tier >= 1 ? 3 : 2);
			surcharge += cost - perLevel * max;
		}
		return surcharge;
	}

	private int computeCost(ItemStack first, ItemStack result) {
		int cost = 0;

		// Enchantment cost: count each enchant that was added or upgraded
		Map<Enchantment, Integer> firstEnchants = getEnchants(first);
		Map<Enchantment, Integer> resultEnchants = getEnchants(result);

		for(Map.Entry<Enchantment, Integer> entry : resultEnchants.entrySet()) {
			Enchantment ench = entry.getKey();
			int resultLevel = entry.getValue();
			int firstLevel = firstEnchants.getOrDefault(ench, 0);

			if(resultLevel > firstLevel) {
				// Enchant was added or upgraded, so the cost is the new level
				cost += resultLevel;
			}
		}

		return Math.max(cost, 1);
	}

	private Map<Enchantment, Integer> getEnchants(ItemStack item) {
		if(item.getItemMeta() instanceof EnchantmentStorageMeta meta) {
			return meta.getStoredEnchants();
		}
		return item.getEnchantments();
	}

	@EventHandler
	public void onAnvilClick(InventoryClickEvent e) {
		if(e.getInventory().getType() != InventoryType.ANVIL) return;
		if(!(e.getWhoClicked() instanceof Player player)) return;

		// Handle clicking the result slot (slot 2), since vanilla may block custom results
		if(e.getRawSlot() == 2) {
			ItemStack result = e.getInventory().getItem(2);
			if(result != null && result.getType() != Material.AIR) {
				ItemStack cursor = e.getCursor();
				if(cursor.getType() != Material.AIR) return;

				e.setCancelled(true);

				// Read before clearing the inputs: an empty anvil resets cost to -1
				ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
				int cost = serverPlayer.containerMenu instanceof AnvilMenu anvilMenu ? Math.max(0, anvilMenu.cost.get()) : 0;
				boolean creative = player.getGameMode() == org.bukkit.GameMode.CREATIVE;
				if(creative || player.getLevel() >= cost) {
					player.setItemOnCursor(result);
					e.getInventory().setItem(0, null);
					e.getInventory().setItem(1, null);
					e.getInventory().setItem(2, null);

					if(!creative) player.setLevel(player.getLevel() - cost);

					player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F);
				}
			}
		}

		Utils.scheduleTask(player::updateInventory, 1);
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
				// Item doesn't have this enchant, so apply it
				result.addUnsafeEnchantment(enchantment, bookLevel);
				anyApplied = true;
			} else if(bookLevel > itemLevel) {
				// Book level is higher, so upgrade
				result.addUnsafeEnchantment(enchantment, bookLevel);
				anyApplied = true;
			} else if(bookLevel == itemLevel) {
				int combined = bookLevel + 1;
				if(notOverlevelled(enchantment, combined)) {
					result.addUnsafeEnchantment(enchantment, combined);
					anyApplied = true;
				}
				// else: would create overleveled, so skip this enchant
			}
			// bookLevel < itemLevel, so skip; no downgrade
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
				if(notOverlevelled(enchantment, combined)) {
					enchants1.put(enchantment, combined);
					anyChanged = true;
				}
				// else: would create overleveled, so skip
			}
			// level2 < level1, so skip
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
				if(notOverlevelled(enchantment, combined)) {
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
			result.setItemMeta(dmg1);
			return result;
		}

		if(!anyApplied) return null;
		return result;
	}

	private boolean notOverlevelled(Enchantment enchantment, int level) {
		return level <= enchantment.getMaxLevel();
	}

	private boolean canApplyToItem(ItemStack item, Enchantment enchantment) {
		if(enchantment == Enchantment.PROTECTION && Utils.firstLorePlain(item).equals("skyblock/combat/necron_elytra")) {
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
