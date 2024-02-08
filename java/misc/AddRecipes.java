package misc;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class AddRecipes {
	public static Set<NamespacedKey> returnRecipes(Plugin plugin) {
		Set<NamespacedKey> allRecipes = new HashSet<>();
		allRecipes.add(new NamespacedKey(plugin, "scylla"));
		allRecipes.add(new NamespacedKey(plugin, "aotv"));
		allRecipes.add(new NamespacedKey(plugin, "term"));
		allRecipes.add(new NamespacedKey(plugin, "enchanted_golden_apple"));
		allRecipes.add(new NamespacedKey(plugin, "wardenHelmet"));
		allRecipes.add(new NamespacedKey(plugin, "necronElytra"));
		allRecipes.add(new NamespacedKey(plugin, "goldorLeggings"));
		allRecipes.add(new NamespacedKey(plugin, "maxorBoots"));
		allRecipes.add(new NamespacedKey(plugin, "wandOfRestoration"));
		allRecipes.add(new NamespacedKey(plugin, "wandOfAtonement"));
		allRecipes.add(new NamespacedKey(plugin, "holyIce"));
		return allRecipes;
	}

	public static Recipe addScyllaRecipe(Plugin plugin) {
		ItemStack scylla = SimilarData.scylla();
		ItemStack shadowWarp = SimilarData.shadowWarp();
		ItemStack implosion = SimilarData.implosion();
		ItemStack witherShield = SimilarData.witherShield();
		ItemStack handle = SimilarData.handle();

		NamespacedKey scyllaKey = new NamespacedKey(plugin, "scylla");
		ShapedRecipe scyllaRecipe = new ShapedRecipe(scyllaKey, scylla);

		scyllaRecipe.shape("WIS", "NDN", "NHN");
		scyllaRecipe.setIngredient('W', new RecipeChoice.ExactChoice(shadowWarp));
		scyllaRecipe.setIngredient('I', new RecipeChoice.ExactChoice(implosion));
		scyllaRecipe.setIngredient('S', new RecipeChoice.ExactChoice(witherShield));
		scyllaRecipe.setIngredient('N', Material.NETHER_STAR);
		scyllaRecipe.setIngredient('D', Material.NETHERITE_SWORD);
		scyllaRecipe.setIngredient('H', new RecipeChoice.ExactChoice(handle));
		
		return scyllaRecipe;
	}

	public static Recipe addTermRecipe(Plugin plugin) {
		ItemStack term = SimilarData.term();
		ItemStack core = SimilarData.core();
		ItemStack tessellated = SimilarData.tessellatedPearl();
		ItemStack nullBlade = SimilarData.nullBlade();
		ItemStack braidedFeather = SimilarData.braidedFeather();
		ItemStack taraSilk = SimilarData.taraSilk();

		NamespacedKey termKey = new NamespacedKey(plugin, "term");
		ShapedRecipe termRecipe = new ShapedRecipe(termKey, term);

		termRecipe.shape("TNS", "NCF", "TNS");
		termRecipe.setIngredient('T', new RecipeChoice.ExactChoice(tessellated));
		termRecipe.setIngredient('N', new RecipeChoice.ExactChoice(nullBlade));
		termRecipe.setIngredient('S', new RecipeChoice.ExactChoice(taraSilk));
		termRecipe.setIngredient('C', new RecipeChoice.ExactChoice(core));
		termRecipe.setIngredient('F', new RecipeChoice.ExactChoice(braidedFeather));

		return termRecipe;
	}

	public static Recipe addAOTVRecipe(Plugin plugin) {
		ItemStack aotv = SimilarData.AOTV();
		ItemStack tessellated = SimilarData.tessellatedPearl();

		NamespacedKey aotvKey = new NamespacedKey(plugin, "aotv");
		ShapedRecipe aotvRecipe = new ShapedRecipe(aotvKey, aotv);

		aotvRecipe.shape("TTT", "TST", "TTT");
		aotvRecipe.setIngredient('T', new RecipeChoice.ExactChoice(tessellated));
		aotvRecipe.setIngredient('S', Material.NETHERITE_SHOVEL);

		return aotvRecipe;
	}

	public static Recipe addWandOfRestorationRecipe(Plugin plugin) {
		ItemStack wand = SimilarData.wandOfRestoration();

		NamespacedKey restorationKey = new NamespacedKey(plugin, "wandOfRestoration");
		ShapedRecipe wandRecipe = new ShapedRecipe(restorationKey, wand);

		wandRecipe.shape("III", "IFI", "ISI");
		wandRecipe.setIngredient('I', Material.IRON_INGOT);
		wandRecipe.setIngredient('F', Material.ROTTEN_FLESH);
		wandRecipe.setIngredient('S', Material.STICK);

		return wandRecipe;
	}

	public static Recipe addWandOfAtonementRecipe(Plugin plugin) {
		ItemStack oldWand = SimilarData.wandOfRestoration();
		ItemStack wand = SimilarData.wandOfAtonement();
		ItemStack viscera = SimilarData.viscera();

		NamespacedKey atonementKey = new NamespacedKey(plugin, "wandOfAtonement");
		ShapedRecipe wandRecipe = new ShapedRecipe(atonementKey, wand);

		wandRecipe.shape("DDD", "DWD", "DVD");
		wandRecipe.setIngredient('W', new RecipeChoice.ExactChoice(oldWand));
		wandRecipe.setIngredient('V', new RecipeChoice.ExactChoice(viscera));
		wandRecipe.setIngredient('D', Material.DIAMOND);

		return wandRecipe;
	}

	public static Recipe addHolyIceRecipe(Plugin plugin) {
		ItemStack ice = SimilarData.holyIce();

		NamespacedKey iceKey = new NamespacedKey(plugin, "holyIce");
		ShapedRecipe iceRecipe = new ShapedRecipe(iceKey, ice);

		iceRecipe.shape("III", "IDI", "III");
		iceRecipe.setIngredient('I', Material.PACKED_ICE);
		iceRecipe.setIngredient('D', Material.DIAMOND);

		return iceRecipe;
	}

	public static Recipe addWardenHelmetRecipe(Plugin plugin) {
		ItemStack wardenHelmet = SimilarData.wardenHelmet();
		ItemStack wardenHeart = SimilarData.wardenHeart();
		ItemStack viscera = SimilarData.viscera();

		NamespacedKey wardenKey = new NamespacedKey(plugin, "wardenHelmet");
		ShapedRecipe wardenRecipe = new ShapedRecipe(wardenKey, wardenHelmet);

		wardenRecipe.shape("VVV", "VHV", "VWV");
		wardenRecipe.setIngredient('V', new RecipeChoice.ExactChoice(viscera));
		wardenRecipe.setIngredient('H', Material.NETHERITE_HELMET);
		wardenRecipe.setIngredient('W', new RecipeChoice.ExactChoice(wardenHeart));

		return wardenRecipe;
	}

	public static Recipe addNecronElytraRecipe(Plugin plugin) {
		ItemStack necronElytra = SimilarData.necronElytra();
		ItemStack necronSecrets = SimilarData.necronSecrets();

		NamespacedKey elytraKey = new NamespacedKey(plugin, "necronElytra");
		ShapedRecipe elytraRecipe = new ShapedRecipe(elytraKey, necronElytra);

		elytraRecipe.shape("NSN", "NCN", "NEN");
		elytraRecipe.setIngredient('C', new RecipeChoice.ExactChoice(necronSecrets));
		elytraRecipe.setIngredient('S', Material.NETHERITE_CHESTPLATE);
		elytraRecipe.setIngredient('N', Material.NETHER_STAR);
		elytraRecipe.setIngredient('E', Material.ELYTRA);

		return elytraRecipe;
	}

	public static Recipe addGoldorLeggingsRecipe(Plugin plugin) {
		ItemStack goldorLeggings = SimilarData.goldorLeggings();
		ItemStack goldorSecrets = SimilarData.goldorSecrets();

		NamespacedKey goldorKey = new NamespacedKey(plugin, "goldorLeggings");
		ShapedRecipe goldorRecipe = new ShapedRecipe(goldorKey, goldorLeggings);

		goldorRecipe.shape("NNN", "NLN", "NSN");
		goldorRecipe.setIngredient('N', Material.NETHER_STAR);
		goldorRecipe.setIngredient('L', Material.NETHERITE_LEGGINGS);
		goldorRecipe.setIngredient('S', new RecipeChoice.ExactChoice(goldorSecrets));

		return goldorRecipe;
	}

	public static Recipe addMaxorBootsRecipe(Plugin plugin) {
		ItemStack maxorBoots = SimilarData.maxorBoots();
		ItemStack maxorSecrets = SimilarData.maxorSecrets();

		NamespacedKey maxorKey = new NamespacedKey(plugin, "maxorBoots");
		ShapedRecipe maxorRecipe = new ShapedRecipe(maxorKey, maxorBoots);

		maxorRecipe.shape("NNN", "NBN", "NSN");
		maxorRecipe.setIngredient('N', Material.NETHER_STAR);
		maxorRecipe.setIngredient('B', Material.NETHERITE_BOOTS);
		maxorRecipe.setIngredient('S', new RecipeChoice.ExactChoice(maxorSecrets));

		return maxorRecipe;
	}

	public static Recipe addGodAppleRecipe(Plugin plugin) {
		NamespacedKey godAppleKey = new NamespacedKey(plugin, "enchanted_golden_apple");
		ShapedRecipe godAppleRecipe = new ShapedRecipe(godAppleKey, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));

		godAppleRecipe.shape("GGG", "GAG", "GGG");
		godAppleRecipe.setIngredient('G', Material.GOLD_BLOCK);
		godAppleRecipe.setIngredient('A', Material.APPLE);

		return godAppleRecipe;
	}
}