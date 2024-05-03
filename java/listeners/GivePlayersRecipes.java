package listeners;

import misc.AddRecipes;
import misc.Plugin;
import misc.SimilarData;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GivePlayersRecipes implements Listener {
	private PlayerInventory inventory;

	public void setItem(int i, ItemStack newItem) {
		ItemStack oldItem = Objects.requireNonNull(inventory.getItem(i));
		try {
			Map<Enchantment, Integer> enchants = oldItem.getEnchantments();
			newItem.addUnsafeEnchantments(enchants);
		} catch(Exception exception) {
			// nothing here lol
		}
		newItem.setAmount(oldItem.getAmount());
		inventory.setItem(i, newItem);
	}

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
		p.removeScoreboardTag("SalvationCooldown");
		p.removeScoreboardTag("WandCooldown");
		p.removeScoreboardTag("IceSprayCooldown");

		this.inventory = p.getInventory();
		for(int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			try {
				if(item.hasItemMeta()) {
					if(item.getItemMeta().hasLore()) {
						List<String> lore = item.getItemMeta().getLore();
						switch(lore.get(0)) {
							case "skyblock/combat/aspect_of_the_void" -> setItem(i, SimilarData.AOTV());
							case "skyblock/combat/scylla" -> setItem(i, SimilarData.scylla());
							case "skyblock/combat/terminator" -> setItem(i, SimilarData.term());
							case "skyblock/combat/ice_spray_wand" -> setItem(i, SimilarData.iceSpray());
							case "skyblock/combat/wand_of_restoration" ->
									setItem(i, SimilarData.wandOfRestoration());
							case "skyblock/combat/wand_of_atonement" ->
									setItem(i, SimilarData.wandOfAtonement());
							case "skyblock/combat/holy_ice" -> setItem(i, SimilarData.holyIce());
							case "skyblock/combat/dark_claymore" -> setItem(i, SimilarData.claymore());
							case "skyblock/combat/warden_helmet" -> setItem(i, SimilarData.wardenHelmet());
							case "skyblock/combat/necron_elytra" -> setItem(i, SimilarData.necronElytra());
							case "skyblock/combat/goldor_pants" -> setItem(i, SimilarData.goldorLeggings());
							case "skyblock/combat/maxor_boots" -> setItem(i, SimilarData.maxorBoots());
							case "skyblock/ingredient/shadow_warp" -> setItem(i, SimilarData.shadowWarp());
							case "skyblock/ingredient/implosion" -> setItem(i, SimilarData.implosion());
							case "skyblock/ingredient/wither_shield" ->
									setItem(i, SimilarData.witherShield());
							case "skyblock/ingredient/necron_handle" -> setItem(i, SimilarData.handle());
							case "skyblock/ingredient/giant_sword_remnant" ->
									setItem(i, SimilarData.giantSwordRemnant());
							case "skyblock/ingredient/maxor_secrets" ->
									setItem(i, SimilarData.maxorSecrets());
							case "skyblock/ingredient/storm_secrets" ->
									setItem(i, SimilarData.stormSecrets());
							case "skyblock/ingredient/goldor_secrets" ->
									setItem(i, SimilarData.goldorSecrets());
							case "skyblock/ingredient/necron_secrets" ->
									setItem(i, SimilarData.necronSecrets());
							case "skyblock/ingredient/warden_heart" -> setItem(i, SimilarData.wardenHeart());
							case "skyblock/ingredient/judgement_core" -> setItem(i, SimilarData.core());
							case "skyblock/ingredient/tessellated_pearl" ->
									setItem(i, SimilarData.tessellatedPearl());
							case "skyblock/ingredient/null_blade" -> setItem(i, SimilarData.nullBlade());
							case "skyblock/ingredient/braided_feather" ->
									setItem(i, SimilarData.braidedFeather());
							case "skyblock/ingredient/tarantula_silk" -> setItem(i, SimilarData.taraSilk());
							case "skyblock/ingredient/revenant_viscera" -> setItem(i, SimilarData.viscera());
							case "skyblock/summon/superior_remnant" -> setItem(i, SimilarData.supRemnant());
							case "skyblock/summon/corrupt_pearl" -> setItem(i, SimilarData.corruptedPearl());
							case "skyblock/summon/antimatter" -> setItem(i, SimilarData.antimatter());
							case "skyblock/summon/omega_egg" -> setItem(i, SimilarData.omegaEgg());
							case "skyblock/summon/spider_relic" -> setItem(i, SimilarData.spiderRelic());
							case "skyblock/summon/atoned_flesh" -> setItem(i, SimilarData.atonedFlesh());
							case "skyblock/summon/giant_flesh" -> setItem(i, SimilarData.giantZombieFlesh());
						}
					}
				}
			} catch(Exception exception) {
				// nothing here
			}
		}
	}
}