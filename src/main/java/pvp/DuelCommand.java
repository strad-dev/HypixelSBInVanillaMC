package pvp;

import misc.Utils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /duel <player>   challenge an online player here.
 * /duel accept     accept the latest challenge.
 * /duel start <a> <b> force-pair two players (used by the network plugin / admins).
 */
public class DuelCommand implements CommandExecutor {
	private final DuelManager duels;

	public DuelCommand(DuelManager duels) {
		this.duels = duels;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args.length >= 1 && args[0].equalsIgnoreCase("start")) {
			return start(sender, args);
		}
		if (!(sender instanceof Player p)) {
			sender.sendMessage(Utils.msg("<red>Only players can duel."));
			return true;
		}
		if (args.length >= 1 && args[0].equalsIgnoreCase("accept")) {
			duels.accept(p);
			return true;
		}
		if (args.length < 1) {
			p.sendMessage(Utils.msg("<red>Usage: /duel <player> | /duel accept"));
			return true;
		}
		Player target = Bukkit.getPlayerExact(args[0]);
		if (target == null) {
			p.sendMessage(Utils.msg("<red>That player isn't on this server."));
			return true;
		}
		duels.invite(p, target);
		return true;
	}

	/** /duel start <a> <b> console/op only (network plugin pairing). */
	private boolean start(CommandSender sender, String[] args) {
		boolean privileged = !(sender instanceof Player p) || p.isOp();
		if (!privileged) {
			sender.sendMessage(Utils.msg("<red>You can't force-start duels."));
			return true;
		}
		if (args.length < 3) {
			sender.sendMessage(Utils.msg("<red>Usage: /duel start <a> <b>"));
			return true;
		}
		Player a = Bukkit.getPlayerExact(args[1]);
		Player b = Bukkit.getPlayerExact(args[2]);
		if (a == null || b == null) {
			sender.sendMessage(Utils.msg("<red>Both players must be online here. (<a>, <b>)",
					Placeholder.unparsed("a", args[1]), Placeholder.unparsed("b", args[2])));
			return true;
		}
		duels.start(a, b);
		return true;
	}
}
