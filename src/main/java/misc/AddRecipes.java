package misc;

import items.armor.*;
import items.ingredients.mining.*;
import items.ingredients.misc.*;
import items.ingredients.witherLords.*;
import items.misc.*;
import items.weapons.Claymore;
import items.weapons.Scylla;
import items.weapons.Terminator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
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
		ItemStack scylla = Scylla.getItem(Enchantment.SHARPNESS, 0);
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
		ItemStack claymore = Claymore.getItem(Enchantment.SHARPNESS, 0);
		ItemStack stormSecrets = StormSecrets.getItem();
		ItemStack gsRemnant = GiantSwordRemnant.getItem();
		ItemStack bigStone = ConcentratedStone.getItem();

		NamespacedKey claymoreKey = new NamespacedKey(plugin, "claymore");
		ShapedRecipe claymoreRecipe = new ShapedRecipe(claymoreKey, claymore);

		claymoreRecipe.shape("NSN", "CDC", "NHN");
		claymoreRecipe.setIngredient('S', new RecipeChoice.ExactChoice(stormSecrets));
		claymoreRecipe.setIngredient('N', Material.NETHER_STAR);
		claymoreRecipe.setIngredient('D', Material.STONE_SWORD);
		claymoreRecipe.setIngredient('H', new RecipeChoice.ExactChoice(gsRemnant));
		claymoreRecipe.setIngredient('C', new RecipeChoice.ExactChoice(bigStone));

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
		drillRecipe.setIngredient('D', new RecipeChoice.ExactChoice(diamond));
		drillRecipe.setIngredient('A', new RecipeChoice.ExactChoice(alloy));
		drillRecipe.setIngredient('E', new RecipeChoice.ExactChoice(emerald));
		drillRecipe.setIngredient('G', new RecipeChoice.ExactChoice(gold));
		drillRecipe.setIngredient('P', Material.NETHERITE_PICKAXE);
		drillRecipe.setIngredient('I', new RecipeChoice.ExactChoice(iron));
		drillRecipe.setIngredient('L', new RecipeChoice.ExactChoice(lapis));
		drillRecipe.setIngredient('N', new RecipeChoice.ExactChoice(netherite));
		drillRecipe.setIngredient('R', new RecipeChoice.ExactChoice(redstone));

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
		gyroRecipe.setIngredient('N',  new RecipeChoice.ExactChoice(nullOvoid));
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

	public static Recipe addPrimalChesplateRecipe(Plugin plugin) {
		ItemStack primalChestplate = PrimalDragonChestplate.getItem();
		ItemStack dragonEgg = AncientDragonEgg.getItem();
		ItemStack core = Core.getItem();
		ItemStack ovoid = NullOvoid.getItem();

		NamespacedKey primalKey = new NamespacedKey(plugin, "primalChestplate");
		ShapedRecipe primalRecipe = new ShapedRecipe(primalKey, primalChestplate);

		primalRecipe.shape("OEO", "OCO", "OJO");
		primalRecipe.setIngredient('O', new RecipeChoice.ExactChoice(ovoid));
		primalRecipe.setIngredient('J', new RecipeChoice.ExactChoice(core));
		primalRecipe.setIngredient('E', new RecipeChoice.ExactChoice(dragonEgg));
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
		goldorRecipe.setIngredient('S', new RecipeChoice.ExactChoice(goldorSecrets));

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
		necromancerRecipe.setIngredient('V', new RecipeChoice.ExactChoice(viscera));
		necromancerRecipe.setIngredient('B', new RecipeChoice.ExactChoice(brooch));

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
		maxorRecipe.setIngredient('S', new RecipeChoice.ExactChoice(maxorSecrets));

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

		sharp7Recipe.addIngredient(new RecipeChoice.ExactChoice(sharp6));
		sharp7Recipe.addIngredient(new RecipeChoice.ExactChoice(sharp6));
		sharp7Recipe.addIngredient(new RecipeChoice.ExactChoice(upgrader));

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

		power7Recipe.addIngredient(new RecipeChoice.ExactChoice(power6));
		power7Recipe.addIngredient(new RecipeChoice.ExactChoice(power6));
		power7Recipe.addIngredient(new RecipeChoice.ExactChoice(upgrader));

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

		looting5Recipe.addIngredient(new RecipeChoice.ExactChoice(looting4));
		looting5Recipe.addIngredient(new RecipeChoice.ExactChoice(looting4));
		looting5Recipe.addIngredient(new RecipeChoice.ExactChoice(upgrader));

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

		efficiency6Recipe.addIngredient(new RecipeChoice.ExactChoice(stones));
		efficiency6Recipe.addIngredient(new RecipeChoice.ExactChoice(stones));
		efficiency6Recipe.addIngredient(new RecipeChoice.ExactChoice(stones));
		efficiency6Recipe.addIngredient(new RecipeChoice.ExactChoice(stones));
		efficiency6Recipe.addIngredient(new RecipeChoice.ExactChoice(stones));
		efficiency6Recipe.addIngredient(new RecipeChoice.ExactChoice(stones));
		efficiency6Recipe.addIngredient(new RecipeChoice.ExactChoice(stones));
		efficiency6Recipe.addIngredient(new RecipeChoice.ExactChoice(stones));
		efficiency6Recipe.addIngredient(new RecipeChoice.ExactChoice(stones));

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

		featherFalling5Recipe.addIngredient(new RecipeChoice.ExactChoice(featherFalling4));
		featherFalling5Recipe.addIngredient(new RecipeChoice.ExactChoice(featherFalling5));
		featherFalling5Recipe.addIngredient(new RecipeChoice.ExactChoice(feather));

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
}