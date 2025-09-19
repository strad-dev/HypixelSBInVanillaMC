package items.weapons;

import items.AbilityItem;
import misc.Plugin;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static misc.PluginUtils.shootBeam;

public class Terminator implements AbilityItem {
	private static final String COOLDOWN_TAG = "SalvationCooldown";
	private static final int COOLDOWN = 20;

	public static ItemStack getItem(int powerLevel) {
		ItemStack term = new ItemStack(Material.BOW);

		ItemMeta data = term.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Terminator");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

		String loreDamage;
		switch(powerLevel) {
			case 1 -> loreDamage = "2.75";
			case 2 -> loreDamage = "3";
			case 3 -> loreDamage = "3.25";
			case 4 -> loreDamage = "3.5";
			case 5 -> loreDamage = "3.75";
			case 6 -> loreDamage = "4";
			case 7 -> loreDamage = "4.5";
			default -> loreDamage = "2.5";
		}

		String salvationDamage;
		switch(powerLevel) {
			case 1 -> salvationDamage = "4.5";
			case 2 -> salvationDamage = "5";
			case 3 -> salvationDamage = "5.5";
			case 4 -> salvationDamage = "6";
			case 5 -> salvationDamage = "6.5";
			case 6 -> salvationDamage = "7";
			case 7 -> salvationDamage = "8";
			default -> salvationDamage = "4";
		}

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/terminator");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+" + loreDamage);
		lore.add(ChatColor.GRAY + "Shot Cooldown: " + ChatColor.GREEN + "0.25s");
		lore.add("");
		lore.add(ChatColor.GOLD + "Shortbow: Instantly Shoots!");
		lore.add(ChatColor.GRAY + "Shoots " + ChatColor.AQUA + "3" + ChatColor.GRAY + " arrows at once.");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Salvation " + ChatColor.GREEN + ChatColor.BOLD + "LEFT CLICK");
		lore.add(ChatColor.GRAY + "Shoot a beam, penetrating up to");
		lore.add(ChatColor.YELLOW + "5" + ChatColor.GRAY + " foes and dealing " + ChatColor.RED + salvationDamage);
		lore.add(ChatColor.GRAY + "damage to each enemy.");
		lore.add(ChatColor.GRAY + "Cooldown: " + ChatColor.GREEN + "1s");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC BOW " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		term.setItemMeta(data);

		return term;
	}

	@Override
	public boolean onRightClick(Player p) {
		// you don't need arrows
		p.getInventory().remove(Material.ARROW);
		p.getInventory().remove(Material.TIPPED_ARROW);
		p.getInventory().remove(Material.SPECTRAL_ARROW);

		// setting the three arrows
		Vector baseDirection = p.getLocation().getDirection().normalize();
		Vector leftDirection = baseDirection.clone().rotateAroundY(Math.toRadians(-5));
		Vector rightDirection = baseDirection.clone().rotateAroundY(Math.toRadians(5));

		// Create spawn locations with proper yaw/pitch
		Location baseSpawnLoc = p.getEyeLocation().add(baseDirection.clone());

		Location leftSpawnLoc = baseSpawnLoc.clone();
		leftSpawnLoc.setDirection(leftDirection);

		Location middleSpawnLoc = baseSpawnLoc.clone();
		middleSpawnLoc.setDirection(baseDirection);

		Location rightSpawnLoc = baseSpawnLoc.clone();
		rightSpawnLoc.setDirection(rightDirection);

		// calculate power and strength bonus
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

		// shoot the three arrows
		double add = powerBonus + strengthBonus;
		Arrow left = p.getWorld().spawn(leftSpawnLoc, Arrow.class);
		Arrow middle = p.getWorld().spawn(middleSpawnLoc, Arrow.class);
		Arrow right = p.getWorld().spawn(rightSpawnLoc, Arrow.class);

		double speed = 4.0; // Adjust as needed
		Vector leftVel = leftDirection.multiply(speed);
		Vector middleVel = baseDirection.multiply(speed);
		Vector rightVel = rightDirection.multiply(speed);

		left.setVelocity(leftVel);
		middle.setVelocity(middleVel);
		right.setVelocity(rightVel);

		for(Arrow arrow : Arrays.asList(left, middle, right)) {
			arrow.setDamage(2.5 + add);
			arrow.setPierceLevel(4);
			arrow.setShooter(p);
			arrow.setWeapon(p.getInventory().getItemInMainHand());
			arrow.addScoreboardTag("TerminatorArrow");
		}

		new BukkitRunnable() {
			int ticks = 0;
			@Override
			public void run() {
				if (ticks >= 2 || left.isDead() || middle.isDead() || right.isDead()) {
					this.cancel();
					return;
				}

				left.setVelocity(leftVel);
				middle.setVelocity(middleVel);
				right.setVelocity(rightVel);

				ticks++;
			}
		}.runTaskTimer(Plugin.getInstance(), 1L, 1L);
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