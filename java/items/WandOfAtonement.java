package items;

import listeners.CustomItems;
import misc.Plugin;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.List;

public class WandOfAtonement implements CustomItem {
	private static final int MANA_COST = 6;
	private static final String COOLDOWN_TAG = "WandCooldown";
	private static final int COOLDOWN = 100;

	public static ItemStack getItem() {ItemStack wandOfAtonement = new ItemStack(Material.STICK);
		wandOfAtonement.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = wandOfAtonement.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + "Wand of Atonement");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/wand_of_atonement");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Big Heal " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Gain Regeration III for");
		lore.add(ChatColor.GREEN + "2.5" + ChatColor.GRAY + " seconds!");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + MANA_COST);
		lore.add(ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + COOLDOWN / 20 + "s");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY WAND " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		wandOfAtonement.setItemMeta(data);

		return wandOfAtonement;
	}

	@Override
	public void onRightClick(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 51, 2));
		p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.0F, 1.0F);
		if(!p.getGameMode().equals(GameMode.CREATIVE)) {
			Score score = CustomItems.currentScore();
			score.setScore(score.getScore() - manaCost());
		}
		p.addScoreboardTag("WandCooldown");
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> p.removeScoreboardTag("WandCooldown"), 100L);

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
