package commands;

import misc.SimilarData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Bukkit.getServer;

public class GetOPItems implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if(commandSender.isOp()) {
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
						ItemStack wardenHelmet = SimilarData.wardenHelmet();
						ItemStack necronElytra = SimilarData.necronElytra();
						ItemStack goldorLeggings = SimilarData.goldorLeggings();
						ItemStack maxorBoots = SimilarData.maxorBoots();
						ItemStack scylla = SimilarData.scylla();
						ItemStack term = SimilarData.term();
						ItemStack aotv = SimilarData.AOTV();
						ItemStack iceSpray = SimilarData.iceSpray();
						ItemStack wandOfAtonement = SimilarData.wandOfAtonement();
						ItemStack holyIce = SimilarData.holyIce();
						ItemStack claymore = SimilarData.claymore();

						player.getInventory().addItem(scylla, aotv, iceSpray, claymore, term, wandOfAtonement, holyIce, wardenHelmet, necronElytra, goldorLeggings, maxorBoots);
						commandSender.sendMessage("Successfully gave " + player.getName() + " Combat Items");
						return true;
					}
					case "ingredient" -> {
						player.getInventory().addItem(SimilarData.shadowWarp(),
								SimilarData.implosion(),
								SimilarData.witherShield(),
								SimilarData.handle(),
								SimilarData.maxorSecrets(),
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
						player.getInventory().addItem(SimilarData.supRemnant(),
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
