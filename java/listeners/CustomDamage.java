package listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

public class CustomDamage implements Listener {
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player p) {
			double damage = e.getDamage();
			Random random = new Random();
			try {
				if(e.getEntity() instanceof IronGolem golem && Objects.requireNonNull(e.getEntity().getCustomName()).contains("meloG norI")) {
					if(damage > 5.0) {
						e.setCancelled(true);
						golem.setTarget(p);
						if(golem.getHealth() + (damage - 5.0) > 200) {
							golem.setHealth(200);
							p.damage(damage / 2, golem);
							p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The meloG norI is at full health and has REFLECTED " + damage / 2 + " + back to you!");
						} else {
							golem.setHealth(golem.getHealth() + (damage - 5.0));
							p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You have done too much damage to the meloG norI!  It has HEALED ITSELF!");
						}
					}
				} else if(e.getEntity() instanceof Spider spider && Objects.requireNonNull(e.getEntity().getCustomName()).contains("Tarantula Broodfather")) {
					if(damage > 2.0) {
						e.setDamage(2.0);
					}
					Location l = spider.getLocation();
					Location l2 = spider.getLocation();
					Vector added = new Vector(random.nextInt(33) - 16, 0, random.nextInt(33) - 16);
					l.add(added);
					l2.add(added);
					while(true) {
						if(l.getY() > 319 || l2.getY() < -63) {
							p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Tarantula Broodfather could not find a spot to teleport to!");
							break;
						} else if(l.getBlock().isEmpty()) {
							spider.teleport(l);
							p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Tarantula Broodfather has scurried away!");
							break;
						} else if(l2.getBlock().isEmpty()) {
							spider.teleport(l2);
							p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Tarantula Broodfather has scurried away!");
							break;
						}
						l.add(0, 1, 0);
						l2.add(0, -1, 0);
					}
					spider.getWorld().playSound(spider.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
				}
			} catch(NullPointerException exception) {
				// do nothing
			}
		}
	}
}
