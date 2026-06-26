package pvp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Tiny atomic JSON store for PvP stats (temp file + rename so the network-shared stats file is
 * never read half-written). Self-contained so SkyBlock stays standalone.
 */
public final class PvpJson {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	private PvpJson() {}

	public static <T> T load(Path file, Class<T> type, T fallback) {
		try {
			if (file == null || !Files.exists(file)) return fallback;
			try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
				T v = GSON.fromJson(r, type);
				return v != null ? v : fallback;
			}
		} catch (Exception e) {
			return fallback;
		}
	}

	public static void save(Path file, Object value) {
		try {
			Path parent = file.getParent();
			if (parent != null) Files.createDirectories(parent);
			Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
			try (Writer w = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
				GSON.toJson(value, w);
			}
			try {
				Files.move(tmp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
			} catch (AtomicMoveNotSupportedException ex) {
				Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception ignored) {
		}
	}
}
