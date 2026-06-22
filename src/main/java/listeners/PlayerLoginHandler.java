package listeners;

import misc.AddRecipes;
import misc.BossBarManager;
import misc.Plugin;
import misc.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerLoginHandler implements Listener {

	@SuppressWarnings("DataFlowIssue")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		BossBarManager.addPlayerToActiveBars(p);
		p.discoverRecipes(AddRecipes.returnRecipes(Plugin.getInstance()));
		p.sendMessage(Utils.msg("""
				 <blue><bold>✦ Hypixel SkyBlock in Vanilla Minecraft Plugin ✦<reset>
				<black>---<dark_blue>---<dark_green>---<dark_aqua>---<dark_red>---<dark_purple>---<gold>---<gray>---\
				<dark_gray>---<blue>---<green>---<aqua>---<red>---<light_purple>---<yellow>---<white>---
				<reset><aqua><bold>GITHUB: <reset>https://github.com/strad-dev/HypixelSBInVanillaMC\s
				
				<reset><blue><bold>DISCORD: <reset>https://discord.gg/gNfPwa8\s
				
				<reset><red><bold>YOUTUBE: <reset>https://www.youtube.com/@Stradivarius_Violin\s
				
				<reset><yellow>Found a bug?  Have a suggestion?  Make a ticket in the Github or contact stradivariusviolin on Discord."""));
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

	}
}
