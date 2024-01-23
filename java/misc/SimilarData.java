package misc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
		AttributeModifier attackDamage = new AttributeModifier("AOTVModifier", -1000, AttributeModifier.Operation.ADD_NUMBER);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Melee Damage: " + ChatColor.RED + "0");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Instant Transmission " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Teleport " + ChatColor.GREEN + "12 blocks" + ChatColor.GRAY + " ahead of you.");
		lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + "0");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Ether Transmission " + ChatColor.GREEN + ChatColor.BOLD + "SNEAK RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Teleport to your targetted block");
		lore.add(ChatColor.GRAY + "up to " + ChatColor.GREEN + "61 blocks" + ChatColor.GRAY + " blocks away.");
		lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + "0");
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
		AttributeModifier attackSpeed = new AttributeModifier("scyllaModifier", 100, AttributeModifier.Operation.ADD_NUMBER);
		AttributeModifier attackDamage = new AttributeModifier("scyllaModifierDmg", 8, AttributeModifier.Operation.ADD_NUMBER);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeed);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Melee Damage: " + ChatColor.RED + "+8");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Wither Impact " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Teleport " + ChatColor.GREEN + "10 blocks" + ChatColor.GRAY + " ahead of");
		lore.add(ChatColor.GRAY + "you.  Then implode, dealing");
		lore.add(ChatColor.RED + "4-6" + ChatColor.GRAY + " damage to nearby");
		lore.add(ChatColor.GRAY + "enemies.  Also applies the Wither");
		lore.add(ChatColor.GRAY + "Shield Scroll Ability, reducing");
		lore.add(ChatColor.GRAY + "damage taken and granting an");
		lore.add(ChatColor.GRAY + "absorption shield for " + ChatColor.YELLOW + "5");
		lore.add(ChatColor.GRAY + "seconds.");
		lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + "8");
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
		lore.add("");
		lore.add(ChatColor.GRAY + "Ranged Damage: " + ChatColor.RED + "+2");
		lore.add("");
		lore.add(ChatColor.GOLD + "Shortbow: Instantly Shoots!");
		lore.add(ChatColor.GRAY + "Shoots " + ChatColor.AQUA + "3" + ChatColor.GRAY + " arrows at once.");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Salvation " + ChatColor.GREEN + ChatColor.BOLD + "LEFT CLICK");
		lore.add(ChatColor.GRAY + "Can be casted after landing " + ChatColor.GOLD + "3" + ChatColor.GRAY + " hits.");
		lore.add(ChatColor.GRAY + "Shoot a beam, penetrating up");
		lore.add(ChatColor.GRAY + "to " + ChatColor.YELLOW + "5" + ChatColor.GRAY + " foes and dealing " + ChatColor.RED + "2x");
		lore.add(ChatColor.GRAY + "the damage an arrow would.");
		lore.add(ChatColor.GRAY + "The beam always crits.");
		lore.add(ChatColor.GRAY + "This ability does not exist zzz.");
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
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Ice Spray " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Produces a cone of ice in front");
		lore.add(ChatColor.GRAY + "of the caster that deals");
		lore.add(ChatColor.RED + "1" + ChatColor.GRAY + " damage to mobs and");
		lore.add(ChatColor.GRAY + "freezes them in place for " + ChatColor.GREEN + "5");
		lore.add(ChatColor.GRAY + "seconds!  Frozen mobs take");
		lore.add(ChatColor.RED + "+1" + ChatColor.GRAY + " increased damage!");
		lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + "2");
		lore.add(ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + "5s");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY WAND " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		iceSpray.setItemMeta(data);

		return iceSpray;
	}

	public static ItemStack wandOfAtonement() {
		ItemStack wandOfAtonement = new ItemStack(Material.STICK);
		wandOfAtonement.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = wandOfAtonement.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + "Wand of Atonement");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Heal " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Gain Regeration I for");
		lore.add(ChatColor.GREEN + "5" + ChatColor.GRAY + " seconds!");
		lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + "4");
		lore.add(ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + "5s");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY WAND " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		wandOfAtonement.setItemMeta(data);

		return wandOfAtonement;
	}

	public static ItemStack claymore() {
		ItemStack claymore = new ItemStack(Material.STONE_SWORD);

		ItemMeta data = claymore.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Dark Claymore");
		AttributeModifier attackSpeed = new AttributeModifier("claymoreModifier", 100, AttributeModifier.Operation.ADD_NUMBER);
		AttributeModifier attackDamage = new AttributeModifier("claymoreModifierDmg", 9, AttributeModifier.Operation.ADD_NUMBER);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeed);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
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
		AttributeModifier damage = new AttributeModifier("wardenHelmetDamage", 2, AttributeModifier.Operation.ADD_NUMBER);
		AttributeModifier armor = new AttributeModifier("wardenHelmetArmor", 3, AttributeModifier.Operation.ADD_NUMBER);
		AttributeModifier toughness = new AttributeModifier("wardenHelmetToughness", 3, AttributeModifier.Operation.ADD_NUMBER);
		AttributeModifier antiKB = new AttributeModifier("wardenHelmetAntiKB", 0.1, AttributeModifier.Operation.ADD_NUMBER);
		AttributeModifier speed = new AttributeModifier("wardenHelmetSpeed", -0.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
		data.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, speed);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Melee Damage: " + ChatColor.RED + "+2");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "+1");
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
		necronElytra.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		necronElytra.addUnsafeEnchantment(Enchantment.THORNS, 3);

		ItemMeta data = necronElytra.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Necron's Elytra");
		AttributeModifier damage = new AttributeModifier("necronElytraDamage", 1, AttributeModifier.Operation.ADD_NUMBER);
		AttributeModifier armor = new AttributeModifier("necronElytraArmor", 8, AttributeModifier.Operation.ADD_NUMBER);
		AttributeModifier toughness = new AttributeModifier("necronElytraToughness", 3, AttributeModifier.Operation.ADD_NUMBER);
		AttributeModifier antiKB = new AttributeModifier("necronElytraAntiKB", 0.1, AttributeModifier.Operation.ADD_NUMBER);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Melee Damage: " + ChatColor.RED + "+1");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+8");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "+1");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "This Elytra has the stats of a Netherite");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "Chestplate, while still allowing you to fly!");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC CHESTPLATE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		necronElytra.setItemMeta(data);

		return necronElytra;
	}

	public static ItemStack maxorBoots() {
		ItemStack maxorBoots = new ItemStack(Material.NETHERITE_BOOTS);

		ItemMeta data = maxorBoots.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Maxor's Boots");
		AttributeModifier damage = new AttributeModifier("maxorBootsDamage", 1, AttributeModifier.Operation.ADD_NUMBER);
		AttributeModifier armor = new AttributeModifier("maxorBootsArmor", 3, AttributeModifier.Operation.ADD_NUMBER);
		AttributeModifier toughness = new AttributeModifier("maxorBootsToughness", 3, AttributeModifier.Operation.ADD_NUMBER);
		AttributeModifier antiKB = new AttributeModifier("maxorBootsAntiKB", 0.1, AttributeModifier.Operation.ADD_NUMBER);
		AttributeModifier speed = new AttributeModifier("maxorBootsSpeed", 3, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
		data.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, speed);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damage);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
		data.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
		data.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, antiKB);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Melee Damage: " + ChatColor.RED + "+1");
		lore.add(ChatColor.GRAY + "Defense: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Toughness: " + ChatColor.RED + "+3");
		lore.add(ChatColor.GRAY + "Knockback Resistance: " + ChatColor.RED + "+1");
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
		lore.add(ChatColor.GRAY + "The hilt of the GREATEST sword");
		lore.add(ChatColor.GRAY + "to ever exist, imbued with the");
		lore.add(ChatColor.GRAY + "power of Necron.");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		handle.setItemMeta(data);

		return handle;
	}

	public static ItemStack maxorSecrets() {
		ItemStack maxorSecrets = new ItemStack(Material.PAPER);
		maxorSecrets.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = maxorSecrets.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Maxor's Secrets");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
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

	public static ItemStack necronSecrets() {
		ItemStack necronSecrets = new ItemStack(Material.PAPER);
		necronSecrets.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = necronSecrets.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Necron's Secrets");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
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
}