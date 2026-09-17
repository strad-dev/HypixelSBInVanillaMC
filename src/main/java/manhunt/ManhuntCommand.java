package manhunt;

import misc.Utils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * {@code /manhunt} - manage a game of Minecraft Manhunt. Only bound when {@code manhunt: true}, so the
 * command does not exist at all on an ordinary server.
 */
public class ManhuntCommand implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			usage(sender);
			return true;
		}

		// Anyone can look at the teams; only an operator gets to change them or run a match.
		if(!args[0].equalsIgnoreCase("teams") && !sender.isOp()) {
			sender.sendMessage(Utils.msg("<red>You do not have permission to do that."));
			return true;
		}

		switch(args[0].toLowerCase(java.util.Locale.ROOT)) {
			case "speedrunner" -> {
				if(args.length < 3) {
					sender.sendMessage(Utils.msg("<red>Usage: /manhunt speedrunner [add|remove] [player]"));
					return true;
				}
				OfflinePlayer target = resolve(args[2]);
				if(target == null) {
					sender.sendMessage(Utils.msg("<red>Never seen a player called <name>.",
							Placeholder.unparsed("name", args[2])));
					return true;
				}
				String name = target.getName() == null ? args[2] : target.getName();
				if(args[1].equalsIgnoreCase("add")) {
					if(Manhunt.addSpeedrunner(target)) {
						sender.sendMessage(Utils.msg("<green>" + name + " is now a Speedrunner."));
					} else {
						sender.sendMessage(Utils.msg("<red>" + name + " is already a Speedrunner."));
					}
				} else if(args[1].equalsIgnoreCase("remove")) {
					if(Manhunt.removeSpeedrunner(target)) {
						sender.sendMessage(Utils.msg("<green>" + name + " is now a Hunter."));
					} else {
						sender.sendMessage(Utils.msg("<red>" + name + " is not a Speedrunner."));
					}
				} else {
					sender.sendMessage(Utils.msg("<red>Usage: /manhunt speedrunner [add|remove] [player]"));
				}
			}
			case "teams" -> teams(sender);
			case "start" -> {
				Manhunt.start();
				Bukkit.broadcast(Utils.msg("<gold><bold>The Manhunt has begun!"));
				teams(sender);
			}
			case "reset" -> {
				Manhunt.reset();
				sender.sendMessage(Utils.msg("<green>Manhunt reset.  Every Manhunt Hyperion and Compass has been taken back."));
			}
			default -> usage(sender);
		}
		return true;
	}

	private void usage(CommandSender sender) {
		sender.sendMessage(Utils.msg("<gold>/manhunt speedrunner [add|remove] [player] <gray>- Join or leave the speedrunner team"));
		sender.sendMessage(Utils.msg("<gold>/manhunt teams <gray>- Lists the current teams"));
		sender.sendMessage(Utils.msg("<gold>/manhunt start <gray>- Start the Manhunt"));
		sender.sendMessage(Utils.msg("<gold>/manhunt reset <gray>- Reset all Manhunt items"));
	}

	private void teams(CommandSender sender) {
		List<String> speedrunners = new ArrayList<>();
		for(int i = 0; i < Manhunt.speedrunners().size(); i++) {
			UUID uuid = Manhunt.speedrunners().get(i);
			String name = Manhunt.speedrunnerName(i);
			speedrunners.add(Bukkit.getPlayer(uuid) == null ? "<gray>" + name : "<white>" + name);
		}
		List<String> hunters = new ArrayList<>();
		for(Player p : Manhunt.onlineHunters()) {
			hunters.add("<white>" + p.getName());
		}

		sender.sendMessage(Utils.msg("<green><bold>Speedrunners<green> (" + speedrunners.size() + ")<gray>: "
				+ (speedrunners.isEmpty() ? "<gray>nobody yet" : String.join("<gray>, ", speedrunners))));
		sender.sendMessage(Utils.msg("<red><bold>Hunters<red> (" + hunters.size() + ")<gray>: "
				+ (hunters.isEmpty() ? "<gray>nobody online" : String.join("<gray>, ", hunters))));
	}

	/** Online first, then anybody the server has playerdata for, so an offline Speedrunner can be set up. */
	@Nullable
	private OfflinePlayer resolve(String name) {
		Player online = Bukkit.getPlayerExact(name);
		if(online != null) return online;
		for(OfflinePlayer known : Bukkit.getOfflinePlayers()) {
			if(name.equalsIgnoreCase(known.getName())) return known;
		}
		return null;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> out = new ArrayList<>();
		if(args.length == 1) {
			for(String sub : new String[] {"speedrunner", "teams", "start", "reset"}) {
				if(sub.startsWith(args[0].toLowerCase(java.util.Locale.ROOT))) out.add(sub);
			}
		} else if(args.length == 2 && args[0].equalsIgnoreCase("speedrunner")) {
			for(String sub : new String[] {"add", "remove"}) {
				if(sub.startsWith(args[1].toLowerCase(java.util.Locale.ROOT))) out.add(sub);
			}
		} else if(args.length == 3 && args[0].equalsIgnoreCase("speedrunner")) {
			// add offers whoever is not a Speedrunner yet, remove offers only the ones who are
			boolean adding = args[1].equalsIgnoreCase("add");
			for(Player p : adding ? Manhunt.onlineHunters() : Manhunt.onlineSpeedrunners()) {
				if(p.getName().toLowerCase(java.util.Locale.ROOT).startsWith(args[2].toLowerCase(java.util.Locale.ROOT))) {
					out.add(p.getName());
				}
			}
		}
		return out;
	}
}
