package listeners;

import misc.AddRecipes;
import misc.Plugin;
import misc.SimilarData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
		Player p = e.getPlayer();
		p.discoverRecipes(AddRecipes.returnRecipes(Plugin.getInstance()));
		p.sendMessage(" " + ChatColor.BLUE + ChatColor.BOLD + ChatColor.MAGIC + "E" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " Hypixel SkyBlock in Vanilla Minecraft Plugin " + ChatColor.MAGIC + "E" + ChatColor.RESET + "\n" +
				ChatColor.BLACK + "---" + ChatColor.DARK_BLUE + "---" + ChatColor.DARK_GREEN + "---" + ChatColor.DARK_AQUA + "---" + ChatColor.DARK_RED + "---" + ChatColor.DARK_PURPLE + "---" + ChatColor.GOLD + "---" + ChatColor.GRAY + "---" +
				ChatColor.DARK_GRAY + "---" + ChatColor.BLUE + "---" + ChatColor.GREEN + "---" + ChatColor.AQUA + "---" + ChatColor.RED + "---" + ChatColor.LIGHT_PURPLE + "---" + ChatColor.YELLOW + "---" + ChatColor.WHITE + "---\n" +
				ChatColor.RESET + ChatColor.AQUA + ChatColor.BOLD + "DOCUMENTATION: " + ChatColor.RESET + "https://docs.google.com/document/d/1UX1amfhHxzVG-SChIbLrAEgsXr6ITOVU-xCh23qaguM/edit?usp=sharing \n\n" +
				ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + "DISCORD: " + ChatColor.RESET + "https://discord.gg/gNfPwa8 \n\n" +
				ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + "YOUTUBE PLAYLIST: " + ChatColor.RESET + "https://www.youtube.com/playlist?list=PLbM7AgwT4xmHo3ySkIA4jIEgvn8TzLQb0");
		p.removeScoreboardTag("AbilityCooldown");
		p.removeScoreboardTag("TerminatorCooldown");
		p.removeScoreboardTag("IceSprayed");

		PlayerInventory inventory = p.getInventory();
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
							case "skyblock/combat/wand_of_restoration" ->
									inventory.setItem(i, SimilarData.wandOfRestoration());
							case "skyblock/combat/wand_of_atonement" ->
									inventory.setItem(i, SimilarData.wandOfAtonement());
							case "skyblock/combat/holy_ice" -> inventory.setItem(i, SimilarData.holyIce());
							case "skyblock/combat/dark_claymore" -> inventory.setItem(i, SimilarData.claymore());
							case "skyblock/combat/warden_helmet" -> inventory.setItem(i, SimilarData.wardenHelmet());
							case "skyblock/combat/necron_elytra" -> inventory.setItem(i, SimilarData.necronElytra());
							case "skyblock/combat/goldor_pants" -> inventory.setItem(i, SimilarData.goldorLeggings());
							case "skyblock/combat/maxor_boots" -> inventory.setItem(i, SimilarData.maxorBoots());
							case "skyblock/ingredient/shadow_warp" -> inventory.setItem(i, SimilarData.shadowWarp());
							case "skyblock/ingredient/implosion" -> inventory.setItem(i, SimilarData.implosion());
							case "skyblock/ingredient/wither_shield" ->
									inventory.setItem(i, SimilarData.witherShield());
							case "skyblock/ingredient/necron_handle" -> inventory.setItem(i, SimilarData.handle());
							case "skyblock/ingredient/maxor_secrets" ->
									inventory.setItem(i, SimilarData.maxorSecrets());
							case "skyblock/ingredient/goldor_secrets" ->
									inventory.setItem(i, SimilarData.goldorSecrets());
							case "skyblock/ingredient/necron_secrets" ->
									inventory.setItem(i, SimilarData.necronSecrets());
							case "skyblock/ingredient/warden_heart" -> inventory.setItem(i, SimilarData.wardenHeart());
							case "skyblock/ingredient/judgement_core" -> inventory.setItem(i, SimilarData.core());
							case "skyblock/ingredient/tessellated_pearl" ->
									inventory.setItem(i, SimilarData.tessellatedPearl());
							case "skyblock/ingredient/null_blade" -> inventory.setItem(i, SimilarData.nullBlade());
							case "skyblock/ingredient/braided_feather" ->
									inventory.setItem(i, SimilarData.braidedFeather());
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
				}
			} catch(Exception exception) {
				// nothing here
			}
		}
	}
}