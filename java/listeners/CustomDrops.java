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

	public static void sendRareDropMessage(Player p, String message) {
		if(p != null) {
			p.sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RARE DROP!  " + ChatColor.RESET + message);
			p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
		}
	}

	public static void loot(LivingEntity died, Entity killer) {
		Player p;
		int lootingLevel = 0;
		if(killer instanceof Player p1) {
			p = p1;
			try {
				lootingLevel = Math.min(3, p1.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOTING));
			} catch(Exception exception) {
				// do nothing
			}
		} else {
			p = Plugin.getNearestPlayer(died);
			if(p != null && p.getLocation().distance(died.getLocation()) > 16) {
				p = null;
			}
		}
		double rngLootingBonus = 1.0;
		switch(lootingLevel) {
			case 1 -> rngLootingBonus = 1.167;
			case 2 -> rngLootingBonus = 1.333;
			case 3 -> rngLootingBonus = 1.500;
		}
		Random random = new Random();
		World world = died.getWorld();
		Location l = died.getLocation();
		boolean onFire = died.getFireTicks() > 0;
		ItemStack item;
		switch(died) {
			case Blaze ignored -> {
				item = new ItemStack(Material.BLAZE_ROD);
				item.setAmount(random.nextInt(2 + lootingLevel));
				world.dropItemNaturally(l, item);
			}
			case Cat ignored -> {
				item = new ItemStack(Material.STRING);
				item.setAmount(random.nextInt(3));
				world.dropItemNaturally(l, item);
			}
			case CaveSpider ignored -> {
				item = new ItemStack(Material.STRING);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(random.nextDouble() < 0.4 * rngLootingBonus) {
					item = new ItemStack(Material.SPIDER_EYE);
					world.dropItemNaturally(l, item);
				}
			}
			case Chicken chicken -> {
				item = new ItemStack(Material.FEATHER);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(onFire) {
					item = new ItemStack(Material.COOKED_CHICKEN);
				} else {
					item = new ItemStack(Material.CHICKEN);
				}
				item.setAmount(random.nextInt(1 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
				if(chicken.getCustomName().contains("Chickzilla")) {
					if(random.nextDouble() < 0.1 * rngLootingBonus) {
						item = SimilarData.braidedFeather();
						chicken.getWorld().dropItem(chicken.getLocation(), item);
						sendRareDropMessage(p, "Braided Feather");
					}
				} else if(random.nextDouble() < 0.02 * rngLootingBonus && p != null) {
					item = SimilarData.omegaEgg();
					chicken.getWorld().dropItem(chicken.getLocation(), item);
					sendRareDropMessage(p, "Omega Egg");
				}
			}
			case Cod ignored -> {
				if(random.nextDouble() < 0.05 * rngLootingBonus) {
					item = new ItemStack(Material.BONE_MEAL);
					world.dropItemNaturally(l, item);
				}
				if(onFire) {
					item = new ItemStack(Material.COOKED_COD);
				} else {
					item = new ItemStack(Material.COD);
				}
				item.setAmount(random.nextInt(1 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
			}
			case Cow ignored -> {
				item = new ItemStack(Material.LEATHER);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(onFire) {
					item = new ItemStack(Material.COOKED_BEEF);
				} else {
					item = new ItemStack(Material.BEEF);
				}
				item.setAmount(random.nextInt(3 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
			}
			case Creeper ignored -> {
				item = new ItemStack(Material.GUNPOWDER);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(killer instanceof Creeper c && c.isPowered()) {
					item = new ItemStack(Material.CREEPER_HEAD);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Creeper Head");
				}
				if(killer instanceof Skeleton) {
					switch(random.nextInt(12)) {
						case 0 -> {
							item = new ItemStack(Material.MUSIC_DISC_11);
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "11");
						}
						case 1 -> {
							item = new ItemStack(Material.MUSIC_DISC_13);
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "13");
						}
						case 2 -> {
							item = new ItemStack(Material.MUSIC_DISC_BLOCKS);
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Blocks");
						}
						case 3 -> {
							item = new ItemStack(Material.MUSIC_DISC_CAT);
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Cat");
						}
						case 4 -> {
							item = new ItemStack(Material.MUSIC_DISC_CHIRP);
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Chirp");
						}
						case 5 -> {
							item = new ItemStack(Material.MUSIC_DISC_FAR);
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Far");
						}
						case 6 -> {
							item = new ItemStack(Material.MUSIC_DISC_MALL);
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Mall");
						}
						case 7 -> {
							item = new ItemStack(Material.MUSIC_DISC_MELLOHI);
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Mellohi");
						}
						case 8 -> {
							item = new ItemStack(Material.MUSIC_DISC_STAL);
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Stal");
						}
						case 9 -> {
							item = new ItemStack(Material.MUSIC_DISC_STRAD);
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Strad");
						}
						case 10 -> {
							item = new ItemStack(Material.MUSIC_DISC_WAIT);
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Wait");
						}
						case 11 -> {
							item = new ItemStack(Material.MUSIC_DISC_WARD);
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Ward");
						}
					}
				}
			}
			case Dolphin ignored -> {
				if(onFire) {
					item = new ItemStack(Material.COOKED_COD);
				} else {
					item = new ItemStack(Material.COD);
				}
				item.setAmount(random.nextInt(2 + lootingLevel));
				world.dropItemNaturally(l, item);
			}
			case Drowned ignored -> {
				item = new ItemStack(Material.ROTTEN_FLESH);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(random.nextDouble() < 0.1 * rngLootingBonus) {
					item = new ItemStack(Material.COPPER_INGOT);
					world.dropItemNaturally(l, item);
				}
				if(random.nextDouble() < 0.005 * rngLootingBonus) {
					item = new ItemStack(Material.TRIDENT);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Trident");
				}
				if(random.nextDouble() < 0.03 * rngLootingBonus) {
					item = new ItemStack(Material.NAUTILUS_SHELL);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Nautilus Shell");
				}
			}
			case ElderGuardian ignored -> {
				item = new ItemStack(Material.PRISMARINE_SHARD);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				item = new ItemStack(Material.WET_SPONGE);
				item.setAmount(random.nextInt(1 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
				if(random.nextDouble() < 0.2 * rngLootingBonus) {
					item = new ItemStack(Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Tide Armor Trim");
				}
				if(random.nextDouble() < 0.333 * rngLootingBonus) {
					item = new ItemStack(Material.PRISMARINE_CRYSTALS);
					world.dropItemNaturally(l, item);
				}
			}
			case EnderDragon dragon -> {
				if(Objects.requireNonNull(dragon.getCustomName()).contains("Superior Dragon") || random.nextDouble() < 0.02 * rngLootingBonus) {
					item = SimilarData.supRemnant();
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Remnant of the Superior Dragon");
				}
			}
			case Enderman enderman -> {
				item = new ItemStack(Material.ENDER_PEARL);
				item.setAmount(random.nextInt(2 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(Objects.requireNonNull(enderman.getCustomName()).contains("Voidgloom Seraph")) {
					if(random.nextDouble() < 0.1 * rngLootingBonus) {
						item = SimilarData.core();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Judgement Core");
					}
				} else if(enderman.getCustomName().contains("Mutant Enderman")) {
					if(random.nextDouble() < 0.1 * rngLootingBonus) {
						item = SimilarData.tessellatedPearl();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Tessellated Ender Pearl");
					}
				} else {
					if((died.getWorld().getEnvironment().equals(World.Environment.THE_END) && random.nextDouble() < 0.005 * rngLootingBonus ||
							!died.getWorld().getEnvironment().equals(World.Environment.THE_END) && random.nextDouble() < 0.03 * rngLootingBonus) && p != null) {
						item = SimilarData.corruptedPearl();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Corrupted Pearl");
					}
				}
			}
			// no drops
			case Evoker ignored -> {
				item = new ItemStack(Material.TOTEM_OF_UNDYING);
				world.dropItemNaturally(l, item);
				item = new ItemStack(Material.EMERALD);
				item.setAmount(random.nextInt(2 + lootingLevel));
				world.dropItemNaturally(l, item);
			}
			// TODO fox drop whatever they're holding
			// no drops
			case Ghast ignored -> {
				item = new ItemStack(Material.GHAST_TEAR);
				item.setAmount(random.nextInt(2 + lootingLevel));
				world.dropItemNaturally(l, item);
				item = new ItemStack(Material.GUNPOWDER);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
			}
			// no drops
			case GlowSquid ignored -> {
				item = new ItemStack(Material.GLOW_INK_SAC);
				item.setAmount(random.nextInt(3 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
			}
			// no drops
			case Guardian ignored -> {
				item = new ItemStack(Material.PRISMARINE_SHARD);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(random.nextDouble() < 0.2 * rngLootingBonus) {
					item = new ItemStack(Material.PRISMARINE_CRYSTALS);
					world.dropItemNaturally(l, item);
				}
			}
			case Hoglin ignored -> {
				if(onFire) {
					item = new ItemStack(Material.COOKED_PORKCHOP);
				} else {
					item = new ItemStack(Material.PORKCHOP);
				}
				item.setAmount(random.nextInt(3 + lootingLevel) + 2);
				world.dropItemNaturally(l, item);
				item = new ItemStack(Material.LEATHER);
				item.setAmount(random.nextInt(1 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
			}
			case Husk ignored -> {
				item = new ItemStack(Material.ROTTEN_FLESH);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(random.nextDouble() < 0.01 * rngLootingBonus) {
					item = new ItemStack(Material.IRON_INGOT);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Iron Ingot");
				}
				if(random.nextDouble() < 0.01 * rngLootingBonus) {
					item = new ItemStack(Material.CARROT);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Carrot");
				}
				if(random.nextDouble() < 0.01 * rngLootingBonus) {
					item = new ItemStack(Material.POTATO);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Potato");
				}
			}
			case IronGolem golem -> {
				item = new ItemStack(Material.IRON_INGOT);
				item.setAmount(random.nextInt(3 + lootingLevel) + 3);
				world.dropItemNaturally(l, item);
				item = new ItemStack(Material.POPPY);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(Objects.requireNonNull(golem.getCustomName()).contains("meloG norI")) {
					if(random.nextDouble() < 0.1 * rngLootingBonus) {
						item = SimilarData.nullBlade();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Null Blade");
					}
				} else if(random.nextDouble() < 0.02 * rngLootingBonus && p != null) {
					item = SimilarData.antimatter();
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Antimatter");
				}
			}
			case MagmaCube cube -> {
				cube.setCustomName("Magma Cube");
				item = new ItemStack(Material.MAGMA_CREAM);
				item.setAmount(random.nextInt(1 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
				if(killer instanceof Frog f) {
					if(f.getVariant() == Frog.Variant.WARM) {
						item = new ItemStack(Material.PEARLESCENT_FROGLIGHT);
						world.dropItemNaturally(l, item);
					} else if(f.getVariant() == Frog.Variant.TEMPERATE) {
						item = new ItemStack(Material.OCHRE_FROGLIGHT);
						world.dropItemNaturally(l, item);
					} else if(f.getVariant() == Frog.Variant.COLD) {
						item = new ItemStack(Material.VERDANT_FROGLIGHT);
						world.dropItemNaturally(l, item);
					}
				}
			}
			case Parrot ignored -> {
				item = new ItemStack(Material.FEATHER);
				item.setAmount(random.nextInt(2 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
			}
			case Phantom ignored -> {
				item = new ItemStack(Material.PHANTOM_MEMBRANE);
				item.setAmount(random.nextInt(2 + lootingLevel));
				world.dropItemNaturally(l, item);
			}
			case Pig pig -> {
				if(onFire) {
					item = new ItemStack(Material.COOKED_PORKCHOP);
				} else {
					item = new ItemStack(Material.PORKCHOP);
				}
				item.setAmount(random.nextInt(3 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
				if(pig.hasSaddle()) {
					world.dropItemNaturally(l, new ItemStack(Material.SADDLE));
				}
			}
			case Piglin ignored -> {
				if(killer instanceof Creeper c && c.isPowered()) {
					item = new ItemStack(Material.PIGLIN_HEAD);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Piglin Head");
				}
			}
			// no drops
			case PigZombie ignored -> {
				item = new ItemStack(Material.ROTTEN_FLESH);
				item.setAmount(random.nextInt(2 + lootingLevel));
				world.dropItemNaturally(l, item);
				item = new ItemStack(Material.GOLD_NUGGET);
				item.setAmount(random.nextInt(2 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(random.nextDouble() < 0.02 * rngLootingBonus) {
					item = new ItemStack(Material.GOLD_INGOT);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Gold Ingot");
				}
			}
			// no drops
			// no drops
			case PufferFish ignored -> {
				item = new ItemStack(Material.PUFFERFISH);
				item.setAmount(random.nextInt(1 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
				if(random.nextDouble() < 0.05 * rngLootingBonus) {
					item = new ItemStack(Material.BONE_MEAL);
					world.dropItemNaturally(l, item);
				}
			}
			case Rabbit ignored -> {
				item = new ItemStack(Material.RABBIT_HIDE);
				item.setAmount(random.nextInt(2 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(onFire) {
					item = new ItemStack(Material.COOKED_RABBIT);
				} else {
					item = new ItemStack(Material.RABBIT);
				}
				item.setAmount(random.nextInt(1 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
				if(random.nextDouble() < 0.12 * rngLootingBonus) {
					item = new ItemStack(Material.RABBIT_HIDE);
					world.dropItemNaturally(l, item);
				}
			}
			case Ravager ignored -> {
				item = new ItemStack(Material.SADDLE);
				world.dropItemNaturally(l, item);
			}
			case Salmon ignored -> {
				if(random.nextDouble() < 0.05 * rngLootingBonus) {
					item = new ItemStack(Material.BONE_MEAL);
					world.dropItemNaturally(l, item);
				}
				if(onFire) {
					item = new ItemStack(Material.COOKED_SALMON);
				} else {
					item = new ItemStack(Material.SALMON);
				}
				item.setAmount(random.nextInt(1 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
			}
			case Sheep ignored -> {
				item = new ItemStack(Material.WHITE_WOOL);
				item.setAmount(random.nextInt(1 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
				if(onFire) {
					item = new ItemStack(Material.COOKED_MUTTON);
				} else {
					item = new ItemStack(Material.MUTTON);
				}
				item.setAmount(random.nextInt(2 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
			}
			case Shulker ignored -> {
				item = new ItemStack(Material.SHULKER_SHELL);
				item.setAmount(random.nextInt(2 + lootingLevel));
				world.dropItemNaturally(l, item);
			}
			// no drops
			case Skeleton ignored -> {
				item = new ItemStack(Material.BONE);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				item = new ItemStack(Material.ARROW);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(killer instanceof Creeper c && c.isPowered()) {
					item = new ItemStack(Material.SKELETON_SKULL);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Skeleton Skull");
				}
			}
			case Slime slime -> {
				slime.setCustomName("Slime");
				item = new ItemStack(Material.SLIME_BALL);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
			}
			// no drops
			case Snowman ignored -> {
				item = new ItemStack(Material.SNOWBALL);
				item.setAmount(random.nextInt(16 + 16 * lootingLevel));
				world.dropItemNaturally(l, item);
			}
			case Spider spider -> {
				item = new ItemStack(Material.STRING);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(random.nextDouble() < 0.4 * rngLootingBonus) {
					item = new ItemStack(Material.SPIDER_EYE);
					world.dropItemNaturally(l, item);
				}
				if(Objects.requireNonNull(spider.getCustomName()).contains("Tarantula Broodfather")) {
					if(random.nextDouble() < 0.1 * rngLootingBonus) {
						item = SimilarData.taraSilk();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Tarantula Silk");
					}
				} else if(random.nextDouble() < 0.03 * rngLootingBonus && p != null) {
					item = SimilarData.spiderRelic();
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Spider Relic");
				}
			}
			case Squid ignored -> {
				item = new ItemStack(Material.INK_SAC);
				item.setAmount(random.nextInt(3 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
			}
			case Stray ignored -> {
				if(random.nextDouble() < 0.01 * rngLootingBonus && p != null) {
					item = SimilarData.iceSpray();
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Ice Spray Wand");
				}
			}
			case Strider strider -> {
				item = new ItemStack(Material.STRING);
				item.setAmount(random.nextInt(4 + lootingLevel) + 2);
				world.dropItemNaturally(l, item);
				if(strider.hasSaddle()) {
					world.dropItemNaturally(l, new ItemStack(Material.SADDLE));
				}
			}
			// no drops
			case TropicalFish ignored -> {
				if(random.nextDouble() < 0.05 * rngLootingBonus) {
					item = new ItemStack(Material.BONE_MEAL);
					world.dropItemNaturally(l, item);
				}
				item = new ItemStack(Material.TROPICAL_FISH);
				item.setAmount(random.nextInt(1 + lootingLevel) + 2);
				world.dropItemNaturally(l, item);
			}
			case Turtle ignored -> {
				item = new ItemStack(Material.SEAGRASS);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(killer instanceof LightningStrike) {
					item = new ItemStack(Material.BOWL);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Bowl");
				}
			}
			// no drops
			// no drops
			case Vindicator ignored -> {
				item = new ItemStack(Material.EMERALD);
				item.setAmount(random.nextInt(2 + lootingLevel));
				world.dropItemNaturally(l, item);
			}
			// no loot
			case Warden ignored -> {
				item = new ItemStack(Material.SCULK_CATALYST);
				item.setAmount(random.nextInt(1 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
				if(random.nextDouble() < 0.02 * rngLootingBonus && p != null) {
					item = SimilarData.wardenHeart();
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Warden Heart");
				}
			}
			case Witch ignored -> {
				if(random.nextDouble() < 0.25 * rngLootingBonus) {
					item = new ItemStack(Material.GLOWSTONE_DUST);
					world.dropItemNaturally(l, item);
				}
				if(random.nextDouble() < 0.25 * rngLootingBonus) {
					item = new ItemStack(Material.SUGAR);
					world.dropItemNaturally(l, item);
				}
				if(random.nextDouble() < 0.25 * rngLootingBonus) {
					item = new ItemStack(Material.REDSTONE);
					world.dropItemNaturally(l, item);
				}
				if(random.nextDouble() < 0.25 * rngLootingBonus) {
					item = new ItemStack(Material.SPIDER_EYE);
					world.dropItemNaturally(l, item);
				}
				if(random.nextDouble() < 0.25 * rngLootingBonus) {
					item = new ItemStack(Material.GLASS_BOTTLE);
					world.dropItemNaturally(l, item);
				}
				if(random.nextDouble() < 0.25 * rngLootingBonus) {
					item = new ItemStack(Material.GUNPOWDER);
					world.dropItemNaturally(l, item);
				}
				if(random.nextDouble() < 0.5 * rngLootingBonus) {
					item = new ItemStack(Material.STICK);
					world.dropItemNaturally(l, item);
				}
			}
			case Wither wither -> {
				world.dropItemNaturally(l, new ItemStack(Material.NETHER_STAR));
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=wither_skull]");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=lightning_bolt]");
				if(Objects.requireNonNull(wither.getCustomName()).contains("Maxor")) {
					if(random.nextDouble() < 0.01 * rngLootingBonus) {
						item = SimilarData.maxorSecrets();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Maxor's Secrets");
					} else if(random.nextDouble() < 0.06 * rngLootingBonus) {
						item = SimilarData.shadowWarp();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Shadow Warp");
					}
				} else if(Objects.requireNonNull(wither.getCustomName()).contains("Storm")) {
					wither.getServer().dispatchCommand(Bukkit.getConsoleSender(), "weather clear");
					if(random.nextDouble() < 0.01 * rngLootingBonus) {
						item = SimilarData.stormSecrets();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Storm's Secrets");
					} else if(random.nextDouble() < 0.06 * rngLootingBonus) {
						item = SimilarData.implosion();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Implosion");
					}
				} else if(Objects.requireNonNull(wither.getCustomName()).contains("Goldor")) {
					if(random.nextDouble() < 0.01 * rngLootingBonus) {
						item = SimilarData.goldorSecrets();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Goldor's Secrets");
					} else if(random.nextDouble() < 0.06 * rngLootingBonus) {
						item = SimilarData.witherShield();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Wither Shield");
					}
				} else if(Objects.requireNonNull(wither.getCustomName()).contains("Necron")) {
					if(random.nextDouble() < 0.01 * rngLootingBonus) {
						item = SimilarData.necronSecrets();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Necron's Secrets");
					} else if(random.nextDouble() < 0.06 * rngLootingBonus) {
						item = SimilarData.handle();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Necron's Handle");
					}
				}
			}
			case WitherSkeleton skeleton -> {
				item = new ItemStack(Material.COAL);
				item.setAmount(random.nextInt(1 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
				item = new ItemStack(Material.BONE);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(skeleton.getCustomName().contains("Highly Infuriated Wither Skeleton")) {
					world.dropItemNaturally(l, new ItemStack(Material.WITHER_SKELETON_SKULL));
					sendRareDropMessage(p, "Wither Skeleton Skull");
				} else {
					if(random.nextDouble() < 0.03 * rngLootingBonus && p != null) {
						spawnInfuriation(skeleton);
					}
				}
			}
			// no drops
			case Zoglin ignored -> {
				item = new ItemStack(Material.ROTTEN_FLESH);
				item.setAmount(random.nextInt(3 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
			}
			case ZombieVillager ignored -> {
				item = new ItemStack(Material.ROTTEN_FLESH);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(random.nextDouble() < 0.01 * rngLootingBonus) {
					item = new ItemStack(Material.IRON_INGOT);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Iron Ingot");
				}
				if(random.nextDouble() < 0.01 * rngLootingBonus) {
					item = new ItemStack(Material.CARROT);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Carrot");
				}
				if(random.nextDouble() < 0.01 * rngLootingBonus) {
					item = new ItemStack(Material.POTATO);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Potato");
				}
			}
			case Zombie zombie -> {
				item = new ItemStack(Material.ROTTEN_FLESH);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(random.nextDouble() < 0.01 * rngLootingBonus) {
					item = new ItemStack(Material.IRON_INGOT);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Iron Ingot");
				}
				if(random.nextDouble() < 0.01 * rngLootingBonus) {
					item = new ItemStack(Material.CARROT);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Carrot");
				}
				if(random.nextDouble() < 0.01 * rngLootingBonus) {
					item = new ItemStack(Material.POTATO);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Potato");
				}
				if(killer instanceof Creeper c && c.isPowered()) {
					item = new ItemStack(Material.ZOMBIE_HEAD);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Zombie Head");
				}
				if(Objects.requireNonNull(zombie.getCustomName()).contains("Atoned Horror")) {
					if(random.nextDouble() < 0.1 * rngLootingBonus) {
						item = SimilarData.viscera();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Revenant Viscera");
					}
				} else if(Objects.requireNonNull(zombie.getCustomName()).contains("Sadan")) {
					if(random.nextDouble() < 0.1 * rngLootingBonus) {
						item = SimilarData.giantSwordRemnant();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Remnant of the Giant's Sword");
					}
					Objects.requireNonNull(Plugin.getInstance().getServer().getBossBar(new NamespacedKey(Plugin.getInstance(), "sadan"))).removePlayer(p);
				} else if(random.nextDouble() < 0.005 * rngLootingBonus && p != null) {
					item = SimilarData.giantZombieFlesh();
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Giant Zombie Flesh");
				} else if(random.nextDouble() < 0.01 * rngLootingBonus && p != null) {
					item = SimilarData.atonedFlesh();
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Atoned Flesh");
				}
			}
			case null, default -> {
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		LivingEntity died = e.getEntity();
		if(died instanceof Player || died instanceof ArmorStand || died instanceof AbstractHorse) {
			return;
		}
		List<ItemStack> drops = e.getDrops();
		drops.clear();
		e.setDroppedExp(e.getDroppedExp() * 2);
	}

	public static void spawnInfuriation(Entity e) {
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
		skeleton.addScoreboardTag("SkyblockBoss");
		Objects.requireNonNull(Plugin.getNearestPlayer(e)).sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "From the ashes of the Wither Skeleton rises its reincarnation: a HIGHLY INFURIATED Wither Skeleton");
	}
}