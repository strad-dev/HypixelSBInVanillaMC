package commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

@SuppressWarnings("DuplicateExpressions")
public class GetOPItems implements CommandExecutor {
	public Map<Enchantment, Integer> createEnchantMap() {
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.put(Enchantment.ARROW_DAMAGE, 255);
		enchantments.put(Enchantment.ARROW_FIRE, 255);
		enchantments.put(Enchantment.ARROW_INFINITE, 255);
		enchantments.put(Enchantment.ARROW_KNOCKBACK, 255);
		enchantments.put(Enchantment.CHANNELING, 255);
		enchantments.put(Enchantment.DAMAGE_ALL, 255);
		enchantments.put(Enchantment.DAMAGE_ARTHROPODS, 255);
		enchantments.put(Enchantment.DAMAGE_UNDEAD, 255);
		enchantments.put(Enchantment.DEPTH_STRIDER, 255);
		enchantments.put(Enchantment.DIG_SPEED, 255);
		enchantments.put(Enchantment.DURABILITY, 255);
		enchantments.put(Enchantment.FIRE_ASPECT, 255);
		enchantments.put(Enchantment.IMPALING, 255);
		enchantments.put(Enchantment.KNOCKBACK, 255);
		enchantments.put(Enchantment.LOOT_BONUS_BLOCKS, 255);
		enchantments.put(Enchantment.LOOT_BONUS_MOBS, 255);
		enchantments.put(Enchantment.LOYALTY, 255);
		enchantments.put(Enchantment.LUCK, 255);
		enchantments.put(Enchantment.LURE, 255);
		enchantments.put(Enchantment.MENDING, 255);
		enchantments.put(Enchantment.MULTISHOT, 255);
		enchantments.put(Enchantment.OXYGEN, 255);
		enchantments.put(Enchantment.PIERCING, 255);
		enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 255);
		enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 255);
		enchantments.put(Enchantment.PROTECTION_FALL, 255);
		enchantments.put(Enchantment.PROTECTION_FIRE, 255);
		enchantments.put(Enchantment.PROTECTION_PROJECTILE, 255);
		enchantments.put(Enchantment.QUICK_CHARGE, 5);
		enchantments.put(Enchantment.RIPTIDE, 255);
		enchantments.put(Enchantment.SILK_TOUCH, 255);
		enchantments.put(Enchantment.SOUL_SPEED, 255);
		enchantments.put(Enchantment.SWIFT_SNEAK, 2);
		enchantments.put(Enchantment.THORNS, 255);
		enchantments.put(Enchantment.WATER_WORKER, 255);
		return enchantments;
	}
	
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if(commandSender.isOp()) {
			if(commandSender instanceof Player player) {
				try {
					player = getServer().getPlayer(strings[1]);
				} catch(Exception exception) {
					//nothing here lol
				}
				if(player == null) {
					commandSender.sendMessage(ChatColor.RED + "Invalid player provided.");
					return false;
				}
				boolean hasEnchants = false;
				try {
					hasEnchants = Boolean.parseBoolean(strings[0]);
				} catch(Exception exception) {
					ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
					helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
					helmet.addEnchantment(Enchantment.DURABILITY, 3);
					helmet.addEnchantment(Enchantment.MENDING, 1);
					helmet.addEnchantment(Enchantment.THORNS, 3);
					helmet.addEnchantment(Enchantment.OXYGEN, 3);
					helmet.addEnchantment(Enchantment.WATER_WORKER, 1);

					// NECRON'S CHESTPLATE
					ItemStack chestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
					chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
					chestplate.addEnchantment(Enchantment.DURABILITY, 3);
					chestplate.addEnchantment(Enchantment.MENDING, 1);
					chestplate.addEnchantment(Enchantment.THORNS, 3);

					// NECRON'S LEGGINGS
					ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
					leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
					leggings.addEnchantment(Enchantment.DURABILITY, 3);
					leggings.addEnchantment(Enchantment.MENDING, 1);
					leggings.addEnchantment(Enchantment.THORNS, 3);
					leggings.addEnchantment(Enchantment.SWIFT_SNEAK, 3);

					// NECRON'S BOOTS
					ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
					boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
					boots.addEnchantment(Enchantment.DURABILITY, 3);
					boots.addEnchantment(Enchantment.MENDING, 1);
					boots.addEnchantment(Enchantment.THORNS, 3);
					boots.addEnchantment(Enchantment.DEPTH_STRIDER, 3);
					boots.addEnchantment(Enchantment.SOUL_SPEED, 3);
					boots.addEnchantment(Enchantment.PROTECTION_FALL, 4);

					// ELYTRA
					ItemStack elytra = new ItemStack(Material.ELYTRA);
					elytra.addEnchantment(Enchantment.DURABILITY, 3);
					elytra.addEnchantment(Enchantment.MENDING, 1);

					ItemMeta data = elytra.getItemMeta();
					assert data != null;
					data.setUnbreakable(true);
					data.setDisplayName(ChatColor.LIGHT_PURPLE + "Elytra " + ChatColor.RED + "✪✪✪✪✪");
					data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
					List<String> lore = new ArrayList<>();

					lore.add(ChatColor.GRAY + "Allows you to fly.  Does not give any stats.");
					lore.add(ChatColor.GRAY + "Cannot be reforged.  Have fun cheesing!");
					lore.add("");
					lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC DUNGEON ELYTRA " + ChatColor.MAGIC + "a");
					data.setLore(lore);
					elytra.setItemMeta(data);

					// SCYLLA
					ItemStack scylla = new ItemStack(Material.NETHERITE_SWORD);
					scylla.addEnchantment(Enchantment.DAMAGE_ALL, 5);
					scylla.addEnchantment(Enchantment.DURABILITY, 3);
					scylla.addEnchantment(Enchantment.MENDING, 1);
					scylla.addEnchantment(Enchantment.FIRE_ASPECT, 2);
					scylla.addEnchantment(Enchantment.LOOT_BONUS_MOBS, 3);

					// TERMINATOR
					ItemStack term = new ItemStack(Material.BOW);
					term.addEnchantment(Enchantment.ARROW_DAMAGE, 5);
					term.addEnchantment(Enchantment.DURABILITY, 3);
					term.addEnchantment(Enchantment.MENDING, 1);
					term.addEnchantment(Enchantment.ARROW_FIRE, 1);

					// ASPECT OF THE VOID
					ItemStack aotv = new ItemStack(Material.NETHERITE_SHOVEL);
					aotv.addEnchantment(Enchantment.DIG_SPEED, 5);
					aotv.addEnchantment(Enchantment.DURABILITY, 3);
					aotv.addEnchantment(Enchantment.MENDING, 1);
					aotv.addEnchantment(Enchantment.SILK_TOUCH, 1);

					data = aotv.getItemMeta();
					assert data != null;
					data.setUnbreakable(true);
					data.setDisplayName(ChatColor.LIGHT_PURPLE + "Warped Aspect of the Void " + ChatColor.RED + "✪✪✪✪✪");
					AttributeModifier attackDamage = new AttributeModifier("AOTVModifier", 0, AttributeModifier.Operation.ADD_NUMBER);
					data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);
					data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
					lore = new ArrayList<>();

					lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "0");
					lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+569.6" + ChatColor.BLUE + " (+65)" + ChatColor.LIGHT_PURPLE + " (+24)");
					lore.add(ChatColor.GOLD + "[" + ChatColor.AQUA + "✎" + ChatColor.GOLD + "]");
					lore.add("");
					lore.add(ChatColor.GOLD + "Ability: Instant Transmission " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
					lore.add(ChatColor.GRAY + "Teleport " + ChatColor.GREEN + "8 blocks" + ChatColor.GRAY + " ahead of you.");
					lore.add(ChatColor.DARK_GRAY + "Mana Cost: 0");
					lore.add("");
					lore.add(ChatColor.GOLD + "Ability: Ether Transmission " + ChatColor.GREEN + ChatColor.BOLD + "SNEAK RIGHT CLICK");
					lore.add(ChatColor.GRAY + "Teleport to your targetted block");
					lore.add(ChatColor.GRAY + "up to " + ChatColor.GREEN + "61 blocks" + ChatColor.GRAY + " blocks away.");
					lore.add(ChatColor.DARK_GRAY + "Soulflow Cost: " + ChatColor.DARK_AQUA + "0⸎");
					lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + "0");
					lore.add("");
					lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " LEGENDARY DUNGEON SHOVEL " + ChatColor.MAGIC + "a");

					data.setLore(lore);
					aotv.setItemMeta(data);

					player.getInventory().addItem(helmet, chestplate, leggings, boots, elytra, scylla, term, aotv);
					commandSender.sendMessage("Successfully gave " + player.getName() + " Vanilla Items");
					return true;
				}
				
				Map<Enchantment, Integer> enchantments;
				if(hasEnchants) {
					enchantments = createEnchantMap();
				} else {
					enchantments = new HashMap<>();
				}
				
				// NECRON'S HELMET
				ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
				helmet.addUnsafeEnchantments(enchantments);
				
				ItemMeta data = helmet.getItemMeta();
				assert data != null;
				data.setUnbreakable(true);
				data.setDisplayName(ChatColor.LIGHT_PURPLE + "Ancient Necron's Helmet " + ChatColor.RED + "✪✪✪✪✪");
				data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
				List<String> lore = new ArrayList<>();
				
				lore.add(ChatColor.GRAY + "Strength: " + ChatColor.RED + "+684.8 " + ChatColor.BLUE + "(+35) " + ChatColor.LIGHT_PURPLE + "(+32)");
				lore.add(ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "+96% " + ChatColor.BLUE + "(+15%)");
				lore.add(ChatColor.GRAY + "Crit Damage: " + ChatColor.RED + "+512% " + ChatColor.BLUE + "(+50%)");
				lore.add(ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+2764.8 " + ChatColor.YELLOW + "(+60) " + ChatColor.RED + "[+40] " + ChatColor.BLUE + "(+7)");
				lore.add(ChatColor.GRAY + "Defense: " + ChatColor.GREEN + "+1011.2 " + ChatColor.YELLOW + "(+30) " + ChatColor.BLUE + "(+7)");
				lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+512 " + ChatColor.BLUE + "(+25)");
				lore.add(ChatColor.GRAY + "Health Regen: " + ChatColor.GREEN + "+64");
				lore.add(ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "❁" + ChatColor.GOLD + "] [" + ChatColor.LIGHT_PURPLE + "⚔" + ChatColor.GOLD + "]");
				lore.add("");
				lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + "Legion V" + ChatColor.RESET + ChatColor.GRAY + ", " + ChatColor.GOLD + "Aqua Affinity I" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Big Brain V" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Ferocious Mana X" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Growth VII" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Hecatomb X" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Protection VII" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Rejuvenate V" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Respiration III" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Thorns III");
				lore.add("");
				lore.add(ChatColor.GRAY + "Reduces the damage you take from");
				lore.add(ChatColor.GRAY + "withers by " + ChatColor.RED + "10%" + ChatColor.GRAY + ".");
				lore.add("");
				lore.add(ChatColor.GOLD + "Full Set Bonus: Witherborn");
				lore.add(ChatColor.GRAY + "Spans a wither minion every");
				lore.add(ChatColor.YELLOW + "30" + ChatColor.GRAY + " seconds up to a maximum");
				lore.add(ChatColor.GREEN + "1" + ChatColor.GRAY + " wither.  Your withers will");
				lore.add(ChatColor.GRAY + "travel to and explode on nearby");
				lore.add(ChatColor.GRAY + "enemies.");
				lore.add("");
				lore.add(ChatColor.BLUE + "Ancient Bonus");
				lore.add(ChatColor.GRAY + "Grants " + ChatColor.GREEN + "+1" + ChatColor.BLUE + " ☠ Crit Damage");
				lore.add(ChatColor.GRAY + "per " + ChatColor.RED + "Catacombs" + ChatColor.GRAY + " level.");
				lore.add("");
				lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC DUNGEON HELMET " + ChatColor.MAGIC + "a");
				
				data.setLore(lore);
				helmet.setItemMeta(data);
				
				// NECRON'S CHESTPLATE
				ItemStack chestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
				chestplate.addUnsafeEnchantments(enchantments);
				
				data = chestplate.getItemMeta();
				assert data != null;
				data.setUnbreakable(true);
				data.setDisplayName(ChatColor.LIGHT_PURPLE + "Ancient Necron's Chestplate " + ChatColor.RED + "✪✪✪✪✪");
				data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
				lore = new ArrayList<>();
				
				lore.add(ChatColor.GRAY + "Strength: " + ChatColor.RED + "+684.8 " + ChatColor.BLUE + "(+35) " + ChatColor.LIGHT_PURPLE + "(+32)");
				lore.add(ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "+96% " + ChatColor.BLUE + "(+15%)");
				lore.add(ChatColor.GRAY + "Crit Damage: " + ChatColor.RED + "+512% " + ChatColor.BLUE + "(+50%)");
				lore.add(ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+3020.8 " + ChatColor.YELLOW + "(+60) " + ChatColor.RED + "[+40] " + ChatColor.BLUE + "(+7)");
				lore.add(ChatColor.GRAY + "Defense: " + ChatColor.GREEN + "+1267.2 " + ChatColor.YELLOW + "(+30) " + ChatColor.BLUE + "(+7)");
				lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+224 " + ChatColor.BLUE + "(+25)");
				lore.add(ChatColor.GRAY + "True Defense: " + ChatColor.GREEN + "+32");
				lore.add(ChatColor.GRAY + "Health Regen: " + ChatColor.GREEN + "+64");
				lore.add(ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "❁" + ChatColor.GOLD + "] [" + ChatColor.LIGHT_PURPLE + "⚔" + ChatColor.GOLD + "]");
				lore.add("");
				lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + "Legion V" + ChatColor.RESET + ChatColor.GRAY + ", " + ChatColor.GOLD + "Counter-Strike V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Ferocious Mana X" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Growth VII" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Protection VII" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Reflection V" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Rejuvenate V" + ChatColor.GRAY + "," + ChatColor.GOLD + "True Protection I");
				lore.add("");
				lore.add(ChatColor.GRAY + "Reduces the damage you take from");
				lore.add(ChatColor.GRAY + "withers by " + ChatColor.RED + "10%" + ChatColor.GRAY + ".");
				lore.add("");
				lore.add(ChatColor.GOLD + "Full Set Bonus: Witherborn");
				lore.add(ChatColor.GRAY + "Spans a wither minion every");
				lore.add(ChatColor.YELLOW + "30" + ChatColor.GRAY + " seconds up to a maximum");
				lore.add(ChatColor.GREEN + "1" + ChatColor.GRAY + " wither.  Your withers will");
				lore.add(ChatColor.GRAY + "travel to and explode on nearby");
				lore.add(ChatColor.GRAY + "enemies.");
				lore.add("");
				lore.add(ChatColor.BLUE + "Ancient Bonus");
				lore.add(ChatColor.GRAY + "Grants " + ChatColor.GREEN + "+1" + ChatColor.BLUE + " ☠ Crit Damage");
				lore.add(ChatColor.GRAY + "per " + ChatColor.RED + "Catacombs" + ChatColor.GRAY + " level.");
				lore.add("");
				lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC DUNGEON CHESTPLATE " + ChatColor.MAGIC + "a");
				
				data.setLore(lore);
				chestplate.setItemMeta(data);
				
				// NECRON'S LEGGINGS
				ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
				leggings.addUnsafeEnchantments(enchantments);
				
				data = leggings.getItemMeta();
				assert data != null;
				data.setUnbreakable(true);
				data.setDisplayName(ChatColor.LIGHT_PURPLE + "Ancient Necron's Leggings " + ChatColor.RED + "✪✪✪✪✪");
				data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
				lore = new ArrayList<>();
				
				lore.add(ChatColor.GRAY + "Strength: " + ChatColor.RED + "+684.8 " + ChatColor.BLUE + "(+35) " + ChatColor.LIGHT_PURPLE + "(+32)");
				lore.add(ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "+96% " + ChatColor.BLUE + "(+15%)");
				lore.add(ChatColor.GRAY + "Crit Damage: " + ChatColor.RED + "+512% " + ChatColor.BLUE + "(+50%)");
				lore.add(ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+2828.8 " + ChatColor.YELLOW + "(+60) " + ChatColor.RED + "[+40] " + ChatColor.BLUE + "(+7)");
				lore.add(ChatColor.GRAY + "Defense: " + ChatColor.GREEN + "+1171.2 " + ChatColor.YELLOW + "(+30) " + ChatColor.BLUE + "(+7)");
				lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+384 " + ChatColor.BLUE + "(+25)");
				lore.add(ChatColor.GRAY + "Health Regen: " + ChatColor.GREEN + "+64");
				lore.add(ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "❁" + ChatColor.GOLD + "] [" + ChatColor.LIGHT_PURPLE + "⚔" + ChatColor.GOLD + "]");
				lore.add("");
				lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + "Legion V" + ChatColor.RESET + ChatColor.GRAY + ", " + ChatColor.GOLD + "Ferocious Mana X" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Growth VII" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Protection VII" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Rejuvenate V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Smarty Pants V" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Thorns III");
				lore.add("");
				lore.add(ChatColor.GRAY + "Reduces the damage you take from");
				lore.add(ChatColor.GRAY + "withers by " + ChatColor.RED + "10%" + ChatColor.GRAY + ".");
				lore.add("");
				lore.add(ChatColor.GOLD + "Full Set Bonus: Witherborn");
				lore.add(ChatColor.GRAY + "Spans a wither minion every");
				lore.add(ChatColor.YELLOW + "30" + ChatColor.GRAY + " seconds up to a maximum");
				lore.add(ChatColor.GREEN + "1" + ChatColor.GRAY + " wither.  Your withers will");
				lore.add(ChatColor.GRAY + "travel to and explode on nearby");
				lore.add(ChatColor.GRAY + "enemies.");
				lore.add("");
				lore.add(ChatColor.BLUE + "Ancient Bonus");
				lore.add(ChatColor.GRAY + "Grants " + ChatColor.GREEN + "+1" + ChatColor.BLUE + " ☠ Crit Damage");
				lore.add(ChatColor.GRAY + "per " + ChatColor.RED + "Catacombs" + ChatColor.GRAY + " level.");
				lore.add("");
				lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC DUNGEON LEGGINGS " + ChatColor.MAGIC + "a");
				
				data.setLore(lore);
				leggings.setItemMeta(data);
				
				// NECRON'S BOOTS
				ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
				boots.addUnsafeEnchantments(enchantments);
				
				data = boots.getItemMeta();
				assert data != null;
				data.setUnbreakable(true);
				data.setDisplayName(ChatColor.LIGHT_PURPLE + "Ancient Necron's Boots " + ChatColor.RED + "✪✪✪✪✪");
				data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
				lore = new ArrayList<>();
				
				lore.add(ChatColor.GRAY + "Strength: " + ChatColor.RED + "+684.8 " + ChatColor.BLUE + "(+35) " + ChatColor.LIGHT_PURPLE + "(+32)");
				lore.add(ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "+96% " + ChatColor.BLUE + "(+15%)");
				lore.add(ChatColor.GRAY + "Crit Damage: " + ChatColor.RED + "+512% " + ChatColor.BLUE + "(+50%)");
				lore.add(ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+2284.8 " + ChatColor.YELLOW + "(+60) " + ChatColor.RED + "[+40] " + ChatColor.BLUE + "(+7)");
				lore.add(ChatColor.GRAY + "Defense: " + ChatColor.GREEN + "+915.2 " + ChatColor.YELLOW + "(+30) " + ChatColor.BLUE + "(+7)");
				lore.add(ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+38.4");
				lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+224 " + ChatColor.BLUE + "(+25)");
				lore.add(ChatColor.GRAY + "Health Regen: " + ChatColor.GREEN + "+64");
				lore.add(ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "❁" + ChatColor.GOLD + "] [" + ChatColor.LIGHT_PURPLE + "⚔" + ChatColor.GOLD + "]");
				lore.add("");
				lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + "Legion V" + ChatColor.RESET + ChatColor.GRAY + ", " + ChatColor.GOLD + "Depth Strider III" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Feather Falling X" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Ferocious Mana X" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Growth VII" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Protection VII" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Rejuvenate V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Sugar Rush III" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Thorns III");
				lore.add("");
				lore.add(ChatColor.GRAY + "Reduces the damage you take from");
				lore.add(ChatColor.GRAY + "withers by " + ChatColor.RED + "10%" + ChatColor.GRAY + ".");
				lore.add("");
				lore.add(ChatColor.GOLD + "Full Set Bonus: Witherborn");
				lore.add(ChatColor.GRAY + "Spans a wither minion every");
				lore.add(ChatColor.YELLOW + "30" + ChatColor.GRAY + " seconds up to a maximum");
				lore.add(ChatColor.GREEN + "1" + ChatColor.GRAY + " wither.  Your withers will");
				lore.add(ChatColor.GRAY + "travel to and explode on nearby");
				lore.add(ChatColor.GRAY + "enemies.");
				lore.add("");
				lore.add(ChatColor.BLUE + "Ancient Bonus");
				lore.add(ChatColor.GRAY + "Grants " + ChatColor.GREEN + "+1" + ChatColor.BLUE + " ☠ Crit Damage");
				lore.add(ChatColor.GRAY + "per " + ChatColor.RED + "Catacombs" + ChatColor.GRAY + " level.");
				lore.add("");
				lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC DUNGEON BOOTS " + ChatColor.MAGIC + "a");
				
				data.setLore(lore);
				boots.setItemMeta(data);
				
				// ELYTRA
				ItemStack elytra = new ItemStack(Material.ELYTRA);
				elytra.addUnsafeEnchantments(enchantments);
				
				data = elytra.getItemMeta();
				assert data != null;
				data.setUnbreakable(true);
				data.setDisplayName(ChatColor.LIGHT_PURPLE + "Elytra " + ChatColor.RED + "✪✪✪✪✪");
				data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
				lore = new ArrayList<>();
				
				lore.add(ChatColor.GRAY + "Allows you to fly.  Does not give any stats.");
				lore.add(ChatColor.GRAY + "Cannot be reforged.  Have fun cheesing!");
				lore.add("");
				lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC DUNGEON ELYTRA " + ChatColor.MAGIC + "a");
				
				data.setLore(lore);
				elytra.setItemMeta(data);
				
				// SCYLLA
				ItemStack scylla = new ItemStack(Material.NETHERITE_SWORD);
				scylla.addUnsafeEnchantments(enchantments);
				
				data = scylla.getItemMeta();
				assert data != null;
				data.setUnbreakable(true);
				data.setDisplayName(ChatColor.LIGHT_PURPLE + "Suspicious Scylla " + ChatColor.RED + "✪✪✪✪✪");
				AttributeModifier attackSpeed = new AttributeModifier("scyllaModifier", 100, AttributeModifier.Operation.ADD_NUMBER);
				data.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeed);
				data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
				lore = new ArrayList<>();
				
				lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+2355.2 " + ChatColor.YELLOW + "(+30) " + ChatColor.BLUE + "(+15)");
				lore.add(ChatColor.GRAY + "Strength: " + ChatColor.RED + "+1388.8 " + ChatColor.YELLOW + "(+30) " + ChatColor.GOLD + "[+5] " + ChatColor.LIGHT_PURPLE + "(+32)");
				lore.add(ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "+140.8% " + ChatColor.BLUE + "(+10%)");
				lore.add(ChatColor.GRAY + "Crit Damage: " + ChatColor.RED + "+1888% " + ChatColor.BLUE + "(+110%)");
				lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+320");
				lore.add(ChatColor.GRAY + "Magic Find: " + ChatColor.GREEN + "+38.4");
				lore.add(ChatColor.GRAY + "Ferocity: " + ChatColor.GREEN + "+224");
				lore.add(ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "⚔" + ChatColor.GOLD + "] [" + ChatColor.LIGHT_PURPLE + "⚔" + ChatColor.GOLD + "]");
				lore.add("");
				lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + "Chimera V" + ChatColor.RESET + ChatColor.GRAY + ", " + ChatColor.GOLD + "Champion X" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Cleave VI" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Critical VII" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Cubism VI" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Divine Gift III" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Dragon Hunter V" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Ender Slayer VII" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Execute VI" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Experience V" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Fire Aspect III" + ChatColor.GRAY + ", " + ChatColor.GOLD + "First Strike V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Giant Killer VII" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Impaling III" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Lethality VI" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Looting V" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Luck VII" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Scavenger V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Sharpness VII" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Smoldering V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Syphon V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Tabasco III" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Thunderlord VII" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Vampirism VI" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Venomous VI" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Vicious V");
				lore.add("");
				lore.add(ChatColor.GRAY + "Deals " + ChatColor.RED + "+50%" + ChatColor.GRAY + " damage to");
				lore.add(ChatColor.GRAY + "Withers.  Grants " + ChatColor.RED + "+1 ❁ Damage");
				lore.add(ChatColor.GRAY + "and " + ChatColor.GREEN + "+1 " + ChatColor.BLUE + " ☠ Crit Damage" + ChatColor.GRAY + " per");
				lore.add(ChatColor.RED + "Catacombs" + ChatColor.GRAY + " level.");
				lore.add("");
				lore.add(ChatColor.GREEN + "Scroll Abilities:");
				lore.add(ChatColor.GOLD + "Ability: Wither Impact " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
				lore.add(ChatColor.GRAY + "Teleport " + ChatColor.GREEN + "10 blocks" + ChatColor.GRAY + " ahead of");
				lore.add(ChatColor.GRAY + "you.  Then implode, dealing");
				lore.add(ChatColor.RED + "2147483647" + ChatColor.GRAY + " damage to nearby");
				lore.add(ChatColor.GRAY + "enemies.  Also applies the Wither");
				lore.add(ChatColor.GRAY + "Shield Scroll Ability, reducing");
				lore.add(ChatColor.GRAY + "damage taken and granting an");
				lore.add(ChatColor.GRAY + "absorption shield for " + ChatColor.YELLOW + "5");
				lore.add(ChatColor.GRAY + "seconds.");
				lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + "0");
				lore.add("");
				lore.add(ChatColor.BLUE + "Suspicious Bonus");
				lore.add(ChatColor.GRAY + "Increases weapon damage by");
				lore.add(ChatColor.RED + "+15" + ChatColor.GRAY + ".");
				lore.add("");
				lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC DUNGEON SWORD " + ChatColor.MAGIC + "a");
				
				data.setLore(lore);
				scylla.setItemMeta(data);
				
				// TERMINATOR
				ItemStack term = new ItemStack(Material.BOW);
				term.addUnsafeEnchantments(enchantments);
				
				data = term.getItemMeta();
				assert data != null;
				data.setUnbreakable(true);
				data.setDisplayName(ChatColor.LIGHT_PURPLE + "Precise Terminator " + ChatColor.RED + "✪✪✪✪✪");
				data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
				lore = new ArrayList<>();
				
				lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+2195.2 " + ChatColor.YELLOW + "(+30)");
				lore.add(ChatColor.GRAY + "Strength: " + ChatColor.RED + "+761.6 " + ChatColor.YELLOW + "(+30) " + ChatColor.GOLD + "[+5] " + ChatColor.BLUE + "(+34)");
				lore.add(ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "+128% " + ChatColor.BLUE + "(+15%)");
				lore.add(ChatColor.GRAY + "Crit Damage: " + ChatColor.RED + "+2080% " + ChatColor.BLUE + "(+70%)");
				lore.add(ChatColor.GRAY + "Bonus Attack Speed: " + ChatColor.RED + "+256%");
				lore.add(ChatColor.GRAY + "Magic Find: " + ChatColor.GREEN + "+38.4");
				lore.add(ChatColor.GRAY + "Ferocity: " + ChatColor.GREEN + "+32");
				lore.add(ChatColor.GRAY + "Shot Cooldown: " + ChatColor.GREEN + "0.0s");
				lore.add("");
				lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + "Fatal Tempo V" + ChatColor.RESET + ChatColor.GRAY + ", " + ChatColor.GOLD + "Chance V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Cubism VI" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Divine Gift III" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Dragon Hunter V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Dragon Tracer V" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Flame II" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Impaling III" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Infinite Quiver X" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Overload V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Piercing I" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Power VII" + ChatColor.GRAY + ",");
				lore.add(ChatColor.GOLD + "Smoldering V" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Snipe IV" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Vicious V");
				lore.add("");
				lore.add(ChatColor.GOLD + "Shortbow: Instantly Shoots!");
				lore.add(ChatColor.GRAY + "Shoots " + ChatColor.AQUA + "3" + ChatColor.GRAY + " arrows at once.");
				lore.add(ChatColor.GRAY + "Can damage Endermen");
				lore.add("");
				lore.add(ChatColor.RED + "Divides your " + ChatColor.BLUE + "☣ Crit Chance" + ChatColor.RED + " by 4!");
				lore.add("");
				lore.add(ChatColor.GOLD + "Ability: Salvation " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
				lore.add(ChatColor.GRAY + "Can be casted after landing " + ChatColor.GOLD + "3" + ChatColor.GRAY + " hits.");
				lore.add(ChatColor.GRAY + "Shoot a beam, penetrating up");
				lore.add(ChatColor.GRAY + "to " + ChatColor.YELLOW + "5" + ChatColor.GRAY + " foes and dealing " + ChatColor.RED + "2x");
				lore.add(ChatColor.GRAY + "the damage an arrow would.");
				lore.add(ChatColor.GRAY + "The beam always crits.");
				lore.add(ChatColor.DARK_GRAY + "Soulflow Cost: " + ChatColor.DARK_AQUA + "0⸎");
				lore.add(ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + "0s");
				lore.add("");
				lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC DUNGEON BOW " + ChatColor.MAGIC + "a");
				
				data.setLore(lore);
				term.setItemMeta(data);
				
				// ASPECT OF THE VOID
				ItemStack aotv = new ItemStack(Material.NETHERITE_SHOVEL);
				aotv.addUnsafeEnchantments(enchantments);
				
				data = aotv.getItemMeta();
				assert data != null;
				data.setUnbreakable(true);
				data.setDisplayName(ChatColor.LIGHT_PURPLE + "Warped Aspect of the Void " + ChatColor.RED + "✪✪✪✪✪");
				AttributeModifier attackDamage = new AttributeModifier("AOTVModifier", 0, AttributeModifier.Operation.ADD_NUMBER);
				data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);
				data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
				lore = new ArrayList<>();
				
				lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "0");
				lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+569.6" + ChatColor.BLUE + " (+65)" + ChatColor.LIGHT_PURPLE + " (+24)");
				lore.add(ChatColor.GOLD + "[" + ChatColor.AQUA + "✎" + ChatColor.GOLD + "]");
				lore.add("");
				lore.add(ChatColor.GOLD + "Ability: Instant Transmission " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
				lore.add(ChatColor.GRAY + "Teleport " + ChatColor.GREEN + "8 blocks" + ChatColor.GRAY + " ahead of you.");
				lore.add(ChatColor.DARK_GRAY + "Mana Cost: 0");
				lore.add("");
				lore.add(ChatColor.GOLD + "Ability: Ether Transmission " + ChatColor.GREEN + ChatColor.BOLD + "SNEAK RIGHT CLICK");
				lore.add(ChatColor.GRAY + "Teleport to your targetted block");
				lore.add(ChatColor.GRAY + "up to " + ChatColor.GREEN + "61 blocks" + ChatColor.GRAY + " blocks away.");
				lore.add(ChatColor.DARK_GRAY + "Soulflow Cost: " + ChatColor.DARK_AQUA + "0⸎");
				lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + "0");
				lore.add("");
				lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " LEGENDARY DUNGEON SHOVEL " + ChatColor.MAGIC + "a");
				
				data.setLore(lore);
				aotv.setItemMeta(data);
				
				player.getInventory().addItem(helmet, chestplate, leggings, boots, elytra, scylla, term, aotv);
				commandSender.sendMessage("Successfully gave " + player.getName() + " OP Items");
			}
		}
		return true;
	}
}
