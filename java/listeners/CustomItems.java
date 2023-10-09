package listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CustomItems implements Listener {
	public boolean isBlocked(PlayerInteractEvent e, Vector constant, Location oldLocation) {
		Location newLocation = oldLocation.add(constant);
		Block b = e.getPlayer().getWorld().getBlockAt(newLocation);
		return b.getType().isSolid();
	}

	@SuppressWarnings("ConstantConditions")
	public boolean isWitherImpactSword(ItemStack item) {
		boolean hasWitherImpact = false;
		if(!item.hasItemMeta()) {
			return false;
		}
		if(!item.getItemMeta().hasLore()) {
			return false;
		}
		for(String string : Objects.requireNonNull(Objects.requireNonNull(item.getItemMeta()).getLore())) {
			if(string.contains("Wither Impact")) {
				hasWitherImpact = true;
				break;
			}
		}
		return item.getType().equals(new ItemStack(Material.NETHERITE_SWORD).getType()) && hasWitherImpact;
	}

	@SuppressWarnings("ConstantConditions")
	public boolean isTerminator(ItemStack item) {
		boolean isTerm = false;
		if(!item.hasItemMeta()) {
			return false;
		}

		if(item.getItemMeta().getDisplayName().contains("Terminator")) {
			isTerm = true;
		}

		return item.getType().equals(new ItemStack(Material.BOW).getType()) && isTerm;
	}

	@SuppressWarnings("ConstantConditions")
	public boolean isAOTV(ItemStack item) {
		boolean isAOTV = false;
		if(!item.hasItemMeta()) {
			return false;
		}

		if(item.getItemMeta().getDisplayName().contains("Aspect of the Void")) {
			isAOTV = true;
		}

		return item.getType().equals(new ItemStack(Material.NETHERITE_SHOVEL).getType()) && isAOTV;
	}

	public List<EntityType> createList() {
		List<EntityType> doNotKill = new ArrayList<>();
		doNotKill.add(EntityType.BOAT);
		doNotKill.add(EntityType.ARMOR_STAND);
		doNotKill.add(EntityType.ARROW);
		doNotKill.add(EntityType.BLOCK_DISPLAY);
		doNotKill.add(EntityType.BOAT);
		doNotKill.add(EntityType.CHEST_BOAT);
		doNotKill.add(EntityType.DRAGON_FIREBALL);
		doNotKill.add(EntityType.DROPPED_ITEM);
		doNotKill.add(EntityType.FIREBALL);
		doNotKill.add(EntityType.EGG);
		doNotKill.add(EntityType.BOAT);
		doNotKill.add(EntityType.ENDER_PEARL);
		doNotKill.add(EntityType.ENDER_SIGNAL);
		doNotKill.add(EntityType.EXPERIENCE_ORB);
		doNotKill.add(EntityType.FALLING_BLOCK);
		doNotKill.add(EntityType.FIREWORK);
		doNotKill.add(EntityType.FISHING_HOOK);
		doNotKill.add(EntityType.GLOW_ITEM_FRAME);
		doNotKill.add(EntityType.ITEM_FRAME);
		doNotKill.add(EntityType.ITEM_DISPLAY);
		doNotKill.add(EntityType.INTERACTION);
		doNotKill.add(EntityType.LEASH_HITCH);
		doNotKill.add(EntityType.LIGHTNING);
		doNotKill.add(EntityType.LLAMA_SPIT);
		doNotKill.add(EntityType.MARKER);
		doNotKill.add(EntityType.MINECART);
		doNotKill.add(EntityType.MINECART_FURNACE);
		doNotKill.add(EntityType.MINECART_CHEST);
		doNotKill.add(EntityType.MINECART_COMMAND);
		doNotKill.add(EntityType.MINECART_HOPPER);
		doNotKill.add(EntityType.MINECART_MOB_SPAWNER);
		doNotKill.add(EntityType.PAINTING);
		doNotKill.add(EntityType.PRIMED_TNT);
		doNotKill.add(EntityType.SHULKER_BULLET);
		doNotKill.add(EntityType.SMALL_FIREBALL);
		doNotKill.add(EntityType.SNOWBALL);
		doNotKill.add(EntityType.SPECTRAL_ARROW);
		doNotKill.add(EntityType.SPLASH_POTION);
		doNotKill.add(EntityType.TEXT_DISPLAY);
		doNotKill.add(EntityType.THROWN_EXP_BOTTLE);
		doNotKill.add(EntityType.TRIDENT);
		doNotKill.add(EntityType.UNKNOWN);
		doNotKill.add(EntityType.WITHER_SKULL);
		return doNotKill;
	}

	public void witherImpact(Player p, PlayerInteractEvent e) {
		Vector unitVector = p.getLocation().getDirection();
		double smallX = unitVector.getX() / 10;
		double smallY = unitVector.getY() / 10;
		double smallZ = unitVector.getZ() / 10;
		Vector constant = new Vector(smallX, smallY, smallZ);
		unitVector = new Vector(0, 0, 0);
		int i = 0;
		Location checkedLocation = p.getLocation();
		checkedLocation.setY(p.getLocation().getY() + 1.62);
		while(i < 100) {
			if(isBlocked(e, constant, checkedLocation)) {
				break;
			}
			checkedLocation.add(constant);
			unitVector.setX(unitVector.getX() + smallX);
			unitVector.setY(unitVector.getY() + smallY);
			unitVector.setZ(unitVector.getZ() + smallZ);
			i++;
		}

		Location newLocation = p.getLocation().add(unitVector);
		newLocation.setY(Math.floor(newLocation.getY() + 1.62));

		// implosion
		p.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, newLocation, 20);
		List<Entity> entities = (List<Entity>) p.getWorld().getNearbyEntities(newLocation, 10, 10, 10);
		List<EntityType> doNotKill = createList();
		Random random = new Random();
		int damaged = 0;
		int damage = 0;
		int sharpnessBonus;
		try {
			int sharpness = Objects.requireNonNull(Objects.requireNonNull(p.getInventory().getItem(e.getPlayer().getInventory().getHeldItemSlot())).getItemMeta()).getEnchants().get(Enchantment.DAMAGE_ALL);
			if(sharpness <= 4) {
				sharpnessBonus = 1;
			} else {
				sharpnessBonus = sharpness - 3;
			}
		} catch(Exception exception) {
			sharpnessBonus = 0;
		}

		int strengthBonus;
		try {
			strengthBonus = Objects.requireNonNull(p.getPotionEffect(PotionEffectType.INCREASE_DAMAGE)).getAmplifier();
		} catch(Exception exception) {
			strengthBonus = 0;
		}

		int add = random.nextInt(3) + sharpnessBonus + strengthBonus;
		for(Entity entity : entities) {
			if(!doNotKill.contains(entity.getType()) && !entity.equals(p) && entity instanceof LivingEntity entity1) {
				entity1.damage(4 + add, p);
				damaged += 1;
				damage += 4 + add;
				((LivingEntity) entity).setNoDamageTicks(0);
				entity.setVelocity(new Vector(0, 0, 0));
			}
		}
		if(damaged > 0) {
			p.sendMessage(ChatColor.RED + "Your Implosion hit " + damaged + " enemies for " + damage + " damage.");
		}

		// wither shield
		List<PotionEffect> temp = p.getActivePotionEffects().stream().toList();
		List<PotionEffectType> effects = new ArrayList<>();
		int absorptionLevel = -1;
		for(PotionEffect effect : temp) {
			if(effect.getType().equals(PotionEffectType.ABSORPTION)) {
				absorptionLevel = effect.getAmplifier();
			}
			effects.add(effect.getType());
		}
		if(absorptionLevel != 2) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 2));
			p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 2.0F);
		}
		if(!effects.contains(PotionEffectType.DAMAGE_RESISTANCE)) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0));
		}
		if(!effects.contains(PotionEffectType.REGENERATION)) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
		}

		// finish shadow warp
		p.setFallDistance(0);
		p.teleport(newLocation);
		p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 0.9F, 1);
		p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
	}

	public void terminator(Player p, PlayerInteractEvent e) {
		// you don't need arrows
		p.getInventory().remove(Material.ARROW);
		p.getInventory().remove(Material.TIPPED_ARROW);
		p.getInventory().remove(Material.SPECTRAL_ARROW);

		// setting the three arrows
		Vector v = p.getLocation().getDirection();
		Location lLeft = p.getLocation();
		lLeft.add(v);
		lLeft.setYaw(lLeft.getYaw() - 5);
		lLeft.setY(lLeft.getY() + 1.62);

		Location l = p.getLocation();
		l.add(v);
		l.setY(l.getY() + 1.62);

		Location lRight = p.getLocation();
		lRight.add(v);
		lRight.setYaw(lRight.getYaw() + 5);
		lRight.setY(lRight.getY() + 1.62);

		// calculate power and strength bonus
		int powerBonus;
		try {
			int power = Objects.requireNonNull(Objects.requireNonNull(p.getInventory().getItem(e.getPlayer().getInventory().getHeldItemSlot())).getItemMeta()).getEnchants().get(Enchantment.ARROW_DAMAGE);
			if(power <= 4) {
				powerBonus = 1;
			} else {
				powerBonus = power - 3;
			}
		} catch(Exception exception) {
			powerBonus = 0;
		}

		int strengthBonus;
		try {
			strengthBonus = Objects.requireNonNull(p.getPotionEffect(PotionEffectType.INCREASE_DAMAGE)).getAmplifier();
		} catch(Exception exception) {
			strengthBonus = 0;
		}

		// shoot the three arrows
		Random random = new Random();
		int add = random.nextInt(2) + powerBonus + strengthBonus;
		Arrow left = p.getWorld().spawnArrow(l, lLeft.getDirection(), 3, 0.1F);
		Arrow middle = p.getWorld().spawnArrow(l, l.getDirection(), 3, 0.1F);
		Arrow right = p.getWorld().spawnArrow(l, lRight.getDirection(), 3, 0.1F);

		left.setDamage(3 + add);
		left.setPierceLevel(4);
		left.setBounce(true);
		left.setShooter(p);

		middle.setDamage(3 + add);
		middle.setPierceLevel(4);
		middle.setBounce(true);
		middle.setShooter(p);

		right.setDamage(3 + add);
		right.setPierceLevel(4);
		right.setBounce(true);
		right.setShooter(p);

		e.setCancelled(true);
		p.playSound(p, Sound.ENTITY_ARROW_SHOOT, 1, 1);
	}

	public void instantTransmission(Player p, PlayerInteractEvent e) {
		Vector unitVector = p.getLocation().getDirection();
		double smallX = unitVector.getX() / 10;
		double smallY = unitVector.getY() / 10;
		double smallZ = unitVector.getZ() / 10;
		Vector constant = new Vector(smallX, smallY, smallZ);
		unitVector = new Vector(0, 0, 0);
		int i = 0;
		Location checkedLocation = p.getLocation();
		checkedLocation.setY(p.getLocation().getY() + 1.62);
		while(i < 120) {
			if(isBlocked(e, constant, checkedLocation)) {
				break;
			}
			checkedLocation.add(constant);
			unitVector.setX(unitVector.getX() + smallX);
			unitVector.setY(unitVector.getY() + smallY);
			unitVector.setZ(unitVector.getZ() + smallZ);
			i++;
		}

		Location newLocation = p.getLocation().add(unitVector);
		newLocation.setY(Math.floor(newLocation.getY() + 1.62));
		p.teleport(newLocation);
		p.setFallDistance(0);
		e.setCancelled(true);
		p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
	}

	public void etherTransmission(Player p, PlayerInteractEvent e) {
		Vector unitVector = p.getLocation().getDirection();
		double smallX = unitVector.getX() / 10;
		double smallY = unitVector.getY() / 10;
		double smallZ = unitVector.getZ() / 10;
		Vector constant = new Vector(smallX, smallY, smallZ);
		unitVector = new Vector(0, 0, 0);
		int i = 0;
		boolean canTP = false;
		Location checkedLocation = p.getLocation();
		checkedLocation.setY(p.getLocation().getY() + 1.62);
		while(i < 610) {
			if(isBlocked(e, constant, checkedLocation)) {
				canTP = true;
				break;
			}
			checkedLocation.add(constant);
			unitVector.setX(unitVector.getX() + smallX);
			unitVector.setY(unitVector.getY() + smallY);
			unitVector.setZ(unitVector.getZ() + smallZ);
			i++;
		}

		if(canTP) {
			Location newLocation = p.getLocation().add(unitVector).add(unitVector);
			newLocation.setY(Math.floor(newLocation.getY() + 2.62));
			p.teleport(newLocation);
			p.setFallDistance(0);
		}
		e.setCancelled(true);
		p.playSound(p, Sound.ENTITY_ENDER_DRAGON_HURT, 1, 0.45F);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack itemInUse = p.getInventory().getItem(e.getPlayer().getInventory().getHeldItemSlot());
		if(itemInUse != null && Objects.equals(e.getHand(), EquipmentSlot.HAND)) {
			if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && isWitherImpactSword(itemInUse) || e.getAction().equals(Action.RIGHT_CLICK_AIR) && isWitherImpactSword(itemInUse)) {
				witherImpact(p, e);
			} else if(!e.getAction().equals(Action.PHYSICAL) && isTerminator(itemInUse)) {
				terminator(p, e);
			} else if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && isAOTV(itemInUse) || e.getAction().equals(Action.RIGHT_CLICK_AIR) && isAOTV(itemInUse)) {
				if(!p.isSneaking()) {
					instantTransmission(p, e);
				} else {
					etherTransmission(p, e);
				}
			}
		}
	}
}