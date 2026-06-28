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

import static misc.Utils.shootBeam;

public class Terminator implements AbilityItem {
	private static final String COOLDOWN_TAG = "SalvationCooldown";
	private static final int COOLDOWN = 16;
	private static final String SHOT_COOLDOWN_TAG = "TerminatorShotCooldown";
	private static final int SHOT_COOLDOWN = 3;

	public static ItemStack getItem(int powerLevel) {
		ItemStack term = new ItemStack(Material.BOW);

		ItemMeta data = term.getItemMeta();
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<light_purple>Terminator"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

		String loreDamage = powerLevel == 7 ? "4.5" : String.valueOf(2.5 + powerLevel * 0.25);
		String salvationDamage = powerLevel == 7 ? "8" : String.valueOf(4 + powerLevel * 0.5);

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

		return term;
	}

	@Override
	public boolean hasLeftClickAbility() {
		return true;
	}

	@Override
	public boolean onRightClick(Player p) {
		// Shortbow shot cooldown — cap firing at once per 3 ticks. Self-contained (not routed through the
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

		// Calculate bonuses
		double powerBonus;
		try {
			int power = p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.POWER);
			powerBonus = power * 0.25;
			if(power == 7) {
				powerBonus += 0.25;
			}
		} catch(Exception exception) {
			powerBonus = 0;
		}

		double strengthBonus;
		try {
			strengthBonus = 0.75 + 0.75 * p.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier();
		} catch(Exception exception) {
			strengthBonus = 0;
		}

		double add = powerBonus + strengthBonus;

		// Set Bukkit properties
		for(Arrow arrow : Arrays.asList(left, middle, right)) {
			arrow.setDamage(2.5 + add);
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
		double powerBonus;
		try {
			int power = p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getEnchantmentLevel(Enchantment.POWER);
			powerBonus = power * 0.5;
			if(power == 7) {
				powerBonus += 0.5;
			}
		} catch(Exception exception) {
			powerBonus = 0;
		}

		double strengthBonus;
		try {
			strengthBonus = 1 + p.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier();
		} catch(Exception exception) {
			strengthBonus = 0;
		}

		// shoot the three arrows
		double add = powerBonus + strengthBonus;
		shootBeam(p, p, Color.RED, 64, 5, 4.5 + add);
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