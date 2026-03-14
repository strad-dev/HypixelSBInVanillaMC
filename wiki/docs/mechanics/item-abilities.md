# Item Abilities

Items with special abilities implement the `AbilityItem` interface. These abilities consume [mana (Intelligence)](mana-system.md) and may have cooldowns.

## How Abilities Work

1. The player activates the ability via **right-click** or **left-click** (depends on the item).
2. The system checks if the player has enough [Intelligence](mana-system.md).
3. If sufficient mana is available, the cost is deducted and the ability executes.
4. If not enough mana, the ability fails and the player is notified.
5. A **cooldown** is applied (if the item has one), preventing reuse until it expires.

## Ability Properties

<div class="stat-line"><strong>Activation type</strong> -- right-click or left-click</div>
<div class="stat-line"><strong>Mana cost</strong> -- Intelligence consumed per use</div>
<div class="stat-line"><strong>Cooldown</strong> -- duration in ticks (20 ticks = 1 second)</div>
<div class="stat-line"><strong>Cooldown tag</strong> -- identifier that tracks the cooldown on the player</div>

## No-Cooldown Items

These items can be spammed as long as you have [mana](mana-system.md):

<div class="stat-line"><a href="../items/weapons/hyperion/">Hyperion</a> -- teleport + AoE damage on right-click</div>
<div class="stat-line"><a href="../items/tools/aspect-of-the-void/">Aspect of the Void (AOTV)</a> -- short-range teleport</div>
<div class="stat-line"><a href="../items/tools/bonzo-staff/">Bonzo's Staff</a> -- ranged magic attack</div>

## Mana Management

Since mana regenerates slowly (+1 every 5 seconds), managing your Intelligence is critical. Spamming abilities will drain your pool quickly, leaving you unable to use abilities when you need them most during [boss fights](../bosses/index.md).
