package listeners;

import items.ingredients.mining.*;
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

public class CustomMining implements Listener {
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		Location l = b.getLocation();
		World world = l.getWorld();
		ItemStack itemInHand = p.getInventory().getItemInMainHand();
		Random random = new Random();
		if(itemInHand.getItemMeta().hasEnchant(Enchantment.FORTUNE)) {
			int fortune = itemInHand.getItemMeta().getEnchantLevel(Enchantment.FORTUNE);
			double fortuneMulti = 1 + 0.25 * fortune;
			ItemStack item;
			switch(b.getType()) {
				case STONE, DEEPSLATE -> {
					if(fortune == 4) {
						if(b.getType().equals(Material.STONE)) {
							item = new ItemStack(Material.COBBLESTONE);
						} else {
							item = new ItemStack(Material.COBBLED_DEEPSLATE);
						}
						world.dropItemNaturally(l, item);
					}
					if(random.nextDouble() < 0.001 * fortuneMulti) {
						world.dropItemNaturally(l, ConcentratedStone.getItem());
						CustomDrops.sendRareDropMessage(p, "Concentrated Stone");
					}
				}
				case LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> {
					if(random.nextDouble() < 0.0025 * fortuneMulti) {
						world.dropItemNaturally(l, RefinedLapis.getItem());
						CustomDrops.sendRareDropMessage(p, "Refined Lapis");
					}
				}
				case IRON_ORE, DEEPSLATE_IRON_ORE -> {
					if(random.nextDouble() < 0.0025 * fortuneMulti) {
						world.dropItemNaturally(l, RefinedIron.getItem());
						CustomDrops.sendRareDropMessage(p, "Refined Iron");
					}
				}
				case GOLD_ORE, DEEPSLATE_GOLD_ORE -> {
					if(random.nextDouble() < 0.0025 * fortuneMulti) {
						world.dropItemNaturally(l, RefinedGold.getItem());
						CustomDrops.sendRareDropMessage(p, "Refined Gold");
					}
				}
				case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE -> {
					if(random.nextDouble() < 0.0025 * fortuneMulti) {
						world.dropItemNaturally(l, RefinedIron.getItem());
						CustomDrops.sendRareDropMessage(p, "Refined Redstone");
					}
				}
				case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> {
					if(random.nextDouble() < 0.005 * fortuneMulti) {
						world.dropItemNaturally(l, RefinedDiamond.getItem());
						CustomDrops.sendRareDropMessage(p, "Refined Diamond");
					}
				}
				case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> {
					if(random.nextDouble() < 0.005 * fortuneMulti) {
						world.dropItemNaturally(l, RefinedEmerald.getItem());
						CustomDrops.sendRareDropMessage(p, "Refined Emerald");
					}
				}
				case ANCIENT_DEBRIS -> {
					if(fortune == 4) {
						item = new ItemStack(Material.ANCIENT_DEBRIS);
						world.dropItemNaturally(l, item);
					}
					if(random.nextDouble() < 0.005 * fortuneMulti) {
						world.dropItemNaturally(l, RefinedNetherite.getItem());
						CustomDrops.sendRareDropMessage(p, "Refined Netherite");
					}
				}
			}
		}
	}
}