package listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
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
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ItemReloader implements Listener {
	@EventHandler
	public void onItemPickup(EntityPickupItemEvent e) {
		if(!(e.getEntity() instanceof Player p)) return;

		ItemStack rebuilt = rebuild(e.getItem().getItemStack(), p);
		if(rebuilt != null) e.getItem().setItemStack(rebuilt);
	}

	/**
	 * Rebuilds the whole inventory a tick after the player lands, never on the event itself. A weapon whose
	 * lore quotes a live figure - the Hyperion's implosion damage - reads it off the player's ATTACK_DAMAGE
	 * attribute, and the modifiers their armour contributes are TRANSIENT: the server re-derives them when
	 * the player first ticks, which is after PlayerJoinEvent. Rebuilding inline wrote the figure for a naked
	 * player, so a Hyperion in full custom armour said 5.4 and imploded for 10.2 until the next slot switch.
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Utils.scheduleTask(() -> {
			if(!p.isOnline()) return;
			PlayerInventory inventory = p.getInventory();

			for(int i = 0; i < inventory.getSize(); i++) {
				ItemStack rebuilt = rebuild(inventory.getItem(i), p);
				if(rebuilt != null) inventory.setItem(i, rebuilt);
			}
		}, 1);
	}

	/**
	 * Rewrites the held weapon when the player's armour changes, for the same reason {@link #onItemHeld}
	 * does it on a slot switch: an armour piece carrying ATTACK_DAMAGE moves the implosion figure, and
	 * nothing else would rewrite the lore of an item they are already holding. A tick late, because the
	 * attribute modifiers the new piece contributes are not on the attribute yet while this event runs.
	 */
	@EventHandler
	public void onArmorChange(PlayerArmorChangeEvent e) {
		Player p = e.getPlayer();
		Utils.scheduleTask(() -> {
			if(!p.isOnline()) return;
			PlayerInventory inventory = p.getInventory();
			ItemStack rebuilt = rebuild(inventory.getItemInMainHand(), p);
			if(rebuilt != null) inventory.setItemInMainHand(rebuilt);
		}, 1);
	}

	/**
	 * Rewrites the item the player just switched to. Weapons whose lore quotes a live figure - the Hyperion's
	 * implosion damage - go stale when whatever it is read off changes, so the item is rebuilt on every slot
	 * switch as well as on join and on pickup.
	 */
	@EventHandler
	public void onItemHeld(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		PlayerInventory inventory = p.getInventory();
		ItemStack rebuilt = rebuild(inventory.getItem(e.getNewSlot()), p);
		if(rebuilt != null) inventory.setItem(e.getNewSlot(), rebuilt);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player p)) return;

		Utils.scheduleTask(() -> {
			ItemStack cursor = rebuild(p.getItemOnCursor(), p);
			if(cursor != null) p.setItemOnCursor(cursor);

			ItemStack current = rebuild(e.getCurrentItem(), p);
			if(current != null) e.setCurrentItem(current);
		}, 1);
	}

	/**
	 * Stamps whatever a creative-mode player pulls out of the creative menu, <b>as they pull it</b>. The
	 * creative tabs themselves are built by the client off its own item registry, so nothing here can touch
	 * how they render - but the stack the client then asks the server to put in a slot arrives as a bare
	 * vanilla item, and it arrives through the creative set-slot packet, which is not a click the other
	 * handlers see and does not go through {@code onSlotChange} either.
	 *
	 * <p>Handled inline rather than a tick later: the cursor here is the item being placed, and rewriting it
	 * is the only chance to stamp it before the client's own copy of the slot is authoritative.
	 */
	@EventHandler
	public void onCreativeSet(InventoryCreativeEvent e) {
		if(!(e.getWhoClicked() instanceof Player p)) return;
		ItemStack cursor = e.getCursor();
		if(cursor == null || cursor.getType().isAir()) return;

		// On a clone, because modifyVanillaArmor re-stats the stack in place and the id table does not cover
		// every material it handles - the Elytra has no counterpart, so rebuild hands back null for it and
		// the re-statting is only visible on the copy.
		ItemStack copy = cursor.clone();
		ItemStack stamped = rebuild(copy, p);
		if(stamped != null) {
			e.setCursor(stamped);
		} else if(!copy.equals(cursor)) {
			e.setCursor(copy);
		}
	}

	/**
	 * Stamps the result a crafting grid is showing. The result slot is filled by the recipe rather than out
	 * of anybody's inventory, so a vanilla craft - a diamond sword, a netherite helmet - sat there as a
	 * plain item and only picked up its id once it landed in a slot and {@code onSlotChange} caught it, a
	 * tick after the player had already seen the wrong texture.
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
	 * Rebuilds a single slot of the player's own inventory whenever anything changes it. This is the
	 * catch-all the other handlers cannot be: {@code onInventoryClick} only ever sees the clicked stack and
	 * the cursor, so an item <b>shift-clicked</b> somewhere - most visibly a crafting bench's result, which
	 * skips the cursor entirely - landed in the inventory with no id on it. Dragging, hotbar swaps, a
	 * hopper and {@code /give} were all equally invisible.
	 *
	 * <p><b>The equality test is what stops this looping.</b> Writing the slot fires this event again, so a
	 * rebuild that changed nothing must not write: {@code getItem()} hands back a fresh object every time,
	 * but an equal one, so an already-correct stack is left alone and the cycle ends. A stack that really
	 * did need rebuilding is written once and its re-fire settles on the second pass.
	 */
	@EventHandler
	public void onSlotChange(PlayerInventorySlotChangeEvent e) {
		Player p = e.getPlayer();
		int slot = e.getSlot();
		Utils.scheduleTask(() -> {
			if(!p.isOnline()) return;
			PlayerInventory inventory = p.getInventory();
			ItemStack current = inventory.getItem(slot);
			ItemStack rebuilt = rebuild(current, p);
			if(rebuilt != null && !rebuilt.equals(current)) inventory.setItem(slot, rebuilt);
		}, 1);
	}

	/**
	 * The one rebuild, and what every handler above calls: a custom item comes back from its own
	 * {@code getItem()}, a vanilla one is re-statted in place and stamped with its SkyBlock id.
	 * <b>Null means leave the slot alone</b> - nothing about the stack needed to change.
	 *
	 * <p>The two used to be an if/else repeated at each call site, which is how {@code onItemHeld} ended up
	 * as the one handler that rebuilt custom items but never touched vanilla ones.
	 *
	 * <p>It must stay <b>idempotent</b>: {@code onSlotChange} compares its result against what was already
	 * there and would loop if an unchanged stack came back unequal.
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
