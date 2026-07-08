package listeners;

import misc.AddRecipes;
import misc.BossBarManager;
import misc.Cooldowns;
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
		// MiniMessage output isn't auto-linked by the client the way legacy chat URLs were, so the
		// links are wrapped in explicit <click:open_url>.
		p.sendMessage(Utils.msg("""
				 <blue><bold><obfuscated>E</obfuscated> Hypixel SkyBlock in Vanilla Minecraft Plugin <obfuscated>E</obfuscated><reset>
				<black>---<dark_blue>---<dark_green>---<dark_aqua>---<dark_red>---<dark_purple>---<gold>---<gray>---\
				<dark_gray>---<blue>---<green>---<aqua>---<red>---<light_purple>---<yellow>---<white>---
				<reset><aqua><bold>GITHUB: <reset><click:open_url:'https://github.com/strad-dev/HypixelSBInVanillaMC'><u>https://github.com/strad-dev/HypixelSBInVanillaMC</u></click>

				<reset><blue><bold>DISCORD: <reset><click:open_url:'https://discord.gg/gNfPwa8'><u>https://discord.gg/gNfPwa8</u></click>

				<reset><red><bold>YOUTUBE: <reset><click:open_url:'https://www.youtube.com/@Stradivarius_Violin'><u>https://www.youtube.com/@Stradivarius_Violin</u></click>

				<reset><yellow>Found a bug?  Have a suggestion?  Make a ticket in the Github or contact stradivariusviolin on Discord."""));
		Cooldowns.clearAll(p); // ability cooldowns are tick-timestamp based now; reset them on (re)join
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
