package items.weapons;

import items.AbilityItem;
import misc.Utils;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static misc.Utils.shootBeam;

public class Terminator implements AbilityItem {
	private static final String COOLDOWN_TAG = "SalvationCooldown";
	private static final int COOLDOWN = 16;
	private static final String SHOT_COOLDOWN_TAG = "TerminatorShotCooldown";
	private static final int SHOT_COOLDOWN = 3;

	/** An ordinary shot's own damage, before Power. */
	private static final double ARROW_BASE = 2.5;
	/** The Salvation beam's own damage, before Power. */
	private static final double SALVATION_BASE = 4;

	/**
	 * One arrow's damage at this Power level, before the Strength bonus the shot adds on top.  <b>The lore and
	 * {@link #onRightClick} both read this</b>, which is the whole point of it existing: the numbers were
	 * written out twice and had already drifted apart on the Salvation line below.
	 *
	 * <p>Level 7 rounds up to a flat +2 rather than 1.75, the same way the melee enchantments round at 7.
	 */
	public static double arrowDamage(int power) {
		if(power <= 0) return ARROW_BASE;
		return ARROW_BASE + (power == 7 ? 2.0 : power * 0.25);
	}

	/** The Salvation beam's damage at this Power level, before Strength.  @see #arrowDamage */
	public static double salvationDamage(int power) {
		if(power <= 0) return SALVATION_BASE;
		return SALVATION_BASE + (power == 7 ? 4.0 : power * 0.5);
	}

	public static ItemStack getItem() {
		return getItem(Map.of());
	}

