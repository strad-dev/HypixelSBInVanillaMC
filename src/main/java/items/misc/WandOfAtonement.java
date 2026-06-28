package items.misc;

import items.AbilityItem;
import misc.Plugin;
import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
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

		ItemMeta data = wandOfAtonement.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setMaxStackSize(1);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<gold>Wand of Atonement"));
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "Wand1Modifier"), -1000, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/wand_of_atonement"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>0"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold>Ability: Big Heal <green><bold>RIGHT CLICK"));
		lore.add(Utils.mm("<gray>Gain +<red>0.5❤<gray> every <green>0.6"));
		lore.add(Utils.mm("<gray>seconds for <green>3<gray> seconds!"));
		lore.add(Utils.mm("<dark_gray>Intelligence Cost: <dark_aqua>" + MANA_COST));
		lore.add(Utils.mm("<dark_gray>Cooldown: <green>" + COOLDOWN / 20 + "s"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold><bold><obfuscated>a</obfuscated> LEGENDARY WAND <obfuscated>a</obfuscated>"));

		data.lore(lore);
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
			Utils.scheduleTask(() -> {
				double cur = p.getHealth();
				if(cur < maxHealth) {
					p.setHealth(Math.min(cur + 1, maxHealth));
					pvp.PvpHooks.trackHeal(p, 1);
				}
			}, i);
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
