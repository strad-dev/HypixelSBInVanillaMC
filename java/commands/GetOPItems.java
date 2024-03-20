package commands;

import misc.SimilarData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Bukkit.getServer;

public class GetOPItems implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if(commandSender.isOp() || commandSender.getName().equals("Beethoven_")) {
			if(commandSender instanceof Player player) {
				try {
					player = getServer().getPlayer(strings[1]);
				} catch(Exception exception) {
					//nothing here lol
				}
				if(player == null) {
					commandSender.sendMessage(ChatColor.RED + "Invalid player provided.");
					return false;
				}

				String itemSet;
				try {
					itemSet = strings[0];
				} catch(Exception exception) {
					commandSender.sendMessage(ChatColor.RED + "You must provide a valid Item Set.\nSets: combat, summon, ingredient");
					return false;
				}

				switch(itemSet) {
					case "combat" -> {
						player.getInventory().addItem(
								SimilarData.scylla(),
								SimilarData.AOTV(),
								SimilarData.iceSpray(),
								SimilarData.claymore(),
								SimilarData.term(),
								SimilarData.wandOfAtonement(),
								SimilarData.holyIce(),
								SimilarData.wardenHelmet(),
								SimilarData.necronElytra(),
								SimilarData.goldorLeggings(),
								SimilarData.maxorBoots(),
								new ItemStack(Material.GOLDEN_CARROT, 64),
								new ItemStack(Material.TOTEM_OF_UNDYING));
						commandSender.sendMessage("Successfully gave " + player.getName() + " Combat Items");
						return true;
					}
					case "ingredient" -> {
						player.getInventory().addItem(
								SimilarData.shadowWarp(),
								SimilarData.implosion(),
								SimilarData.witherShield(),
								SimilarData.handle(),
								SimilarData.giantSwordRemnant(),
								SimilarData.maxorSecrets(),
								SimilarData.stormSecrets(),
								SimilarData.goldorSecrets(),
								SimilarData.necronSecrets(),
								SimilarData.wardenHeart(),
								SimilarData.core(),
								SimilarData.tessellatedPearl(),
								SimilarData.nullBlade(),
								SimilarData.braidedFeather(),
								SimilarData.taraSilk(),
								SimilarData.viscera());
						commandSender.sendMessage("Successfully gave " + player.getName() + " Ingredients");
						return true;
					}
					case "summon" -> {
						player.getInventory().addItem(
								SimilarData.supRemnant(),
								SimilarData.corruptedPearl(),
								SimilarData.antimatter(),
								SimilarData.omegaEgg(),
								SimilarData.spiderRelic(),
								SimilarData.atonedFlesh());
						commandSender.sendMessage("Successfully gave " + player.getName() + " Summon Items");
						return true;
					}
					default -> {
						commandSender.sendMessage("Invalid Item Set profided.\nSets: combat, summon, ingredient");
						return false;
					}
				}
			}
		}
		return true;
	}
}
