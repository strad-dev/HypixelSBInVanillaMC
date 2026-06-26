package items.misc;

import items.AbilityItem;
import listeners.CustomItems;
import listeners.DamageType;
import misc.Plugin;
import misc.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static listeners.CustomDamage.customMobs;

public class IceSpray implements AbilityItem {
	private static final int MANA_COST = 8;
	private static final String COOLDOWN_TAG = "IceSprayCooldown";
	private static final int COOLDOWN = 100;

	public static ItemStack getItem() {
		ItemStack iceSpray = new ItemStack(Material.STICK);

		ItemMeta data = iceSpray.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setMaxStackSize(1);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<gold>Ice Spray Wand"));
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "IceSprayWandModifier"), -1000, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/ice_spray_wand"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>0"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold>Ability: Ice Spray <green><bold>RIGHT CLICK"));
		lore.add(Utils.mm("<gray>Produces a cone of ice in front"));
		lore.add(Utils.mm("<gray>of the caster that deals"));
		lore.add(Utils.mm("<red>1<gray> damage to enemies and"));
		lore.add(Utils.mm("<gray>slows them down for <green>5"));
		lore.add(Utils.mm("<gray>seconds!  Frozen enemies take"));
		lore.add(Utils.mm("<red>+10%<gray> increased damage!"));
		lore.add(Utils.mm("<dark_gray>Intelligence Cost: <dark_aqua>" + MANA_COST));
		lore.add(Utils.mm("<dark_gray>Cooldown: <green>" + COOLDOWN / 20 + "s"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold><bold><obfuscated>a</obfuscated> LEGENDARY WAND <obfuscated>a</obfuscated>"));

		data.lore(lore);
		iceSpray.setItemMeta(data);

		return iceSpray;
	}

	@Override
	public boolean hasLeftClickAbility() {
		return false;
	}

	@Override
	public boolean onRightClick(Player p) {
		Location l = p.getEyeLocation();
		p.getWorld().spawnParticle(Particle.SNOWFLAKE, l, 256);
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
					Utils.scheduleTask(() -> entity1.removeScoreboardTag("IceSprayed"), 101L);
					if(entity1 instanceof Player enemy) {
						enemy.showTitle(Title.title(Utils.msg("<aqua><bold>❄ ❅ ❆"), Utils.msg("<blue>Brrrr..."), Title.Times.times(Duration.ZERO, Duration.ofMillis(101L * 50L), Duration.ZERO)));
						enemy.sendMessage(Utils.msg("<aqua><bold>" + p.getName() + " has Ice Sprayed you for 5 seconds!"));
					}
				}
			}
		}
		if(damage > 0) {
			p.sendMessage(Utils.msg("<red>Your Ice Spray debuffed " + damage + " enemies."));
		}
		if(alreadyDebuffed > 0) {
			p.sendMessage(Utils.msg("<red>" + alreadyDebuffed + " enemies have already been debuffed."));
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
