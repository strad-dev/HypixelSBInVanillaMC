package misc;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Real Hypixel item id for each custom item, and the one place deciding which have one.
 *
 * <p>Written into {@code minecraft:custom_data} as a bare top-level {@code id}, no {@code ExtraAttributes}
 * wrapper: that is where SkyblockAPI reads it, and Catharsis resolves a texture from {@code id} alone,
 * lower-cased, not gated on being on Hypixel. <b>Same key and place as M7 TAS</b>, so one pack covers both.
 *
 * <p><b>Keyed on the lore id</b>, so a build method just calls {@code stamp(...)}. Exception: the Manhunt
 * Hyperion's eight rungs share one lore id, so its id lives on {@link manhunt.ManhuntTier}.
 *
 * <p>Absent = <b>no real counterpart</b> (Necron's Elytra, Ancient Dragon Egg, Wither Lords' secrets, six of seven
 * Refined metals, Manhunt Compass). Those get nothing, since a wrong id retextures an item as the wrong thing.
 * Every id was checked against {@code api.hypixel.net/v2/resources/skyblock/items}.
 *
 * <p>Saved loadouts match on the first lore line, not this, so changing a row needs no migration; items pick it
 * up on the next {@code ItemReloader} pass.
 */
public final class SkyblockId {
	private SkyblockId() {}

	private static final Map<String, String> IDS = Map.ofEntries(
			// Weapons and abilities
			Map.entry("skyblock/combat/scylla", "HYPERION"),
			Map.entry("skyblock/combat/dark_claymore", "DARK_CLAYMORE"),
			Map.entry("skyblock/combat/terminator", "TERMINATOR"),
			Map.entry("skyblock/combat/sword_of_bad_health", "SWORD_OF_BAD_HEALTH"),
			Map.entry("skyblock/combat/aspect_of_the_void", "ASPECT_OF_THE_VOID"),
			// The STARRED_ (dungeon-upgraded) variants, the same ones M7 TAS uses.
			Map.entry("skyblock/combat/bonzo_staff", "STARRED_BONZO_STAFF"),
			Map.entry("skyblock/combat/ice_spray_wand", "STARRED_ICE_SPRAY_WAND"),
			Map.entry("skyblock/combat/holy_ice", "HOLY_ICE"),
			Map.entry("skyblock/combat/gyro", "GYROKINETIC_WAND"),
			Map.entry("skyblock/combat/tactical_insertion", "TACTICAL_INSERTION"),
			Map.entry("skyblock/combat/wand_of_restoration", "WAND_OF_RESTORATION"),
			Map.entry("skyblock/combat/wand_of_atonement", "WAND_OF_ATONEMENT"),
			// Hypixel's Divan tool is a drill; ours is a pickaxe, and borrows it deliberately.
			Map.entry("skyblock/combat/divan_pickaxe", "DIVAN_DRILL"),

			// Armour.  The Wither Lords' sets are named after the STAT on Hypixel, not the lord: Necron's is
			// POWER, Storm's WISE, Goldor's TANK, Maxor's SPEED.
			Map.entry("skyblock/combat/warden_helmet", "WARDEN_HELMET"),
			Map.entry("skyblock/combat/goldor_pants", "TANK_WITHER_LEGGINGS"),
			Map.entry("skyblock/combat/maxor_boots", "SPEED_WITHER_BOOTS"),
			Map.entry("skyblock/combat/necromancer_pants", "NECROMANCER_LORD_LEGGINGS"),
			// Not a Primal Dragon or a Wither King on Hypixel, so these two borrow the nearest real piece:
			// Necron's Chestplate, and the Crown of Greed.
			Map.entry("skyblock/combat/primal_chestplate", "POWER_WITHER_CHESTPLATE"),
			Map.entry("skyblock/combat/wither_king_crown", "CROWN_OF_AVARICE"),

			// Ingredients.  The three Wither Lord abilities are SCROLLs on Hypixel.
			Map.entry("skyblock/ingredient/implosion", "IMPLOSION_SCROLL"),
			Map.entry("skyblock/ingredient/shadow_warp", "SHADOW_WARP_SCROLL"),
			Map.entry("skyblock/ingredient/wither_shield", "WITHER_SHIELD_SCROLL"),
			Map.entry("skyblock/ingredient/necron_handle", "NECRON_HANDLE"),
			Map.entry("skyblock/ingredient/necromancer_brooch", "NECROMANCER_BROOCH"),
			Map.entry("skyblock/ingredient/judgement_core", "JUDGEMENT_CORE"),
			Map.entry("skyblock/ingredient/tessellated_pearl", "TESSELLATED_ENDER_PEARL"),
			Map.entry("skyblock/ingredient/null_ovoid", "NULL_OVOID"),
			Map.entry("skyblock/ingredient/null_blade", "NULL_BLADE"),
			Map.entry("skyblock/ingredient/tarantula_silk", "TARANTULA_SILK"),
			Map.entry("skyblock/ingredient/revenant_viscera", "REVENANT_VISCERA"),
			Map.entry("skyblock/ingredient/warden_heart", "WARDEN_HEART"),
			Map.entry("skyblock/ingredient/braided_feather", "BRAIDED_GRIFFIN_FEATHER"),
			Map.entry("skyblock/ingredient/enchantment_upgrader", "EXCEEDINGLY_RARE_ENDER_ARTIFACT_UPGRADER"),
			Map.entry("skyblock/ingredient/alloy", "DIVAN_ALLOY"),
			Map.entry("skyblock/ingredient/concentrated_stone", "CONCENTRATED_STONE"),
			// The only Refined metal Hypixel has.  Its others are Mithril, Titanium, Umber, Tungsten and
			// Mineral - none of which is our Gold, Iron, Lapis, Redstone, Emerald or Netherite.
			Map.entry("skyblock/ingredient/refined_diamond", "REFINED_DIAMOND"),

			// Summons.  Each is the real drop the summon is made of, not the mob it calls up.
			Map.entry("skyblock/summon/omega_egg", "OMEGA_EGG"),
			Map.entry("skyblock/summon/superior_remnant", "SUPERIOR_FRAGMENT"),
			Map.entry("skyblock/summon/spider_relic", "ARACHNE_CRYSTAL"),
			Map.entry("skyblock/summon/corrupt_pearl", "NULL_SPHERE"),
			Map.entry("skyblock/summon/atoned_flesh", "REVENANT_FLESH"),
			Map.entry("skyblock/summon/giant_flesh", "PREMIUM_FLESH")
	);

