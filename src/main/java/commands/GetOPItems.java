package commands;

import ingredients.*;
import items.Terminator;
import items.*;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import summonItems.*;

import static org.bukkit.Bukkit.getServer;

public class GetOPItems implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if(commandSender instanceof Player player && (player.getGameMode().equals(GameMode.CREATIVE) || player.isOp())) {
			if(player.isOp()) {
				try {
					player = getServer().getPlayer(strings[1]);
				} catch(Exception exception) {
					//nothing here lol
				}
			}
			if(player == null) {
				commandSender.sendMessage(ChatColor.RED + "Invalid player provided.");
				return false;
			}

			String itemSet;
			try {
				itemSet = strings[0];
			} catch(Exception exception) {
				commandSender.sendMessage(ChatColor.RED + "You must provide a valid Item Set.\nSets: combat, summon, ingredient, t7");
				return false;
			}

			switch(itemSet) {
				case "combat" -> {
					player.getInventory().addItem(
							Scylla.getItem(0),
							AOTV.getItem(),
							IceSpray.getItem(),
							Claymore.getItem(0),
							Terminator.getItem(0),
							WandOfAtonement.getItem(),
							HolyIce.getItem(),
							WardenHelmet.getItem(),
							NecronElytra.getItem(),
							GoldorLeggings.getItem(),
							MaxorBoots.getItem(),
							new ItemStack(Material.GOLDEN_CARROT, 64),
							new ItemStack(Material.TOTEM_OF_UNDYING));
					commandSender.sendMessage("Successfully gave " + player.getName() + " Combat Items");
					return true;
				}
				case "ingredient" -> {
					player.getInventory().addItem(
							ShadowWarp.getItem(),
							Implosion.getItem(),
							WitherShield.getItem(),
							Handle.getItem(),
							GiantSwordRemnant.getItem(),
							MaxorSecrets.getItem(),
							StormSecrets.getItem(),
							GoldorSecrets.getItem(),
							NecronSecrets.getItem(),
							WardenHeart.getItem(),
							Core.getItem(),
							TessellatedPearl.getItem(),
							NullBlade.getItem(),
							BraidedFeather.getItem(),
							TarantulaSilk.getItem(),
							Viscera.getItem());
					commandSender.sendMessage("Successfully gave " + player.getName() + " Ingredients");
					return true;
				}
				case "summon" -> {
					player.getInventory().addItem(
							SuperiorRemnant.getItem(),
							CorruptPearl.getItem(),
							Antimatter.getItem(),
							OmegaEgg.getItem(),
							SpiderRelic.getItem(),
							AtonedFlesh.getItem(),
							GiantZombieFlesh.getItem());
					commandSender.sendMessage("Successfully gave " + player.getName() + " Summon Items");
					return true;
				}
				case "t7" -> {
					ItemStack godBook = new ItemStack(Material.ENCHANTED_BOOK);
					EnchantmentStorageMeta meta = (EnchantmentStorageMeta) godBook.getItemMeta();
					meta.addStoredEnchant(Enchantment.SHARPNESS, 7, true);
					meta.addStoredEnchant(Enchantment.POWER, 7, true);
					meta.addStoredEnchant(Enchantment.LOOTING, 5, true);
					meta.addStoredEnchant(Enchantment.FORTUNE, 4, true);
					meta.addStoredEnchant(Enchantment.EFFICIENCY, 6, true);
					meta.addStoredEnchant(Enchantment.PROTECTION, 5, true);
					meta.addStoredEnchant(Enchantment.FEATHER_FALLING, 5, true);
					godBook.setItemMeta(meta);
					player.getInventory().addItem(godBook);
					commandSender.sendMessage("Successfully gave " + player.getName() + " The God Book");
					return true;
				}
				default -> {
					commandSender.sendMessage("Invalid Item Set profided.\nSets: combat, summon, ingredient, t7");
					return false;
				}
			}
		}
		return true;
	}
}
