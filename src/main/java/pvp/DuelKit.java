package pvp;

import items.armor.*;
import items.misc.*;
import items.weapons.Claymore;
import items.weapons.Scylla;
import items.weapons.SwordOfBadHealth;
import items.weapons.Terminator;
import listeners.ItemReloader;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Fixed 1v1 kit. {@link #apply(Player)} wipes the inventory and lays it out; {@link DuelManager} saves and
 * restores the real one. Kit enchants are applied here by material type.
 */
public final class DuelKit {
	private DuelKit() {}

	public static void apply(Player p) {
		PvpLoadouts.apply(p, defaultLoadout());
	}

	/**
	 * Kit as a 41-slot array (0-35 main, 36 helmet, 37 chest, 38 legs, 39 boots, 40 off-hand): what a loadout
	 * starts from and resets to.
	 */
	public static ItemStack[] defaultLoadout() {
		ItemStack[] a = new ItemStack[41];
		a[0] = k(Scylla.getItem());        // "sharpness hyperion"
		a[1] = k(AOTV.getItem());
		a[2] = k(IceSpray.getItem());
		a[3] = k(Claymore.getItem());      // "sharpness dark claymore"
		a[4] = k(Terminator.getItem());
		a[5] = k(WandOfAtonement.getItem());
		a[6] = k(SwordOfBadHealth.getItem());
		a[7] = k(HolyIce.getItem());
		a[8] = new ItemStack(Material.GOLDEN_CARROT, 64);
		a[9] = k(WardenHelmet.getItem());
		a[10] = k(NecronElytra.getItem());
		a[11] = k(GoldorLeggings.getItem());
		a[28] = k(BonzoStaff.getItem());
		a[33] = k(GyrokineticWand.getItem());
		a[34] = new ItemStack(Material.WATER_BUCKET);
		a[36] = k(WitherKingCrown.getItem());
		a[37] = k(PrimalDragonChestplate.getItem());
		a[38] = k(NecromancerLordLeggings.getItem());
		a[39] = k(MaxorBoots.getItem());
		a[40] = new ItemStack(Material.TOTEM_OF_UNDYING);
		return a;
	}

	/** Kit enchants by material type. */
	private static ItemStack k(ItemStack item) {
		if (item == null) return null;
		Material m = item.getType();
		String n = m.name();

		if (n.endsWith("_SWORD")) {
			// Force Sharpness VII (overrides Smite/Bane), plus the sword set.
			item.removeEnchantment(Enchantment.SMITE);
			item.removeEnchantment(Enchantment.BANE_OF_ARTHROPODS);
			item.addUnsafeEnchantment(Enchantment.SHARPNESS, 7);
			item.addUnsafeEnchantment(Enchantment.SWEEPING_EDGE, 4);
			item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
			item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
		}

		boolean helmet = n.endsWith("_HELMET");
		boolean chest = n.endsWith("_CHESTPLATE") || m == Material.ELYTRA;
		boolean legs = n.endsWith("_LEGGINGS");
		boolean boots = n.endsWith("_BOOTS");
		if (helmet || chest || legs || boots) {
			item.addUnsafeEnchantment(Enchantment.PROTECTION, 5);
		}
		if (helmet) {
			item.addUnsafeEnchantment(Enchantment.AQUA_AFFINITY, 1);
			item.addUnsafeEnchantment(Enchantment.RESPIRATION, 3);
		}
		if (legs) {
			item.addUnsafeEnchantment(Enchantment.SWIFT_SNEAK, 3);
		}
		if (boots) {
			item.addUnsafeEnchantment(Enchantment.FEATHER_FALLING, 5);
			item.addUnsafeEnchantment(Enchantment.SOUL_SPEED, 3);
			item.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 3);
		}
		if (n.endsWith("_SHOVEL") || n.endsWith("_PICKAXE")) {
			item.addUnsafeEnchantment(Enchantment.EFFICIENCY, 6);
		}
		if (m == Material.BOW) {
			item.addUnsafeEnchantment(Enchantment.POWER, 7);
			item.addUnsafeEnchantment(Enchantment.FLAME, 1);
			item.addUnsafeEnchantment(Enchantment.PUNCH, 2);
		}
		// Enchanting here leaves stale LORE (the kit Claymore read "Damage: +9" with Sharpness VII), so rebuild,
		// which regenerates lore from the enchants on the stack.
		ItemStack refreshed = ItemReloader.refreshItem(item);
		return refreshed != null ? refreshed : item;
	}
}
