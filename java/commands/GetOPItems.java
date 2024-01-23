package commands;

import misc.SimilarData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
						// WARDEN HELMET
						ItemStack wardenHelmet = SimilarData.wardenHelmet();
						wardenHelmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
						wardenHelmet.addEnchantment(Enchantment.DURABILITY, 3);
						wardenHelmet.addEnchantment(Enchantment.MENDING, 1);
						wardenHelmet.addEnchantment(Enchantment.THORNS, 3);
						wardenHelmet.addEnchantment(Enchantment.OXYGEN, 3);
						wardenHelmet.addEnchantment(Enchantment.WATER_WORKER, 1);

						// NECRON'S ELYTRA
						ItemStack necronElytra = SimilarData.necronElytra()	;
						necronElytra.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
						necronElytra.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
						necronElytra.addUnsafeEnchantment(Enchantment.MENDING, 1);
						necronElytra.addUnsafeEnchantment(Enchantment.THORNS, 3);

						// LEGGINGS
						ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
						leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
						leggings.addEnchantment(Enchantment.DURABILITY, 3);
						leggings.addEnchantment(Enchantment.MENDING, 1);
						leggings.addEnchantment(Enchantment.THORNS, 3);
						leggings.addEnchantment(Enchantment.SWIFT_SNEAK, 3);

						// MAXOR BOOTS
						ItemStack maxorBoots = SimilarData.maxorBoots();
						maxorBoots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
						maxorBoots.addEnchantment(Enchantment.DURABILITY, 3);
						maxorBoots.addEnchantment(Enchantment.MENDING, 1);
						maxorBoots.addEnchantment(Enchantment.THORNS, 3);
						maxorBoots.addEnchantment(Enchantment.DEPTH_STRIDER, 3);
						maxorBoots.addEnchantment(Enchantment.SOUL_SPEED, 3);
						maxorBoots.addEnchantment(Enchantment.PROTECTION_FALL, 4);

						// SCYLLA
						ItemStack scylla = SimilarData.scylla();
						scylla.addEnchantment(Enchantment.DAMAGE_ALL, 5);
						scylla.addEnchantment(Enchantment.DURABILITY, 3);
						scylla.addEnchantment(Enchantment.MENDING, 1);
						scylla.addEnchantment(Enchantment.FIRE_ASPECT, 2);
						scylla.addEnchantment(Enchantment.LOOT_BONUS_MOBS, 3);

						// TERMINATOR
						ItemStack term = SimilarData.term();
						term.addEnchantment(Enchantment.ARROW_DAMAGE, 5);
						term.addEnchantment(Enchantment.DURABILITY, 3);
						term.addEnchantment(Enchantment.MENDING, 1);
						term.addEnchantment(Enchantment.ARROW_FIRE, 1);

						// ASPECT OF THE VOID
						ItemStack aotv = SimilarData.AOTV();
						aotv.addEnchantment(Enchantment.DIG_SPEED, 5);
						aotv.addEnchantment(Enchantment.DURABILITY, 3);
						aotv.addEnchantment(Enchantment.MENDING, 1);
						aotv.addEnchantment(Enchantment.SILK_TOUCH, 1);

						// ICE SPRAY
						ItemStack iceSpray = SimilarData.iceSpray();

						// WAND OF ATONEMENT
						ItemStack wandOfAtonement = SimilarData.wandOfAtonement();

						// CLAYMORE
						ItemStack claymore = SimilarData.claymore();
						claymore.addEnchantment(Enchantment.DAMAGE_ALL, 5);
						claymore.addEnchantment(Enchantment.DURABILITY, 3);
						claymore.addEnchantment(Enchantment.MENDING, 1);
						claymore.addEnchantment(Enchantment.FIRE_ASPECT, 2);
						claymore.addEnchantment(Enchantment.LOOT_BONUS_MOBS, 3);

						player.getInventory().addItem(scylla, aotv, iceSpray, claymore, term, wandOfAtonement, wardenHelmet, necronElytra, leggings, maxorBoots);
						commandSender.sendMessage("Successfully gave " + player.getName() + " Combat Items");
						return true;
					}
					case "ingredient" -> {
						player.getInventory().addItem(SimilarData.shadowWarp(),
								SimilarData.implosion(),
								SimilarData.witherShield(),
								SimilarData.handle(),
								SimilarData.maxorSecrets(),
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
