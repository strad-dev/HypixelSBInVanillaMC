package mobs.hardmode.withers;

import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FireWitherSkeleton implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		e.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Henchman of Fire" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ a");
		Utils.changeName(e);
		e.addScoreboardTag("Fire");
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
			Utils.scheduleTask(() -> walkOnFire(e), 5);
		}
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(damagee.getHealth() - originalDamage < 1) {
			List<EntityType> immune = new ArrayList<>();
			immune.add(EntityType.WITHER_SKELETON);
			immune.add(EntityType.WITHER);
			Utils.spawnTNT(damagee, damagee.getLocation(), 0, 12, 25, immune);
			MasterWitherKing.defeatHenchman("Fire");
			damagee.remove();
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		return true;
	}
}
