package pvp;

import misc.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * /joinarena — teleport into the FFA arena spawn (remembering where the player was).
 * /leavearena — return to that original spot, or the player's respawn point if it's invalid, or
 * world spawn if that's invalid too.
 */
public class JoinArenaCommand implements CommandExecutor {
	private final PvpConfig cfg;
	private final DuelManager duels;
	/** where each player was before they ran /joinarena. */
	private final Map<UUID, Location> origins = new HashMap<>();

	public JoinArenaCommand(PvpConfig cfg, DuelManager duels) {
		this.cfg = cfg;
		this.duels = duels;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player p)) {
			sender.sendMessage(Utils.msg("<red>Only players can do that."));
			return true;
		}
		if (duels.inDuel(p.getUniqueId())) {
			p.sendMessage(Utils.msg("<red>You're in a duel."));
			return true;
		}
		if (command.getName().equalsIgnoreCase("leavearena")) {
			p.teleport(returnLocation(p));
			origins.remove(p.getUniqueId());
			p.sendMessage(Utils.msg("<gray>Left the arena."));
			return true;
		}
		if (!cfg.ffaEnabled()) {
			p.sendMessage(Utils.msg("<red>FFA is disabled on this server."));
			return true;
		}
		Location spawn = cfg.ffaSpawn();
		if (spawn == null) {
			p.sendMessage(Utils.msg("<red>The FFA arena isn't configured yet."));
			return true;
		}
		origins.put(p.getUniqueId(), p.getLocation().clone()); // remember where they came from
		p.teleport(spawn);
		p.sendMessage(Utils.msg("<green>Welcome to the FFA arena — good luck!"));
		return true;
	}

	/** Original spot -> player respawn point -> world spawn, picking the first valid one. */
	private Location returnLocation(Player p) {
		Location origin = origins.get(p.getUniqueId());
		if (valid(origin)) return origin;
		Location respawn = p.getRespawnLocation();
		if (valid(respawn)) return respawn;
		return p.getWorld().getSpawnLocation().add(0.5, 0, 0.5);
	}

	private static boolean valid(Location loc) {
		return loc != null && loc.getWorld() != null;
	}
}
