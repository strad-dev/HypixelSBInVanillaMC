package items.misc;

import items.AbilityItem;
import listeners.CustomItems;
import listeners.DamageType;
import misc.Plugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

import static listeners.CustomDamage.customMobs;

public class IceSpray implements AbilityItem {
	private static final int MANA_COST = 8;
	private static final String COOLDOWN_TAG = "IceSprayCooldown";
	private static final int COOLDOWN = 100;

	public static ItemStack getItem() {
		ItemStack iceSpray = new ItemStack(Material.STICK);
		iceSpray.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = iceSpray.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + "Ice Spray Wand");
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "IceSprayWandModifier"), -1000, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/ice_spray_wand");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "0");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Ice Spray " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Produces a cone of ice in front");
		lore.add(ChatColor.GRAY + "of the caster that deals");
		lore.add(ChatColor.RED + "1" + ChatColor.GRAY + " damage to enemies and");
		lore.add(ChatColor.GRAY + "slows them down for " + ChatColor.GREEN + "5");
		lore.add(ChatColor.GRAY + "seconds!  Frozen enemies take");
		lore.add(ChatColor.RED + "+10%" + ChatColor.GRAY + " increased damage!");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + MANA_COST);
		lore.add(ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + COOLDOWN / 20 + "s");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY WAND " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		iceSpray.setItemMeta(data);

		return iceSpray;
	}

	@Override
	public boolean onRightClick(Player p) {
		Location l = p.getEyeLocation();
		p.getWorld().spawnParticle(Particle.SNOWFLAKE, l, 1000);
		List<Entity> entities = (List<Entity>) p.getWorld().getNearbyEntities(l, 6, 6, 6);
		List<EntityType> doNotKill = CustomItems.createList();
		int damage = 0;
		int alreadyDebuffed = 0;
		for(Entity entity : entities) {
			if(!doNotKill.contains(entity.getType()) && entity instanceof LivingEntity entity1 && !entity.equals(p) && entity1.getHealth() > 0) {
				if(entity instanceof Player target && (target.getGameMode().equals(GameMode.CREATIVE) || target.getGameMode().equals(GameMode.SPECTATOR))) {
					continue;
				}
				if(entity1.getScoreboardTags().contains("IceSprayed")) {
					alreadyDebuffed++;
				} else {
					damage += 1;
					customMobs(entity1, p, 1, DamageType.PLAYER_MAGIC);
					entity1.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 101, 3));
					entity1.addScoreboardTag("IceSprayed");
					Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> entity1.removeScoreboardTag("IceSprayed"), 101L);
					if(entity1 instanceof Player enemy) {
						enemy.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "❄ ❅ ❆", ChatColor.BLUE + "Brrrr...", 0, 101, 0);
						enemy.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + p.getName() + " has Ice Sprayed you for 5 seconds!");
					}
				}
			}
		}
		if(damage > 0) {
			p.sendMessage(ChatColor.RED + "Your Ice Spray debuffed " + damage + " enemies.");
		}
		if(alreadyDebuffed > 0) {
			p.sendMessage(ChatColor.RED + String.valueOf(alreadyDebuffed) + " enemies have already been debuffed.");
		}
		p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
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
		return COOLDOWN_TAG;
	}

	@Override
	public int cooldown() {
		return COOLDOWN;
	}
}
