package items.weapons;

import items.AbilityItem;
import listeners.CustomDamage;
import listeners.CustomItems;
import listeners.DamageType;
import misc.Cooldowns;
import misc.DamageData;
import misc.MinecraftFont;
import misc.Plugin;
import misc.SkyblockId;
import misc.Utils;
import net.kyori.adventure.text.Component;
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
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.ToDoubleFunction;

public class Scylla implements AbilityItem {
	private static final int MANA_COST = 15;

	/** Before enchants. Quoted on the lore. */
	private static final double BASE_DAMAGE = 8;

	/** Share of the wielder's melee damage the implosion deals. */
	public static final double IMPLOSION_SHARE = 0.60;
	/** Flat bonus vs Withers. Read by {@link listeners.CustomDamage#calculateFinalDamage}, quoted by the lore. */
	public static final double WITHER_BONUS = 4;

	/**
	 * Share of remaining absorption that becomes health when the shield expires. Same on every rung: it was a
	 * ManhuntTier column, but absorption already scales, so the rungs differed twice for one effect.
	 */
	public static final double HEAL_SHARE = 0.50;

	/**
	 * Ticks before absorption converts to healing, same on every rung.
	 * {@link manhunt.ManhuntTier#witherShieldCooldown} only decides when the next shield may go up.
	 */
	public static final int SHIELD_DURATION = 101;

	/** {@link Cooldowns} tag for the shield refresh, separate from the ability. */
	private static final String SHIELD_COOLDOWN = "witherShield";

	public static ItemStack getItem() {
		return getItem(Map.of());
	}

	public static ItemStack getItem(Map<Enchantment, Integer> enchants) {
		return getItem(enchants, null);
	}

	public static final String ID = "skyblock/combat/scylla";

