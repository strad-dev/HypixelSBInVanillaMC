package misc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings({"ExtractMethodRecommender", "DataFlowIssue"})
public class SimilarData {
	public static void changeName(LivingEntity entity) {
		if(!(entity instanceof Player)) {
			String[] oldName;
			int health = (int) (entity.getHealth() + entity.getAbsorptionAmount());
			int maxHealth = (int) Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
			try {
				oldName = Objects.requireNonNull(entity.getCustomName()).split(" ");
			} catch(Exception exception) {
				oldName = (entity.getName() + " " + ChatColor.YELLOW + health + "/" + maxHealth).split(" ");
			}
			oldName[oldName.length - 1] = ChatColor.YELLOW + "" + health + "/" + maxHealth;
			StringBuilder newName = new StringBuilder(oldName[0]);
			for(int i = 1; i < oldName.length; i++) {
				newName.append(" ").append(oldName[i]);
			}
			entity.setCustomName(newName.toString());
		}
	}

	/*
	 * ██╗████████╗███████╗███╗   ███╗███████╗
	 * ██║╚══██╔══╝██╔════╝████╗ ████║██╔════╝
	 * ██║   ██║   █████╗  ██╔████╔██║███████╗
	 * ██║   ██║   ██╔══╝  ██║╚██╔╝██║╚════██║
	 * ██║   ██║   ███████╗██║ ╚═╝ ██║███████║
	 * ╚═╝   ╚═╝   ╚══════╝╚═╝     ╚═╝╚══════╝
	 */

