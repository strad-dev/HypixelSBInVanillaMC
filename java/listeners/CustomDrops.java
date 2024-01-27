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

import java.util.List;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings({"DataFlowIssue"})
public class CustomDrops implements Listener {
	private Player p;

	public void sendRareDropMessage(String message) {
		p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + message);
		p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0F, 1.0F);
	}

	@SuppressWarnings({"StatementWithEmptyBody", "ConstantValue"})
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		LivingEntity entity;
		try {
			entity = e.getEntity();
		} catch(Exception exception) {
			return;
		}

		p = entity.getKiller();
		if(p == null) {
			p = Plugin.getNearestPlayer(entity);
		}

		List<ItemStack> drops = e.getDrops();
		if(entity instanceof Player || entity instanceof ArmorStand || entity instanceof AbstractHorse) {
			return;
		}
		drops.clear();

		int lootingLevel = 0;
		try {
			lootingLevel = Math.min(3, p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS));
		} catch(Exception exception) {
			// do nothing
		}
		double rngLootingBonus = 1.0;
		switch(lootingLevel) {
			case 1 -> rngLootingBonus = 1.167;
			case 2 -> rngLootingBonus = 1.333;
			case 3 -> rngLootingBonus = 1.500;
		}
		Random random = new Random();
		boolean onFire = entity.getFireTicks() > 0;

		ItemStack item;
		if(entity instanceof Allay) {
			// no loot
		} else if(entity instanceof Axolotl) {
			// no loot
		} else if(entity instanceof Bat) {
			// no loot
		} else if(entity instanceof Bee) {
			// no loot
		} else if(entity instanceof Blaze) {
			item = new ItemStack(Material.BLAZE_ROD);
			item.setAmount(random.nextInt(2 + lootingLevel));
			drops.add(item);
		} else if(entity instanceof Cat) {
			item = new ItemStack(Material.STRING);
			item.setAmount(random.nextInt(3));
			drops.add(item);
		} else if(entity instanceof CaveSpider) {
			item = new ItemStack(Material.STRING);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			if(random.nextDouble() < 0.4 * rngLootingBonus) {
				item = new ItemStack(Material.SPIDER_EYE);
				drops.add(item);
			}
		} else if(entity instanceof Chicken chicken) {
			item = new ItemStack(Material.FEATHER);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			if(onFire) {
				item = new ItemStack(Material.COOKED_CHICKEN);
			} else {
				item = new ItemStack(Material.CHICKEN);
			}
			item.setAmount(random.nextInt(1 + lootingLevel) + 1);
			drops.add(item);

			if(chicken.getCustomName().contains("Chickzilla")) {
				if(random.nextDouble() < 0.1 * rngLootingBonus) {
					item = SimilarData.braidedFeather();
					chicken.getWorld().dropItem(chicken.getLocation(), item);
					sendRareDropMessage("Braided Feather");
				}
			} else if(random.nextDouble() < 0.02 * rngLootingBonus) {
				item = SimilarData.omegaEgg();
				chicken.getWorld().dropItem(chicken.getLocation(), item);
				sendRareDropMessage("Omega Egg");
			}
		} else if(entity instanceof Cod) {
			if(random.nextDouble() < 0.05 * rngLootingBonus) {
				item = new ItemStack(Material.BONE_MEAL);
				drops.add(item);
			}

			if(onFire) {
				item = new ItemStack(Material.COOKED_COD);
			} else {
				item = new ItemStack(Material.COD);
			}
			item.setAmount(random.nextInt(1 + lootingLevel) + 1);
			drops.add(item);
		} else if(entity instanceof Cow) {
			item = new ItemStack(Material.LEATHER);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			if(onFire) {
				item = new ItemStack(Material.COOKED_BEEF);
			} else {
				item = new ItemStack(Material.BEEF);
			}
			item.setAmount(random.nextInt(3 + lootingLevel) + 1);
			drops.add(item);
		} else if(entity instanceof Creeper) {
			item = new ItemStack(Material.GUNPOWDER);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			if(random.nextDouble() < 0.001 * rngLootingBonus) {
				item = new ItemStack(Material.CREEPER_HEAD);
				drops.add(item);
				sendRareDropMessage("Creeper Head");
			}

			if(random.nextDouble() < 0.001 * rngLootingBonus) {
				item = new ItemStack(Material.MUSIC_DISC_11);
				drops.add(item);
				item = new ItemStack(Material.MUSIC_DISC_13);
				drops.add(item);
				item = new ItemStack(Material.MUSIC_DISC_BLOCKS);
				drops.add(item);
				item = new ItemStack(Material.MUSIC_DISC_CAT);
				drops.add(item);
				item = new ItemStack(Material.MUSIC_DISC_CHIRP);
				drops.add(item);
				item = new ItemStack(Material.MUSIC_DISC_FAR);
				drops.add(item);
				item = new ItemStack(Material.MUSIC_DISC_MALL);
				drops.add(item);
				item = new ItemStack(Material.MUSIC_DISC_MELLOHI);
				drops.add(item);
				item = new ItemStack(Material.MUSIC_DISC_STAL);
				drops.add(item);
				item = new ItemStack(Material.MUSIC_DISC_STRAD);
				drops.add(item);
				item = new ItemStack(Material.MUSIC_DISC_WAIT);
				drops.add(item);
				item = new ItemStack(Material.MUSIC_DISC_WARD);
				drops.add(item);
				sendRareDropMessage("The Entire Music Disc Collection Because Strad Is Lazy");
			}
		} else if(entity instanceof Dolphin) {
			if(onFire) {
				item = new ItemStack(Material.COOKED_COD);
			} else {
				item = new ItemStack(Material.COD);
			}
			item.setAmount(random.nextInt(2 + lootingLevel));
			drops.add(item);
		} else if(entity instanceof Drowned) {
			item = new ItemStack(Material.ROTTEN_FLESH);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			if(random.nextDouble() < 0.1 * rngLootingBonus) {
				item = new ItemStack(Material.COPPER_INGOT);
				drops.add(item);
			}

			if(random.nextDouble() < 0.005 * rngLootingBonus) {
				item = new ItemStack(Material.TRIDENT);
				drops.add(item);
				sendRareDropMessage("Trident");
			}

			if(random.nextDouble() < 0.03 * rngLootingBonus) {
				item = new ItemStack(Material.NAUTILUS_SHELL);
				drops.add(item);
				sendRareDropMessage("Nautilus Shell");
			}
		} else if(entity instanceof ElderGuardian) {
			item = new ItemStack(Material.PRISMARINE_SHARD);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			item = new ItemStack(Material.WET_SPONGE);
			item.setAmount(random.nextInt(1 + lootingLevel) + 1);
			drops.add(item);

			if(random.nextDouble() < 0.2 * rngLootingBonus) {
				item = new ItemStack(Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE);
				drops.add(item);
				sendRareDropMessage("Tide Armor Trim");
			}

			if(random.nextDouble() < 0.333 * rngLootingBonus) {
				item = new ItemStack(Material.PRISMARINE_CRYSTALS);
				drops.add(item);
			}
		} else if(entity instanceof EnderDragon dragon) {
			if(Objects.requireNonNull(dragon.getCustomName()).contains("Superior Dragon") || random.nextDouble() < 0.01 * rngLootingBonus) {
				item = SimilarData.supRemnant();
				drops.add(item);
				sendRareDropMessage("Remnant of the Superior Dragon");
			}

			dragon.teleport(new Location(dragon.getWorld(), 0.5, 70.0, 0.5));
		} else if(entity instanceof Enderman enderman) {
			item = new ItemStack(Material.ENDER_PEARL);
			item.setAmount(random.nextInt(2 + lootingLevel));
			drops.add(item);

			if(Objects.requireNonNull(enderman.getCustomName()).contains("Voidgloom Seraph")) {
				if(random.nextDouble() < 0.1 * rngLootingBonus) {
					item = SimilarData.core();
					drops.add(item);
					sendRareDropMessage("Judgement Core");
				}
			} else if(enderman.getCustomName().contains("Mutant Enderman")) {
				if(random.nextDouble() < 0.1 * rngLootingBonus) {
					item = SimilarData.tessellatedPearl();
					drops.add(item);
					sendRareDropMessage("Tessellated Ender Pearl");
				}
			} else {
				if(entity.getWorld().getEnvironment().equals(World.Environment.THE_END) && random.nextDouble() < 0.005 * rngLootingBonus ||
						!entity.getWorld().getEnvironment().equals(World.Environment.THE_END) && random.nextDouble() < 0.03 * rngLootingBonus) {
					item = SimilarData.corruptedPearl();
					drops.add(item);
					sendRareDropMessage("Corrupted Pearl");
				}
			}
		} else if(entity instanceof Endermite) {
			// no drops
		} else if(entity instanceof Evoker) {
			item = new ItemStack(Material.TOTEM_OF_UNDYING);
			drops.add(item);

			item = new ItemStack(Material.EMERALD);
			item.setAmount(random.nextInt(2 + lootingLevel));
			drops.add(item);
		} else if(entity instanceof Fox fox) {
			// TODO fox drop whatever they're holding
		} else if(entity instanceof Frog) {
			// no drops
		} else if(entity instanceof Ghast) {
			item = new ItemStack(Material.GHAST_TEAR);
			item.setAmount(random.nextInt(2 + lootingLevel));
			drops.add(item);

			item = new ItemStack(Material.GUNPOWDER);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);
		} else if(entity instanceof Giant) {
			// no drops
		} else if(entity instanceof GlowSquid) {
			item = new ItemStack(Material.GLOW_INK_SAC);
			item.setAmount(random.nextInt(3 + lootingLevel) + 1);
			drops.add(item);
		} else if(entity instanceof Goat) {
			// no drops
		} else if(entity instanceof Guardian) {
			item = new ItemStack(Material.PRISMARINE_SHARD);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			if(random.nextDouble() < 0.2 * rngLootingBonus) {
				item = new ItemStack(Material.PRISMARINE_CRYSTALS);
				drops.add(item);
			}
		} else if(entity instanceof Hoglin) {
			if(onFire) {
				item = new ItemStack(Material.COOKED_PORKCHOP);
			} else {
				item = new ItemStack(Material.PORKCHOP);
			}
			item.setAmount(random.nextInt(3 + lootingLevel) + 2);
			drops.add(item);

			item = new ItemStack(Material.LEATHER);
			item.setAmount(random.nextInt(1 + lootingLevel) + 1);
			drops.add(item);
		} else if(entity instanceof Husk) {
			item = new ItemStack(Material.ROTTEN_FLESH);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			if(random.nextDouble() < 0.01 * rngLootingBonus) {
				item = new ItemStack(Material.IRON_INGOT);
				drops.add(item);
				sendRareDropMessage("Iron Ingot");
			}

			if(random.nextDouble() < 0.01 * rngLootingBonus) {
				item = new ItemStack(Material.CARROT);
				drops.add(item);
				sendRareDropMessage("Carrot");
			}

			if(random.nextDouble() < 0.01 * rngLootingBonus) {
				item = new ItemStack(Material.POTATO);
				drops.add(item);
				sendRareDropMessage("Potato");
			}
		} else if(entity instanceof IronGolem golem) {
			item = new ItemStack(Material.IRON_INGOT);
			item.setAmount(random.nextInt(3 + lootingLevel) + 3);
			drops.add(item);

			item = new ItemStack(Material.POPPY);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			if(Objects.requireNonNull(golem.getCustomName()).contains("meloG norI")) {
				if(random.nextDouble() < 0.1 * rngLootingBonus) {
					item = SimilarData.nullBlade();
					drops.add(item);
					sendRareDropMessage("Null Blade");
				}
			} else if(random.nextDouble() < 0.02 * rngLootingBonus) {
				item = SimilarData.antimatter();
				drops.add(item);
				sendRareDropMessage("Antimatter");
			}
		} else if(entity instanceof MagmaCube cube) {
			cube.setCustomName("Magma Cube");
			item = new ItemStack(Material.MAGMA_CREAM);
			item.setAmount(random.nextInt(1 + lootingLevel) + 1);
			drops.add(item);

			if(random.nextDouble() < 0.001 * rngLootingBonus) {
				item = new ItemStack(Material.PEARLESCENT_FROGLIGHT);
				drops.add(item);
				item = new ItemStack(Material.PEARLESCENT_FROGLIGHT);
				drops.add(item);
				item = new ItemStack(Material.PEARLESCENT_FROGLIGHT);
				drops.add(item);
				sendRareDropMessage("Froglight Collection");
			}
		} else if(entity instanceof MushroomCow) {
			// see Cow - here for completeness
		} else if(entity instanceof Ocelot) {
			// no drops
		} else if(entity instanceof Panda) {
			// no drops
		} else if(entity instanceof Parrot) {
			item = new ItemStack(Material.FEATHER);
			item.setAmount(random.nextInt(2 + lootingLevel) + 1);
			drops.add(item);
		} else if(entity instanceof Phantom) {
			item = new ItemStack(Material.PHANTOM_MEMBRANE);
			item.setAmount(random.nextInt(2 + lootingLevel));
			drops.add(item);
		} else if(entity instanceof Pig pig) {
			if(onFire) {
				item = new ItemStack(Material.COOKED_PORKCHOP);
			} else {
				item = new ItemStack(Material.PORKCHOP);
			}
			item.setAmount(random.nextInt(3 + lootingLevel) + 1);
			drops.add(item);

			if(pig.hasSaddle()) {
				drops.add(new ItemStack(Material.SADDLE));
			}
		} else if(entity instanceof Piglin) {
			if(random.nextDouble() < 0.001 * rngLootingBonus) {
				item = new ItemStack(Material.PIGLIN_HEAD);
				drops.add(item);
				sendRareDropMessage("Piglin Head");
			}
		} else if(entity instanceof PiglinBrute) {
			// no drops
		} else if(entity instanceof PigZombie) {
			item = new ItemStack(Material.ROTTEN_FLESH);
			item.setAmount(random.nextInt(2 + lootingLevel));
			drops.add(item);

			item = new ItemStack(Material.GOLD_NUGGET);
			item.setAmount(random.nextInt(2 + lootingLevel));
			drops.add(item);

			if(random.nextDouble() < 0.02 * rngLootingBonus) {
				item = new ItemStack(Material.GOLD_INGOT);
				drops.add(item);
				sendRareDropMessage("Gold Ingot");
			}
		} else if(entity instanceof Pillager) {
			// no drops
		} else if(entity instanceof PolarBear) {
			// no drops
		} else if(entity instanceof PufferFish) {
			item = new ItemStack(Material.PUFFERFISH);
			item.setAmount(random.nextInt(1 + lootingLevel) + 1);
			drops.add(item);

			if(random.nextDouble() < 0.05 * rngLootingBonus) {
				item = new ItemStack(Material.BONE_MEAL);
				drops.add(item);
			}
		} else if(entity instanceof Rabbit) {
			item = new ItemStack(Material.RABBIT_HIDE);
			item.setAmount(random.nextInt(2 + lootingLevel));
			drops.add(item);

			if(onFire) {
				item = new ItemStack(Material.COOKED_RABBIT);
			} else {
				item = new ItemStack(Material.RABBIT);
			}
			item.setAmount(random.nextInt(1 + lootingLevel) + 1);
			drops.add(item);

			if(random.nextDouble() < 0.12 * rngLootingBonus) {
				item = new ItemStack(Material.RABBIT_HIDE);
				drops.add(item);
			}
		} else if(entity instanceof Ravager) {
			item = new ItemStack(Material.SADDLE);
			drops.add(item);
		} else if(entity instanceof Salmon) {
			if(random.nextDouble() < 0.05 * rngLootingBonus) {
				item = new ItemStack(Material.BONE_MEAL);
				drops.add(item);
			}

			if(onFire) {
				item = new ItemStack(Material.COOKED_SALMON);
			} else {
				item = new ItemStack(Material.SALMON);
			}
			item.setAmount(random.nextInt(1 + lootingLevel) + 1);
			drops.add(item);
		} else if(entity instanceof Sheep) {
			item = new ItemStack(Material.WHITE_WOOL);
			item.setAmount(random.nextInt(1 + lootingLevel) + 1);
			drops.add(item);

			if(onFire) {
				item = new ItemStack(Material.COOKED_MUTTON);
			} else {
				item = new ItemStack(Material.MUTTON);
			}
			item.setAmount(random.nextInt(2 + lootingLevel) + 1);
			drops.add(item);
		} else if(entity instanceof Shulker) {
			item = new ItemStack(Material.SHULKER_SHELL);
			item.setAmount(random.nextInt(2 + lootingLevel));
			drops.add(item);
		} else if(entity instanceof Silverfish) {
			// no drops
		} else if(entity instanceof Skeleton) {
			item = new ItemStack(Material.BONE);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			item = new ItemStack(Material.ARROW);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			if(random.nextDouble() < 0.001 * rngLootingBonus) {
				item = new ItemStack(Material.SKELETON_SKULL);
				drops.add(item);
				sendRareDropMessage("Skeleton Skull");
			}
		} else if(entity instanceof Slime slime) {
			slime.setCustomName("Slime");
			item = new ItemStack(Material.SLIME_BALL);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);
		} else if(entity instanceof Sniffer) {
			// no drops
		} else if(entity instanceof Snowman) {
			item = new ItemStack(Material.SNOWBALL);
			item.setAmount(random.nextInt(16 + 16 * lootingLevel));
			drops.add(item);
		} else if(entity instanceof Spider spider) {
			item = new ItemStack(Material.STRING);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			if(random.nextDouble() < 0.4 * rngLootingBonus) {
				item = new ItemStack(Material.SPIDER_EYE);
				drops.add(item);
			}

			if(Objects.requireNonNull(spider.getCustomName()).contains("Tarantula Broodfather")) {
				if(random.nextDouble() < 0.1 * rngLootingBonus) {
					item = SimilarData.taraSilk();
					drops.add(item);
					sendRareDropMessage("Tarantula Silk");
				}
			} else if(random.nextDouble() < 0.03 * rngLootingBonus) {
				item = SimilarData.spiderRelic();
				drops.add(item);
				sendRareDropMessage("Spider Relic");
			}
		} else if(entity instanceof Squid) {
			item = new ItemStack(Material.INK_SAC);
			item.setAmount(random.nextInt(3 + lootingLevel) + 1);
			drops.add(item);
		} else if(entity instanceof Stray) {
			if(random.nextDouble() < 0.01 * rngLootingBonus) {
				item = SimilarData.iceSpray();
				drops.add(item);
				sendRareDropMessage("Ice Spray Wand");
			}
		} else if(entity instanceof Strider strider) {
			item = new ItemStack(Material.STRING);
			item.setAmount(random.nextInt(4 + lootingLevel) + 2);
			drops.add(item);

			if(strider.hasSaddle()) {
				drops.add(new ItemStack(Material.SADDLE));
			}
		} else if(entity instanceof Tadpole) {
			// no drops
		} else if(entity instanceof TropicalFish) {
			if(random.nextDouble() < 0.05 * rngLootingBonus) {
				item = new ItemStack(Material.BONE_MEAL);
				drops.add(item);
			}

			item = new ItemStack(Material.TROPICAL_FISH);
			item.setAmount(random.nextInt(1 + lootingLevel) + 2);
			drops.add(item);
		} else if(entity instanceof Turtle) {
			item = new ItemStack(Material.SEAGRASS);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			if(random.nextDouble() < 0.001 * rngLootingBonus) {
				item = new ItemStack(Material.BOWL);
				drops.add(item);
				sendRareDropMessage("Bowl (for whatever reason)");
			}
		} else if(entity instanceof Vex) {
			// no drops
		} else if(entity instanceof Villager) {
			// no drops
		} else if(entity instanceof Vindicator) {
			item = new ItemStack(Material.EMERALD);
			item.setAmount(random.nextInt(2 + lootingLevel));
			drops.add(item);
		} else if(entity instanceof WanderingTrader) {
			// no loot
		} else if(entity instanceof Warden) {
			item = new ItemStack(Material.SCULK_CATALYST);
			item.setAmount(random.nextInt(1 + lootingLevel) + 1);
			drops.add(item);

			if(random.nextDouble() < 0.01 * rngLootingBonus) {
				item = SimilarData.wardenHeart();
				drops.add(item);
				sendRareDropMessage("Warden Heart");
			}
		} else if(entity instanceof Witch) {
			if(random.nextDouble() < 0.25 * rngLootingBonus) {
				item = new ItemStack(Material.GLOWSTONE_DUST);
				drops.add(item);
			}

			if(random.nextDouble() < 0.25 * rngLootingBonus) {
				item = new ItemStack(Material.SUGAR);
				drops.add(item);
			}

			if(random.nextDouble() < 0.25 * rngLootingBonus) {
				item = new ItemStack(Material.REDSTONE);
				drops.add(item);
			}

			if(random.nextDouble() < 0.25 * rngLootingBonus) {
				item = new ItemStack(Material.SPIDER_EYE);
				drops.add(item);
			}

			if(random.nextDouble() < 0.25 * rngLootingBonus) {
				item = new ItemStack(Material.GLASS_BOTTLE);
				drops.add(item);
			}

			if(random.nextDouble() < 0.25 * rngLootingBonus) {
				item = new ItemStack(Material.GUNPOWDER);
				drops.add(item);
			}

			if(random.nextDouble() < 0.5 * rngLootingBonus) {
				item = new ItemStack(Material.STICK);
				drops.add(item);
			}
		} else if(entity instanceof Wither wither) {
			drops.add(new ItemStack(Material.NETHER_STAR));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=wither_skull]");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=lightning_bolt]");

			if(Objects.requireNonNull(wither.getCustomName()).contains("Maxor")) {
				if(random.nextDouble() < 0.01 * rngLootingBonus) {
					item = SimilarData.maxorSecrets();
					drops.add(item);
					sendRareDropMessage("Maxor's Secrets");
				} else if(random.nextDouble() < 0.06 * rngLootingBonus) {
					item = SimilarData.shadowWarp();
					drops.add(item);
					sendRareDropMessage("Shadow Warp");
				}
			} else if(Objects.requireNonNull(wither.getCustomName()).contains("Storm")) {
				wither.getServer().dispatchCommand(Bukkit.getConsoleSender(), "weather clear");
				if(random.nextDouble() < 0.05 * rngLootingBonus) {
					item = SimilarData.implosion();
					drops.add(item);
					sendRareDropMessage("Implosion");
				}
			} else if(Objects.requireNonNull(wither.getCustomName()).contains("Goldor")) {
				if(random.nextDouble() < 0.05 * rngLootingBonus) {
					item = SimilarData.witherShield();
					drops.add(item);
					sendRareDropMessage("Wither Shield");
				}
			} else if(Objects.requireNonNull(wither.getCustomName()).contains("Necron")) {
				if(random.nextDouble() < 0.01 * rngLootingBonus) {
					item = SimilarData.necronSecrets();
					drops.add(item);
					sendRareDropMessage("Necron's Secrets");
				} else if(random.nextDouble() < 0.06 * rngLootingBonus) {
					item = SimilarData.handle();
					drops.add(item);
					sendRareDropMessage("Necron's Handle");
				}
			}
		} else if(entity instanceof WitherSkeleton skeleton) {
			item = new ItemStack(Material.COAL);
			item.setAmount(random.nextInt(1 + lootingLevel) + 1);
			drops.add(item);

			item = new ItemStack(Material.BONE);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			if(skeleton.getCustomName().contains("Highly Infuriated Wither Skeleton")) {
				drops.add(new ItemStack(Material.WITHER_SKELETON_SKULL));
				sendRareDropMessage("Wither Skeleton Skull");
			} else {
				if(random.nextDouble() < 0.03 * rngLootingBonus) {
					spawnInfuriation(skeleton);
				}
			}
		} else if(entity instanceof Wolf) {
			// no drops
		} else if(entity instanceof Zoglin) {
			item = new ItemStack(Material.ROTTEN_FLESH);
			item.setAmount(random.nextInt(3 + lootingLevel) + 1);
			drops.add(item);
		} else if(entity instanceof Zombie zombie) {
			item = new ItemStack(Material.ROTTEN_FLESH);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			if(random.nextDouble() < 0.01 * rngLootingBonus) {
				item = new ItemStack(Material.IRON_INGOT);
				drops.add(item);
				sendRareDropMessage("Iron Ingot");
			}

			if(random.nextDouble() < 0.01 * rngLootingBonus) {
				item = new ItemStack(Material.CARROT);
				drops.add(item);
				sendRareDropMessage("Carrot");
			}

			if(random.nextDouble() < 0.01 * rngLootingBonus) {
				item = new ItemStack(Material.POTATO);
				drops.add(item);
				sendRareDropMessage("Potato");
			}

			if(random.nextDouble() < 0.001 * rngLootingBonus) {
				item = new ItemStack(Material.ZOMBIE_HEAD);
				drops.add(item);
				sendRareDropMessage("Zombie Head");
			}

			if(Objects.requireNonNull(zombie.getCustomName()).contains("Atoned Horror")) {
				if(random.nextDouble() < 0.1 * rngLootingBonus) {
					item = SimilarData.viscera();
					drops.add(item);
					sendRareDropMessage("Revenant Viscera");
				}
			} else if(random.nextDouble() < 0.005 * rngLootingBonus) {
				item = SimilarData.atonedFlesh();
				drops.add(item);
				sendRareDropMessage("Atoned Flesh");
			}
		} else if(entity instanceof ZombieVillager) {
			item = new ItemStack(Material.ROTTEN_FLESH);
			item.setAmount(random.nextInt(3 + lootingLevel));
			drops.add(item);

			if(random.nextDouble() < 0.01 * rngLootingBonus) {
				item = new ItemStack(Material.IRON_INGOT);
				drops.add(item);
				sendRareDropMessage("Iron Ingot");
			}

			if(random.nextDouble() < 0.01 * rngLootingBonus) {
				item = new ItemStack(Material.CARROT);
				drops.add(item);
				sendRareDropMessage("Carrot");
			}

			if(random.nextDouble() < 0.01 * rngLootingBonus) {
				item = new ItemStack(Material.POTATO);
				drops.add(item);
				sendRareDropMessage("Potato");
			}
		}
	}

	public void spawnInfuriation(Entity e) {
		WitherSkeleton skeleton = (WitherSkeleton) e.getWorld().spawnEntity(e.getLocation(), EntityType.WITHER_SKELETON);
		skeleton.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Highly Infuriated Wither Skeleton" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RED + "❤ " + ChatColor.YELLOW + 50 + "/" + 50);
		ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
		sword.addEnchantment(Enchantment.KNOCKBACK, 2);

		Objects.requireNonNull(skeleton.getEquipment()).setItemInMainHand(sword);
		skeleton.getEquipment().setItemInMainHandDropChance(0.0F);
		skeleton.getEquipment().setItemInOffHand(sword);
		skeleton.getEquipment().setItemInOffHandDropChance(0.0F);

		Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(50.0);
		skeleton.setHealth(50.0);
		Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(12.5);
		Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.5);
		skeleton.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		skeleton.setTarget(Plugin.getNearestPlayer(skeleton));
		skeleton.setCustomNameVisible(true);
		skeleton.teleport(Objects.requireNonNull(Plugin.getNearestPlayer(skeleton)));
		Objects.requireNonNull(Plugin.getNearestPlayer(e)).sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "From the ashes of the Wither Skeleton rises its reincarnation: a HIGHLY INFURIATED Wither Skeleton");
	}
}