	/**
	 * The item, carrying {@code enchants} and with lore that says so.  <b>The enchantments go on here rather
	 * than being applied by the caller afterwards</b> - that was the desync: the caller built the item, got
	 * lore for the Power level it named, and then enchanted the stack by material type.
	 */
	public static ItemStack getItem(Map<Enchantment, Integer> enchants) {
		int powerLevel = enchants == null ? 0 : enchants.getOrDefault(Enchantment.POWER, 0);
		ItemStack term = new ItemStack(Material.BOW);

		ItemMeta data = term.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<light_purple>Terminator"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

		String loreDamage = Utils.damageNumber(arrowDamage(powerLevel));
		String salvationDamage = Utils.damageNumber(salvationDamage(powerLevel));

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/terminator"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>+" + loreDamage));
		lore.add(Utils.mm("<gray>Shot Cooldown: <green>0.2s"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold>Shortbow: Instantly Shoots!"));
		lore.add(Utils.mm("<gray>Shoots <aqua>3<gray> arrows at once."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold>Ability: Salvation <green><bold>LEFT CLICK"));
		lore.add(Utils.mm("<gray>Shoot a beam, penetrating up to"));
		lore.add(Utils.mm("<yellow>5<gray> foes and dealing <red>" + salvationDamage));
		lore.add(Utils.mm("<gray>damage to each enemy."));
		lore.add(Utils.mm("<gray>Cooldown: <green>0.8s"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<light_purple><bold><obfuscated>a</obfuscated> MYTHIC BOW <obfuscated>a</obfuscated>"));

		data.lore(lore);
		term.setItemMeta(data);
		term.addUnsafeEnchantments(enchants);

		return term;
	}

	@Override
	public boolean hasLeftClickAbility() {
		return true;
	}

	@Override
	public boolean onRightClick(Player p) {
		// Shortbow shot cooldown: cap firing at once per 3 ticks.  Self-contained, not routed through the
		// dispatcher's ability cooldown) so shooting never puts the Salvation beam on its 20-tick cooldown.
		if(p.getScoreboardTags().contains(SHOT_COOLDOWN_TAG)) {
			return false;
		}
		p.addScoreboardTag(SHOT_COOLDOWN_TAG);
		Utils.scheduleTask(() -> p.removeScoreboardTag(SHOT_COOLDOWN_TAG), SHOT_COOLDOWN);

		// you don't need arrows
		p.getInventory().remove(Material.ARROW);
		p.getInventory().remove(Material.TIPPED_ARROW);
		p.getInventory().remove(Material.SPECTRAL_ARROW);

		// Get NMS world and player
		ServerLevel nmsWorld = ((CraftWorld) p.getWorld()).getHandle();
		ServerPlayer nmsPlayer = ((CraftPlayer) p).getHandle();

		// Calculate directions
		Vector baseDirection = p.getEyeLocation().getDirection().normalize();
		Vector leftDirection = baseDirection.clone().rotateAroundY(Math.toRadians(-5));
		Vector rightDirection = baseDirection.clone().rotateAroundY(Math.toRadians(5));

		// Calculate spawn position
		Location spawnLoc = p.getEyeLocation().add(baseDirection.clone());

		// Create NMS arrows directly (26.2: EntityType.ARROW constant removed; use the position+item constructor with a null weapon)
		net.minecraft.world.entity.projectile.arrow.Arrow nmsLeft = new net.minecraft.world.entity.projectile.arrow.Arrow(nmsWorld, 0, 0, 0, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ARROW), null);
		net.minecraft.world.entity.projectile.arrow.Arrow nmsMiddle = new net.minecraft.world.entity.projectile.arrow.Arrow(nmsWorld, 0, 0, 0, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ARROW), null);
		net.minecraft.world.entity.projectile.arrow.Arrow nmsRight = new net.minecraft.world.entity.projectile.arrow.Arrow(nmsWorld, 0, 0, 0, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ARROW), null);

		// Set positions
		nmsLeft.setPos(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());
		nmsMiddle.setPos(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());
		nmsRight.setPos(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());

		// shoot() sets velocity AND derives rotation from the direction vector
		float speed = 3.175f;
		nmsLeft.shoot(leftDirection.getX(), leftDirection.getY(), leftDirection.getZ(), speed, 0);
		nmsMiddle.shoot(baseDirection.getX(), baseDirection.getY(), baseDirection.getZ(), speed, 0);
		nmsRight.shoot(rightDirection.getX(), rightDirection.getY(), rightDirection.getZ(), speed, 0);

		// Set other properties
		nmsLeft.setOwner(nmsPlayer);
		nmsMiddle.setOwner(nmsPlayer);
		nmsRight.setOwner(nmsPlayer);

		// Add to world
		nmsWorld.addFreshEntity(nmsLeft);
		nmsWorld.addFreshEntity(nmsMiddle);
		nmsWorld.addFreshEntity(nmsRight);

		// Get Bukkit wrappers for further modification
		Arrow left = (Arrow) nmsLeft.getBukkitEntity();
		Arrow middle = (Arrow) nmsMiddle.getBukkitEntity();
		Arrow right = (Arrow) nmsRight.getBukkitEntity();

		// The lore's figure, not a second copy of the arithmetic - see arrowDamage.
		double damage = arrowDamage(p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.POWER));

		double strengthBonus;
		try {
			strengthBonus = 0.75 + 0.75 * p.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier();
		} catch(Exception exception) {
			strengthBonus = 0;
		}

		// Set Bukkit properties
		for(Arrow arrow : Arrays.asList(left, middle, right)) {
			arrow.setDamage(damage + strengthBonus);
			arrow.setPierceLevel(4);
			arrow.setShooter(p);
			arrow.setWeapon(p.getInventory().getItemInMainHand());
			arrow.addScoreboardTag("TerminatorArrow");
		}

		p.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);

		return false;
	}

	@Override
	public boolean onLeftClick(Player p) {
		// Off the MAIN HAND, like the shot above and like the lore.  This used to read
		// getItem(getHeldItemSlot()), which is the same item by a route that can return null - hence the
		// try/catch that then swallowed the Power level and silently fired an unenchanted beam.
		double damage = salvationDamage(p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.POWER));

		double strengthBonus;
		try {
			strengthBonus = 1 + p.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier();
		} catch(Exception exception) {
			strengthBonus = 0;
		}

		shootBeam(p, p, Color.RED, 64, 5, damage + strengthBonus);
		p.playSound(p.getLocation(), Sound.ENTITY_GUARDIAN_DEATH, 1.0F, 2.0F);
		return true;
	}

	@Override
	public int manaCost() {
		return 0;
	}

	@Override
	public String cooldownTag() {
		return COOLDOWN_TAG;
	}

	@Override
	public int cooldown() {
		return COOLDOWN;
	}
}