	public static ItemStack AOTV() {
		ItemStack aotv = new ItemStack(Material.NETHERITE_SHOVEL);

		ItemMeta data = aotv.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Aspect of the Void");
		AttributeModifier attackDamage = new AttributeModifier(UUID.fromString("bccbe67e-2649-4c53-8664-b00916b69125"), "AOTVModifier", -1000, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/aspect_of_the_void");
		lore.add("");
		lore.add(ChatColor.GRAY + "Melee Damage: " + ChatColor.RED + "0");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Instant Transmission " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Teleport " + ChatColor.GREEN + "12 blocks" + ChatColor.GRAY + " ahead of you.");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + "0");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Ether Transmission " + ChatColor.GREEN + ChatColor.BOLD + "SNEAK RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Teleport to your targetted block");
		lore.add(ChatColor.GRAY + "up to " + ChatColor.GREEN + "61 blocks" + ChatColor.GRAY + " blocks away.");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + "0");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC SHOVEL " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		aotv.setItemMeta(data);

		return aotv;
	}

	public static ItemStack scylla() {
		ItemStack scylla = new ItemStack(Material.NETHERITE_SWORD);

		ItemMeta data = scylla.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Hyperion");
		AttributeModifier attackSpeed = new AttributeModifier(UUID.fromString("d818fcd7-a2f8-434a-bfc3-6a352185714b"), "scyllaModifier", 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		AttributeModifier attackDamage = new AttributeModifier(UUID.fromString("b1bda7a7-b3eb-43b3-9e91-2473e9004f79"), "scyllaModifierDmg", 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeed);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/scylla");
		lore.add("");
		lore.add(ChatColor.GRAY + "Melee Damage: " + ChatColor.RED + "+8");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Wither Impact " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Teleport " + ChatColor.GREEN + "10 blocks" + ChatColor.GRAY + " ahead of");
		lore.add(ChatColor.GRAY + "you.  Then implode, dealing");
		lore.add(ChatColor.RED + "51%" + ChatColor.GRAY + " of your Melee Damage to");
		lore.add(ChatColor.GRAY + "nearby enemies.  Also applies");
		lore.add(ChatColor.GRAY + "the Wither Shield Scroll Ability,");
		lore.add(ChatColor.GRAY + "reducing damage taken and");
		lore.add(ChatColor.GRAY + "granting an absorption shield");
		lore.add(ChatColor.GRAY + "for " + ChatColor.YELLOW + "5 seconds.");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + "12");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC SWORD " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		scylla.setItemMeta(data);

		return scylla;
	}

	public static ItemStack term() {
		ItemStack term = new ItemStack(Material.BOW);

		ItemMeta data = term.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Terminator");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/terminator");
		lore.add("");
		lore.add(ChatColor.GRAY + "Ranged Damage: " + ChatColor.RED + "+1");
		lore.add(ChatColor.GRAY + "Shot Cooldown: " + ChatColor.GREEN + "0.25s");
		lore.add("");
		lore.add(ChatColor.GOLD + "Shortbow: Instantly Shoots!");
		lore.add(ChatColor.GRAY + "Shoots " + ChatColor.AQUA + "3" + ChatColor.GRAY + " arrows at once.");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Salvation " + ChatColor.GREEN + ChatColor.BOLD + "LEFT CLICK");
		lore.add(ChatColor.GRAY + "Shoot a beam, penetrating up to");
		lore.add(ChatColor.YELLOW + "5" + ChatColor.GRAY + " foes and dealing " + ChatColor.RED + "2");
		lore.add(ChatColor.GRAY + "damage to each enemy.");
		lore.add(ChatColor.GRAY + "Cooldown: " + ChatColor.GREEN + "1s");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC BOW " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		term.setItemMeta(data);

		return term;
	}

	public static ItemStack iceSpray() {
		ItemStack iceSpray = new ItemStack(Material.STICK);
		iceSpray.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = iceSpray.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + "Ice Spray Wand");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/ice_spray_wand");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Ice Spray " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Produces a cone of ice in front");
		lore.add(ChatColor.GRAY + "of the caster that deals");
		lore.add(ChatColor.RED + "1" + ChatColor.GRAY + " damage to mobs and");
		lore.add(ChatColor.GRAY + "freezes them in place for " + ChatColor.GREEN + "5");
		lore.add(ChatColor.GRAY + "seconds!  Frozen mobs take");
		lore.add(ChatColor.RED + "+1" + ChatColor.GRAY + " increased damage!");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + "4");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY WAND " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		iceSpray.setItemMeta(data);

		return iceSpray;
	}

	public static ItemStack wandOfRestoration() {
		ItemStack wand = new ItemStack(Material.STICK);
		wand.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = wand.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + "Wand of Restoration");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/wand_of_restoration");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Heal " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Gain Regeration I for");
		lore.add(ChatColor.GREEN + "5" + ChatColor.GRAY + " seconds!");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + "6");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC WAND " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		wand.setItemMeta(data);

		return wand;
	}

	public static ItemStack wandOfAtonement() {
		ItemStack wandOfAtonement = new ItemStack(Material.STICK);
		wandOfAtonement.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = wandOfAtonement.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + "Wand of Atonement");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/wand_of_atonement");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Big Heal " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Gain Regeration II for");
		lore.add(ChatColor.GREEN + "5" + ChatColor.GRAY + " seconds!");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + "6");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY WAND " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		wandOfAtonement.setItemMeta(data);

		return wandOfAtonement;
	}

	public static ItemStack holyIce() {
		ItemStack holyIce = new ItemStack(Material.DIAMOND);
		holyIce.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = holyIce.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + "Holy Ice");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/holy_ice");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Splash Yo Face " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Gain Resistance IV for");
		lore.add(ChatColor.GREEN + "1.5" + ChatColor.GRAY + " seconds!");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + "25");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		holyIce.setItemMeta(data);

		return holyIce;
	}

	public static ItemStack claymore() {
		ItemStack claymore = new ItemStack(Material.STONE_SWORD);

		ItemMeta data = claymore.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Dark Claymore");
		AttributeModifier attackSpeed = new AttributeModifier(UUID.fromString("e51c72e7-2b0d-4064-8d7f-89133cfd4b8b"), "claymoreModifier", 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		AttributeModifier attackDamage = new AttributeModifier(UUID.fromString("25cc1a90-327d-4115-a912-869e883862f4"), "claymoreModifierDmg", 9, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		AttributeModifier attackRange = new AttributeModifier(UUID.fromString("95037a43-49c4-4d9e-93a7-49c50c59e811"), "claymoreModifierRange", 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeed);
		data.addAttributeModifier(Attribute.PLAYER_ENTITY_INTERACTION_RANGE, attackRange);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/dark_claymore");
		lore.add("");
		lore.add(ChatColor.GRAY + "Melee Damage: " + ChatColor.RED + "+9");
		lore.add(ChatColor.GRAY + "Swing Range: " + ChatColor.RED + "+2");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "That thing was too big to be");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "called a sword, it was more like");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "a large hunk of stone.");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC SWORD " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		claymore.setItemMeta(data);

		return claymore;
	}

	public static ItemStack wardenHelmet() {
		ItemStack wardenHelmet = new ItemStack(Material.NETHERITE_HELMET);

		ItemMeta data = wardenHelmet.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Warden Helmet");
		AttributeModifier damage = new AttributeModifier(UUID.fromString("4b5a478b-3b58-42ab-917d-8194a1a6d679"), "wardenHelmetDamage", 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
		AttributeModifier armor = new AttributeModifier(UUID.fromString("b9f30162-1921-4cc6-ad60-91cc43234ba3"), "wardenHelmetArmor", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
		AttributeModifier toughness = new AttributeModifier(UUID.fromString("c1474682-574f-472c-a611-62da34f6e1f1"), "wardenHelmetToughness", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
		AttributeModifier antiKB = new AttributeModifier(UUID.fromString("4e16c033-3349-4fb7-a0c6-0fac98ef6c6a"), "wardenHelmetAntiKB", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
		AttributeModifier speed = new AttributeModifier(UUID.fromString("f15d0a37-e366-4c5a-982d-57995d036a6c"), "wardenHelmetSpeed", -0.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HEAD);
		data.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, speed);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/warden_helmet");
		lore.add("");
		lore.add(ChatColor.GRAY + "Melee Damage: " + ChatColor.RED + "+2");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "-10%");
		lore.add(ChatColor.GRAY + "Speed: " + ChatColor.RED + "x0.5");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "The brute force of the Warden");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "packed into a single helmet.");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "It grants you a lot of strength,");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "but makes you quite a bit sluggish.");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC HELMET " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		wardenHelmet.setItemMeta(data);

		return wardenHelmet;
	}

	public static ItemStack necronElytra() {
		ItemStack necronElytra = new ItemStack(Material.ELYTRA);
		necronElytra.addUnsafeEnchantment(Enchantment.PROTECTION, 4);

		ItemMeta data = necronElytra.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Necron's Elytra");
		AttributeModifier damage = new AttributeModifier(UUID.fromString("636ece54-f65c-4275-8ab7-916a1a1056ca"), "necronElytraDamage", 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
		AttributeModifier armor = new AttributeModifier(UUID.fromString("92166a58-d64d-465b-9810-3f8966f33566"), "necronElytraArmor", 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
		AttributeModifier toughness = new AttributeModifier(UUID.fromString("e0f20d73-639a-4530-937e-c7f298b268ec"), "necronElytraToughness", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
		AttributeModifier antiKB = new AttributeModifier(UUID.fromString("2855bb5e-6e64-4dea-a5c1-63725ec18c9a"), "necronElytraAntiKB", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/necron_elytra");
		lore.add("");
		lore.add(ChatColor.GRAY + "Melee Damage: " + ChatColor.RED + "+1");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+8");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "-10%");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "This Elytra has the stats of a Netherite");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "Chestplate, while still allowing you to fly!");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC CHESTPLATE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		necronElytra.setItemMeta(data);

		return necronElytra;
	}

	public static ItemStack goldorLeggings() {
		ItemStack goldorLeggings = new ItemStack(Material.NETHERITE_LEGGINGS);

		ItemMeta data = goldorLeggings.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Goldor's Leggings");
		AttributeModifier damage = new AttributeModifier(UUID.fromString("dc60c4c3-18ee-4e60-a580-7e3604e720e9"), "goldorLeggingsDamage", 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
		AttributeModifier armor = new AttributeModifier(UUID.fromString("80b8389f-f018-48c9-b3c0-c576bce5520a"), "goldorLeggingsArmor", 6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
		AttributeModifier toughness = new AttributeModifier(UUID.fromString("307e9660-858e-4ba3-9e60-2e0debf02345"), "goldorLeggingsToughness", 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
		AttributeModifier antiKB = new AttributeModifier(UUID.fromString("e4fd0260-7d71-4881-b47b-4c1bc68ac440"), "goldorLeggingsAntiKB", 0.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/goldor_pants");
		lore.add("");
		lore.add(ChatColor.GRAY + "Melee Damage: " + ChatColor.RED + "+1");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+6");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+4");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "-20%");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "Goldor has spent centuries");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "researching how to make Netherite");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "Leggings that are even tougher!");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC LEGGINGS " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		goldorLeggings.setItemMeta(data);

		return goldorLeggings;
	}

	public static ItemStack maxorBoots() {
		ItemStack maxorBoots = new ItemStack(Material.NETHERITE_BOOTS);

		ItemMeta data = maxorBoots.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Maxor's Boots");
		AttributeModifier damage = new AttributeModifier(UUID.fromString("c5be199a-6b3f-4860-b7b8-36e45fef60a3"), "maxorBootsDamage", 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
		AttributeModifier armor = new AttributeModifier(UUID.fromString("d8fe7972-aa27-42b4-aa5a-9eac4fb3b608"), "maxorBootsArmor", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
		AttributeModifier toughness = new AttributeModifier(UUID.fromString("f3058f46-630f-4cb5-8682-1141c5317e47"), "maxorBootsToughness", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
		AttributeModifier antiKB = new AttributeModifier(UUID.fromString("0c759c56-bd52-41a4-803f-f15275e61e4c"), "maxorBootsAntiKB", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
		AttributeModifier speed = new AttributeModifier(UUID.fromString("02307a42-a70a-4e55-a915-a71264f7455d"), "maxorBootsSpeed", 3, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.FEET);
		data.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, speed);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/maxor_boots");
		lore.add("");
		lore.add(ChatColor.GRAY + "Melee Damage: " + ChatColor.RED + "+1");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "-10%");
		lore.add(ChatColor.GRAY + "Speed: " + ChatColor.RED + "x4");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "Zoooooooooooooooooooooooooooooom");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC BOOTS " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		maxorBoots.setItemMeta(data);

		return maxorBoots;
	}


	/*
	 *  ██████╗██████╗  █████╗ ███████╗████████╗██╗███╗   ██╗ ██████╗
	 * ██╔════╝██╔══██╗██╔══██╗██╔════╝╚══██╔══╝██║████╗  ██║██╔════╝
	 * ██║     ██████╔╝███████║█████╗     ██║   ██║██╔██╗ ██║██║  ███╗
	 * ██║     ██╔══██╗██╔══██║██╔══╝     ██║   ██║██║╚██╗██║██║   ██║
	 * ╚██████╗██║  ██║██║  ██║██║        ██║   ██║██║ ╚████║╚██████╔╝
	 * ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝        ╚═╝   ╚═╝╚═╝  ╚═══╝ ╚═════╝
	 *
	 * ██╗███╗   ██╗ ██████╗ ██████╗ ███████╗██████╗ ██╗███████╗███╗   ██╗████████╗███████╗
	 * ██║████╗  ██║██╔════╝ ██╔══██╗██╔════╝██╔══██╗██║██╔════╝████╗  ██║╚══██╔══╝██╔════╝
	 * ██║██╔██╗ ██║██║  ███╗██████╔╝█████╗  ██║  ██║██║█████╗  ██╔██╗ ██║   ██║   ███████╗
	 * ██║██║╚██╗██║██║   ██║██╔══██╗██╔══╝  ██║  ██║██║██╔══╝  ██║╚██╗██║   ██║   ╚════██║
	 * ██║██║ ╚████║╚██████╔╝██║  ██║███████╗██████╔╝██║███████╗██║ ╚████║   ██║   ███████║
	 * ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝╚═════╝ ╚═╝╚══════╝╚═╝  ╚═══╝   ╚═╝   ╚══════╝
	 */

	public static ItemStack shadowWarp() {
		ItemStack shadowWarp = new ItemStack(Material.ENCHANTED_BOOK);
		shadowWarp.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = shadowWarp.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Shadow Warp");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/shadow_warp");
		lore.add("");
		lore.add(ChatColor.GRAY + "A rare Enchanted Book imbued");
		lore.add(ChatColor.GRAY + "with the power of Maxor.");
		lore.add(ChatColor.GRAY + "Grants the ability to teleport");
		lore.add(ChatColor.GRAY + "10 blocks using Wither Impact.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		shadowWarp.setItemMeta(data);

		return shadowWarp;
	}

	public static ItemStack implosion() {
		ItemStack implosion = new ItemStack(Material.ENCHANTED_BOOK);
		implosion.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = implosion.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Implosion");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/implosion");
		lore.add("");
		lore.add(ChatColor.GRAY + "A rare Enchanted Book imbued");
		lore.add(ChatColor.GRAY + "with the power of Storm.");
		lore.add(ChatColor.GRAY + "Grants the ability to implode");
		lore.add(ChatColor.GRAY + "and damage all enemies within");
		lore.add(ChatColor.GRAY + "10 blocks.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		implosion.setItemMeta(data);

		return implosion;
	}

	public static ItemStack witherShield() {
		ItemStack witherShield = new ItemStack(Material.ENCHANTED_BOOK);
		witherShield.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = witherShield.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Wither Shield");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/wither_shield");
		lore.add("");
		lore.add(ChatColor.GRAY + "A rare Enchanted Book imbued");
		lore.add(ChatColor.GRAY + "with the power of Goldor.");
		lore.add(ChatColor.GRAY + "Grants the ability to summon");
		lore.add(ChatColor.GRAY + "6 extra hearts of health,");
		lore.add(ChatColor.GRAY + "gain Regeneration II, and");
		lore.add(ChatColor.GRAY + "take 20% less damage for 5 seconds.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		witherShield.setItemMeta(data);

		return witherShield;
	}

	public static ItemStack handle() {
		ItemStack handle = new ItemStack(Material.STICK);
		handle.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = handle.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Necron's Handle");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/necron_handle");
		lore.add("");
		lore.add(ChatColor.GRAY + "The hilt of the GREATEST sword");
		lore.add(ChatColor.GRAY + "to ever exist, imbued with the");
		lore.add(ChatColor.GRAY + "power of Necron.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		handle.setItemMeta(data);

		return handle;
	}

	public static ItemStack giantSwordRemnant() {
		ItemStack giantSwordRemnant = new ItemStack(Material.STICK);
		giantSwordRemnant.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = giantSwordRemnant.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Remnant of the Giant's Sword");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/giant_sword_remnant");
		lore.add("");
		lore.add(ChatColor.GRAY + "A hilt with very strange properties.");
		lore.add(ChatColor.GRAY + "Legend has it that it once belonged to");
		lore.add(ChatColor.GRAY + "Sadan, but historians disagree on this.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		giantSwordRemnant.setItemMeta(data);

		return giantSwordRemnant;
	}

	public static ItemStack maxorSecrets() {
		ItemStack maxorSecrets = new ItemStack(Material.PAPER);
		maxorSecrets.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = maxorSecrets.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Maxor's Secrets");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/maxor_secrets");
		lore.add("");
		lore.add(ChatColor.GRAY + "Maxor has some secrets.  Being");
		lore.add(ChatColor.GRAY + "the youngest Wither, he was always");
		lore.add(ChatColor.GRAY + "able to snoop around and find");
		lore.add(ChatColor.GRAY + "a fair few bits of information");
		lore.add(ChatColor.GRAY + "that he otherwise was not supposed");
		lore.add(ChatColor.GRAY + "to know about until he was much older.");
		lore.add(ChatColor.GRAY + "This meant that he was also chased");
		lore.add(ChatColor.GRAY + "around a lot, which prompted him to");
		lore.add(ChatColor.GRAY + "learn how to fly faster.  The paper");
		lore.add(ChatColor.GRAY + "is ripped after this point, but");
		lore.add(ChatColor.GRAY + "it still has some useful properties.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		maxorSecrets.setItemMeta(data);

		return maxorSecrets;
	}

	public static ItemStack stormSecrets() {
		ItemStack stormSecrets = new ItemStack(Material.PAPER);
		stormSecrets.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = stormSecrets.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Storm's Secrets");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/storm_secrets");
		lore.add("");
		lore.add(ChatColor.GRAY + "Despite being acquainted with the");
		lore.add(ChatColor.GRAY + "power of lightning, Storm is also");
		lore.add(ChatColor.GRAY + "acquainted with attacking players");
		lore.add(ChatColor.GRAY + "from much further than they are");
		lore.add(ChatColor.GRAY + "supposed to.  This paper contains");
		lore.add(ChatColor.GRAY + "the essence of the research.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		stormSecrets.setItemMeta(data);

		return stormSecrets;
	}

	public static ItemStack goldorSecrets() {
		ItemStack goldorSecrets = new ItemStack(Material.PAPER);
		goldorSecrets.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = goldorSecrets.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Goldor's Secrets");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/goldor_secrets");
		lore.add("");
		lore.add(ChatColor.GRAY + "A piece of research that has over three");
		lore.add(ChatColor.GRAY + "centuries of work, studying how to grant");
		lore.add(ChatColor.GRAY + "just one more of a stat deemed to already");
		lore.add(ChatColor.GRAY + "have been completely maxed out.  Unfortunately,");
		lore.add(ChatColor.GRAY + "Goldor was a but flustered at being defeated,");
		lore.add(ChatColor.GRAY + "and dropped this research behind him.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		goldorSecrets.setItemMeta(data);

		return goldorSecrets;
	}

	public static ItemStack necronSecrets() {
		ItemStack necronSecrets = new ItemStack(Material.PAPER);
		necronSecrets.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = necronSecrets.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Necron's Secrets");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/necron_secrets");
		lore.add("");
		lore.add(ChatColor.GRAY + "Being the right-hand man of the");
		lore.add(ChatColor.GRAY + "Wither King, Necron knows some tricks");
		lore.add(ChatColor.GRAY + "about being able to fly with lots of armor.");
		lore.add(ChatColor.GRAY + "This paper, which he dropped behind him,");
		lore.add(ChatColor.GRAY + "contains all of those secrets.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		necronSecrets.setItemMeta(data);

		return necronSecrets;
	}

	public static ItemStack wardenHeart() {
		ItemStack wardenHeart = new ItemStack(Material.REDSTONE_BLOCK);
		wardenHeart.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = wardenHeart.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Warden Heart");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/warden_heart");
		lore.add("");
		lore.add(ChatColor.GRAY + "The heart of a powerful creature,");
		lore.add(ChatColor.GRAY + "dropped by the Warden.");
		lore.add(ChatColor.GRAY + "(NOT the Atoned Horror)");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		wardenHeart.setItemMeta(data);

		return wardenHeart;
	}

	public static ItemStack core() {
		ItemStack core = new ItemStack(Material.CHISELED_QUARTZ_BLOCK);
		core.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = core.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Judgement Core");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/judgement_core");
		lore.add("");
		lore.add(ChatColor.GRAY + "A core so powerful that even");
		lore.add(ChatColor.GRAY + "the most dedicated players");
		lore.add(ChatColor.GRAY + "tremble at it's power.");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		core.setItemMeta(data);

		return core;
	}

	public static ItemStack tessellatedPearl() {
		ItemStack tessellated = new ItemStack(Material.ENDER_PEARL);
		tessellated.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = tessellated.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Tessellated Ender Pearl");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/tessellated_pearl");
		lore.add("");
		lore.add(ChatColor.GRAY + "An Ender Pearl so dense that even");
		lore.add(ChatColor.GRAY + "the most knowledgeable players");
		lore.add(ChatColor.GRAY + "are mystified by it.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		tessellated.setItemMeta(data);

		return tessellated;
	}

	public static ItemStack nullBlade() {
		ItemStack nullBlade = new ItemStack(Material.SHEARS);
		nullBlade.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = nullBlade.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Null Blade");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/null_blade");
		lore.add("");
		lore.add(ChatColor.GRAY + "A pair of Shears so null that even");
		lore.add(ChatColor.GRAY + "the most intelligent players");
		lore.add(ChatColor.GRAY + "are confused by it.");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		nullBlade.setItemMeta(data);

		return nullBlade;
	}

	public static ItemStack braidedFeather() {
		ItemStack braidedFeather = new ItemStack(Material.FEATHER);
		braidedFeather.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = braidedFeather.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Braided Feather");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/braided_feather");
		lore.add("");
		lore.add(ChatColor.GRAY + "A Feather so sturdy that even");
		lore.add(ChatColor.GRAY + "the most powerful players");
		lore.add(ChatColor.GRAY + "cannot destroy it.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		braidedFeather.setItemMeta(data);

		return braidedFeather;
	}

	public static ItemStack taraSilk() {
		ItemStack taraSilk = new ItemStack(Material.COBWEB);
		taraSilk.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = taraSilk.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Tarantula Silk");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/tarantula_silk");
		lore.add("");
		lore.add(ChatColor.GRAY + "A Web so perfect that even");
		lore.add(ChatColor.GRAY + "the most powerful players");
		lore.add(ChatColor.GRAY + "cannot escape it.");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		taraSilk.setItemMeta(data);

		return taraSilk;
	}

	public static ItemStack viscera() {
		ItemStack viscera = new ItemStack(Material.COOKED_PORKCHOP);
		viscera.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = viscera.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Revenant Viscera");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/ingredient/revenant_viscera");
		lore.add("");
		lore.add(ChatColor.GRAY + "The disgusting remains");
		lore.add(ChatColor.GRAY + "of a horror that once");
		lore.add(ChatColor.GRAY + "walked this world.");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		viscera.setItemMeta(data);

		return viscera;
	}

	/*
	 * ███████╗██████╗  █████╗ ██╗    ██╗███╗   ██╗██╗███╗   ██╗ ██████╗
	 * ██╔════╝██╔══██╗██╔══██╗██║    ██║████╗  ██║██║████╗  ██║██╔════╝
	 * ███████╗██████╔╝███████║██║ █╗ ██║██╔██╗ ██║██║██╔██╗ ██║██║  ███╗
	 * ╚════██║██╔═══╝ ██╔══██║██║███╗██║██║╚██╗██║██║██║╚██╗██║██║   ██║
	 * ███████║██║     ██║  ██║╚███╔███╔╝██║ ╚████║██║██║ ╚████║╚██████╔╝
	 * ╚══════╝╚═╝     ╚═╝  ╚═╝ ╚══╝╚══╝ ╚═╝  ╚═══╝╚═╝╚═╝  ╚═══╝ ╚═════╝
	 *
	 * ██╗████████╗███████╗███╗   ███╗███████╗
	 * ██║╚══██╔══╝██╔════╝████╗ ████║██╔════╝
	 * ██║   ██║   █████╗  ██╔████╔██║███████╗
	 * ██║   ██║   ██╔══╝  ██║╚██╔╝██║╚════██║
	 * ██║   ██║   ███████╗██║ ╚═╝ ██║███████║
	 * ╚═╝   ╚═╝   ╚══════╝╚═╝     ╚═╝╚══════╝
	 */

	public static ItemStack supRemnant() {
		ItemStack supRemnant = new ItemStack(Material.QUARTZ);
		supRemnant.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = supRemnant.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Remnant of the Superior Dragon");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/summon/superior_remnant");
		lore.add("");
		lore.add(ChatColor.GRAY + "The remains of the strongest");
		lore.add(ChatColor.GRAY + "Dragon to ever exist.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Use this on an Enderman to summon");
		lore.add(ChatColor.GRAY + "the terrifying Voidgloom Seraph!");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		supRemnant.setItemMeta(data);

		return supRemnant;
	}

	public static ItemStack corruptedPearl() {
		ItemStack corruptPearl = new ItemStack(Material.ENDER_EYE);
		corruptPearl.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = corruptPearl.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Corrupted Pearl");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/summon/corrupt_pearl");
		lore.add("");
		lore.add(ChatColor.GRAY + "An interesting mutation");
		lore.add(ChatColor.GRAY + "of Ender Pearls.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Use this on an Enderman to");
		lore.add(ChatColor.GRAY + "summon a Mutant Enderman!");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		corruptPearl.setItemMeta(data);

		return corruptPearl;
	}

	public static ItemStack antimatter() {
		ItemStack antimatter = new ItemStack(Material.WARPED_FUNGUS);
		antimatter.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = antimatter.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Antimatter");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/summon/antimatter");
		lore.add("");
		lore.add(ChatColor.GRAY + "The consequence of storing such");
		lore.add(ChatColor.GRAY + "massive amounts of Iron together.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Use this on an Iron Golem");
		lore.add(ChatColor.GRAY + "to summon a meloG norI!");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		antimatter.setItemMeta(data);

		return antimatter;
	}

	public static ItemStack omegaEgg() {
		ItemStack omegaEgg = new ItemStack(Material.EGG);
		omegaEgg.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = omegaEgg.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Omega Egg");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/summon/omega_egg");
		lore.add("");
		lore.add(ChatColor.GRAY + "A strange specimen used to");
		lore.add(ChatColor.GRAY + "create the strongest Chickens.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Use this on a Chicken");
		lore.add(ChatColor.GRAY + "to summon Chickzilla!");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		omegaEgg.setItemMeta(data);

		return omegaEgg;
	}

	public static ItemStack spiderRelic() {
		ItemStack spiderRelic = new ItemStack(Material.FERMENTED_SPIDER_EYE);
		spiderRelic.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = spiderRelic.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Spider Relic");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/summon/spider_relic");
		lore.add("");
		lore.add(ChatColor.GRAY + "An ancient artifact left");
		lore.add(ChatColor.GRAY + "by the Broodfather itself.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Use this on a Spider to summon");
		lore.add(ChatColor.GRAY + "the Tarantula Broodfather!");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		spiderRelic.setItemMeta(data);

		return spiderRelic;
	}

	public static ItemStack atonedFlesh() {
		ItemStack atonedFlesh = new ItemStack(Material.ROTTEN_FLESH);
		atonedFlesh.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = atonedFlesh.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Atoned Flesh");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/summon/atoned_flesh");
		lore.add("");
		lore.add(ChatColor.GRAY + "A suspicious piece of flesh");
		lore.add(ChatColor.GRAY + "left behind by an unknown being.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Use this on a Zombie to");
		lore.add(ChatColor.GRAY + "summon the Atoned Horror!");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		atonedFlesh.setItemMeta(data);

		return atonedFlesh;
	}

	public static ItemStack giantZombieFlesh() {
		ItemStack giantZombieFlesh = new ItemStack(Material.ROTTEN_FLESH);
		giantZombieFlesh.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = giantZombieFlesh.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Giant Zombie Flesh");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/summon/giant_flesh");
		lore.add("");
		lore.add(ChatColor.GRAY + "This zombie dropped a larger");
		lore.add(ChatColor.GRAY + "piece of flesh than normal.");
		lore.add(ChatColor.GRAY + "Maybe it has useful properties?");
		lore.add("");
		lore.add(ChatColor.GRAY + "Use this on a Zombie");
		lore.add(ChatColor.GRAY + "to summon Sadan!");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		giantZombieFlesh.setItemMeta(data);

		return giantZombieFlesh;
	}
}