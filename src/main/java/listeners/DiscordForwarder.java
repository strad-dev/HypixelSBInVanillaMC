package listeners;

import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.entity.Player;

public class DiscordForwarder {
	@SuppressWarnings("deprecation")
	public static void forward(Player player, String message) {
		DiscordSRV.getPlugin().processChatMessage(player, message, "global", false);
	}

}
