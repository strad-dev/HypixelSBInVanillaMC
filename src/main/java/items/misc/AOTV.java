package items.misc;

import items.AbilityItem;
import misc.Plugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
	public boolean hasLeftClickAbility() {
		return false;
	}

	@Override
	public boolean onRightClick(Player p) {
		if(p.isSneaking()) {
			RayTraceResult result = p.rayTraceBlocks(61);
			if(result != null) {
				Block b = result.getHitBlock();
				Location l = b.getLocation().add(0.5, 1, 0.5);
				if(l.getBlock().getType().isSolid() || l.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
					return false;
				}
				l.setYaw(p.getEyeLocation().getYaw());
				l.setPitch(p.getEyeLocation().getPitch());
				p.setFallDistance(0);
				p.playSound(p, Sound.ENTITY_ENDER_DRAGON_HURT, 1, 0.50F);
				p.teleport(l);
				return true;
			}
			return false;
		} else {
			Location origin = p.getLocation().clone();
			RayTraceResult result = p.rayTraceBlocks(13.65);
			if(result == null) {
				Location targetLoc = p.getLocation().add(p.getLocation().getDirection().multiply(12));
				targetLoc.setX(Math.floor(targetLoc.getX()) + 0.5);
				targetLoc.setY(Math.floor(targetLoc.getY()));
				targetLoc.setZ(Math.floor(targetLoc.getZ()) + 0.5);

				// Check if the target location is safe
				Block feetBlock = targetLoc.getBlock();
				Block headBlock = feetBlock.getRelative(BlockFace.UP);

				// If either block is solid, we need to adjust
				if(!feetBlock.isPassable() || !headBlock.isPassable()) {
					// Try to move up until we find a safe spot or reach original height
					double originalY = p.getLocation().getY();
					Location checkLoc = targetLoc.clone();
					boolean foundSafe = false;

					// Check up to 10 blocks up or until at original height
					for(int i = 0; i < 10; i++) {
						checkLoc.add(0, 1, 0);
						Block checkFeet = checkLoc.getBlock();
						Block checkHead = checkFeet.getRelative(BlockFace.UP);

						// Check if this position is safe (2 blocks of air)
						if(checkFeet.isPassable() && checkHead.isPassable()) {
							// Also check we're not in a 1-block gap if above original height
							if(checkLoc.getY() >= originalY) {
								Block aboveHead = checkHead.getRelative(BlockFace.UP);
								if(!aboveHead.isPassable()) {
									// This is a 1-block gap at or above original height - skip it
									continue;
								}
							}

							targetLoc = checkLoc.clone();
							foundSafe = true;
							break;
						}

						// Stop if we've reached or passed original height and no safe spot
						if(checkLoc.getY() >= originalY) {
							break;
						}
					}

					// If no safe spot found, don't teleport
					if(!foundSafe) {
						return false;
					}
				}

				// Additional check for 1-block tall spaces when below original height
				if(targetLoc.getY() < p.getLocation().getY()) {
					Block aboveHead = targetLoc.getBlock().getRelative(BlockFace.UP, 2);
					if(!aboveHead.isPassable()) {
						// This would put player in crawl mode below their starting position
						// Try to find a better spot
						for(int i = 1; i <= 3; i++) {
							Location upLoc = targetLoc.clone().add(0, i, 0);
							Block upFeet = upLoc.getBlock();
							Block upHead = upFeet.getRelative(BlockFace.UP);
							Block upAbove = upHead.getRelative(BlockFace.UP);

							if(upFeet.isPassable() && upHead.isPassable() && upAbove.isPassable()) {
								targetLoc = upLoc;
								break;
							}
						}
					}
				}

				targetLoc.setYaw(origin.getYaw());
				targetLoc.setPitch(origin.getPitch());
				p.teleport(targetLoc);
			} else {
				switch(result.getHitBlockFace()) {
					case SELF -> {
						// empty case
					}
					case UP -> {
						Location l = result.getHitBlock().getLocation().add(0.5, 1, 0.5);
						l.setYaw(origin.getYaw());
						l.setPitch(origin.getPitch());
						p.teleport(l);
					}
					case DOWN -> {
						Location l = result.getHitBlock().getLocation().add(0.5, -2, 0.5);
						l.setYaw(origin.getYaw());
						l.setPitch(origin.getPitch());
						p.teleport(l);
					}
					default -> {
						// Hit a side face - backtrack until we find a safe spot
						Location hitLocation = result.getHitPosition().toLocation(p.getWorld());
						Vector direction = origin.getDirection().normalize();

						// Calculate max backtrack distance (don't go past player's origin)
						double maxBacktrack = origin.distance(hitLocation);

						// Backtrack from the exact hit point
						Location checkLoc = hitLocation.clone();
						Location lastSafe = null;
						double totalBacktracked = 0;

						// Backtrack in smaller increments for more precision
						for(int i = 0; i < 120; i++) { // 120 * 0.1 = 12 blocks
							// Backtrack by 0.1 blocks for precision
							checkLoc.subtract(direction.clone().multiply(0.1));
							totalBacktracked += 0.1;

							// Don't go past the player's starting position
							if(totalBacktracked > maxBacktrack) {
								break;
							}

							// Check current block
							Block feetBlock = checkLoc.getBlock();
							Block headBlock = feetBlock.getRelative(BlockFace.UP);

							if(feetBlock.isPassable() && headBlock.isPassable()) {
								// This spot is safe, but keep checking for the optimal position
								lastSafe = checkLoc.clone();

								// Check if we've backtracked enough (at least 0.5 blocks from wall)
								if(checkLoc.distance(hitLocation) >= 0.5) {
									// Center on the block we're in
									Location l = new Location(checkLoc.getWorld(), Math.floor(checkLoc.getX()) + 0.5, Math.floor(checkLoc.getY()), Math.floor(checkLoc.getZ()) + 0.5);
									l.setYaw(origin.getYaw());
									l.setPitch(origin.getPitch());
									p.teleport(l);
									break;
								}
							}
						}

						// If we found a safe spot but didn't teleport yet
						if(lastSafe != null) {
							Location l = new Location(lastSafe.getWorld(), Math.floor(lastSafe.getX()) + 0.5, Math.floor(lastSafe.getY()), Math.floor(lastSafe.getZ()) + 0.5);
							l.setYaw(origin.getYaw());
							l.setPitch(origin.getPitch());
							p.teleport(l);
						}
					}
				}
			}
			p.setFallDistance(0);
			p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
		}
		return true;
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
