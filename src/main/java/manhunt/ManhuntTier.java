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
 * Manhunt Hyperion upgrade ladder, Stick to Netherite; Netherite crafts into a real
 * {@code skyblock/combat/scylla}. Every number the item quotes lives here so lore can't drift.
 * A rung IS its material; all share one lore id, which is also why the SkyBlock id comes from
 * {@link #skyblockId()} and not a row in {@link misc.SkyblockId}'s {@code IDS} (all eight would collide).
 */
public enum ManhuntTier {
	//                                                                swords  dmg   cost  abs  reduce  shieldCd  implode  radius  tick  intel  ench
	// No teleport column: it's a flat 10 blocks on every rung.
	BASE(Material.STICK, "", Rank.COMMON,                            0, 0,   25, 1, 0.00, 200, 1,    7.5, 200, 250,  0),
	WOOD(Material.WOODEN_SWORD, "Wooden", Rank.COMMON,               8, 1.5, 24, 3, 0.05, 200, 1.25, 8,   180, 325,  10),
	STONE(Material.STONE_SWORD, "Stone", Rank.UNCOMMON,              8, 2.5, 23, 4, 0.06, 200, 1.75, 8.5, 160, 400,  12),
	COPPER(Material.COPPER_SWORD, "Copper", Rank.UNCOMMON,           6, 3,   22, 4, 0.06, 180, 2,    8.5, 150, 450,  14),
	IRON(Material.IRON_SWORD, "Iron", Rank.RARE,                     4, 4,   20, 5, 0.07, 180, 2.5,  9,   130, 575,  16),
	GOLD(Material.GOLDEN_SWORD, "Golden", Rank.RARE,                 4, 4.5, 19, 5, 0.07, 160, 2.75, 9,   120, 625,  18),
	DIAMOND(Material.DIAMOND_SWORD, "Diamond", Rank.EPIC,            2, 5.5, 17, 6, 0.08, 160, 3.25, 9.5, 100, 750,  20),
	NETHERITE(Material.NETHERITE_SWORD, "Netherite", Rank.LEGENDARY, 1, 6.5, 15, 8, 0.10, 150, 3.75, 10,  80,  1000, 25);

	/** Item rarity. */
	public enum Rank {
		COMMON("white"), UNCOMMON("green"), RARE("blue"), EPIC("dark_purple"), LEGENDARY("gold");

		private final String colour;

		Rank(String colour) {
			this.colour = colour;
		}

		public String colour() {
			return colour;
		}

		/** Bottom rarity line, same style as every other custom item. */
		public String lore() {
			return "<" + colour + "><bold><obfuscated>a</obfuscated> " + name() + " SWORD <obfuscated>a</obfuscated>";
		}
	}

	/** Full Hyperion's regen and cap. */
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
	private final int witherShieldCooldown;
	private final double implosionDamage;
	private final double radius;
	private final int ticksPerMana;
	private final int maxIntelligence;
	private final int enchantability;

	ManhuntTier(Material material, String prefix, Rank rank, int swordsToUpgrade, double damage, int manaCost,
				double absorption, double damageReduction, int witherShieldCooldown, double implosionDamage,
				double radius, int ticksPerMana, int maxIntelligence, int enchantability) {
		this.material = material;
		this.prefix = prefix;
		this.rank = rank;
		this.swordsToUpgrade = swordsToUpgrade;
		this.damage = damage;
		this.manaCost = manaCost;
		this.absorption = absorption;
		this.damageReduction = damageReduction;
		this.witherShieldCooldown = witherShieldCooldown;
		this.implosionDamage = implosionDamage;
		this.radius = radius;
		this.ticksPerMana = ticksPerMana;
		this.maxIntelligence = maxIntelligence;
		this.enchantability = enchantability;
	}

	/** Null if not a Manhunt Hyperion. */
	@Nullable
	public static ManhuntTier of(@Nullable ItemStack item) {
		// Material first, before lore: the intelligence loop walks every inventory every tick.
		if(item == null) return null;
		ManhuntTier tier = BY_MATERIAL.get(item.getType());
		if(tier == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) return null;
		return ID.equals(Utils.firstLorePlain(item.getItemMeta())) ? tier : null;
	}

	/** Null for Netherite, whose upgrade is the normal Hyperion recipe. */
	@Nullable
	public ManhuntTier next() {
		ManhuntTier[] all = values();
		return ordinal() + 1 < all.length ? all[ordinal() + 1] : null;
	}

	public Material material() {
		return material;
	}

	/** Swords of this material to upgrade the rung below into this one. 0 for the Stick. */
	public int swordsToUpgrade() {
		return swordsToUpgrade;
	}

	public String displayName() {
		return prefix.isEmpty() ? "Manhunt Hyperion" : prefix + " Manhunt Hyperion";
	}

	/** Vanilla's adjective ({@code Wooden}). Empty for the Stick. */
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

	/** Wither Shield absorption in HP (1 HP = half a heart). */
	public double absorption() {
		return absorption;
	}

	/** Share taken off incoming damage while the Wither Shield is up. */
	public double damageReduction() {
		return damageReduction;
	}

	/**
	 * Ticks before the shield can go up again, not its length (always {@link items.weapons.Scylla#SHIELD_DURATION}).
	 * A standing shield blocks a refresh too, so SHIELD_DURATION is the floor. Full Hyperion has none.
	 */
	public int witherShieldCooldown() {
		return witherShieldCooldown;
	}

	/** Flat. Only the full Hyperion scales off melee damage. */
	public double implosionDamage() {
		return implosionDamage;
	}

	/** Implosion radius. Teleport stays a flat 10 on every rung: players build muscle memory around it. */
	public double radius() {
		return radius;
	}

	/** Passive regen ticks per point. */
	public int ticksPerMana() {
		return ticksPerMana;
	}

	public int maxIntelligence() {
		return maxIntelligence;
	}

	/**
	 * Overrides the material's own, which isn't a ladder (gold 22, netherite 15, stone 5). 0 on the Stick
	 * means no {@code minecraft:enchantable}: books and anvils only. See {@link misc.Utils#setEnchantability}.
	 */
	public int enchantability() {
		return enchantability;
	}

	/**
	 * Hypixel id for the texture pack: same as a plain sword of this material, read from
	 * {@link misc.SkyblockId#vanillaFor} so the two can't drift. Null on the Stick.
	 */
	@Nullable
	public String skyblockId() {
		return SkyblockId.vanillaFor(material);
	}
}
