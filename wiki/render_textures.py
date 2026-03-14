"""
Post-process extracted textures:
1. Render block textures as isometric cubes (Minecraft inventory style)
2. Add enchantment glint overlay to custom item textures

Run after extract_textures.py:
    python render_textures.py
"""
import os
from PIL import Image, ImageDraw, ImageEnhance

TEXTURE_DIR = "docs/assets/textures"

# Block textures that should be rendered as isometric cubes
BLOCKS = [
	"obsidian",
	"packed_ice",
	"gold_block",
	"redstone_block",
	"purple_concrete",
	"purple_stained_glass",
	"stone",
	"cobblestone",
	"crafting_table",
	"dragon_egg",
]

# Items that should get the enchantment glint
ENCHANTED_ITEMS = [
	"netherite_sword", "stone_sword", "bow",
	"netherite_helmet", "elytra", "netherite_chestplate",
	"netherite_leggings", "netherite_boots", "golden_helmet",
	"netherite_shovel", "netherite_pickaxe", "stick",
	"breeze_rod", "blaze_rod", "diamond",
	"feather", "ender_pearl", "cobweb", "cooked_porkchop",
	"amethyst_shard", "shears", "netherite_upgrade_smithing_template",
	"paper", "enchanted_book", "quartz", "nether_star",
	"rotten_flesh", "fermented_spider_eye", "egg", "ender_eye",
	"warped_fungus", "iron_ingot", "gold_ingot", "lapis_lazuli",
	"redstone", "emerald", "netherite_scrap",
]


