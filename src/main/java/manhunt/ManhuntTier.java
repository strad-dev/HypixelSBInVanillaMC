package manhunt;

import misc.SkyblockId;
import misc.Utils;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * The Manhunt Hyperion's upgrade ladder. One rung per sword material, from the Stick everybody starts a
 * Manhunt with up to Netherite, which crafts into a real {@code skyblock/combat/scylla} through the ordinary
 * Hyperion recipe. Every number the item quotes or pays out is here, so the lore cannot drift from the
 * ability.
 *
 * <p><b>A rung is identified by the stack's MATERIAL</b>, not by anything written into its NBT - every tier
 * shares the one item ID {@code skyblock/manhunt/hyperion}. Netherite is shared with the full Hyperion, which
 * is why {@link #of} checks the item ID first.
 *
 * <p>That shared item ID is also why the SkyBlock id is resolved here rather than by a row in
 * {@link misc.SkyblockId}'s {@code IDS} table: every other custom item's id is looked up from its lore id, and
 * these eight would all collide on one. <b>The two are separate identities</b> - the lore id is ours and picks
 * the behaviour, the SkyBlock id is Hypixel's and picks the client-side texture. A rung reads as whatever a
 * plain sword of the same material reads as, so {@link #skyblockId()} just asks
 * {@link misc.SkyblockId#vanillaFor}.
 */
public enum ManhuntTier {
	//                                                                swords  dmg   cost  abs  reduce  implode  radius  tick  intel  ench
	// The teleport is NOT in here: it is a flat 10 blocks on every rung, so upgrading never moves it.
	BASE(Material.STICK, "", Rank.COMMON,                            0, 0,   25, 1,  0.00, 0.5,  7.5, 200, 250,  0),
	WOOD(Material.WOODEN_SWORD, "Wooden", Rank.COMMON,               8, 1.5, 24, 3,  0.05, 0.75, 8,   180, 325,  10),
	STONE(Material.STONE_SWORD, "Stone", Rank.UNCOMMON,              8, 2.5, 23, 4,  0.06, 1.25, 8.5, 160, 400,  12),
	COPPER(Material.COPPER_SWORD, "Copper", Rank.UNCOMMON,           4, 3,   22, 4,  0.06, 1.5,  8.5, 150, 450,  14),
	IRON(Material.IRON_SWORD, "Iron", Rank.RARE,                     4, 4,   20, 5,  0.08, 2,    9,   130, 575,  16),
	GOLD(Material.GOLDEN_SWORD, "Golden", Rank.RARE,                 4, 4.5, 19, 5,  0.08, 2.25, 9,   120, 625,  18),
	DIAMOND(Material.DIAMOND_SWORD, "Diamond", Rank.EPIC,            2, 5.5, 17, 6,  0.10, 2.75, 9.5, 100, 750,  20),
	NETHERITE(Material.NETHERITE_SWORD, "Netherite", Rank.LEGENDARY, 1, 7,   15, 10, 0.15, 3.5,  10,  80,  1000, 25);

	/** Item rarity, in the colours the rest of the plugin's lore uses. */
	public enum Rank {
		COMMON("white"), UNCOMMON("green"), RARE("blue"), EPIC("dark_purple"), LEGENDARY("gold");

		private final String colour;

		Rank(String colour) {
			this.colour = colour;
		}

		public String colour() {
			return colour;
		}

		/**
		 * The bottom rarity line, in the house style every other custom item uses: bold, the rank's colour,
		 * and a single obfuscated glyph shimmering at each end.
		 */
		public String lore() {
			return "<" + colour + "><bold><obfuscated>a</obfuscated> " + name() + " SWORD <obfuscated>a</obfuscated>";
		}
	}

	/** The full Hyperion's rung, for the rules that have to cover it too: mana regen and the intelligence cap. */
	public static final int FULL_TICKS_PER_MANA = 80;
	public static final int FULL_MAX_INTELLIGENCE = 2500;

	public static final String ID = "skyblock/manhunt/hyperion";

	private static final Map<Material, ManhuntTier> BY_MATERIAL = new HashMap<>();

	static {
		for(ManhuntTier tier : values()) {
			BY_MATERIAL.put(tier.material, tier);
		}
	}

	private final Material material;
	private final String prefix;
	private final Rank rank;
	private final int swordsToUpgrade;
	private final double damage;
	private final int manaCost;
	private final double absorption;
	private final double damageReduction;
	private final double implosionDamage;
	private final double radius;
	private final int ticksPerMana;
	private final int maxIntelligence;
	private final int enchantability;

	ManhuntTier(Material material, String prefix, Rank rank, int swordsToUpgrade, double damage, int manaCost,
				double absorption, double damageReduction, double implosionDamage, double radius,
				int ticksPerMana, int maxIntelligence, int enchantability) {
		this.material = material;
		this.prefix = prefix;
		this.rank = rank;
		this.swordsToUpgrade = swordsToUpgrade;
		this.damage = damage;
		this.manaCost = manaCost;
		this.absorption = absorption;
		this.damageReduction = damageReduction;
		this.implosionDamage = implosionDamage;
		this.radius = radius;
		this.ticksPerMana = ticksPerMana;
		this.maxIntelligence = maxIntelligence;
		this.enchantability = enchantability;
	}

	/** The rung {@code item} sits on, or null if it is not a Manhunt Hyperion at all. */
	@Nullable
	public static ManhuntTier of(@Nullable ItemStack item) {
		// Material first: it rules the stack out without reading any lore, which matters because the
		// intelligence loop walks every inventory every tick.
		if(item == null) return null;
		ManhuntTier tier = BY_MATERIAL.get(item.getType());
		if(tier == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) return null;
		return ID.equals(Utils.firstLorePlain(item.getItemMeta())) ? tier : null;
	}

	/** The next rung up, or null for Netherite - whose upgrade is the ordinary Hyperion recipe. */
	@Nullable
	public ManhuntTier next() {
		ManhuntTier[] all = values();
		return ordinal() + 1 < all.length ? all[ordinal() + 1] : null;
	}

	public Material material() {
		return material;
	}

	/** How many swords of this rung's material upgrade the rung below into this one. 0 for the Stick. */
	public int swordsToUpgrade() {
		return swordsToUpgrade;
	}

	public String displayName() {
		return prefix.isEmpty() ? "Manhunt Hyperion" : prefix + " Manhunt Hyperion";
	}

	/** The material adjective, as vanilla names the sword: {@code Wooden}, {@code Golden}. Empty for the Stick. */
	public String prefix() {
		return prefix;
	}

	public Rank rank() {
		return rank;
	}

	public double damage() {
		return damage;
	}

	public int manaCost() {
		return manaCost;
	}

	/** Absorption HP the Wither Shield grants, in half-hearts of display (1 HP = half a heart on the bar). */
	public double absorption() {
		return absorption;
	}

	/** Share taken off incoming damage while the Wither Shield is up. */
	public double damageReduction() {
		return damageReduction;
	}

	/** Flat implosion damage. The full Hyperion is the one rung that scales off melee damage instead. */
	public double implosionDamage() {
		return implosionDamage;
	}

	/**
	 * Implosion radius. <b>The teleport is a flat 10 blocks on every rung</b>, deliberately - it is the one
	 * number a player builds muscle memory around, so upgrading must not move it.
	 */
	public double radius() {
		return radius;
	}

	/** Ticks of passive regen per point of intelligence. */
	public int ticksPerMana() {
		return ticksPerMana;
	}

	public int maxIntelligence() {
		return maxIntelligence;
	}

	/**
	 * Enchanting-table power, <b>overriding the sword material's own</b> - which is not a ladder at all
	 * (gold 22 beats netherite 15, stone is 5). 0 on the Stick, which leaves it with no
	 * {@code minecraft:enchantable} component and therefore unenchantable at a table, books and anvils only.
	 *
	 * @see misc.Utils#setEnchantability
	 */
	public int enchantability() {
		return enchantability;
	}

	/**
	 * The real Hypixel item id this rung is stamped with, which is what a client-side pack reads to pick the
	 * item's texture. <b>A rung renders as whatever a plain sword of its material renders as</b> - Undead
	 * Sword through to Necron's Blade - so it is read straight out of {@link misc.SkyblockId#vanillaFor}
	 * rather than copied into a column here, where the two could drift apart.
	 *
	 * <p>Null on the Stick, which is not a sword material and has no counterpart: the bottom rung stays a
	 * plain stick client-side, the same as any other stick.
	 */
	@Nullable
	public String skyblockId() {
		return SkyblockId.vanillaFor(material);
	}
}
