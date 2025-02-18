package items.misc;

import items.AbilityItem;
import misc.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BonzoStaff implements AbilityItem {
	private static final int MANA_COST = 1;

	public static ItemStack getItem() {
		ItemStack bonzoStaff = new ItemStack(Material.BREEZE_ROD);

		ItemMeta data = bonzoStaff.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + "Bonzo's Staff");
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "BonzoModifier"), -1000, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/bonzo_staff");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "0");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Showtime " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Shoots Wind Charges that create an explosion that propels the player forward.");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + MANA_COST);
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		bonzoStaff.setItemMeta(data);

		return bonzoStaff;
	}

	@Override
	public void onRightClick(Player p) {
		p.getLocation().getWorld().spawnEntity(p.getLocation(), EntityType.WIND_CHARGE);
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
		return "";
	}

	@Override
	public int cooldown() {
		return 0;
	}
}