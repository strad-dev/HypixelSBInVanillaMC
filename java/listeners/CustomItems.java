package listeners;

import misc.Plugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
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

import static listeners.CustomDamage.customMobs;

public class CustomItems implements Listener {
	private Score score;

	@SuppressWarnings("DataFlowIssue")
	public boolean isItem(ItemStack item, String id) {
		if(!item.hasItemMeta()) {
			return false;
		} else if(!item.getItemMeta().hasLore()) {
			return false;
		} else return item.getItemMeta().getLore().get(0).equals(id);
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
		doNotKill.add(EntityType.FIREBALL);
		doNotKill.add(EntityType.EGG);
		doNotKill.add(EntityType.BOAT);
		doNotKill.add(EntityType.ENDER_PEARL);
		doNotKill.add(EntityType.EXPERIENCE_ORB);
		doNotKill.add(EntityType.FALLING_BLOCK);
		doNotKill.add(EntityType.FIREWORK_ROCKET);
		doNotKill.add(EntityType.FISHING_BOBBER);
		doNotKill.add(EntityType.GLOW_ITEM_FRAME);
		doNotKill.add(EntityType.HORSE);
		doNotKill.add(EntityType.ITEM_FRAME);
		doNotKill.add(EntityType.ITEM_DISPLAY);
		doNotKill.add(EntityType.INTERACTION);
		doNotKill.add(EntityType.LEASH_KNOT);
		doNotKill.add(EntityType.LIGHTNING_BOLT);
		doNotKill.add(EntityType.LLAMA);
		doNotKill.add(EntityType.LLAMA_SPIT);
		doNotKill.add(EntityType.MARKER);
		doNotKill.add(EntityType.MINECART);
		doNotKill.add(EntityType.FURNACE_MINECART);
		doNotKill.add(EntityType.CHEST_MINECART);
		doNotKill.add(EntityType.COMMAND_BLOCK_MINECART);
		doNotKill.add(EntityType.HOPPER_MINECART);
		doNotKill.add(EntityType.SPAWNER_MINECART);
		doNotKill.add(EntityType.MULE);
		doNotKill.add(EntityType.OCELOT);
		doNotKill.add(EntityType.PAINTING);
		doNotKill.add(EntityType.PARROT);
		doNotKill.add(EntityType.TNT);
		doNotKill.add(EntityType.SHULKER_BULLET);
		doNotKill.add(EntityType.SKELETON_HORSE);
		doNotKill.add(EntityType.SMALL_FIREBALL);
		doNotKill.add(EntityType.SNOWBALL);
		doNotKill.add(EntityType.SPECTRAL_ARROW);
		doNotKill.add(EntityType.TEXT_DISPLAY);
		doNotKill.add(EntityType.EXPERIENCE_BOTTLE);
		doNotKill.add(EntityType.TRIDENT);
		doNotKill.add(EntityType.UNKNOWN);
		doNotKill.add(EntityType.VILLAGER);
		doNotKill.add(EntityType.WITHER_SKULL);
		doNotKill.add(EntityType.WOLF);
		return doNotKill;
	}

