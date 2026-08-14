package pvp;

import misc.Utils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * /duel <player>     challenge an online player here.
 * /duel accept       accept the latest challenge.
 * /duel decline      decline a pending challenge (receiver).
 * /duel cancel       cancel your outgoing challenge (sender).
 * /duel leave (ff)   forfeit the duel you're currently in.
 * /duel start <a> <b> force-pair two players (used by the network plugin / admins).
 */
public class DuelCommand implements CommandExecutor, TabCompleter {
	private static final List<String> SUBCOMMANDS = List.of("accept", "decline", "cancel", "leave", "ff");

	private final DuelManager duels;

	public DuelCommand(DuelManager duels) {
		this.duels = duels;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args.length >= 1 && args[0].equalsIgnoreCase("start")) {
			return start(sender, args);
		}
		if (args.length >= 1 && args[0].equalsIgnoreCase("forceclear")) {
			return forceClear(sender, args);
		}
		if (!(sender instanceof Player p)) {
			sender.sendMessage(Utils.msg("<red>Only players can duel"));
			return true;
		}
		if (args.length >= 1 && args[0].equalsIgnoreCase("accept")) {
			duels.accept(p);
			return true;
		}
		if (args.length >= 1 && args[0].equalsIgnoreCase("decline")) {
			duels.decline(p);
			return true;
		}
		if (args.length >= 1 && args[0].equalsIgnoreCase("cancel")) {
			duels.cancel(p);
			return true;
		}
		if (args.length >= 1 && (args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("ff"))) {
			duels.leave(p);
			return true;
		}
		if (args.length < 1) {
			p.sendMessage(Utils.msg("<red>Usage: /duel <player> | /duel accept | /duel decline | /duel cancel | /duel leave (ff)"));
			return true;
		}
		Player target = Bukkit.getPlayerExact(args[0]);
		if (target == null) {
			p.sendMessage(Utils.msg("<red>That player isn't on this server"));
			return true;
		}
		duels.invite(p, target);
		return true;
	}

	/** /duel start <a> <b> console/op only (network plugin pairing). */
	private boolean start(CommandSender sender, String[] args) {
		boolean privileged = !(sender instanceof Player p) || p.isOp();
		if (!privileged) {
			sender.sendMessage(Utils.msg("<red>You can't force-start duels"));
			return true;
		}
		if (args.length < 3) {
			sender.sendMessage(Utils.msg("<red>Usage: /duel start <a> <b>"));
			return true;
		}
		Player a = Bukkit.getPlayerExact(args[1]);
		Player b = Bukkit.getPlayerExact(args[2]);
		if (a == null || b == null) {
			// Placeholders are <x>/<y>: <b> is MiniMessage's bold tag and would eat the second name.
			sender.sendMessage(Utils.msg("<red>Both players must be online here (<x>, <y>)",
					Placeholder.unparsed("x", args[1]), Placeholder.unparsed("y", args[2])));
			return true;
		}
		duels.start(a, b);
		return true;
	}

	/**
	 * /duel forceclear <player> console/op only: end whatever duel state they're in - an active duel
	 * becomes a draw, a queue slot is dropped, requests are cancelled. The network plugin runs this before
	 * force-pairing someone who's already busy (its /forceduel).
	 */
	private boolean forceClear(CommandSender sender, String[] args) {
		boolean privileged = !(sender instanceof Player p) || p.isOp();
		if (!privileged) {
			sender.sendMessage(Utils.msg("<red>You can't force-clear duels"));
			return true;
		}
		if (args.length < 2) {
			sender.sendMessage(Utils.msg("<red>Usage: /duel forceclear <player>"));
			return true;
		}
		Player t = Bukkit.getPlayerExact(args[1]);
		if (t == null) {
			sender.sendMessage(Utils.msg("<red>That player isn't on this server"));
			return true;
		}
		duels.forceClear(t);
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (args.length != 1) return List.of();
		String prefix = args[0].toLowerCase();
		List<String> out = new ArrayList<>();
		for (String s : SUBCOMMANDS) if (s.startsWith(prefix)) out.add(s);
		for (Player pl : Bukkit.getOnlinePlayers()) {
			if (pl.getName().toLowerCase().startsWith(prefix)) out.add(pl.getName());
		}
		return out;
	}
}
