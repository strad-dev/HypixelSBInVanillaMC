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

@SuppressWarnings({"DataFlowIssue"})
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
						// HELMET
						ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
						helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
						helmet.addEnchantment(Enchantment.DURABILITY, 3);
						helmet.addEnchantment(Enchantment.MENDING, 1);
						helmet.addEnchantment(Enchantment.THORNS, 3);
						helmet.addEnchantment(Enchantment.OXYGEN, 3);
						helmet.addEnchantment(Enchantment.WATER_WORKER, 1);

						// CHESTPLATE
						ItemStack chestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
						chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
						chestplate.addEnchantment(Enchantment.DURABILITY, 3);
						chestplate.addEnchantment(Enchantment.MENDING, 1);
						chestplate.addEnchantment(Enchantment.THORNS, 3);

						// LEGGINGS
						ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
						leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
						leggings.addEnchantment(Enchantment.DURABILITY, 3);
						leggings.addEnchantment(Enchantment.MENDING, 1);
						leggings.addEnchantment(Enchantment.THORNS, 3);
						leggings.addEnchantment(Enchantment.SWIFT_SNEAK, 3);

						// BOOTS
						ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
						boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
						boots.addEnchantment(Enchantment.DURABILITY, 3);
						boots.addEnchantment(Enchantment.MENDING, 1);
						boots.addEnchantment(Enchantment.THORNS, 3);
						boots.addEnchantment(Enchantment.DEPTH_STRIDER, 3);
						boots.addEnchantment(Enchantment.SOUL_SPEED, 3);
						boots.addEnchantment(Enchantment.PROTECTION_FALL, 4);

						// ELYTRA
						ItemStack elytra = new ItemStack(Material.ELYTRA);
						elytra.addEnchantment(Enchantment.DURABILITY, 3);
						elytra.addEnchantment(Enchantment.MENDING, 1);

						// SCYLLA
						ItemStack scylla = new ItemStack(Material.NETHERITE_SWORD);
						scylla.addEnchantment(Enchantment.DAMAGE_ALL, 5);
						scylla.addEnchantment(Enchantment.DURABILITY, 3);
						scylla.addEnchantment(Enchantment.MENDING, 1);
						scylla.addEnchantment(Enchantment.FIRE_ASPECT, 2);
						scylla.addEnchantment(Enchantment.LOOT_BONUS_MOBS, 3);
						scylla.setItemMeta(SimilarData.scyllaMeta(scylla.getItemMeta()));

						// TERMINATOR
						ItemStack term = new ItemStack(Material.BOW);
						term.addEnchantment(Enchantment.ARROW_DAMAGE, 5);
						term.addEnchantment(Enchantment.DURABILITY, 3);
						term.addEnchantment(Enchantment.MENDING, 1);
						term.addEnchantment(Enchantment.ARROW_FIRE, 1);
						term.setItemMeta(SimilarData.termMeta(term.getItemMeta()));

						// ASPECT OF THE VOID
						ItemStack aotv = new ItemStack(Material.NETHERITE_SHOVEL);
						aotv.addEnchantment(Enchantment.DIG_SPEED, 5);
						aotv.addEnchantment(Enchantment.DURABILITY, 3);
						aotv.addEnchantment(Enchantment.MENDING, 1);
						aotv.addEnchantment(Enchantment.SILK_TOUCH, 1);
						aotv.setItemMeta(SimilarData.AOTVMeta(aotv.getItemMeta()));

						// ICE SPRAY
						ItemStack iceSpray = new ItemStack(Material.STICK);
						iceSpray.setItemMeta(SimilarData.iceSprayMeta(iceSpray.getItemMeta()));

						// CLAYMORE
						ItemStack claymore = new ItemStack(Material.STONE_SWORD);
						claymore.addEnchantment(Enchantment.DAMAGE_ALL, 5);
						claymore.addEnchantment(Enchantment.DURABILITY, 3);
						claymore.addEnchantment(Enchantment.MENDING, 1);
						claymore.addEnchantment(Enchantment.FIRE_ASPECT, 2);
						claymore.addEnchantment(Enchantment.LOOT_BONUS_MOBS, 3);
						claymore.setItemMeta(SimilarData.claymoreMeta(claymore.getItemMeta()));

						player.getInventory().addItem(scylla, aotv, iceSpray, claymore, term, helmet, chestplate, leggings, boots, elytra);
						commandSender.sendMessage("Successfully gave " + player.getName() + " Combat Items");
						return true;
					}
					case "ingredient" -> {
						// SHADOW WARP
						ItemStack shadowWarp = new ItemStack(Material.ENCHANTED_BOOK);
						shadowWarp.setItemMeta(SimilarData.shadowWarpMeta(shadowWarp.getItemMeta()));
						shadowWarp.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

						// IMPLOSION
						ItemStack implosion = new ItemStack(Material.ENCHANTED_BOOK);
						implosion.setItemMeta(SimilarData.implosionMeta(implosion.getItemMeta()));
						implosion.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

						// WITHER SHIELD
						ItemStack witherShield = new ItemStack(Material.ENCHANTED_BOOK);
						witherShield.setItemMeta(SimilarData.witherShieldMeta(witherShield.getItemMeta()));
						witherShield.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

						// NECRON'S HANDLE
						ItemStack handle = new ItemStack(Material.STICK);
						handle.setItemMeta(SimilarData.necronHandleMeta(handle.getItemMeta()));
						handle.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

						// JUDGEMENT CORE
						ItemStack core = new ItemStack(Material.CHISELED_QUARTZ_BLOCK);
						core.setItemMeta(SimilarData.coreMeta(core.getItemMeta()));
						core.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

						// TESSELLATED ENDER PEARL
						ItemStack tessellated = new ItemStack(Material.ENDER_PEARL);
						tessellated.setItemMeta(SimilarData.tessellatedPearlMeta(tessellated.getItemMeta()));
						tessellated.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

						// NULL BLADE
						ItemStack nullBlade = new ItemStack(Material.SHEARS);
						nullBlade.setItemMeta(SimilarData.nullBladeBeta(nullBlade.getItemMeta()));
						nullBlade.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

						// BRAIDED FEATHER
						ItemStack braidedFeather = new ItemStack(Material.FEATHER);
						braidedFeather.setItemMeta(SimilarData.braidedFeatherMeta(braidedFeather.getItemMeta()));
						braidedFeather.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

						// TARANTULA SILK
						ItemStack taraSilk = new ItemStack(Material.COBWEB);
						taraSilk.setItemMeta(SimilarData.taraSilkMeta(taraSilk.getItemMeta()));
						taraSilk.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

						player.getInventory().addItem(shadowWarp, implosion, witherShield, handle, core, tessellated, nullBlade, braidedFeather, taraSilk);
						commandSender.sendMessage("Successfully gave " + player.getName() + " Ingredients");
						return true;
					}
					case "summon" -> {
						// SUP REMNANT
						ItemStack supRemnant = new ItemStack(Material.QUARTZ);
						supRemnant.setItemMeta(SimilarData.supRemnantMeta(supRemnant.getItemMeta()));
						supRemnant.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

						// CORRUPTED PEARL
						ItemStack corruptPearl = new ItemStack(Material.ENDER_EYE);
						corruptPearl.setItemMeta(SimilarData.corruptedPearlMeta(corruptPearl.getItemMeta()));
						corruptPearl.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

						// ANTIMATTER
						ItemStack antimatter = new ItemStack(Material.WARPED_FUNGUS);
						antimatter.setItemMeta(SimilarData.antimatterMeta(antimatter.getItemMeta()));
						antimatter.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

						// OMEGA EGG
						ItemStack omegaEgg = new ItemStack(Material.EGG);
						omegaEgg.setItemMeta(SimilarData.omegaEggMeta(omegaEgg.getItemMeta()));
						omegaEgg.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

						// SPIDER RELIC
						ItemStack spiderRelic = new ItemStack(Material.FERMENTED_SPIDER_EYE);
						spiderRelic.setItemMeta(SimilarData.spiderRelicMeta(spiderRelic.getItemMeta()));
						spiderRelic.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

						player.getInventory().addItem(supRemnant, corruptPearl, antimatter, omegaEgg, spiderRelic);
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
