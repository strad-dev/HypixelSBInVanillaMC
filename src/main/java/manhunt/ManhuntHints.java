package manhunt;

import com.mojang.datafixers.util.Pair;
import misc.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
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

	private static Level level = Level.DIRECTION;

	/** Same radius as /locate. */
	private static final int SEARCH_CHUNKS = 100;

	private static final String[] DIRECTIONS = {"North", "Northeast", "East", "Southeast", "South", "Southwest", "West", "Northwest"};

	public static void setLevel(String value) {
		level = switch(value.toLowerCase(Locale.ROOT)) {
			case "false", "off", "none" -> Level.OFF;
			case "type" -> Level.TYPE;
			case "coords" -> Level.COORDS;
			default -> Level.DIRECTION;
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
		Map<String, HolderSet<Structure>> named = interest(world.registryAccess().lookupOrThrow(Registries.STRUCTURE));
		List<Holder<Structure>> all = new ArrayList<>();
		for(HolderSet<Structure> set : named.values()) {
			for(Holder<Structure> holder : set) all.add(holder);
		}
		Location at = p.getLocation();
		Pair<BlockPos, Holder<Structure>> found = world.getChunkSource().getGenerator().findNearestMapStructure(world,
				HolderSet.direct(all), BlockPos.containing(at.getX(), at.getY(), at.getZ()), SEARCH_CHUNKS, false);
		if(found == null) return;

		BlockPos pos = found.getFirst();
		String type = "Structure";
		for(Map.Entry<String, HolderSet<Structure>> entry : named.entrySet()) {
			if(entry.getValue().contains(found.getSecond())) {
				type = entry.getKey();
				break;
			}
		}
		String direction = direction(pos.getX() - at.getX(), pos.getZ() - at.getZ());
		p.sendMessage(Utils.msg(switch(level) {
			case TYPE -> "<gray>" + type + " " + direction;
			case COORDS -> "<gray>" + type + " at " + pos.getX() + ", " + pos.getZ();
			default -> "<gray>" + direction;
		}));
	}

	/** Display name to structures. Ocean ruins share a name; tags cover their biome variants. */
	private static Map<String, HolderSet<Structure>> interest(HolderGetter<Structure> structures) {
		Map<String, HolderSet<Structure>> out = new LinkedHashMap<>();
		out.put("Desert Pyramid", HolderSet.direct(structures.getOrThrow(BuiltinStructures.DESERT_PYRAMID)));
		out.put("Igloo", HolderSet.direct(structures.getOrThrow(BuiltinStructures.IGLOO)));
		out.put("Jungle Pyramid", HolderSet.direct(structures.getOrThrow(BuiltinStructures.JUNGLE_TEMPLE)));
		out.put("Ocean Ruin", HolderSet.direct(structures.getOrThrow(BuiltinStructures.OCEAN_RUIN_COLD),
				structures.getOrThrow(BuiltinStructures.OCEAN_RUIN_WARM)));
		out.put("Pillager Outpost", HolderSet.direct(structures.getOrThrow(BuiltinStructures.PILLAGER_OUTPOST)));
		out.put("Ruined Portal", structures.getOrThrow(StructureTags.RUINED_PORTAL));
		out.put("Shipwreck", structures.getOrThrow(StructureTags.SHIPWRECK));
		out.put("Village", structures.getOrThrow(StructureTags.VILLAGE));
		return out;
	}

	/** North is -Z. */
	private static String direction(double dx, double dz) {
		double degrees = Math.toDegrees(Math.atan2(dx, -dz));
		return DIRECTIONS[Math.floorMod((int) Math.round(degrees / 45), 8)];
	}
}
