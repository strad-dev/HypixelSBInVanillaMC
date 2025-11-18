package listeners;

public enum DamageType {
	MELEE,
	/*
	 * Melee Damage Type
	 * ------------------
	 * Used for all damage via Melee Weapons and Non-Environmental Abilities (e.g. Atoned Horror TNT)
	 * Affected by: Armor, Toughness, Protection, Resistance
	 * Knockback: Normal
	 * I-Frames: Normal
	 */

	MELEE_SWEEP,
	/*
	 * Melee Sweep Damage Type
	 * ------------------
	 * Used for all damage caused by Sweep attacks
	 */

	RANGED,
	/*
	 * Ranged Damage Type
	 * ------------------
	 * Used for all damage via Ranged Weapons (e.g. Bows, Tridents, Wither Skulls)
	 * Affected by: Armor, Toughness, Protection, Resistance
	 * Knockback: Reduced
	 * I-Frames: None
	 */
	RANGED_SPECIAL,
	/*
	 * Ranged Special Damage Type
	 * ------------------
	 * Used for custom Ranged damage from other sources (e.g. Terminator beam)
	 * Affected by: Armor, Toughness, Protection, Resistance
	 * Knockback: Reduced
	 * I-Frames: None
	 */
	MAGIC,
	/*
	 * Magic Damage Type
	 * ------------------
	 * Used for all damage via Potions and Status Effects
	 * Affected by: Toughness, Protection, Resistance
	 * Knockback: None
	 * I-Frames: None
	 */

	PLAYER_MAGIC,
	/*
	 * Player Magic Damage Type
	 * ------------------
	 * Used for all other Magic damage - either Player Abilities (e.g. Wither Impact, Ice Spray) or other Magical attacks (e.g. MASTER Goldor AOE, Customly-Spawned TNT)
	 * Affected by: Armor, Toughness, Protection, Resistance
	 * Knockback: None
	 * I-Frames: None
	 */

	ENVIRONMENTAL,
	/*
	 * Environmental Damage Type
	 * ------------------
	 * Used for all damage via other methods (e.g. Fire)
	 * Affected by: Armor, Toughness, Protection, Resistance
	 * Knockback: None
	 * I-Frames: Special
	 */

	IFRAME_ENVIRONMENTAL,
	/*
	 * I-Frame Environmental Damage Type
	 * ------------------
	 * Used for all environmental damage that should proc normal I-Frames
	 * Affected by: Armor, Toughness, Protection, Resistance
	 * Knockback: None
	 * I-Frames: Normal
	 */

	FALL,
	/*
	 * Fall Damage Type
	 * ------------------
	 * Used for Fall & Physics-Induced Damage
	 * Affected by: Feather Falling, Toughness, Protection, Resistance
	 * Knockback: None
	 * I-Frames: Normal
	 */

	ABSOLUTE,
	/*
	 * ABSOLUTE Damage Type
	 * ------------------
	 * Used for damage that will always deal full damage
	 * Affected by: Totem of Undying
	 * Knockback: None
	 * I-Frames: None
	 */

	LETHAL_ABSOLUTE;
	/*
	 * ABSOLUTE Damage Type
	 * ------------------
	 * Used for damage that will always deal full damage
	 * Affected by: NOTHING
	 * Knockback: None
	 * I-Frames: None
	 */

	public static boolean isAbsoluteDamage(DamageType type) {
		return type == DamageType.ABSOLUTE || type == DamageType.LETHAL_ABSOLUTE;
	}

	public static String toString(DamageType type) {
		switch(type) {
			case MELEE -> {
				return "Melee Damage";
			}
			case MELEE_SWEEP -> {
				return "Sweep Damage";
			}
			case RANGED, RANGED_SPECIAL -> {
				return "Ranged Damage";
			}
			case MAGIC, PLAYER_MAGIC -> {
				return "Magic Damage";
			}
			case ENVIRONMENTAL, IFRAME_ENVIRONMENTAL -> {
				return "Environmental Damage";
			}
			case FALL -> {
				return "Fall Damage";
			}
			case ABSOLUTE, LETHAL_ABSOLUTE -> {
				return "Absolute Damage";
			}
			default -> throw new IllegalArgumentException("Unknown Damage Type");
		}
	}
}