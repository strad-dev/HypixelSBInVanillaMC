package mobs.hardmode.generic;

import listeners.DamageType;
import misc.Plugin;
import misc.PluginUtils;
import mobs.CustomMob;
import mobs.hardmode.withers.MasterWitherKing;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class FireWitherSkeleton implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		e.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Henchman of Fire" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ ");
		PluginUtils.changeName(e);

		ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
		sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 7);
		sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 255);
		Objects.requireNonNull(e.getEquipment()).setItemInMainHand(sword);

		walkOnFire(e);

		return "";
	}

	private void walkOnFire(Mob e) {
		if(!e.isDead()) {
			Block b = e.getLocation().add(0, 1, 0).getBlock();
			if(b.getType() != Material.AIR) {
				b.setType(Material.FIRE);
			}
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> walkOnFire(e), 5);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(damagee.getHealth() - originalDamage < 1) {
			MasterWitherKing.defeatHenchman("Fire");
			damagee.remove();
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return true;
	}
}
