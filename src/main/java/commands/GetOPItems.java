package commands;

import misc.Plugin;
import misc.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class GetOPItems implements CommandExecutor {
	@Override
	public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String s, String @NonNull [] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage(Utils.msg("Players only!"));
			return true;
		}

		if (!player.getGameMode().equals(GameMode.CREATIVE) && !player.isOp()) {
			sender.sendMessage(Utils.msg("<red>You must be in creative mode or be an operator!"));
			return false;
		}

		// Give the menu opener item
		ItemStack menuOpener = createMenuOpener();
		player.getInventory().addItem(menuOpener);
		player.sendMessage(Utils.msg("<green>Added SkyBlock Creative Menu to your inventory!"));
		return true;
	}

	private ItemStack createMenuOpener() {
		ItemStack item = new ItemStack(Material.NETHER_STAR);
		ItemMeta meta = item.getItemMeta();
		meta.displayName(Utils.mm("<red><bold>Creative Menu"));
		meta.lore(List.of(Utils.mm("<green>Right-click to open")));
		meta.lore(List.of(Utils.mm("")));
		meta.lore(List.of(Utils.mm("<red><bold><obfuscated>a</obfuscated> SPECIAL <obfuscated>a</obfuscated>")));

		// Add unique identifier
		NamespacedKey key = new NamespacedKey(Plugin.getInstance(), "creative_menu");
		meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte)1);

		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		return item;
	}
}