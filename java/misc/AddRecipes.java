package misc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class AddRecipes {
	public static Recipe addScyllaRecipe(Plugin plugin) {
		ItemStack scylla = new ItemStack(Material.NETHERITE_SWORD);
		ItemMeta data = scylla.getItemMeta();
		assert data != null;
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Suspicious Scylla " + ChatColor.RED + "✪✪✪✪✪");
		AttributeModifier attackSpeed = new AttributeModifier("scyllaModifierAts", 100, AttributeModifier.Operation.ADD_NUMBER);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeed);
		AttributeModifier attackDamage = new AttributeModifier("scyllaModifierDmg", 10, AttributeModifier.Operation.ADD_NUMBER);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		List<String> lore = new ArrayList<>();

		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+2355.2 " + ChatColor.YELLOW + "(+30) " + ChatColor.BLUE + "(+15)");
		lore.add(ChatColor.GRAY + "Strength: " + ChatColor.RED + "+1388.8 " + ChatColor.YELLOW + "(+30) " + ChatColor.GOLD + "[+5] " + ChatColor.LIGHT_PURPLE + "(+32)");
		lore.add(ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "+140.8% " + ChatColor.BLUE + "(+10%)");
		lore.add(ChatColor.GRAY + "Crit Damage: " + ChatColor.RED + "+1888% " + ChatColor.BLUE + "(+110%)");
		lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+320");
		lore.add(ChatColor.GRAY + "Magic Find: " + ChatColor.GREEN + "+38.4");
		lore.add(ChatColor.GRAY + "Ferocity: " + ChatColor.GREEN + "+224");
		lore.add(ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "⚔" + ChatColor.GOLD + "] [" + ChatColor.LIGHT_PURPLE + "⚔" + ChatColor.GOLD + "]");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + "Chimera V" + ChatColor.RESET + ChatColor.GRAY + ", " + ChatColor.GOLD + "Champion X" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Cleave VI" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Critical VII" + ChatColor.GRAY + ",");
		lore.add(ChatColor.GOLD + "Cubism VI" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Divine Gift III" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Dragon Hunter V" + ChatColor.GRAY + ",");
		lore.add(ChatColor.GOLD + "Ender Slayer VII" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Execute VI" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Experience V" + ChatColor.GRAY + ",");
		lore.add(ChatColor.GOLD + "Fire Aspect III" + ChatColor.GRAY + ", " + ChatColor.GOLD + "First Strike V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Giant Killer VII" + ChatColor.GRAY + ",");
		lore.add(ChatColor.GOLD + "Impaling III" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Lethality VI" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Looting V" + ChatColor.GRAY + ",");
		lore.add(ChatColor.GOLD + "Luck VII" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Scavenger V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Sharpness VII" + ChatColor.GRAY + ",");
		lore.add(ChatColor.GOLD + "Smoldering V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Syphon V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Tabasco III" + ChatColor.GRAY + ",");
		lore.add(ChatColor.GOLD + "Thunderlord VII" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Vampirism VI" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Venomous VI" + ChatColor.GRAY + ",");
		lore.add(ChatColor.GOLD + "Vicious V");
		lore.add("");
		lore.add(ChatColor.GRAY + "Deals " + ChatColor.RED + "+50%" + ChatColor.GRAY + " damage to");
		lore.add(ChatColor.GRAY + "Withers.  Grants " + ChatColor.RED + "+1 ❁ Damage");
		lore.add(ChatColor.GRAY + "and " + ChatColor.GREEN + "+1 " + ChatColor.BLUE + " ☠ Crit Damage" + ChatColor.GRAY + " per");
		lore.add(ChatColor.RED + "Catacombs" + ChatColor.GRAY + " level.");
		lore.add("");
		lore.add(ChatColor.GREEN + "Scroll Abilities:");
		lore.add(ChatColor.GOLD + "Ability: Wither Impact " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Teleport " + ChatColor.GREEN + "10 blocks" + ChatColor.GRAY + " ahead of");
		lore.add(ChatColor.GRAY + "you.  Then implode, dealing");
		lore.add(ChatColor.RED + "2147483647" + ChatColor.GRAY + " damage to nearby");
		lore.add(ChatColor.GRAY + "enemies.  Also applies the Wither");
		lore.add(ChatColor.GRAY + "Shield Scroll Ability, reducing");
		lore.add(ChatColor.GRAY + "damage taken and granting an");
		lore.add(ChatColor.GRAY + "absorption shield for " + ChatColor.YELLOW + "5");
		lore.add(ChatColor.GRAY + "seconds.");
		lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + "0");
		lore.add("");
		lore.add(ChatColor.BLUE + "Suspicious Bonus");
		lore.add(ChatColor.GRAY + "Increases weapon damage by");
		lore.add(ChatColor.RED + "+15" + ChatColor.GRAY + ".");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC DUNGEON SWORD " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		scylla.setItemMeta(data);

		ItemStack shadowWarp = new ItemStack(Material.ENCHANTED_BOOK);
		data = shadowWarp.getItemMeta();
		assert data != null;
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Shadow Warp");
		lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A rare Enchanted Book imbued");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "with the power of Maxor.");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Grants the ability to teleport");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "10 blocks using Wither Impact.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Combine with the other two");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Wither Scrolls and Necron's");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Handle to create the Scylla!");
		data.setLore(lore);
		shadowWarp.setItemMeta(data);

		ItemStack implosion = new ItemStack(Material.ENCHANTED_BOOK);
		data = implosion.getItemMeta();
		assert data != null;
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Implosion");
		lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A rare Enchanted Book imbued");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "with the power of Storm.");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Grants the ability to implode");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "and damage all enemies within");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "10 blocks.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Combine with the other two");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Wither Scrolls and Necron's");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Handle to create the Scylla!");
		data.setLore(lore);
		implosion.setItemMeta(data);

		ItemStack witherShield = new ItemStack(Material.ENCHANTED_BOOK);
		data = witherShield.getItemMeta();
		assert data != null;
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Wither Shield");
		lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A rare Enchanted Book imbued");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "with the power of Goldor.");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Grants the ability to summon");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "6 extra hearts of health,");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "gain Regeneration II, and");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "take 20% less damage for 5 seconds.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Combine with the other two");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Wither Scrolls and Necron's");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Handle to create the Scylla!");
		data.setLore(lore);
		witherShield.setItemMeta(data);

		ItemStack handle = new ItemStack(Material.STICK);
		data = handle.getItemMeta();
		assert data != null;
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Necron's Handle");
		lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "The hilt of the GREATEST sword");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "to ever exist, imbued with the");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "power of Necron.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use this with a Netherite Sword");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "and the other three Wither");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Scrolls to craft the Scylla!");
		lore.add(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "WARNING: REMOVES ALL ENCHANTMENTS");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
		handle.setItemMeta(data);
		handle.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		NamespacedKey scyllaKey = new NamespacedKey(plugin, "scylla");

		ShapelessRecipe scyllaRecipe = new ShapelessRecipe(scyllaKey, scylla);
		scyllaRecipe.addIngredient(Material.NETHER_STAR);
		scyllaRecipe.addIngredient(Material.NETHER_STAR);
		scyllaRecipe.addIngredient(Material.NETHER_STAR);
		scyllaRecipe.addIngredient(Material.NETHER_STAR);
		scyllaRecipe.addIngredient(Material.NETHERITE_SWORD);

		scyllaRecipe.addIngredient(new RecipeChoice.ExactChoice(handle));
		scyllaRecipe.addIngredient(new RecipeChoice.ExactChoice(witherShield));
		scyllaRecipe.addIngredient(new RecipeChoice.ExactChoice(shadowWarp));
		scyllaRecipe.addIngredient(new RecipeChoice.ExactChoice(implosion));

		return scyllaRecipe;
	}

	public static Recipe addTermRecipe(Plugin plugin) {
		ItemStack term = new ItemStack(Material.BOW);

		ItemMeta data = term.getItemMeta();
		assert data != null;
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Precise Terminator " + ChatColor.RED + "✪✪✪✪✪");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		List<String> lore = new ArrayList<>();

		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+2195.2 " + ChatColor.YELLOW + "(+30)");
		lore.add(ChatColor.GRAY + "Strength: " + ChatColor.RED + "+761.6 " + ChatColor.YELLOW + "(+30) " + ChatColor.GOLD + "[+5] " + ChatColor.BLUE + "(+34)");
		lore.add(ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "+128% " + ChatColor.BLUE + "(+15%)");
		lore.add(ChatColor.GRAY + "Crit Damage: " + ChatColor.RED + "+2080% " + ChatColor.BLUE + "(+70%)");
		lore.add(ChatColor.GRAY + "Bonus Attack Speed: " + ChatColor.RED + "+256%");
		lore.add(ChatColor.GRAY + "Magic Find: " + ChatColor.GREEN + "+38.4");
		lore.add(ChatColor.GRAY + "Ferocity: " + ChatColor.GREEN + "+32");
		lore.add(ChatColor.GRAY + "Shot Cooldown: " + ChatColor.GREEN + "0.0s");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + "Fatal Tempo V" + ChatColor.RESET + ChatColor.GRAY + ", " + ChatColor.GOLD + "Chance V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Cubism VI" + ChatColor.GRAY + ",");
		lore.add(ChatColor.GOLD + "Divine Gift III" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Dragon Hunter V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Dragon Tracer V" + ChatColor.GRAY + ",");
		lore.add(ChatColor.GOLD + "Flame II" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Impaling III" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Infinite Quiver X" + ChatColor.GRAY + ",");
		lore.add(ChatColor.GOLD + "Overload V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Piercing I" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Power VII" + ChatColor.GRAY + ",");
		lore.add(ChatColor.GOLD + "Smoldering V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Snipe IV" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Vicious V");
		lore.add("");
		lore.add(ChatColor.GOLD + "Shortbow: Instantly Shoots!");
		lore.add(ChatColor.GRAY + "Shoots " + ChatColor.AQUA + "3" + ChatColor.GRAY + " arrows at once.");
		lore.add(ChatColor.GRAY + "Can damage Endermen");
		lore.add("");
		lore.add(ChatColor.RED + "Divides your " + ChatColor.BLUE + "☣ Crit Chance" + ChatColor.RED + " by 4!");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Salvation " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Can be casted after landing " + ChatColor.GOLD + "3" + ChatColor.GRAY + " hits.");
		lore.add(ChatColor.GRAY + "Shoot a beam, penetrating up");
		lore.add(ChatColor.GRAY + "to " + ChatColor.YELLOW + "5" + ChatColor.GRAY + " foes and dealing " + ChatColor.RED + "2x");
		lore.add(ChatColor.GRAY + "the damage an arrow would.");
		lore.add(ChatColor.GRAY + "The beam always crits.");
		lore.add(ChatColor.DARK_GRAY + "Soulflow Cost: " + ChatColor.DARK_AQUA + "0⸎");
		lore.add(ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + "0s");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC DUNGEON BOW " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		term.setItemMeta(data);

		ItemStack core = new ItemStack(Material.CHISELED_QUARTZ_BLOCK);
		data = core.getItemMeta();
		assert data != null;
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Judgement Core");
		lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A core so powerful that even");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the most dedicated players");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "tremble at it's power.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use this to craft the Terminator!");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
		core.setItemMeta(data);
		core.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemStack tessellated = new ItemStack(Material.ENDER_PEARL);
		data = tessellated.getItemMeta();
		assert data != null;
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Tessellated Ender Pearl");
		lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "An Ender Pearl so dense that even");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the most knowledgeable players");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "are mystified by it.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use 2 of this to craft the Terminator!");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
		tessellated.setItemMeta(data);
		tessellated.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemStack nullBlade = new ItemStack(Material.SHEARS);
		data = nullBlade.getItemMeta();
		assert data != null;
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Null Blade");
		lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A pair of Shears so null that even");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the most intelligent players");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "are confused by it.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use 3 of these to craft the Terminator!");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
		nullBlade.setItemMeta(data);
		nullBlade.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemStack braidedFeather = new ItemStack(Material.FEATHER);
		data = braidedFeather.getItemMeta();
		assert data != null;
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Braided Feather");
		lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A Feather so sturdy that even");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the most powerful players");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "cannot destroy it.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use this to craft the Terminator!");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
		braidedFeather.setItemMeta(data);
		braidedFeather.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemStack taraSilk = new ItemStack(Material.COBWEB);
		data = taraSilk.getItemMeta();
		assert data != null;
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Tarantula Silk");
		lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A Web so perfect that even");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the most powerful players");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "cannot escape it.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use 2 of this to craft the Terminator!");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
		taraSilk.setItemMeta(data);
		taraSilk.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		NamespacedKey termKey = new NamespacedKey(plugin, "term");

		ShapelessRecipe termRecipe = new ShapelessRecipe(termKey, term);

		termRecipe.addIngredient(new RecipeChoice.ExactChoice(core));
		termRecipe.addIngredient(new RecipeChoice.ExactChoice(tessellated));
		termRecipe.addIngredient(new RecipeChoice.ExactChoice(tessellated));
		termRecipe.addIngredient(new RecipeChoice.ExactChoice(nullBlade));
		termRecipe.addIngredient(new RecipeChoice.ExactChoice(nullBlade));
		termRecipe.addIngredient(new RecipeChoice.ExactChoice(nullBlade));
		termRecipe.addIngredient(new RecipeChoice.ExactChoice(braidedFeather));
		termRecipe.addIngredient(new RecipeChoice.ExactChoice(taraSilk));
		termRecipe.addIngredient(new RecipeChoice.ExactChoice(taraSilk));

		return termRecipe;
	}
}