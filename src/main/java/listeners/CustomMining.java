package listeners;

import items.ingredients.mining.*;
import misc.Utils;
import mobs.hardmode.generic.VoidcrazedSeraph;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import misc.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static misc.Utils.sendRareDropMessage;

public class CustomMining implements Listener {
	// Players actively breaking a Voidgloom yang-glyph beacon: their target block + the scheduled defuse.
	private final Map<UUID, Block> beaconTarget = new HashMap<>();
	private final Map<UUID, BukkitTask> beaconTask = new HashMap<>();

	/**
	 * Voidgloom yang-glyph beacon: break (defuse) it on a fixed timer - 3s with a fist, 2s with the
	 * Divan's Pickaxe (33% faster) - instead of the slow vanilla beacon dig (a beacon isn't
	 * pickaxe-mineable, so vanilla tool tier doesn't speed it up). Defusing it removes the beacon so
	 * the boss never detonates it.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onBeaconDamage(BlockDamageEvent e) {
		Block b = e.getBlock();
		if(!VoidcrazedSeraph.isVoidgloomBeacon(b) || b.getType() != Material.BEACON) return;
		Player p = e.getPlayer();
		UUID id = p.getUniqueId();
		if(b.equals(beaconTarget.get(id))) return; // already breaking this beacon
		cancelBeacon(id);
		boolean divan = isDivanPickaxe(p.getInventory().getItemInMainHand());
		long ticks = divan ? 40L : 60L; // 2s with Divan's Pickaxe (33% faster), 3s otherwise
		beaconTarget.put(id, b);
		beaconTask.put(id, Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			beaconTarget.remove(id);
			beaconTask.remove(id);
			if(VoidcrazedSeraph.isVoidgloomBeacon(b) && b.getType() == Material.BEACON) {
				b.setType(Material.AIR); // defused before the boss can detonate it
				b.getWorld().playSound(b.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);
			}
		}, ticks));
	}

	@EventHandler
	public void onBeaconAbort(BlockDamageAbortEvent e) {
		cancelBeacon(e.getPlayer().getUniqueId());
	}

	private void cancelBeacon(UUID id) {
		BukkitTask t = beaconTask.remove(id);
		if(t != null) t.cancel();
		beaconTarget.remove(id);
	}

	private static boolean isDivanPickaxe(ItemStack item) {
		return item != null && item.hasItemMeta() && item.getItemMeta().hasLore()
				&& Utils.firstLorePlain(item.getItemMeta()).equals("skyblock/combat/divan_pickaxe");
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if(VoidcrazedSeraph.isVoidgloomBeacon(b)) {
			e.setDropItems(false);
			return;
		}
		Location l = b.getLocation();
		World world = l.getWorld();
		ItemStack itemInHand = p.getInventory().getItemInMainHand();
		Random random = new Random();
		if(itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasEnchant(Enchantment.FORTUNE)) {
			boolean dropDouble = false;
			if(itemInHand.getItemMeta().hasLore()) {
				dropDouble = Utils.firstLorePlain(itemInHand.getItemMeta()).contains("skyblock/combat/divan_pickaxe");
			}
			int fortune = itemInHand.getItemMeta().getEnchantLevel(Enchantment.FORTUNE);

			double fortuneMulti = 1 + 0.25 * fortune;
			ItemStack item;
			Location dropLocation = e.getBlock().getLocation().add(0.5, 0.5, 0.5);
			switch(b.getType()) {
				case STONE, DEEPSLATE -> {
					if(dropDouble) {
						e.setCancelled(true);
						if(b.getType().equals(Material.STONE)) {
							item = new ItemStack(Material.COBBLESTONE);
						} else {
							item = new ItemStack(Material.COBBLED_DEEPSLATE);
						}
						item.setAmount(2);
						world.dropItemNaturally(dropLocation, item);
						e.getBlock().getWorld().getBlockAt(dropLocation).setType(Material.AIR);
						Utils.damageItem(p, itemInHand, 1); // *technically* this isnt needed becuase only divan pick can drop double
					}
					if(random.nextDouble() < 0.001 * fortuneMulti) {
						world.dropItemNaturally(l, ConcentratedStone.getItem());
						sendRareDropMessage(p, "Concentrated Stone");
					}
				}
				case COAL_ORE, DEEPSLATE_COAL_ORE -> {
					if(dropDouble) {
						item = new ItemStack(Material.COAL);
						item.setAmount(random.nextInt(fortune + 1) + 1);
						world.dropItemNaturally(dropLocation, item);
					}
				}
				case COPPER_ORE, DEEPSLATE_COPPER_ORE -> {
					if(dropDouble) {
						item = new ItemStack(Material.RAW_COPPER);
						item.setAmount(random.nextInt((fortune + 1) * 4) + 2);
						world.dropItemNaturally(dropLocation, item);
					}
				}
				case LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> {
					if(dropDouble) {
						item = new ItemStack(Material.LAPIS_LAZULI);
						item.setAmount(random.nextInt((fortune + 1) * 6) + 4);
						world.dropItemNaturally(dropLocation, item);
					}
					if(random.nextDouble() < 0.0025 * fortuneMulti) {
						world.dropItemNaturally(l, RefinedLapis.getItem());
						sendRareDropMessage(p, "Refined Lapis");
					}
				}
				case IRON_ORE, DEEPSLATE_IRON_ORE -> {
					if(dropDouble) {
						item = new ItemStack(Material.RAW_IRON);
						item.setAmount(random.nextInt(fortune + 1) + 1);
						world.dropItemNaturally(dropLocation, item);
					}
					if(random.nextDouble() < 0.0025 * fortuneMulti) {
						world.dropItemNaturally(l, RefinedIron.getItem());
						sendRareDropMessage(p, "Refined Iron");
					}
				}
				case GOLD_ORE, DEEPSLATE_GOLD_ORE -> {
					if(dropDouble) {
						item = new ItemStack(Material.RAW_GOLD);
						item.setAmount(random.nextInt(fortune + 1) + 1);
						world.dropItemNaturally(dropLocation, item);
					}
					if(random.nextDouble() < 0.0025 * fortuneMulti) {
						world.dropItemNaturally(l, RefinedGold.getItem());
						sendRareDropMessage(p, "Refined Gold");
					}
				}
				case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE -> {
					if(dropDouble) {
						item = new ItemStack(Material.REDSTONE);
						item.setAmount(random.nextInt(fortune + 2) + 4);
						world.dropItemNaturally(dropLocation, item);
					}
					if(random.nextDouble() < 0.0025 * fortuneMulti) {
						world.dropItemNaturally(l, RefinedRedstone.getItem());
						sendRareDropMessage(p, "Refined Redstone");
					}
				}
				case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> {
					if(dropDouble) {
						item = new ItemStack(Material.DIAMOND);
						item.setAmount(random.nextInt(fortune + 1) + 1);
						world.dropItemNaturally(dropLocation, item);
					}
					if(random.nextDouble() < 0.005 * fortuneMulti) {
						world.dropItemNaturally(l, RefinedDiamond.getItem());
						sendRareDropMessage(p, "Refined Diamond");
					}
				}
				case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> {
					if(dropDouble) {
						item = new ItemStack(Material.EMERALD);
						item.setAmount(random.nextInt(fortune + 1) + 1);
						world.dropItemNaturally(dropLocation, item);
					}
					if(random.nextDouble() < 0.01 * fortuneMulti) {
						world.dropItemNaturally(l, RefinedEmerald.getItem());
						sendRareDropMessage(p, "Refined Emerald");
					}
				}
				case ANCIENT_DEBRIS -> {
					if(dropDouble) {
						e.setCancelled(true);
						item = new ItemStack(Material.NETHERITE_SCRAP);
						item.setAmount(item.getAmount() + 1);
						world.dropItemNaturally(dropLocation, item);
						e.getBlock().getWorld().getBlockAt(dropLocation).setType(Material.AIR);
						Utils.damageItem(p, itemInHand, 1); // see comment in stone section
					}
					if(random.nextDouble() < 0.01 * fortuneMulti) {
						world.dropItemNaturally(l, RefinedNetherite.getItem());
						sendRareDropMessage(p, "Refined Netherite");
					}
				}
			}
		}
	}
}