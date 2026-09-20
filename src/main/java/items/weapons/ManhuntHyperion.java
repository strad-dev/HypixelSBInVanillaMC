package items.weapons;

import items.AbilityItem;
import manhunt.Manhunt;
import manhunt.ManhuntTier;
import misc.MinecraftFont;
import misc.Plugin;
import misc.SkyblockId;
import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Manhunt Hyperion - the whole Hyperion, zero hit cooldown included, on the numbers of whichever
 * {@link ManhuntTier} rung it sits on. Everyone in a Manhunt starts with the Stick and upgrades it with
 * swords; the Netherite rung crafts into a real Hyperion through the ordinary recipe.
 *
 * <p>Wither Impact itself is {@link Scylla#witherImpact}, so the two items cannot drift apart. The one real
 * difference is the implosion: a flat number here, a share of melee damage there.
 */
public class ManhuntHyperion implements AbilityItem {
	public static ItemStack getItem() {
		return getItem(ManhuntTier.BASE);
	}

	public static ItemStack getItem(ManhuntTier tier) {
		return getItem(tier, Map.of(), null);
	}

	/**
	 * The item on {@code tier}'s rung, carrying {@code enchants}. {@code p} is who the lore is written for
	 * and may be null - as on the full Hyperion, only the live figures care, and {@code ItemReloader} keeps
	 * them in step on join, pickup and every slot switch.
	 */
	public static ItemStack getItem(ManhuntTier tier, Map<Enchantment, Integer> enchants, @Nullable Player p) {
		ItemStack item = new ItemStack(tier.material());

		ItemMeta data = item.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<" + tier.rank().colour() + ">" + tier.displayName()));
		// Naming ATTACK_DAMAGE and ATTACK_SPEED at all replaces the sword's own defaults, which is the point:
		// a player's damage is their base +1 plus this rung's number, never the vanilla sword's as well.
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(
				new NamespacedKey(Plugin.getInstance(), "manhuntHyperionDmg"), tier.damage(), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
		data.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(
				new NamespacedKey(Plugin.getInstance(), "manhuntHyperionSpeed"), 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(data);
		item.addUnsafeEnchantments(enchants);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm(ManhuntTier.ID));
		lore.add(Utils.mm(""));
		lore.addAll(Utils.statLore(data, enchants));
		lore.addAll(Utils.bonusDamageLore(enchants));
		lore.add(Utils.mm(""));
		// The header is the line the tooltip is measured against, so it is never wrapped.
		lore.add(Utils.mm("<gold>Ability: Wither Impact <green><bold>RIGHT CLICK"));
		lore.addAll(MinecraftFont.wrapLore(
				"<gray>Teleport <green>10 blocks<gray> ahead of you.  Then implode, dealing <red>"
				+ Utils.tenthNumber(tier.implosionDamage()) + " damage <gray>to enemies within <green>"
				+ Utils.damageNumber(tier.radius()) + " blocks<gray>.  Also reduces damage taken by <red>"
				+ Utils.percent(tier.damageReduction()) + "<gray> and grants an Absorption Shield with <red>"
				+ Utils.damageNumber(tier.absorption()) + " HP <gray>for <yellow>5 seconds<gray>."));
		lore.add(Utils.mm("<dark_gray>Intelligence Cost: <dark_aqua>" + tier.manaCost()));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_gray>Max Intelligence: <dark_aqua>" + tier.maxIntelligence()));
		lore.add(Utils.mm("<dark_gray>Intelligence Regen: <dark_aqua>1 per " + tier.ticksPerMana() + " ticks"));
		lore.add(Utils.mm(""));
		lore.addAll(upgradeLines(tier));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm(tier.rank().lore()));

		data = item.getItemMeta();
		data.lore(lore);
		item.setItemMeta(data);
		// Last, after the final setItemMeta - and it OVERRIDES the sword material's own value, which is no
		// ladder at all (vanilla gold 22 beats netherite 15, stone is 5). The Stick's 0 leaves the component
		// off entirely, so the base item cannot be taken to an enchanting table.
		Utils.setEnchantability(item, tier.enchantability());
		// The rung's own id, not a lookup off the lore id: all eight rungs share one lore id, so the table in
		// SkyblockId cannot tell them apart.
		return SkyblockId.stamp(item, tier.skyblockId());
	}

	/**
	 * How to get off this rung and <b>what it buys</b>: the swords to hand over, then one line per stat that
	 * actually moves.  Every figure is the difference between two {@link ManhuntTier} rows, so it cannot
	 * drift from what the upgrade pays out - and a stat that does not change is left out rather than shown
	 * as {@code +0}, which is why the block is short on the small steps and long on the big ones.
	 */
	private static List<Component> upgradeLines(ManhuntTier tier) {
		ManhuntTier next = tier.next();
		if(next == null) {
			return MinecraftFont.wrapLore("<gray>Craft into a <light_purple>Hyperion<gray>.");
		}

		int swords = next.swordsToUpgrade();
		List<Component> out = new ArrayList<>(MinecraftFont.wrapLore("<gray>Upgrade with <green>" + swords + " "
				+ next.prefix() + (swords == 1 ? " Sword" : " Swords") + "<gray>:"));

		// Direction is chosen by the argument ORDER, not by a flag: a stat that reads better when it falls -
		// the damage you take, the regen ticks, the ability cost - is passed as next-minus-this so it shows a
		// minus, which is what a player expects to see for an improvement.
		delta(out, "Damage", "red", next.damage() - tier.damage(), "");
		delta(out, "Implosion Damage", "red", next.implosionDamage() - tier.implosionDamage(), "");
		delta(out, "Implosion Radius", "green", next.radius() - tier.radius(), " blocks");
		delta(out, "Absorption HP", "red", next.absorption() - tier.absorption(), "");
		delta(out, "Damage Reduction", "red", (tier.damageReduction() - next.damageReduction()) * 100, "%");
		delta(out, "Max Intelligence", "dark_aqua", next.maxIntelligence() - tier.maxIntelligence(), "");
		delta(out, "Intel Regen", "dark_aqua", next.ticksPerMana() - tier.ticksPerMana(), "<dark_gray> ticks/intel");
		delta(out, "Ability Cost", "dark_aqua", next.manaCost() - tier.manaCost(), "");
		return out;
	}

	/**
	 * One {@code <label> <colour>±N<unit>} line, omitted when the stat does not move between the two rungs.
	 * The sign is the raw difference, so the caller decides which way round reads as an improvement.
	 */
	private static void delta(List<Component> out, String label, String colour, double delta, String unit) {
		if(delta == 0) return;
		out.add(Utils.mm("<dark_gray>" + label + " <" + colour + ">" + (delta > 0 ? "+" : "-")
				+ Utils.damageNumber(Math.abs(delta)) + unit));
	}

	@Override
	public boolean hasLeftClickAbility() {
		return false;
	}

	@Override
	public boolean onRightClick(Player p) {
		ManhuntTier tier = ManhuntTier.of(p.getInventory().getItemInMainHand());
		if(tier == null) return false;
		// The teleport is 10 blocks on every rung - the same reach as the full Hyperion, so a player's aim
		// does not have to be relearned each upgrade.  Only the implosion grows.
		//
		// A player can only be imploded once a second, across ALL attackers - claimImplosion returns false
		// while their window is up and 0 damage is witherImpact's "skip this one", so they are left out of
		// the blast entirely rather than hit for nothing.  Mobs are not on a clock.
		return Scylla.witherImpact(p, 10, tier.radius(), tier.absorption(), tier.damageReduction(),
				entity -> Manhunt.claimImplosion(entity) ? tier.implosionDamage() : 0);
	}

	@Override
	public boolean onLeftClick(Player p) {
		return false;
	}

	/** Never asked for - the cost is the rung's, so every call site goes through {@link #manaCost(ItemStack)}. */
	@Override
	public int manaCost() {
		return ManhuntTier.BASE.manaCost();
	}

	@Override
	public int manaCost(ItemStack item) {
		ManhuntTier tier = ManhuntTier.of(item);
		return tier == null ? ManhuntTier.BASE.manaCost() : tier.manaCost();
	}

	@Override
	public String cooldownTag() {
		return "";
	}

	@Override
	public int cooldown() {
		return 0;
	}
}
