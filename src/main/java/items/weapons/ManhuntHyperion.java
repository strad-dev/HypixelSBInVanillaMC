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
 * Hyperion on its {@link ManhuntTier} rung's numbers. Shares {@link Scylla#witherImpact}. Differences: flat
 * implosion damage, a per-rung ability cooldown, and a hit cooldown ({@link #ATTACK_SPEED}); the real one has none.
 */
public class ManhuntHyperion implements AbilityItem {
	/** Full charge in 10 ticks, a hurt player's i-frames: every hit as they expire lands full, spam clicks don't. */
	public static final double ATTACK_SPEED = 2;

	public static ItemStack getItem() {
		return getItem(ManhuntTier.BASE);
	}

	public static ItemStack getItem(ManhuntTier tier) {
		return getItem(tier, Map.of(), null);
	}

	/** {@code p} is who the lore is written for, may be null; {@code ItemReloader} keeps live figures current. */
	public static ItemStack getItem(ManhuntTier tier, Map<Enchantment, Integer> enchants, @Nullable Player p) {
		ItemStack item = new ItemStack(tier.material());

		ItemMeta data = item.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<" + tier.rank().colour() + ">" + tier.displayName()));
		// Any modifier replaces the sword's defaults: damage is base 1 + this rung, not the vanilla sword too.
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(
				new NamespacedKey(Plugin.getInstance(), "manhuntHyperionDmg"), tier.damage(), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
		// Adds to the player's base 4, like a vanilla sword's -2.4: every rung lands on exactly ATTACK_SPEED.
		data.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(
				new NamespacedKey(Plugin.getInstance(), "manhuntHyperionSpeed"), ATTACK_SPEED - 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(data);
		item.addUnsafeEnchantments(enchants);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm(ManhuntTier.ID));
		lore.add(Utils.mm(""));
		List<Component> stats = Utils.statLore(data, enchants);
		// Right after Damage (statLore's first line); the total, not the -2 modifier.
		stats.add(Math.min(1, stats.size()), Utils.mm("<gray>Attack Speed: <red>" + Utils.damageNumber(ATTACK_SPEED)));
		lore.addAll(stats);
		lore.addAll(Utils.bonusDamageLore(enchants));
		lore.add(Utils.mm(""));
		// Header sets the tooltip width, never wrapped.
		lore.add(Utils.mm("<gold>Ability: Wither Impact <green><bold>RIGHT CLICK"));
		lore.addAll(MinecraftFont.wrapLore(
				(Manhunt.shadowWarp() ? "<gray>Teleport <green>10 blocks<gray> ahead of you.  Then implode" : "<gray>Implode")
				+ ", dealing <red>"
				+ Utils.tenthNumber(tier.implosionDamage()) + " damage <gray>to enemies within <green>"
				+ Utils.damageNumber(tier.radius()) + " blocks<gray>.  Also reduces damage taken by <red>"
				+ Utils.percent(tier.damageReduction()) + "<gray> and grants an Absorption Shield with <red>"
				+ Utils.damageNumber(tier.absorption()) + " HP <gray>for <green>5<gray> seconds."));
		lore.add(Utils.mm("<dark_gray>Intelligence Cost: <dark_aqua>" + tier.manaCost()));
		lore.add(Utils.mm("<dark_gray>Cooldown: <green>" + Utils.damageNumber(tier.cooldown() / 20.0) + "s"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_gray>Max Intelligence: <dark_aqua>" + tier.maxIntelligence()));
		lore.add(Utils.mm("<dark_gray>Intelligence Regen: <dark_aqua>1 per "
				+ Utils.damageNumber(tier.ticksPerMana() / 20.0) + "s"));
		lore.add(Utils.mm(""));
		lore.addAll(upgradeLines(tier));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm(tier.rank().lore()));

		data = item.getItemMeta();
		data.lore(lore);
		item.setItemMeta(data);
		// Last, after the final setItemMeta. See ManhuntTier.enchantability.
		Utils.setEnchantability(item, tier.enchantability());
		// Rung's own id: all eight share one lore id, so SkyblockId's table can't tell them apart.
		return SkyblockId.stamp(item, tier.skyblockId());
	}

	/** Swords needed, then one line per stat that changes, computed from two {@link ManhuntTier} rows. */
	private static List<Component> upgradeLines(ManhuntTier tier) {
		ManhuntTier next = tier.next();
		if(next == null) {
			return MinecraftFont.wrapLore("<gray>Craft into a <light_purple>Hyperion<gray>.");
		}

		int swords = next.swordsToUpgrade();
		List<Component> out = new ArrayList<>(MinecraftFont.wrapLore("<gray>Upgrade with <green>" + swords + " "
				+ next.prefix() + (swords == 1 ? " Sword" : " Swords") + "<gray>:"));

		// Sign comes from argument order: stats that improve by falling (regen ticks, cost) show a minus.
		delta(out, "Damage", "red", next.damage() - tier.damage(), "");
		delta(out, "Implosion Damage", "red", next.implosionDamage() - tier.implosionDamage(), "");
		delta(out, "Implosion Radius", "green", next.radius() - tier.radius(), " blocks");
		delta(out, "Absorption HP", "red", next.absorption() - tier.absorption(), "");
		delta(out, "Damage Reduction", "red", (tier.damageReduction() - next.damageReduction()) * 100, "%");
		delta(out, "Cooldown", "green", (next.cooldown() - tier.cooldown()) / 20.0, "s");
		delta(out, "Max Intelligence", "dark_aqua", next.maxIntelligence() - tier.maxIntelligence(), "");
		delta(out, "Intel Regen", "dark_aqua", (next.ticksPerMana() - tier.ticksPerMana()) / 20.0, "s<dark_gray>/intel");
		delta(out, "Ability Cost", "dark_aqua", next.manaCost() - tier.manaCost(), "");
		return out;
	}

	/** Omitted when 0. Sign is the raw difference; caller picks the order. */
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
		// Teleport is 10 on every rung, same as the full Hyperion, or none with shadow-warp off. A player can be
		// imploded once a second across all attackers; 0 damage is witherImpact's skip signal, so they're left
		// out, not hit for 0.
		return Scylla.witherImpact(p, Manhunt.shadowWarp() ? 10 : 0, tier.radius(), tier.absorption(), tier.damageReduction(),
				entity -> Manhunt.claimImplosion(entity) ? tier.implosionDamage() : 0);
	}

	@Override
	public boolean onLeftClick(Player p) {
		return false;
	}

	/** Unused: every call site uses {@link #manaCost(ItemStack)}. */
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
		return "ManhuntHyperionCooldown";
	}

	/** Unused: the dispatcher uses {@link #cooldown(ItemStack)}. */
	@Override
	public int cooldown() {
		return ManhuntTier.BASE.cooldown();
	}

	@Override
	public int cooldown(ItemStack item) {
		ManhuntTier tier = ManhuntTier.of(item);
		return tier == null ? ManhuntTier.BASE.cooldown() : tier.cooldown();
	}
}
