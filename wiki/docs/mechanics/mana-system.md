# Mana System

The mana system is internally called **Intelligence** and powers all [item abilities](item-abilities.md) in the plugin.

## How It Works

<div class="stat-line"><strong>Maximum mana:</strong> 2500</div>
<div class="stat-line"><strong>Minimum mana:</strong> 0</div>
<div class="stat-line"><strong>Passive regen:</strong> +1 every 5 seconds</div>
<div class="stat-line"><strong>Tracked via:</strong> scoreboard objective <code>"Intelligence"</code></div>
<div class="stat-line"><strong>Display:</strong> action bar via NMS packets</div>

## Using Mana

[Ability items](item-abilities.md) consume Intelligence when activated:

1. Player activates an ability (right-click or left-click depending on the item).
2. The system checks if the player has enough Intelligence.
3. If yes, the mana cost is deducted and the ability executes.
4. If no, the ability fails and the player is notified.

## Mana Management

Mana regenerates slowly at +1 every 5 seconds. There is no way to increase the regeneration rate or maximum beyond 2500. Spamming abilities like [Hyperion](../items/weapons/hyperion.md) or [AOTV](../items/tools/aspect-of-the-void.md) will drain your pool quickly -- manage it carefully during boss fights.
