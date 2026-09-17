package manhunt;

import misc.Utils;
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
 */
public enum ManhuntTier {
	//                                             swords  dmg cost abs  heal  reduce  implode radius  tick  intel  ench
	BASE(Material.STICK, "", Rank.COMMON,               0,   0,  20,  1,  0.33,  0.05,      1,     5,   160,   250,    0),
	WOOD(Material.WOODEN_SWORD, "Wooden", Rank.COMMON,  8,   1,  20,  2,  0.35,  0.06,      2,     6,   150,   400,   10),
	STONE(Material.STONE_SWORD, "Stone", Rank.COMMON,   8,   2,  20,  3,  0.37,  0.07,    2.5,   6.5,   140,   500,   12),
	COPPER(Material.COPPER_SWORD, "Copper", Rank.UNCOMMON, 8, 3, 20,  4,  0.39,  0.08,      3,     7,   130,   600,   14),
	IRON(Material.IRON_SWORD, "Iron", Rank.RARE,        8,   4,  19,  5,  0.41,  0.09,    3.5,   7.5,   120,   700,   16),
	GOLD(Material.GOLDEN_SWORD, "Golden", Rank.RARE,    8,   5,  18,  6,  0.43,  0.10,      4,     8,   110,   800,   18),
	DIAMOND(Material.DIAMOND_SWORD, "Diamond", Rank.EPIC, 4, 6,  17,  7,  0.45,  0.11,    4.5,   8.5,   100,   900,   20),
	NETHERITE(Material.NETHERITE_SWORD, "Netherite", Rank.LEGENDARY, 2, 7, 16, 8, 0.47, 0.13, 5,   9,    90,  1000,   25);

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

		public String lore() {
			return "<" + colour + "><bold>" + name() + " SWORD";
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
	private final double healShare;
	private final double damageReduction;
	private final double implosionDamage;
	private final double radius;
	private final int ticksPerMana;
	private final int maxIntelligence;
	private final int enchantability;

	ManhuntTier(Material material, String prefix, Rank rank, int swordsToUpgrade, double damage, int manaCost,
				double absorption, double healShare, double damageReduction, double implosionDamage, double radius,
				int ticksPerMana, int maxIntelligence, int enchantability) {
		this.material = material;
		this.prefix = prefix;
		this.rank = rank;
		this.swordsToUpgrade = swordsToUpgrade;
		this.damage = damage;
		this.manaCost = manaCost;
		this.absorption = absorption;
		this.healShare = healShare;
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

	/** Share of the absorption still standing when the shield expires that becomes real health. */
	public double healShare() {
		return healShare;
	}

	/** Share taken off incoming damage while the Wither Shield is up. */
	public double damageReduction() {
		return damageReduction;
	}

	/** Flat implosion damage. The full Hyperion is the one rung that scales off melee damage instead. */
	public double implosionDamage() {
		return implosionDamage;
	}

	/** Implosion radius, and the Shadow Warp teleport distance - always the same number. */
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
}
