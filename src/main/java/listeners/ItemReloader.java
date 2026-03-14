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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class ItemReloader implements Listener {
	@EventHandler
	public void onItemPickup(EntityPickupItemEvent e) {
		if(!(e.getEntity() instanceof Player p)) return;

		ItemStack item = e.getItem().getItemStack();
		ItemStack refreshed = refreshItem(item);
		if(refreshed != null) {
			e.getItem().setItemStack(refreshed);
		} else {
			modifyVanillaArmor(e.getItem().getItemStack());
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		PlayerInventory inventory = p.getInventory();

		for(int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if(item == null) continue;
			ItemStack refreshed = refreshItem(item);
			if(refreshed != null) {
				inventory.setItem(i, refreshed);
			} else {
				modifyVanillaArmor(item);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player p)) return;

		Utils.scheduleTask(() -> {
			ItemStack cursor = p.getItemOnCursor();
			if(!cursor.getType().isAir()) {
				ItemStack refreshed = refreshItem(cursor);
				if(refreshed != null) {
					p.setItemOnCursor(refreshed);
				} else {
					modifyVanillaArmor(cursor);
				}
			}

			ItemStack current = e.getCurrentItem();
			if(current != null && !current.getType().isAir()) {
				ItemStack refreshed = refreshItem(current);
				if(refreshed != null) {
					e.setCurrentItem(refreshed);
				} else {
					modifyVanillaArmor(current);
				}
			}
		}, 1);
	}

	public static void modifyVanillaArmor(ItemStack item) {
		if(item == null || item.getType().isAir()) return;
		if(item.hasItemMeta() && item.getItemMeta().hasLore()) {
			List<String> lore = item.getItemMeta().getLore();
			if(lore != null && !lore.isEmpty() && lore.getFirst().startsWith("skyblock/")) return;
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
		if(item == null || item.getType().isAir()) return null;
		if(!item.hasItemMeta() || !item.getItemMeta().hasLore()) return null;

		List<String> lore = item.getItemMeta().getLore();
		String key = lore.getFirst();

		Enchantment ench = Enchantment.SHARPNESS;
		if(item.getEnchantments().containsKey(Enchantment.SMITE)) {
			ench = Enchantment.SMITE;
		} else if(item.getEnchantments().containsKey(Enchantment.BANE_OF_ARTHROPODS)) {
			ench = Enchantment.BANE_OF_ARTHROPODS;
		}

		ItemStack newItem = switch(key) {
			case "skyblock/combat/aspect_of_the_void" -> AOTV.getItem();
			case "skyblock/combat/scylla" -> Scylla.getItem(ench, item.getEnchantmentLevel(ench));
			case "skyblock/combat/terminator" -> Terminator.getItem(item.getEnchantmentLevel(Enchantment.POWER));
			case "skyblock/combat/ice_spray_wand" -> IceSpray.getItem();
			case "skyblock/combat/wand_of_restoration" -> WandOfRestoration.getItem();
			case "skyblock/combat/wand_of_atonement" -> WandOfAtonement.getItem();
			case "skyblock/combat/divan_pickaxe" -> DivanPickaxe.getItem();
			case "skyblock/combat/holy_ice" -> HolyIce.getItem();
			case "skyblock/combat/bonzo_staff" -> BonzoStaff.getItem();
			case "skyblock/combat/tactical_insertion" -> TacticalInsertion.getItem();
			case "skyblock/combat/gyro" -> GyrokineticWand.getItem();
			case "skyblock/combat/dark_claymore" -> Claymore.getItem(ench, item.getEnchantmentLevel(ench));
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
