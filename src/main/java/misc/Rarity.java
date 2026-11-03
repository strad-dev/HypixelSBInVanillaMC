package misc;

/** Item rarity: name colour and the bottom lore line. */
public enum Rarity {
	COMMON("white"), UNCOMMON("green"), RARE("blue"), EPIC("dark_purple"), LEGENDARY("gold"), MYTHIC("light_purple");

	private final String colour;

	Rarity(String colour) {
		this.colour = colour;
	}

	public String colour() {
		return colour;
	}

	/** Bottom rarity line, same style as every custom item. {@code type} may be empty ({@code EPIC} alone). */
	public String lore(String type) {
		String label = type.isEmpty() ? name() : name() + " " + type;
		return "<" + colour + "><bold><obfuscated>a</obfuscated> " + label + " <obfuscated>a</obfuscated>";
	}
}
