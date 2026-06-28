package pvp;

import misc.Utils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * /stats [player] view a player's PvP stats (self if omitted). /pvptop <ffa|1v1> leaderboard.
 * Reads the shared stats file fresh each call, so it works on any SkyBlock server.
 */
public class StatsCommand implements CommandExecutor {
	private final PvpConfig cfg;

	public StatsCommand(PvpConfig cfg) {
		this.cfg = cfg;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		PvpStats.Data data = PvpJson.load(cfg.statsFile(), PvpStats.Data.class, new PvpStats.Data());

		if (command.getName().equalsIgnoreCase("pvptop")) {
			return top(sender, args, data);
		}

		String target = args.length >= 1 ? args[0]
				: (sender instanceof Player p ? p.getName() : null);
		if (target == null) {
			sender.sendMessage(Utils.msg("<red>Usage: /stats <player>"));
			return true;
		}
		PvpStats.Entry e = findByName(data, target);
		if (e == null) {
			sender.sendMessage(Utils.msg("<red>No stats for <p>.", Placeholder.unparsed("p", target)));
			return true;
		}
		sender.sendMessage(Utils.msg("<gold><bold><p></bold></gold> <gray>'s PvP stats", Placeholder.unparsed("p", e.name)));
		sender.sendMessage(Utils.msg("<yellow>Overall:</yellow> <white><k></white> K / <white><d></white> D (<aqua><kd></aqua> K/D), streak <white><cs></white> (best <white><bs></white>)",
				Placeholder.unparsed("k", String.valueOf(e.kills)),
				Placeholder.unparsed("d", String.valueOf(e.deaths)),
				Placeholder.unparsed("kd", fmt(e.kd())),
				Placeholder.unparsed("cs", String.valueOf(e.killStreak)),
				Placeholder.unparsed("bs", String.valueOf(e.bestKillStreak))));
		sender.sendMessage(Utils.msg("<yellow>1v1:</yellow> <white><w></white> W / <white><l></white> L (<aqua><wl></aqua> W/L), <white><m></white> matches, win streak <white><ws></white> (best <white><bws></white>)",
				Placeholder.unparsed("w", String.valueOf(e.wins)),
				Placeholder.unparsed("l", String.valueOf(e.losses)),
				Placeholder.unparsed("wl", fmt(e.wl())),
				Placeholder.unparsed("m", String.valueOf(e.matches)),
				Placeholder.unparsed("ws", String.valueOf(e.winStreak)),
				Placeholder.unparsed("bws", String.valueOf(e.bestWinStreak))));
		int acc = e.hitAttempts > 0 ? (int) Math.round(e.hitsLanded * 100.0 / e.hitAttempts) : 0;
		int critPct = e.hitsLanded > 0 ? (int) Math.round(e.criticalHits * 100.0 / e.hitsLanded) : 0;
		int iframePct = e.hitsLanded > 0 ? (int) Math.round(e.iframeHits * 100.0 / e.hitsLanded) : 0;
		sender.sendMessage(Utils.msg("<yellow>Combat:</yellow> <red><dmg></red> dmg <dark_gray>|</dark_gray> <aqua><h>/<ha> hits (<acc>%)</aqua> <dark_gray>|</dark_gray> <gold><cr>/<h> crit (<cp>%)</gold> <dark_gray>|</dark_gray> <blue><ifc>/<h> i-frame (<ip>%)</blue> <dark_gray>|</dark_gray> <dark_aqua><intel></dark_aqua> intelligence used <dark_gray>|</dark_gray> <green><heal></green> HP healed <dark_gray>|</dark_gray> <yellow><food></yellow> food eaten",
				Placeholder.unparsed("dmg", fmt(e.damageDealt)),
				Placeholder.unparsed("h", String.valueOf(e.hitsLanded)),
				Placeholder.unparsed("ha", String.valueOf(e.hitAttempts)),
				Placeholder.unparsed("acc", String.valueOf(acc)),
				Placeholder.unparsed("cr", String.valueOf(e.criticalHits)),
				Placeholder.unparsed("cp", String.valueOf(critPct)),
				Placeholder.unparsed("ifc", String.valueOf(e.iframeHits)),
				Placeholder.unparsed("ip", String.valueOf(iframePct)),
				Placeholder.unparsed("intel", String.valueOf(e.intelligenceUsed)),
				Placeholder.unparsed("heal", String.valueOf(e.hpHealed)),
				Placeholder.unparsed("food", String.valueOf(e.foodEaten))));
		return true;
	}

	private boolean top(CommandSender sender, String[] args, PvpStats.Data data) {
		boolean oneVone = args.length >= 1 && (args[0].equalsIgnoreCase("1v1") || args[0].equalsIgnoreCase("duel"));
		List<PvpStats.Entry> entries = new ArrayList<>(data.players.values());
		entries.sort(oneVone
				? Comparator.comparingInt((PvpStats.Entry e) -> e.wins).reversed()
				: Comparator.comparingInt((PvpStats.Entry e) -> e.kills).reversed());

		sender.sendMessage(Utils.msg("<gold><bold>Top <mode></bold></gold>",
				Placeholder.unparsed("mode", oneVone ? "1v1 (wins)" : "Free-For-All (kills)")));
		int n = Math.min(10, entries.size());
		for (int i = 0; i < n; i++) {
			PvpStats.Entry e = entries.get(i);
			String stat = oneVone ? e.wins + "W " + e.losses + "L" : e.kills + "K " + e.deaths + "D";
			sender.sendMessage(Utils.msg("<gray><i>.</gray> <white><name></white> <yellow><stat></yellow>",
					Placeholder.unparsed("i", String.valueOf(i + 1)),
					Placeholder.unparsed("name", e.name == null ? "?" : e.name),
					Placeholder.unparsed("stat", stat)));
		}
		if (n == 0) sender.sendMessage(Utils.msg("<gray>No stats yet."));
		return true;
	}

	private static PvpStats.Entry findByName(PvpStats.Data data, String name) {
		for (PvpStats.Entry e : data.players.values()) {
			if (e.name != null && e.name.equalsIgnoreCase(name)) return e;
		}
		return null;
	}

	private static String fmt(double d) {
		return String.format(Locale.US, "%.2f", d);
	}
}
