/*
 * MIT License
 *
 * Copyright ©2025 Stradivarius Violin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package misc;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.events.PlayerLoadingCompletedEvent;
import commands.*;
import listeners.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Score;

import java.util.Objects;

@SuppressWarnings({"unused"})
public class Plugin extends JavaPlugin {
	private static Plugin instance;
	private static UltimateAdvancementAPI advancementAPI;

	@Override
	public void onEnable() {
		instance = this;
		Objects.requireNonNull(this.getCommand("getopitems")).setExecutor(new GetOPItems());
		Objects.requireNonNull(this.getCommand("locateplayer")).setExecutor(new LocatePlayer());
		Objects.requireNonNull(this.getCommand("w")).setExecutor(new Tell());
		Objects.requireNonNull(this.getCommand("resetwitherfight")).setExecutor(new ResetWitherFight());
		Objects.requireNonNull(this.getCommand("m7tasactivatewitherfight")).setExecutor(new ActivateWitherFight());

		getServer().getPluginManager().registerEvents(new CustomItems(), this);
		getServer().getPluginManager().registerEvents(new BetterAnvil(), this);
		getServer().getPluginManager().registerEvents(new KeepEnchantsOnCraft(), this);
		getServer().getPluginManager().registerEvents(new NoArrows(), this);
		getServer().getPluginManager().registerEvents(new ArrowSounds(), this);
		getServer().getPluginManager().registerEvents(new ArrowMechanicsHandler(), this);
		getServer().getPluginManager().registerEvents(new CustomMobs(), this);
		getServer().getPluginManager().registerEvents(new CustomDrops(), this);
		getServer().getPluginManager().registerEvents(new EditSkull(), this);
		getServer().getPluginManager().registerEvents(new CustomDamage(), this);
		getServer().getPluginManager().registerEvents(new OldRegen(), this);
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new CustomItemUses(), this);
		getServer().getPluginManager().registerEvents(new StopBossesTeleporting(), this);
		getServer().getPluginManager().registerEvents(new PlayerLoginHandler(), this);
		getServer().getPluginManager().registerEvents(new AllMobsHaveNames(), this);
		getServer().getPluginManager().registerEvents(new CustomChestLoot(), this);
		getServer().getPluginManager().registerEvents(new WitherKingDragonCustomAI(), this);
		getServer().getPluginManager().registerEvents(new CustomMining(), this);
		getServer().getPluginManager().registerEvents(new CreativeMenu(), this);

		getServer().addRecipe(AddRecipes.addScyllaRecipe(this));
		getServer().addRecipe(AddRecipes.addClaymoreRecipe(this));
		getServer().addRecipe(AddRecipes.addTermRecipe(this));
		getServer().addRecipe(AddRecipes.addAOTVRecipe(this));
		getServer().addRecipe(AddRecipes.addWardenHelmetRecipe(this));
		getServer().addRecipe(AddRecipes.addNecronElytraRecipe(this));
		getServer().addRecipe(AddRecipes.addGoldorLeggingsRecipe(this));
		getServer().addRecipe(AddRecipes.addNecromancerLegginsRecipe(this));
		getServer().addRecipe(AddRecipes.addMaxorBootsRecipe(this));
		getServer().addRecipe(AddRecipes.addPrimalChesplateRecipe(this));
		getServer().addRecipe(AddRecipes.addGodAppleRecipe(this));
		getServer().addRecipe(AddRecipes.addWandOfRestorationRecipe(this));
		getServer().addRecipe(AddRecipes.addWandOfAtonementRecipe(this));
		getServer().addRecipe(AddRecipes.addDrillRecipe(this));
		getServer().addRecipe(AddRecipes.addHolyIceRecipe(this));
		getServer().addRecipe(AddRecipes.addGyroRecipe(this));
		getServer().addRecipe(AddRecipes.addSharp7Recipe(this));
		getServer().addRecipe(AddRecipes.addPower7Recipe(this));
		getServer().addRecipe(AddRecipes.addLooting5Recipe(this));
		getServer().addRecipe(AddRecipes.addEfficiency6Recipe(this));
		getServer().addRecipe(AddRecipes.addFeatherFalling5Recipe(this));

		getLogger().info("Started SkyBlock in Vanilla!");

		setupAdvancements();

		try {
			Objects.requireNonNull(getServer().getScoreboardManager()).getMainScoreboard().registerNewObjective("Intelligence", Criteria.DUMMY, "Intelligence");
			getLogger().info("Could not find Intelligence.  Adding to Scoreboard.");
		} catch(Exception exception) {
			Objects.requireNonNull(getServer().getScoreboardManager()).getMainScoreboard().getObjective("Intelligence");
			getLogger().info("Deteced Intelligence.");
		}

		Utils.scheduleTask(() -> passiveIntel(0), 20L);
	}

	private static void setupAdvancements() {
		advancementAPI = UltimateAdvancementAPI.getInstance(Plugin.getInstance());

		AdvancementTab tab = advancementAPI.createAdvancementTab("skyblock");

		RootAdvancement root = new RootAdvancement(tab, "root", new AdvancementDisplay(Material.NETHER_STAR, "SkyBlock", AdvancementFrameType.TASK, false, false, 0, 3, "Can you beat all the bosses?"), "textures/block/light_blue_concrete.png");
		BaseAdvancement defeatChickzilla = new BaseAdvancement("defeat_chickzilla", new AdvancementDisplay(Material.COOKED_CHICKEN, "Cooked Chicken", AdvancementFrameType.TASK, true, true, 1, 6, "Yummy food."), root, 1);
		BaseAdvancement defeatHardChickzilla = new BaseAdvancement("defeat_hard_chickzilla", new AdvancementDisplay(Material.CAMPFIRE, "Still Cooked", AdvancementFrameType.TASK, true, true, 2, 6, "Always a good day to have cooked chicken."), defeatChickzilla, 1);
		BaseAdvancement defeatBroodfather = new BaseAdvancement("defeat_tarantula_broodfather", new AdvancementDisplay(Material.SPIDER_EYE, "Spider Squasher", AdvancementFrameType.TASK, true, true, 1, 5, "It will never know what hit it."), root, 1);
		BaseAdvancement defeatPrimordialBroodfather = new BaseAdvancement("defeat_primordial_broodfather", new AdvancementDisplay(Material.FERMENTED_SPIDER_EYE, "Saving Austrailia", AdvancementFrameType.CHALLENGE, true, true, 2, 5, "One less scary spider."), defeatBroodfather, 1);
		BaseAdvancement defeatRevenant = new BaseAdvancement("defeat_revenant_horror", new AdvancementDisplay(Material.ROTTEN_FLESH, "Trolling the Reaper", AdvancementFrameType.TASK, true, true, 1, 4, "Trololololol"), root, 1);
		BaseAdvancement defeatAtonedHorror = new BaseAdvancement("defeat_atoned_horror", new AdvancementDisplay(Material.ZOMBIE_HEAD, "The Dead Truly Die", AdvancementFrameType.TASK, true, true, 2, 4, "Who knew!"), defeatRevenant, 1);
		BaseAdvancement defeatSadan = new BaseAdvancement("defeat_sadan", new AdvancementDisplay(Material.WRITABLE_BOOK, "The Apprentice Necromancer", AdvancementFrameType.TASK, true, true, 3, 4, "I guess the dead don't die after all."), defeatAtonedHorror, 1);
		BaseAdvancement defeatHardSadan = new BaseAdvancement("defeat_hard_sadan", new AdvancementDisplay(Material.ENCHANTED_BOOK, "The Master Necromancer", AdvancementFrameType.CHALLENGE, true, true, 4, 4, "Nevermind, they truly die after all."), defeatSadan, 1);
		BaseAdvancement defeatWitherLords = new BaseAdvancement("defeat_wither_lords", new AdvancementDisplay(Material.WITHER_SKELETON_SKULL, "Slayer of Withers, Master of Worlds", AdvancementFrameType.CHALLENGE, true, true, 1, 3, "You are a mighty warrior."), root, 1);
		BaseAdvancement defeatmeloGnorI = new BaseAdvancement("defeat_melog_nori", new AdvancementDisplay(Material.WRITABLE_BOOK, "Master's in Antimatter", AdvancementFrameType.TASK, true, true, 1, 2, "Granted by the University of SkyBlock."), root, 1);
		BaseAdvancement defeatHardmeloGnorI = new BaseAdvancement("defeat_hard_melog_nori", new AdvancementDisplay(Material.ENCHANTED_BOOK, "Doctorate in Antimatter", AdvancementFrameType.TASK, true, true, 2, 2, "Also granted by the University of SkyBlock."), defeatmeloGnorI, 1);
		BaseAdvancement defeatZealot = new BaseAdvancement("defeat_zealot", new AdvancementDisplay(Material.ENDER_PEARL, "The End?", AdvancementFrameType.TASK, true, true, 1, 1, "???"), root, 1);
		BaseAdvancement defeatBrusier = new BaseAdvancement("defeat_zealot_brusier", new AdvancementDisplay(Material.ENDER_EYE, "The End.", AdvancementFrameType.TASK, true, true, 2, 1, "Or is it?"), defeatZealot, 1);
		BaseAdvancement defeatVoidgloom = new BaseAdvancement("defeat_voidgloom_seraph", new AdvancementDisplay(Material.END_CRYSTAL, "Ender of Ender", AdvancementFrameType.TASK, true, true, 3, 1.5f, "Silence falls across the land."), defeatBrusier, 1);
		BaseAdvancement defeatVoidcrazedSeraph = new BaseAdvancement("defeat_voidcrazed_seraph", new AdvancementDisplay(Material.BEACON, "The Line Between Genius and Insanity", AdvancementFrameType.CHALLENGE, true, true, 4, 1.5f, "??????????!!!!!!!!!!"), defeatVoidgloom, 1);
		BaseAdvancement defeatPrimalDragon = new BaseAdvancement("defeat_primal_dragon", new AdvancementDisplay(Material.DRAGON_EGG, "The Beginning.", AdvancementFrameType.CHALLENGE, true, true, 3, 0.5f, "The start of something new."), defeatBrusier, 1);

		tab.registerAdvancements(root, defeatChickzilla, defeatHardChickzilla, defeatBroodfather, defeatPrimordialBroodfather, defeatmeloGnorI, defeatHardmeloGnorI, defeatWitherLords, defeatRevenant, defeatAtonedHorror, defeatSadan, defeatHardSadan, defeatZealot, defeatBrusier, defeatVoidgloom, defeatVoidcrazedSeraph, defeatPrimalDragon);

		tab.getEventManager().register(tab, PlayerLoadingCompletedEvent.class, event -> {
			tab.showTab(event.getPlayer());
			tab.grantRootAdvancement(event.getPlayer());
		});
	}

	public static void passiveIntel(int second) {
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			try {
				Score score = Objects.requireNonNull(Objects.requireNonNull(Plugin.getInstance().getServer().getScoreboardManager()).getMainScoreboard().getObjective("Intelligence")).getScore(p.getName());
				if(score.getScore() < 2500) {
					if(second == 5) {
						score.setScore(score.getScore() + 1);
					}
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.AQUA + "Intelligence: " + score.getScore() + "/2500"));
				} else {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.AQUA + "Intelligence: " + score.getScore() + "/2500 " + ChatColor.RED + ChatColor.BOLD + "MAX INTELLIGENCE"));
				}
			} catch(Exception exception) {
				Plugin.getInstance().getLogger().info("Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
				Bukkit.broadcastMessage(ChatColor.RED + "Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
				return;
			}
		}

		if(second == 5) {
			second = 0;
		} else {
			second ++;
		}
		int finalSecond = second;
		Utils.scheduleTask(() -> passiveIntel(finalSecond), 20L);
	}

	@Override
	public void onDisable() {
		getLogger().info("Stopped SkyBlock in Vanilla!");
	}

	public static Plugin getInstance() {
		return instance;
	}

	public static UltimateAdvancementAPI getAdvancementAPI() {
		return advancementAPI;
	}
}