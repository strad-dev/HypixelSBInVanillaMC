package misc;

import items.armor.*;
import items.ingredients.mining.*;
import items.ingredients.misc.*;
import items.ingredients.witherLords.*;
import items.misc.*;
import items.weapons.Claymore;
import items.weapons.ManhuntHyperion;
import items.weapons.Scylla;
import items.weapons.Terminator;
import manhunt.ManhuntTier;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
		allRecipes.add(new NamespacedKey(plugin, "primalChestplate"));
		allRecipes.add(new NamespacedKey(plugin, "goldorLeggings"));
		allRecipes.add(new NamespacedKey(plugin, "necromancerPants"));
		allRecipes.add(new NamespacedKey(plugin, "maxorBoots"));
		allRecipes.add(new NamespacedKey(plugin, "wandOfRestoration"));
		allRecipes.add(new NamespacedKey(plugin, "wandOfAtonement"));
		allRecipes.add(new NamespacedKey(plugin, "holyIce"));
		allRecipes.add(new NamespacedKey(plugin, "gyro"));
		allRecipes.add(new NamespacedKey(plugin, "sharp7"));
		allRecipes.add(new NamespacedKey(plugin, "power7"));
		allRecipes.add(new NamespacedKey(plugin, "looting5"));
		allRecipes.add(new NamespacedKey(plugin, "featherFalling5"));
		allRecipes.add(new NamespacedKey(plugin, "efficiency6"));
		allRecipes.add(new NamespacedKey(plugin, "divanPickaxe"));
		return allRecipes;
	}

	public static Recipe addScyllaRecipe(Plugin plugin) {
		ItemStack scylla = Scylla.getItem();
		ItemStack shadowWarp = ShadowWarp.getItem();
		ItemStack implosion = Implosion.getItem();
		ItemStack witherShield = WitherShield.getItem();
		ItemStack handle = Handle.getItem();

		NamespacedKey scyllaKey = new NamespacedKey(plugin, "scylla");
		ShapedRecipe scyllaRecipe = new ShapedRecipe(scyllaKey, scylla);

		scyllaRecipe.shape("WIS", "NDN", "NHN");
		scyllaRecipe.setIngredient('W', RecipeChoice.exactChoice(shadowWarp));
		scyllaRecipe.setIngredient('I', RecipeChoice.exactChoice(implosion));
		scyllaRecipe.setIngredient('S', RecipeChoice.exactChoice(witherShield));
		scyllaRecipe.setIngredient('N', Material.NETHER_STAR);
		scyllaRecipe.setIngredient('D', Material.NETHERITE_SWORD);
		scyllaRecipe.setIngredient('H', RecipeChoice.exactChoice(handle));

		return scyllaRecipe;
	}

	public static Recipe addClaymoreRecipe(Plugin plugin) {
		ItemStack claymore = Claymore.getItem();
		ItemStack stormSecrets = StormSecrets.getItem();
		ItemStack gsRemnant = GiantSwordRemnant.getItem();
		ItemStack bigStone = ConcentratedStone.getItem();

		NamespacedKey claymoreKey = new NamespacedKey(plugin, "claymore");
		ShapedRecipe claymoreRecipe = new ShapedRecipe(claymoreKey, claymore);

		claymoreRecipe.shape("NSN", "CDC", "NHN");
		claymoreRecipe.setIngredient('S', RecipeChoice.exactChoice(stormSecrets));
		claymoreRecipe.setIngredient('N', Material.NETHER_STAR);
		claymoreRecipe.setIngredient('D', Material.STONE_SWORD);
		claymoreRecipe.setIngredient('H', RecipeChoice.exactChoice(gsRemnant));
		claymoreRecipe.setIngredient('C', RecipeChoice.exactChoice(bigStone));

		return claymoreRecipe;
	}

	public static Recipe addTermRecipe(Plugin plugin) {
		ItemStack term = Terminator.getItem();
		ItemStack core = Core.getItem();
		ItemStack tessellated = TessellatedPearl.getItem();
		ItemStack nullBlade = NullBlade.getItem();
		ItemStack braidedFeather = BraidedFeather.getItem();
		ItemStack taraSilk = TarantulaSilk.getItem();

		NamespacedKey termKey = new NamespacedKey(plugin, "term");
		ShapedRecipe termRecipe = new ShapedRecipe(termKey, term);

		termRecipe.shape("TNS", "CBF", "TNS");
		termRecipe.setIngredient('B', Material.BOW);
		termRecipe.setIngredient('T', RecipeChoice.exactChoice(tessellated));
		termRecipe.setIngredient('N', RecipeChoice.exactChoice(nullBlade));
		termRecipe.setIngredient('S', RecipeChoice.exactChoice(taraSilk));
		termRecipe.setIngredient('C', RecipeChoice.exactChoice(core));
		termRecipe.setIngredient('F', RecipeChoice.exactChoice(braidedFeather));

		return termRecipe;
	}

	public static Recipe addAOTVRecipe(Plugin plugin) {
		ItemStack aotv = AOTV.getItem();
		ItemStack tessellated = TessellatedPearl.getItem();

		NamespacedKey aotvKey = new NamespacedKey(plugin, "aotv");
		ShapedRecipe aotvRecipe = new ShapedRecipe(aotvKey, aotv);

		aotvRecipe.shape("TTT", "TST", "TTT");
		aotvRecipe.setIngredient('T', RecipeChoice.exactChoice(tessellated));
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
		wandRecipe.setIngredient('W', RecipeChoice.exactChoice(oldWand));
		wandRecipe.setIngredient('V', RecipeChoice.exactChoice(viscera));
		wandRecipe.setIngredient('D', Material.DIAMOND);

		return wandRecipe;
	}

	public static Recipe addDrillRecipe(Plugin plugin) {
		ItemStack pick = DivanPickaxe.getItem();
		ItemStack alloy = Alloy.getItem();
		ItemStack diamond = RefinedDiamond.getItem();
		ItemStack emerald = RefinedEmerald.getItem();
		ItemStack gold = RefinedGold.getItem();
		ItemStack iron = RefinedIron.getItem();
		ItemStack lapis = RefinedLapis.getItem();
		ItemStack netherite = RefinedNetherite.getItem();
		ItemStack redstone = RefinedRedstone.getItem();

		NamespacedKey drillKey = new NamespacedKey(plugin, "divanPickaxe");
		ShapedRecipe drillRecipe = new ShapedRecipe(drillKey, pick);

		drillRecipe.shape("DAE", "GPI", "LNR");
		drillRecipe.setIngredient('D', RecipeChoice.exactChoice(diamond));
		drillRecipe.setIngredient('A', RecipeChoice.exactChoice(alloy));
		drillRecipe.setIngredient('E', RecipeChoice.exactChoice(emerald));
		drillRecipe.setIngredient('G', RecipeChoice.exactChoice(gold));
		drillRecipe.setIngredient('P', Material.NETHERITE_PICKAXE);
		drillRecipe.setIngredient('I', RecipeChoice.exactChoice(iron));
		drillRecipe.setIngredient('L', RecipeChoice.exactChoice(lapis));
		drillRecipe.setIngredient('N', RecipeChoice.exactChoice(netherite));
		drillRecipe.setIngredient('R', RecipeChoice.exactChoice(redstone));

		return drillRecipe;
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

	public static Recipe addGyroRecipe(Plugin plugin) {
		ItemStack gyro = GyrokineticWand.getItem();
		ItemStack nullOvoid = NullOvoid.getItem();

		NamespacedKey gyroKey = new NamespacedKey(plugin, "gyro");
		ShapedRecipe gyroRecipe = new ShapedRecipe(gyroKey, gyro);

		gyroRecipe.shape("NON", "NCN", "NGN");
		gyroRecipe.setIngredient('N',  RecipeChoice.exactChoice(nullOvoid));
		gyroRecipe.setIngredient('O', Material.OBSIDIAN);
		gyroRecipe.setIngredient('C', Material.PURPLE_CONCRETE);
		gyroRecipe.setIngredient('G', Material.PURPLE_STAINED_GLASS);
		return gyroRecipe;
	}

	public static Recipe addWardenHelmetRecipe(Plugin plugin) {
		ItemStack wardenHelmet = WardenHelmet.getItem();
		ItemStack wardenHeart = WardenHeart.getItem();
		ItemStack viscera = Viscera.getItem();

		NamespacedKey wardenKey = new NamespacedKey(plugin, "wardenHelmet");
		ShapedRecipe wardenRecipe = new ShapedRecipe(wardenKey, wardenHelmet);

		wardenRecipe.shape("VVV", "VHV", "VWV");
		wardenRecipe.setIngredient('V', RecipeChoice.exactChoice(viscera));
		wardenRecipe.setIngredient('H', Material.NETHERITE_HELMET);
		wardenRecipe.setIngredient('W', RecipeChoice.exactChoice(wardenHeart));

		return wardenRecipe;
	}

	public static Recipe addNecronElytraRecipe(Plugin plugin) {
		ItemStack necronElytra = NecronElytra.getItem();
		ItemStack necronSecrets = NecronSecrets.getItem();

		NamespacedKey elytraKey = new NamespacedKey(plugin, "necronElytra");
		ShapedRecipe elytraRecipe = new ShapedRecipe(elytraKey, necronElytra);

		elytraRecipe.shape("NSN", "NCN", "NEN");
		elytraRecipe.setIngredient('S', RecipeChoice.exactChoice(necronSecrets));
		elytraRecipe.setIngredient('C', Material.NETHERITE_CHESTPLATE);
		elytraRecipe.setIngredient('N', Material.NETHER_STAR);
		elytraRecipe.setIngredient('E', Material.ELYTRA);

		return elytraRecipe;
	}

	public static Recipe addPrimalChesplateRecipe(Plugin plugin) {
		ItemStack primalChestplate = PrimalDragonChestplate.getItem();
		ItemStack dragonEgg = AncientDragonEgg.getItem();
		ItemStack core = Core.getItem();
		ItemStack ovoid = NullOvoid.getItem();

		NamespacedKey primalKey = new NamespacedKey(plugin, "primalChestplate");
		ShapedRecipe primalRecipe = new ShapedRecipe(primalKey, primalChestplate);

		primalRecipe.shape("OEO", "OCO", "OJO");
		primalRecipe.setIngredient('O', RecipeChoice.exactChoice(ovoid));
		primalRecipe.setIngredient('J', RecipeChoice.exactChoice(core));
		primalRecipe.setIngredient('E', RecipeChoice.exactChoice(dragonEgg));
		primalRecipe.setIngredient('C', Material.NETHERITE_CHESTPLATE);

		return primalRecipe;
	}

	public static Recipe addGoldorLeggingsRecipe(Plugin plugin) {
		ItemStack goldorLeggings = GoldorLeggings.getItem();
		ItemStack goldorSecrets = GoldorSecrets.getItem();

		NamespacedKey goldorKey = new NamespacedKey(plugin, "goldorLeggings");
		ShapedRecipe goldorRecipe = new ShapedRecipe(goldorKey, goldorLeggings);

		goldorRecipe.shape("NNN", "NLN", "NSN");
		goldorRecipe.setIngredient('N', Material.NETHER_STAR);
		goldorRecipe.setIngredient('L', Material.NETHERITE_LEGGINGS);
		goldorRecipe.setIngredient('S', RecipeChoice.exactChoice(goldorSecrets));

		return goldorRecipe;
	}

	public static Recipe addNecromancerLegginsRecipe(Plugin plugin) {
		ItemStack necromancerLeggings = NecromancerLordLeggings.getItem();
		ItemStack brooch = NecromancerBrooch.getItem();
		ItemStack viscera = Viscera.getItem();

		NamespacedKey necromancerKey = new NamespacedKey(plugin, "necromancerPants");
		ShapedRecipe necromancerRecipe = new ShapedRecipe(necromancerKey, necromancerLeggings);

		necromancerRecipe.shape("VVV", "SNS", "SBS");
		necromancerRecipe.setIngredient('S', Material.NETHER_STAR);
		necromancerRecipe.setIngredient('N', Material.NETHERITE_LEGGINGS);
		necromancerRecipe.setIngredient('V', RecipeChoice.exactChoice(viscera));
		necromancerRecipe.setIngredient('B', RecipeChoice.exactChoice(brooch));

		return necromancerRecipe;
	}

	public static Recipe addMaxorBootsRecipe(Plugin plugin) {
		ItemStack maxorBoots = MaxorBoots.getItem();
		ItemStack maxorSecrets = MaxorSecrets.getItem();

		NamespacedKey maxorKey = new NamespacedKey(plugin, "maxorBoots");
		ShapedRecipe maxorRecipe = new ShapedRecipe(maxorKey, maxorBoots);

		maxorRecipe.shape("NNN", "NBN", "NSN");
		maxorRecipe.setIngredient('N', Material.NETHER_STAR);
		maxorRecipe.setIngredient('B', Material.NETHERITE_BOOTS);
		maxorRecipe.setIngredient('S', RecipeChoice.exactChoice(maxorSecrets));

		return maxorRecipe;
	}

	public static Recipe addSharp7Recipe(Plugin plugin) {
		ItemStack sharp7 = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) sharp7.getItemMeta();
		meta.addStoredEnchant(Enchantment.SHARPNESS, 7, true);
		sharp7.setItemMeta(meta);

		ItemStack sharp6 = new ItemStack(Material.ENCHANTED_BOOK);
		meta = (EnchantmentStorageMeta) sharp6.getItemMeta();
		meta.addStoredEnchant(Enchantment.SHARPNESS, 6, true);
		sharp6.setItemMeta(meta);

		ItemStack upgrader = EnchantmentUpgrader.getItem();

		NamespacedKey sharp7Key = new NamespacedKey(plugin, "sharp7");
		ShapelessRecipe sharp7Recipe = new ShapelessRecipe(sharp7Key, sharp7);

		sharp7Recipe.addIngredient(RecipeChoice.exactChoice(sharp6));
		sharp7Recipe.addIngredient(RecipeChoice.exactChoice(sharp6));
		sharp7Recipe.addIngredient(RecipeChoice.exactChoice(upgrader));

		return sharp7Recipe;
	}

	public static Recipe addPower7Recipe(Plugin plugin) {
		ItemStack power7 = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) power7.getItemMeta();
		meta.addStoredEnchant(Enchantment.POWER, 7, true);
		power7.setItemMeta(meta);

		ItemStack power6 = new ItemStack(Material.ENCHANTED_BOOK);
		meta = (EnchantmentStorageMeta) power6.getItemMeta();
		meta.addStoredEnchant(Enchantment.POWER, 6, true);
		power6.setItemMeta(meta);

		ItemStack upgrader = EnchantmentUpgrader.getItem();

		NamespacedKey power7Key = new NamespacedKey(plugin, "power7");
		ShapelessRecipe power7Recipe = new ShapelessRecipe(power7Key, power7);

		power7Recipe.addIngredient(RecipeChoice.exactChoice(power6));
		power7Recipe.addIngredient(RecipeChoice.exactChoice(power6));
		power7Recipe.addIngredient(RecipeChoice.exactChoice(upgrader));

		return power7Recipe;
	}

	public static Recipe addLooting5Recipe(Plugin plugin) {
		ItemStack looting5 = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) looting5.getItemMeta();
		meta.addStoredEnchant(Enchantment.LOOTING, 5, true);
		looting5.setItemMeta(meta);

		ItemStack looting4 = new ItemStack(Material.ENCHANTED_BOOK);
		meta = (EnchantmentStorageMeta) looting4.getItemMeta();
		meta.addStoredEnchant(Enchantment.LOOTING, 4, true);
		looting4.setItemMeta(meta);

		ItemStack upgrader = EnchantmentUpgrader.getItem();

		NamespacedKey looting5Key = new NamespacedKey(plugin, "looting5");
		ShapelessRecipe looting5Recipe = new ShapelessRecipe(looting5Key, looting5);

		looting5Recipe.addIngredient(RecipeChoice.exactChoice(looting4));
		looting5Recipe.addIngredient(RecipeChoice.exactChoice(looting4));
		looting5Recipe.addIngredient(RecipeChoice.exactChoice(upgrader));

		return looting5Recipe;
	}

	public static Recipe addEfficiency6Recipe(Plugin plugin) {
		ItemStack efficiency6 = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) efficiency6.getItemMeta();
		meta.addStoredEnchant(Enchantment.EFFICIENCY, 6, true);
		efficiency6.setItemMeta(meta);

		ItemStack stones = ConcentratedStone.getItem();

		NamespacedKey efficiency6Key = new NamespacedKey(plugin, "efficiency6");
		ShapelessRecipe efficiency6Recipe = new ShapelessRecipe(efficiency6Key, efficiency6);

		efficiency6Recipe.addIngredient(RecipeChoice.exactChoice(stones));
		efficiency6Recipe.addIngredient(RecipeChoice.exactChoice(stones));
		efficiency6Recipe.addIngredient(RecipeChoice.exactChoice(stones));
		efficiency6Recipe.addIngredient(RecipeChoice.exactChoice(stones));
		efficiency6Recipe.addIngredient(RecipeChoice.exactChoice(stones));
		efficiency6Recipe.addIngredient(RecipeChoice.exactChoice(stones));
		efficiency6Recipe.addIngredient(RecipeChoice.exactChoice(stones));
		efficiency6Recipe.addIngredient(RecipeChoice.exactChoice(stones));
		efficiency6Recipe.addIngredient(RecipeChoice.exactChoice(stones));

		return efficiency6Recipe;
	}

	public static Recipe addFeatherFalling5Recipe(Plugin plugin) {
		ItemStack featherFalling5 = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) featherFalling5.getItemMeta();
		meta.addStoredEnchant(Enchantment.FEATHER_FALLING, 5, true);
		featherFalling5.setItemMeta(meta);

		ItemStack featherFalling4 = new ItemStack(Material.ENCHANTED_BOOK);
		meta = (EnchantmentStorageMeta) featherFalling4.getItemMeta();
		meta.addStoredEnchant(Enchantment.FEATHER_FALLING, 4, true);
		featherFalling4.setItemMeta(meta);

		ItemStack feather = BraidedFeather.getItem();

		NamespacedKey featherFalling5Key = new NamespacedKey(plugin, "featherFalling5");
		ShapelessRecipe featherFalling5Recipe = new ShapelessRecipe(featherFalling5Key, featherFalling5);

		featherFalling5Recipe.addIngredient(RecipeChoice.exactChoice(featherFalling4));
		featherFalling5Recipe.addIngredient(RecipeChoice.exactChoice(featherFalling4));
		featherFalling5Recipe.addIngredient(RecipeChoice.exactChoice(feather));

		return featherFalling5Recipe;
	}


	public static Recipe addGodAppleRecipe(Plugin plugin) {
		NamespacedKey godAppleKey = new NamespacedKey(plugin, "enchanted_golden_apple");
		ShapedRecipe godAppleRecipe = new ShapedRecipe(godAppleKey, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));

		godAppleRecipe.shape("GGG", "GAG", "GGG");
		godAppleRecipe.setIngredient('G', Material.GOLD_BLOCK);
		godAppleRecipe.setIngredient('A', Material.APPLE);

		return godAppleRecipe;
	}

	/**
	 * One recipe per Manhunt Hyperion rung: the rung below plus that many swords of the new material, <b>shapeless</b>.
	 * Registered only when Manhunt is on.
	 *
	 * <p>The Hyperion ingredient can only match on MATERIAL (it carries enchants and a live damage figure, so no
	 * {@code ExactChoice} matches), so a bare stick plus eight wooden swords matches too.
	 * {@code ManhuntListener.onPrepareCraft} re-checks it, throws away a fake, and puts the right enchants and lore on
	 * the result; that is why {@code KeepEnchantsOnCraft} must leave these alone.
	 */
	public static List<Recipe> addManhuntRecipes(Plugin plugin) {
		List<Recipe> out = new ArrayList<>();
		for(ManhuntTier tier : ManhuntTier.values()) {
			if(tier == ManhuntTier.BASE) continue;
			ManhuntTier from = ManhuntTier.values()[tier.ordinal() - 1];

			ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, manhuntKey(tier)), ManhuntHyperion.getItem(tier));
			recipe.addIngredient(from.material());
			recipe.addIngredient(tier.swordsToUpgrade(), tier.material());
			out.add(recipe);
		}

		ShapelessRecipe skip = new ShapelessRecipe(
				new NamespacedKey(plugin, manhuntKey(ManhuntTier.IRON) + "_from_" + ManhuntTier.STONE.name().toLowerCase(java.util.Locale.ROOT)),
				ManhuntHyperion.getItem(ManhuntTier.IRON));
		skip.addIngredient(ManhuntTier.STONE.material());
		skip.addIngredient(ManhuntTier.STONE_TO_IRON_SWORDS, ManhuntTier.IRON.material());
		out.add(skip);
		return out;
	}

	/** Whether {@code recipe} is a Manhunt upgrade, whose result nothing else may touch. */
	public static boolean isManhuntRecipe(Recipe recipe) {
		return recipe instanceof Keyed keyed && keyed.getKey().getKey().startsWith("manhunt_");
	}

	private static String manhuntKey(ManhuntTier tier) {
		return "manhunt_" + tier.name().toLowerCase(java.util.Locale.ROOT);
	}

	/** Keys of every Manhunt recipe, so they can be force-discovered on join like the rest. */
	public static Set<NamespacedKey> manhuntRecipeKeys(Plugin plugin) {
		Set<NamespacedKey> keys = new HashSet<>();
		for(Recipe recipe : addManhuntRecipes(plugin)) {
			keys.add(((Keyed) recipe).getKey());
		}
		return keys;
	}
}