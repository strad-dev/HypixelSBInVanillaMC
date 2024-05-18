package listeners;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;

import java.util.Objects;

public class AllMobsHaveNames implements Listener {
	@EventHandler
	public void onEntitiesLoad(EntitiesLoadEvent e) {
		for(Entity temp : e.getEntities()) {
			if(temp instanceof LivingEntity entity && entity.getCustomName() == null) {
				// add health to the entity name
				int health = (int) (entity.getHealth() + entity.getAbsorptionAmount());
				int maxHealth = (int) Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();

				// " ♥ 20/20";
				entity.setCustomName(ChatColor.AQUA + entity.getName() + " " + ChatColor.RED + "❤ " + ChatColor.YELLOW + health + "/" + maxHealth);
				entity.setCustomNameVisible(true);
			}
		}
	}
}