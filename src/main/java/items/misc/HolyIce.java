package items.misc;

import items.AbilityItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class HolyIce implements AbilityItem {
	private static final int MANA_COST = 30;
	private static final String COOLDOWN_TAG = "IceCooldown";
	private static final int COOLDOWN = 80;

	public static ItemStack getItem() {
		ItemStack holyIce = new ItemStack(Material.DIAMOND);
		holyIce.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = holyIce.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + "Holy Ice");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/holy_ice");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Splash Yo Face " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Gain Resistance IV for");
		lore.add(ChatColor.GREEN + "1.5" + ChatColor.GRAY + " seconds!");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + MANA_COST);
		lore.add(ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + COOLDOWN / 20 + "s");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		holyIce.setItemMeta(data);

		return holyIce;
	}

	@Override
	public void onRightClick(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 31, 3));
		p.getWorld().spawnParticle(Particle.DRIPPING_WATER, p.getLocation(), 1000);
		p.playSound(p, Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED, 0.5F, 1.0F);
	}

	@Override
	public void onLeftClick(Player p) {

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
