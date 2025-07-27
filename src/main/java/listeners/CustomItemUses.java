package listeners;

import items.summonItems.SummonItem;
import misc.PluginUtils;
import mobs.CustomMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("DataFlowIssue")
public class CustomItemUses implements Listener {

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		PlayerInventory inventory = p.getInventory();
		ItemStack item = inventory.getItemInMainHand();
		String id;
		try {
			id = item.getItemMeta().getLore().getFirst();
		} catch(Exception exception) {
			id = "";
		}

		// logic for the Maxor fight
		switch(e.getRightClicked()) {
			case EnderCrystal crystal when crystal.getScoreboardTags().contains("SkyblockBoss") -> {
				crystal.remove();
				p.addScoreboardTag("HasCrystal");
				p.sendMessage(ChatColor.YELLOW + "You have picked up an Energy Crystal!");
			}
			case Wither wither when wither.getScoreboardTags().contains("Maxor") && p.getScoreboardTags().contains("HasCrystal") -> {
				wither.removeScoreboardTag("Invulnerable");
				Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "MASTER Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": OUCH!  HOW DID YOU FIGURE IT OUT???.");
				List<EntityType> immune = new ArrayList<>();
				immune.add(EntityType.WITHER_SKELETON);
				PluginUtils.spawnTNT(wither, wither.getLocation(), 0, 8, 10, immune);
				p.removeScoreboardTag("HasCrystal");
			}
			case Mob entity when e.getHand().equals(EquipmentSlot.HAND) -> {
				CustomMob mob = SummonItem.spawnABoss(id);
				String newName;
				if(mob == null) {
					if(item.getType().equals(Material.NAME_TAG)) {
						e.setCancelled(true);
						newName = item.getItemMeta().getDisplayName();
					} else {
						return;
					}
				} else {
					if(entity.getScoreboardTags().contains("SkyblockBoss")) {
						return;
					}
					newName = mob.onSpawn(p, entity);
				}
				int health = (int) (entity.getHealth() + entity.getAbsorptionAmount());
				int maxHealth = (int) Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).getValue();
				newName += " " + ChatColor.RED + "❤ " + ChatColor.YELLOW + health + "/" + maxHealth;
				// " ♥ 20/20";
				entity.setCustomName(newName);
				entity.setCustomNameVisible(true);

				if(!p.getGameMode().equals(GameMode.CREATIVE)) {
					item.setAmount(item.getAmount() - 1);
				}
			}
			default -> {
			}
		}
	}
}