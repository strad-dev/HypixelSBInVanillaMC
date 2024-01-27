package listeners;

import misc.Plugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CustomItems implements Listener {
	private Score score;

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
			if(string.contains("Ability: Wither Impact")) {
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
		if(!item.getItemMeta().hasLore()) {
			return false;
		}
		for(String string : Objects.requireNonNull(Objects.requireNonNull(item.getItemMeta()).getLore())) {
			if(string.contains("Ability: Salvation")) {
				isTerm = true;
				break;
			}
		}
		return item.getType().equals(new ItemStack(Material.BOW).getType()) && isTerm;
	}

	@SuppressWarnings("ConstantConditions")
	public boolean isAOTV(ItemStack item) {
		boolean isAOTV = false;
		if(!item.hasItemMeta()) {
			return false;
		}
		if(!item.getItemMeta().hasLore()) {
			return false;
		}
		for(String string : Objects.requireNonNull(Objects.requireNonNull(item.getItemMeta()).getLore())) {
			if(string.contains("Ability: Instant Transmission")) {
				isAOTV = true;
				break;
			}
		}
		return item.getType().equals(new ItemStack(Material.NETHERITE_SHOVEL).getType()) && isAOTV;
	}

	@SuppressWarnings("ConstantConditions")
	public boolean isIceSprayWand(ItemStack item) {
		boolean isWand = false;
		if(!item.hasItemMeta()) {
			return false;
		}
		if(!item.getItemMeta().hasLore()) {
			return false;
		}
		for(String string : Objects.requireNonNull(Objects.requireNonNull(item.getItemMeta()).getLore())) {
			if(string.contains("Ability: Ice Spray")) {
				isWand = true;
				break;
			}
		}
		return item.getType().equals(new ItemStack(Material.STICK).getType()) && isWand;
	}

	@SuppressWarnings("ConstantConditions")
	public boolean isWandOfRestoration(ItemStack item) {
		boolean isWand = false;
		if(!item.hasItemMeta()) {
			return false;
		}
		if(!item.getItemMeta().hasLore()) {
			return false;
		}
		for(String string : Objects.requireNonNull(Objects.requireNonNull(item.getItemMeta()).getLore())) {
			if(string.contains("Ability: Heal")) {
				isWand = true;
				break;
			}
		}
		return item.getType().equals(new ItemStack(Material.STICK).getType()) && isWand;
	}

	@SuppressWarnings("DataFlowIssue")
	public boolean isWandOfAtonement(ItemStack item) {
		boolean isWand = false;
		if(!item.hasItemMeta()) {
			return false;
		}
		if(!item.getItemMeta().hasLore()) {
			return false;
		}
		for(String string : Objects.requireNonNull(Objects.requireNonNull(item.getItemMeta()).getLore())) {
			if(string.contains("Ability: Big Heal")) {
				isWand = true;
				break;
			}
		}
		return item.getType().equals(new ItemStack(Material.STICK).getType()) && isWand;
	}

	@SuppressWarnings("DataFlowIssue")
	public boolean isHolyIce(ItemStack item) {
		boolean isIce = false;
		if(!item.hasItemMeta()) {
			return false;
		}
		if(!item.getItemMeta().hasLore()) {
			return false;
		}
		for(String string : Objects.requireNonNull(Objects.requireNonNull(item.getItemMeta()).getLore())) {
			if(string.contains("Ability: Splash Yo Face")) {
				isIce = true;
				break;
			}
		}
		return item.getType().equals(new ItemStack(Material.DIAMOND).getType()) && isIce;
	}

	public List<EntityType> createList() {
		List<EntityType> doNotKill = new ArrayList<>();
		doNotKill.add(EntityType.ALLAY);
		doNotKill.add(EntityType.ARMOR_STAND);
		doNotKill.add(EntityType.ARROW);
		doNotKill.add(EntityType.AXOLOTL);
		doNotKill.add(EntityType.BLOCK_DISPLAY);
		doNotKill.add(EntityType.BOAT);
		doNotKill.add(EntityType.CAT);
		doNotKill.add(EntityType.CHEST_BOAT);
		doNotKill.add(EntityType.DONKEY);
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
		doNotKill.add(EntityType.HORSE);
		doNotKill.add(EntityType.ITEM_FRAME);
		doNotKill.add(EntityType.ITEM_DISPLAY);
		doNotKill.add(EntityType.INTERACTION);
		doNotKill.add(EntityType.LEASH_HITCH);
		doNotKill.add(EntityType.LIGHTNING);
		doNotKill.add(EntityType.LLAMA);
		doNotKill.add(EntityType.LLAMA_SPIT);
		doNotKill.add(EntityType.MARKER);
		doNotKill.add(EntityType.MINECART);
		doNotKill.add(EntityType.MINECART_FURNACE);
		doNotKill.add(EntityType.MINECART_CHEST);
		doNotKill.add(EntityType.MINECART_COMMAND);
		doNotKill.add(EntityType.MINECART_HOPPER);
		doNotKill.add(EntityType.MINECART_MOB_SPAWNER);
		doNotKill.add(EntityType.MULE);
		doNotKill.add(EntityType.OCELOT);
		doNotKill.add(EntityType.PAINTING);
		doNotKill.add(EntityType.PARROT);
		doNotKill.add(EntityType.PRIMED_TNT);
		doNotKill.add(EntityType.SHULKER_BULLET);
		doNotKill.add(EntityType.SKELETON_HORSE);
		doNotKill.add(EntityType.SMALL_FIREBALL);
		doNotKill.add(EntityType.SNOWBALL);
		doNotKill.add(EntityType.SPECTRAL_ARROW);
		doNotKill.add(EntityType.TEXT_DISPLAY);
		doNotKill.add(EntityType.THROWN_EXP_BOTTLE);
		doNotKill.add(EntityType.TRIDENT);
		doNotKill.add(EntityType.UNKNOWN);
		doNotKill.add(EntityType.VILLAGER);
		doNotKill.add(EntityType.WITHER_SKULL);
		doNotKill.add(EntityType.WOLF);
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
		double damage = 0;
		double sharpnessBonus;
		try {
			int sharpness = Objects.requireNonNull(Objects.requireNonNull(p.getInventory().getItem(e.getPlayer().getInventory().getHeldItemSlot())).getItemMeta()).getEnchants().get(Enchantment.DAMAGE_ALL);
			sharpnessBonus = sharpness * 0.4;
		} catch(Exception exception) {
			sharpnessBonus = 0;
		}

		double strengthBonus;
		try {
			strengthBonus = Objects.requireNonNull(p.getPotionEffect(PotionEffectType.INCREASE_DAMAGE)).getAmplifier();
		} catch(Exception exception) {
			strengthBonus = 0;
		}

		double add = random.nextInt(3) + sharpnessBonus + strengthBonus;
		for(Entity entity : entities) {
			if(!doNotKill.contains(entity.getType()) && !entity.equals(p) && entity instanceof LivingEntity entity1) {
				CustomDamage.dealWithCustomMobs(entity1, p, 4 + add, CustomDamage.calculateFinalDamage(entity1, 4 + add), false, false);
				damaged += 1;
				damage += 4 + add;
				((LivingEntity) entity).setNoDamageTicks(0);
				entity.setVelocity(new Vector(0, 0, 0));
			}
		}
		if(damaged > 0) {
			p.sendMessage(ChatColor.RED + "Your Implosion hit " + damaged + " enemies for " + ((int) damage) + " damage.");
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
			p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 101, 2));
			p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0F, 0.65F);
		}
		if(!effects.contains(PotionEffectType.DAMAGE_RESISTANCE)) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 101, 0));
		}
		if(!effects.contains(PotionEffectType.REGENERATION)) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 101, 1));
		}

		// finish shadow warp
		p.setFallDistance(0);
		p.teleport(newLocation);
		p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 0.9F, 1);
		p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
		if(!p.getGameMode().equals(GameMode.CREATIVE)) {
			score.setScore(score.getScore() - 8);
		}
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
		double powerBonus;
		try {
			int power = Objects.requireNonNull(Objects.requireNonNull(p.getInventory().getItem(e.getPlayer().getInventory().getHeldItemSlot())).getItemMeta()).getEnchants().get(Enchantment.ARROW_DAMAGE);
			 powerBonus = power * 0.2;
		} catch(Exception exception) {
			powerBonus = 0;
		}

		double strengthBonus;
		try {
			strengthBonus = 0.5 * Objects.requireNonNull(p.getPotionEffect(PotionEffectType.INCREASE_DAMAGE)).getAmplifier();
		} catch(Exception exception) {
			strengthBonus = 0;
		}

		// shoot the three arrows
		double add = powerBonus + strengthBonus;
		Arrow left = p.getWorld().spawnArrow(l, lLeft.getDirection(), 3, 0.1F);
		Arrow middle = p.getWorld().spawnArrow(l, l.getDirection(), 3, 0.1F);
		Arrow right = p.getWorld().spawnArrow(l, lRight.getDirection(), 3, 0.1F);

		left.setDamage(2 + add);
		left.setPierceLevel(4);
		left.setShooter(p);

		middle.setDamage(2 + add);
		middle.setPierceLevel(4);
		middle.setShooter(p);

		right.setDamage(2 + add);
		right.setPierceLevel(4);
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
			e.setCancelled(true);
			p.playSound(p, Sound.ENTITY_ENDER_DRAGON_HURT, 1, 0.50F);
		}
	}

	public void iceSprayWand(Player p) {
		p.getWorld().spawnParticle(Particle.SNOWFLAKE, p.getLocation(), 1000);
		List<Entity> entities = (List<Entity>) p.getWorld().getNearbyEntities(p.getLocation(), 5, 5, 5);
		List<EntityType> doNotKill = createList();
		int damage = 0;
		int alreadyDebuffed = 0;
		for(Entity entity : entities) {
			if(!doNotKill.contains(entity.getType()) && entity instanceof LivingEntity entity1 && !entity.equals(p)) {
				if(entity1.hasPotionEffect(PotionEffectType.UNLUCK)) {
					alreadyDebuffed ++;
				} else {
					damage += 1;
					CustomDamage.dealWithCustomMobs(entity1, p, 1, CustomDamage.calculateFinalDamage(entity1, 1), false, false);
					((LivingEntity) entity).setNoDamageTicks(0);
					entity.setVelocity(new Vector(0, 0, 0));
					((LivingEntity) entity).addPotionEffect(PotionEffectType.UNLUCK.createEffect(101, 255));
					((LivingEntity) entity).addPotionEffect(PotionEffectType.SLOW.createEffect(101, 3));
				}
			}
		}
		if(damage > 0) {
			p.sendMessage(ChatColor.RED + "Your Ice Spray debuffed " + damage + " enemies.");
		}
		if(alreadyDebuffed > 0) {
			p.sendMessage(ChatColor.RED + String.valueOf(alreadyDebuffed) + " enemies have already been debuffed.");
		}
		p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
		if(!p.getGameMode().equals(GameMode.CREATIVE)) {
			score.setScore(score.getScore() - 2);
		}
	}

	public void wandOfRestoration(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 101, 0));
		p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.0F, 1.0F);
		if(!p.getGameMode().equals(GameMode.CREATIVE)) {
			score.setScore(score.getScore() - 4);
		}
	}

	public void wandOfAtonement(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 101, 1));
		p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.0F, 1.0F);
		if(!p.getGameMode().equals(GameMode.CREATIVE)) {
			score.setScore(score.getScore() - 4);
		}
	}

	public void holyIce(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 31, 3));
		p.playSound(p, Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED, 1.0F, 1.0F);
		if(!p.getGameMode().equals(GameMode.CREATIVE)) {
			score.setScore(score.getScore() - 20);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack itemInUse = p.getInventory().getItemInMainHand();
		try {
			score = Objects.requireNonNull(Objects.requireNonNull(Plugin.getInstance().getServer().getScoreboardManager()).getMainScoreboard().getObjective("Intelligence")).getScore(p.getName());
		} catch(Exception exception) {
			Plugin.getInstance().getLogger().info("Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
			Bukkit.broadcastMessage(ChatColor.RED + "Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
			return;
		}

		if(Objects.equals(e.getHand(), EquipmentSlot.HAND)) {
			if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
				if(isWitherImpactSword(itemInUse)) {
					if(score.getScore() < 8 && !p.getGameMode().equals(GameMode.CREATIVE)) {
						p.sendMessage(ChatColor.RED + "You do not have enough Intelligence to use this ability!  Required Intelligence: 8");
						p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
					} else {
						witherImpact(p, e);
					}
				} else if(isAOTV(itemInUse)) {
					if(!p.isSneaking()) {
						instantTransmission(p, e);
					} else {
						etherTransmission(p, e);
					}
				} else if(isIceSprayWand(itemInUse)) {
					if(score.getScore() < 2 && !p.getGameMode().equals(GameMode.CREATIVE)) {
						p.sendMessage(ChatColor.RED + "You do not have enough Intelligence to use this ability!  Required Intelligence: 2");
						p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
					} else {
						iceSprayWand(p);
					}
				} else if(isTerminator(itemInUse)) {
					terminator(p, e);
				} else if(isWandOfRestoration(itemInUse)) {
					if(score.getScore() < 4 && !p.getGameMode().equals(GameMode.CREATIVE)) {
						p.sendMessage(ChatColor.RED + "You do not have enough Intelligence to use this ability!  Required Intelligence: 4");
						p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
					} else if(p.hasPotionEffect(PotionEffectType.REGENERATION)) {
						p.sendMessage(ChatColor.RED + "You cannot use this item while you are already Regenerating!");
						p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
					} else {
						wandOfRestoration(p);
					}
				} else if(isWandOfAtonement(itemInUse)) {
					if(score.getScore() < 4 && !p.getGameMode().equals(GameMode.CREATIVE)) {
						p.sendMessage(ChatColor.RED + "You do not have enough Intelligence to use this ability!  Required Intelligence: 4");
						p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
					} else if(p.hasPotionEffect(PotionEffectType.REGENERATION)) {
						p.sendMessage(ChatColor.RED + "You cannot use this item while you are already Regenerating!");
						p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
					} else {
						wandOfAtonement(p);
					}
				} else if(isHolyIce(itemInUse)) {
					if(score.getScore() < 20 && !p.getGameMode().equals(GameMode.CREATIVE)) {
						p.sendMessage(ChatColor.RED + "You do not have enough Intelligence to use this ability!  Required Intelligence: 20");
						p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
					} else if(p.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
						p.sendMessage(ChatColor.RED + "You cannot use this item while you already have Resistance!");
						p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
					} else {
						holyIce(p);
					}
				} else {
					boolean unbreakable = false;
					try {
						//noinspection DataFlowIssue
						unbreakable = itemInUse.getItemMeta().isUnbreakable();
					} catch(Exception exception) {
						// nothing here
					}
					if(unbreakable) {
						e.setCancelled(true);
					}
				}
			}
			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Intelligence: " + score.getScore() + "/1000", ChatColor.AQUA.asBungee()));
		}
	}
}