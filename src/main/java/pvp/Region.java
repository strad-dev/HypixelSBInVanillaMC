package pvp;

import org.bukkit.Location;

/**
 * A simple world-bound axis-aligned box used for FFA bounds, the safe zone, and the duel arena.
 */
public class Region {
	public final String world;
	public final double minX, minY, minZ, maxX, maxY, maxZ;

	public Region(String world, double[] min, double[] max) {
		this.world = world;
		this.minX = Math.min(min[0], max[0]);
		this.minY = Math.min(min[1], max[1]);
		this.minZ = Math.min(min[2], max[2]);
		this.maxX = Math.max(min[0], max[0]);
		this.maxY = Math.max(min[1], max[1]);
		this.maxZ = Math.max(min[2], max[2]);
	}

	public boolean contains(Location loc) {
		if (loc == null || loc.getWorld() == null) return false;
		if (world != null && !world.equalsIgnoreCase(loc.getWorld().getName())) return false;
		return loc.getX() >= minX && loc.getX() <= maxX
				&& loc.getY() >= minY && loc.getY() <= maxY
				&& loc.getZ() >= minZ && loc.getZ() <= maxZ;
	}
}
