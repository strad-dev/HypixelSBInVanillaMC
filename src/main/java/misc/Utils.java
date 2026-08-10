package misc;

import listeners.CustomDamage;
import listeners.DamageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

import static listeners.CustomDamage.customMobs;

public class Utils {
	private static final Random random = new Random();
	private static final MiniMessage MM = MiniMessage.miniMessage();
	private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();
	private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

	/** Item display-name / lore line from a MiniMessage string. The default item italic is suppressed unless the line
	 *  explicitly sets italic itself (e.g. <italic> flavor text), so names/lore render non-italic like vanilla. */
	public static Component mm(String s) {
		return MM.deserialize(s).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
	}

	/** Chat message / entity custom-name component from a MiniMessage string (no forced italic). */
	public static Component msg(String s) {
		return MM.deserialize(s);
	}

	/** Chat component from a MiniMessage template with tag resolvers. Use Placeholder.unparsed(...) for untrusted
	 *  input (player names, chat text) so it is inserted literally and cannot inject MiniMessage tags. */
	public static Component msg(String template, TagResolver... resolvers) {
		return MM.deserialize(template, resolvers);
	}

	/** Legacy §-coded string of a component for Bukkit APIs that only accept a String (e.g. boss bar titles). */
	public static String legacyString(Component c) {
		return c == null ? "" : LEGACY.serialize(c);
	}

	/** MiniMessage string of a component to round-trip a Component (e.g. an item display name) back through the
	 *  MiniMessage-based helpers such as {@link #changeName(LivingEntity, String)} while preserving its formatting. */
	public static String mmString(Component c) {
		return c == null ? "" : MM.serialize(c);
	}

	/** Plain text (no formatting) of a component e.g. reading a custom name or item ID. */
	public static String plain(Component c) {
		return c == null ? "" : PLAIN.serialize(c);
	}

	/** Plain text of an item's first lore line, used to read the custom-item ID. */
	public static String firstLorePlain(ItemMeta meta) {
		if(meta == null) {
			return "";
		}
		List<Component> l = meta.lore();
		return l == null || l.isEmpty() ? "" : PLAIN.serialize(l.getFirst());
	}

