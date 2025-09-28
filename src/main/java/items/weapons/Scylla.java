package items.weapons;

import items.AbilityItem;
import listeners.CustomItems;
import listeners.DamageType;
import misc.Plugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static listeners.CustomDamage.customMobs;

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
				case 2 -> loreDamage = "9.5";
				case 3 -> loreDamage = "10";
				case 4 -> loreDamage = "10.5";
				case 5 -> loreDamage = "11";
				case 6 -> loreDamage = "11.5";
				case 7 -> loreDamage = "12";
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
	public boolean onRightClick(Player p) {
		Location originalLocation = p.getLocation().clone();
		Location l = p.getEyeLocation();
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
		p.setFallDistance(0);
		p.teleport(l);
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
			targetDamage += sharpness * 0.5 + 0.5;
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
				customMobs(entity1, p, tempDamage, DamageType.PLAYER_MAGIC);
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
			p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0F, 0.65F);
			Location finalL = l;
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> { // convert to healing after 5 seconds
				p.setHealth(Math.min(p.getHealth() + (p.getAbsorptionAmount() / 2), p.getAttribute(Attribute.MAX_HEALTH).getValue()));
				p.playSound(finalL, Sound.ENTITY_PLAYER_LEVELUP, 2.0F, 2.0F);
			}, 101L);
		}
		if(!p.getScoreboardTags().contains("WitherShield")) { // reduced damage
			p.addScoreboardTag("WitherShield");
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> p.removeScoreboardTag("WitherShield"), 101);
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
