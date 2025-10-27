package mobs.hardmode.withers;

import listeners.DamageType;
import misc.DamageData;
import misc.Utils;
import mobs.CustomMob;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MartialWitherSkeleton implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		e.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Henchman of Martial Arts" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ a");
		Utils.changeName(e);
		e.addScoreboardTag("Martial");

		ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
		sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 7);
		sword.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10);
		Objects.requireNonNull(e.getEquipment()).setItemInMainHand(sword);

		return "";
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type, DamageData data) {
		if(damagee.getHealth() - originalDamage < 1) {
			List<EntityType> immune = new ArrayList<>();
			immune.add(EntityType.WITHER_SKELETON);
			immune.add(EntityType.WITHER);
			Utils.spawnTNT(damagee, damagee.getLocation(), 0, 12, 25, immune);
			MasterWitherKing.defeatHenchman("Martial");
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
