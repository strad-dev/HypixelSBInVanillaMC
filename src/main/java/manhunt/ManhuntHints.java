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
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** {@code manhunt.runner-hints}: on start, each online Speedrunner is told about the nearest structure of interest. */
public final class ManhuntHints {
	private ManhuntHints() {}

	public enum Level { OFF, DIRECTION, TYPE, COORDS }

	private static Level level = Level.TYPE;

	/** Same radius as /locate, in grid cells. */
	private static final int SEARCH_RINGS = 100;

	private static final String[] DIRECTIONS = {"North", "Northeast", "East", "Southeast", "South", "Southwest", "West", "Northwest"};

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
		for(Map.Entry<RandomSpreadStructurePlacement, Map<Holder<Structure>, String>> grid : grids.entrySet()) {
			RandomSpreadStructurePlacement spread = grid.getKey();
			int spacing = spread.spacing();
			for(int ring = 0; ring <= SEARCH_RINGS; ring++) {
				// Ring r is at least (r - 1) * spacing chunks away on one axis.
				double closest = Math.max(0, ring - 1) * spacing * 16.0;
				if(closest * closest > best) break;
				for(int x = -ring; x <= ring; x++) {
					for(int z = -ring; z <= ring; z++) {
						if(Math.abs(x) != ring && Math.abs(z) != ring) continue;
						ChunkPos chunk = spread.getPotentialStructureChunk(seed, chunkX + spacing * x, chunkZ + spacing * z);
						for(Map.Entry<Holder<Structure>, String> entry : grid.getValue().entrySet()) {
							if(!startsAt(world, entry.getKey().value(), spread, chunk)) continue;
							BlockPos found = spread.getLocatePos(chunk);
							double dx = found.getX() - at.getX();
							double dz = found.getZ() - at.getZ();
							if(dx * dx + dz * dz < best) {
								best = dx * dx + dz * dz;
								pos = found;
								type = entry.getValue();
							}
							break;
						}
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

	/** Same check as vanilla's locate, which keeps it private. */
	private static boolean startsAt(ServerLevel world, Structure structure, StructurePlacement placement, ChunkPos chunk) {
		StructureCheckResult result = world.structureManager().checkStructurePresence(chunk, structure, placement, false);
		if(result == StructureCheckResult.START_NOT_PRESENT) return false;
		if(result == StructureCheckResult.START_PRESENT) return true;
		ChunkAccess access = world.getChunk(chunk.x(), chunk.z(), ChunkStatus.STRUCTURE_STARTS);
		StructureStart start = world.structureManager().getStartForStructure(SectionPos.bottomOf(access), structure, access);
		return start != null && start.isValid();
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
		return DIRECTIONS[Math.floorMod((int) Math.round(degrees / 45), 8)];
	}
}
