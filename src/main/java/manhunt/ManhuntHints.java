package manhunt;

import misc.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** {@code manhunt.runner-hints}: on start, each online Speedrunner is told about the nearest structure of interest. */
public final class ManhuntHints {
	private ManhuntHints() {}

	public enum Level { OFF, DIRECTION, TYPE, COORDS }

	private static Level level = Level.TYPE;

	/** Same radius as /locate, in grid cells. */
	private static final int SEARCH_RINGS = 100;

	/** Share of footprint columns that must be open to the sky. */
	private static final double MIN_EXPOSED = 0.25;

	private static final String[] DIRECTIONS = {"North", "North-northeast", "Northeast", "East-northeast", "East",
			"East-southeast", "Southeast", "South-southeast", "South", "South-southwest", "Southwest", "West-southwest",
			"West", "West-northwest", "Northwest", "North-northwest"};

	public static void setLevel(String value) {
		level = switch(value.toLowerCase(Locale.ROOT)) {
			case "false", "off", "none" -> Level.OFF;
			case "direction" -> Level.DIRECTION;
			case "coords" -> Level.COORDS;
			default -> Level.TYPE;
		};
	}

	public static void give() {
		if(level == Level.OFF) return;
		for(Player p : Manhunt.onlineSpeedrunners()) {
			give(p);
		}
	}

	private static void give(Player p) {
		ServerLevel world = ((CraftWorld) p.getWorld()).getHandle();
		ChunkGeneratorStructureState state = world.getChunkSource().getGeneratorState();
		Map<RandomSpreadStructurePlacement, Map<Holder<Structure>, String>> grids = new LinkedHashMap<>();
		for(Map.Entry<Holder<Structure>, String> entry : interest(world.registryAccess().lookupOrThrow(Registries.STRUCTURE)).entrySet()) {
			for(StructurePlacement placement : state.getPlacementsForStructure(entry.getKey())) {
				if(placement instanceof RandomSpreadStructurePlacement spread) {
					grids.computeIfAbsent(spread, k -> new LinkedHashMap<>()).put(entry.getKey(), entry.getValue());
				}
			}
		}

		// Not findNearestMapStructure: it returns the first hit in the first ring with one, so a portal 428 away
		// beat one 134 away in the next ring. Every ring that could still hold something closer is searched.
		Location at = p.getLocation();
		int chunkX = SectionPos.blockToSectionCoord(at.getX());
		int chunkZ = SectionPos.blockToSectionCoord(at.getZ());
		long seed = state.getLevelSeed();
		BlockPos pos = null;
		String type = null;
		double best = Double.MAX_VALUE;
		// Grids advance together, nearest ring first, so a rare grid (desert pyramid) never searches past a closer
		// hit from another one.
		List<Map.Entry<RandomSpreadStructurePlacement, Map<Holder<Structure>, String>>> order = new ArrayList<>(grids.entrySet());
		int[] next = new int[order.size()];
		while(true) {
			int pick = -1;
			double closest = 0;
			for(int i = 0; i < order.size(); i++) {
				if(next[i] > SEARCH_RINGS) continue;
				// Ring r is at least (r - 1) * spacing chunks away on one axis.
				double bound = Math.max(0, next[i] - 1) * order.get(i).getKey().spacing() * 16.0;
				if(pick == -1 || bound < closest) {
					pick = i;
					closest = bound;
				}
			}
			if(pick == -1 || closest * closest > best) break;
			RandomSpreadStructurePlacement spread = order.get(pick).getKey();
			int spacing = spread.spacing();
			int ring = next[pick]++;
			for(int x = -ring; x <= ring; x++) {
				for(int z = -ring; z <= ring; z++) {
					if(Math.abs(x) != ring && Math.abs(z) != ring) continue;
					ChunkPos chunk = spread.getPotentialStructureChunk(seed, chunkX + spacing * x, chunkZ + spacing * z);
					BlockPos found = spread.getLocatePos(chunk);
					double dx = found.getX() - at.getX();
					double dz = found.getZ() - at.getZ();
					// The presence check samples terrain noise; only pay for it when the cell could win.
					if(dx * dx + dz * dz >= best) continue;
					for(Map.Entry<Holder<Structure>, String> entry : order.get(pick).getValue().entrySet()) {
						if(!startsAt(world, entry.getKey().value(), spread, chunk)) continue;
						best = dx * dx + dz * dz;
						pos = found;
						type = entry.getValue();
						break;
					}
				}
			}
		}
		if(pos == null) return;

		String direction = direction(pos.getX() - at.getX(), pos.getZ() - at.getZ());
		p.sendMessage(Utils.msg(switch(level) {
			case TYPE -> "<gray>" + type + " " + direction;
			case COORDS -> "<gray>" + type + " at " + pos.getX() + ", " + pos.getZ();
			default -> "<gray>" + direction;
		}));
	}

