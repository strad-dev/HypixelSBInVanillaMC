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

import commands.*;
import listeners.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Score;

import java.util.Objects;

@SuppressWarnings({"unused"})
public class Plugin extends JavaPlugin implements Listener {
	private static Plugin instance;

	@Override
	public void onEnable() {
		instance = this;
		Objects.requireNonNull(this.getCommand("getopitems")).setExecutor(new GetOPItems());
		Objects.requireNonNull(this.getCommand("locateplayer")).setExecutor(new LocatePlayer());
		Objects.requireNonNull(this.getCommand("w")).setExecutor(new Tell());
		Objects.requireNonNull(this.getCommand("tell")).setExecutor(new Tell());
		Objects.requireNonNull(this.getCommand("msg")).setExecutor(new Tell());
		Objects.requireNonNull(this.getCommand("resetwitherfight")).setExecutor(new ResetWitherFight());
		Objects.requireNonNull(this.getCommand("m7tasactivatewitherfight")).setExecutor(new ActivateWitherFight());
		Objects.requireNonNull(this.getCommand("say")).setExecutor(new Say());
		Objects.requireNonNull(this.getCommand("me")).setExecutor(new Me());

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
		getServer().getPluginManager().registerEvents(new ItemReloader(), this);
		getServer().getPluginManager().registerEvents(new CommandInterceptor(), this);
		getServer().getPluginManager().registerEvents(this, this);

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

	@SuppressWarnings("deprecation")
	private static void setupAdvancements() {
		// Root advancement
		loadAdvancement("root", null, "minecraft:nether_star", "SkyBlock", "Can you beat all the bosses?", "task", false, false, 0, 3, "minecraft:block/light_blue_concrete");

		// Chickzilla branch (y=6)
		loadAdvancement("defeat_chickzilla", "skyblock:root", "minecraft:cooked_chicken", "Cooked Chicken", "Yummy food.", "task", true, true, 1, 6, null);
		loadAdvancement("defeat_hard_chickzilla", "skyblock:defeat_chickzilla", "minecraft:campfire", "Still Cooked", "Always a good day to have cooked chicken.", "task", true, true, 2, 6, null);

		// Broodfather branch (y=5)
		loadAdvancement("defeat_tarantula_broodfather", "skyblock:root", "minecraft:spider_eye", "Spider Squasher", "It will never know what hit it.", "task", true, true, 1, 5, null);
		loadAdvancement("defeat_primordial_broodfather", "skyblock:defeat_tarantula_broodfather", "minecraft:fermented_spider_eye", "Saving Austrailia", "One less scary spider.", "challenge", true, true, 2, 5, null);

		// Revenant branch (y=4)
		loadAdvancement("defeat_revenant_horror", "skyblock:root", "minecraft:rotten_flesh", "Trolling the Reaper", "Trololololol", "task", true, true, 1, 4, null);
		loadAdvancement("defeat_atoned_horror", "skyblock:defeat_revenant_horror", "minecraft:zombie_head", "The Dead Truly Die", "Who knew!", "task", true, true, 2, 4, null);
		loadAdvancement("defeat_sadan", "skyblock:defeat_atoned_horror", "minecraft:writable_book", "The Apprentice Necromancer", "I guess the dead don't die after all.", "task", true, true, 3, 4, null);
		loadAdvancement("defeat_hard_sadan", "skyblock:defeat_sadan", "minecraft:enchanted_book", "The Master Necromancer", "Nevermind, they truly die after all.", "challenge", true, true, 4, 4, null);

		// Wither Lords (y=3)
		loadAdvancement("defeat_wither_lords", "skyblock:root", "minecraft:wither_skeleton_skull", "Slayer of Withers, Master of Worlds", "You are a mighty warrior.", "challenge", true, true, 1, 3, null);

		// meloG norI branch (y=2)
		loadAdvancement("defeat_melog_nori", "skyblock:root", "minecraft:writable_book", "Master's in Antimatter", "Granted by the University of SkyBlock.", "task", true, true, 1, 2, null);
		loadAdvancement("defeat_hard_melog_nori", "skyblock:defeat_melog_nori", "minecraft:enchanted_book", "Doctorate in Antimatter", "Also granted by the University of SkyBlock.", "task", true, true, 2, 2, null);

		// Zealot / Ender branch (y=1)
		loadAdvancement("defeat_zealot", "skyblock:root", "minecraft:ender_pearl", "The End?", "???", "task", true, true, 1, 1, null);
		loadAdvancement("defeat_zealot_brusier", "skyblock:defeat_zealot", "minecraft:ender_eye", "The End.", "Or is it?", "task", true, true, 2, 1, null);
		loadAdvancement("defeat_voidgloom_seraph", "skyblock:defeat_zealot_brusier", "minecraft:end_crystal", "Ender of Ender", "Silence falls across the land.", "task", true, true, 3, 1.5f, null);
		loadAdvancement("defeat_voidcrazed_seraph", "skyblock:defeat_voidgloom_seraph", "minecraft:beacon", "The Line Between Genius and Insanity", "??????????!!!!!!!!!!", "challenge", true, true, 4, 1.5f, null);
		loadAdvancement("defeat_primal_dragon", "skyblock:defeat_zealot_brusier", "minecraft:dragon_egg", "The Beginning.", "The start of something new.", "challenge", true, true, 3, 0.5f, null);
	}

	@SuppressWarnings("deprecation")
	private static void loadAdvancement(String name, String parent, String icon, String title, String description, String frame, boolean showToast, boolean announceToChat, float x, float y, String background) {
		NamespacedKey key = new NamespacedKey(instance, name);

		// Remove if already registered (e.g. from a reload)
		if(Bukkit.getAdvancement(key) != null) {
			Bukkit.getUnsafe().removeAdvancement(key);
		}

		StringBuilder json = new StringBuilder();
		json.append("{");

		// Display
		json.append("\"display\":{");
		json.append("\"icon\":{\"id\":\"").append(icon).append("\"},");
		json.append("\"title\":\"").append(title).append("\",");
		json.append("\"description\":\"").append(description).append("\",");
		json.append("\"frame\":\"").append(frame).append("\",");
		json.append("\"show_toast\":").append(showToast).append(",");
		json.append("\"announce_to_chat\":").append(announceToChat);
		if(background != null) {
			json.append(",\"background\":\"").append(background).append("\"");
		}
		json.append("},");

		// Parent
		if(parent != null) {
			json.append("\"parent\":\"").append(parent).append("\",");
		}

		// Single impossible criterion that we grant manually
		json.append("\"criteria\":{\"requirement\":{\"trigger\":\"minecraft:impossible\"}}");
		json.append("}");

		Bukkit.getUnsafe().loadAdvancement(key, json.toString());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Grant root advancement on join so the tab is visible
		Advancement root = Bukkit.getAdvancement(new NamespacedKey(instance, "root"));
		if(root != null) {
			event.getPlayer().getAdvancementProgress(root).awardCriteria("requirement");
		}
	}

	public static void grantAdvancement(String key, Player player) {
		// key format is "skyblock:advancement_name" - extract the name part
		String name = key.contains(":") ? key.split(":")[1] : key;
		Advancement adv = Bukkit.getAdvancement(new NamespacedKey(instance, name));
		if(adv != null && player != null) {
			player.getAdvancementProgress(adv).awardCriteria("requirement");
		}
	}

	public static Score getIntelligence(Player p) {
		return Objects.requireNonNull(Objects.requireNonNull(
			Plugin.getInstance().getServer().getScoreboardManager())
			.getMainScoreboard().getObjective("Intelligence"))
			.getScore(p.getName());
	}

	public static void sendIntelligenceBar(Player p, Score score) {
		MutableComponent message = Component.literal("Intelligence: " + score.getScore() + "/2500")
			.withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA));

		if(score.getScore() >= 2500) {
			message.append(Component.literal(" MAX INTELLIGENCE")
				.withStyle(Style.EMPTY.withColor(ChatFormatting.RED).withBold(true)));
		}

		((CraftPlayer) p).getHandle().connection.send(new ClientboundSetActionBarTextPacket(message));
	}

	public static void passiveIntel(int second) {
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			try {
				Score score = Plugin.getIntelligence(p);
				if(score.getScore() < 2500 && second == 5) {
					score.setScore(score.getScore() + 1);
				}
				Plugin.sendIntelligenceBar(p, score);
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

}