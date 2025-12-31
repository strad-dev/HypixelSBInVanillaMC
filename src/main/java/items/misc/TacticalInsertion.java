package items.misc;

import items.AbilityItem;
import misc.Plugin;
import misc.Utils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class TacticalInsertion implements AbilityItem {
	private static final int MANA_COST = 10;
	private static final String COOLDOWN_TAG = "TacCooldown";
	private static final int COOLDOWN = 400;

	public static ItemStack getItem() {
		ItemStack tac = new ItemStack(Material.BLAZE_ROD);

		ItemMeta data = tac.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + "Tactical Insertion");
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "TacModifier"), -1000, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/tactical_insertion");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "0");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Gorilla Tactics " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Marks your location and teleports");
		lore.add(ChatColor.GRAY + "back there after " + ChatColor.GREEN + "3s");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + MANA_COST);
		lore.add(ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + COOLDOWN / 20 + "s");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		tac.setItemMeta(data);

		return tac;
	}

	@Override
	public boolean hasLeftClickAbility() {
		return false;
	}

	@Override
	public boolean onRightClick(Player p) {
		Location l = p.getLocation();
		p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 0.707107F);
		p.playSound(p, Sound.ITEM_FLINTANDSTEEL_USE, 1.0F, 1.0F);
		Utils.scheduleTask(() -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 0.793701F), 10);
		Utils.scheduleTask(() -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 0.890899F), 20);
		Utils.scheduleTask(() -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 0.943874F), 30);
		Utils.scheduleTask(() -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1F), 40);
		Utils.scheduleTask(() -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.059463F), 50);
		Utils.scheduleTask(() -> {
			p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 1.0F);
			p.teleport(l);
			p.setVelocity(new Vector(0, 0, 0));
			Utils.scheduleTask(() -> p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 1000), 1);
		}, 60);
		Utils.scheduleTask(() -> p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 1.0F), 63);
		Utils.scheduleTask(() -> p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 1.0F), 66);
		return true;
	}

	@Override
	public boolean onLeftClick(Player p) {
		return false;
	}

	@Override
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