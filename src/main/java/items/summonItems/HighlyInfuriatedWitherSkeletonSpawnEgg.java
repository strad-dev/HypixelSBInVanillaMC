package items.summonItems;

import items.AbilityItem;
import misc.Utils;
import mobs.generic.InfuriatedWitherSkeleton;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
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
		ItemMeta data = egg.getItemMeta();
		data.setEnchantmentGlintOverride(true);
		data.setUnbreakable(true);
		data.displayName(Utils.mm("<dark_purple><bold>Highly Infuriated Wither Skeleton Spawn Egg"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("skyblock/summon/wither_skeleton_spawn_egg"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>This egg is full to the brim"));
		lore.add(Utils.mm("<gray>with rage.  Do you have"));
		lore.add(Utils.mm("<gray>what it takes to calm it?"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Use this to summon a Highly"));
		lore.add(Utils.mm("<gray>Infuriated Wither Skeleton!"));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<dark_purple><bold><obfuscated>a</obfuscated> EPIC <obfuscated>a</obfuscated>"));

		data.lore(lore);
		egg.setItemMeta(data);

		return egg;
	}

	@Override
	public boolean hasLeftClickAbility() {
		return false;
	}

	@Override
	public boolean onRightClick(Player p) {
		WitherSkeleton skeleton = (WitherSkeleton) p.getWorld().spawnEntity(p.getLocation(), EntityType.WITHER_SKELETON);
		String name = new InfuriatedWitherSkeleton().onSpawn(p, skeleton);
		Utils.changeName(skeleton, name);
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
