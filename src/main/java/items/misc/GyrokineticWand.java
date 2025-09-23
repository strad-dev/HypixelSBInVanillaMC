package items.misc;

import items.AbilityItem;
import misc.Plugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GyrokineticWand implements AbilityItem {
	private static final int MANA_COST = 50;
	private static final String COOLDOWN_TAG = "GyroCooldown";
	private static final int COOLDOWN = 600;

	public static ItemStack getItem() {
		ItemStack gyro = new ItemStack(Material.BLAZE_ROD);

		ItemMeta data = gyro.getItemMeta();
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
		gyro.setItemMeta(data);

		return gyro;
	}

	@Override
	public boolean onRightClick(Player p) {
		return false;
	}

	@Override
	public boolean onLeftClick(Player p) {
		RayTraceResult location = p.rayTraceBlocks(25);
		if(location == null) {
			return false;
		}
		Location rift = location.getHitPosition().toLocation(p.getWorld());
		p.getWorld().spawnParticle(Particle.PORTAL, rift, 1000);
		rift.setY(rift.getY() + 1);
		rift.add(0, 0, 0);
		new BukkitRunnable() {
			float pitch = 0.5f;

			@Override
			public void run() {
				if(pitch >= 2.0f) {
					cancel();
					return;
				}
				p.playSound(rift, Sound.ENTITY_ENDERMAN_TELEPORT, 2.0f, pitch);
				pitch += 0.025f;
			}
		}.runTaskTimer(Plugin.getInstance(), 0L, 1L);

		Material[] blockTypes = {Material.OBSIDIAN, Material.PURPLE_CONCRETE, Material.PURPLE_STAINED_GLASS};
		List<FallingBlock> fallingBlocks = new ArrayList<>();
		boolean[] effectEnded = {false}; // Flag to track if effect has ended

		// Initial block spawning
		for(double angle = 0; angle < Math.PI * 2; angle += Math.PI / 32) {
			double x = rift.getX() + Math.cos(angle) * 10;
			double z = rift.getZ() + Math.sin(angle) * 10;

			// Find the next air block going upwards from the requested location
			double y = rift.getY();
			while (!Objects.requireNonNull(rift.getWorld()).getBlockAt((int) x, (int) y, (int) z).getType().isAir() && y < rift.getWorld().getMaxHeight()) {
				y++;
			}
			y += 0.05; // Place falling block 0.05 above the air block
			Location blockLoc = new Location(rift.getWorld(), x, Math.max(y, rift.getY()), z);

			Material blockType = blockTypes[(int) (Math.random() * blockTypes.length)];
			FallingBlock block = rift.getWorld().spawnFallingBlock(blockLoc, blockType.createBlockData());
			block.setGravity(false);
			block.setDropItem(false);
			block.setInvulnerable(true);

			block.setHurtEntities(false);
			block.setPersistent(true);
			block.setCancelDrop(true);

			fallingBlocks.add(block);
		}

		// Ensure blocks don't place by constantly checking and respawning if needed
		BukkitRunnable blockChecker = new BukkitRunnable() {
			@Override
			public void run() {
				if(effectEnded[0]) {
					cancel();
					return;
				}

				List<FallingBlock> toRemove = new ArrayList<>();
				List<FallingBlock> toAdd = new ArrayList<>();

				for(FallingBlock block : fallingBlocks) {
					if(block.isDead() || !block.isValid()) {
						// The block has turned into a real block or disappeared
						toRemove.add(block);

						// Create a new block at the same location
						Material blockType = blockTypes[(int) (Math.random() * blockTypes.length)];
						Location respawnLoc = block.getLocation().clone().add(0, 0.1, 0);

						FallingBlock newBlock = respawnLoc.getWorld().spawnFallingBlock(respawnLoc, blockType.createBlockData());
						newBlock.setGravity(false);
						newBlock.setDropItem(false);
						newBlock.setInvulnerable(true);
						newBlock.setHurtEntities(false);
						newBlock.setPersistent(true);
						newBlock.setCancelDrop(true);

						toAdd.add(newBlock);
					}
				}

				// Update our block list
				fallingBlocks.removeAll(toRemove);
				fallingBlocks.addAll(toAdd);

				if(fallingBlocks.isEmpty()) {
					cancel();
				}
			}
		};
		blockChecker.runTaskTimer(Plugin.getInstance(), 5L, 5L);

		new BukkitRunnable() {
			int tick = 0;
			final List<Location> initialPositions = new ArrayList<>();
			final double TOTAL_TICKS = 60.0; // 3 seconds
			final double CONVERGE_TICKS = 40.0; // 2 seconds to converge

			@Override
			public void run() {
				if(tick == 0) {
					for(FallingBlock block : fallingBlocks) {
						initialPositions.add(block.getLocation());
					}
				}

				if(tick >= TOTAL_TICKS) {
					effectEnded[0] = true;
					fallingBlocks.forEach(block -> {
						if(block.isValid()) {
							block.remove();
						}
					});
					fallingBlocks.clear();
					cancel();
					return;
				}

				double progress = Math.min(tick / CONVERGE_TICKS, 1.0);

				for(int i = 0; i < fallingBlocks.size(); i++) {
					FallingBlock block = fallingBlocks.get(i);
					if(!block.isValid()) continue; // Skip invalid blocks

					Location startLoc = i < initialPositions.size() ?
							initialPositions.get(i) : block.getLocation();

					// Linear interpolation between start and rift
					Vector direction = rift.toVector().subtract(startLoc.toVector());
					Vector currentPos = startLoc.toVector().add(direction.multiply(progress));

					if(progress < 1.0) {
						// Add constant wobble effect 
						Vector wobble = new Vector(
								(Math.random() - 0.5) * 4,
								(Math.random() - 0.5) * 4,
								(Math.random() - 0.5) * 4
						);

						currentPos.add(wobble);
					} else {
						// Keep blocks at center with slight wobble
						Vector wobble = new Vector(
								(Math.random() - 0.5) * 2,
								(Math.random() - 0.5) * 2,
								(Math.random() - 0.5) * 2
						);
						currentPos = rift.toVector().add(wobble);
					}

					Location targetLoc = currentPos.toLocation(rift.getWorld());

					// Ensure minimum height above ground
					// Find the next air block going upwards from the requested location
					double minY = rift.getY();
					while (!Objects.requireNonNull(rift.getWorld()).getBlockAt(targetLoc.getBlockX(), (int) minY, targetLoc.getBlockY()).getType().isAir() && minY < rift.getWorld().getMaxHeight()) {
						minY++;
					}
					minY += 0.05; // Place falling block 0.05 above the air block
					targetLoc.setY(Math.max(targetLoc.getY(), minY));
					targetLoc.setY(Math.max(targetLoc.getY(), minY));

					// Calculate velocity vector based on distance to target with increased speed
					Vector velocity = targetLoc.toVector().subtract(block.getLocation().toVector()).multiply(0.3);

					block.setVelocity(velocity);
				}

				tick++;
			}
		}.runTaskTimer(Plugin.getInstance(), 0L, 1L);


		for(Entity e : rift.getWorld().getNearbyEntities(rift, 10, 10, 10)) {
			if(e instanceof LivingEntity entity && !(e.equals(p))) {
				new BukkitRunnable() {
					int tick = 0;

					@Override
					public void run() {
						if(tick >= 60) { // 3 seconds * 20 ticks
							cancel();
							return;
						}

						if(tick < 10) { // First 0.5 seconds - pull in
							Location entityLoc = entity.getLocation();
							double x = (rift.getX() - entityLoc.getX()) / 5;
							double y = (rift.getY() - entityLoc.getY()) / 5;
							double z = (rift.getZ() - entityLoc.getZ()) / 5;
							entity.setVelocity(new Vector(x, y, z));
						} else { // Next 2.5 seconds - keep at rift
							entity.teleport(rift);
						}

						tick++;
					}
				}.runTaskTimer(Plugin.getInstance(), 0L, 1L);
			}
		}
		return true;
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