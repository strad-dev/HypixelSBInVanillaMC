package items.weapons;

import items.AbilityItem;
import listeners.CustomDamage;
import listeners.CustomItems;
import listeners.DamageType;
import misc.Plugin;
import misc.Utils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Scylla implements AbilityItem {
	private static final int MANA_COST = 12;

	public static ItemStack getItem(Enchantment ench, int enchLevel) {
		ItemStack scylla = new ItemStack(Material.NETHERITE_SWORD);

		ItemMeta data = scylla.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Hyperion");
		AttributeModifier attackSpeed = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "scyllaModifier"), 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "scyllaModifierDmg"), 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.ATTACK_SPEED, attackSpeed);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		String loreDamage = "8";
		if(ench.equals(Enchantment.SHARPNESS)) {
			switch(enchLevel) {
				case 1 -> loreDamage = "9";
				case 2 -> loreDamage = "10";
				case 3 -> loreDamage = "11";
				case 4 -> loreDamage = "12";
				case 5 -> loreDamage = "13";
				case 6 -> loreDamage = "14";
				case 7 -> loreDamage = "15";
				default -> loreDamage = "8";
			}
		}

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/scylla");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+" + loreDamage);
		if(ench.equals(Enchantment.SMITE) || ench.equals(Enchantment.BANE_OF_ARTHROPODS)) {
			lore.add("");
			switch(enchLevel) {
				case 1 -> loreDamage = "2.5";
				case 2 -> loreDamage = "5";
				case 3 -> loreDamage = "7.5";
				case 4 -> loreDamage = "10";
				case 5 -> loreDamage = "12.5";
				case 6 -> loreDamage = "15";
				default -> loreDamage = "0";
			}
			if(ench.equals(Enchantment.SMITE)) {
				lore.add(ChatColor.GRAY + "Bonus Undead Damage: " + ChatColor.RED + "+" + loreDamage);
			} else {
				lore.add(ChatColor.GRAY + "Bonus Arthropod Damage: " + ChatColor.RED + "+" + loreDamage);
			}
		}
		lore.add("");
		lore.add(ChatColor.GRAY + "Deals " + ChatColor.RED + "+4" + ChatColor.GRAY + " damage to Withers.");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Wither Impact " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Teleport " + ChatColor.GREEN + "10 blocks" + ChatColor.GRAY + " ahead of");
		lore.add(ChatColor.GRAY + "you.  Then implode, dealing");
		lore.add(ChatColor.RED + "51%" + ChatColor.GRAY + " of your Melee Damage to");
		lore.add(ChatColor.GRAY + "nearby enemies.  Also applies");
		lore.add(ChatColor.GRAY + "the Wither Shield Scroll Ability,");
		lore.add(ChatColor.GRAY + "reducing damage taken and");
		lore.add(ChatColor.GRAY + "granting an absorption shield");
		lore.add(ChatColor.GRAY + "for " + ChatColor.YELLOW + "5 seconds.");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + MANA_COST);
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC SWORD " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		scylla.setItemMeta(data);

		return scylla;
	}

	@Override
	public boolean hasLeftClickAbility() {
		return false;
	}

	@Override
	public boolean onRightClick(Player p) {
		Location origin = p.getLocation().clone();
		Location l = null;
		RayTraceResult result = p.rayTraceBlocks(11.65);
		if(result == null) {
			l = p.getLocation().add(p.getLocation().getDirection().multiply(10));
			l.setX(Math.floor(l.getX()) + 0.5);
			l.setY(Math.floor(l.getY()));
			l.setZ(Math.floor(l.getZ()) + 0.5);

			// Check if the target location is safe
			Block feetBlock = l.getBlock();
			Block headBlock = feetBlock.getRelative(BlockFace.UP);

			// If either block is solid, we need to adjust
			if(!feetBlock.isPassable() || !headBlock.isPassable()) {
				// Try to move up until we find a safe spot or reach original height
				double originalY = p.getLocation().getY();
				Location checkLoc = l.clone();

				// Check up to 10 blocks up or until at original height
				for(int i = 0; i < 10; i++) {
					checkLoc.add(0, 1, 0);
					Block checkFeet = checkLoc.getBlock();
					Block checkHead = checkFeet.getRelative(BlockFace.UP);

					// Check if this position is safe (2 blocks of air)
					if(checkFeet.isPassable() && checkHead.isPassable()) {
						// Also check we're not in a 1-block gap if above original height
						if(checkLoc.getY() >= originalY) {
							Block aboveHead = checkHead.getRelative(BlockFace.UP);
							if(!aboveHead.isPassable()) {
								// This is a 1-block gap at or above original height - skip it
								continue;
							}
						}

						l = checkLoc.clone();
						break;
					}

					// Stop if we've reached or passed original height and no safe spot
					if(checkLoc.getY() >= originalY) {
						break;
					}
				}
			}

			// Additional check for 1-block tall spaces when below original height
			if(l.getY() < p.getLocation().getY()) {
				Block aboveHead = l.getBlock().getRelative(BlockFace.UP, 2);
				if(!aboveHead.isPassable()) {
					// This would put player in crawl mode below their starting position
					// Try to find a better spot
					for(int i = 1; i <= 3; i++) {
						Location upLoc = l.clone().add(0, i, 0);
						Block upFeet = upLoc.getBlock();
						Block upHead = upFeet.getRelative(BlockFace.UP);
						Block upAbove = upHead.getRelative(BlockFace.UP);

						if(upFeet.isPassable() && upHead.isPassable() && upAbove.isPassable()) {
							l = upLoc;
							break;
						}
					}
				}
			}

			l.setYaw(origin.getYaw());
			l.setPitch(origin.getPitch());
			p.teleport(l);
		} else {
			switch(result.getHitBlockFace()) {
				case SELF -> {
					// empty case
				}
				case UP -> {
					l = result.getHitBlock().getLocation().add(0.5, 1, 0.5);
					l.setYaw(origin.getYaw());
					l.setPitch(origin.getPitch());
					p.teleport(l);
				}
				case DOWN -> {
					l = result.getHitBlock().getLocation().add(0.5, -2, 0.5);
					l.setYaw(origin.getYaw());
					l.setPitch(origin.getPitch());
					p.teleport(l);
				}
				default -> {
					// Hit a side face - backtrack until we find a safe spot
					Location hitLocation = result.getHitPosition().toLocation(p.getWorld());
					Vector direction = origin.getDirection().normalize();

					// Calculate max backtrack distance (don't go past player's origin)
					double maxBacktrack = origin.distance(hitLocation);

					// Backtrack from the exact hit point
					Location checkLoc = hitLocation.clone();
					Location lastSafe = null;
					double totalBacktracked = 0;

					// Backtrack in smaller increments for more precision
					for(int i = 0; i < 100; i++) { // 120 * 0.1 = 12 blocks
						// Backtrack by 0.1 blocks for precision
						checkLoc.subtract(direction.clone().multiply(0.1));
						totalBacktracked += 0.1;

						// Don't go past the player's starting position
						if(totalBacktracked > maxBacktrack) {
							break;
						}

						// Check current block
						Block feetBlock = checkLoc.getBlock();
						Block headBlock = feetBlock.getRelative(BlockFace.UP);

						if(feetBlock.isPassable() && headBlock.isPassable()) {
							// This spot is safe, but keep checking for the optimal position
							lastSafe = checkLoc.clone();

							// Check if we've backtracked enough (at least 0.5 blocks from wall)
							if(checkLoc.distance(hitLocation) >= 0.5) {
								// Center on the block we're in
								l = new Location(checkLoc.getWorld(), Math.floor(checkLoc.getX()) + 0.5, Math.floor(checkLoc.getY()), Math.floor(checkLoc.getZ()) + 0.5);
								l.setYaw(origin.getYaw());
								l.setPitch(origin.getPitch());
								p.teleport(l);
								break;
							}
						}
					}

					// If we found a safe spot but didn't teleport yet
					if(lastSafe != null) {
						l = new Location(lastSafe.getWorld(), Math.floor(lastSafe.getX()) + 0.5, Math.floor(lastSafe.getY()), Math.floor(lastSafe.getZ()) + 0.5);
						l.setYaw(origin.getYaw());
						l.setPitch(origin.getPitch());
						p.teleport(l);
					}
				}
			}
		}
		p.setFallDistance(0);
		p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

		// implosion
		p.getWorld().spawnParticle(Particle.EXPLOSION, l, 20);
		List<Entity> entities = p.getNearbyEntities(10, 10, 10);
		List<EntityType> doNotKill = CustomItems.createList();
		double targetDamage = Objects.requireNonNull(p.getAttribute(Attribute.ATTACK_DAMAGE)).getValue();
		int damaged = 0;
		double damage = 0;
		int smite = 0;
		int bane = 0;
		if(p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SHARPNESS)) {
			int sharpness = p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.SHARPNESS);
			targetDamage += sharpness;
		} else if(p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SMITE)) {
			smite = p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.SMITE);
		} else if(p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.BANE_OF_ARTHROPODS)) {
			bane = p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.BANE_OF_ARTHROPODS);
		}
		for(Entity entity : entities) {
			if(!doNotKill.contains(entity.getType()) && !entity.equals(p) && entity instanceof LivingEntity entity1 && entity1.getHealth() > 0) {
				double tempDamage = targetDamage;
				if(entity1 instanceof Wither) {
					tempDamage += 4 + smite * 2.5;
				} else if(entity1 instanceof Zombie || entity1 instanceof AbstractSkeleton || entity1 instanceof SkeletonHorse || entity1 instanceof ZombieHorse || entity1 instanceof Phantom || entity1 instanceof Zoglin) {
					tempDamage += smite * 2.5;
				} else if(entity1 instanceof Spider || entity1 instanceof Bee || entity1 instanceof Silverfish || entity1 instanceof Endermite) {
					tempDamage += bane * 2.5;
				}
				tempDamage = Math.ceil(tempDamage * 0.51);
				CustomDamage.customMobs(entity1, p, tempDamage, DamageType.PLAYER_MAGIC);
				damaged += 1;
				damage += tempDamage;
			}
		}
		if(damaged > 0) {
			p.sendMessage(ChatColor.RED + "Your Implosion hit " + damaged + " enemies for " + ((int) damage) + " damage.");
		}
		p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

		// wither shield
		int absorptionLevel = -1;
		if(p.hasPotionEffect(PotionEffectType.ABSORPTION)) {
			absorptionLevel = p.getPotionEffect(PotionEffectType.ABSORPTION).getAmplifier();
		}
		if(absorptionLevel != 2) { // absorption shield
			p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 101, 2));
			p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0F, 0.66666F);
			Location finalL = l;
			Utils.scheduleTask(() -> { // convert to healing after 5 seconds
				p.setHealth(Math.min(p.getHealth() + (p.getAbsorptionAmount() / 2), p.getAttribute(Attribute.MAX_HEALTH).getValue()));
				p.playSound(finalL, Sound.ENTITY_PLAYER_LEVELUP, 2.0F, 2.0F);
			}, 101L);
		}
		if(!p.getScoreboardTags().contains("WitherShield")) { // reduced damage
			p.addScoreboardTag("WitherShield");
			Utils.scheduleTask(() -> p.removeScoreboardTag("WitherShield"), 101);
		}
		return true;
	}

	@Override
	public boolean onLeftClick(Player p) {
		return false;
	}

	public int manaCost() {
		return MANA_COST;
	}

	@Override
	public String cooldownTag() {
		return "";
	}

	@Override
	public int cooldown() {
		return 0;
	}
}
