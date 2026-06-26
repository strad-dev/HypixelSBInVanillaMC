package items.misc;

import items.AbilityItem;
import misc.Plugin;
import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HolyIce implements AbilityItem {
	private static final int MANA_COST = 25;
	private static final String COOLDOWN_TAG = "IceCooldown";
	private static final int COOLDOWN = 60;

	public static ItemStack getItem() {
		ItemStack holyIce = new ItemStack(Material.DIAMOND);

		ItemMeta data = holyIce.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setMaxStackSize(1);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<blue>Holy Ice"));
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "HolyIceModifier"), -1000, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/combat/holy_ice"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Damage: <red>0"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold>Ability: Splash Yo Face <green><bold>RIGHT CLICK"));
		lore.add(Utils.mm("<gray>Take <green>75%<gray> less damage"));
		lore.add(Utils.mm("<gray>for <green>1<gray> second!"));
		lore.add(Utils.mm("<dark_gray>Intelligence Cost: <dark_aqua>" + MANA_COST));
		lore.add(Utils.mm("<dark_gray>Cooldown: <green>" + COOLDOWN / 20 + "s"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<blue><bold><obfuscated>a</obfuscated> RARE <obfuscated>a</obfuscated>"));

		data.lore(lore);
		holyIce.setItemMeta(data);

		return holyIce;
	}

	@Override
	public boolean hasLeftClickAbility() {
		return false;
	}

	@Override
	public boolean onRightClick(Player p) {
		p.addScoreboardTag("HolyIce");
		Utils.scheduleTask(() -> p.removeScoreboardTag("HolyIce"), 20);
		p.getWorld().spawnParticle(Particle.DRIPPING_WATER, p.getEyeLocation(), 256);
		p.playSound(p, Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED, 0.5F, 1.0F);
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