	/**
	 * Writes the SkyBlock id for {@code item}'s lore id and returns the stack; no row = untouched. Call it
	 * <b>last</b>: the write goes through NMS and returns a copy, so later edits to the original are lost.
	 */
	public static ItemStack stamp(ItemStack item) {
		if(item == null || !item.hasItemMeta()) return item;
		ItemMeta meta = item.getItemMeta();
		if(!meta.hasLore()) return item;
		return stamp(item, of(Utils.firstLorePlain(meta)));
	}

	/** As {@link #stamp(ItemStack)}, for an item whose SkyBlock id is not its lore id's - the Manhunt Hyperion. */
	public static ItemStack stamp(ItemStack item, @Nullable String skyblockId) {
		if(item == null || skyblockId == null) return item;
		return NBT.modify(item, nbt -> nbt.putString("id", skyblockId));
	}

	/**
	 * As {@link #stamp(ItemStack)}, plus extras in the same compound (AOTV's Etherwarp and reforge keys). One write,
	 * so extras can't land on a stack that missed its id.
	 */
	public static ItemStack stamp(ItemStack item, Consumer<CompoundTag> extra) {
		if(item == null || !item.hasItemMeta()) return item;
		ItemMeta meta = item.getItemMeta();
		String id = meta.hasLore() ? of(Utils.firstLorePlain(meta)) : null;
		return NBT.modify(item, nbt -> {
			if(id != null) nbt.putString("id", id);
			extra.accept(nbt);
		});
	}

	/** The real Hypixel id for a lore id, or null if that item has no counterpart. */
	@Nullable
	public static String of(String loreId) {
		return IDS.get(loreId);
	}

