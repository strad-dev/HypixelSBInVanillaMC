package commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.papermc.paper.event.server.ServerResourcesReloadedEvent;

import java.lang.reflect.Field;
import java.util.UUID;

public class VanillaDamageCommand implements Listener {
	private static UUID watching;
	private static Double dealt;

	public static void record(org.bukkit.entity.Entity damagee, double finalDamage) {
		if(watching != null && watching.equals(damagee.getUniqueId())) dealt = finalDamage;
	}

	public static void install() {
		var root = ((CraftServer) Bukkit.getServer()).getServer().getCommands().getDispatcher().getRoot();
		try {
			Field command = CommandNode.class.getDeclaredField("command");
			command.setAccessible(true);
			for(String label : new String[]{"damage", "minecraft:damage"}) {
				CommandNode<CommandSourceStack> node = root.getChild(label);
				if(node != null) wrap(node, command);
			}
		} catch(ReflectiveOperationException e) {
			Bukkit.getLogger().warning("[SkyBlock] Could not wrap /damage: " + e);
		}
	}

	@SuppressWarnings("unchecked")
	private static void wrap(CommandNode<CommandSourceStack> node, Field command) throws IllegalAccessException {
		Command<CommandSourceStack> orig = (Command<CommandSourceStack>) command.get(node);
		if(orig != null && !(orig instanceof Wrapped)) command.set(node, new Wrapped(orig));
		for(CommandNode<CommandSourceStack> child : node.getChildren()) wrap(child, command);
	}

	@EventHandler
	public void onReload(ServerResourcesReloadedEvent e) {
		install();
	}

	private record Wrapped(Command<CommandSourceStack> orig) implements Command<CommandSourceStack> {
		@Override
		public int run(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
			Entity target = EntityArgument.getEntity(ctx, "target");
			UUID prevWatching = watching;
			Double prevDealt = dealt;
			watching = target.getUUID();
			dealt = null;
			try {
				return orig.run(ctx);
			} catch(CommandSyntaxException e) {
				if(dealt == null) throw e;
				float amount = Math.round(dealt * 10) / 10f;
				ctx.getSource().sendSuccess(() -> Component.translatable("commands.damage.success", amount, target.getDisplayName()), true);
				return 1;
			} finally {
				watching = prevWatching;
				dealt = prevDealt;
			}
		}
	}
}
