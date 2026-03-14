# Damage System

The plugin completely overrides vanilla Minecraft damage calculations using NMS (net.minecraft.server) code.

## How It Works

The `CustomDamage` listener runs at `EventPriority.LOWEST`, meaning it processes damage events before any other listener. This gives it full control over the damage pipeline.

### DamageData

Every damage event is tracked with a `DamageData` object that records context:

<div class="stat-line"><strong>Blocking state</strong> -- whether the target is blocking with a shield</div>
<div class="stat-line"><strong>Arrow type</strong> -- what kind of arrow was used (if ranged, e.g. from <a href="../items/weapons/terminator/">Terminator</a>)</div>
<div class="stat-line"><strong>Lightning</strong> -- whether the damage came from lightning</div>
<div class="stat-line"><strong>Trident</strong> -- whether a trident was thrown</div>

### Damage Flow

1. Detect if the damaged entity or damager is a [custom mob](../bosses/index.md).
2. If so, call `whenDamaging()` (for the attacker) or `whenDamaged()` (for the target).
3. Apply custom damage modifiers from the mob's implementation.
4. Calculate final damage using the modified values.

### Armor Calculation

Armor reduces damage using Minecraft's standard formula, but **armor toughness has been removed** from the system entirely. Only the base armor value matters for damage reduction.

## Damage Types

Different [bosses](../bosses/index.md) may be immune to or interact differently with these damage categories:

<div class="stat-line"><strong>Melee</strong> -- direct player hits (e.g. <a href="../items/weapons/hyperion/">Hyperion</a>, <a href="../items/weapons/dark-claymore/">Dark Claymore</a>)</div>
<div class="stat-line"><strong>Ranged</strong> -- arrows and other projectiles (e.g. <a href="../items/weapons/terminator/">Terminator</a>)</div>
<div class="stat-line"><strong>Magic</strong> -- potion effects, Dissonance, and other magic sources</div>
<div class="stat-line"><strong>Ranged-Special</strong> -- special ranged attacks from <a href="item-abilities/">abilities</a></div>
<div class="stat-line"><strong>Absolute</strong> -- damage that bypasses all reductions (used by some boss mechanics)</div>
