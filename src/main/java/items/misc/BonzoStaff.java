package items.misc;

import items.AbilityItem;
import misc.Plugin;
import misc.Utils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BonzoStaff implements AbilityItem {
	private static final int MANA_COST = 1;

	public static ItemStack getItem() {
		ItemStack bonzoStaff = new ItemStack(Material.BREEZE_ROD);

		ItemMeta data = bonzoStaff.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.BLUE + "Bonzo's Staff");
		AttributeModifier attackDamage = new AttributeModifier(new NamespacedKey(Plugin.getInstance(), "BonzoModifier"), -1000, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
		data.addAttributeModifier(Attribute.ATTACK_DAMAGE, attackDamage);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/bonzo_staff");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "0");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Showtime " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Shoots Wind Charges that create an");
		lore.add(ChatColor.GRAY + "explosion that propels the player forward.");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + MANA_COST);
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		bonzoStaff.setItemMeta(data);

		return bonzoStaff;
	}

	@Override
	public boolean hasLeftClickAbility() {
		return false;
	}

	@Override
	public boolean onRightClick(Player p) {
		Location l = p.getLocation();
		Vector v = l.getDirection();
		RayTraceResult result = p.rayTraceBlocks(2.5);
		double dY = p.getVelocity().getY();
		float pitch = l.getPitch();
		Vector bonzoDirection = null;

		if(result != null) {
			switch(result.getHitBlockFace()) {
				case BlockFace.UP -> bonzoDirection = new Vector(v.getX(), 0, v.getZ()).normalize();
				case BlockFace.DOWN, BlockFace.SELF -> fireWindCharge(p);
				default -> bonzoDirection = new Vector(-v.getX(), 0, -v.getZ()).normalize();
			}
		} else {
			if(pitch > 60 && (dY < 0 && dY > -0.3333)) {
				bonzoDirection = new Vector(v.getX(), 0, v.getZ()).normalize();
			}
		}

		if(bonzoDirection != null) {
			if(!(p instanceof CraftPlayer cp)) {
				return false;
			}
			net.minecraft.world.entity.LivingEntity nmsEntity = cp.getHandle();

			// Calculate velocity: 1.52552 blocks/tick horizontal, 0.5 blocks/tick vertical
			Vector velocity = bonzoDirection.multiply(1.52552);
			velocity.setY(0.5);

			// Set the velocity directly through NMS for precise control
			nmsEntity.setDeltaMovement(velocity.getX(), velocity.getY(), velocity.getZ());
			nmsEntity.hurtMarked = true;

			p.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, l, 350, 0, 0, 0, 0.75);
			p.getWorld().spawnParticle(Particle.CRIT, l, 150, 0, 0, 0, 2);
			p.playSound(l, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2.0F, 1.0F);
		} else {
			fireWindCharge(p);
		}
		return true;
	}

	private static void fireWindCharge(Player p) {
		Location l = p.getEyeLocation();
		l.add(l.getDirection().setY(0).normalize().multiply(0.5));
		Utils.scheduleTask(() -> {
			l.getWorld().spawnEntity(l, EntityType.WIND_CHARGE);
			l.getWorld().spawnEntity(l, EntityType.WIND_CHARGE);
		}, 1);
	}

	@Override
	public boolean onLeftClick(Player p) {
		return false;
	}

	@Override
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