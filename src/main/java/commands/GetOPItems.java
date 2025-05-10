package commands;

import items.armor.*;
import items.ingredients.mining.*;
import items.ingredients.misc.*;
import items.ingredients.witherLords.*;
import items.misc.*;
import items.summonItems.*;
import items.weapons.Claymore;
import items.weapons.Scylla;
import items.weapons.Terminator;
import org.bukkit.Bukkit;
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

import static org.bukkit.Bukkit.getServer;

public class GetOPItems implements CommandExecutor {
	private static final String ITEM_SETS = "combat, summon, ingredient/withers, ingredient/misc, ingredient/mining, t7, t7smite, t7bane";

	public void sendMessage(CommandSender sender, Player p, String item) {
		sender.sendMessage("Successfully gave " + p.getName() + " " + item);
		Bukkit.getLogger().info(sender.getName() + " gave " + p.getName() + " " + item);
		if(!p.equals(sender)) {
			p.sendMessage(ChatColor.DARK_RED + String.valueOf(ChatColor.BOLD) + "AN ADMIN HAS GIVEN YOU " + item);
		}
	}

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
				commandSender.sendMessage(ChatColor.RED + "You must provide a valid Item Set.\nSets: " + ITEM_SETS);
				return false;
			}

			switch(itemSet) {
				case "combat" -> {
					player.getInventory().addItem(
							Scylla.getItem(Enchantment.SHARPNESS, 0),
							AOTV.getItem(),
							IceSpray.getItem(),
							Claymore.getItem(Enchantment.SHARPNESS, 0),
							Terminator.getItem(0),
							DivanPickaxe.getItem(),
							WandOfAtonement.getItem(),
							HolyIce.getItem(),
							new ItemStack(Material.GOLDEN_CARROT, 64),
							BonzoStaff.getItem(),
							TacticalInsertion.getItem(),
							GyrokineticWand.getItem(),
							WardenHelmet.getItem(),
							WitherKingCrown.getItem(),
							NecronElytra.getItem(),
							GoldorLeggings.getItem(),
							MaxorBoots.getItem(),
							new ItemStack(Material.TOTEM_OF_UNDYING));
					sendMessage(commandSender, player, "Combat Items");
					return true;
				}
				case "ingredient/withers" -> {
					player.getInventory().addItem(
							ShadowWarp.getItem(),
							Implosion.getItem(),
							WitherShield.getItem(),
							Handle.getItem(),
							MaxorSecrets.getItem(),
							StormSecrets.getItem(),
							GoldorSecrets.getItem(),
							NecronSecrets.getItem());
					sendMessage(commandSender, player, "Wither Ingredients");
					return true;
				}
				case "ingredient/misc" -> {
					player.getInventory().addItem(
							WardenHeart.getItem(),
							Core.getItem(),
							TessellatedPearl.getItem(),
							NullBlade.getItem(),
							BraidedFeather.getItem(),
							TarantulaSilk.getItem(),
							Viscera.getItem(),
							GiantSwordRemnant.getItem(),
							Alloy.getItem(),
							ConcentratedStone.getItem());

					sendMessage(commandSender, player, "Misc Ingredients");
					return true;
				}
				case "ingredient/mining" -> {
					player.getInventory().addItem(
							Alloy.getItem(),
							ConcentratedStone.getItem(),
							RefinedDiamond.getItem(),
							RefinedEmerald.getItem(),
							RefinedGold.getItem(),
							RefinedIron.getItem(),
							RefinedLapis.getItem(),
							RefinedNetherite.getItem(),
							RefinedRedstone.getItem());
					sendMessage(commandSender, player, "Mining Ingredients");
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
							GiantZombieFlesh.getItem(),
							HighlyInfuriatedWitherSkeletonSpawnEgg.getItem());
					sendMessage(commandSender, player, "Summon Items");
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
					meta.addStoredEnchant(Enchantment.SWEEPING_EDGE, 4, true);
					meta.addStoredEnchant(Enchantment.FIRE_ASPECT, 2, true);
					meta.addStoredEnchant(Enchantment.KNOCKBACK, 2, true);
					meta.addStoredEnchant(Enchantment.PUNCH, 2, true);
					godBook.setItemMeta(meta);
					player.getInventory().addItem(godBook);
					sendMessage(commandSender, player, "The God Book");
					return true;
				}
				case "t7smite" -> {
					ItemStack godBook = new ItemStack(Material.ENCHANTED_BOOK);
					EnchantmentStorageMeta meta = (EnchantmentStorageMeta) godBook.getItemMeta();
					meta.addStoredEnchant(Enchantment.SMITE, 6, true);
					meta.addStoredEnchant(Enchantment.POWER, 7, true);
					meta.addStoredEnchant(Enchantment.LOOTING, 5, true);
					meta.addStoredEnchant(Enchantment.FORTUNE, 4, true);
					meta.addStoredEnchant(Enchantment.EFFICIENCY, 6, true);
					meta.addStoredEnchant(Enchantment.PROTECTION, 5, true);
					meta.addStoredEnchant(Enchantment.FEATHER_FALLING, 5, true);
					meta.addStoredEnchant(Enchantment.SWEEPING_EDGE, 4, true);
					meta.addStoredEnchant(Enchantment.FIRE_ASPECT, 2, true);
					meta.addStoredEnchant(Enchantment.KNOCKBACK, 2, true);
					meta.addStoredEnchant(Enchantment.PUNCH, 2, true);
					godBook.setItemMeta(meta);
					player.getInventory().addItem(godBook);
					sendMessage(commandSender, player, "The God Book (Smite Edition)");
					return true;
				}
				case "t7bane" -> {
					ItemStack godBook = new ItemStack(Material.ENCHANTED_BOOK);
					EnchantmentStorageMeta meta = (EnchantmentStorageMeta) godBook.getItemMeta();
					meta.addStoredEnchant(Enchantment.BANE_OF_ARTHROPODS, 6, true);
					meta.addStoredEnchant(Enchantment.POWER, 7, true);
					meta.addStoredEnchant(Enchantment.LOOTING, 5, true);
					meta.addStoredEnchant(Enchantment.FORTUNE, 4, true);
					meta.addStoredEnchant(Enchantment.EFFICIENCY, 6, true);
					meta.addStoredEnchant(Enchantment.PROTECTION, 5, true);
					meta.addStoredEnchant(Enchantment.FEATHER_FALLING, 5, true);
					meta.addStoredEnchant(Enchantment.SWEEPING_EDGE, 4, true);
					meta.addStoredEnchant(Enchantment.FIRE_ASPECT, 2, true);
					meta.addStoredEnchant(Enchantment.KNOCKBACK, 2, true);
					meta.addStoredEnchant(Enchantment.PUNCH, 2, true);
					godBook.setItemMeta(meta);
					player.getInventory().addItem(godBook);
					sendMessage(commandSender, player, "The God Book (Bane of Arthropods Edition)");
					return true;
				}
				default -> {
					commandSender.sendMessage("Invalid Item Set profided.\nSets: " + ITEM_SETS);
					return false;
				}
			}
		}
		return true;
	}
}