	/**
	 * Updates the HP display of the given entity.
	 *
	 * @param entity The entity in question.
	 */
	public static void changeName(LivingEntity entity) {
		if(!(entity instanceof Player)) {
			int health = (int) Math.ceil(entity.getHealth() + entity.getAbsorptionAmount());
			int maxHealth = (int) Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).getValue();
			Component current = entity.customName();
			if(current == null) {
				changeName(entity, "<aqua>" + entity.getName());
				return;
			}
			// Swap the trailing "HP/maxHP" of the existing name in place, preserving all surrounding colors/formatting.
			// Uses the legacy (§) serializer, NOT MiniMessage: this runs on EVERY hit to EVERY custom mob, and the
			// MiniMessage serialize+parse round-trip here was a real per-hit hot-path cost. Legacy round-trips these
			// simple color/bold names losslessly at a fraction of the cost.
			String serialized = LEGACY.serialize(current).replaceFirst("\\d+/\\d+(\\s*)$", health + "/" + maxHealth + "$1");
			entity.customName(LEGACY.deserialize(serialized));
		}
	}

	/**
	 * Sets the entity's custom name to the given base name with health appended.
	 *
	 * @param entity   The entity in question.
	 * @param baseName The base display name (without health).
	 */
	public static void changeName(LivingEntity entity, String baseName) {
		if(!(entity instanceof Player)) {
			int health = (int) Math.ceil(entity.getHealth() + entity.getAbsorptionAmount());
			int maxHealth = (int) Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).getValue();
			entity.customName(msg(baseName + " <!bold><red>❤ <yellow>" + health + "/" + maxHealth));
		}
	}

	/**
	 * Gets the closest non-Spectator Player to the provided Entity<br>If all players on the server are in Spectator, a semi-random Player will be returned.
	 *
	 * @param e the entity
	 * @return the closest non-spectator player
	 */
	public static @Nullable Player getNearestPlayer(Entity e) {
		return getNearestPlayer(e, 2147483647);
	}

	/**
	 * Gets the closest non-Spectator Player to the provided Entity within a set amount of blocks.<br>If all players on the server are in Spectator, a semi-random Player will be returned.
	 *
	 * @param e      the entity
	 * @param blocks the furthest distance a player can be
	 * @return the closest non-spectator player
	 */
	public static @Nullable Player getNearestPlayer(Entity e, int blocks) {
		World world = e.getWorld();
		Location location = e.getLocation();
		ArrayList<Player> playersInWorld = new ArrayList<>(world.getEntitiesByClass(Player.class));
		if(playersInWorld.isEmpty()) {
			return null;
		}
		for(int i = 0; i < playersInWorld.size(); i++) {
			if(playersInWorld.get(i).getGameMode().equals(GameMode.SPECTATOR) && playersInWorld.size() > 1) {
				playersInWorld.remove(i);
				i--;
			}
		}
		playersInWorld.sort(Comparator.comparingDouble(o -> o.getLocation().distanceSquared(location)));
		if(e.getLocation().distanceSquared(playersInWorld.getFirst().getLocation()) > (double) blocks * blocks) {
			return null;
		}
		return playersInWorld.getFirst();
	}

	/**
	 * Plays a sound for every player on the server
	 *
	 * @param s The sound to play
	 */
	public static void playGlobalSound(Sound s) {
		Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p, s, 1.0F, 1.0F));
	}

	/**
	 * Plays a sound for every player on the server
	 *
	 * @param s      The sound to play
	 * @param volume The volume of the sound
	 * @param pitch  The pitch of the sound
	 */
	public static void playGlobalSound(Sound s, float volume, float pitch) {
		Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p, s, volume, pitch));
	}

	/**
	 * Notifies the player that they received a rare drop
	 *
	 * @param p       The Player that received the drop
	 * @param message The name of the item
	 */
	public static void sendRareDropMessage(Player p, String message) {
		if(p != null) {
			p.sendMessage(msg("<gold><bold>RARE DROP!  <reset>" + message));
			Bukkit.getLogger().info(p.getName() + " dropped a " + message);
			p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
		}
	}

	/**
	 * Shoot a beam dealing damage to everything in its path!
	 *
	 * @param origin      The Entity shooting the beam
	 * @param destination The Entity that is being targeted<br>If this is the same Entity as the origin, the beam is shot in the direction the Entity is looking at.
	 * @param color       The color of the beam
	 * @param distance    How far the beam should go
	 * @param pierce      How many enemies should be pierced
	 * @param damage      The damage of the beam
	 */
	public static void shootBeam(Entity origin, Entity destination, Color color, long distance, long pierce, double damage) {
		Location l = origin.getLocation();
		if(origin instanceof LivingEntity entity) {
			l = entity.getEyeLocation();
		}
		Vector v;
		if(origin.equals(destination) || destination == null) {
			v = l.getDirection();
		} else {
			Location destinationLocation = destination.getLocation().add(0, destination.getHeight() / 2, 0);
			double x = destinationLocation.getX() - l.getX();
			double y = destinationLocation.getY() - l.getY();
			double z = destinationLocation.getZ() - l.getZ();
			v = new Vector(x, y, z);
		}
		v.setX(v.getX() / 5);
		v.setY(v.getY() / 5);
		v.setZ(v.getZ() / 5);
		World world = origin.getWorld();
		Set<Entity> damagedEntities = new HashSet<>();
		damagedEntities.add(origin);
		for(int i = 0; i < distance * 5 && pierce > 0; i++) {
			if(l.getBlock().getType().isSolid()) {
				break;
			}
			Collection<Entity> entities = world.getNearbyEntities(l, 1, 1, 1);
			for(Entity entity : entities) {
				if(entity instanceof LivingEntity temp && !damagedEntities.contains(entity)) {
					damagedEntities.add(entity);
					customMobs(temp, origin, damage, DamageType.RANGED_SPECIAL);
					pierce--;
				}
			}
			Particle.DustOptions particle = new Particle.DustOptions(color, 1.0F);
			world.spawnParticle(Particle.DUST, l, 1, particle);
			l.add(v);
		}
	}

	/**
	 * Spawns a custom TNT<br>If the fuse is 0 ticks, the TNT entity will not be spawned
	 *
	 * @param spawner stores the spawner of the TNT; this entity is ALWAYS immune to the TNT's damage
	 * @param l       the location the TNT should be spawned at
	 * @param fuse    the duration before the TNT explodes - the TNT entity's fuse is 20 ticks longer
	 * @param radius  the radius in which the damage is effective
	 * @param damage  the amount of damage to deal
	 * @param immune  represents which entity types are immune to damage; by default, no entity types are immune and the spawner is always immune
	 */
	public static void spawnTNT(Entity spawner, Location l, int fuse, int radius, int damage, List<EntityType> immune) {
		if(fuse == 0) {
			List<Entity> entities = (List<Entity>) l.getWorld().getNearbyEntities(l, radius, radius, radius);
			for(Entity entity : entities) {
				if(!entity.equals(spawner) && entity instanceof LivingEntity entity1 && !immune.contains(entity.getType()) && (entity instanceof Player p && p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR)) {
					CustomDamage.customMobs(entity1, spawner, damage, DamageType.PLAYER_MAGIC);
				}
			}
			spawner.getWorld().spawnParticle(Particle.EXPLOSION, spawner.getLocation(), Math.min((int) Math.pow(radius, 3), 16384), radius, radius / 2.0, radius);
			spawner.getWorld().playSound(spawner.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 0.6F);
		} else {
			TNTPrimed tnt = (TNTPrimed) l.getWorld().spawnEntity(l, EntityType.TNT);
			tnt.setFuseTicks(fuse + 20);
			spawner.getWorld().playSound(spawner.getLocation(), Sound.ENTITY_TNT_PRIMED, 2.0F, 1.0F);

			Utils.scheduleTask(() -> {
				List<Entity> entities = tnt.getNearbyEntities(radius, radius, radius);
				for(Entity entity : entities) {
					if(!entity.equals(spawner) && entity instanceof LivingEntity entity1 && !immune.contains(entity.getType()) && (entity instanceof Player p && p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR)) {
						CustomDamage.customMobs(entity1, spawner, damage, DamageType.PLAYER_MAGIC);
					}
				}
				tnt.getWorld().spawnParticle(Particle.EXPLOSION, tnt.getLocation(), Math.min((int) Math.pow(radius, 3), 16384), radius, radius / 2.0, radius);
				tnt.getWorld().playSound(tnt.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 0.6F);
				tnt.remove();
			}, fuse);
		}
	}

	/**
	 * Drops an anvil on a location.<br>The anvil is a falling block that deletes itself on impact instead of landing as
	 * a real block, so an anvil barrage doesn't litter the arena with anvils (or anvil drops) to clean up afterwards.
	 *
	 * @param l the location the anvil should fall from; nothing is spawned if that space isn't air
	 * @return whether an anvil was spawned
	 */
	public static boolean spawnAnvil(Location l) {
		Block b = l.getBlock();
		if(!b.getType().equals(Material.AIR)) {
			return false;
		}
		// Vanilla only arms anvil fall damage in AnvilBlock.falling(), which runs when a *placed* block turns itself into
		// an entity - a directly spawned falling block starts at hurtEntities = false with 0 damage per block, so both
		// have to be set here to match a real anvil (2 per block, capped at 40).
		l.getWorld().spawn(b.getLocation().add(0.5, 0, 0.5), FallingBlock.class, fb -> {
			fb.setBlockData(Material.DAMAGED_ANVIL.createBlockData());
			fb.setHurtEntities(true);
			fb.setDamagePerBlock(2.0F);
			fb.setMaxDamage(40);
			fb.setCancelDrop(true); // on landing: discard the entity, place no block, drop nothing
			fb.setDropItem(false); // ...and drop nothing either if it never lands and times out over a void
		});
		return true;
	}

	/**
	 * Teleports the entity to a random position in a given radius from its current location.<br>The entity lands on
	 * the nearest ground it actually fits on (see {@link #getNearestValidBlockYAt}).
	 *
	 * @param e      The entity to be teleported
	 * @param radius The radius of the randomness
	 */
	public static void teleport(Entity e, int radius) {
		teleport(e, e.getLocation(), radius, true);
	}

	/**
	 * Teleports the entity to a random position in a given radius from its current location, optionally silently.<br>
	 * Bosses repositioning themselves mid-phase pass {@code false} - they have their own audio cue and the enderman
	 * warp on top of it reads as a different mechanic.
	 *
	 * @param e      The entity to be teleported
	 * @param radius The radius of the randomness
	 * @param sound  Whether to play the enderman teleport sound
	 */
	public static void teleport(Entity e, int radius, boolean sound) {
		teleport(e, e.getLocation(), radius, sound);
	}

	/**
	 * Teleports the entity to a random position in a given radius from the given location.<br>The entity lands on
	 * the nearest ground its own hitbox fits on, so it can't be stuffed into a gap that's too short for it.
	 *
	 * @param e      The entity to be teleported
	 * @param center The center of the radius to teleport from
	 * @param radius The radius of the randomness
	 */
	public static void teleport(Entity e, Location center, int radius) {
		teleport(e, center, radius, true);
	}

	/**
	 * Teleports the entity to a random position in a given radius from the given location, optionally silently.<br>
	 * A {@code radius} of 0 is legal and means "stay put, but snap to a spot that actually fits" - several bosses use
	 * it to unstick themselves when entering a phase.
	 *
	 * @param e      The entity to be teleported
	 * @param center The center of the radius to teleport from
	 * @param radius The radius of the randomness
	 * @param sound  Whether to play the enderman teleport sound
	 */
	public static void teleport(Entity e, Location center, int radius, boolean sound) {
		e.teleport(randomLocation(center, radius, e.getHeight()));
		if(sound) {
			e.getWorld().playSound(e.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
		}
	}

	/**
	 * Finds a random location given a center and a radius, snapped to the nearest ground (by Y).<br>Use the
	 * {@code height} overload for anything that has a hitbox to fit.
	 *
	 * @param center The center
	 * @param radius How far away at most
	 * @return The randomized Location
	 */
	public static Location randomLocation(Location center, int radius) {
		Location l = scatter(center, radius);
		l.setY(getNearestBlockYAt(l));
		return l;
	}

	/**
	 * Finds a random location given a center and a radius, snapped to the nearest ground that a body of the given
	 * height fits on.
	 *
	 * @param center The center
	 * @param radius How far away at most
	 * @param height The height (in blocks) that must fit above the ground - e.g. {@code entity.getHeight()}
	 * @return The randomized Location
	 */
	public static Location randomLocation(Location center, int radius, double height) {
		Location l = scatter(center, radius);
		l.setY(getNearestValidBlockYAt(l, height));
		return l;
	}

	/** Randomizes X/Z within the radius, leaving Y alone. Never mutates the caller's Location. */
	private static Location scatter(Location center, int radius) {
		Vector added = new Vector(random.nextInt(radius * 2 + 1) - radius, 0, random.nextInt(radius * 2 + 1) - radius);
		return center.clone().add(added);
	}

	/**
	 * The Y of the first surface found searching outward (down first, then up) from the location's own Y: the block
	 * above the nearest non-air block in that column. Ignores hitboxes entirely - prefer
	 * {@link #getNearestValidBlockYAt} when placing something that can get stuck.
	 */
	public static int getNearestBlockYAt(Location l) {
		World w = l.getWorld();
		int min = w.getMinHeight();
		int max = w.getMaxHeight();
		int yUp = (int) l.getY();
		int yDown = yUp;
		while(yUp < max || yDown > min) {
			Location temp = l.clone();
			if(yDown > min) {
				temp.setY(yDown);
				Block b = temp.getBlock();
				if(b.getType() != Material.AIR && b.getType() != Material.VOID_AIR) {
					yDown ++;
					return yDown;
				}
				yDown--;
			}
			if(yUp < max) {
				temp.setY(yUp);
				Block b = temp.getBlock();
				if(b.getType() != Material.AIR && b.getType() != Material.VOID_AIR) {
					yUp ++;
					return yUp;
				}
				yUp++;
			}
		}
		return max;
	}

	/**
	 * The Y a body of the given height can actually stand at, searching outward (down first, then up) from the
	 * location's own Y for the closest spot in that column with solid ground underfoot AND enough clear headroom
	 * for the whole hitbox. This is what stops a tall mob being teleported/spawned into a gap it doesn't fit in -
	 * a 2.9-block Enderman won't be dropped into a 1-block pocket or suffocated in a ceiling.
	 *
	 * <p>Falls back to {@link #getNearestBlockYAt} when nothing in the column fits, so a mob too tall for anywhere
	 * in its column still lands on a surface rather than mid-air.
	 *
	 * @param l      The column to search (its Y is the starting point)
	 * @param height Hitbox height in blocks - {@code entity.getHeight()} for a live entity, otherwise the mob's
	 *               natural height (scaled entities are taller than their vanilla type)
	 */
	public static int getNearestValidBlockYAt(Location l, double height) {
		World w = l.getWorld();
		int needed = Math.max(1, (int) Math.ceil(height));
		int min = w.getMinHeight() + 1;         // needs a block underneath to stand on
		int max = w.getMaxHeight() - needed;    // needs the whole hitbox under the build limit
		if(min > max) return getNearestBlockYAt(l);
		int start = Math.min(Math.max(l.getBlockY(), min), max);
		for(int offset = 0; offset <= max - min; offset++) {
			int down = start - offset;
			if(down >= min && fits(l, down, needed)) return down;
			int up = start + offset;
			if(offset > 0 && up <= max && fits(l, up, needed)) return up;
		}
		return getNearestBlockYAt(l);
	}

	/** True if {@code needed} blocks of headroom starting at y are clear and the block below y is solid ground. */
	private static boolean fits(Location column, int y, int needed) {
		World w = column.getWorld();
		int x = column.getBlockX();
		int z = column.getBlockZ();
		if(!w.getBlockAt(x, y - 1, z).getType().isSolid()) return false;
		for(int i = 0; i < needed; i++) {
			if(!w.getBlockAt(x, y + i, z).isPassable()) return false;
		}
		return true;
	}

	/** True if nothing is between this location and the sky - the vanilla "can rain/lightning reach you" test. */
	public static boolean underOpenSky(Location l) {
		return l.getWorld().getHighestBlockYAt(l) <= l.getBlockY();
	}

	public static void spawnGuards(LivingEntity entity, int num) {
		for(int i = 0; i < num; i++) {
			WitherSkeleton e = (WitherSkeleton) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.WITHER_SKELETON);
			e.getEquipment().clear();
			ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
			sword.addEnchantment(Enchantment.KNOCKBACK, 1);
			ItemStack shield = new ItemStack(Material.SHIELD);

			Objects.requireNonNull(e.getEquipment()).setItemInMainHand(sword);
			e.getEquipment().setItemInMainHandDropChance(0.0F);
			e.getEquipment().setItemInOffHand(shield);
			e.getEquipment().setItemInOffHandDropChance(0.0F);

			//noinspection DuplicatedCode
			e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(50.0);
			e.setHealth(50.0);
			Utils.changeName(e, "<gold><bold>﴾ <red><bold>Wither Guard<gold><bold> ﴿");
			e.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.5);
			e.getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER).setBaseValue(0.0);
			Utils.setupBoss(e, Utils.getNearestPlayer(entity), "GuardSkeleton");
		}
		entity.getWorld().playSound(entity, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0F, 2.0F);
	}

	/**
	 * Damages an item
	 *
	 * @param user   The owner of the item being damaged
	 * @param item   The item to be damage
	 * @param damage The amount of damage to deal.  Partial amounts equate to the chance to deal that extra damage, e.g. 3.33 means 3 damage with 33% chance to deal +1 damage.
	 */
	public static void damageItem(Entity user, ItemStack item, double damage) {
		int maxDurability = item.getType().getMaxDurability();
		if(item.getItemMeta() instanceof Damageable d && !d.isUnbreakable() && maxDurability != 0) {
			if(!(user instanceof Player p) || (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE)) {
				double finalDamage = damage / (double) (d.getEnchantLevel(Enchantment.UNBREAKING) + 1);
				int guaranteedDamage = (int) finalDamage;
				if(finalDamage % 1 > random.nextDouble()) {
					guaranteedDamage++;
				}
				int newDamage = d.getDamage() + guaranteedDamage;

				if(newDamage >= maxDurability) {
					if(user instanceof Player p) {
						// Fire the break event for advancements
						PlayerItemBreakEvent breakEvent = new PlayerItemBreakEvent(p, item.clone());
						Bukkit.getPluginManager().callEvent(breakEvent);

						// Update statistics
						p.incrementStatistic(Statistic.BREAK_ITEM, item.getType());

						// Play break sound and show animation manually
						p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);

						// advancements
						ServerPlayer serverPlayer = ((CraftPlayer) p).getHandle();
						net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
						nmsItem.setDamageValue(maxDurability);
						CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(serverPlayer, nmsItem, maxDurability);
					}

					// Remove the item
					item.setAmount(0);
				} else {
					// advancements
					if(user instanceof Player p) {
						ServerPlayer serverPlayer = ((CraftPlayer) p).getHandle();
						net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
						nmsItem.setDamageValue(newDamage);
						CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(serverPlayer, nmsItem, newDamage);
					}

					// Item survives
					d.setDamage(newDamage);
					item.setItemMeta(d);
				}
			}
		}
	}

	public static void scheduleTask(Runnable task, long delay) {
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), task, delay);
	}

	/**
	 * Gives the entity full water-movement efficiency (equivalent to Depth Strider 3) so bosses and
	 * their subentities don't wade slowly through water. No-op if the entity lacks the attribute
	 * (e.g. flying withers/dragons that never had it registered).
	 */
	public static void applyDepthStrider(LivingEntity entity) {
		var attr = entity.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY);
		if(attr != null) {
			attr.setBaseValue(1.0);
		}
	}

	/**
	 * Applies the setup shared by every SkyBlock boss and boss-spawned subentity: no item pickup,
	 * infinite fire resistance, name always visible, persistence across chunk unloads, the
	 * "SkyblockBoss" tag plus any {@code extraTags} (e.g. the mob's registry key), and a target.
	 * Pass {@code target == null} to leave targeting alone. Full water-movement efficiency is applied
	 * here too: the SkyblockBoss sweep in {@link listeners.CustomMobs} only fires on EntitySpawnEvent,
	 * so summon-item bosses that convert an existing mob (e.g. Atoned Horror) would otherwise miss it.
	 */
	public static void setupBoss(Mob e, @Nullable Player target, String... extraTags) {
		e.setCanPickupItems(false);
		applyDepthStrider(e);
		e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		if(target != null) {
			e.setTarget(target);
		}
		e.setCustomNameVisible(true);
		e.addScoreboardTag("SkyblockBoss");
		for(String tag : extraTags) {
			e.addScoreboardTag(tag);
		}
		e.setPersistent(true);
		e.setRemoveWhenFarAway(false);
	}

	/**
	 * Applys the given function to all Players that are in Survival or in the given radius of the given Entity
	 * @param entity   The Entity
	 * @param radius   The radius in which to apply th efucntion
	 * @param function The function to apply
	 */
	public static void applyToAllNearbyPlayers(LivingEntity entity, int radius, Consumer<Player> function) {
		entity.getNearbyEntities(radius, radius, radius).stream().filter(e -> e instanceof Player p && (p.getGameMode() == GameMode.ADVENTURE || p.getGameMode() == GameMode.SURVIVAL)).map(Player.class::cast).forEach(function);
	}

	/**
	 * Sends a packet to every player on the server.
	 *
	 * @param pkt Packet to send
	 */
	public static void broadcastPacket(Packet<?> pkt) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.isOnline()) {
				((CraftPlayer) p).getHandle().connection.send(pkt);
			}
		}
	}
}