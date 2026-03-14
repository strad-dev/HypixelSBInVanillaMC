"""
Extract Minecraft Programmer Art textures from the game assets for wiki use.
Run: python extract_textures.py
"""
import os
import zipfile

PROG_ART_ZIP = os.path.expandvars(
	r"%APPDATA%\.minecraft\assets\objects\24\24e1b0a4692ff3379226ae300009cf124b992b79"
)
TEXTURE_DIR = "docs/assets/textures"
ITEM_PREFIX = "assets/minecraft/textures/item/"
BLOCK_PREFIX = "assets/minecraft/textures/block/"

# Map of wiki texture name -> path inside the programmer art zip
TEXTURES = {
	# Weapons
	"netherite_sword": f"{ITEM_PREFIX}netherite_sword.png",
	"stone_sword": f"{ITEM_PREFIX}stone_sword.png",
	"bow": f"{ITEM_PREFIX}bow.png",
	"iron_sword": f"{ITEM_PREFIX}iron_sword.png",
	"diamond_sword": f"{ITEM_PREFIX}diamond_sword.png",

	# Armor
	"netherite_helmet": f"{ITEM_PREFIX}netherite_helmet.png",
	"elytra": f"{ITEM_PREFIX}elytra.png",
	"netherite_chestplate": f"{ITEM_PREFIX}netherite_chestplate.png",
	"netherite_leggings": f"{ITEM_PREFIX}netherite_leggings.png",
	"netherite_boots": f"{ITEM_PREFIX}netherite_boots.png",
	"golden_helmet": f"{ITEM_PREFIX}golden_helmet.png",
	"chainmail_helmet": f"{ITEM_PREFIX}chainmail_helmet.png",
	"chainmail_chestplate": f"{ITEM_PREFIX}chainmail_chestplate.png",
	"chainmail_leggings": f"{ITEM_PREFIX}chainmail_leggings.png",
	"chainmail_boots": f"{ITEM_PREFIX}chainmail_boots.png",
	"diamond_helmet": f"{ITEM_PREFIX}diamond_helmet.png",
	"diamond_chestplate": f"{ITEM_PREFIX}diamond_chestplate.png",
	"diamond_leggings": f"{ITEM_PREFIX}diamond_leggings.png",
	"diamond_boots": f"{ITEM_PREFIX}diamond_boots.png",

	# Tools
	"netherite_shovel": f"{ITEM_PREFIX}netherite_shovel.png",
	"netherite_pickaxe": f"{ITEM_PREFIX}netherite_pickaxe.png",
	"stick": f"{ITEM_PREFIX}stick.png",
	"blaze_rod": f"{ITEM_PREFIX}blaze_rod.png",
	"diamond": f"{ITEM_PREFIX}diamond.png",

	# Ingredients - mining
	"iron_ingot": f"{ITEM_PREFIX}iron_ingot.png",
	"gold_ingot": f"{ITEM_PREFIX}gold_ingot.png",
	"lapis_lazuli": f"{ITEM_PREFIX}lapis_lazuli.png",
	"redstone": f"{ITEM_PREFIX}redstone.png",
	"emerald": f"{ITEM_PREFIX}emerald.png",
	"netherite_scrap": f"{ITEM_PREFIX}netherite_scrap.png",
	"cobblestone": f"{BLOCK_PREFIX}cobblestone.png",
	"raw_iron": f"{ITEM_PREFIX}raw_iron.png",
	"raw_gold": f"{ITEM_PREFIX}raw_gold.png",
	"coal": f"{ITEM_PREFIX}coal.png",
	"raw_copper": f"{ITEM_PREFIX}raw_copper.png",

	# Ingredients - misc
	"feather": f"{ITEM_PREFIX}feather.png",
	"dragon_egg": f"{BLOCK_PREFIX}dragon_egg.png",
	"ender_pearl": f"{ITEM_PREFIX}ender_pearl.png",
	"cobweb": f"{BLOCK_PREFIX}cobweb.png",
	"cooked_porkchop": f"{ITEM_PREFIX}cooked_porkchop.png",
	"amethyst_shard": f"{ITEM_PREFIX}amethyst_shard.png",
	"shears": f"{ITEM_PREFIX}shears.png",
	"netherite_upgrade_smithing_template": f"{ITEM_PREFIX}netherite_upgrade_smithing_template.png",

	# Blocks used in crafting
	"nether_star": f"{ITEM_PREFIX}nether_star.png",
	"obsidian": f"{BLOCK_PREFIX}obsidian.png",
	"packed_ice": f"{BLOCK_PREFIX}packed_ice.png",
	"apple": f"{ITEM_PREFIX}apple.png",
	"gold_block": f"{BLOCK_PREFIX}gold_block.png",
	"redstone_block": f"{BLOCK_PREFIX}redstone_block.png",
	"purple_concrete": f"{BLOCK_PREFIX}purple_concrete.png",
	"purple_stained_glass": f"{BLOCK_PREFIX}purple_stained_glass.png",
	"stone": f"{BLOCK_PREFIX}stone.png",

	# Summon items
	"rotten_flesh": f"{ITEM_PREFIX}rotten_flesh.png",
	"fermented_spider_eye": f"{ITEM_PREFIX}fermented_spider_eye.png",
	"egg": f"{ITEM_PREFIX}egg.png",
	"ender_eye": f"{ITEM_PREFIX}ender_eye.png",
	"warped_fungus": f"{BLOCK_PREFIX}warped_fungus.png",
	"quartz": f"{ITEM_PREFIX}quartz.png",

	# Enchanted book
	"enchanted_book": f"{ITEM_PREFIX}enchanted_book.png",

	# Other useful
	"paper": f"{ITEM_PREFIX}paper.png",
	"spider_eye": f"{ITEM_PREFIX}spider_eye.png",
	"bone": f"{ITEM_PREFIX}bone.png",
	"string": f"{ITEM_PREFIX}string.png",
	"gunpowder": f"{ITEM_PREFIX}gunpowder.png",
	"arrow": f"{ITEM_PREFIX}arrow.png",

	# Crafting/UI elements
	"crafting_table": f"{BLOCK_PREFIX}crafting_table_front.png",
}

