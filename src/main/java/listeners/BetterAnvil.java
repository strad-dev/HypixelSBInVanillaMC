package listeners;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import misc.Utils;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AnvilMenu;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

public class BetterAnvil implements Listener {
	private static final String HANDLER_NAME = "better_anvil_rename_fix";

	private static Channel getChannel(ServerPlayer serverPlayer) {
		try {
			// ServerCommonPacketListenerImpl.connection is protected
			Field connectionField = serverPlayer.connection.getClass().getSuperclass().getDeclaredField("connection");
			connectionField.setAccessible(true);
			Object connection = connectionField.get(serverPlayer.connection);
			// Connection.channel
			Field channelField = connection.getClass().getDeclaredField("channel");
			channelField.setAccessible(true);
			return (Channel) channelField.get(connection);
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@EventHandler
	public void onAnvilOpen(InventoryOpenEvent e) {
		if(e.getInventory().getType() != InventoryType.ANVIL) return;
		if(!(e.getPlayer() instanceof Player player)) return;

		// containerMenu isn't AnvilMenu yet at open time — delay by 1 tick
		Utils.scheduleTask(() -> {
			ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
			if(!(serverPlayer.containerMenu instanceof AnvilMenu anvilMenu)) return;

			anvilMenu.maximumRepairCost = Integer.MAX_VALUE;

			int containerId = anvilMenu.containerId;
			Channel channel = getChannel(serverPlayer);
			if(channel == null) return;

			// Remove existing handler if present
			if(channel.pipeline().get(HANDLER_NAME) != null) {
				channel.pipeline().remove(HANDLER_NAME);
			}

			// Intercept packets to prevent rename text field reset
			channel.pipeline().addBefore("packet_handler", HANDLER_NAME, new ChannelDuplexHandler() {
				private boolean renaming = false;
				private String renameText = null;

				@Override
				public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
					if(msg instanceof ServerboundRenameItemPacket renamePacket) {
						renaming = true;
						renameText = renamePacket.getName();
					} else if(msg instanceof ServerboundContainerClickPacket) {
						renaming = false;
						renameText = null;
					}
					super.channelRead(ctx, msg);
				}

				@Override
				public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
					if(renaming) {
						// Suppress individual slot 0 updates during rename
						if(msg instanceof ClientboundContainerSetSlotPacket slotPacket) {
							if(slotPacket.getContainerId() == containerId && slotPacket.getSlot() == 0) {
								promise.setSuccess();
								return;
							}
						}
						// Replace full container updates — send slot 0 with rename text as display name
						if(msg instanceof ClientboundContainerSetContentPacket(
								int id, int stateId, java.util.List<net.minecraft.world.item.ItemStack> items,
								net.minecraft.world.item.ItemStack carriedItem
						)) {
							if(id == containerId) {
								// Send slot 0 with modified display name so client text field stays correct
								if(!items.isEmpty() && renameText != null) {
									net.minecraft.world.item.ItemStack slot0 = items.getFirst().copy();
									slot0.set(
										net.minecraft.core.component.DataComponents.CUSTOM_NAME,
										net.minecraft.network.chat.Component.literal(renameText)
									);
									ctx.write(new ClientboundContainerSetSlotPacket(containerId, stateId, 0, slot0));
								}
								for(int i = 1; i < items.size() - 1; i++) {
									ctx.write(new ClientboundContainerSetSlotPacket(containerId, stateId, i, items.get(i)));
								}
								if(items.size() > 1) {
									ctx.write(new ClientboundContainerSetSlotPacket(
										containerId, stateId, items.size() - 1, items.getLast()
									), promise);
								}
								return;
							}
						}
					}
					super.write(ctx, msg, promise);
				}
			});
		}, 1);
	}

	@EventHandler
	public void onAnvilClose(InventoryCloseEvent e) {
		if(e.getInventory().getType() != InventoryType.ANVIL) return;
		if(!(e.getPlayer() instanceof Player player)) return;

		ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
		Channel channel = getChannel(serverPlayer);
		if(channel == null) return;
		channel.eventLoop().execute(() -> {
			if(channel.pipeline().get(HANDLER_NAME) != null) {
				channel.pipeline().remove(HANDLER_NAME);
			}
		});
	}

	@EventHandler
	public void onAnvilClick(InventoryClickEvent e) {
		if(e.getInventory().getType() != InventoryType.ANVIL) return;
		if(!(e.getWhoClicked() instanceof Player player)) return;

		Inventory anvil = e.getInventory();

		// After vanilla processes the click, check if we need to override the result
		Utils.scheduleTask(() -> {
			ItemStack first = anvil.getItem(0);
			ItemStack second = anvil.getItem(1);

			if(first == null || first.getType() == Material.AIR) return;
			if(second == null || second.getType() == Material.AIR) return;
			if(second.getType() != Material.ENCHANTED_BOOK) return;
			if(first.getType() == Material.ENCHANTED_BOOK) return;

			ItemStack result = first.clone();
			ItemMeta bookMeta = second.getItemMeta();
			if(bookMeta == null) return;

			for(Enchantment enchantment : ((EnchantmentStorageMeta) bookMeta).getStoredEnchants().keySet()) {
				if(canEnchant(first, enchantment)) {
					int bookLevel = ((EnchantmentStorageMeta) bookMeta).getStoredEnchantLevel(enchantment);
					int itemLevel = first.getEnchantmentLevel(enchantment);
					if(itemLevel < bookLevel) {
						result.addUnsafeEnchantment(enchantment, bookLevel);
					}
				}
			}

			// Preserve any rename from vanilla's result
			ItemStack vanillaResult = anvil.getItem(2);
			if(vanillaResult != null && vanillaResult.hasItemMeta()) {
				ItemMeta vanillaMeta = vanillaResult.getItemMeta();
				if(vanillaMeta != null && vanillaMeta.hasDisplayName()) {
					ItemMeta rm = result.getItemMeta();
					if(rm != null) {
						rm.setDisplayName(vanillaMeta.getDisplayName());
						result.setItemMeta(rm);
					}
				}
			}

			ItemMeta resultMeta = result.getItemMeta();
			if(resultMeta instanceof Repairable repairable) {
				if(repairable.getRepairCost() > 50) {
					repairable.setRepairCost(50);
				}
				result.setItemMeta(resultMeta);
			}

			anvil.setItem(2, result);

			ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
			if(serverPlayer.containerMenu instanceof AnvilMenu anvilMenu) {
				if(anvilMenu.cost.get() > 50) {
					anvilMenu.cost.set(50);
				}
			}

			player.updateInventory();
		}, 1);
	}

	public boolean canEnchant(ItemStack itemStack, Enchantment enchantment) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		AtomicBoolean canEnchant = new AtomicBoolean(enchantment.canEnchantItem(itemStack));

		if(itemMeta != null) {
			if(itemStack.getType() == Material.ELYTRA && enchantment == Enchantment.PROTECTION) {
				canEnchant.set(true);
			}
			itemMeta.getEnchants().keySet().forEach(ench -> {
				if(ench != enchantment && ench.conflictsWith(enchantment)) {
					canEnchant.set(false);
				}
			});
		}
		return canEnchant.get();
	}
}
