package misc;

import commands.GetOPItems;
import commands.Tell;
import listeners.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Score;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

@SuppressWarnings({"unused"})
public class Plugin extends JavaPlugin {
	private static Plugin instance;

	@Override
	public void onEnable() {
		Objects.requireNonNull(this.getCommand("getopitems")).setExecutor(new GetOPItems());
		Objects.requireNonNull(this.getCommand("w")).setExecutor((new Tell()));

		getServer().getPluginManager().registerEvents(new CustomItems(), this);
		getServer().getPluginManager().registerEvents(new NonEntityDamage(), this);
		getServer().getPluginManager().registerEvents(new NoArrows(), this);
		getServer().getPluginManager().registerEvents(new ArrowSounds(), this);
		getServer().getPluginManager().registerEvents(new NoArrowsOnGround(), this);
		getServer().getPluginManager().registerEvents(new CustomMobs(), this);
		getServer().getPluginManager().registerEvents(new CustomDrops(), this);
		getServer().getPluginManager().registerEvents(new EditSkull(), this);
		getServer().getPluginManager().registerEvents(new CustomDamage(), this);
		getServer().getPluginManager().registerEvents(new OldRegen(), this);
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new CustomItemUses(), this);
		getServer().getPluginManager().registerEvents(new StopBossesTeleporting(), this);
		getServer().getPluginManager().registerEvents(new SomeTNTDoNoDamage(), this);
		getServer().getPluginManager().registerEvents(new GivePlayersRecipes(), this);
		getServer().getPluginManager().registerEvents(new AllMobsHaveNames(), this);

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
		getServer().addRecipe(AddRecipes.addHolyIceRecipe(this));

		getLogger().info("Started SkyBlock in Vanilla!");
		instance = this;

		try {
			Objects.requireNonNull(getServer().getScoreboardManager()).getMainScoreboard().registerNewObjective("Intelligence", Criteria.DUMMY, "Intelligence");
			getLogger().info("Could not find Intelligence.  Adding to Scoreboard.");
		} catch(Exception exception) {
			Objects.requireNonNull(getServer().getScoreboardManager()).getMainScoreboard().getObjective("Intelligence");
			getLogger().info("Deteced Intelligence.");
		}

		try {
			Objects.requireNonNull(getServer().getBossBar(new NamespacedKey(this, "sadan"))).getPlayers().clear();
			getLogger().info("Detected Sadan Bossbar");
		} catch(Exception exception) {
			getServer().createBossBar(new NamespacedKey(this, "sadan"), "Sadan", BarColor.RED, BarStyle.SOLID);
			getLogger().info("Could not find Sadan Bossbar.  Adding to Bossbars.");
		}

		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), this::passiveIntel, 40L);
	}

	public void passiveIntel() {
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			try {
				Score score = Objects.requireNonNull(Objects.requireNonNull(Plugin.getInstance().getServer().getScoreboardManager()).getMainScoreboard().getObjective("Intelligence")).getScore(p.getName());
				if(score.getScore() < 2500) {
					score.setScore(score.getScore() + 1);
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Intelligence: " + score.getScore() + "/2500", ChatColor.AQUA.asBungee()));
				} else {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Intelligence: " + score.getScore() + "/2500 " + ChatColor.RED + ChatColor.BOLD + "MAX INTELLIGENCE", ChatColor.AQUA.asBungee()));
				}
			} catch(Exception exception) {
				Plugin.getInstance().getLogger().info("Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
				Bukkit.broadcastMessage(ChatColor.RED + "Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
				return;
			}
		}
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), this::passiveIntel, 40L);
	}

	@Override
	public void onDisable() {
		getLogger().info("Stopped SkyBlock in Vanilla!");
	}

	public static @Nullable Player getNearestPlayer(Entity e) {
		World world = e.getWorld();
		Location location = e.getLocation();
		ArrayList<Player> playersInWorld = new ArrayList<>(world.getEntitiesByClass(Player.class));
		if(playersInWorld.isEmpty()) return null;
		playersInWorld.sort(Comparator.comparingDouble(o -> o.getLocation().distanceSquared(location)));
		return playersInWorld.get(0);
	}

	public static Plugin getInstance() {
		return instance;
	}
}