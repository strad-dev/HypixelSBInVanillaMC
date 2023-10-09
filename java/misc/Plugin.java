package misc;

import commands.*;
import listeners.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

@SuppressWarnings({"unused"})
public class Plugin extends JavaPlugin {
	@Override
	public void onEnable() {
		Objects.requireNonNull(this.getCommand("getopitems")).setExecutor(new GetOPItems());
		Objects.requireNonNull(this.getCommand("smite")).setExecutor(new Smite());
		Objects.requireNonNull(this.getCommand("god")).setExecutor(new GOD());
		Objects.requireNonNull(this.getCommand("getopeffects")).setExecutor(new GetOPEffects());
		Objects.requireNonNull(this.getCommand("w")).setExecutor((new Tell()));
		getServer().getPluginManager().registerEvents(new CustomItems(), this);
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new DeathListener(), this);
		getServer().getPluginManager().registerEvents(new CommandUsageCheck(), this);
		getServer().getPluginManager().registerEvents(new NoArrows(), this);
		getServer().getPluginManager().registerEvents(new ArrowSounds(), this);
		getServer().getPluginManager().registerEvents(new NoArrowsOnGround(), this);
		getServer().getPluginManager().registerEvents(new CustomMobs(), this);
		getServer().getPluginManager().registerEvents(new CustomDrops(), this);
		getServer().getPluginManager().registerEvents(new EditSkull(), this);
		getServer().getPluginManager().registerEvents(new CustomDamage(), this);
		getServer().addRecipe(AddRecipes.addScyllaRecipe(this));
		getServer().addRecipe(AddRecipes.addTermRecipe(this));
		getServer().addRecipe(AddRecipes.addAOTVRecipe(this));
		getLogger().info("Started SkyBlock in Vanilla!");
	}
	@Override
	public void onDisable() {
		getLogger().info("Stopped SkyBlock in Vanilla!");
	}

	public Plugin getPlugin() {
		return this;
	}

	/**
	 * Returns the nearest player to another player, or null if there is no other player in this world
	 * @param e Entity to check
	 * @return Nearest other player, or null if there is no other player in this world
	 */
	public static @Nullable Player getNearestPlayer(Entity e) {
		World world = e.getWorld();
		Location location = e.getLocation();
		ArrayList<Player> playersInWorld = new ArrayList<>(world.getEntitiesByClass(Player.class));
		if(playersInWorld.isEmpty()) return null;
		playersInWorld.sort(Comparator.comparingDouble(o -> o.getLocation().distanceSquared(location)));
		return playersInWorld.get(0);
	}
}