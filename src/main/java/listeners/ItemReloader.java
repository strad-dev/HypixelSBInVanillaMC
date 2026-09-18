package listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import items.armor.*;
import items.ingredients.mining.*;
import items.ingredients.misc.*;
import items.ingredients.witherLords.*;
import items.misc.*;
import items.summonItems.*;
import items.weapons.Claymore;
import items.weapons.ManhuntHyperion;
import items.weapons.Scylla;
import items.weapons.SwordOfBadHealth;
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
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ItemReloader implements Listener {
	/**
	 * <b>THE item updater.</b>  Every slot a player owns plus their cursor, rebuilt and written back when it
	 * differs.  This replaced seven separate event handlers - pickup, join, armour change, slot switch,
	 * inventory click, creative set and slot change - which between them still missed cases and, worse,
	 * could not make the client believe them.
	 *
	 * <p>Driven by the four moments Hypixel refreshes on - <b>login, switching to a hotbar slot, picking an
	 * item up off the ground, and clicking in an inventory</b> - plus <b>an armour change</b>, which is ours
	 * alone because this plugin puts ATTACK_DAMAGE on armour and Hypixel does not.  No ticker: a repeating
	 * sweep of every online player would cost the server real work to catch changes that only ever happen
	 * on one of those actions anyway.
	 *
	 * <p><b>{@code updateInventory()} is the load-bearing line</b>, and its absence is why the per-event
	 * approach looked broken for so long.  {@code handleSetCreativeModeSlot} ends with
	 * {@code InventoryMenu.setRemoteSlot(...)}, and a held-slot switch behaves the same way: the server
	 * records the client as already holding that stack, so rewriting the slot afterwards finds nothing to
	 * broadcast and the client keeps rendering the item it made up.  {@code updateInventory} resets that
	 * record and resends the container, which is the only thing that defeats it.  Guarded on
	 * {@code changed}, so a settled inventory costs one comparison per slot and sends nothing.
	 */
	public static void sweep(Player p) {
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

		ItemStack cursor = p.getItemOnCursor();
		ItemStack rebuiltCursor = rebuild(cursor, p);
		if(rebuiltCursor != null && !rebuiltCursor.equals(cursor)) {
			p.setItemOnCursor(rebuiltCursor);
			changed = true;
		}

		if(changed) p.updateInventory();
	}

	/**
	 * Login.  A tick late, deliberately: the ATTACK_DAMAGE modifiers a player's armour contributes are
	 * TRANSIENT, re-derived when the entity first ticks, which is after PlayerJoinEvent.  Sweeping inline
	 * wrote every live figure for a naked player - a Hyperion in the full custom set said 5.4 and imploded
	 * for 10.2.
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Utils.scheduleTask(() -> {
			if(p.isOnline()) sweep(p);
		}, 1);
	}

	/** Switching to a hotbar slot.  The classic desync: without the sweep's updateInventory the client kept
	 *  showing whatever it had cached for that slot. */
	@EventHandler
	public void onItemHeld(PlayerItemHeldEvent e) {
		sweep(e.getPlayer());
	}

	/** Picking an item up off the ground. */
	@EventHandler
	public void onItemPickup(EntityPickupItemEvent e) {
		if(!(e.getEntity() instanceof Player p)) return;
		// The stack is still on the ground entity when this fires, so re-stat it there too - a vanilla
		// armour piece picked up has to arrive already carrying its attribute values.
		modifyVanillaArmor(e.getItem().getItemStack());
		Utils.scheduleTask(() -> {
			if(p.isOnline()) sweep(p);
		}, 1);
	}

	/**
	 * Changing armour - the one refresh point that is ours rather than Hypixel's, and it is here because
	 * this plugin puts <b>ATTACK_DAMAGE on armour</b> (the crown +3, the Primal chestplate +2, Necromancer
	 * leggings +2, Maxor boots +1).  That moves the Hyperion's implosion figure, and equipping by
	 * right-click is not a click, a pickup or a slot switch, so nothing else here would catch it: the lore
	 * sat stale until the next click.  A tick late, because the modifiers the new piece contributes are not
	 * on the attribute yet while the event runs.
	 */
	@EventHandler
	public void onArmorChange(PlayerArmorChangeEvent e) {
		Player p = e.getPlayer();
		Utils.scheduleTask(() -> {
			if(p.isOnline()) sweep(p);
		}, 1);
	}

	/**
	 * Clicking in an inventory.  A tick late because the click has not been applied yet while the event
	 * runs.  <b>This also covers the creative menu</b>: {@code InventoryCreativeEvent} extends
	 * {@code InventoryClickEvent}, so taking an item out of the creative palette arrives here, and the
	 * sweep's {@code updateInventory} is what finally makes the stamp visible.
	 */
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player p)) return;
		Utils.scheduleTask(() -> {
			if(p.isOnline()) sweep(p);
		}, 1);
	}

	/**
	 * Stamps the result a crafting grid is showing. The result slot is filled by the recipe rather than out
	 * of anybody's inventory, so a vanilla craft - a diamond sword, a netherite helmet - sat there as a
	 * plain item and only picked up its id once the player clicked it into a slot - a tick after they had
	 * already read the wrong texture off the result.
	 *
	 * <p>Only {@code stampVanilla}, deliberately: a custom result came out of its own {@code getItem()} with
	 * its id already on it, and rebuilding it here would fight {@code ManhuntListener.onPrepareCraft}, which
	 * writes the upgrade result on this same event.
	 */
	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent e) {
		ItemStack stamped = SkyblockId.stampVanilla(e.getInventory().getResult());
		if(stamped != null) e.getInventory().setResult(stamped);
	}

	/**
	 * The one rebuild, and what every handler above calls: a custom item comes back from its own
	 * {@code getItem()}, a vanilla one is re-statted in place and stamped with its SkyBlock id.
	 * <b>Null means leave the slot alone</b> - nothing about the stack needed to change.
	 *
	 * <p>The two used to be an if/else repeated at each call site, which is how {@code onItemHeld} ended up
	 * as the one handler that rebuilt custom items but never touched vanilla ones.
	 *
	 * <p>It must stay <b>idempotent</b>: {@link #sweep} compares its result against what is already in the
	 * slot, so a rebuild that never comes back equal would rewrite and resend all 41 slots every time.
	 */
	@Nullable
	private static ItemStack rebuild(ItemStack item, Player p) {
		if(item == null || item.getType().isAir()) return null;
		ItemStack refreshed = refreshItem(item, p);
		if(refreshed != null) return refreshed;
		// Mutates in place and covers materials the id table does not, so it runs either way.
		modifyVanillaArmor(item);
		return SkyblockId.stampVanilla(item);
	}

	public static void modifyVanillaArmor(ItemStack item) {
		if(item == null || item.getType().isAir()) return;
		if(item.hasItemMeta() && item.getItemMeta().hasLore()) {
			if(Utils.firstLorePlain(item.getItemMeta()).startsWith("skyblock/")) return;
		}

		Material mat = item.getType();
		String slotKey;
		double armorValue;
		double kbResistance = -1;
		EquipmentSlotGroup slotGroup;

		switch(mat) {
			case DIAMOND_HELMET -> {
				slotKey = "armor.helmet";
				armorValue = 3;
				slotGroup = EquipmentSlotGroup.HEAD;
			}
			case DIAMOND_CHESTPLATE -> {
				slotKey = "armor.chestplate";
				armorValue = 8;
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
				armorValue = 4;
				kbResistance = 0.1;
				slotGroup = EquipmentSlotGroup.HEAD;
			}
			case NETHERITE_CHESTPLATE -> {
				slotKey = "armor.chestplate";
				armorValue = 10;
				kbResistance = 0.1;
				slotGroup = EquipmentSlotGroup.CHEST;
			}
			case NETHERITE_LEGGINGS -> {
				slotKey = "armor.leggings";
				armorValue = 7;
				kbResistance = 0.1;
				slotGroup = EquipmentSlotGroup.LEGS;
			}
			case NETHERITE_BOOTS -> {
				slotKey = "armor.boots";
				armorValue = 4;
				kbResistance = 0.1;
				slotGroup = EquipmentSlotGroup.FEET;
			}
			case ELYTRA -> {
				slotKey = "armor.chestplate";
				armorValue = 4;
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
	 * As {@link #refreshItem(ItemStack)}, for an item that belongs to {@code p}. Only weapons whose lore
	 * quotes a live figure care who the owner is - the Hyperion writes out the implosion damage it would
	 * deal in <i>their</i> hands, so a rebuild without the player would reset that line to the figure for a
	 * player with no other damage modifiers. Saved-loadout refreshes have no live player and pass null.
	 */
	public static ItemStack refreshItem(ItemStack item, Player p) {
		if(item == null || item.getType().isAir()) return null;
		if(!item.hasItemMeta() || !item.getItemMeta().hasLore()) return null;

		String key = Utils.firstLorePlain(item.getItemMeta());

		// The weapons take the WHOLE enchantment map, not one enchantment picked out of it. This used to
		// choose Smite over Bane over Sharpness and hand that single pair to getItem, so a sword carrying
		// both showed one of them and the other silently vanished from the lore.
		ItemStack newItem = switch(key) {
			case "skyblock/combat/aspect_of_the_void" -> AOTV.getItem();
			case "skyblock/combat/scylla" -> Scylla.getItem(item.getEnchantments(), p);
			// Both Manhunt items carry state a plain getItem() would lose: the Hyperion its rung
			// (its material), the compass its target and the needle's last known position.
			case "skyblock/manhunt/hyperion" -> {
				// No rung means the ID was pasted onto something that is not one of the eight materials.
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
			// Was missing from this list entirely, so a saved Sword of Bad Health was the one weapon whose
			// lore never came back into step with the enchantments on it.
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
		return newItem;
	}
}
