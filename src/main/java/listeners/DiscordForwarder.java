package listeners;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.entity.Player;

public class DiscordForwarder {
	@SuppressWarnings("deprecation")
	public static void forward(Player player, String message) {
		DiscordSRV.getPlugin().processChatMessage(player, message, "global", false);
	}

	public static void forwardDeathMessage(String message) {
		DiscordUtil.sendMessage(DiscordSRV.getPlugin().getMainTextChannel(), message);
	}

	public static void forwardAdvancement(String playerName, String advancementTitle) {
		DiscordUtil.sendMessage(DiscordSRV.getPlugin().getMainTextChannel(),
			playerName + " has made the advancement " + advancementTitle);
	}
}