# Fallback to main JAR for textures not in programmer art (newer items)
MC_JAR = os.path.expandvars(r"%APPDATA%\.minecraft\versions\1.21.11\1.21.11.jar")

# Newer items only in main JAR (not in programmer art)
FALLBACK_TEXTURES = {
	"breeze_rod": f"{ITEM_PREFIX}breeze_rod.png",
}


def main():
	os.makedirs(TEXTURE_DIR, exist_ok=True)

	extracted = 0
	missing = []

	# First extract from programmer art
	print(f"Extracting from Programmer Art...")
	with zipfile.ZipFile(PROG_ART_ZIP, 'r') as pak:
		all_files = pak.namelist()
		for name, jar_path in TEXTURES.items():
			if jar_path in all_files:
				data = pak.read(jar_path)
				out_path = os.path.join(TEXTURE_DIR, f"{name}.png")
				with open(out_path, 'wb') as f:
					f.write(data)
				extracted += 1
			else:
				missing.append((name, jar_path))

	print(f"  Got {extracted}/{len(TEXTURES)} from Programmer Art")

	# Try missing textures from main JAR (newer items not in programmer art)
	all_missing = list(missing)
	for name, path in FALLBACK_TEXTURES.items():
		all_missing.append((name, path))

	if all_missing and os.path.exists(MC_JAR):
		print(f"Checking main JAR for {len(all_missing)} remaining textures...")
		still_missing = []
		with zipfile.ZipFile(MC_JAR, 'r') as jar:
			all_files = jar.namelist()
			for name, jar_path in all_missing:
				if jar_path in all_files:
					data = jar.read(jar_path)
					out_path = os.path.join(TEXTURE_DIR, f"{name}.png")
					with open(out_path, 'wb') as f:
						f.write(data)
					extracted += 1
					print(f"  Fallback: {name}")
				else:
					still_missing.append((name, jar_path))
		missing = still_missing

	print(f"\nTotal extracted: {extracted}")
	if missing:
		print(f"Still missing {len(missing)}:")
		for name, path in missing:
			print(f"  {name} -> {path}")
	else:
		print("All textures extracted successfully!")


if __name__ == "__main__":
	main()
