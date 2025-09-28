package listeners;

import items.armor.*;
import items.ingredients.mining.*;
import items.ingredients.misc.*;
import items.ingredients.witherLords.*;
import items.misc.*;
import items.summonItems.*;
import items.weapons.Claymore;
import items.weapons.Scylla;
import items.weapons.Terminator;
import misc.AddRecipes;
import misc.BossBarManager;
import misc.Plugin;
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
		BossBarManager.addPlayerToActiveBars(p);
		p.discoverRecipes(AddRecipes.returnRecipes(Plugin.getInstance()));
		p.sendMessage(" " + ChatColor.BLUE + ChatColor.BOLD + ChatColor.MAGIC + "E" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " Hypixel SkyBlock in Vanilla Minecraft Plugin " + ChatColor.MAGIC + "E" + ChatColor.RESET + "\n" +
				ChatColor.BLACK + "---" + ChatColor.DARK_BLUE + "---" + ChatColor.DARK_GREEN + "---" + ChatColor.DARK_AQUA + "---" + ChatColor.DARK_RED + "---" + ChatColor.DARK_PURPLE + "---" + ChatColor.GOLD + "---" + ChatColor.GRAY + "---" +
				ChatColor.DARK_GRAY + "---" + ChatColor.BLUE + "---" + ChatColor.GREEN + "---" + ChatColor.AQUA + "---" + ChatColor.RED + "---" + ChatColor.LIGHT_PURPLE + "---" + ChatColor.YELLOW + "---" + ChatColor.WHITE + "---\n" +
				ChatColor.RESET + ChatColor.AQUA + ChatColor.BOLD + "GITHUB: " + ChatColor.RESET + "https://github.com/strad-dev/HypixelSBInVanillaMC \n\n" +
				ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + "DISCORD: " + ChatColor.RESET + "https://discord.gg/gNfPwa8 \n\n" +
				ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + "YOUTUBE PLAYLIST: " + ChatColor.RESET + "https://www.youtube.com/playlist?list=PLbM7AgwT4xmHo3ySkIA4jIEgvn8TzLQb0 \n\n" +
				ChatColor.RESET + ChatColor.YELLOW + "Found a bug?  Have a suggestion?  Make a ticket in the Github or contact stradivariusviolin on Discord.");
		p.removeScoreboardTag("AbilityCooldown");
		p.removeScoreboardTag("TerminatorCooldown");
		p.removeScoreboardTag("IceSprayed");
		p.removeScoreboardTag("SalvationCooldown");
		p.removeScoreboardTag("WandCooldown");
		p.removeScoreboardTag("IceSprayCooldown");
		p.removeScoreboardTag("IceCooldown");
		p.removeScoreboardTag("TacCooldown");
		p.removeScoreboardTag("GyroCooldown");
		p.removeScoreboardTag("WitherShield");
		p.removeScoreboardTag("HolyIce");

		this.inventory = p.getInventory();
		for(int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			try {
				if(item.hasItemMeta()) {
					if(item.getItemMeta().hasLore()) {
						List<String> lore = item.getItemMeta().getLore();
						Enchantment ench = Enchantment.SHARPNESS;
						if(item.getEnchantments().containsKey(Enchantment.SMITE)) {
							ench = Enchantment.SMITE;
						} else if(item.getEnchantments().containsKey(Enchantment.BANE_OF_ARTHROPODS)) {
							ench = Enchantment.BANE_OF_ARTHROPODS;
						}
						switch(lore.getFirst()) {
							case "skyblock/combat/aspect_of_the_void" -> setItem(i, AOTV.getItem());
							case "skyblock/combat/scylla" ->
									setItem(i, Scylla.getItem(ench, item.getEnchantmentLevel(ench)));
							case "skyblock/combat/terminator" ->
									setItem(i, Terminator.getItem(item.getEnchantmentLevel(Enchantment.POWER)));
							case "skyblock/combat/ice_spray_wand" -> setItem(i, IceSpray.getItem());
							case "skyblock/combat/wand_of_restoration" -> setItem(i, WandOfRestoration.getItem());
							case "skyblock/combat/wand_of_atonement" -> setItem(i, WandOfAtonement.getItem());
							case "skyblock/combat/divan_pickaxe" -> setItem(i, DivanPickaxe.getItem());
							case "skyblock/combat/holy_ice" -> setItem(i, HolyIce.getItem());
							case "skyblock/combat/bonzo_staff" -> setItem(i, BonzoStaff.getItem());
							case "skyblock/combat/tactical_insertion" -> setItem(i, TacticalInsertion.getItem());
							case "skyblock/combat/gyro" -> setItem(i, GyrokineticWand.getItem());
							case "skyblock/combat/dark_claymore" ->
									setItem(i, Claymore.getItem(ench, item.getEnchantmentLevel(ench)));
							case "skyblock/combat/warden_helmet" -> setItem(i, WardenHelmet.getItem());
							case "skyblock/combat/wither_king_crown" -> setItem(i, WitherKingCrown.getItem());
							case "skyblock/combat/necron_elytra" -> setItem(i, NecronElytra.getItem());
							case "skyblock/combat/goldor_pants" -> setItem(i, GoldorLeggings.getItem());
							case "skyblock/combat/maxor_boots" -> setItem(i, MaxorBoots.getItem());
							case "skyblock/ingredient/shadow_warp" -> setItem(i, ShadowWarp.getItem());
							case "skyblock/ingredient/implosion" -> setItem(i, Implosion.getItem());
							case "skyblock/ingredient/wither_shield" -> setItem(i, WitherShield.getItem());
							case "skyblock/ingredient/necron_handle" -> setItem(i, Handle.getItem());
							case "skyblock/ingredient/giant_sword_remnant" -> setItem(i, GiantSwordRemnant.getItem());
							case "skyblock/ingredient/maxor_secrets" -> setItem(i, MaxorSecrets.getItem());
							case "skyblock/ingredient/storm_secrets" -> setItem(i, StormSecrets.getItem());
							case "skyblock/ingredient/goldor_secrets" -> setItem(i, GoldorSecrets.getItem());
							case "skyblock/ingredient/necron_secrets" -> setItem(i, NecronSecrets.getItem());
							case "skyblock/ingredient/warden_heart" -> setItem(i, WardenHeart.getItem());
							case "skyblock/ingredient/judgement_core" -> setItem(i, Core.getItem());
							case "skyblock/ingredient/tessellated_pearl" -> setItem(i, TessellatedPearl.getItem());
							case "skyblock/ingredient/null_blade" -> setItem(i, NullBlade.getItem());
							case "skyblock/ingredient/braided_feather" -> setItem(i, BraidedFeather.getItem());
							case "skyblock/ingredient/tarantula_silk" -> setItem(i, TarantulaSilk.getItem());
							case "skyblock/ingredient/revenant_viscera" -> setItem(i, Viscera.getItem());
							case "skyblock/ingredient/alloy" -> setItem(i, Alloy.getItem());
							case "skyblock/ingredient/concentrated_stone" -> setItem(i, ConcentratedStone.getItem());
							case "skyblock/ingredient/refined_diamond" -> setItem(i, RefinedDiamond.getItem());
							case "skyblock/ingredient/refined_emerald" -> setItem(i, RefinedEmerald.getItem());
							case "skyblock/ingredient/refined_gold" -> setItem(i, RefinedGold.getItem());
							case "skyblock/ingredient/refined_iron" -> setItem(i, RefinedIron.getItem());
							case "skyblock/ingredient/refined_lapis" -> setItem(i, RefinedLapis.getItem());
							case "skyblock/ingredient/refined_netherite" -> setItem(i, RefinedNetherite.getItem());
							case "skyblock/ingredient/refined_redstone" -> setItem(i, RefinedRedstone.getItem());
							case "skyblock/ingredient/enchantment_upgrader" ->
									setItem(i, EnchantmentUpgrader.getItem());
							case "skyblock/summon/superior_remnant" -> setItem(i, SuperiorRemnant.getItem());
							case "skyblock/summon/corrupt_pearl" -> setItem(i, CorruptPearl.getItem());
							case "skyblock/summon/antimatter" -> setItem(i, Antimatter.getItem());
							case "skyblock/summon/omega_egg" -> setItem(i, OmegaEgg.getItem());
							case "skyblock/summon/spider_relic" -> setItem(i, SpiderRelic.getItem());
							case "skyblock/summon/atoned_flesh" -> setItem(i, AtonedFlesh.getItem());
							case "skyblock/summon/giant_flesh" -> setItem(i, GiantZombieFlesh.getItem());
						}
					}
				}
			} catch(Exception exception) {
				// nothing here
			}
		}
	}
}