	/** Full Hyperion only, not a Manhunt one or plain netherite sword. */
	public static boolean isScylla(ItemStack item) {
		if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) return false;
		return ID.equals(Utils.firstLorePlain(item.getItemMeta()));
	}

	/** Lore figure; what {@link #onRightClick} pays before Smite/Bane. */
	public static double implosionDamage(@Nullable Player p, ItemStack weapon) {
		return Utils.meleeDamageWith(p, weapon) * IMPLOSION_SHARE;
	}

	/**
	 * Enchants go on here, not by the caller after; that was the lore desync. {@code p} may be null (recipe,
	 * creative palette): lore then assumes no other damage modifiers. {@code ItemReloader} keeps it current.
	 */
	public static ItemStack getItem(Map<Enchantment, Integer> enchants, @Nullable Player p) {
		ItemStack scylla = new ItemStack(Material.NETHERITE_SWORD);

		ItemMeta data = scylla.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<light_purple>Hyperion"));
		AttributeModifier attackSpeed = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "scyllaModifier"), 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "scyllaModifierDmg"), BASE_DAMAGE, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.ATTACK_SPEED, attackSpeed);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
		scylla.setItemMeta(data);
		scylla.addUnsafeEnchantments(enchants);

		// Read off the finished stack so the lore quotes exactly what the ability pays.
		double implosion = implosionDamage(p, scylla);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm(ID));
		lore.add(Utils.mm(""));
		lore.addAll(Utils.statLore(data, enchants));
		lore.addAll(Utils.bonusDamageLore(enchants));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Deals <red>+" + Utils.damageNumber(WITHER_BONUS) + "<gray> damage to Withers."));
		lore.add(Utils.mm(""));
		// Never wrapped: every tooltip is measured against it. See MinecraftFont.LORE_WIDTH.
		lore.add(Utils.mm("<gold>Ability: Wither Impact <green><bold>RIGHT CLICK"));
		lore.addAll(MinecraftFont.wrapLore(
				"<gray>Teleport <green>10 blocks<gray> ahead of you.  Then implode, dealing <red>"
				+ Utils.tenthNumber(implosion) + " damage <gray>to enemies within <green>10 blocks<gray>.  Also"
				+ " reduces damage taken by <red>15%<gray> and grants an Absorption Shield with <red>10 HP"
				+ " <gray>for <green>5<gray> seconds."));
		lore.add(Utils.mm("<dark_gray>Intelligence Cost: <dark_aqua>" + MANA_COST));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<light_purple><bold><obfuscated>a</obfuscated> MYTHIC SWORD <obfuscated>a</obfuscated>"));

		data = scylla.getItemMeta();
		data.lore(lore);
		scylla.setItemMeta(data);
		Utils.setEnchantability(scylla, Utils.SKYBLOCK_ENCHANTABILITY);

		return SkyblockId.stamp(scylla);
	}

	@Override
	public boolean hasLeftClickAbility() {
		return false;
	}

	@Override
	public boolean onRightClick(Player p) {
		ItemStack held = p.getInventory().getItemInMainHand();
		// Same figures as the melee pipeline. The old else-if chain counted only one enchant and used numbers
		// (level, level * 2) matching neither vanilla nor this plugin.
		double targetDamage = Utils.meleeDamageWith(p, held);
		double smite = CustomDamage.smiteBonus(held.getEnchantmentLevel(Enchantment.SMITE));
		double bane = CustomDamage.smiteBonus(held.getEnchantmentLevel(Enchantment.BANE_OF_ARTHROPODS));

		// 0 refresh cooldown: only a standing shield gates it. See ManhuntTier.witherShieldCooldown.
		return witherImpact(p, 10, 10, 10, 0.15, 0, entity -> {
			double tempDamage = targetDamage;
			if(entity instanceof Wither) {
				tempDamage += 4 + smite;
			} else if(entity instanceof Zombie || entity instanceof AbstractSkeleton || entity instanceof SkeletonHorse || entity instanceof ZombieHorse || entity instanceof Phantom || entity instanceof Zoglin) {
				tempDamage += smite;
			} else if(entity instanceof Spider || entity instanceof Bee || entity instanceof Silverfish || entity instanceof Endermite) {
				tempDamage += bane;
			}
			return tempDamage * IMPLOSION_SHARE;
		});
	}

	/**
	 * Teleport, implode, Wither Shield. Shared with the Manhunt Hyperion so the placement search has one copy.
	 * {@code shieldCooldown}: ticks before the next shield, on top of a standing one; 0 = standing shield is the
	 * only gate. Never changes {@link #SHIELD_DURATION}. {@code damage} of 0 or less skips the target entirely
	 * (no pipeline, no knockback, not counted); Manhunt's implosion cooldown uses that.
	 */
	public static boolean witherImpact(Player p, double distance, double radius, double absorption,
									   double damageReduction, int shieldCooldown,
									   ToDoubleFunction<LivingEntity> damage) {
		Location origin = p.getLocation().clone();
		Location l = null;
		// World border counts as solid, so an impact aimed through it lands just inside.
		double reach = Utils.borderDistance(p.getLocation(), p.getLocation().getDirection(), distance + 1.65);
		RayTraceResult result = p.rayTraceBlocks(reach);
		if(result == null) {
			l = p.getLocation().add(p.getLocation().getDirection().multiply(Math.min(distance, reach)));
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
		// A SELF hit or a backtrack with no safe spot left l null and NPE'd the particle call. A blocked
		// teleport still implodes, where the player stands.
		if(l == null) l = p.getLocation();

		p.setFallDistance(0);
		p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

		// implosion
		p.getWorld().spawnParticle(Particle.EXPLOSION, l, 20);
		List<Entity> entities = new ArrayList<>(l.getWorld().getNearbyEntities(l, radius, radius, radius));
		List<EntityType> doNotKill = CustomItems.createList();
		int damaged = 0;
		double total = 0;
		for(Entity entity : entities) {
			if(!doNotKill.contains(entity.getType()) && !entity.equals(p) && entity instanceof LivingEntity entity1 && entity1.getHealth() > 0) {
				// Counter reports damage LANDED (after armour, shields, PvP etc.), read off DamageData rather than
				// target health, which would cap kills and lose overkill.
				double tempDamage = damage.applyAsDouble(entity1);
				if(tempDamage <= 0) continue; // refused, not a target
				DamageData data = new DamageData(entity1, p, tempDamage);
				CustomDamage.customMobs(entity1, p, tempDamage, DamageType.PLAYER_MAGIC, data);
				if(data.damageDealt == 0) continue; // soaked or suppressed, not a hit
				damaged += 1;
				total += data.damageDealt;
			}
		}
		if(damaged > 0) {
			p.sendMessage(Utils.msg("<red>Your Implosion hit " + damaged + (damaged == 1 ? " enemy" : " enemies") + " for " + Utils.tenthNumber(total) + " damage."));
		}
		p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

		// wither shield
		// Gated by a standing shield and the refresh cooldown, so a cooldown under SHIELD_DURATION buys nothing.
		boolean shieldStanding = p.getScoreboardTags().contains("WitherShield");
		if(!shieldStanding && !Cooldowns.onCooldown(p, SHIELD_COOLDOWN)) { // reduced damage
			Cooldowns.start(p, SHIELD_COOLDOWN, shieldCooldown);
			double absorptionBefore = p.getAbsorptionAmount();
			AttributeModifier temp = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "witherShield"), absorption, AttributeModifier.Operation.ADD_NUMBER);
			p.getAttribute(Attribute.MAX_ABSORPTION).addModifier(temp);
			p.setAbsorptionAmount(absorptionBefore + absorption);
			p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0F, 0.66666F);
			Location finalL = l;
			Utils.scheduleTask(() -> { // convert to healing after 5 seconds
				p.setHealth(Math.min(p.getHealth() + (Math.max(0, (p.getAbsorptionAmount() - absorptionBefore)) * HEAL_SHARE), p.getAttribute(Attribute.MAX_HEALTH).getValue()));
				p.getAttribute(Attribute.MAX_ABSORPTION).removeModifier(temp);
				if(p.getAbsorptionAmount() > absorptionBefore) {
					p.setAbsorptionAmount(absorptionBefore);
				}
				p.playSound(finalL, Sound.ENTITY_PLAYER_LEVELUP, 2.0F, 2.0F);
			}, SHIELD_DURATION);
			p.addScoreboardTag("WitherShield");
			// Per-Hyperion, so CustomDamage can't read it off the tag alone.
			WITHER_SHIELDS.put(p.getUniqueId(), damageReduction);
			Utils.scheduleTask(() -> {
				p.removeScoreboardTag("WitherShield");
				WITHER_SHIELDS.remove(p.getUniqueId());
			}, SHIELD_DURATION);
		} else if(!shieldStanding) {
			// No shield and none coming: worth a message. Silent while one stands since Wither Impact is spammed.
			// Only reachable with a cooldown over SHIELD_DURATION; the full Hyperion passes 0.
			p.sendMessage(Utils.msg("<red>Your Wither Shield is on cooldown for "
					+ String.format("%.2f", Cooldowns.remaining(p, SHIELD_COOLDOWN) / 20.0) + " seconds!"));
		}
		return true;
	}

	/** Damage reduction per live shield, by owner. The {@code WitherShield} tag alone can't tell rungs apart. */
	private static final Map<UUID, Double> WITHER_SHIELDS = new HashMap<>();

	/** For a {@code WitherShield}-tagged entity. Falls back to the full Hyperion's 15%. */
	public static double witherShieldReduction(LivingEntity e) {
		return WITHER_SHIELDS.getOrDefault(e.getUniqueId(), 0.15);
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
