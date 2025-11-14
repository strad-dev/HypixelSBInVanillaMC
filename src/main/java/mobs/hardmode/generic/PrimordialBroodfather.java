package mobs.hardmode.generic;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

import static misc.Utils.teleport;

public class PrimordialBroodfather implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		Spider spider;
		if(e instanceof Spider) {
			spider = (Spider) e;
		} else {
			throw new IllegalStateException("Uh oh!  Wrong mob type!");
		}

		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Primordial Broodfather" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		spider.getAttribute(Attribute.MAX_HEALTH).setBaseValue(100.0);
		spider.setHealth(100.0);
		spider.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.67);
		spider.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(10.0);
		spider.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		spider.setTarget(Utils.getNearestPlayer(spider));
		spider.setCustomNameVisible(true);
		spider.addScoreboardTag("SkyblockBoss");
		spider.addScoreboardTag("PrimordialBroodfather");
		spider.addScoreboardTag("HardMode");
		spider.addScoreboardTag("50Trigger");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Spider Relic draws the attention of the Primordial Broodfather!");
		Bukkit.getLogger().info(p.getName() + " has summoned the Primordial Broodfather.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		spider.setPersistent(true);
		spider.setRemoveWhenFarAway(false);

		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(damager instanceof LivingEntity livingEntity) {
			CustomDamage.customMobs(livingEntity, damagee, 1, DamageType.ABSOLUTE);
		}
		double finalDamage = originalDamage;
		if(originalDamage > 5) {
			finalDamage = 5 + (originalDamage - 5) / 10;
		}

		double health = damagee.getHealth();

		if(damagee.getScoreboardTags().contains("Invulnerable")) {
			if(damager instanceof Player p) {
				p.sendTitle("", ChatColor.YELLOW + "You cannot damage the Primordial Broodfather.", 0, 20, 0);
			}
			damagee.getWorld().playSound(damagee, Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.5F);
			return false;
		} else if(health - finalDamage < 0 && !damagee.isDead()) {
			damagee.remove();
			Spider spider = (Spider) damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.SPIDER);
			CustomMob.getMob("ConjoinedBrood", false).onSpawn(Utils.getNearestPlayer(damagee), spider);
		} else if(health - finalDamage < 50 && damagee.getScoreboardTags().contains("50Trigger")) {
			damagee.setHealth(50.0);
			Utils.changeName(damagee);
			damagee.addScoreboardTag("Invulnerable");
			damagee.removeScoreboardTag("50Trigger");
			damagee.setAI(false);

			Block b = damagee.getWorld().getBlockAt(damagee.getLocation());
			Set<Block> changedBlocks = new HashSet<>();
			int x = b.getX() - 3;
			int y = b.getY() - 3;
			int z = b.getZ() - 3;
			for(int i = x; i < b.getX() + 3; i++) {
				for(int j = y; j < b.getY() + 3; j++) {
					for(int k = z; k < b.getZ() + 3; k++) {
						Block temp = damagee.getWorld().getBlockAt(i, j, k);
						if(temp.getType() == Material.AIR) {
							temp.setType(Material.COBWEB);
							changedBlocks.add(temp);
						}
					}
				}
			}

			for(int i = 10; i < 201; i += 10) {
				Utils.scheduleTask(() -> damagee.getNearbyEntities(32, 32, 32).stream().filter(entity -> entity instanceof Player).forEach(p -> {
					CustomDamage.customMobs((LivingEntity) p, damagee, p.getLocation().distanceSquared(damagee.getLocation()) < 4 ? 2 : 1, DamageType.ABSOLUTE);
				}), i);
			}
			Utils.scheduleTask(() -> {
				damagee.removeScoreboardTag("Invulnerable");
				damagee.setAI(true);
				for(Block block : changedBlocks) {
					block.setType(Material.AIR);
				}
			}, 200);
		} else {
			teleport(damagee, 12);
			CustomDamage.calculateFinalDamage(damagee, damager, finalDamage, type);
		}
		return false;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}