	public void witherImpact(Player p, PlayerInteractEvent e) {
		Location originalLocation = p.getLocation().clone();
		Location l = p.getLocation().clone();
		l.add(0, 1.62, 0);
		Vector v = l.getDirection();
		v.setX(v.getX() / 10);
		v.setY(v.getY() / 10);
		v.setZ(v.getZ() / 10);
		for(int i = 0; i < 100; i++) {
			l.add(v);
			if(l.getBlock().getType().isSolid()) {
				l = l.subtract(v).getBlock().getLocation();
				if(originalLocation.getPitch() > 0) {
					l.add(0, 1.62, 0);
				}
				l.setYaw(originalLocation.getYaw());
				l.setPitch(originalLocation.getPitch());
				l.add(0.5, 0, 0.5);
				break;
			}
		}
		l.subtract(0, 1.62, 0);
		if(!l.getBlock().isEmpty()) {
			l.add(0, 1, 0);
		}
		if(!l.getBlock().isEmpty()) {
			l.add(0, 1, 0);
		}
		p.setFallDistance(0);
		p.teleport(l);
		p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

		// implosion
		p.getWorld().spawnParticle(Particle.EXPLOSION, l, 20);
		List<Entity> entities = p.getNearbyEntities(10, 10, 10);
		List<EntityType> doNotKill = createList();
		double targetDamage = Objects.requireNonNull(p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).getValue();
		int damaged = 0;
		double damage = 0;
		try {
			int sharpness = Objects.requireNonNull(Objects.requireNonNull(p.getInventory().getItem(e.getPlayer().getInventory().getHeldItemSlot())).getItemMeta()).getEnchants().get(Enchantment.SHARPNESS);
			targetDamage += sharpness * 0.5 + 0.5;
		} catch(Exception exception) {
			// nothing
		}
		targetDamage = Math.ceil(targetDamage * 0.51);
		for(Entity entity : entities) {
			if(!doNotKill.contains(entity.getType()) && !entity.equals(p) && entity instanceof LivingEntity entity1 && entity1.getHealth() > 0) {
				if(entity instanceof Player target && (target.getGameMode().equals(GameMode.CREATIVE) || target.getGameMode().equals(GameMode.SPECTATOR))) {
					continue;
				}
				customMobs(entity1, p, targetDamage, DamageType.PLAYER_MAGIC);
				damaged += 1;
				damage += targetDamage;
			}
		}
		if(damaged > 0) {
			p.sendMessage(ChatColor.RED + "Your Implosion hit " + damaged + " enemies for " + ((int) damage) + " damage.");
		}
		p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

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
		if(absorptionLevel != 2) { // absorption shield
			p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 101, 2));
			p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0F, 0.65F);
			Location finalL = l;
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> { // convert to healing after 5 seconds
				p.setHealth(Math.min(p.getHealth() + (p.getAbsorptionAmount() / 2), 20.0));
				p.playSound(finalL, Sound.ENTITY_PLAYER_LEVELUP, 2.0F, 2.0F);
			}, 101L);
		}
		if(!effects.contains(PotionEffectType.RESISTANCE)) { // reduced damage
			p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 101, 0));
		}

		if(!p.getGameMode().equals(GameMode.CREATIVE)) {
			score.setScore(score.getScore() - 12);
		}

		e.setCancelled(true);
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
			int power = Objects.requireNonNull(Objects.requireNonNull(p.getInventory().getItem(e.getPlayer().getInventory().getHeldItemSlot())).getItemMeta()).getEnchants().get(Enchantment.POWER);
			powerBonus = power * 0.1;
		} catch(Exception exception) {
			powerBonus = 0;
		}

		double strengthBonus;
		try {
			strengthBonus = 0.25 * Objects.requireNonNull(p.getPotionEffect(PotionEffectType.STRENGTH)).getAmplifier();
		} catch(Exception exception) {
			strengthBonus = 0;
		}

		// shoot the three arrows
		double add = powerBonus + strengthBonus;
		Arrow left = p.getWorld().spawnArrow(l, lLeft.getDirection(), 3, 0.1F);
		Arrow middle = p.getWorld().spawnArrow(l, l.getDirection(), 3, 0.1F);
		Arrow right = p.getWorld().spawnArrow(l, lRight.getDirection(), 3, 0.1F);

		left.setDamage(1 + add);
		left.setPierceLevel(4);
		left.setShooter(p);
		left.addScoreboardTag("TerminatorArrow");

		middle.setDamage(1 + add);
		middle.setPierceLevel(4);
		middle.setShooter(p);
		middle.addScoreboardTag("TerminatorArrow");

		right.setDamage(1 + add);
		right.setPierceLevel(4);
		right.setShooter(p);
		right.addScoreboardTag("TerminatorArrow");

		p.playSound(p, Sound.ENTITY_ARROW_SHOOT, 1, 1);
		p.addScoreboardTag("TerminatorCooldown");
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> p.removeScoreboardTag("TerminatorCooldown"), 4L);
	}

	public void instantTransmission(Player p) {
		Location originalLocation = p.getLocation().clone();
		Location l = p.getLocation().clone();
		l.add(0, 1.62, 0);
		Vector v = l.getDirection();
		v.setX(v.getX() / 10);
		v.setY(v.getY() / 10);
		v.setZ(v.getZ() / 10);
		for(int i = 0; i < 120; i++) {
			l.add(v);
			if(l.getBlock().getType().isSolid()) {
				l = l.subtract(v).getBlock().getLocation();
				if(originalLocation.getPitch() > 0) {
					l.add(0, 1.62, 0);
				}
				l.setYaw(originalLocation.getYaw());
				l.setPitch(originalLocation.getPitch());
				l.add(0.5, 0, 0.5);
				break;
			}
		}
		l.subtract(0, 1.62, 0);
		if(!l.getBlock().isEmpty()) {
			l.add(0, 1, 0);
		}
		if(!l.getBlock().isEmpty()) {
			l.add(0, 1, 0);
		}
		p.setFallDistance(0);
		p.teleport(l);
		p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
	}

	public void etherTransmission(Player p) {
		Location l = p.getLocation();
		l.add(0, 1.32, 0);
		Vector v = l.getDirection();
		v.setX(v.getX() / 10);
		v.setY(v.getY() / 10);
		v.setZ(v.getZ() / 10);
		for(int i = 0; i < 610; i++) {
			l.add(v);
			if(l.getBlock().getType().isSolid()) {
				Location newLocation = l.add(0, 1, 0).getBlock().getLocation();
				if(l.getBlock().isEmpty() && l.add(0, 1, 0).getBlock().isEmpty()) {
					newLocation.setYaw(l.getYaw());
					newLocation.setPitch(l.getPitch());
					newLocation.add(0.5, 0, 0.5);
					p.setFallDistance(0);
					p.teleport(newLocation);
					p.playSound(p, Sound.ENTITY_ENDER_DRAGON_HURT, 1, 0.50F);
				}
				break;
			}
		}
	}

	public void iceSprayWand(Player p) {
		p.getWorld().spawnParticle(Particle.SNOWFLAKE, p.getLocation(), 1000);
		List<Entity> entities = (List<Entity>) p.getWorld().getNearbyEntities(p.getLocation(), 5, 5, 5);
		List<EntityType> doNotKill = createList();
		int damage = 0;
		int alreadyDebuffed = 0;
		for(Entity entity : entities) {
			if(!doNotKill.contains(entity.getType()) && entity instanceof LivingEntity entity1 && !entity.equals(p) && entity1.getHealth() > 0) {
				if(entity instanceof Player target && (target.getGameMode().equals(GameMode.CREATIVE) || target.getGameMode().equals(GameMode.SPECTATOR))) {
					continue;
				}
				if(entity1.getScoreboardTags().contains("IceSprayed")) {
					alreadyDebuffed++;
				} else {
					damage += 1;
					customMobs(entity1, p, 1, DamageType.PLAYER_MAGIC);
					entity1.addPotionEffect(PotionEffectType.SLOWNESS.createEffect(101, 3));
					entity1.addScoreboardTag("IceSprayed");
					Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> entity1.removeScoreboardTag("IceSprayed"), 101L);
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
			score.setScore(score.getScore() - 4);
		}
		p.addScoreboardTag("IceSprayCooldown");
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> p.removeScoreboardTag("IceSprayCooldown"), 100L);
	}

	public void wandOfRestoration(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 26, 2));
		p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.0F, 1.0F);
		if(!p.getGameMode().equals(GameMode.CREATIVE)) {
			score.setScore(score.getScore() - 6);
		}
		p.addScoreboardTag("WandCooldown");
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> p.removeScoreboardTag("WandCooldown"), 80L);
	}

	public void wandOfAtonement(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 51, 2));
		p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.0F, 1.0F);
		if(!p.getGameMode().equals(GameMode.CREATIVE)) {
			score.setScore(score.getScore() - 6);
		}
		p.addScoreboardTag("WandCooldown");
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> p.removeScoreboardTag("WandCooldown"), 80L);
	}

	public void holyIce(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 31, 3));
		p.getWorld().spawnParticle(Particle.DRIPPING_WATER, p.getLocation(), 1000);
		p.playSound(p, Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED, 1.0F, 1.0F);
		if(!p.getGameMode().equals(GameMode.CREATIVE)) {
			score.setScore(score.getScore() - 25);
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
			if(!p.getScoreboardTags().contains("AbilityCooldown")) {
				if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
					if(isItem(itemInUse, "skyblock/combat/scylla")) {
						if(score.getScore() < 12 && !p.getGameMode().equals(GameMode.CREATIVE)) {
							p.sendMessage(ChatColor.RED + "You do not have enough Intelligence to use this ability!  Required Intelligence: 12");
							p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
						} else {
							witherImpact(p, e);
						}
					} else if(isItem(itemInUse, "skyblock/combat/aspect_of_the_void")) {
						if(!p.isSneaking()) {
							instantTransmission(p);
						} else {
							etherTransmission(p);
						}
						e.setCancelled(true);
					} else if(isItem(itemInUse, "skyblock/combat/ice_spray_wand")) {
						if(score.getScore() < 4 && !p.getGameMode().equals(GameMode.CREATIVE)) {
							p.sendMessage(ChatColor.RED + "You do not have enough Intelligence to use this ability!  Required Intelligence: 4");
							p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
						} else if(p.getScoreboardTags().contains("IceSprayCooldown")) {
							p.sendMessage(ChatColor.RED + "This ability is on cooldown!");
							p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
						} else {
							iceSprayWand(p);
						}
					} else if(isItem(itemInUse, "skyblock/combat/terminator")) {
						if(!p.getScoreboardTags().contains("TerminatorCooldown")) {
							terminator(p, e);
						}
						e.setCancelled(true);
					} else if(isItem(itemInUse, "skyblock/combat/wand_of_restoration")) {
						if(score.getScore() < 6 && !p.getGameMode().equals(GameMode.CREATIVE)) {
							p.sendMessage(ChatColor.RED + "You do not have enough Intelligence to use this ability!  Required Intelligence: 6");
							p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
						} else if(p.getScoreboardTags().contains("WandCooldown")) {
							p.sendMessage(ChatColor.RED + "This ability is on cooldown!");
							p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
						} else {
							wandOfRestoration(p);
						}
					} else if(isItem(itemInUse, "skyblock/combat/wand_of_atonement")) {
						if(score.getScore() < 6 && !p.getGameMode().equals(GameMode.CREATIVE)) {
							p.sendMessage(ChatColor.RED + "You do not have enough Intelligence to use this ability!  Required Intelligence: 6");
							p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
						} else if(p.getScoreboardTags().contains("WandCooldown")) {
							p.sendMessage(ChatColor.RED + "This ability is on cooldown!");
							p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
						} else {
							wandOfAtonement(p);
						}
					} else if(isItem(itemInUse, "skyblock/combat/holy_ice")) {
						if(score.getScore() < 25 && !p.getGameMode().equals(GameMode.CREATIVE)) {
							p.sendMessage(ChatColor.RED + "You do not have enough Intelligence to use this ability!  Required Intelligence: 25");
							p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
						} else {
							holyIce(p);
						}
					}
					p.addScoreboardTag("AbilityCooldown");
					Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> p.removeScoreboardTag("AbilityCooldown"), 2L);
				}
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Intelligence: " + score.getScore() + "/2500", ChatColor.AQUA.asBungee()));
			}
			if(e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.PHYSICAL)) {
				if(isItem(itemInUse, "skyblock/combat/terminator")) {
					if(!p.getScoreboardTags().contains("SalvationCooldown")) {
						NonEntityDamage.shootBeam(p, p, Color.RED, 64, 5, 2);
						p.playSound(p.getLocation(), Sound.ENTITY_GUARDIAN_DEATH, 1.0F, 2.0F);
						p.addScoreboardTag("SalvationCooldown");
						Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> p.removeScoreboardTag("SalvationCooldown"), 19L);
					}
					e.setCancelled(true);
				}
			}
			try {
				if(itemInUse.getEnchantments().get(Enchantment.KNOCKBACK).equals(1)) {
					e.setCancelled(true);
				}
			} catch(Exception exception) {
				// nothing here
			}
		}
	}
}