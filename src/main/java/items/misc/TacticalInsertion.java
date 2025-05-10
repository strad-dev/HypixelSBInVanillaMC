package items.misc;

import items.AbilityItem;
import misc.Plugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TacticalInsertion implements AbilityItem {
	private static final int MANA_COST = 10;
	private static final String COOLDOWN_TAG = "TacCooldown";
	private static final int COOLDOWN = 400;

	public static ItemStack getItem() {
		ItemStack bonzoStaff = new ItemStack(Material.BLAZE_ROD);

		ItemMeta data = bonzoStaff.getItemMeta();
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
		bonzoStaff.setItemMeta(data);

		return bonzoStaff;
	}

	@Override
	public void onRightClick(Player p) {
		Location l = p.getLocation();
			p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 0.707107F);
			p.playSound(p, Sound.ITEM_FLINTANDSTEEL_USE, 1.0F, 1.0F);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 0.793701F), 12);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 0.890899F), 24);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 0.943874F), 36);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1F), 48);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 0.50F);
			p.teleport(l);
		}, 60);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 0.50F);
		}, 63);
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 0.50F);
		}, 66);
	}

	@Override
	public void onLeftClick(Player p) {

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