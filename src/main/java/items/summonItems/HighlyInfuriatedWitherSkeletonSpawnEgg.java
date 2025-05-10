package items.summonItems;

import items.AbilityItem;
import mobs.generic.InfuriatedWitherSkeleton;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HighlyInfuriatedWitherSkeletonSpawnEgg implements AbilityItem, SummonItem {
	public static ItemStack getItem() {
		ItemStack egg = new ItemStack(Material.WITHER_SKELETON_SPAWN_EGG);
		egg.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		ItemMeta data = egg.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Highly Infuriated Wither Skeleton Spawn Egg");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/summon/wither_skeleton_spawn_egg");
		lore.add("");
		lore.add(ChatColor.GRAY + "This egg is full to the brim");
		lore.add(ChatColor.GRAY + "with rage.  Do you have");
		lore.add(ChatColor.GRAY + "what it takes to calm it?");
		lore.add("");
		lore.add(ChatColor.GRAY + "Use this to summon a Highly");
		lore.add(ChatColor.GRAY + "Infuriated Wither Skeleton!");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		egg.setItemMeta(data);

		return egg;
	}

	@Override
	public boolean onRightClick(Player p) {
		WitherSkeleton skeleton = (WitherSkeleton) p.getWorld().spawnEntity(p.getLocation(), EntityType.WITHER_SKELETON);
		skeleton.setCustomName(new InfuriatedWitherSkeleton().onSpawn(p, skeleton));
		skeleton.setCustomNameVisible(true);
		if(p.getGameMode() != GameMode.CREATIVE) {
			p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
		}
		return false;
	}

	@Override
	public boolean onLeftClick(Player p) {
		return false;
	}

	@Override
	public int manaCost() {
		return 0;
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