package listeners;

import items.ingredients.mining.Alloy;
import items.ingredients.misc.EnchantmentUpgrader;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Random;

public class CustomChestLoot implements Listener {
	@EventHandler
	public void onLootGenerate(LootGenerateEvent e) {
		Random random = new Random();
		if(e.getInventoryHolder() instanceof Chest chest) {
			if(random.nextDouble() < 0.005) { // 0.5%
				chest.getBlockInventory().addItem(EnchantmentUpgrader.getItem());
			} else if(random.nextDouble() < 0.01) { // 0.5%
				ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
				meta.addStoredEnchant(Enchantment.LOOTING, 4, true);
				book.setItemMeta(meta);

				chest.getBlockInventory().addItem(book);
			} else if(random.nextDouble() < 0.015) { // 0.5%
				ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
				meta.addStoredEnchant(Enchantment.FORTUNE, 4, true);
				book.setItemMeta(meta);

				chest.getBlockInventory().addItem(book);
			} else if(random.nextDouble() < 0.025) { // 1%
				ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
				meta.addStoredEnchant(Enchantment.SHARPNESS, 6, true);
				book.setItemMeta(meta);

				chest.getBlockInventory().addItem(book);
			} else if(random.nextDouble() < 0.035) { // 1%
				ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
				meta.addStoredEnchant(Enchantment.POWER, 6, true);
				book.setItemMeta(meta);

				chest.getBlockInventory().addItem(book);
			} else if(random.nextDouble() < 0.05) { // 1.5%
				ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
				meta.addStoredEnchant(Enchantment.PROTECTION, 5, true);
				book.setItemMeta(meta);

				chest.getBlockInventory().addItem(book);
			} else if(random.nextDouble() < 0.06) { // 1%
				ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
				meta.addStoredEnchant(Enchantment.SWEEPING_EDGE, 4, true);
				book.setItemMeta(meta);

				chest.getBlockInventory().addItem(book);
			} else if(random.nextDouble() < 0.07) { // 1%
				ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
				meta.addStoredEnchant(Enchantment.SMITE, 6, true);
				book.setItemMeta(meta);

				chest.getBlockInventory().addItem(book);
			} else if(random.nextDouble() < 0.08) { // 1%
				ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
				meta.addStoredEnchant(Enchantment.BANE_OF_ARTHROPODS, 6, true);
				book.setItemMeta(meta);

				chest.getBlockInventory().addItem(book);
			}
		} else if(e.getInventoryHolder() instanceof StorageMinecart minecart) {
			if(random.nextDouble() < 0.02) {
				minecart.getInventory().addItem(Alloy.getItem());
			}
		}
	}
}