	/**
	 * What a <b>plain vanilla</b> tool, weapon or armour piece is dressed up as. Only the id changes: a diamond sword
	 * is still a diamond sword to the server, it just renders as a Giant's Sword with the pack.
	 *
	 * <p>Each material ladder reads as a progression. Rungs with no sensible counterpart are absent (netherite axe;
	 * stone, golden, wooden shovels).
	 *
	 * <p><b>Sharing a material with a custom item is fine</b>: {@link #stampVanilla} refuses anything with a
	 * {@code skyblock/} lore line, so the Hyperion doesn't become Necron's Blade.
	 */
	private static final Map<Material, String> VANILLA = Map.ofEntries(
			Map.entry(Material.NETHERITE_SWORD, "NECRON_BLADE"),
			Map.entry(Material.DIAMOND_SWORD, "GIANTS_SWORD"),
			Map.entry(Material.GOLDEN_SWORD, "STARRED_MIDAS_SWORD"),
			// Plural name, SINGULAR id - Hypixel calls it ASPECT_OF_THE_DRAGON.
			Map.entry(Material.IRON_SWORD, "ASPECT_OF_THE_DRAGON"),
			Map.entry(Material.COPPER_SWORD, "FEL_SWORD"),
			Map.entry(Material.STONE_SWORD, "GOLEM_SWORD"),
			Map.entry(Material.WOODEN_SWORD, "UNDEAD_SWORD"),

			Map.entry(Material.NETHERITE_PICKAXE, "TITANIUM_DRILL_4"),
			Map.entry(Material.DIAMOND_PICKAXE, "TITANIUM_DRILL_3"),
			Map.entry(Material.IRON_PICKAXE, "TITANIUM_DRILL_2"),
			Map.entry(Material.STONE_PICKAXE, "TITANIUM_DRILL_1"),
			Map.entry(Material.GOLDEN_PICKAXE, "STONK_PICKAXE"),
			Map.entry(Material.COPPER_PICKAXE, "PICKONIMBUS"),
			Map.entry(Material.WOODEN_PICKAXE, "ROOKIE_PICKAXE"),

			// No netherite axe: nothing sits above the Helix Chopper.
			Map.entry(Material.DIAMOND_AXE, "HELIX_CHOPPER"),
			Map.entry(Material.IRON_AXE, "FIGSTONE_AXE"),
			Map.entry(Material.STONE_AXE, "FIG_AXE"),
			Map.entry(Material.GOLDEN_AXE, "EFFICIENT_AXE"),
			// The bare TREECAPITATOR is not an id; the Treecapitator is TREECAPITATOR_AXE.
			Map.entry(Material.COPPER_AXE, "TREECAPITATOR_AXE"),
			Map.entry(Material.WOODEN_AXE, "JUNGLE_AXE"),

			// The netherite shovel IS the Aspect of the Void, which is what our own AOTV is built on too.
			Map.entry(Material.NETHERITE_SHOVEL, "ASPECT_OF_THE_VOID"),
			Map.entry(Material.DIAMOND_SHOVEL, "FLINT_SHOVEL"),
			Map.entry(Material.IRON_SHOVEL, "PROMISING_SPADE"),
			Map.entry(Material.COPPER_SHOVEL, "SNOW_SHOVEL"),

			Map.entry(Material.NETHERITE_HOE, "THEORETICAL_HOE_CANE_3"),
			Map.entry(Material.DIAMOND_HOE, "THEORETICAL_HOE_CANE_2"),
			Map.entry(Material.IRON_HOE, "THEORETICAL_HOE_CANE_1"),
			Map.entry(Material.STONE_HOE, "ADVANCED_GARDENING_HOE"),
			Map.entry(Material.GOLDEN_HOE, "ROOKIE_HOE"),
			Map.entry(Material.COPPER_HOE, "THEORETICAL_HOE_POTATO_3"),
			Map.entry(Material.WOODEN_HOE, "BASIC_GARDENING_HOE"),

			Map.entry(Material.BOW, "MOSQUITO_BOW"),

			// Armour, one real SkyBlock set per vanilla set.
			Map.entry(Material.NETHERITE_HELMET, "WITHER_HELMET"),
			Map.entry(Material.NETHERITE_CHESTPLATE, "WITHER_CHESTPLATE"),
			Map.entry(Material.NETHERITE_LEGGINGS, "WITHER_LEGGINGS"),
			Map.entry(Material.NETHERITE_BOOTS, "WITHER_BOOTS"),

			Map.entry(Material.DIAMOND_HELMET, "SUPERIOR_DRAGON_HELMET"),
			Map.entry(Material.DIAMOND_CHESTPLATE, "SUPERIOR_DRAGON_CHESTPLATE"),
			Map.entry(Material.DIAMOND_LEGGINGS, "SUPERIOR_DRAGON_LEGGINGS"),
			Map.entry(Material.DIAMOND_BOOTS, "SUPERIOR_DRAGON_BOOTS"),

			Map.entry(Material.IRON_HELMET, "GLACITE_HELMET"),
			Map.entry(Material.IRON_CHESTPLATE, "GLACITE_CHESTPLATE"),
			Map.entry(Material.IRON_LEGGINGS, "GLACITE_LEGGINGS"),
			Map.entry(Material.IRON_BOOTS, "GLACITE_BOOTS"),

			// Ender Armor, which Hypixel still ids as END_.
			Map.entry(Material.CHAINMAIL_HELMET, "END_HELMET"),
			Map.entry(Material.CHAINMAIL_CHESTPLATE, "END_CHESTPLATE"),
			Map.entry(Material.CHAINMAIL_LEGGINGS, "END_LEGGINGS"),
			Map.entry(Material.CHAINMAIL_BOOTS, "END_BOOTS"),

			Map.entry(Material.GOLDEN_HELMET, "DIVAN_HELMET"),
			Map.entry(Material.GOLDEN_CHESTPLATE, "DIVAN_CHESTPLATE"),
			Map.entry(Material.GOLDEN_LEGGINGS, "DIVAN_LEGGINGS"),
			Map.entry(Material.GOLDEN_BOOTS, "DIVAN_BOOTS"),

			Map.entry(Material.COPPER_HELMET, "STRONG_DRAGON_HELMET"),
			Map.entry(Material.COPPER_CHESTPLATE, "STRONG_DRAGON_CHESTPLATE"),
			Map.entry(Material.COPPER_LEGGINGS, "STRONG_DRAGON_LEGGINGS"),
			Map.entry(Material.COPPER_BOOTS, "STRONG_DRAGON_BOOTS"),

			Map.entry(Material.LEATHER_HELMET, "LAPIS_ARMOR_HELMET"),
			Map.entry(Material.LEATHER_CHESTPLATE, "LAPIS_ARMOR_CHESTPLATE"),
			Map.entry(Material.LEATHER_LEGGINGS, "LAPIS_ARMOR_LEGGINGS"),
			Map.entry(Material.LEATHER_BOOTS, "LAPIS_ARMOR_BOOTS")
	);

	/**
	 * Id a plain stack of {@code material} is dressed up as, or null. Public because the Manhunt Hyperion reads its
	 * rung's id from here: a rung is a sword material and renders as that material does.
	 */
	@Nullable
	public static String vanillaFor(Material material) {
		return VANILLA.get(material);
	}

	/**
	 * Dresses a plain vanilla stack as its SkyBlock counterpart and returns the stamped copy, or <b>null</b> = leave
	 * it: no counterpart, a custom item, or already the right id. The last matters because this runs on every slot
	 * switch, and re-stamping would rewrite and resend the slot for nothing.
	 *
	 * <p><b>Refuses custom items outright</b>, same test as {@code ItemReloader.modifyVanillaArmor}: a
	 * {@code skyblock/} first lore line.
	 */
	@Nullable
	public static ItemStack stampVanilla(ItemStack item) {
		if(item == null || item.getType().isAir()) return null;
		if(item.hasItemMeta() && item.getItemMeta().hasLore()
				&& Utils.firstLorePlain(item.getItemMeta()).startsWith("skyblock/")) {
			return null;
		}
		String id = VANILLA.get(item.getType());
		if(id == null || id.equals(NBT.getId(item))) return null;
		return stamp(item, id);
	}
}
