package items.misc;

import items.AbilityItem;
import misc.Plugin;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.List;

public class GyrokineticWand implements AbilityItem {
	private static final int MANA_COST = 50;
	private static final String COOLDOWN_TAG = "GyroCooldown";
	private static final int COOLDOWN = 600;
	private static final int ANIMATION_DURATION = 80; // 4 seconds * 20 ticks

	public static ItemStack getItem() {
		ItemStack bonzoStaff = new ItemStack(Material.BLAZE_ROD);

		ItemMeta data = bonzoStaff.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.DARK_PURPLE + "Gyrokinetic Wand");
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "GyroModifier"), -1000, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/gyro");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "0");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Gravity Storm " + ChatColor.GREEN + ChatColor.BOLD + "LEFT CLICK");
		lore.add(ChatColor.GRAY + "Creates a large " + ChatColor.DARK_PURPLE + "rift" + ChatColor.GRAY + " at the aimed");
		lore.add(ChatColor.GRAY + "location, pulling all mobs together.");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + MANA_COST);
		lore.add(ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + COOLDOWN / 20 + "s");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		bonzoStaff.setItemMeta(data);

		return bonzoStaff;
	}

	@Override
	public void onRightClick(Player p) {
	}

	@Override
	public void onLeftClick(Player p) {
		RayTraceResult location = p.rayTraceBlocks(25);
		if(location == null) {
			return;
		}
		Location rift = location.getHitPosition().toLocation(p.getWorld());
		rift.setY(rift.getY() + 1);
		rift.add(0, 1, 0);
		for(Entity e : rift.getWorld().getNearbyEntities(rift, 10, 10, 10)) {
			if(e instanceof LivingEntity entity && !(e.equals(p))) {
				new BukkitRunnable() {
					int tick = 0;

					@Override
					public void run() {
						if(tick >= ANIMATION_DURATION) {
							cancel();
							return;
						}

						if(tick < 20) { // First second - pull in
							Location entityLoc = entity.getLocation();
							double x = (rift.getX() - entityLoc.getX()) / 10;
							double y = (rift.getY() - entityLoc.getY()) / 10;
							double z = (rift.getZ() - entityLoc.getZ()) / 10;
							entity.setVelocity(new org.bukkit.util.Vector(x, y, z));
						} else { // Next 3 seconds - keep at rift
							entity.teleport(rift);
						}

						float pitch = 0.5f + ((float) tick / ANIMATION_DURATION);
						p.playSound(rift, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, pitch);

						tick++;
					}
				}.runTaskTimer(Plugin.getInstance(), 0L, 1L);
			}
		}
	}

	@Override
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