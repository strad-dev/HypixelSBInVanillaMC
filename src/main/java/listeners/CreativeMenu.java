package listeners;

import items.armor.*;
import items.ingredients.mining.*;
import items.ingredients.misc.*;
import items.ingredients.witherLords.*;
import items.misc.*;
import items.summonItems.*;
import items.weapons.Claymore;
import items.weapons.Scylla;
import items.weapons.Terminator;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CreativeMenu implements Listener {
	private static final String GUI_TITLE = ChatColor.DARK_GRAY + "SkyBlock";
	private static final Map<UUID, String> playerTabs = new HashMap<>();
	private static final Map<UUID, Integer> playerPages = new HashMap<>();

	// Item categories
	private static final Map<String, List<ItemStack>> ITEMS = new HashMap<>();

	static {
		// Initialize items
		ITEMS.put("items", Arrays.asList(
				Scylla.getItem(Enchantment.SHARPNESS, 0),
				Claymore.getItem(Enchantment.SHARPNESS, 0),
				Terminator.getItem(0),
				WardenHelmet.getItem(),
				WitherKingCrown.getItem(),
				NecronElytra.getItem(),
				PrimalDragonChestplate.getItem(),
				GoldorLeggings.getItem(),
				MaxorBoots.getItem(),
				AOTV.getItem(),
				IceSpray.getItem(),
				DivanPickaxe.getItem(),
				WandOfRestoration.getItem(),
				WandOfAtonement.getItem(),
				HolyIce.getItem(),
				BonzoStaff.getItem(),
				TacticalInsertion.getItem(),
				GyrokineticWand.getItem()
		));

		// Initialize ingredients
		ITEMS.put("ingredients", Arrays.asList(
				Handle.getItem(),
				ShadowWarp.getItem(),
				Implosion.getItem(),
				WitherShield.getItem(),
				MaxorSecrets.getItem(),
				StormSecrets.getItem(),
				GoldorSecrets.getItem(),
				NecronSecrets.getItem(),
				WardenHeart.getItem(),
				AncientDragonEgg.getItem(),
				Core.getItem(),
				TessellatedPearl.getItem(),
				NullOvoid.getItem(),
				NullBlade.getItem(),
				BraidedFeather.getItem(),
				TarantulaSilk.getItem(),
				Viscera.getItem(),
				GiantSwordRemnant.getItem(),
				Alloy.getItem(),
				ConcentratedStone.getItem(),
				RefinedDiamond.getItem(),
				RefinedEmerald.getItem(),
				RefinedGold.getItem(),
				RefinedIron.getItem(),
				RefinedLapis.getItem(),
				RefinedNetherite.getItem(),
				RefinedRedstone.getItem()
		));

		// Initialize summon items
		ITEMS.put("summon", Arrays.asList(
				SuperiorRemnant.getItem(),
				CorruptPearl.getItem(),
				Antimatter.getItem(),
				OmegaEgg.getItem(),
				SpiderRelic.getItem(),
				AtonedFlesh.getItem(),
				GiantZombieFlesh.getItem(),
				HighlyInfuriatedWitherSkeletonSpawnEgg.getItem()
		));

		ITEMS.put("enchantments", Arrays.asList(
				EnchantmentUpgrader.getItem(),
				getEnchantedBook(Enchantment.SHARPNESS, 6),
				getEnchantedBook(Enchantment.SHARPNESS, 7),
				getEnchantedBook(Enchantment.SMITE, 6),
				getEnchantedBook(Enchantment.BANE_OF_ARTHROPODS, 6),
				getEnchantedBook(Enchantment.POWER, 6),
				getEnchantedBook(Enchantment.POWER, 7),
				getEnchantedBook(Enchantment.LOOTING, 4),
				getEnchantedBook(Enchantment.LOOTING, 5),
				getEnchantedBook(Enchantment.SWEEPING_EDGE, 4),
				getEnchantedBook(Enchantment.EFFICIENCY, 6),
				getEnchantedBook(Enchantment.FORTUNE, 4),
				getEnchantedBook(Enchantment.PROTECTION, 5),
				getEnchantedBook(Enchantment.FEATHER_FALLING, 5)
		));
	}

	private static ItemStack getEnchantedBook(Enchantment enchantment, int level) {
		ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
		meta.addStoredEnchant(enchantment, level, true);
		book.setItemMeta(meta);
		return book;
	}

	private static String tabIdToName(String tab) {
		return switch(tab) {
			case "items" -> "Items";
			case "ingredients" -> "Ingredients";
			case "summon" -> "Summon Items";
			case "enchantments" -> "Enchantments";
			default -> throw new IllegalStateException("Unexpected value: " + tab);
		};
	}

	public static void openCreativeMenu(Player player) {
		String currentTab = playerTabs.getOrDefault(player.getUniqueId(), "items");
		int page = playerPages.getOrDefault(player.getUniqueId(), 0);

		Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE + " " + tabIdToName(currentTab));

		// Add tabs at top
		addTabs(gui, currentTab);

		// Add items for current tab
		List<ItemStack> items = ITEMS.get(currentTab);
		if (items != null) {
			int startIndex = page * 36; // 36 items per page (excluding tab row)
			int endIndex = Math.min(startIndex + 36, items.size());

			for (int i = startIndex; i < endIndex; i++) {
				gui.setItem(9 + (i - startIndex), items.get(i));
			}
		}

		// Add navigation buttons if needed
		if (page > 0) {
			ItemStack prevPage = new ItemStack(Material.ARROW);
			ItemMeta prevMeta = prevPage.getItemMeta();
			prevMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
			prevPage.setItemMeta(prevMeta);
			gui.setItem(45, prevPage);
		}

		if (items != null && (page + 1) * 36 < items.size()) {
			ItemStack nextPage = new ItemStack(Material.ARROW);
			ItemMeta nextMeta = nextPage.getItemMeta();
			nextMeta.setDisplayName(ChatColor.GREEN + "Next Page");
			nextPage.setItemMeta(nextMeta);
			gui.setItem(53, nextPage);
		}

		player.openInventory(gui);
	}

	private static void addTabs(Inventory gui, String currentTab) {
		// Combat tab
		ItemStack items = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta itemsMeta = items.getItemMeta();
		itemsMeta.setDisplayName("Items");
		itemsMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

		if (currentTab.equals("items")) {
			itemsMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
			itemsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		items.setItemMeta(itemsMeta);
		gui.setItem(0, items);

		// Ingredients tab
		ItemStack ingredients = new ItemStack(Material.BLAZE_POWDER);
		ItemMeta ingredientsMeta = ingredients.getItemMeta();
		ingredientsMeta.setDisplayName("Ingredients");

		if (currentTab.equals("ingredients")) {
			ingredientsMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
			ingredientsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		ingredients.setItemMeta(ingredientsMeta);
		gui.setItem(1, ingredients);

		// Summon items tab
		ItemStack summon = new ItemStack(Material.ZOMBIE_SPAWN_EGG);
		ItemMeta summonMeta = summon.getItemMeta();
		summonMeta.setDisplayName("Summon Items");

		if (currentTab.equals("summon")) {
			summonMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
			summonMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		summon.setItemMeta(summonMeta);
		gui.setItem(2, summon);

		// Enchantments items tab
		ItemStack enchantment = new ItemStack(Material.ENCHANTED_BOOK);
		ItemMeta enchantmentMeta = summon.getItemMeta();
		summonMeta.setDisplayName("Enchantments");

		if (currentTab.equals("enchantments")) {
			summonMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
			summonMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		summon.setItemMeta(enchantmentMeta);
		gui.setItem(3, enchantment);

		// Fill rest of top row with glass
		ItemStack glass = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
		ItemMeta glassMeta = glass.getItemMeta();
		glassMeta.setDisplayName(" ");
		glass.setItemMeta(glassMeta);

		for (int i = 4; i < 9; i++) {
			gui.setItem(i, glass);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (!e.getView().getTitle().startsWith(GUI_TITLE)) return;

		Player player = (Player) e.getWhoClicked();
		ItemStack clicked = e.getCurrentItem();
		int slot = e.getRawSlot();

		// Only cancel if clicking in the GUI area (top inventory)
		// Slot < 54 means it's in the GUI, not player inventory
		if (e.getClickedInventory() != null && slot < 54) {
			e.setCancelled(true);

			if (clicked == null || clicked.getType() == Material.AIR) {
				player.setItemOnCursor(null);
				return;
			}

			// Tab clicks (top row)
			if (slot < 9) {
				switch (slot) {
					case 0 -> {
						playerTabs.put(player.getUniqueId(), "items");
						playerPages.put(player.getUniqueId(), 0);
						openCreativeMenu(player);
					}
					case 1 -> {
						playerTabs.put(player.getUniqueId(), "ingredients");
						playerPages.put(player.getUniqueId(), 0);
						openCreativeMenu(player);
					}
					case 2 -> {
						playerTabs.put(player.getUniqueId(), "summon");
						playerPages.put(player.getUniqueId(), 0);
						openCreativeMenu(player);
					}
					case 3 -> {
						playerTabs.put(player.getUniqueId(), "enchantments");
						playerPages.put(player.getUniqueId(), 0);
						openCreativeMenu(player);
					}
				}
			}
			// Navigation
			else if (slot == 45 && clicked.getType() == Material.ARROW) {
				playerPages.compute(player.getUniqueId(), (k, currentPage) -> Math.max(0, currentPage - 1));
				openCreativeMenu(player);
			}
			else if (slot == 53 && clicked.getType() == Material.ARROW) {
				playerPages.compute(player.getUniqueId(), (k, currentPage) -> currentPage + 1);
				openCreativeMenu(player);
			}
			// Item clicks - behave like creative
			else if (slot < 45) {
				// Left click - pick up item
				if (e.isLeftClick() && !e.isShiftClick()) {
					player.setItemOnCursor(clicked.clone());
				}
				// Shift click - add to inventory
				else if (e.isShiftClick()) {
					ItemStack toGive = clicked.clone();
					if (player.getInventory().firstEmpty() != -1) {
						player.getInventory().addItem(toGive);
						player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1f);
					}
				}
				// Middle click - clone with NBT
				else if (e.getClick() == ClickType.MIDDLE) {
					ItemStack toGive = clicked.clone();
					toGive.setAmount(toGive.getMaxStackSize());
					player.setItemOnCursor(toGive);
				}
			}
		}
		// Don't cancel clicks in player inventory (slots >= 54)
		// This allows normal inventory management
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getView().getTitle().startsWith(GUI_TITLE)) {
			Player player = (Player) event.getPlayer();

			// Clear cursor item like creative does
			if (player.getItemOnCursor().getType() != Material.AIR) {
				player.setItemOnCursor(new ItemStack(Material.AIR));
			}
		}
	}
}