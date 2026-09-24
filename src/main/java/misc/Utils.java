package misc;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Enchantable;
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
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
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

	/** Item name / lore line from MiniMessage. Default item italic is off unless the line sets it (<italic> flavor
	 *  text), so it renders non-italic like vanilla. */
	public static Component mm(String s) {
		return MM.deserialize(s).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
	}

	/** Chat message / entity custom-name component from a MiniMessage string (no forced italic). */
	public static Component msg(String s) {
		return MM.deserialize(s);
	}

	/** Chat component from a MiniMessage template with resolvers. Use Placeholder.unparsed(...) for untrusted input
	 *  (names, chat text) so it can't inject tags. */
	public static Component msg(String template, TagResolver... resolvers) {
		return MM.deserialize(template, resolvers);
	}

	/** Legacy §-coded string of a component for Bukkit APIs that only accept a String (e.g. boss bar titles). */
	public static String legacyString(Component c) {
		return c == null ? "" : LEGACY.serialize(c);
	}

	/** MiniMessage string of a component, to round-trip it (e.g. an item name) through helpers like
	 *  {@link #changeName(LivingEntity, String)} keeping its formatting. */
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
	 * Damage figure for lore: no trailing {@code .0}, at most two decimals (Sharpness is 0.75 a level).
	 */
	public static String damageNumber(double d) {
		if(d == Math.rint(d)) {
			return String.valueOf((long) d);
		}
		return new java.math.BigDecimal(d).setScale(2, java.math.RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
	}

	/**
	 * Enchantability of high-end SkyBlock items (Hyperion, Dark Claymore, custom armour, AOTV, Divan's Pickaxe).
	 * Well above vanilla's best (gold, 22), so a level-30 table reaches the top cost bands.
	 *
	 * @see #setEnchantability
	 */
	public static final int SKYBLOCK_ENCHANTABILITY = 30;

	/**
	 * Sets vanilla's {@code minecraft:enchantable} component (the material's value is just the default).
	 *
	 * <p>Buys <b>better rolls, not a cheaper price</b>: the table adds {@code 1 + rand(value/4 + 1) + rand(value/4 + 1)}
	 * to the slot's level before picking enchants. XP, lapis and anvil cost unchanged. Vanilla: gold 22, wood and
	 * netherite 15, iron 14, copper 13, diamond 10, stone 5, bow 1.
	 *
	 * <p><b>Call last</b>, after the final {@code setItemMeta}: an {@code ItemMeta} is a component snapshot and
	 * applying one puts the default back.
	 *
	 * <p>{@code value <= 0} removes the component: <b>unenchantable at a table</b> (books and anvils still work).
	 * Deliberate for the Stick Manhunt Hyperion.
	 */
	public static void setEnchantability(ItemStack item, int value) {
		if(value <= 0) {
			item.unsetData(DataComponentTypes.ENCHANTABLE);
			return;
		}
		item.setData(DataComponentTypes.ENCHANTABLE, Enchantable.enchantable(value));
	}

	/**
	 * Rounds to 0.1 for lore quoting a live damage figure; the pipeline keeps full precision.
	 */
	public static String tenthNumber(double d) {
		return damageNumber(Math.round(d * 10) / 10.0);
	}

	/**
	 * A 0-1 SHARE as a lore percentage: {@code 0.15 -> "15%"}, {@code 0.335 -> "33.5%"}. Takes the share since every
	 * caller holds one, so the x100 happens once here. Rounded to 0.1%, then {@link #damageNumber} drops a {@code .0}.
	 */
	public static String percent(double share) {
		return damageNumber(Math.round(share * 1000) / 10.0) + "%";
	}

	/** One {@link #statLore} line: attribute, label, and whether it's a 0-1 SHARE (printed as a percentage). */
	private record StatLine(Attribute attribute, String label, boolean share) {}

	/** Every stat the lore quotes, IN QUOTED ORDER. Absent attributes print nothing, so one list covers armour,
	 *  weapons and tools. Add a row to give a new attribute a line. */
	private static final List<StatLine> STAT_LINES = List.of(
			new StatLine(Attribute.ATTACK_DAMAGE, "Damage", false),
			new StatLine(Attribute.ARMOR, "Armor", false),
			new StatLine(Attribute.ENTITY_INTERACTION_RANGE, "Swing Range", false),
			new StatLine(Attribute.BLOCK_BREAK_SPEED, "Mining Speed", false),
			new StatLine(Attribute.BLOCK_INTERACTION_RANGE, "Range", false),
			new StatLine(Attribute.KNOCKBACK_RESISTANCE, "Knockback Resistance", true),
			new StatLine(Attribute.MOVEMENT_SPEED, "Speed", true),
			new StatLine(Attribute.FALL_DAMAGE_MULTIPLIER, "Fall Damage", true));

	/** @see #statLore(ItemMeta, Map) */
	public static List<Component> statLore(ItemMeta data) {
		return statLore(data, null);
	}

	/**
	 * An item's whole stat block, read off the modifiers <b>already on {@code data}</b>, not hand-written figures.
	 * <b>Call after the {@code addAttributeModifier} calls and before {@code lore(...)}</b>; pass {@code enchants} for
	 * a weapon so Sharpness lands on the damage line ({@link #damageLore}), none for armour and tools.
	 *
	 * <p>Hand-written stats drift silently: the Crown of the Wither King gave +3 damage while its lore said +2, the
	 * Warden Helmet +2 while it said +1. The numbers now live only in the modifiers.
	 *
	 * <p>Order and labels from {@link #STAT_LINES}. Flat amounts print as-is, a {@code share} as a signed percentage,
	 * {@code MULTIPLY_SCALAR_1} through {@link #multiplier}. <b>Damage is special twice</b>: a tool's {@code -1000}
	 * means "not a weapon" and floors at 0, so it reads {@code 0} ({@link #damageLore} gets the raw amount to tell
	 * that from a weapon adding nothing); and Sharpness folds into it. No item has flat + scalar on one attribute;
	 * if one does, it gets two lines.
	 */
	public static List<Component> statLore(ItemMeta data, @Nullable Map<Enchantment, Integer> enchants) {
		List<Component> out = new ArrayList<>();
		for(StatLine line : STAT_LINES) {
			Collection<AttributeModifier> modifiers = data.getAttributeModifiers(line.attribute());
			if(modifiers == null || modifiers.isEmpty()) continue;

			double flat = 0;
			double scalar = 0;
			for(AttributeModifier modifier : modifiers) {
				if(modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_SCALAR_1) {
					scalar += modifier.getAmount();
				} else {
					flat += modifier.getAmount();
				}
			}

			if(line.attribute() == Attribute.ATTACK_DAMAGE) {
				out.add(damageLore(flat, enchants));
				continue;
			}
			if(scalar != 0) {
				out.add(mm("<gray>" + line.label() + ": <red>" + multiplier(scalar)));
			}
			if(flat != 0) {
				out.add(mm("<gray>" + line.label() + ": <red>" + (flat > 0 ? "+" : "-")
						+ (line.share() ? percent(Math.abs(flat)) : damageNumber(Math.abs(flat)))));
			}
		}
		return out;
	}

	/**
	 * A {@code MULTIPLY_SCALAR_1} amount for lore: a buff as a multiplier ({@code x2}, {@code x1.33}), a penalty as
	 * a reduction ({@code -25%}). Vanilla does {@code value *= 1 + amount}, so both are the same number.
	 */
	private static String multiplier(double amount) {
		return amount > 0 ? "x" + damageNumber(1 + amount) : "-" + percent(Math.abs(amount));
	}

	/**
	 * Player's melee damage with {@code weapon} in main hand, Sharpness included. Resolved by hand, not
	 * {@code getValue()}, since the weapon <b>isn't necessarily held</b> (Hyperion lore is rewritten on slot switch,
	 * pickup, login). Main-hand modifiers are dropped and {@code weapon}'s put in; everything else (Strength,
	 * Weakness, armour) still counts.
	 *
	 * @see CustomDamage#sharpnessBonus
	 */
	public static double meleeDamageWith(@Nullable Player p, @Nullable ItemStack weapon) {
		double base = 1; // a player's own attack damage, before any weapon
		double add = 0;
		double addScalar = 0;
		List<Double> multiply = new ArrayList<>();

		if(p != null) {
			// The current main-hand item must NOT count, and can't be found by slot group: a modifier off a LIVE
			// attribute always comes back EquipmentSlotGroup.ANY (NMS has no slot; CraftAttributeInstance.convert
			// fills ANY in). The old slot test never matched and counted the held weapon TWICE: the Hyperion
			// quoted 60% of 30.5 for a 22.5 melee. Matched by KEY instead: the held item's modifier keys plus
			// minecraft:base_attack_damage (armour never uses it, so a damage helmet still counts).
			Set<NamespacedKey> heldKeys = new HashSet<>();
			heldKeys.add(NamespacedKey.minecraft("base_attack_damage"));
			ItemStack held = p.getInventory().getItemInMainHand();
			if(held.hasItemMeta()) {
				Collection<AttributeModifier> heldOwn = held.getItemMeta().getAttributeModifiers(Attribute.ATTACK_DAMAGE);
				if(heldOwn != null) {
					for(AttributeModifier modifier : heldOwn) heldKeys.add(modifier.getKey());
				}
			}

			AttributeInstance instance = p.getAttribute(Attribute.ATTACK_DAMAGE);
			if(instance != null) {
				base = instance.getBaseValue();
				for(AttributeModifier modifier : instance.getModifiers()) {
					if(heldKeys.contains(modifier.getKey())) continue;
					switch(modifier.getOperation()) {
						case ADD_NUMBER -> add += modifier.getAmount();
						case ADD_SCALAR -> addScalar += modifier.getAmount();
						case MULTIPLY_SCALAR_1 -> multiply.add(modifier.getAmount());
					}
				}
			}
		}

		if(weapon != null && weapon.hasItemMeta()) {
			Collection<AttributeModifier> own = weapon.getItemMeta().getAttributeModifiers(Attribute.ATTACK_DAMAGE);
			if(own != null) {
				for(AttributeModifier modifier : own) {
					switch(modifier.getOperation()) {
						case ADD_NUMBER -> add += modifier.getAmount();
						case ADD_SCALAR -> addScalar += modifier.getAmount();
						case MULTIPLY_SCALAR_1 -> multiply.add(modifier.getAmount());
					}
				}
			}
		}

		// The order AttributeInstance.calculateValue uses: every ADD_NUMBER, then each ADD_SCALAR off the
		// summed base, then each MULTIPLY_SCALAR_1 in turn.
		double value = base + add;
		value += base * addScalar;
		for(double factor : multiply) {
			value *= 1 + factor;
		}

		if(weapon != null) {
			value += CustomDamage.sharpnessBonus(weapon.getEnchantmentLevel(Enchantment.SHARPNESS));
		}
		// Strength's vanilla +3/level is in the sum above; bring it down to our +2, same as
		// CustomDamage.rebuildMelee, so lore and implosion match a real swing.
		if(p != null) {
			value -= CustomDamage.strengthPenalty(p);
		}
		return Math.max(0, value);
	}

	/**
	 * The {@code Damage: +N} line for a weapon: its attack damage plus the Sharpness it actually carries.
	 *
	 * <p><b>Built from the item's enchantments, not an argument.</b> Before, the palette and duel kit got lore for
	 * the enchant they named and THEN enchanted by material, so a Claymore saying {@code Damage: +9} carried
	 * Sharpness VII. It also read one enchant only and quoted numbers ({@code level * 2} for Smite) the pipeline
	 * no longer paid.
	 *
	 * @see CustomDamage#sharpnessBonus
	 */
	public static Component damageLore(double baseDamage, @Nullable Map<Enchantment, Integer> enchants) {
		int sharpness = enchants == null ? 0 : enchants.getOrDefault(Enchantment.SHARPNESS, 0);
		// Floored here, not by the caller, because the sign below reads the RAW amount.
		double total = Math.max(0, baseDamage + CustomDamage.sharpnessBonus(sharpness));
		// `Damage: 0` and `Damage: +0` differ. An ability tool subtracts out of being a weapon (-1000) and reads
		// `0`. A weapon adding nothing YET (Manhunt Stick, bottom of the ladder) keeps its sign.
		boolean bonus = baseDamage >= 0;
		return mm("<gray>Damage: <red>" + (total > 0 || bonus ? "+" : "") + damageNumber(total));
	}

	/**
	 * The {@code Bonus Undead/Arthropod Damage} lines for a weapon, blank separator included, or nothing at
	 * all if it carries neither enchantment.  Both are listed when an item somehow has both.
	 *
	 * @see #damageLore
	 */
	public static List<Component> bonusDamageLore(Map<Enchantment, Integer> enchants) {
		List<Component> out = new ArrayList<>();
		if(enchants == null) {
			return out;
		}
		int smite = enchants.getOrDefault(Enchantment.SMITE, 0);
		int bane = enchants.getOrDefault(Enchantment.BANE_OF_ARTHROPODS, 0);
		if(smite <= 0 && bane <= 0) {
			return out;
		}
		out.add(mm(""));
		if(smite > 0) {
			out.add(mm("<gray>Bonus Undead Damage: <red>+" + damageNumber(CustomDamage.smiteBonus(smite))));
		}
		if(bane > 0) {
			out.add(mm("<gray>Bonus Arthropod Damage: <red>+" + damageNumber(CustomDamage.smiteBonus(bane))));
		}
		return out;
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
			// Swap the trailing "HP/maxHP" in place, keeping formatting. Legacy (§) serializer, NOT MiniMessage: this
			// runs on EVERY hit to EVERY custom mob and the MiniMessage round-trip was a real hot-path cost. Legacy
			// round-trips these simple names losslessly for a fraction of it.
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
	 * Drops an anvil on a location.<br>It deletes itself on impact instead of landing, so a barrage doesn't litter
	 * the arena with anvils or drops.
	 *
	 * @param l the location the anvil should fall from; nothing is spawned if that space isn't air
	 * @return whether an anvil was spawned
	 */
	public static boolean spawnAnvil(Location l) {
		Block b = l.getBlock();
		if(!b.getType().equals(Material.AIR)) {
			return false;
		}
		// Vanilla arms anvil damage only in AnvilBlock.falling() (placed block -> entity); a spawned falling block
		// has hurtEntities = false and 0 per block, so set both to match a real anvil (2 per block, max 40).
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
	 * How much of a player's hitbox must stay inside the border. Vanilla shoves an entity back once its box
	 * crosses, so landing ON the line is outside.
	 */
	private static final double BORDER_MARGIN = 0.5;

	/**
	 * How far a teleport may go from {@code origin} along {@code direction} before the world border, capped at
	 * {@code distance}. <b>The border is a solid block</b>: an ability that would carry a player through stops just
	 * inside, like a wall. Past it, vanilla shoves them back, in chunks nobody needed loaded.
	 *
	 * <p>Only the four walls exist, so X and Z only. Square, centred on {@code getCenter()}, {@code getSize()} full
	 * width. A player already outside can still move: the wall ahead is used, so moving back in works, further out
	 * doesn't.
	 */
	public static double borderDistance(Location origin, Vector direction, double distance) {
		WorldBorder border = origin.getWorld().getWorldBorder();
		double half = border.getSize() / 2 - BORDER_MARGIN;
		Location centre = border.getCenter();
		double limit = Math.min(distance, borderAxis(origin.getX() - centre.getX(), direction.getX(), half, distance));
		limit = Math.min(limit, borderAxis(origin.getZ() - centre.getZ(), direction.getZ(), half, distance));
		return Math.max(0, limit);
	}

	/** One axis of {@link #borderDistance}: the blocks travelled before this pair of walls is reached. */
	private static double borderAxis(double offset, double towards, double half, double distance) {
		if(towards == 0) return distance; // parallel to these two walls, so they are never reached
		double hit = ((towards > 0 ? half : -half) - offset) / towards;
		return hit < 0 ? 0 : hit; // already past it and still heading out
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
	 * Bosses repositioning mid-phase pass {@code false}: they have their own audio cue.
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
	 * the nearest ground its own hitbox fits on.
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
	 * {@code radius} 0 = stay put but snap to a spot that fits; bosses use it to unstick when entering a phase.
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
	 * Y of the first surface searching outward (down, then up) from the location's Y: the block above the nearest
	 * non-air block. Ignores hitboxes; prefer {@link #getNearestValidBlockYAt} for anything that can get stuck.
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
	 * Y a body of the given height can stand at, searching outward (down, then up) from the location's Y for the
	 * closest spot with solid ground AND headroom for the whole hitbox, so a 2.9-block Enderman isn't dropped into a
	 * 1-block pocket. Falls back to {@link #getNearestBlockYAt} if nothing fits, so it still lands on a surface.
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
	 * Full water-movement efficiency (= Depth Strider 3) so bosses and subentities don't wade slowly. No-op without
	 * the attribute (flying withers/dragons).
	 */
	public static void applyDepthStrider(LivingEntity entity) {
		var attr = entity.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY);
		if(attr != null) {
			attr.setBaseValue(1.0);
		}
	}

	/**
	 * Ice Spray freeze: halves movement speed for {@code ticks}, then restores it. A MOVEMENT_SPEED modifier, not
	 * Slowness, so milk can't clear it and it doesn't fight other slowness; MULTIPLY_SCALAR_1 at -0.5 is exactly 0.5x.
	 * Transient, so never saved to NBT and a restart can't leave it stuck. Callers pair it with the "IceSprayed" tag
	 * (+10% damage taken / -15% dealt) on the same timer. No-op without the attribute.
	 */
	public static void iceSpraySlow(LivingEntity entity, long ticks) {
		var speed = entity.getAttribute(Attribute.MOVEMENT_SPEED);
		if(speed != null) {
			NamespacedKey key = new NamespacedKey(Plugin.getInstance(), "IceSpraySlow");
			AttributeModifier stale = speed.getModifier(key); // a leftover would make addModifier throw
			if(stale != null) {
				speed.removeModifier(stale);
			}
			AttributeModifier slow = new AttributeModifier(key, -0.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
			speed.addTransientModifier(slow);
			scheduleTask(() -> speed.removeModifier(slow), ticks);
		}
	}

	/**
	 * Setup shared by every boss and boss subentity: no pickup, infinite fire resistance, name visible, persistent,
	 * "SkyblockBoss" tag plus {@code extraTags}, and a target ({@code null} leaves targeting alone). Water movement is
	 * applied here too: the CustomMobs sweep only runs on EntitySpawnEvent, so bosses converted from an existing mob
	 * (Atoned Horror) would miss it.
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