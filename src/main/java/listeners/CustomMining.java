package listeners;

import items.ingredients.mining.*;
import misc.PluginUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

import static misc.PluginUtils.sendRareDropMessage;

public class CustomMining implements Listener {
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		Location l = b.getLocation();
		World world = l.getWorld();
		ItemStack itemInHand = p.getInventory().getItemInMainHand();
		Random random = new Random();
		if(itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasEnchant(Enchantment.FORTUNE)) {
			boolean dropDouble = false;
			if(itemInHand.getItemMeta().hasLore()) {
				dropDouble = itemInHand.getItemMeta().getLore().getFirst().contains("skyblock/combat/divan_pickaxe");
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
						PluginUtils.damageItem(p, itemInHand, 1); // *technically* this isnt needed becuase only divan pick can drop double
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
					if(random.nextDouble() < 0.005 * fortuneMulti) {
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
						PluginUtils.damageItem(p, itemInHand, 1); // see comment in stone section
					}
					if(random.nextDouble() < 0.005 * fortuneMulti) {
						world.dropItemNaturally(l, RefinedNetherite.getItem());
						sendRareDropMessage(p, "Refined Netherite");
					}
				}
			}
		}
	}
}