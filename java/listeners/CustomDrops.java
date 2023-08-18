package listeners;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import misc.Plugin;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("ExtractMethodRecommender")
public class CustomDrops implements Listener {
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		Random random = new Random();
		try {
			if(e.getEntity() instanceof Wither wither) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=wither_skull]");
				boolean drop = random.nextDouble() > 0.98;

				// SHADOW WARP
				if(Objects.requireNonNull(wither.getCustomName()).contains("Maxor")) {
					if(drop) {
						ItemStack shadowWarp = new ItemStack(Material.ENCHANTED_BOOK);
						ItemMeta data = shadowWarp.getItemMeta();
						assert data != null;
						data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Shadow Warp");
						List<String> lore = new ArrayList<>();
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A rare Enchanted Book imbued");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "with the power of Maxor.");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Grants the ability to teleport");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "10 blocks using Wither Impact.");
						lore.add("");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Combine with the other two");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Wither Scrolls and Necron's");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Handle to create the Scylla!");

						data.setLore(lore);
						shadowWarp.setItemMeta(data);
						wither.getWorld().dropItem(wither.getLocation(), shadowWarp);

						Player p = Plugin.getNearestPlayer(wither);
						assert p != null;
						p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Shadow Warp");
						p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
					}

					// IMPLOSION
				} else if(Objects.requireNonNull(wither.getCustomName()).contains("Storm")) {
					wither.getServer().dispatchCommand(Bukkit.getConsoleSender(), "weather clear");
					if(drop) {
						ItemStack implosion = new ItemStack(Material.ENCHANTED_BOOK);
						ItemMeta data = implosion.getItemMeta();
						assert data != null;
						data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Implosion");
						List<String> lore = new ArrayList<>();
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A rare Enchanted Book imbued");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "with the power of Storm.");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Grants the ability to implode");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "and damage all enemies within");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "10 blocks.");
						lore.add("");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Combine with the other two");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Wither Scrolls and Necron's");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Handle to create the Scylla!");
						data.setLore(lore);
						implosion.setItemMeta(data);
						wither.getWorld().dropItem(wither.getLocation(), implosion);

						Player p = Plugin.getNearestPlayer(wither);
						assert p != null;
						p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Implosion");
						p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
					}

					// WITHER SHIELD
				} else if(Objects.requireNonNull(wither.getCustomName()).contains("Goldor")) {
					if(drop) {
						ItemStack witherShield = new ItemStack(Material.ENCHANTED_BOOK);
						ItemMeta data = witherShield.getItemMeta();
						assert data != null;
						data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Wither Shield");
						List<String> lore = new ArrayList<>();
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A rare Enchanted Book imbued");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "with the power of Goldor.");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Grants the ability to summon");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "6 extra hearts of health,");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "gain Regeneration II, and");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "take 20% less damage for 5 seconds.");
						lore.add("");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Combine with the other two");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Wither Scrolls and Necron's");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Handle to create the Scylla!");
						data.setLore(lore);
						witherShield.setItemMeta(data);
						wither.getWorld().dropItem(wither.getLocation(), witherShield);

						Player p = Plugin.getNearestPlayer(wither);
						assert p != null;
						p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Wither Shield");
						p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
					}

					// NECRON'S HANDLE
				} else if(Objects.requireNonNull(wither.getCustomName()).contains("Necron")) {
					if(drop) {
						ItemStack handle = new ItemStack(Material.STICK);
						ItemMeta data = handle.getItemMeta();
						assert data != null;
						data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Necron's Handle");
						List<String> lore = new ArrayList<>();
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "The hilt of the GREATEST sword");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "to ever exist, imbued with the");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "power of Necron.");
						lore.add("");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use this with a Netherite Sword");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "and the other three Wither");
						lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Scrolls to craft the Scylla!");
						lore.add(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "WARNING: REMOVES ALL ENCHANTMENTS");
						data.setLore(lore);
						data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
						handle.setItemMeta(data);
						handle.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
						wither.getWorld().dropItem(wither.getLocation(), handle);

						Player p = Plugin.getNearestPlayer(wither);
						assert p != null;
						p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Necron's Handle");
						p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
					}
				}


				// SPAWN A VOIDGLOOM SERAPH
			} else if(e.getEntity() instanceof EnderDragon dragon) {
				if(Objects.requireNonNull(dragon.getCustomName()).contains("Superior Dragon")) {
					spawnVoidgloomSeraph(e.getEntity());
				}

				// JUDGEMENT CORE
			} else if(e.getEntity() instanceof Enderman enderman) {
				if(Objects.requireNonNull(enderman.getCustomName()).contains("Voidgloom Seraph") && random.nextDouble() > 0.875) {
					ItemStack core = new ItemStack(Material.CHISELED_QUARTZ_BLOCK);
					ItemMeta data = core.getItemMeta();
					assert data != null;
					data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Judgement Core");
					List<String> lore = new ArrayList<>();
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A core so powerful that even");
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the most dedicated players");
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "tremble at it's power.");
					lore.add("");
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use this to craft the Terminator!");
					data.setLore(lore);
					data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
					core.setItemMeta(data);
					core.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					enderman.getWorld().dropItem(enderman.getLocation(), core);

					Player p = Plugin.getNearestPlayer(enderman);
					assert p != null;
					p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Judgement Core");
					p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);

					// TESSELLATED ENDER PEARL
				} else if(enderman.getCustomName().contains("Mutant Enderman") && random.nextDouble() > 0.9) {
					ItemStack tessellated = new ItemStack(Material.ENDER_PEARL);
					ItemMeta data = tessellated.getItemMeta();
					assert data != null;
					data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Tessellated Ender Pearl");
					List<String> lore = new ArrayList<>();
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "An Ender Pearl so dense that even");
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the most knowledgeable players");
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "are mystified by it.");
					lore.add("");
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use 2 of this to craft the Terminator!");
					data.setLore(lore);
					data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
					tessellated.setItemMeta(data);
					tessellated.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					enderman.getWorld().dropItem(enderman.getLocation(), tessellated);

					Player p = Plugin.getNearestPlayer(enderman);
					assert p != null;
					p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Tessellated Ender Pearl");
					p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
				}

				// NULL BLADE
			} else if(e.getEntity() instanceof IronGolem golem) {
				if(Objects.requireNonNull(golem.getCustomName()).contains("meloG norI") && random.nextDouble() > 0.9) {
					ItemStack nullBlade = new ItemStack(Material.SHEARS);
					ItemMeta data = nullBlade.getItemMeta();
					assert data != null;
					data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Null Blade");
					List<String> lore = new ArrayList<>();
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A pair of Shears so null that even");
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the most intelligent players");
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "are confused by it.");
					lore.add("");
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use 3 of these to craft the Terminator!");
					data.setLore(lore);
					data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
					nullBlade.setItemMeta(data);
					nullBlade.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					golem.getWorld().dropItem(golem.getLocation(), nullBlade);

					Player p = Plugin.getNearestPlayer(golem);
					assert p != null;
					p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Null Blade");
					p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
				}

				// BRAIDED FEATHER
			} else if(e.getEntity() instanceof Chicken chicken) {
				if(Objects.requireNonNull(chicken.getCustomName()).contains("Chickzilla") && random.nextDouble() > 0.9) {
					ItemStack braidedFeather = new ItemStack(Material.FEATHER);
					ItemMeta data = braidedFeather.getItemMeta();
					assert data != null;
					data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Braided Feather");
					List<String> lore = new ArrayList<>();
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A Feather so sturdy that even");
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the most powerful players");
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "cannot destroy it.");
					lore.add("");
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use this to craft the Terminator!");
					data.setLore(lore);
					data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
					braidedFeather.setItemMeta(data);
					braidedFeather.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					chicken.getWorld().dropItem(chicken.getLocation(), braidedFeather);

					Player p = Plugin.getNearestPlayer(chicken);
					assert p != null;
					p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Braided Feather");
					p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
				}

				// TARANTULA SILK
			} else if(e.getEntity() instanceof Spider spider) {
				if(Objects.requireNonNull(spider.getCustomName()).contains("Tarantula Broodfather") && random.nextDouble() > 0.9) {
					ItemStack taraSilk = new ItemStack(Material.COBWEB);
					ItemMeta data = taraSilk.getItemMeta();
					assert data != null;
					data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Tarantula Silk");
					List<String> lore = new ArrayList<>();
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A Web so perfect that even");
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the most powerful players");
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "cannot escape it.");
					lore.add("");
					lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use 2 of this to craft the Terminator!");
					data.setLore(lore);
					data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
					taraSilk.setItemMeta(data);
					taraSilk.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					spider.getWorld().dropItem(spider.getLocation(), taraSilk);

					Player p = Plugin.getNearestPlayer(spider);
					assert p != null;
					p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Tarantula Silk");
					p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
				}
				// SPAWN HIGHLY INFURIATED WITHER SKELETON
			} else if(e.getEntity() instanceof WitherSkeleton skeleton) {
				try {
					if(Objects.requireNonNull(skeleton.getCustomName()).contains("Highly Infuriated Wither Skeleton")) {
						ItemStack skull = new ItemStack(Material.WITHER_SKELETON_SKULL);
						skeleton.getWorld().dropItem(skeleton.getLocation(), skull);

						Player p = Plugin.getNearestPlayer(skeleton);
						assert p != null;
						p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Wither Skeleton Skull");
						p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
					}
				} catch(NullPointerException exception) {
					if(random.nextDouble() > 0.96) {
						spawnInfuriation(skeleton);
						LootContext context = new LootContext.Builder(skeleton.getLocation()).lootedEntity(skeleton).killer(e.getEntity().getKiller()).build();
						Objects.requireNonNull(skeleton.getLootTable()).populateLoot(random, context).remove(new ItemStack(Material.WITHER_SKELETON_SKULL));
					}
				}
			}
		} catch(NullPointerException exception) {
			// do nothing
		}
	}

	public void spawnVoidgloomSeraph(Entity e) {
		Enderman enderman = (Enderman) e.getWorld().spawnEntity(new Location(e.getWorld(), 5, 75, 5), EntityType.ENDERMAN);
		enderman.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Voidgloom Seraph" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
		enderman.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, -1, 3));
		enderman.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		enderman.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, -1, 3));
		enderman.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 9));
		CustomMobs.teleportToPlayer(enderman);
		Objects.requireNonNull(Plugin.getNearestPlayer(e)).sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The death of the Superior Dragon has drawn the attention of the VOIDGLOOM SERAPH!  Defeat it before it's too late!");
	}

	public void spawnInfuriation(Entity e) {
		WitherSkeleton skeleton = (WitherSkeleton) e.getWorld().spawnEntity(e.getLocation(), EntityType.WITHER_SKELETON);
		skeleton.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Highly Infuriated Wither Skeleton" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
		ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
		sword.addEnchantment(Enchantment.KNOCKBACK, 2);

		Objects.requireNonNull(skeleton.getEquipment()).setItemInMainHand(sword);
		skeleton.getEquipment().setItemInMainHandDropChance(0.0F);
		skeleton.getEquipment().setItemInOffHand(sword);
		skeleton.getEquipment().setItemInOffHandDropChance(0.0F);

		skeleton.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, -1, 3));
		skeleton.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 4));
		skeleton.setTarget(Plugin.getNearestPlayer(skeleton));
		skeleton.setCustomNameVisible(true);
		skeleton.teleport(Objects.requireNonNull(Plugin.getNearestPlayer(skeleton)));
		Objects.requireNonNull(Plugin.getNearestPlayer(e)).sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "From the ashes of the Wither Skeleton rises its reincarnation: a HIGHLY INFURIATED Wither Skeleton");
	}
}