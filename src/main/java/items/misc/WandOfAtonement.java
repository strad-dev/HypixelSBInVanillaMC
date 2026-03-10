package items.misc;

import items.AbilityItem;
import misc.Plugin;
import misc.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WandOfAtonement implements AbilityItem {
	private static final int MANA_COST = 10;
	private static final String COOLDOWN_TAG = "WandCooldown";
	private static final int COOLDOWN = 100;

	public static ItemStack getItem() {
		ItemStack wandOfAtonement = new ItemStack(Material.STICK);
		wandOfAtonement.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = wandOfAtonement.getItemMeta();
		data.setMaxStackSize(1);
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + "Wand of Atonement");
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "Wand1Modifier"), -1000, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/wand_of_atonement");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "0");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Big Heal " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Gain + " + ChatColor.RED + "0.5❤" + ChatColor.GRAY + " every " + ChatColor.GREEN + "0.6");
		lore.add(ChatColor.GRAY + " seconds for " + ChatColor.GREEN + "3" + ChatColor.GRAY + " seconds!");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + MANA_COST);
		lore.add(ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + COOLDOWN / 20 + "s");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY WAND " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		wandOfAtonement.setItemMeta(data);

		return wandOfAtonement;
	}

	@Override
	public boolean hasLeftClickAbility() {
		return false;
	}

	@Override
	public boolean onRightClick(Player p) {
		double maxHealth = p.getAttribute(Attribute.MAX_HEALTH).getValue();
		for(int i = 0; i < 61; i += 12) {
			Utils.scheduleTask(() -> p.setHealth(Math.min(p.getHealth() + 1, maxHealth)), i);
		}
		p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.0F, 1.0F);
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
