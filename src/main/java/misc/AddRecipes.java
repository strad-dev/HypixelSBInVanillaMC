package misc;

import ingredients.*;
import items.Terminator;
import items.*;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class AddRecipes {
	public static Set<NamespacedKey> returnRecipes(Plugin plugin) {
		Set<NamespacedKey> allRecipes = new HashSet<>();
		allRecipes.add(new NamespacedKey(plugin, "scylla"));
		allRecipes.add(new NamespacedKey(plugin, "claymore"));
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
		ItemStack scylla = Scylla.getItem(0);
		ItemStack shadowWarp = ShadowWarp.getItem();
		ItemStack implosion = Implosion.getItem();
		ItemStack witherShield = WitherShield.getItem();
		ItemStack handle = Handle.getItem();

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

	public static Recipe addClaymoreRecipe(Plugin plugin) {
		ItemStack claymore = Claymore.getItem(0);
		ItemStack stormSecrets = StormSecrets.getItem();
		ItemStack gsRemnant = GiantSwordRemnant.getItem();

		NamespacedKey claymoreKey = new NamespacedKey(plugin, "claymore");
		ShapedRecipe claymoreRecipe = new ShapedRecipe(claymoreKey, claymore);

		claymoreRecipe.shape("NSN", "NDN", "NHN");
		claymoreRecipe.setIngredient('S', new RecipeChoice.ExactChoice(stormSecrets));
		claymoreRecipe.setIngredient('N', Material.NETHER_STAR);
		claymoreRecipe.setIngredient('D', Material.NETHERITE_SWORD);
		claymoreRecipe.setIngredient('H', new RecipeChoice.ExactChoice(gsRemnant));

		return claymoreRecipe;
	}

	public static Recipe addTermRecipe(Plugin plugin) {
		ItemStack term = Terminator.getItem(0);
		ItemStack core = Core.getItem();
		ItemStack tessellated = TessellatedPearl.getItem();
		ItemStack nullBlade = NullBlade.getItem();
		ItemStack braidedFeather = BraidedFeather.getItem();
		ItemStack taraSilk = TarantulaSilk.getItem();

		NamespacedKey termKey = new NamespacedKey(plugin, "term");
		ShapedRecipe termRecipe = new ShapedRecipe(termKey, term);

		termRecipe.shape("TNS", "CBF", "TNS");
		termRecipe.setIngredient('B', Material.BOW);
		termRecipe.setIngredient('T', new RecipeChoice.ExactChoice(tessellated));
		termRecipe.setIngredient('N', new RecipeChoice.ExactChoice(nullBlade));
		termRecipe.setIngredient('S', new RecipeChoice.ExactChoice(taraSilk));
		termRecipe.setIngredient('C', new RecipeChoice.ExactChoice(core));
		termRecipe.setIngredient('F', new RecipeChoice.ExactChoice(braidedFeather));

		return termRecipe;
	}

	public static Recipe addAOTVRecipe(Plugin plugin) {
		ItemStack aotv = AOTV.getItem();
		ItemStack tessellated = TessellatedPearl.getItem();

		NamespacedKey aotvKey = new NamespacedKey(plugin, "aotv");
		ShapedRecipe aotvRecipe = new ShapedRecipe(aotvKey, aotv);

		aotvRecipe.shape("TTT", "TST", "TTT");
		aotvRecipe.setIngredient('T', new RecipeChoice.ExactChoice(tessellated));
		aotvRecipe.setIngredient('S', Material.NETHERITE_SHOVEL);

		return aotvRecipe;
	}

	public static Recipe addWandOfRestorationRecipe(Plugin plugin) {
		ItemStack wand = WandOfRestoration.getItem();

		NamespacedKey restorationKey = new NamespacedKey(plugin, "wandOfRestoration");
		ShapedRecipe wandRecipe = new ShapedRecipe(restorationKey, wand);

		wandRecipe.shape("III", "IFI", "ISI");
		wandRecipe.setIngredient('I', Material.IRON_INGOT);
		wandRecipe.setIngredient('F', Material.ROTTEN_FLESH);
		wandRecipe.setIngredient('S', Material.STICK);

		return wandRecipe;
	}

	public static Recipe addWandOfAtonementRecipe(Plugin plugin) {
		ItemStack oldWand = WandOfRestoration.getItem();
		ItemStack wand = WandOfAtonement.getItem();
		ItemStack viscera = Viscera.getItem();

		NamespacedKey atonementKey = new NamespacedKey(plugin, "wandOfAtonement");
		ShapedRecipe wandRecipe = new ShapedRecipe(atonementKey, wand);

		wandRecipe.shape("DDD", "DWD", "DVD");
		wandRecipe.setIngredient('W', new RecipeChoice.ExactChoice(oldWand));
		wandRecipe.setIngredient('V', new RecipeChoice.ExactChoice(viscera));
		wandRecipe.setIngredient('D', Material.DIAMOND);

		return wandRecipe;
	}

	public static Recipe addHolyIceRecipe(Plugin plugin) {
		ItemStack ice = HolyIce.getItem();

		NamespacedKey iceKey = new NamespacedKey(plugin, "holyIce");
		ShapedRecipe iceRecipe = new ShapedRecipe(iceKey, ice);

		iceRecipe.shape("III", "IDI", "III");
		iceRecipe.setIngredient('I', Material.PACKED_ICE);
		iceRecipe.setIngredient('D', Material.DIAMOND);

		return iceRecipe;
	}

	public static Recipe addWardenHelmetRecipe(Plugin plugin) {
		ItemStack wardenHelmet = WardenHelmet.getItem();
		ItemStack wardenHeart = WardenHeart.getItem();
		ItemStack viscera = Viscera.getItem();

		NamespacedKey wardenKey = new NamespacedKey(plugin, "wardenHelmet");
		ShapedRecipe wardenRecipe = new ShapedRecipe(wardenKey, wardenHelmet);

		wardenRecipe.shape("VVV", "VHV", "VWV");
		wardenRecipe.setIngredient('V', new RecipeChoice.ExactChoice(viscera));
		wardenRecipe.setIngredient('H', Material.NETHERITE_HELMET);
		wardenRecipe.setIngredient('W', new RecipeChoice.ExactChoice(wardenHeart));

		return wardenRecipe;
	}

	public static Recipe addNecronElytraRecipe(Plugin plugin) {
		ItemStack necronElytra = NecronElytra.getItem();
		ItemStack necronSecrets = NecronSecrets.getItem();

		NamespacedKey elytraKey = new NamespacedKey(plugin, "necronElytra");
		ShapedRecipe elytraRecipe = new ShapedRecipe(elytraKey, necronElytra);

		elytraRecipe.shape("NSN", "NCN", "NEN");
		elytraRecipe.setIngredient('S', new RecipeChoice.ExactChoice(necronSecrets));
		elytraRecipe.setIngredient('C', Material.NETHERITE_CHESTPLATE);
		elytraRecipe.setIngredient('N', Material.NETHER_STAR);
		elytraRecipe.setIngredient('E', Material.ELYTRA);

		return elytraRecipe;
	}

	public static Recipe addGoldorLeggingsRecipe(Plugin plugin) {
		ItemStack goldorLeggings = GoldorLeggings.getItem();
		ItemStack goldorSecrets = GoldorSecrets.getItem();

		NamespacedKey goldorKey = new NamespacedKey(plugin, "goldorLeggings");
		ShapedRecipe goldorRecipe = new ShapedRecipe(goldorKey, goldorLeggings);

		goldorRecipe.shape("NNN", "NLN", "NSN");
		goldorRecipe.setIngredient('N', Material.NETHER_STAR);
		goldorRecipe.setIngredient('L', Material.NETHERITE_LEGGINGS);
		goldorRecipe.setIngredient('S', new RecipeChoice.ExactChoice(goldorSecrets));

		return goldorRecipe;
	}

	public static Recipe addMaxorBootsRecipe(Plugin plugin) {
		ItemStack maxorBoots = MaxorBoots.getItem();
		ItemStack maxorSecrets = MaxorSecrets.getItem();

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