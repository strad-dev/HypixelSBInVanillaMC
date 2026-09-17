package items.weapons;

import items.AbilityItem;
import manhunt.ManhuntTier;
import misc.Plugin;
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
 * {@link ManhuntTier} rung it sits on. Everyone in a Manhunt starts with the Stick and upgrades it by
 * surrounding it with swords; the Netherite rung crafts into a real Hyperion through the ordinary recipe.
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

		String blocks = Utils.damageNumber(tier.radius()) + " blocks";

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm(ManhuntTier.ID));
		lore.add(Utils.mm(""));
		lore.add(Utils.damageLore(tier.damage(), enchants));
		lore.addAll(Utils.bonusDamageLore(enchants));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold>Ability: Wither Impact <green><bold>RIGHT CLICK"));
		lore.add(Utils.mm("<gray>Teleport <green>" + blocks + "<gray> ahead of"));
		lore.add(Utils.mm("<gray>you.  Then implode, dealing"));
		lore.add(Utils.mm("<red>" + Utils.tenthNumber(tier.implosionDamage()) + " damage <gray>to enemies"));
		lore.add(Utils.mm("<gray>within <green>" + blocks + "<gray>.  Also"));
		lore.add(Utils.mm("<gray>reduces damage taken by <red>" + percent(tier.damageReduction()) + " <gray>and"));
		lore.add(Utils.mm("<gray>grants an Absorption Shield with"));
		lore.add(Utils.mm("<gray><red>" + Utils.damageNumber(tier.absorption()) + " HP <gray>for <yellow>5 seconds<gray>,"));
		lore.add(Utils.mm("<gray>healing <red>" + percent(tier.healShare()) + "<gray> of what is left."));
		lore.add(Utils.mm("<dark_gray>Intelligence Cost: <dark_aqua>" + tier.manaCost()));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_gray>Max Intelligence: <dark_aqua>" + tier.maxIntelligence()));
		lore.add(Utils.mm("<dark_gray>Intelligence Regen: <dark_aqua>1 per " + tier.ticksPerMana() + " ticks"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm(upgradeLore(tier)));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm(tier.rank().lore()));

		data = item.getItemMeta();
		data.lore(lore);
		item.setItemMeta(data);
		// Last, after the final setItemMeta - and it OVERRIDES the sword material's own value, which is no
		// ladder at all (vanilla gold 22 beats netherite 15, stone is 5). The Stick's 0 leaves the component
		// off entirely, so the base item cannot be taken to an enchanting table.
		Utils.setEnchantability(item, tier.enchantability());

		return item;
	}

	/** How to get off this rung: N of the next material's swords, or the Hyperion recipe at the top. */
	private static String upgradeLore(ManhuntTier tier) {
		ManhuntTier next = tier.next();
		if(next == null) {
			return "<gray>Craft into a <light_purple>Hyperion<gray>.";
		}
		return "<gray>Craft with <green>" + next.swordsToUpgrade() + " " + next.prefix() + " Swords<gray>.";
	}

	private static String percent(double share) {
		return Utils.damageNumber(Math.round(share * 1000) / 10.0) + "%";
	}

	@Override
	public boolean hasLeftClickAbility() {
		return false;
	}

	@Override
	public boolean onRightClick(Player p) {
		ManhuntTier tier = ManhuntTier.of(p.getInventory().getItemInMainHand());
		if(tier == null) return false;
		return Scylla.witherImpact(p, tier.radius(), tier.radius(), tier.absorption(), tier.healShare(),
				tier.damageReduction(), entity -> tier.implosionDamage());
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
