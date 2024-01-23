package listeners;

import misc.AddRecipes;
import misc.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GivePlayersRecipes implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().discoverRecipes(AddRecipes.returnRecipes(Plugin.getInstance()));
		e.getPlayer().sendMessage(" " + ChatColor.BLUE + ChatColor.BOLD + ChatColor.MAGIC + "E" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " Hypixel SkyBlock in Vanilla Minecraft Plugin " + ChatColor.MAGIC + "E" + ChatColor.RESET + "\n" +
				ChatColor.BLACK + "---" + ChatColor.DARK_BLUE + "---" + ChatColor.DARK_GREEN + "---" + ChatColor.DARK_AQUA + "---" + ChatColor.DARK_RED + "---" + ChatColor.DARK_PURPLE + "---" + ChatColor.GOLD + "---" + ChatColor.GRAY + "---" +
				ChatColor.DARK_GRAY + "---" + ChatColor.BLUE + "---" + ChatColor.GREEN + "---" + ChatColor.AQUA + "---" + ChatColor.RED + "---" + ChatColor.LIGHT_PURPLE + "---" + ChatColor.YELLOW + "---" + ChatColor.WHITE + "---\n" +
				ChatColor.RESET + ChatColor.AQUA + ChatColor.BOLD + "DOCUMENTATION: " + ChatColor.RESET + "https://docs.google.com/document/d/1UX1amfhHxzVG-SChIbLrAEgsXr6ITOVU-xCh23qaguM/edit?usp=sharing \n\n" +
				ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + "DISCORD: " + ChatColor.RESET + "https://discord.gg/gNfPwa8 \n\n" +
				ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + "YOUTUBE PLAYLIST: " + ChatColor.RESET + "https://www.youtube.com/playlist?list=PLbM7AgwT4xmHo3ySkIA4jIEgvn8TzLQb0");
	}
}