	/** Vanilla's locate check (private there), plus {@link #exposed}. */
	private static boolean startsAt(ServerLevel world, Structure structure, StructurePlacement placement, ChunkPos chunk) {
		StructureCheckResult result = world.structureManager().checkStructurePresence(chunk, structure, placement, false);
		if(result == StructureCheckResult.START_NOT_PRESENT) return false;
		ChunkAccess access = world.getChunk(chunk.x(), chunk.z(), ChunkStatus.STRUCTURE_STARTS);
		StructureStart start = world.structureManager().getStartForStructure(SectionPos.bottomOf(access), structure, access);
		return start != null && start.isValid() && exposed(world, start);
	}

	/** Enough of the footprint reaches the predicted surface. Water counts as sky; trees don't exist yet. */
	private static boolean exposed(ServerLevel world, StructureStart start) {
		ChunkGenerator generator = world.getChunkSource().getGenerator();
		RandomState random = world.getChunkSource().randomState();
		BoundingBox bounds = start.getBoundingBox();
		int columns = 0;
		int open = 0;
		for(int x = bounds.minX(); x <= bounds.maxX(); x += 4) {
			for(int z = bounds.minZ(); z <= bounds.maxZ(); z += 4) {
				int top = Integer.MIN_VALUE;
				for(StructurePiece piece : start.getPieces()) {
					BoundingBox box = piece.getBoundingBox();
					if(box.intersects(x, z, x, z)) top = Math.max(top, box.maxY());
				}
				if(top == Integer.MIN_VALUE) continue;
				columns++;
				if(top + 1 >= generator.getBaseHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, world, random)) open++;
			}
		}
		return columns > 0 && open >= columns * MIN_EXPOSED;
	}

	/** Structure to display name. The nether ruined portal is left out: it never generates in the overworld. */
	private static Map<Holder<Structure>, String> interest(HolderGetter<Structure> structures) {
		Map<Holder<Structure>, String> out = new LinkedHashMap<>();
		name(out, structures, "Desert Pyramid", BuiltinStructures.DESERT_PYRAMID);
		name(out, structures, "Jungle Pyramid", BuiltinStructures.JUNGLE_TEMPLE);
		name(out, structures, "Ocean Ruin", BuiltinStructures.OCEAN_RUIN_COLD, BuiltinStructures.OCEAN_RUIN_WARM);
		name(out, structures, "Pillager Outpost", BuiltinStructures.PILLAGER_OUTPOST);
		name(out, structures, "Ruined Portal", BuiltinStructures.RUINED_PORTAL_STANDARD, BuiltinStructures.RUINED_PORTAL_DESERT,
				BuiltinStructures.RUINED_PORTAL_JUNGLE, BuiltinStructures.RUINED_PORTAL_SWAMP, BuiltinStructures.RUINED_PORTAL_MOUNTAIN,
				BuiltinStructures.RUINED_PORTAL_OCEAN);
		name(out, structures, "Shipwreck", BuiltinStructures.SHIPWRECK, BuiltinStructures.SHIPWRECK_BEACHED);
		name(out, structures, "Village", BuiltinStructures.VILLAGE_PLAINS, BuiltinStructures.VILLAGE_DESERT,
				BuiltinStructures.VILLAGE_SAVANNA, BuiltinStructures.VILLAGE_SNOWY, BuiltinStructures.VILLAGE_TAIGA);
		return out;
	}

	@SafeVarargs
	private static void name(Map<Holder<Structure>, String> out, HolderGetter<Structure> structures, String name, ResourceKey<Structure>... keys) {
		for(ResourceKey<Structure> key : keys) {
			out.put(structures.getOrThrow(key), name);
		}
	}

	/** North is -Z. */
	private static String direction(double dx, double dz) {
		double degrees = Math.toDegrees(Math.atan2(dx, -dz));
		return DIRECTIONS[Math.floorMod((int) Math.round(degrees / 22.5), DIRECTIONS.length)];
	}
}
