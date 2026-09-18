package misc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Minecraft's default font, in pixels, and the word wrapping that follows from it. Lore is written as one
 * sentence and broken into lines here, so a line that quotes a live number ("8.7 damage", "13.25 damage")
 * stays inside the tooltip instead of being hand-wrapped to fit whatever the number was that day.
 *
 * <p><b>The width table is a copy of StradsPlugin's {@code driving.trains.MinecraftFont}</b> - the two plugins
 * share no dependency, so keep them in step if either is corrected.
 */
public final class MinecraftFont {
	private MinecraftFont() {}

	/** Every glyph is one pixel wider in bold. */
	private static final int BOLD_EXTRA = 1;

	/**
	 * The widest a lore line may get: the Hyperion's own ability header, which is the longest line the
	 * tooltip has to show and therefore sets the shape of the box. Measured rather than guessed, and the
	 * {@code RIGHT CLICK} half is bold.
	 */
	public static final int LORE_WIDTH = width("Ability: Wither Impact ") + width("RIGHT CLICK", true);

	/** Glyph width including the 1px of spacing that follows it. */
	public static int charWidth(char c) {
		return switch(c) {
			case 'i', '!', '|' -> 2;
			case 'l', '\'', '`', '.', ',', ':', ';' -> 3;
			case 'I', 't', '"', '(', ')', '*', '<', '>', '{', '}', '[', ']', ' ' -> 4;
			case 'f', 'k' -> 5;
			case '@', '~' -> 7;
			default -> 6; // most letters, digits, and symbols
		};
	}

	public static int width(String text) {
		return width(text, false);
	}

	public static int width(String text, boolean bold) {
		int width = 0;
		for(int i = 0; i < text.length(); i++) {
			width += charWidth(text.charAt(i)) + (bold ? BOLD_EXTRA : 0);
		}
		return width;
	}

	/** Rendered width of a component, bold runs inside it included. */
	public static int width(Component component) {
		int width = 0;
		for(Glyph glyph : flatten(component)) {
			width += glyph.width();
		}
		return width;
	}

	/**
	 * Breaks one MiniMessage paragraph into lore lines no wider than {@link #LORE_WIDTH}.
	 *
	 * @see #wrap(Component, int)
	 */
	public static List<Component> wrapLore(String miniMessage) {
		return wrap(Utils.mm(miniMessage), LORE_WIDTH);
	}

	/**
	 * Breaks {@code component} into lines no wider than {@code maxWidth}, at word boundaries, <b>keeping
	 * every colour and decoration</b> - a line may start mid-way through a coloured run and still come out
	 * the right colour, because the text is taken apart per glyph and put back together per style run.
	 *
	 * <p>Runs of spaces are held back rather than measured into the line they would end: a wrap drops them,
	 * so a break after {@code "you."} does not leave the next line indented by the two spaces that followed
	 * it. A single word wider than {@code maxWidth} is left to overrun on a line of its own rather than
	 * broken in half.
	 */
	public static List<Component> wrap(Component component, int maxWidth) {
		List<Component> lines = new ArrayList<>();
		List<Glyph> line = new ArrayList<>();
		List<Glyph> word = new ArrayList<>();
		List<Glyph> spaces = new ArrayList<>();
		int lineWidth = 0;
		int wordWidth = 0;
		int spaceWidth = 0;

		for(Glyph glyph : flatten(component)) {
			if(glyph.character() == '\n') {
				endWord(lines, line, word, spaces, lineWidth, wordWidth, spaceWidth, maxWidth);
				lines.add(build(line));
				line.clear();
				word.clear();
				spaces.clear();
				lineWidth = 0;
				wordWidth = 0;
				spaceWidth = 0;
				continue;
			}

			if(glyph.character() == ' ') {
				if(!word.isEmpty()) {
					lineWidth = endWord(lines, line, word, spaces, lineWidth, wordWidth, spaceWidth, maxWidth);
					word.clear();
					spaces.clear();
					wordWidth = 0;
					spaceWidth = 0;
				}
				spaces.add(glyph);
				spaceWidth += glyph.width();
			} else {
				word.add(glyph);
				wordWidth += glyph.width();
			}
		}

		endWord(lines, line, word, spaces, lineWidth, wordWidth, spaceWidth, maxWidth);
		if(!line.isEmpty() || lines.isEmpty()) {
			lines.add(build(line));
		}
		return lines;
	}

	/**
	 * Commits the word being built to the current line, or starts a new line with it when it will not fit.
	 * The spaces in front of it come along only if it stays put.
	 *
	 * @return the width of the current line afterwards
	 */
	private static int endWord(List<Component> lines, List<Glyph> line, List<Glyph> word, List<Glyph> spaces,
							   int lineWidth, int wordWidth, int spaceWidth, int maxWidth) {
		if(word.isEmpty()) {
			return lineWidth;
		}
		if(!line.isEmpty() && lineWidth + spaceWidth + wordWidth > maxWidth) {
			lines.add(build(line));
			line.clear();
			line.addAll(word);
			return wordWidth;
		}
		line.addAll(spaces);
		line.addAll(word);
		return lineWidth + spaceWidth + wordWidth;
	}

	/** One character and the style it ended up with once every parent's style had been folded in. */
	private record Glyph(char character, Style style) {
		int width() {
			return charWidth(character) + (style.hasDecoration(TextDecoration.BOLD) ? BOLD_EXTRA : 0);
		}
	}

	private static List<Glyph> flatten(Component component) {
		List<Glyph> out = new ArrayList<>();
		flatten(component, Style.empty(), out);
		return out;
	}

	private static void flatten(Component component, Style parent, List<Glyph> out) {
		// The child wins wherever it says anything; the parent fills in the rest.
		Style style = component.style().merge(parent, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
		if(component instanceof TextComponent text) {
			for(char c : text.content().toCharArray()) {
				out.add(new Glyph(c, style));
			}
		}
		for(Component child : component.children()) {
			flatten(child, style, out);
		}
	}

	/** Rebuilds a line, one component per run of characters that share a style. */
	private static Component build(List<Glyph> glyphs) {
		TextComponent.Builder out = Component.text();
		StringBuilder run = new StringBuilder();
		Style current = null;
		for(Glyph glyph : glyphs) {
			if(current != null && !current.equals(glyph.style())) {
				out.append(Component.text(run.toString(), current));
				run.setLength(0);
			}
			current = glyph.style();
			run.append(glyph.character());
		}
		if(!run.isEmpty()) {
			out.append(Component.text(run.toString(), current));
		}
		// Lore is italic by default; mm() kills it on the paragraph, and each line it is cut into needs it too.
		return out.build().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
	}
}
