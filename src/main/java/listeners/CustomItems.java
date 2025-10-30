package listeners;

import items.AbilityItem;
import items.CustomItem;
import items.misc.AOTV;
import items.weapons.Scylla;
import items.weapons.Terminator;
import misc.Plugin;
import misc.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomItems implements Listener {
	private static Score score;

	public static Score currentScore() {
		return score;
	}

	public String getID(ItemStack item) {
		if(item == null || !item.hasItemMeta()) {
			return "";
		} else if(!item.getItemMeta().hasLore()) {
			return "";
		} else return item.getItemMeta().getLore().getFirst();
	}

	public static List<EntityType> createList() {
		List<EntityType> doNotKill = new ArrayList<>();
		doNotKill.add(EntityType.ACACIA_BOAT);
		doNotKill.add(EntityType.ACACIA_CHEST_BOAT);
		doNotKill.add(EntityType.ALLAY);
		doNotKill.add(EntityType.ARMOR_STAND);
		doNotKill.add(EntityType.ARROW);
		doNotKill.add(EntityType.AXOLOTL);
		doNotKill.add(EntityType.BLOCK_DISPLAY);
		doNotKill.add(EntityType.BIRCH_BOAT);
		doNotKill.add(EntityType.BIRCH_CHEST_BOAT);
		doNotKill.add(EntityType.CAT);
		doNotKill.add(EntityType.CHERRY_BOAT);
		doNotKill.add(EntityType.CHERRY_CHEST_BOAT);
		doNotKill.add(EntityType.CHEST_MINECART);
		doNotKill.add(EntityType.COMMAND_BLOCK_MINECART);
		doNotKill.add(EntityType.DARK_OAK_BOAT);
		doNotKill.add(EntityType.DARK_OAK_CHEST_BOAT);
		doNotKill.add(EntityType.DONKEY);
		doNotKill.add(EntityType.DRAGON_FIREBALL);
		doNotKill.add(EntityType.FIREBALL);
		doNotKill.add(EntityType.EGG);
		doNotKill.add(EntityType.ENDER_PEARL);
		doNotKill.add(EntityType.EXPERIENCE_BOTTLE);
		doNotKill.add(EntityType.EXPERIENCE_ORB);
		doNotKill.add(EntityType.FALLING_BLOCK);
		doNotKill.add(EntityType.FIREWORK_ROCKET);
		doNotKill.add(EntityType.FISHING_BOBBER);
		doNotKill.add(EntityType.FURNACE_MINECART);
		doNotKill.add(EntityType.GLOW_ITEM_FRAME);
		doNotKill.add(EntityType.HOPPER_MINECART);
		doNotKill.add(EntityType.HORSE);
		doNotKill.add(EntityType.ITEM_FRAME);
		doNotKill.add(EntityType.ITEM_DISPLAY);
		doNotKill.add(EntityType.INTERACTION);
		doNotKill.add(EntityType.JUNGLE_BOAT);
		doNotKill.add(EntityType.JUNGLE_CHEST_BOAT);
		doNotKill.add(EntityType.LEASH_KNOT);
		doNotKill.add(EntityType.LIGHTNING_BOLT);
		doNotKill.add(EntityType.LLAMA);
		doNotKill.add(EntityType.LLAMA_SPIT);
		doNotKill.add(EntityType.MANGROVE_BOAT);
		doNotKill.add(EntityType.MANGROVE_CHEST_BOAT);
		doNotKill.add(EntityType.MARKER);
		doNotKill.add(EntityType.MINECART);
		doNotKill.add(EntityType.MULE);
		doNotKill.add(EntityType.OAK_BOAT);
		doNotKill.add(EntityType.OAK_CHEST_BOAT);
		doNotKill.add(EntityType.OCELOT);
		doNotKill.add(EntityType.PAINTING);
		doNotKill.add(EntityType.PARROT);
		doNotKill.add(EntityType.SHULKER_BULLET);
		doNotKill.add(EntityType.SKELETON_HORSE);
		doNotKill.add(EntityType.SMALL_FIREBALL);
		doNotKill.add(EntityType.SNOWBALL);
		doNotKill.add(EntityType.SPAWNER_MINECART);
		doNotKill.add(EntityType.SPECTRAL_ARROW);
		doNotKill.add(EntityType.SPRUCE_BOAT);
		doNotKill.add(EntityType.SPRUCE_CHEST_BOAT);
		doNotKill.add(EntityType.TEXT_DISPLAY);
		doNotKill.add(EntityType.TNT);
		doNotKill.add(EntityType.TRIDENT);
		doNotKill.add(EntityType.UNKNOWN);
		doNotKill.add(EntityType.VILLAGER);
		doNotKill.add(EntityType.WITHER_SKULL);
		doNotKill.add(EntityType.WOLF);
		return doNotKill;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack itemInUse = e.getItem();
		if(itemInUse != null && itemInUse.hasItemMeta()) {
			ItemMeta meta = itemInUse.getItemMeta();
			NamespacedKey key = new NamespacedKey(Plugin.getInstance(), "creative_menu");

			if(meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
				if(e.getAction() != Action.RIGHT_CLICK_AIR &&
						e.getAction() != Action.RIGHT_CLICK_BLOCK) {
					return;
				}

				if(p.getGameMode() != GameMode.CREATIVE) {
					return;
				}

				if(!itemInUse.hasItemMeta()) {
					return;
				}

				e.setCancelled(true);
				CreativeMenu.openCreativeMenu(p);
			}

			if(meta.hasLore()) {
				if(itemInUse.getItemMeta().getLore().getFirst().contains("skyblock/summon")) {
					e.setCancelled(true);
				}
			}
		}

		try {
			score = Objects.requireNonNull(Objects.requireNonNull(Plugin.getInstance().getServer().getScoreboardManager()).getMainScoreboard().getObjective("Intelligence")).getScore(p.getName());
		} catch(Exception exception) {
			Plugin.getInstance().getLogger().info("Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
			Bukkit.broadcastMessage(ChatColor.RED + "Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
			return;
		}

		if(Objects.equals(e.getHand(), EquipmentSlot.HAND)) {
			CustomItem temp = CustomItem.getItem(getID(itemInUse));
			AbilityItem item;
			if(temp instanceof AbilityItem) {
				item = (AbilityItem) temp;
			} else {
				item = null;
			}
			if(item != null) {
				if(!(e.getAction().equals(Action.LEFT_CLICK_BLOCK) && !item.hasLeftClickAbility())) {
					e.setCancelled(true);
				}
				if(!p.getScoreboardTags().contains("AbilityCooldown") || item instanceof Terminator) {
					if(score.getScore() < item.manaCost() && !p.getGameMode().equals(GameMode.CREATIVE)) {
						if(!((e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_AIR)) && !item.hasLeftClickAbility())) {
							p.sendMessage(ChatColor.RED + "You do not have enough Intelligence to use this ability!  Required Intelligence: " + item.manaCost());
							p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
						}
					} else if(p.getScoreboardTags().contains(item.cooldownTag()) && !(item.cooldownTag().equals("SalvationCooldown") && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)))) {
						if(!(e.getAction().equals(Action.LEFT_CLICK_BLOCK) && !item.hasLeftClickAbility())) {
							p.sendMessage(ChatColor.RED + "This ability is on cooldown!");
							p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
						}
					} else {
						boolean abilitySuccessful = false;
						if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
							abilitySuccessful = item.onRightClick(p);
						} else if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.PHYSICAL)) {
							abilitySuccessful = item.onLeftClick(p);
						}

						if(abilitySuccessful) {
							if(!p.getGameMode().equals(GameMode.CREATIVE)) {
								Score score = CustomItems.currentScore();
								score.setScore(score.getScore() - item.manaCost());
							}
							p.addScoreboardTag(item.cooldownTag());
							Utils.scheduleTask(() -> p.removeScoreboardTag(item.cooldownTag()), item.cooldown());
							p.addScoreboardTag("AbilityCooldown");
							Utils.scheduleTask(() -> p.removeScoreboardTag("AbilityCooldown"), 2L);
						}
					}
				} else {
					if(!(item instanceof AOTV) && !(item instanceof Scylla)) {
						p.sendMessage(ChatColor.RED + "You are doing that too fast!");
					}
				}
			}
			if(score.getScore() < 2500) {
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("Intelligence: " + score.getScore() + "/2500", ChatColor.AQUA.asBungee()));
			} else {
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.AQUA + "Intelligence: " + score.getScore() + "/2500 " + ChatColor.RED + ChatColor.BOLD + "MAX INTELLIGENCE"));
			}
		}
	}
}