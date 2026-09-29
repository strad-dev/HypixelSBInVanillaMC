package listeners;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import misc.Plugin;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class ArmorBar implements Listener {

	private static final String HANDLER = "skyblock_armor_bar";

	public static double barValue(double armor) {
		double shown = armor <= 20 ? armor * 0.8 : 16 + (armor - 20) * 0.4;
		return Math.min(20, Math.round(shown));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		install(e.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		uninstall(e.getPlayer());
	}

	public static void installAll() {
		for(Player p : Bukkit.getOnlinePlayers()) install(p);
	}

	public static void uninstallAll() {
		for(Player p : Bukkit.getOnlinePlayers()) uninstall(p);
	}

	private static void install(Player p) {
		ServerPlayer sp = ((CraftPlayer) p).getHandle();
		Channel ch = sp.connection.connection.channel;
		if(ch == null || ch.pipeline().get(HANDLER) != null) return;
		try {
			ch.pipeline().addBefore("packet_handler", HANDLER, new Handler(p));
		} catch(Exception ex) {
			Plugin.getInstance().getLogger().warning("Could not install the armor bar for " + p.getName() + ": " + ex.getMessage());
			return;
		}
		AttributeInstance armor = sp.getAttribute(Attributes.ARMOR);
		if(armor != null) sp.connection.send(new ClientboundUpdateAttributesPacket(sp.getId(), List.of(armor)));
	}

	private static void uninstall(Player p) {
		Channel ch = ((CraftPlayer) p).getHandle().connection.connection.channel;
		if(ch == null) return;
		try {
			if(ch.pipeline().get(HANDLER) != null) ch.pipeline().remove(HANDLER);
		} catch(Exception ignored) {
		}
	}

	private static final class Handler extends ChannelDuplexHandler {
		private final Player player;

		Handler(Player player) {
			this.player = player;
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			super.write(ctx, remap(msg), promise);
		}

		@SuppressWarnings("unchecked")
		private Object remap(Object msg) {
			if(msg instanceof ClientboundUpdateAttributesPacket pkt) {
				return pkt.getEntityId() == player.getEntityId() ? remapAttributes(pkt) : pkt;
			}
			if(msg instanceof ClientboundBundlePacket bundle) {
				List<Packet<? super ClientGamePacketListener>> out = new ArrayList<>();
				boolean changed = false;
				for(Packet<? super ClientGamePacketListener> sub : bundle.subPackets()) {
					Object mapped = remap(sub);
					changed |= mapped != sub;
					out.add((Packet<? super ClientGamePacketListener>) mapped);
				}
				return changed ? new ClientboundBundlePacket(out) : bundle;
			}
			return msg;
		}

		private static ClientboundUpdateAttributesPacket remapAttributes(ClientboundUpdateAttributesPacket pkt) {
			boolean hasArmor = false;
			List<AttributeInstance> out = new ArrayList<>();
			for(ClientboundUpdateAttributesPacket.AttributeSnapshot snapshot : pkt.getValues()) {
				AttributeInstance instance = new AttributeInstance(snapshot.attribute(), i -> {});
				instance.setBaseValue(snapshot.base());
				snapshot.modifiers().forEach(instance::addTransientModifier);
				if(snapshot.attribute().value() == Attributes.ARMOR.value()) {
					hasArmor = true;
					double shown = barValue(instance.getValue());
					instance = new AttributeInstance(snapshot.attribute(), i -> {});
					instance.setBaseValue(shown);
				}
				out.add(instance);
			}
			return hasArmor ? new ClientboundUpdateAttributesPacket(pkt.getEntityId(), out) : pkt;
		}
	}
}
