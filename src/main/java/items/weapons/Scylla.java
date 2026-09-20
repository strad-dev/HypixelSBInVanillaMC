package items.weapons;

import items.AbilityItem;
import listeners.CustomDamage;
import listeners.CustomItems;
import listeners.DamageType;
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

	/** This weapon's own attack damage, before any enchantment. Quoted on the lore line. */
	private static final double BASE_DAMAGE = 8;

	/** Share of the wielder's melee damage the implosion deals. */
	public static final double IMPLOSION_SHARE = 0.60;
	/** Flat bonus every Hyperion deals to Withers. <b>Read by
	 *  {@link listeners.CustomDamage#calculateFinalDamage} and quoted by the lore.</b> */
	public static final double WITHER_BONUS = 4;

	/**
	 * Share of the absorption still standing when the Wither Shield expires that becomes real health.
	 * <p>The same on every Hyperion, Manhunt rungs included - it used to be a {@code ManhuntTier} column, but
	 * the absorption HP already scales what there is to convert, so scaling the share as well made the rungs
	 * differ twice over for one effect.
	 */
	public static final double HEAL_SHARE = 0.50;

	public static ItemStack getItem() {
		return getItem(Map.of());
	}

	public static ItemStack getItem(Map<Enchantment, Integer> enchants) {
		return getItem(enchants, null);
	}

	public static final String ID = "skyblock/combat/scylla";

	/** Whether {@code item} is a full Hyperion, as opposed to a Manhunt one or a plain netherite sword. */
	public static boolean isScylla(ItemStack item) {
		if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) return false;
		return ID.equals(Utils.firstLorePlain(item.getItemMeta()));
	}

	/**
	 * The implosion damage this weapon would deal in {@code p}'s hands - the figure quoted on the lore, and
	 * the one {@link #onRightClick} pays out before any Smite/Bane bonus.
	 */
	public static double implosionDamage(@Nullable Player p, ItemStack weapon) {
		return Utils.meleeDamageWith(p, weapon) * IMPLOSION_SHARE;
	}

	/**
	 * The item, carrying {@code enchants} and with lore that says so. <b>The enchantments go on here rather
	 * than being applied by the caller afterwards</b> - that was the desync: the caller built the item, got
	 * lore for whatever it named, and then enchanted the stack by material type.
	 *
	 * <p>{@code p} is the player the implosion line is written for, and may be null (a recipe result, the
	 * creative palette) - the line then reads for a player with no other damage modifiers. It is kept in
	 * step by {@code ItemReloader}, which rewrites the held Hyperion on join and on every slot switch.
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

		// The implosion figure is read off the FINISHED stack, so the lore quotes exactly what the ability
		// pays out rather than a second copy of the same sum.
		double implosion = implosionDamage(p, scylla);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm(ID));
		lore.add(Utils.mm(""));
		lore.addAll(Utils.statLore(data, enchants));
		lore.addAll(Utils.bonusDamageLore(enchants));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Deals <red>+" + Utils.damageNumber(WITHER_BONUS) + "<gray> damage to Withers."));
		lore.add(Utils.mm(""));
		// This header is the line every lore tooltip in the plugin is measured against, so it is never
		// wrapped - see MinecraftFont.LORE_WIDTH.
		lore.add(Utils.mm("<gold>Ability: Wither Impact <green><bold>RIGHT CLICK"));
		lore.addAll(MinecraftFont.wrapLore(
				"<gray>Teleport <green>10 blocks<gray> ahead of you.  Then implode, dealing <red>"
				+ Utils.tenthNumber(implosion) + " damage <gray>to enemies within <green>10 blocks<gray>.  Also"
				+ " reduces damage taken by <red>15%<gray> and grants an Absorption Shield with <red>10 HP"
				+ " <gray>for <yellow>5 seconds<gray>."));
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
		// The same figures the melee pipeline pays out, so the number on the lore stays true through the
		// Sharpness/Smite retune - and all three are read, where the old else-if chain counted exactly one of
		// them and quoted numbers (level, level * 2) that matched neither vanilla nor this plugin.
		double targetDamage = Utils.meleeDamageWith(p, held);
		double smite = CustomDamage.smiteBonus(held.getEnchantmentLevel(Enchantment.SMITE));
		double bane = CustomDamage.smiteBonus(held.getEnchantmentLevel(Enchantment.BANE_OF_ARTHROPODS));

		return witherImpact(p, 10, 10, 10, 0.15, entity -> {
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
	 * Wither Impact: teleport {@code distance} blocks ahead, implode on everything within {@code radius},
	 * then put up the Wither Shield.
	 *
	 * <p><b>Shared with the Manhunt Hyperion</b>, which is the same ability on smaller numbers, so the two
	 * cannot drift apart - the placement search below is the fiddly part and there is no second copy of it.
	 *
	 * @param distance        blocks to teleport, and how far the block raytrace reaches
	 * @param radius          implosion radius
	 * @param absorption      absorption HP the shield grants
	 * @param damageReduction share taken off incoming damage while the shield is up
	 * @param damage          per-target implosion damage. <b>0 or less means skip that target</b> - it is
	 *                        never run through the pipeline at all, so it takes no knockback and is not
	 *                        counted as hit. The Manhunt Hyperion honours its per-Speedrunner implosion
	 *                        cooldown that way.
	 */
	public static boolean witherImpact(Player p, double distance, double radius, double absorption,
									   double damageReduction, ToDoubleFunction<LivingEntity> damage) {
		Location origin = p.getLocation().clone();
		Location l = null;
		// The world border counts as a solid block: clipped to it, so a Wither Impact aimed through the
		// border lands just inside rather than outside. Shared with the Manhunt Hyperion, like the rest.
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
		// Not every path above finds somewhere to stand: a SELF hit (the ray started inside a block) does
		// nothing at all, and the side-face backtrack can run out of room without ever finding a safe spot.
		// Both left l null and the implosion NPE'd on the particle call.  A blocked teleport does NOT cancel
		// Wither Impact - it still implodes, just where the player already is.
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
				// The counter reports what the implosion actually LANDED, not the figure it asked for: the
				// pipeline still has armour, the Ice Spray modifiers, a shield, a totem and the PvP layer to
				// put between the two.  Read back off DamageData rather than from the target's health, which
				// would cap every kill at whatever the mob had left and lose the overkill.
				double tempDamage = damage.applyAsDouble(entity1);
				if(tempDamage <= 0) continue; // refused outright - not a target, never mind a hit
				DamageData data = new DamageData(entity1, p, tempDamage);
				CustomDamage.customMobs(entity1, p, tempDamage, DamageType.PLAYER_MAGIC, data);
				if(data.damageDealt == 0) continue; // soaked to nothing, or suppressed: not a hit either
				damaged += 1;
				total += data.damageDealt;
			}
		}
		if(damaged > 0) {
			p.sendMessage(Utils.msg("<red>Your Implosion hit " + damaged + (damaged == 1 ? " enemy" : " enemies") + " for " + Utils.tenthNumber(total) + " damage."));
		}
		p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

		// wither shield
		if(!p.getScoreboardTags().contains("WitherShield")) { // reduced damage
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
			}, 101L);
			p.addScoreboardTag("WitherShield");
			// The reduction is per-Hyperion, so CustomDamage cannot read it off the tag alone.
			WITHER_SHIELDS.put(p.getUniqueId(), damageReduction);
			Utils.scheduleTask(() -> {
				p.removeScoreboardTag("WitherShield");
				WITHER_SHIELDS.remove(p.getUniqueId());
			}, 101);
		}
		return true;
	}

	/**
	 * How much every live Wither Shield takes off incoming damage, keyed by its owner. The share is the
	 * Hyperion's, not the shield's, so a Manhunt Hyperion's weaker shield can't be told apart from the full
	 * item's by the {@code WitherShield} tag that {@code CustomDamage} keys on.
	 */
	private static final Map<UUID, Double> WITHER_SHIELDS = new HashMap<>();

	/**
	 * The share to take off damage aimed at {@code e}, given it carries the {@code WitherShield} tag. Falls
	 * back to the full Hyperion's 15% for a shield nothing here put up.
	 */
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
