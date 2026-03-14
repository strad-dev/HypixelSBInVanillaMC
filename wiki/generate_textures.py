"""
Generate simple placeholder item textures for the wiki.
Run: python generate_textures.py
Requires: pip install Pillow

These are colored placeholder icons. Replace with real Minecraft textures
from a resource pack if desired.
"""
import os
from PIL import Image, ImageDraw

TEXTURE_DIR = "docs/assets/textures"
SIZE = 32

# Color definitions for each item type
ITEMS = {
	# Weapons
	"netherite_sword": ("#4D4143", "S"),
	"stone_sword": ("#8B8B8B", "S"),
	"bow": ("#8B6832", "B"),

	# Armor
	"netherite_helmet": ("#4D4143", "H"),
	"elytra": ("#A0A0D0", "E"),
	"netherite_chestplate": ("#4D4143", "C"),
	"netherite_leggings": ("#4D4143", "L"),
	"netherite_boots": ("#4D4143", "B"),
	"golden_helmet": ("#FFDD00", "H"),

	# Tools
	"netherite_shovel": ("#4D4143", "Sh"),
	"netherite_pickaxe": ("#4D4143", "P"),
	"stick": ("#C49A3F", "|"),
	"breeze_rod": ("#6EC8D7", "|"),
	"blaze_rod": ("#FFD733", "|"),
	"diamond": ("#4AEDD9", "D"),

	# Ingredients - mining
	"iron_ingot": ("#D8D8D8", "Fe"),
	"gold_ingot": ("#FFDD00", "Au"),
	"lapis_lazuli": ("#3355CC", "La"),
	"redstone": ("#FF0000", "Rs"),
	"emerald": ("#00DD00", "Em"),
	"netherite_scrap": ("#4D4143", "Nt"),
	"stone": ("#8B8B8B", "St"),
	"gold_block": ("#FFDD00", "GB"),

	# Ingredients - misc
	"feather": ("#FFFFFF", "F"),
	"dragon_egg": ("#1B1B2F", "DE"),
	"chiseled_quartz": ("#EDE4D4", "Co"),
	"redstone_block": ("#FF0000", "RB"),
	"amethyst_shard": ("#9955DD", "Am"),
	"shears": ("#CCCCCC", "Sh"),
	"ender_pearl": ("#0F6F5F", "EP"),
	"cobweb": ("#FFFFFF", "W"),
	"cooked_porkchop": ("#CC7744", "V"),
	"netherite_upgrade": ("#4D4143", "EU"),
	"enderman_spawn_egg": ("#0F1F1F", "NO"),

	# Ingredients - wither lords
	"paper": ("#F0F0F0", "Pa"),
	"enchanted_book": ("#9955DD", "EB"),

	# Summon items
	"rotten_flesh": ("#7F5537", "RF"),
	"fermented_spider_eye": ("#8B0000", "SE"),
	"egg": ("#FFEEDD", "Eg"),
	"ender_eye": ("#00AA55", "EE"),
	"warped_fungus": ("#00BBAA", "WF"),
	"quartz": ("#EDE4D4", "Q"),
	"wither_skeleton_spawn_egg": ("#444444", "WS"),

	# Base materials (for crafting grids)
	"nether_star": ("#FFFFCC", "NS"),
	"obsidian": ("#1B1033", "Ob"),
	"purple_concrete": ("#7B2FBE", "PC"),
	"purple_stained_glass": ("#7B2FBE", "PG"),
	"packed_ice": ("#8DB3E2", "PI"),
	"apple": ("#FF3333", "Ap"),

	# Result/output marker
	"crafting_table": ("#8B6832", "CT"),
}


def generate_texture(name, color, label):
	"""Generate a simple colored square with a label."""
	img = Image.new("RGBA", (SIZE, SIZE), color)
	draw = ImageDraw.Draw(img)

	# Add a darker border
	r, g, b = Image.new("RGB", (1, 1), color).getpixel((0, 0))
	dark = (max(0, r - 60), max(0, g - 60), max(0, b - 60))
	light = (min(255, r + 40), min(255, g + 40), min(255, b + 40))

	# Draw border
	draw.rectangle([0, 0, SIZE - 1, SIZE - 1], outline=dark)
	draw.rectangle([1, 1, SIZE - 2, SIZE - 2], outline=light)

	# Draw text label
	try:
		text_color = "#FFFFFF" if sum([r, g, b]) < 400 else "#000000"
		bbox = draw.textbbox((0, 0), label)
		tw = bbox[2] - bbox[0]
		th = bbox[3] - bbox[1]
		tx = (SIZE - tw) // 2
		ty = (SIZE - th) // 2
		draw.text((tx, ty), label, fill=text_color)
	except Exception:
		pass

	path = os.path.join(TEXTURE_DIR, f"{name}.png")
	img.save(path)
	print(f"  Generated {path}")


def main():
	os.makedirs(TEXTURE_DIR, exist_ok=True)
	print(f"Generating {len(ITEMS)} textures...")
	for name, (color, label) in ITEMS.items():
		generate_texture(name, color, label)

	# Also generate an empty/air slot
	img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
	img.save(os.path.join(TEXTURE_DIR, "empty.png"))
	print(f"  Generated empty.png")

	print(f"\nDone! {len(ITEMS) + 1} textures in {TEXTURE_DIR}/")
	print("Replace these with real Minecraft textures from a resource pack for best results.")


if __name__ == "__main__":
	main()