def render_isometric_block(texture):
	"""
	Render a 16x16 block texture as a 32x32 isometric cube using
	the exact same projection Minecraft uses for GUI items:
	rotateX(30°) rotateY(225°) orthographic projection.
	"""
	import math

	N = 16
	src = texture.convert("RGBA").resize((N, N), Image.NEAREST)

	W, H = 32, 32
	canvas = Image.new("RGBA", (W, H), (0, 0, 0, 0))

	top_src = ImageEnhance.Brightness(src).enhance(1.1)
	left_src = ImageEnhance.Brightness(src).enhance(0.6)
	right_src = ImageEnhance.Brightness(src).enhance(0.8)

	# Minecraft GUI rotation: rotateY(225°) then rotateX(30°)
	cy, sy_ = math.cos(math.radians(225)), math.sin(math.radians(225))
	cx, sx = math.cos(math.radians(30)), math.sin(math.radians(30))

	def project(x, y, z):
		# Y rotation
		xr = x * cy - z * sy_
		zr = x * sy_ + z * cy
		# X rotation, negate for screen coords (Y-down)
		xf = xr
		yf = -(y * cx - zr * sx)
		return (xf, yf)

	# Project unit cube corners, then scale/center to fit 32x32
	corners = [(x, y, z) for x in (0, 1) for y in (0, 1) for z in (0, 1)]
	projected = [project(*c) for c in corners]
	xs = [p[0] for p in projected]
	ys = [p[1] for p in projected]
	scale = min(30 / (max(xs) - min(xs)), 30 / (max(ys) - min(ys)))
	ox = 16 - (min(xs) + max(xs)) / 2 * scale
	oy = 16 - (min(ys) + max(ys)) / 2 * scale

	def screen(x, y, z):
		px, py = project(x, y, z)
		return (px * scale + ox, py * scale + oy)

	# Key screen positions (with negated Y, top of cube is at screen top)
	TFL = screen(0, 1, 0)  # (16.0, 14.5) - bottom of top diamond
	TFR = screen(1, 1, 0)  # ( 2.5,  7.7) - left of top diamond
	TBL = screen(0, 1, 1)  # (29.5,  7.7) - right of top diamond
	TBR = screen(1, 1, 1)  # (16.0,  1.0) - topmost point

	BFL = screen(0, 0, 0)  # (16.0, 31.0) - bottommost point
	BFR = screen(1, 0, 0)  # ( 2.5, 24.3) - bottom-left
	BBL = screen(0, 0, 1)  # (29.5, 24.3) - bottom-right

	def compute_inverse_affine(p0, p1, p2, n):
		"""Compute PIL affine coefficients (inverse mapping) from 3 correspondences:
		tex(0,0)->p0, tex(n,0)->p1, tex(0,n)->p2"""
		a = (p1[0] - p0[0]) / n
		b = (p2[0] - p0[0]) / n
		c = p0[0]
		d = (p1[1] - p0[1]) / n
		e = (p2[1] - p0[1]) / n
		f = p0[1]
		det = a * e - b * d
		A = e / det
		B = -b / det
		C = (b * f - e * c) / det
		D = -d / det
		E = a / det
		F = (d * c - a * f) / det
		return (A, B, C, D, E, F)

	# Texture mapping: tex(0,0)->p0, tex(N,0)->p1 (u-axis), tex(0,N)->p2 (v-axis)
	#
	# Top face: In Minecraft, top face texture u=East(+x), v=South(+z)
	#   +x direction on screen goes from TBL toward TBR (right to top-center)
	#   +z direction on screen goes from TFR toward TBR (left to top-center)
	#   So: tex(0,0)->TFL, tex(u=N)->TBL(+x), tex(v=N)->TFR(+z)
	#   Wait no: +x goes LEFT on screen (TFL->TFR is -x), +z goes RIGHT (TFL->TBL is +z)
	#   tex(0,0) = (-x,-z) corner = TFL
	#   tex(N,0) = (+x,-z) corner = TFR (screen left)
	#   tex(0,N) = (-x,+z) corner = TBL (screen right)
	#
	# Left face (x=1): face texture u=+z, v=-y (down)
	#   tex(0,0) = top-front = TFR, tex(N,0) = top-back = TBR, tex(0,N) = bottom-front = BFR
	#
	# Right face (z=1): face texture u=-x, v=-y (down)
	#   tex(0,0) = top-back = TBR, tex(N,0) = top-front = TBL, tex(0,N) = bottom-back = BBR
	# Visible faces:
	#   Top    (y=1): TFR, TBR, TBL, TFL - diamond at top
	#   Left   (z=0): TFR, TFL, BFL, BFR - parallelogram on screen-left
	#   Right  (x=0): TBL, TFL, BFL, BBL - parallelogram on screen-right
	for face_src, p0, p1, p2 in [
		(top_src,   TFR, TBR, TFL),   # top: tex(0,0)->left, tex(u)->top, tex(v)->bottom
		(left_src,  TFR, TFL, BFR),   # left (z=0): tex(0,0)->top-left, tex(u)->top-right, tex(v)->bot-left
		(right_src, TBL, TFL, BBL),   # right (x=0): tex(0,0)->top-right, tex(u)->top-left, tex(v)->bot-right
	]:
		coeffs = compute_inverse_affine(p0, p1, p2, N)
		xform = face_src.transform(
			(W, H), Image.AFFINE, coeffs,
			resample=Image.NEAREST
		)
		canvas.paste(xform, mask=xform)

	return canvas


