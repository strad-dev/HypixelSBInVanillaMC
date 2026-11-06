package listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import items.VanillaGear;
import items.armor.*;
import items.ingredients.mining.*;
import items.ingredients.misc.*;
import items.ingredients.witherLords.*;
import items.misc.*;
import items.summonItems.*;
import items.weapons.*;
import items.weapons.Terminator;
import misc.SkyblockId;
import misc.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ItemReloader implements Listener {
	/**
	 * <b>THE item updater.</b> Every slot, rebuilt and written back when it differs. Replaced seven
	 * separate handlers (pickup, join, armour change, slot switch, inventory click, creative set, slot change) that
	 * still missed cases and couldn't make the client believe them.
	 *
	 * <p>Driven by the four moments Hypixel refreshes on (<b>login, hotbar switch, ground pickup, inventory click</b>)
	 * plus <b>armour change</b>, ours alone since we put ATTACK_DAMAGE on armour. No ticker: sweeping every player
	 * costs real work for changes that only happen on those actions.
	 *
	 * <p><b>{@code updateInventory()} is the load-bearing line</b>; without it the per-event approach looked broken.
	 * {@code handleSetCreativeModeSlot} ends with {@code InventoryMenu.setRemoteSlot(...)}, and a held-slot switch
	 * does the same: the server records the client as already holding that stack, so rewriting the slot broadcasts
	 * nothing. {@code updateInventory} resets that record and resends the container. Guarded on {@code changed}, so
	 * a settled inventory costs one comparison per slot.
	 */
	public static void sweep(Player p) {
		if(!p.getItemOnCursor().isEmpty()) return;
		PlayerInventory inventory = p.getInventory();
		boolean changed = false;

		for(int i = 0; i < inventory.getSize(); i++) {
			ItemStack current = inventory.getItem(i);
			ItemStack rebuilt = rebuild(current, p);
			if(rebuilt != null && !rebuilt.equals(current)) {
				inventory.setItem(i, rebuilt);
				changed = true;
			}
		}

		if(changed) p.updateInventory();
	}

	private static final Set<UUID> queued = new HashSet<>();

	/** One sweep per player per tick: breaking a chest minecart dropped 27 stacks, each pickup queuing its own. */
	private static void sweepNextTick(Player p) {
		if(!queued.add(p.getUniqueId())) return;
		Utils.scheduleTask(() -> {
			queued.remove(p.getUniqueId());
			if(p.isOnline()) sweep(p);
		}, 1);
	}

	/**
	 * Login. A tick late: armour's ATTACK_DAMAGE modifiers are TRANSIENT, re-derived on the entity's first tick,
	 * after PlayerJoinEvent. Sweeping inline wrote live figures for a naked player (a Hyperion in the full custom
	 * set said 5.4 and imploded for 10.2).
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		sweepNextTick(p);
	}

	/** Hotbar switch. Without the sweep's updateInventory the client kept showing what it had cached. */
	@EventHandler
	public void onItemHeld(PlayerItemHeldEvent e) {
		sweep(e.getPlayer());
	}

	/** Picking an item up off the ground. */
	@EventHandler
	public void onItemPickup(EntityPickupItemEvent e) {
		if(!(e.getEntity() instanceof Player p)) return;
		// Rebuilt IN PLACE before it lands: vanilla adds the stack object it showed this event, so a replaced stack is
		// ignored, and an unstamped one won't merge with the stamped copies already held (dropped dragon heads).
		// getItemStack() is a mirror of the ground stack; setItemMeta resets its patch first, so it ends up equal.
		ItemStack ground = e.getItem().getItemStack();
		ItemStack rebuilt = rebuild(ground, p);
		if(rebuilt != null && rebuilt.getType() == ground.getType()) ground.setItemMeta(rebuilt.getItemMeta());
		sweepNextTick(p);
	}

	/**
	 * Armour change: our refresh point, not Hypixel's, because we put <b>ATTACK_DAMAGE on armour</b> (crown +3,
	 * Primal chestplate +2, Necromancer leggings +2, Maxor boots +1). That moves the Hyperion's implosion figure,
	 * and a right-click equip is no click, pickup or slot switch, so the lore sat stale. A tick late: the new
	 * piece's modifiers aren't on the attribute yet during the event.
	 */
	@EventHandler
	public void onArmorChange(PlayerArmorChangeEvent e) {
		Player p = e.getPlayer();
		sweepNextTick(p);
	}

	/**
	 * Inventory click. A tick late: the click isn't applied yet. <b>Also covers the creative menu</b>:
	 * {@code InventoryCreativeEvent} extends {@code InventoryClickEvent}, and the sweep's {@code updateInventory}
	 * is what makes the stamp visible.
	 */
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player p)) return;
		sweepNextTick(p);
	}

	@EventHandler
	public void onInventoryClose(org.bukkit.event.inventory.InventoryCloseEvent e) {
		if(e.getPlayer() instanceof Player p) sweepNextTick(p);
	}

	/**
	 * Stamps the crafting grid's result. It comes from the recipe, not an inventory, so a vanilla craft sat there
	 * unstamped until clicked into a slot, after the player had seen the wrong texture.
	 *
	 * <p>Only {@code dressVanilla}: a custom result already has its id from {@code getItem()}, and rebuilding it
	 * would fight {@code ManhuntListener.onPrepareCraft}, which writes the upgrade result on this event.
	 */
	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent e) {
		ItemStack stamped = dressVanilla(e.getInventory().getResult());
		if(stamped != null) e.getInventory().setResult(stamped);
	}

	/** Anvil, smithing and grindstone results carry the input's lore (Sharpness added, diamond -> netherite). After
	 *  {@code BetterAnvil}. */
	@EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
	public void onPrepareAnvil(org.bukkit.event.inventory.PrepareAnvilEvent e) {
		ItemStack dressed = dressVanilla(e.getResult());
		if(dressed != null) e.setResult(dressed);
	}

	@EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
	public void onPrepareSmithing(org.bukkit.event.inventory.PrepareSmithingEvent e) {
		if(manhunt.ManhuntTier.of(e.getInventory().getInputEquipment()) != null) {
			e.setResult(null);
			return;
		}
		ItemStack dressed = dressVanilla(e.getResult());
		if(dressed != null) e.setResult(dressed);
	}

	/** Enchanting-table result, relored in the table. A tick late: the enchants land after the event. */
	@EventHandler(ignoreCancelled = true)
	public void onEnchantItem(org.bukkit.event.enchantment.EnchantItemEvent e) {
		Player p = e.getEnchanter();
		org.bukkit.inventory.Inventory table = e.getInventory();
		Utils.scheduleTask(() -> {
			ItemStack current = table.getItem(0);
			ItemStack rebuilt = rebuild(current, p);
			if(rebuilt != null && !rebuilt.equals(current)) table.setItem(0, rebuilt);
		}, 1);
	}

	@EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
	public void onPrepareGrindstone(org.bukkit.event.inventory.PrepareGrindstoneEvent e) {
		ItemStack dressed = dressVanilla(e.getResult());
		if(dressed != null) e.setResult(dressed);
	}

	/**
	 * The one rebuild every handler calls: a custom item comes back from its own {@code getItem()}, a vanilla one
	 * is re-statted in place and stamped with its SkyBlock id. <b>Null = leave the slot alone.</b> This used to be
	 * an if/else per call site, which is how {@code onItemHeld} ended up never touching vanilla items.
	 *
	 * <p>Must stay <b>idempotent</b>: {@link #sweep} compares against the slot, so a rebuild that never comes back
	 * equal would rewrite and resend all 41 slots every time.
	 */
	@Nullable
	private static ItemStack rebuild(ItemStack item, Player p) {
		if(item == null || item.getType().isAir()) return null;
		ItemStack refreshed = refreshItem(item, p);
		if(refreshed != null) return refreshed;
		return dressVanilla(item);
	}

	/** A vanilla stack re-statted, dressed and stamped, as a copy; null when it already is. */
	@Nullable
	public static ItemStack dressVanilla(@Nullable ItemStack item) {
		if(item == null || item.getType().isAir()) return null;
		ItemStack out = item.clone();
		modifyVanillaArmor(out);
		VanillaGear.dress(out);
		ItemStack stamped = SkyblockId.stampVanilla(out);
		if(stamped != null) out = stamped;
		return out.equals(item) ? null : out;
	}

	public static void modifyVanillaArmor(ItemStack item) {
		if(item == null || item.getType().isAir()) return;
		if(Utils.firstLorePlain(item).startsWith("skyblock/")) return;

		Material mat = item.getType();
		String slotKey;
		double armorValue;
		double kbResistance = -1;
		EquipmentSlotGroup slotGroup;

		switch(mat) {
			case IRON_HELMET -> {
				slotKey = "armor.helmet";
				armorValue = 3;
				slotGroup = EquipmentSlotGroup.HEAD;
			}
			case IRON_CHESTPLATE -> {
				slotKey = "armor.chestplate";
				armorValue = 5;
				slotGroup = EquipmentSlotGroup.CHEST;
			}
			case IRON_LEGGINGS -> {
				slotKey = "armor.leggings";
				armorValue = 5;
				slotGroup = EquipmentSlotGroup.LEGS;
			}
			case IRON_BOOTS -> {
				slotKey = "armor.boots";
				armorValue = 2;
				slotGroup = EquipmentSlotGroup.FEET;
			}
			case DIAMOND_HELMET -> {
				slotKey = "armor.helmet";
				armorValue = 4;
				slotGroup = EquipmentSlotGroup.HEAD;
			}
			case DIAMOND_CHESTPLATE -> {
				slotKey = "armor.chestplate";
				armorValue = 7;
				slotGroup = EquipmentSlotGroup.CHEST;
			}
			case DIAMOND_LEGGINGS -> {
				slotKey = "armor.leggings";
				armorValue = 6;
				slotGroup = EquipmentSlotGroup.LEGS;
			}
			case DIAMOND_BOOTS -> {
				slotKey = "armor.boots";
				armorValue = 3;
				slotGroup = EquipmentSlotGroup.FEET;
			}
			case NETHERITE_HELMET -> {
				slotKey = "armor.helmet";
				armorValue = 5.25;
				kbResistance = 0.1;
				slotGroup = EquipmentSlotGroup.HEAD;
			}
			case NETHERITE_CHESTPLATE -> {
				slotKey = "armor.chestplate";
				armorValue = 8.25;
				kbResistance = 0.1;
				slotGroup = EquipmentSlotGroup.CHEST;
			}
			case NETHERITE_LEGGINGS -> {
				slotKey = "armor.leggings";
				armorValue = 7.25;
				kbResistance = 0.1;
				slotGroup = EquipmentSlotGroup.LEGS;
			}
			case NETHERITE_BOOTS -> {
				slotKey = "armor.boots";
				armorValue = 4.25;
				kbResistance = 0.1;
				slotGroup = EquipmentSlotGroup.FEET;
			}
			case ELYTRA -> {
				slotKey = "armor.chestplate";
				armorValue = 3;
				slotGroup = EquipmentSlotGroup.CHEST;
			}
			default -> {
				return;
			}
		}

		ItemMeta meta = item.getItemMeta();

		// Remove existing armor modifiers
		if(meta.getAttributeModifiers(Attribute.ARMOR) != null) {
			for(AttributeModifier mod : List.copyOf(meta.getAttributeModifiers(Attribute.ARMOR))) {
				meta.removeAttributeModifier(Attribute.ARMOR, mod);
			}
		}
		// Remove existing toughness modifiers
		if(meta.getAttributeModifiers(Attribute.ARMOR_TOUGHNESS) != null) {
			for(AttributeModifier mod : List.copyOf(meta.getAttributeModifiers(Attribute.ARMOR_TOUGHNESS))) {
				meta.removeAttributeModifier(Attribute.ARMOR_TOUGHNESS, mod);
			}
		}
		// Remove existing knockback resistance modifiers
		if(meta.getAttributeModifiers(Attribute.KNOCKBACK_RESISTANCE) != null) {
			for(AttributeModifier mod : List.copyOf(meta.getAttributeModifiers(Attribute.KNOCKBACK_RESISTANCE))) {
				meta.removeAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, mod);
			}
		}

		// Add correct armor value
		meta.addAttributeModifier(Attribute.ARMOR, new AttributeModifier(NamespacedKey.minecraft(slotKey), armorValue, AttributeModifier.Operation.ADD_NUMBER, slotGroup));

		// Add knockback resistance for netherite
		if(kbResistance > 0) {
			meta.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, new AttributeModifier(NamespacedKey.minecraft(slotKey), kbResistance, AttributeModifier.Operation.ADD_NUMBER, slotGroup));
		}

		item.setItemMeta(meta);
	}

	/**
	 * Refreshes a custom item by replacing it with a fresh copy from getItem(),
	 * preserving enchantments and stack size. Returns null if the item is not a custom item.
	 */
	public static ItemStack refreshItem(ItemStack item) {
		return refreshItem(item, null);
	}

	/**
	 * As {@link #refreshItem(ItemStack)}, for {@code p}'s item. Only live-figure lore cares: the Hyperion quotes
	 * its implosion damage in <i>their</i> hands, which without a player resets to the no-modifier figure.
	 * Saved-loadout refreshes have no live player and pass null.
	 */
	public static ItemStack refreshItem(ItemStack item, Player p) {
		if(item == null || item.getType().isAir()) return null;
		String key = Utils.firstLorePlain(item);
		if(key.isEmpty()) return null;

		// Weapons take the WHOLE enchantment map. This used to pick one of Smite/Bane/Sharpness, so a sword with
		// two showed one and the other vanished from the lore.
		ItemStack newItem = switch(key) {
			case "skyblock/combat/aspect_of_the_void" -> AOTV.getItem();
			case "skyblock/combat/scylla" -> Scylla.getItem(item.getEnchantments(), p);
			// Both Manhunt items carry state getItem() would lose: the Hyperion its rung (material), the compass its
			// target and the needle's last position.
			case "skyblock/manhunt/hyperion" -> {
				// No rung: the ID was pasted onto something not one of the eight materials.
				manhunt.ManhuntTier tier = manhunt.ManhuntTier.of(item);
				yield tier == null ? null : ManhuntHyperion.getItem(tier, item.getEnchantments(), p);
			}
			case "skyblock/manhunt/compass" -> ManhuntCompass.refresh(item);
			case "skyblock/combat/terminator" -> Terminator.getItem(item.getEnchantments());
			case "skyblock/combat/ice_spray_wand" -> IceSpray.getItem();
			case "skyblock/combat/wand_of_restoration" -> WandOfRestoration.getItem();
			case "skyblock/combat/wand_of_atonement" -> WandOfAtonement.getItem();
			case "skyblock/combat/divan_pickaxe" -> DivanPickaxe.getItem();
			case "skyblock/combat/holy_ice" -> HolyIce.getItem();
			case "skyblock/combat/bonzo_staff" -> BonzoStaff.getItem();
			case "skyblock/combat/tactical_insertion" -> TacticalInsertion.getItem();
			case "skyblock/combat/gyro" -> GyrokineticWand.getItem();
			case "skyblock/combat/dark_claymore" -> Claymore.getItem(item.getEnchantments());
			// Was missing here, so a saved Sword of Bad Health's lore never matched its enchantments.
			case "skyblock/combat/sword_of_bad_health" -> SwordOfBadHealth.getItem(item.getEnchantments());
			case "skyblock/combat/warden_helmet" -> WardenHelmet.getItem();
			case "skyblock/combat/wither_king_crown" -> WitherKingCrown.getItem();
			case "skyblock/combat/necron_elytra" -> NecronElytra.getItem();
			case "skyblock/combat/primal_chestplate" -> PrimalDragonChestplate.getItem();
			case "skyblock/combat/goldor_pants" -> GoldorLeggings.getItem();
			case "skyblock/combat/necromancer_pants" -> NecromancerLordLeggings.getItem();
			case "skyblock/combat/maxor_boots" -> MaxorBoots.getItem();
			case "skyblock/ingredient/shadow_warp" -> ShadowWarp.getItem();
			case "skyblock/ingredient/implosion" -> Implosion.getItem();
			case "skyblock/ingredient/wither_shield" -> WitherShield.getItem();
			case "skyblock/ingredient/necron_handle" -> Handle.getItem();
			case "skyblock/ingredient/giant_sword_remnant" -> GiantSwordRemnant.getItem();
			case "skyblock/ingredient/necromancer_brooch" -> NecromancerBrooch.getItem();
			case "skyblock/ingredient/maxor_secrets" -> MaxorSecrets.getItem();
			case "skyblock/ingredient/storm_secrets" -> StormSecrets.getItem();
			case "skyblock/ingredient/goldor_secrets" -> GoldorSecrets.getItem();
			case "skyblock/ingredient/necron_secrets" -> NecronSecrets.getItem();
			case "skyblock/ingredient/ancient_dragon_egg" -> AncientDragonEgg.getItem();
			case "skyblock/ingredient/warden_heart" -> WardenHeart.getItem();
			case "skyblock/ingredient/judgement_core" -> Core.getItem();
			case "skyblock/ingredient/tessellated_pearl" -> TessellatedPearl.getItem();
			case "skyblock/ingredient/null_ovoid" -> NullOvoid.getItem();
			case "skyblock/ingredient/null_blade" -> NullBlade.getItem();
			case "skyblock/ingredient/braided_feather" -> BraidedFeather.getItem();
			case "skyblock/ingredient/tarantula_silk" -> TarantulaSilk.getItem();
			case "skyblock/ingredient/revenant_viscera" -> Viscera.getItem();
			case "skyblock/ingredient/alloy" -> Alloy.getItem();
			case "skyblock/ingredient/concentrated_stone" -> ConcentratedStone.getItem();
			case "skyblock/ingredient/refined_diamond" -> RefinedDiamond.getItem();
			case "skyblock/ingredient/refined_emerald" -> RefinedEmerald.getItem();
			case "skyblock/ingredient/refined_gold" -> RefinedGold.getItem();
			case "skyblock/ingredient/refined_iron" -> RefinedIron.getItem();
			case "skyblock/ingredient/refined_lapis" -> RefinedLapis.getItem();
			case "skyblock/ingredient/refined_netherite" -> RefinedNetherite.getItem();
			case "skyblock/ingredient/refined_redstone" -> RefinedRedstone.getItem();
			case "skyblock/ingredient/enchantment_upgrader" -> EnchantmentUpgrader.getItem();
			case "skyblock/summon/superior_remnant" -> SuperiorRemnant.getItem();
			case "skyblock/summon/corrupt_pearl" -> CorruptPearl.getItem();
			case "skyblock/summon/antimatter" -> Antimatter.getItem();
			case "skyblock/summon/omega_egg" -> OmegaEgg.getItem();
			case "skyblock/summon/spider_relic" -> SpiderRelic.getItem();
			case "skyblock/summon/atoned_flesh" -> AtonedFlesh.getItem();
			case "skyblock/summon/giant_flesh" -> GiantZombieFlesh.getItem();
			default -> null;
		};

		if(newItem == null) return null;

		// Only preserve enchantments for items that don't use glint override (real enchantable weapons)
		if(!newItem.getItemMeta().hasEnchantmentGlintOverride()) {
			Map<Enchantment, Integer> enchants = item.getEnchantments();
			newItem.addUnsafeEnchantments(enchants);
		}
		newItem.setAmount(item.getAmount());
		Utils.enchantsBelowStats(newItem);
		return newItem;
	}
}
