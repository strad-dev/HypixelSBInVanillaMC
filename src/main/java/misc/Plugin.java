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
		getServer().getPluginManager().registerEvents(new GivePlayersRecipes(), this);
		getServer().getPluginManager().registerEvents(new AllMobsHaveNames(), this);
		getServer().getPluginManager().registerEvents(new CustomChestLoot(), this);
		getServer().getPluginManager().registerEvents(new WitherKingDragonCustomAI(), this);
		getServer().getPluginManager().registerEvents(new CustomMining(), this);

		getServer().addRecipe(AddRecipes.addScyllaRecipe(this));
		getServer().addRecipe(AddRecipes.addClaymoreRecipe(this));
		getServer().addRecipe(AddRecipes.addTermRecipe(this));
		getServer().addRecipe(AddRecipes.addAOTVRecipe(this));
		getServer().addRecipe(AddRecipes.addWardenHelmetRecipe(this));
		getServer().addRecipe(AddRecipes.addNecronElytraRecipe(this));
		getServer().addRecipe(AddRecipes.addGoldorLeggingsRecipe(this));
		getServer().addRecipe(AddRecipes.addMaxorBootsRecipe(this));
		getServer().addRecipe(AddRecipes.addGodAppleRecipe(this));
		getServer().addRecipe(AddRecipes.addWandOfRestorationRecipe(this));
		getServer().addRecipe(AddRecipes.addWandOfAtonementRecipe(this));
		getServer().addRecipe(AddRecipes.addDrillRecipe(this));
		getServer().addRecipe(AddRecipes.addHolyIceRecipe(this));
		getServer().addRecipe(AddRecipes.addSharp7Recipe(this));
		getServer().addRecipe(AddRecipes.addPower7Recipe(this));
		getServer().addRecipe(AddRecipes.addLooting5Recipe(this));
		getServer().addRecipe(AddRecipes.addEfficiency6Recipe(this));
		getServer().addRecipe(AddRecipes.addFeatherFalling5Recipe(this));

		UltimateAdvancementAPI api = UltimateAdvancementAPI.getInstance(this);

		AdvancementTab tab = api.createAdvancementTab("skyblock");

		RootAdvancement root = new RootAdvancement(tab, "root", new AdvancementDisplay(Material.NETHER_STAR, "SkyBlock", AdvancementFrameType.TASK, false, false, 0, 0, "Fully featured in 3-5 business days!"), "textures/block/light_blue_concrete.png");
		BaseAdvancement advancement = new BaseAdvancement("defeat_wither_lords", new AdvancementDisplay(Material.NETHERITE_SWORD, "Slayer of Withers, Master of Worlds", AdvancementFrameType.CHALLENGE, true, true, 1, 0, "You are a mighty warrior."), root);
		tab.registerAdvancements(root, advancement);

		tab.getEventManager().register(tab, PlayerLoadingCompletedEvent.class, event -> {
			tab.showTab(event.getPlayer());
			tab.grantRootAdvancement(event.getPlayer());
		});

		getLogger().info("Started SkyBlock in Vanilla!");

		try {
			Objects.requireNonNull(getServer().getScoreboardManager()).getMainScoreboard().registerNewObjective("Intelligence", Criteria.DUMMY, "Intelligence");
			getLogger().info("Could not find Intelligence.  Adding to Scoreboard.");
		} catch(Exception exception) {
			Objects.requireNonNull(getServer().getScoreboardManager()).getMainScoreboard().getObjective("Intelligence");
			getLogger().info("Deteced Intelligence.");
		}

		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), this::passiveIntel, 100L);
	}

	public void passiveIntel() {
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			try {
				Score score = Objects.requireNonNull(Objects.requireNonNull(Plugin.getInstance().getServer().getScoreboardManager()).getMainScoreboard().getObjective("Intelligence")).getScore(p.getName());
				if(score.getScore() < 2500) {
					score.setScore(score.getScore() + 1);
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
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), this::passiveIntel, 100L);
	}

	@Override
	public void onDisable() {
		getLogger().info("Stopped SkyBlock in Vanilla!");
	}

	public static Plugin getInstance() {
		return instance;
	}
}