def add_enchant_glint(texture):
	"""
	Add a Minecraft-style enchantment glint overlay to a texture.
	Creates a static purple/blue diagonal sheen.
	"""
	img = texture.convert("RGBA")
	w, h = img.size

	# Create the glint overlay
	glint = Image.new("RGBA", (w, h), (0, 0, 0, 0))
	draw = ImageDraw.Draw(glint)

	# Draw diagonal purple streaks
	stripe_width = max(2, w // 5)
	for i in range(-h, w + h, stripe_width):
		r = 100 + (i % 40)
		g = 40 + (i % 20)
		b = 180 + (i % 50)
		alpha = 55 + (i % 25)
		draw.line([(i, 0), (i - h, h)], fill=(r, g, b, alpha), width=max(1, w // 8))

	# Blend glint onto original, only where original has pixels
	result = img.copy()
	for y in range(h):
		for x in range(w):
			orig = img.getpixel((x, y))
			if orig[3] > 0:
				gl = glint.getpixel((x, y))
				nr = min(255, orig[0] + gl[0] * gl[3] // 512)
				ng = min(255, orig[1] + gl[1] * gl[3] // 512)
				nb = min(255, orig[2] + gl[2] * gl[3] // 512)
				result.putpixel((x, y), (nr, ng, nb, orig[3]))

	return result


def main():
	processed = 0

	# First re-extract blocks from source (they may have been overwritten)
	# We need the flat originals to render isometric from
	print("Re-extracting block textures from source...")
	import zipfile
	PROG_ART = os.path.expandvars(
		r"%APPDATA%\.minecraft\assets\objects\24\24e1b0a4692ff3379226ae300009cf124b992b79"
	)
	MC_JAR = os.path.expandvars(r"%APPDATA%\.minecraft\versions\1.21.11\1.21.11.jar")

	block_paths = {
		"obsidian": "assets/minecraft/textures/block/obsidian.png",
		"packed_ice": "assets/minecraft/textures/block/packed_ice.png",
		"gold_block": "assets/minecraft/textures/block/gold_block.png",
		"redstone_block": "assets/minecraft/textures/block/redstone_block.png",
		"purple_concrete": "assets/minecraft/textures/block/purple_concrete.png",
		"purple_stained_glass": "assets/minecraft/textures/block/purple_stained_glass.png",
		"stone": "assets/minecraft/textures/block/stone.png",
		"cobblestone": "assets/minecraft/textures/block/cobblestone.png",
		"crafting_table": "assets/minecraft/textures/block/crafting_table_front.png",
		"dragon_egg": "assets/minecraft/textures/block/dragon_egg.png",
	}
	item_paths = {}
	for name in ENCHANTED_ITEMS:
		item_paths[name] = f"assets/minecraft/textures/item/{name}.png"

	# Re-extract from programmer art first, then fallback to main JAR
	for source_zip in [PROG_ART, MC_JAR]:
		if not os.path.exists(source_zip):
			continue
		with zipfile.ZipFile(source_zip, 'r') as zf:
			all_files = zf.namelist()
			for name, jar_path in {**block_paths, **item_paths}.items():
				out = os.path.join(TEXTURE_DIR, f"{name}.png")
				if not os.path.exists(out) or name in block_paths:
					# Always re-extract blocks; only extract items if missing
					if jar_path in all_files:
						data = zf.read(jar_path)
						with open(out, 'wb') as f:
							f.write(data)
	# Also re-extract items that may only be in main JAR
	with zipfile.ZipFile(MC_JAR, 'r') as zf:
		all_files = zf.namelist()
		for name, jar_path in item_paths.items():
			out = os.path.join(TEXTURE_DIR, f"{name}.png")
			if jar_path in all_files:
				data = zf.read(jar_path)
				with open(out, 'wb') as f:
					f.write(data)

	# Render blocks as isometric cubes
	print("Rendering isometric blocks...")
	for name in BLOCKS:
		path = os.path.join(TEXTURE_DIR, f"{name}.png")
		if os.path.exists(path):
			src = Image.open(path)
			iso = render_isometric_block(src)
			iso.save(path)
			processed += 1
			print(f"  Isometric: {name}")

	# Add enchantment glint to items
	print("Adding enchantment glint to items...")
	for name in ENCHANTED_ITEMS:
		path = os.path.join(TEXTURE_DIR, f"{name}.png")
		if os.path.exists(path):
			src = Image.open(path)
			enchanted = add_enchant_glint(src)
			enchanted.save(path)
			processed += 1
			print(f"  Enchanted: {name}")

	# Add glint to isometric blocks too
	print("Adding glint to blocks...")
	for name in BLOCKS:
		path = os.path.join(TEXTURE_DIR, f"{name}.png")
		if os.path.exists(path):
			src = Image.open(path)
			enchanted = add_enchant_glint(src)
			enchanted.save(path)
			processed += 1
			print(f"  Enchanted block: {name}")

	print(f"\nDone! Processed {processed} textures.")


if __name__ == "__main__":
	main()
