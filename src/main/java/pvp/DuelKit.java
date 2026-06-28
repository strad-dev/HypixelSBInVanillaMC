package pvp;

import items.armor.GoldorLeggings;
import items.armor.MaxorBoots;
import items.armor.NecromancerLordLeggings;
import items.armor.NecronElytra;
import items.armor.PrimalDragonChestplate;
import items.armor.WardenHelmet;
import items.armor.WitherKingCrown;
import items.misc.AOTV;
import items.misc.BonzoStaff;
import items.misc.GyrokineticWand;
import items.misc.HolyIce;
import items.misc.IceSpray;
import items.misc.WandOfAtonement;
import items.weapons.Claymore;
import items.weapons.Scylla;
import items.weapons.Terminator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * The fixed 1v1 loadout. {@link #apply(Player)} wipes the inventory and lays out the standardized kit
 * so every duel is fought on equal footing; the player's real inventory is saved/restored by
 * {@link DuelManager}. Enchants are applied here (kit-only) by material type.
 */
public final class DuelKit {
	private DuelKit() {}

	public static void apply(Player p) {
		PvpLoadouts.apply(p, defaultLoadout());
	}

	/**
	 * The standard kit as a 41-slot loadout array (0-35 main, 36 helmet, 37 chest, 38 legs, 39 boots,
	 * 40 off-hand) - the default a PvP loadout starts from and resets to (same items/enchants the kit
	 * used before custom loadouts existed).
	 */
	public static ItemStack[] defaultLoadout() {
		ItemStack[] a = new ItemStack[41];
		a[0] = k(Scylla.getItem(Enchantment.SHARPNESS, 7));        // "sharpness hyperion"
		a[1] = k(AOTV.getItem());
		a[2] = k(IceSpray.getItem());
		a[3] = k(Claymore.getItem(Enchantment.SHARPNESS, 7));      // "sharpness dark claymore"
		a[4] = k(Terminator.getItem(7));
		a[5] = k(WandOfAtonement.getItem());
		a[6] = k(GyrokineticWand.getItem());
		a[7] = k(HolyIce.getItem());
		a[8] = new ItemStack(Material.GOLDEN_CARROT, 64);
		a[9] = k(WardenHelmet.getItem());
		a[10] = k(NecronElytra.getItem());
		a[11] = k(GoldorLeggings.getItem());
		a[28] = k(BonzoStaff.getItem());
		a[34] = new ItemStack(Material.WATER_BUCKET);
		a[36] = k(WitherKingCrown.getItem());
		a[37] = k(PrimalDragonChestplate.getItem());
		a[38] = k(NecromancerLordLeggings.getItem());
		a[39] = k(MaxorBoots.getItem());
		a[40] = new ItemStack(Material.TOTEM_OF_UNDYING);
		return a;
	}

	/** Applies the kit's standardized enchants to an item by material type, then returns it. */
	private static ItemStack k(ItemStack item) {
		if (item == null) return null;
		Material m = item.getType();
		String n = m.name();

		if (n.endsWith("_SWORD")) {
			// Force Sharpness VII (override any Smite/Bane), plus the standard sword set.
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
		return item;
	}
}
