package items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemLore;
import misc.Rarity;
import misc.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Plain vanilla armour, weapons and tools in the custom items' lore style: rarity-coloured name, stat lines, rarity. */
public final class VanillaGear {
	private VanillaGear() {}

	/** Gear with no attribute modifiers to qualify it. */
	private static final Set<Material> NO_STATS = EnumSet.of(Material.BOW, Material.CROSSBOW, Material.SHIELD,
			Material.FISHING_ROD, Material.SHEARS, Material.FLINT_AND_STEEL, Material.BRUSH);

	/** Checked before the tiers. */
	private static final Map<Material, Rarity> SPECIAL = Map.of(
			Material.ELYTRA, Rarity.LEGENDARY,
			Material.TRIDENT, Rarity.LEGENDARY,
			Material.MACE, Rarity.MYTHIC,
			Material.TURTLE_HELMET, Rarity.EPIC);

	/** Tier by material prefix; anything else is COMMON. */
	private static final Map<String, Rarity> TIERS = Map.of(
			"NETHERITE_", Rarity.LEGENDARY,
			"DIAMOND_", Rarity.EPIC,
			"IRON_", Rarity.RARE,
			"GOLDEN_", Rarity.RARE,
			"STONE_", Rarity.UNCOMMON,
			"COPPER_", Rarity.UNCOMMON,
			"CHAINMAIL_", Rarity.UNCOMMON,
			"WOODEN_", Rarity.COMMON,
			"LEATHER_", Rarity.COMMON);

	/**
	 * Writes name colour, lore and the hidden vanilla attribute tooltip onto {@code item} in place. No-op for custom
	 * items and non-gear. Idempotent: a dressed stack comes back equal.
	 */
	public static void dress(@Nullable ItemStack item) {
		if(item == null || item.getType().isAir()) return;
		if(Utils.firstLorePlain(item).startsWith("skyblock/")) return;

		ItemAttributeModifiers attributes = item.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
		Map<Attribute, List<AttributeModifier>> byAttribute = new HashMap<>();
		if(attributes != null) {
			for(ItemAttributeModifiers.Entry entry : attributes.modifiers()) {
				byAttribute.computeIfAbsent(entry.attribute(), a -> new ArrayList<>()).add(entry.modifier());
			}
		}
		if(byAttribute.isEmpty() && !NO_STATS.contains(item.getType())) return;

		Rarity rarity = rarity(item);
		Map<Enchantment, Integer> enchants = item.getEnchantments();

		List<Component> lore = new ArrayList<>(Utils.statLore(byAttribute::get, enchants));
		List<AttributeModifier> speed = byAttribute.get(Attribute.ATTACK_SPEED);
		if(speed != null) {
			double total = Utils.BASE_ATTACK_SPEED;
			for(AttributeModifier modifier : speed) {
				if(modifier.getOperation() == AttributeModifier.Operation.ADD_NUMBER) total += modifier.getAmount();
			}
			Utils.addAttackSpeedLore(lore, total);
		}
		lore.addAll(Utils.bonusDamageLore(enchants));
		List<Component> enchantLines = Utils.enchantmentLore(item);
		if(!enchantLines.isEmpty()) {
			if(!lore.isEmpty()) lore.add(Utils.mm(""));
			lore.addAll(enchantLines);
		}
		if(!lore.isEmpty()) lore.add(Utils.mm(""));
		lore.add(Utils.mm(rarity.lore(type(item.getType()))));
		item.setData(DataComponentTypes.LORE, ItemLore.lore(lore));

		// A VANILLA name comes from the material's default, not the stack's: the coloured name is a stored component and
		// smithing copies it, so diamond -> netherite kept "Diamond Sword". Any other item_name (a /give or loot-table
		// name) is the item's own and only gets recoloured. Anvil renames are custom_name and never touched.
		Component name = item.getData(DataComponentTypes.ITEM_NAME);
		if(name instanceof net.kyori.adventure.text.TranslatableComponent t
				&& (t.key().startsWith("item.minecraft.") || t.key().startsWith("block.minecraft."))) {
			name = ItemStack.of(item.getType()).getData(DataComponentTypes.ITEM_NAME);
		}
		if(name != null) {
			item.setData(DataComponentTypes.ITEM_NAME, name.color(NamedTextColor.NAMES.value(rarity.colour())));
		}

		Utils.hideInTooltip(item, DataComponentTypes.ATTRIBUTE_MODIFIERS, DataComponentTypes.ENCHANTMENTS);
	}

	private static Rarity rarity(ItemStack item) {
		Rarity special = SPECIAL.get(item.getType());
		if(special != null) return special;
		String name = item.getType().name();
		for(Map.Entry<String, Rarity> tier : TIERS.entrySet()) {
			if(name.startsWith(tier.getKey())) return tier.getValue();
		}
		return Rarity.COMMON;
	}

	/** Hypixel's type word on the rarity line; empty where it has none. PICKAXE before AXE (suffix overlap). */
	private static String type(Material material) {
		String name = material.name();
		if(material == Material.BOW || material == Material.CROSSBOW) return "BOW";
		if(material == Material.FISHING_ROD) return "FISHING ROD";
		if(material == Material.ELYTRA) return "CHESTPLATE";
		if(name.endsWith("_SWORD")) return "SWORD";
		if(name.endsWith("_SPEAR")) return "SPEAR";
		if(name.endsWith("_PICKAXE")) return "PICKAXE";
		if(name.endsWith("_AXE")) return "AXE";
		if(name.endsWith("_SHOVEL")) return "SHOVEL";
		if(name.endsWith("_HOE")) return "HOE";
		if(name.endsWith("_HELMET")) return "HELMET";
		if(name.endsWith("_CHESTPLATE")) return "CHESTPLATE";
		if(name.endsWith("_LEGGINGS")) return "LEGGINGS";
		if(name.endsWith("_BOOTS")) return "BOOTS";
		return "";
	}
}
