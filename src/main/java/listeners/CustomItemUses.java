package listeners;

import items.summonItems.SummonItem;
import mobs.CustomMob;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
		if(e.getRightClicked() instanceof Mob entity && e.getHand().equals(EquipmentSlot.HAND)) {
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
			int maxHealth = (int) Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
			newName += " " + ChatColor.RED + "❤ " + ChatColor.YELLOW + health + "/" + maxHealth;
			// " ♥ 20/20";
			entity.setCustomName(newName);
			entity.setCustomNameVisible(true);

			if(!p.getGameMode().equals(GameMode.CREATIVE)) {
				item.setAmount(item.getAmount() - 1);
			}
		}
	}
}