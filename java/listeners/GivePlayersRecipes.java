package listeners;

import misc.AddRecipes;
import misc.Plugin;
import misc.SimilarData;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class GivePlayersRecipes implements Listener {
	@SuppressWarnings("DataFlowIssue")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().discoverRecipes(AddRecipes.returnRecipes(Plugin.getInstance()));
		e.getPlayer().sendMessage(" " + ChatColor.BLUE + ChatColor.BOLD + ChatColor.MAGIC + "E" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " Hypixel SkyBlock in Vanilla Minecraft Plugin " + ChatColor.MAGIC + "E" + ChatColor.RESET + "\n" +
				ChatColor.BLACK + "---" + ChatColor.DARK_BLUE + "---" + ChatColor.DARK_GREEN + "---" + ChatColor.DARK_AQUA + "---" + ChatColor.DARK_RED + "---" + ChatColor.DARK_PURPLE + "---" + ChatColor.GOLD + "---" + ChatColor.GRAY + "---" +
				ChatColor.DARK_GRAY + "---" + ChatColor.BLUE + "---" + ChatColor.GREEN + "---" + ChatColor.AQUA + "---" + ChatColor.RED + "---" + ChatColor.LIGHT_PURPLE + "---" + ChatColor.YELLOW + "---" + ChatColor.WHITE + "---\n" +
				ChatColor.RESET + ChatColor.AQUA + ChatColor.BOLD + "DOCUMENTATION: " + ChatColor.RESET + "https://docs.google.com/document/d/1UX1amfhHxzVG-SChIbLrAEgsXr6ITOVU-xCh23qaguM/edit?usp=sharing \n\n" +
				ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + "DISCORD: " + ChatColor.RESET + "https://discord.gg/gNfPwa8 \n\n" +
				ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + "YOUTUBE PLAYLIST: " + ChatColor.RESET + "https://www.youtube.com/playlist?list=PLbM7AgwT4xmHo3ySkIA4jIEgvn8TzLQb0");

		PlayerInventory inventory = e.getPlayer().getInventory();
		for(int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			try {
				if(item.hasItemMeta()) {
					if(item.getItemMeta().hasLore()) {
						List<String> lore = item.getItemMeta().getLore();
						switch(lore.get(0)) {
							case "skyblock/combat/aspect_of_the_void" -> inventory.setItem(i, SimilarData.AOTV());
							case "skyblock/combat/scylla" -> inventory.setItem(i, SimilarData.scylla());
							case "skyblock/combat/terminator" -> inventory.setItem(i, SimilarData.term());
							case "skyblock/combat/ice_spray_wand" -> inventory.setItem(i, SimilarData.iceSpray());
							case "skyblock/combat/wand_of_restoration" -> inventory.setItem(i, SimilarData.wandOfRestoration());
							case "skyblock/combat/wand_of_atonement" -> inventory.setItem(i, SimilarData.wandOfAtonement());
							case "skyblock/combat/holy_ice" -> inventory.setItem(i, SimilarData.holyIce());
							case "skyblock/combat/dark_claymore" -> inventory.setItem(i, SimilarData.claymore());
							case "skyblock/combat/warden_helmet" -> inventory.setItem(i, SimilarData.wardenHelmet());
							case "skyblock/combat/necron_elytra" -> inventory.setItem(i, SimilarData.necronElytra());
							case "skyblock/combat/goldor_pants" -> inventory.setItem(i, SimilarData.goldorLeggings());
							case "skyblock/combat/maxor_boots" -> inventory.setItem(i, SimilarData.maxorBoots());
							case "skyblock/ingredient/shadow_warp" -> inventory.setItem(i, SimilarData.shadowWarp());
							case "skyblock/ingredient/implosion" -> inventory.setItem(i, SimilarData.implosion());
							case "skyblock/ingredient/wither_shield" -> inventory.setItem(i, SimilarData.witherShield());
							case "skyblock/ingredient/necron_handle" -> inventory.setItem(i, SimilarData.handle());
							case "skyblock/ingredient/maxor_secrets" -> inventory.setItem(i, SimilarData.maxorSecrets());
							case "skyblock/ingredient/goldor_secrets" -> inventory.setItem(i, SimilarData.goldorSecrets());
							case "skyblock/ingredient/necron_secrets" -> inventory.setItem(i, SimilarData.necronSecrets());
							case "skyblock/ingredient/warden_heart" -> inventory.setItem(i, SimilarData.wardenHeart());
							case "skyblock/ingredient/judgement_core" -> inventory.setItem(i, SimilarData.core());
							case "skyblock/ingredient/tessellated_pearl" -> inventory.setItem(i, SimilarData.tessellatedPearl());
							case "skyblock/ingredient/null_blade" -> inventory.setItem(i, SimilarData.nullBlade());
							case "skyblock/ingredient/braided_feather" -> inventory.setItem(i, SimilarData.braidedFeather());
							case "skyblock/ingredient/tarantula_silk" -> inventory.setItem(i, SimilarData.taraSilk());
							case "skyblock/ingredient/revenant_viscera" -> inventory.setItem(i, SimilarData.viscera());
							case "skyblock/summon/superior_remnant" -> inventory.setItem(i, SimilarData.supRemnant());
							case "skyblock/summon/corrupt_pearl" -> inventory.setItem(i, SimilarData.corruptedPearl());
							case "skyblock/summon/antimatter" -> inventory.setItem(i, SimilarData.antimatter());
							case "skyblock/summon/omega_egg" -> inventory.setItem(i, SimilarData.omegaEgg());
							case "skyblock/summon/spider_relic" -> inventory.setItem(i, SimilarData.spiderRelic());
							case "skyblock/summon/atoned_flesh" -> inventory.setItem(i, SimilarData.atonedFlesh());
						}
					}
					if(item.getItemMeta().hasDisplayName()) {
						//TODO REMOVE AFTER THIS UPDATE
						String itemName = item.getItemMeta().getDisplayName();
						if(itemName.equals(ChatColor.LIGHT_PURPLE + "Aspect of the Void")) {
							inventory.setItem(i, SimilarData.AOTV());
						} else if(itemName.equals(ChatColor.LIGHT_PURPLE + "Hyperion")) {
							inventory.setItem(i, SimilarData.scylla());
						} else if(itemName.equals(ChatColor.LIGHT_PURPLE + "Terminator")) {
							inventory.setItem(i, SimilarData.term());
						} else if(itemName.equals(ChatColor.GOLD + "Ice Spray Wand")) {
							inventory.setItem(i, SimilarData.iceSpray());
						} else if(itemName.equals(ChatColor.DARK_PURPLE + "Wand of Restoration")) {
							inventory.setItem(i, SimilarData.wandOfRestoration());
						} else if(itemName.equals(ChatColor.GOLD + "Wand of Atonement")) {
							inventory.setItem(i, SimilarData.wandOfAtonement());
						} else if(itemName.equals(ChatColor.BLUE + "Holy Ice")) {
							inventory.setItem(i, SimilarData.holyIce());
						} else if(itemName.equals(ChatColor.LIGHT_PURPLE + "Dark Claymore")) {
							inventory.setItem(i, SimilarData.claymore());
						} else if(itemName.equals(ChatColor.LIGHT_PURPLE + "Warden Helmet")) {
							inventory.setItem(i, SimilarData.wardenHelmet());
						} else if(itemName.equals(ChatColor.LIGHT_PURPLE + "Necron's Elytra")) {
							inventory.setItem(i, SimilarData.necronElytra());
						} else if(itemName.equals(ChatColor.LIGHT_PURPLE + "Goldor's Leggings")) {
							inventory.setItem(i, SimilarData.goldorLeggings());
						} else if(itemName.equals(ChatColor.LIGHT_PURPLE + "Maxor's Boots")) {
							inventory.setItem(i, SimilarData.maxorBoots());
						} else if(itemName.equals(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Shadow Warp")) {
							inventory.setItem(i, SimilarData.shadowWarp());
						} else if(itemName.equals(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Implosion")) {
							inventory.setItem(i, SimilarData.implosion());
						} else if(itemName.equals(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Wither Shield")) {
							inventory.setItem(i, SimilarData.witherShield());
						} else if(itemName.equals(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Necron's Handle")) {
							inventory.setItem(i, SimilarData.handle());
						} else if(itemName.equals(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Maxor's Secrets")) {
							inventory.setItem(i, SimilarData.maxorSecrets());
						} else if(itemName.equals(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Goldor's Secrets")) {
							inventory.setItem(i, SimilarData.goldorSecrets());
						} else if(itemName.equals(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Necron's Secrets")) {
							inventory.setItem(i, SimilarData.necronSecrets());
						} else if(itemName.equals(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Warden Heart")) {
							inventory.setItem(i, SimilarData.wardenHeart());
						} else if(itemName.equals(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Judgement Core")) {
							inventory.setItem(i, SimilarData.core());
						} else if(itemName.equals(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Tessellated Ender Pearl")) {
							inventory.setItem(i, SimilarData.tessellatedPearl());
						} else if(itemName.equals(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Null Blade")) {
							inventory.setItem(i, SimilarData.nullBlade());
						} else if(itemName.equals(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Braided Feather")) {
							inventory.setItem(i, SimilarData.braidedFeather());
						} else if(itemName.equals(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Tarantula Silk")) {
							inventory.setItem(i, SimilarData.taraSilk());
						} else if(itemName.equals(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Revenant Viscera")) {
							inventory.setItem(i, SimilarData.viscera());
						} else if(itemName.equals(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Remnant of the Superior Dragon")) {
							inventory.setItem(i, SimilarData.supRemnant());
						} else if(itemName.equals(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Corrupted Pearl")) {
							inventory.setItem(i, SimilarData.corruptedPearl());
						} else if(itemName.equals(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Antimatter")) {
							inventory.setItem(i, SimilarData.antimatter());
						} else if(itemName.equals(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Omega Egg")) {
							inventory.setItem(i, SimilarData.omegaEgg());
						} else if(itemName.equals(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Spider Relic")) {
							inventory.setItem(i, SimilarData.spiderRelic());
						} else if(itemName.equals(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Atoned Flesh")) {
							inventory.setItem(i, SimilarData.atonedFlesh());
						}
					}
				}
			} catch(Exception exception) {
				// nothing here
			}
		}
	}
}