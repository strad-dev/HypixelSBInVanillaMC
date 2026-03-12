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
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Map;

public class ItemReloader implements Listener {
	@EventHandler
	public void onItemPickup(EntityPickupItemEvent e) {
		if (!(e.getEntity() instanceof Player p)) return;

		ItemStack item = e.getItem().getItemStack();
		ItemStack refreshed = refreshItem(item);
		if (refreshed != null) {
			e.getItem().setItemStack(refreshed);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		PlayerInventory inventory = p.getInventory();

		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if (item == null) continue;
			ItemStack refreshed = refreshItem(item);
			if (refreshed != null) {
				inventory.setItem(i, refreshed);
			}
		}
	}

	/**
	 * Refreshes a custom item by replacing it with a fresh copy from getItem(),
	 * preserving enchantments and stack size. Returns null if the item is not a custom item.
	 */
	public static ItemStack refreshItem(ItemStack item) {
		if (item == null || item.getType().isAir()) return null;
		if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return null;

		List<String> lore = item.getItemMeta().getLore();
		String key = lore.getFirst();

		Enchantment ench = Enchantment.SHARPNESS;
		if (item.getEnchantments().containsKey(Enchantment.SMITE)) {
			ench = Enchantment.SMITE;
		} else if (item.getEnchantments().containsKey(Enchantment.BANE_OF_ARTHROPODS)) {
			ench = Enchantment.BANE_OF_ARTHROPODS;
		}

		ItemStack newItem = switch (key) {
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

		if (newItem == null) return null;

		// Only preserve enchantments for items that don't use glint override (real enchantable weapons)
		if (!newItem.getItemMeta().hasEnchantmentGlintOverride()) {
			Map<Enchantment, Integer> enchants = item.getEnchantments();
			newItem.addUnsafeEnchantments(enchants);
		}
		newItem.setAmount(item.getAmount());
		return newItem;
	}
}
