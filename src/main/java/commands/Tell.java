package commands;

import misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class Tell implements CommandExecutor {

	private static final Map<UUID, String> lastFrom = new HashMap<>();

	@Override
	public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String @NonNull [] strings) {
		return sendWhisper(commandSender, strings);
	}

	public static boolean sendWhisper(CommandSender commandSender, String[] strings) {
		Player player;
		try {
			player = getServer().getPlayer(strings[0]);
		} catch(Exception exception) {
			commandSender.sendMessage(Utils.msg("<red>You didn't provide a player to send messages to."));
			return false;
		}
		if(player == null) {
			commandSender.sendMessage(Utils.msg("<red>Invalid player provided."));
			return false;
		}
		if(commandSender instanceof Player temp && temp.equals(player)) {
			commandSender.sendMessage(Utils.msg("<red>Why would you tell something to yourself, just write it down in Notepad or something."));
			return true;
		}
		StringBuilder string = new StringBuilder();
		for(int i = 1; i < strings.length; i++) {
			string.append(strings[i]).append(' ');
		}
		if(!string.isEmpty()) {
			string.deleteCharAt(string.length() - 1);
		}
		player.sendMessage(Utils.msg("<green>" + commandSender.getName() + "<gray><italic> whispers to you: " + string));
		commandSender.sendMessage(Utils.msg("<gray><italic>You whisper to <green>" + player.getName() + "<gray>: " + string));
		Bukkit.getLogger().info(commandSender.getName() + " whispers to " + player.getName() + ": " + string);
		lastFrom.put(player.getUniqueId(), commandSender.getName());
		return true;
	}

	public static boolean reply(CommandSender commandSender, String[] strings) {
		if(!(commandSender instanceof Player player)) {
			commandSender.sendMessage(Utils.msg("<red>Only players can reply."));
			return true;
		}
		String to = lastFrom.get(player.getUniqueId());
		if(to == null) {
			commandSender.sendMessage(Utils.msg("<red>Nobody to reply to."));
			return true;
		}
		String[] args = new String[strings.length + 1];
		args[0] = to;
		System.arraycopy(strings, 0, args, 1, strings.length);
		return sendWhisper(commandSender, args);
	}

	public static class Reply implements CommandExecutor {
		@Override
		public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String @NonNull [] strings) {
			return reply(commandSender, strings);
		}
	}
}
