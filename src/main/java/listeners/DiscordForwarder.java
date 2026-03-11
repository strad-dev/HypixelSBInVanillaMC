package listeners;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.entity.Player;

public class DiscordForwarder {
	@SuppressWarnings("deprecation")
	public static void forward(Player player, String message) {
		DiscordSRV.getPlugin().processChatMessage(player, message, "global", false);
	}

	public static void forward(String message) {
		TextChannel channel = DiscordSRV.getPlugin().getMainTextChannel();
		if (channel != null) {
			DiscordUtil.sendMessage(channel, message);
		}
	}

}
