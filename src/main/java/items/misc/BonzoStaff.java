package items.misc;

import items.AbilityItem;
import misc.Plugin;
import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

public class BonzoStaff implements AbilityItem {
	private static final int MANA_COST = 1;

	public static ItemStack getItem() {
		ItemStack bonzoStaff = new ItemStack(Material.BREEZE_ROD);

		ItemMeta data = bonzoStaff.getItemMeta();
		data.setMaxStackSize(1);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<blue>Bonzo's Staff"));
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "BonzoModifier"), -1000, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/bonzo_staff"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>0"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold>Ability: Showtime <green><bold>RIGHT CLICK"));
		lore.add(Utils.mm("<gray>Shoots Wind Charges that create an"));
		lore.add(Utils.mm("<gray>explosion that propels the player forward."));
		lore.add(Utils.mm("<dark_gray>Intelligence Cost: <dark_aqua>" + MANA_COST));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<blue><bold><obfuscated>a</obfuscated> RARE <obfuscated>a</obfuscated>"));

		data.lore(lore);
		bonzoStaff.setItemMeta(data);

		return bonzoStaff;
	}

	@Override
	public boolean hasLeftClickAbility() {
		return false;
	}

	@Override
	public boolean onRightClick(Player p) {
		Location l = p.getEyeLocation();
		WindCharge windCharge = (WindCharge) l.getWorld().spawnEntity(l, EntityType.WIND_CHARGE);
		windCharge.addScoreboardTag("Bonzo");
		windCharge.setShooter(p);
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
		return "";
	}

	@Override
	public int cooldown() {
		return 0;
	}
}