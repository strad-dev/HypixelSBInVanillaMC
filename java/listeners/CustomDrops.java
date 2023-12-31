package listeners;

import misc.Plugin;
import misc.SimilarData;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.Random;

@SuppressWarnings({"DataFlowIssue"})
public class CustomDrops implements Listener {
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		Random random = new Random();
		try {
			if(e.getEntity() instanceof Wither wither) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=wither_skull]");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=lightning_bolt]");
				boolean drop = random.nextDouble() < 0.05;

				// SHADOW WARP
				if(Objects.requireNonNull(wither.getCustomName()).contains("Maxor")) {
					if(drop) {
						ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
						item.setItemMeta(SimilarData.shadowWarpMeta(item.getItemMeta()));
						item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
						wither.getWorld().dropItem(wither.getLocation(), item);

						Player p = Plugin.getNearestPlayer(wither);
						assert p != null;
						p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Shadow Warp");
						p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
					}

					// IMPLOSION
				} else if(Objects.requireNonNull(wither.getCustomName()).contains("Storm")) {
					wither.getServer().dispatchCommand(Bukkit.getConsoleSender(), "weather clear");
					if(drop) {
						ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
						item.setItemMeta(SimilarData.implosionMeta(item.getItemMeta()));
						item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
						wither.getWorld().dropItem(wither.getLocation(), item);

						Player p = Plugin.getNearestPlayer(wither);
						assert p != null;
						p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Implosion");
						p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
					}

					// WITHER SHIELD
				} else if(Objects.requireNonNull(wither.getCustomName()).contains("Goldor")) {
					if(drop) {
						ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
						item.setItemMeta(SimilarData.witherShieldMeta(item.getItemMeta()));
						item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
						wither.getWorld().dropItem(wither.getLocation(), item);

						Player p = Plugin.getNearestPlayer(wither);
						assert p != null;
						p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Wither Shield");
						p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
					}

					// NECRON'S HANDLE
				} else if(Objects.requireNonNull(wither.getCustomName()).contains("Necron")) {
					if(drop) {
						ItemStack item = new ItemStack(Material.STICK);
						item.setItemMeta(SimilarData.necronHandleMeta(item.getItemMeta()));
						item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
						wither.getWorld().dropItem(wither.getLocation(), item);

						Player p = Plugin.getNearestPlayer(wither);
						assert p != null;
						p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Necron's Handle");
						p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
					}
				}


				// SPAWN A VOIDGLOOM SERAPH
			} else if(e.getEntity() instanceof EnderDragon dragon) {
				if(Objects.requireNonNull(dragon.getCustomName()).contains("Superior Dragon")) {
					ItemStack item = new ItemStack(Material.QUARTZ);
					item.setItemMeta(SimilarData.supRemnantMeta(item.getItemMeta()));
					item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					dragon.getWorld().dropItem(dragon.getLocation(), item);

					Player p = Plugin.getNearestPlayer(dragon);
					assert p != null;
					p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Remnant of the Superior Dragon");
					p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
				}

				// JUDGEMENT CORE
			} else if(e.getEntity() instanceof Enderman enderman) {
				if(Objects.requireNonNull(enderman.getCustomName()).contains("Voidgloom Seraph") && random.nextDouble() < 0.1) {
					ItemStack item = new ItemStack(Material.CHISELED_QUARTZ_BLOCK);
					item.setItemMeta(SimilarData.coreMeta(item.getItemMeta()));
					item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					enderman.getWorld().dropItem(enderman.getLocation(), item);

					Player p = Plugin.getNearestPlayer(enderman);
					assert p != null;
					p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Judgement Core");
					p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);

					// TESSELLATED ENDER PEARL
				} else if(enderman.getCustomName().contains("Mutant Enderman") && random.nextDouble() < 0.1) {
					ItemStack item = new ItemStack(Material.ENDER_PEARL);
					item.setItemMeta(SimilarData.tessellatedPearlMeta(item.getItemMeta()));
					item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					enderman.getWorld().dropItem(enderman.getLocation(), item);

					Player p = Plugin.getNearestPlayer(enderman);
					assert p != null;
					p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Tessellated Ender Pearl");
					p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
				} else {
					if(e.getEntity().getWorld().getEnvironment().equals(World.Environment.THE_END) && random.nextDouble() < 0.005 || random.nextDouble() < 0.03) {
						ItemStack item = new ItemStack(Material.ENDER_EYE);
						item.setItemMeta(SimilarData.corruptedPearlMeta(item.getItemMeta()));
						item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
						enderman.getWorld().dropItem(enderman.getLocation(), item);

						Player p = Plugin.getNearestPlayer(enderman);
						assert p != null;
						p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Corrupted Pearl");
						p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
					}
				}

				// NULL BLADE
			} else if(e.getEntity() instanceof IronGolem golem) {
				if(Objects.requireNonNull(golem.getCustomName()).contains("meloG norI") && random.nextDouble() < 0.1) {
					ItemStack item = new ItemStack(Material.SHEARS);
					item.setItemMeta(SimilarData.nullBladeBeta(item.getItemMeta()));
					item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					golem.getWorld().dropItem(golem.getLocation(), item);

					Player p = Plugin.getNearestPlayer(golem);
					assert p != null;
					p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Null Blade");
					p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
				} else if(random.nextDouble() < 0.02) {
					ItemStack item = new ItemStack(Material.WARPED_FUNGUS);
					item.setItemMeta(SimilarData.antimatterMeta(item.getItemMeta()));
					item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					golem.getWorld().dropItem(golem.getLocation(), item);

					Player p = Plugin.getNearestPlayer(golem);
					assert p != null;
					p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Antimatter");
					p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
				}

				// BRAIDED FEATHER
			} else if(e.getEntity() instanceof Chicken chicken) {
				if(Objects.requireNonNull(chicken.getCustomName()).contains("Chickzilla") && random.nextDouble() < 0.1) {
					ItemStack item = new ItemStack(Material.FEATHER);
					item.setItemMeta(SimilarData.braidedFeatherMeta(item.getItemMeta()));
					item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					chicken.getWorld().dropItem(chicken.getLocation(), item);

					Player p = Plugin.getNearestPlayer(chicken);
					assert p != null;
					p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Braided Feather");
					p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
				} else if(random.nextDouble() < 0.02) {
					ItemStack item = new ItemStack(Material.EGG);
					item.setItemMeta(SimilarData.omegaEggMeta(item.getItemMeta()));
					item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					chicken.getWorld().dropItem(chicken.getLocation(), item);

					Player p = Plugin.getNearestPlayer(chicken);
					assert p != null;
					p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Omega Egg");
					p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
				}

				// TARANTULA SILK
			} else if(e.getEntity() instanceof Spider spider) {
				if(Objects.requireNonNull(spider.getCustomName()).contains("Tarantula Broodfather") && random.nextDouble() < 0.1) {
					ItemStack item = new ItemStack(Material.COBWEB);
					item.setItemMeta(SimilarData.taraSilkMeta(item.getItemMeta()));
					item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					spider.getWorld().dropItem(spider.getLocation(), item);

					Player p = Plugin.getNearestPlayer(spider);
					assert p != null;
					p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Tarantula Silk");
					p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
				} else if(random.nextDouble() < 0.03) {
					ItemStack item = new ItemStack(Material.FERMENTED_SPIDER_EYE);
					item.setItemMeta(SimilarData.spiderRelicMeta(item.getItemMeta()));
					item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					spider.getWorld().dropItem(spider.getLocation(), item);

					Player p = Plugin.getNearestPlayer(spider);
					assert p != null;
					p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Spider Relic");
					p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
				}
				// SPAWN HIGHLY INFURIATED WITHER SKELETON
			} else if(e.getEntity() instanceof WitherSkeleton skeleton) {
				try {
					if(Objects.requireNonNull(skeleton.getCustomName()).contains("Highly Infuriated Wither Skeleton")) {
						ItemStack item = new ItemStack(Material.WITHER_SKELETON_SKULL);
						skeleton.getWorld().dropItem(skeleton.getLocation(), item);

						Player p = Plugin.getNearestPlayer(skeleton);
						assert p != null;
						p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Wither Skeleton Skull");
						p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
					}
				} catch(NullPointerException exception) {
					e.getDrops().remove(new ItemStack(Material.WITHER_SKELETON_SKULL));
					if(random.nextDouble() < 0.04) {
						spawnInfuriation(skeleton);
					}
				}
			} else if(e.getEntity() instanceof Stray stray) {
				if(random.nextDouble() < 0.01) {
					ItemStack item = new ItemStack(Material.STICK);
					item.setItemMeta(SimilarData.iceSprayMeta(item.getItemMeta()));
					item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
					stray.getWorld().dropItem(stray.getLocation(), item);

					Player p = Plugin.getNearestPlayer(stray);
					assert p != null;
					p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + "Ice Spray Wand");
					p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
				}
			}
		} catch(NullPointerException exception) {
			// nothing here
		}
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

		Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(20.0);
		Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.75);
		skeleton.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		skeleton.setTarget(Plugin.getNearestPlayer(skeleton));
		skeleton.setCustomNameVisible(true);
		skeleton.teleport(Objects.requireNonNull(Plugin.getNearestPlayer(skeleton)));
		Objects.requireNonNull(Plugin.getNearestPlayer(e)).sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "From the ashes of the Wither Skeleton rises its reincarnation: a HIGHLY INFURIATED Wither Skeleton");
	}
}