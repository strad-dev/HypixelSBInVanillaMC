package misc;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("DataFlowIssue")
public class AddRecipes {
	public static Recipe addScyllaRecipe(Plugin plugin) {
		ItemStack scylla = new ItemStack(Material.NETHERITE_SWORD);
		scylla.setItemMeta(SimilarData.scyllaMeta(scylla.getItemMeta()));

		ItemStack shadowWarp = new ItemStack(Material.ENCHANTED_BOOK);
		shadowWarp.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		shadowWarp.setItemMeta(SimilarData.shadowWarpMeta(shadowWarp.getItemMeta()));

		ItemStack implosion = new ItemStack(Material.ENCHANTED_BOOK);
		implosion.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		implosion.setItemMeta(SimilarData.implosionMeta(implosion.getItemMeta()));

		ItemStack witherShield = new ItemStack(Material.ENCHANTED_BOOK);
		witherShield.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		witherShield.setItemMeta(SimilarData.witherShieldMeta(witherShield.getItemMeta()));

		ItemStack handle = new ItemStack(Material.STICK);
		handle.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		handle.setItemMeta(SimilarData.necronHandleMeta(handle.getItemMeta()));

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
		term.setItemMeta(SimilarData.termMeta(term.getItemMeta()));

		ItemStack core = new ItemStack(Material.CHISELED_QUARTZ_BLOCK);
		core.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		core.setItemMeta(SimilarData.coreMeta(core.getItemMeta()));

		ItemStack tessellated = new ItemStack(Material.ENDER_PEARL);
		tessellated.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		tessellated.setItemMeta(SimilarData.tessellatedPearlMeta(tessellated.getItemMeta()));

		ItemStack nullBlade = new ItemStack(Material.SHEARS);
		nullBlade.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		nullBlade.setItemMeta(SimilarData.nullBladeBeta(nullBlade.getItemMeta()));

		ItemStack braidedFeather = new ItemStack(Material.FEATHER);
		braidedFeather.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		braidedFeather.setItemMeta(SimilarData.braidedFeatherMeta(braidedFeather.getItemMeta()));

		ItemStack taraSilk = new ItemStack(Material.COBWEB);
		taraSilk.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		taraSilk.setItemMeta(SimilarData.taraSilkMeta(taraSilk.getItemMeta()));

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

	public static Recipe addAOTVRecipe(Plugin plugin) {
		ItemStack aotv = new ItemStack(Material.NETHERITE_SHOVEL);
		aotv.setItemMeta(SimilarData.AOTVMeta(aotv.getItemMeta()));

		NamespacedKey aotvKey = new NamespacedKey(plugin, "aotv");

		ShapelessRecipe aotvRecipe = new ShapelessRecipe(aotvKey, aotv);

		ItemStack tessellated = new ItemStack(Material.ENDER_PEARL);
		tessellated.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		tessellated.setItemMeta(SimilarData.tessellatedPearlMeta(tessellated.getItemMeta()));

		aotvRecipe.addIngredient(Material.NETHERITE_SHOVEL);
		aotvRecipe.addIngredient(new RecipeChoice.ExactChoice(tessellated));
		aotvRecipe.addIngredient(new RecipeChoice.ExactChoice(tessellated));
		aotvRecipe.addIngredient(new RecipeChoice.ExactChoice(tessellated));
		aotvRecipe.addIngredient(new RecipeChoice.ExactChoice(tessellated));
		aotvRecipe.addIngredient(new RecipeChoice.ExactChoice(tessellated));
		aotvRecipe.addIngredient(new RecipeChoice.ExactChoice(tessellated));
		aotvRecipe.addIngredient(new RecipeChoice.ExactChoice(tessellated));
		aotvRecipe.addIngredient(new RecipeChoice.ExactChoice(tessellated));

		return aotvRecipe;
	}
}