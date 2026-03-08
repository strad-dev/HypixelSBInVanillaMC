package listeners;

import misc.AddRecipes;
import misc.BossBarManager;
import misc.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerLoginHandler implements Listener {

	@SuppressWarnings("DataFlowIssue")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		BossBarManager.addPlayerToActiveBars(p);
		p.discoverRecipes(AddRecipes.returnRecipes(Plugin.getInstance()));
		p.sendMessage(" " + ChatColor.BLUE + ChatColor.BOLD + ChatColor.MAGIC + "E" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " Hypixel SkyBlock in Vanilla Minecraft Plugin " + ChatColor.MAGIC + "E" + ChatColor.RESET + "\n" +
				ChatColor.BLACK + "---" + ChatColor.DARK_BLUE + "---" + ChatColor.DARK_GREEN + "---" + ChatColor.DARK_AQUA + "---" + ChatColor.DARK_RED + "---" + ChatColor.DARK_PURPLE + "---" + ChatColor.GOLD + "---" + ChatColor.GRAY + "---" +
				ChatColor.DARK_GRAY + "---" + ChatColor.BLUE + "---" + ChatColor.GREEN + "---" + ChatColor.AQUA + "---" + ChatColor.RED + "---" + ChatColor.LIGHT_PURPLE + "---" + ChatColor.YELLOW + "---" + ChatColor.WHITE + "---\n" +
				ChatColor.RESET + ChatColor.AQUA + ChatColor.BOLD + "GITHUB: " + ChatColor.RESET + "https://github.com/strad-dev/HypixelSBInVanillaMC \n\n" +
				ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + "DISCORD: " + ChatColor.RESET + "https://discord.gg/gNfPwa8 \n\n" +
				ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + "YOUTUBE: " + ChatColor.RESET + "https://www.youtube.com/@Stradivarius_Violin \n\n" +
				ChatColor.RESET + ChatColor.YELLOW + "Found a bug?  Have a suggestion?  Make a ticket in the Github or contact stradivariusviolin on Discord.");
		p.removeScoreboardTag("AbilityCooldown");
		p.removeScoreboardTag("TerminatorCooldown");
		p.removeScoreboardTag("IceSprayed");
		p.removeScoreboardTag("SalvationCooldown");
		p.removeScoreboardTag("WandCooldown");
		p.removeScoreboardTag("IceSprayCooldown");
		p.removeScoreboardTag("IceCooldown");
		p.removeScoreboardTag("TacCooldown");
		p.removeScoreboardTag("GyroCooldown");
		p.removeScoreboardTag("WitherShield");
		p.removeScoreboardTag("HolyIce");

		PlayerInventory inventory = p.getInventory();

		// Strip empty custom_data and refresh custom items
		for(int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if(item == null) continue;
			ItemStack stripped = StripCreativeCustomData.stripEmptyCustomData(item, "PlayerJoin/slot" + i);
			if(stripped != null) {
				item = stripped;
			}
			ItemStack refreshed = StripCreativeCustomData.refreshItem(item);
			if(refreshed != null) {
				inventory.setItem(i, refreshed);
			} else if(stripped != null) {
				inventory.setItem(i, stripped);
			}
		}
	}
}
