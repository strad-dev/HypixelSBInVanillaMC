# CLAUDE.md — SkyBlock in Vanilla

## Project Overview
Minecraft Paper/Spigot plugin that brings Hypixel SkyBlock items, bosses, and mechanics to vanilla survival. Combat-focused, designed as a standalone plugin — **incompatible with other combat plugins**.

- **Java:** 21 | **Minecraft:** 1.21.11 | **Server:** Paper or Spigot
- **Build:** `mvn clean install` (output in `target/`)
- **Entry point:** `misc.Plugin` (singleton via `Plugin.getInstance()`)
- **No test suite** — all testing is manual/in-game

## Project Structure
```
src/main/java/
  commands/          # Command executors (registered in Plugin.onEnable)
  items/             # Custom items by category
    armor/           # Armor pieces
    ingredients/     # Crafting materials (mining/, misc/, witherLords/)
    misc/            # Misc items
    summonItems/     # Boss summoning items
    weapons/         # Weapons (Scylla, Claymore, etc.)
  listeners/         # Event handlers (21+ classes)
  mobs/              # Boss/mob implementations (57+ classes)
    enderDragons/    # Dragon variants
    generic/         # General bosses
    hardmode/        # Hard mode variants (enderDragons/, generic/, withers/)
    withers/         # Wither lords
  misc/              # Core utilities (Plugin, Utils, AddRecipes, BossBarManager)
src/main/resources/
  plugin.yml         # Plugin metadata
```

## Key Patterns

### Items
- Implement `items.CustomItem` interface; register in `CustomItem.ItemRegistry`
- Static `getItem()` returns a fresh `ItemStack` with lore, attributes, enchants
- **First lore line = item ID** (namespaced: `skyblock/{category}/{name}`)
- Ability items implement `AbilityItem` (right/left click, mana cost, cooldown)
- Glint-only items use `setEnchantmentGlintOverride(true)` — do NOT add Knockback 1
- `StripCreativeCustomData.refreshItem()` rebuilds items from `getItem()` on login; only preserves enchantments for items without glint override

### Mobs
- Implement `mobs.CustomMob` interface; register in `CustomMob.MobRegistry`
- Singletons — one instance per mob type
- Key methods: `onSpawn()`, `whenDamaged()`, `whenDamaging()`
- Tag with `"SkyblockBoss"` scoreboard tag; use `setPersistent(true)`
- Hard mode triggered by BAD_OMEN potion effect on nearest player

### Listeners
- Register in `Plugin.onEnable()` via `getServer().getPluginManager().registerEvents()`
- `CustomDamage` uses `EventPriority.LOWEST` (runs first)
- Heavy NMS usage in damage system (`net.minecraft.*`, CraftBukkit classes)

### Damage System (`CustomDamage`)
- `DamageData` tracks context: blocking, arrow type, lightning, trident
- Flow: detect custom mob → call `whenDamaging()`/`whenDamaged()` → apply modifiers

### Mana System
- Scoreboard objective `"Intelligence"` (0–2500 max)
- Passive regen: +1 every 5 seconds
- Action bar display via NMS packet

## Conventions
- **Indentation:** Tabs
- **Naming:** PascalCase classes, camelCase methods/vars
- **Item IDs:** `skyblock/{category}/{snake_case_name}`
- **Colors:** `ChatColor.*` constants
- **Potion effects:** duration `-1` = infinite, amplifier `255` = max level
- **No JavaDoc** — code is self-documenting; comments only for complex logic

## Commit Style
- Bug fixes: `fix {description}` (e.g., "fix enchant glint?")
- Features: `{verb} {feature}` (e.g., "implement wind burst")
- Balancing: `more balancing`, `{item} changes`
- Keep messages short and lowercase

## Adding New Content

### New Item
1. Create class in `items/{category}/`
2. Implement `CustomItem` (or `AbilityItem` for abilities)
3. Add static `getItem()` with lore ID as first line
4. Register in `CustomItem.ItemRegistry`
5. Add to `StripCreativeCustomData.refreshItem()` switch
6. Optionally add recipe in `AddRecipes`

### New Boss
1. Create class in `mobs/{category}/`
2. Implement `CustomMob`
3. Register in `CustomMob.MobRegistry`
4. Add drops in `CustomDrops`
5. Optionally add advancement in `Plugin.setupAdvancements()`

### New Command
1. Create class in `commands/` implementing `CommandExecutor`
2. Register in `Plugin.onEnable()` and `plugin.yml`

## Dependencies
- **Spigot API** 1.21.11-R0.1-SNAPSHOT (provided)
- **Spigot NMS** 1.21.11-R0.1-SNAPSHOT remapped-mojang (provided)
- **DiscordSRV** 1.30.4 (optional soft dependency)

## Other Instructions
- NEVER build/compile