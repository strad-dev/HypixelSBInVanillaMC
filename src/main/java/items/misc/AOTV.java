package items.misc;

import items.AbilityItem;
import misc.Plugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class AOTV implements AbilityItem {
	private static final int MANA_COST = 1;

	public static ItemStack getItem() {
		ItemStack aotv = new ItemStack(Material.NETHERITE_SHOVEL);

		ItemMeta data = aotv.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Aspect of the Void");
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "AOTVModifier"), -1000, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/aspect_of_the_void");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "0");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Instant Transmission " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Teleport " + ChatColor.GREEN + "12 blocks" + ChatColor.GRAY + " ahead of you.");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + MANA_COST);
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Ether Transmission " + ChatColor.GREEN + ChatColor.BOLD + "SNEAK RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Teleport to your targetted block");
		lore.add(ChatColor.GRAY + "up to " + ChatColor.GREEN + "61 blocks" + ChatColor.GRAY + " blocks away.");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + MANA_COST);
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC SHOVEL " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		aotv.setItemMeta(data);

		return aotv;
	}

	@Override
	public boolean onRightClick(Player p) {
		if(p.isSneaking()) {
			RayTraceResult result = p.rayTraceBlocks(61);
			Block b = result.getHitBlock();
			p.teleport(b.getLocation().add(0.5, 0, 0.5));
		} else {
			Location originalLocation = p.getLocation().clone();
			Location l = p.getEyeLocation().clone();
			Vector v = l.getDirection();
			v.setX(v.getX() / 10);
			v.setY(v.getY() / 10);
			v.setZ(v.getZ() / 10);
			for(int i = 0; i < 120; i++) {
				l.add(v);
				if(l.getBlock().getType().isSolid()) {
					l = l.subtract(v).getBlock().getLocation();
					if(originalLocation.getPitch() > 0) {
						l.add(0, 1.62, 0);
					}
					l.setYaw(originalLocation.getYaw());
					l.setPitch(originalLocation.getPitch());
					l.add(0.5, 0, 0.5);
					break;
				}
			}
			l.subtract(0, 1, 0);
			if(!l.getBlock().isEmpty()) {
				l.add(0, 1, 0);
			}
			p.setFallDistance(0);
			p.teleport(l);
			p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
			return true;
		}
		return false;
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
		return "";
	}

	@Override
	public int cooldown() {
		return 0;
	}
}
