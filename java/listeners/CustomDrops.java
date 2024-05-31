package listeners;

import ingredients.*;
import items.IceSpray;
import misc.Plugin;
import mobs.InfuriatedWitherSkeleton;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import summonItems.*;

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
			case 4 -> rngLootingBonus = 1.750;
			case 5 -> rngLootingBonus = 2.000;
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
						item = BraidedFeather.getItem();
						chicken.getWorld().dropItem(chicken.getLocation(), item);
						sendRareDropMessage(p, "Braided Feather");
						Bukkit.getLogger().info(killer.getName() + " dropped a Braided Feather.");
					}
				} else if(random.nextDouble() < 0.02 * rngLootingBonus && p != null) {
					item = OmegaEgg.getItem();
					chicken.getWorld().dropItem(chicken.getLocation(), item);
					sendRareDropMessage(p, "Omega Egg");
					Bukkit.getLogger().info(killer.getName() + " dropped an Omega Egg.");
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
				}
				if(random.nextDouble() < 0.333 * rngLootingBonus) {
					item = new ItemStack(Material.PRISMARINE_CRYSTALS);
					world.dropItemNaturally(l, item);
				}
			}
			case EnderDragon dragon -> {
				if(Objects.requireNonNull(dragon.getCustomName()).contains("Superior Dragon") || random.nextDouble() < 0.02 * rngLootingBonus) {
					item = SuperiorRemnant.getItem();
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Remnant of the Superior Dragon");
					Bukkit.getLogger().info(killer.getName() + " dropped a Remnant of the Superior Dragon.");
				}
			}
			case Enderman enderman -> {
				item = new ItemStack(Material.ENDER_PEARL);
				item.setAmount(random.nextInt(2 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(Objects.requireNonNull(enderman.getCustomName()).contains("Voidgloom Seraph")) {
					if(random.nextDouble() < 0.1 * rngLootingBonus) {
						item = Core.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Judgement Core");
						Bukkit.getLogger().info(killer.getName() + " dropped a Judgement Core.");
					}
				} else if(enderman.getCustomName().contains("Mutant Enderman")) {
					if(random.nextDouble() < 0.1 * rngLootingBonus) {
						item = TessellatedPearl.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Tessellated Ender Pearl");
						Bukkit.getLogger().info(killer.getName() + " dropped a Tessellated Ender Pearl.");
					}
				} else {
					if((died.getWorld().getEnvironment().equals(World.Environment.THE_END) && random.nextDouble() < 0.005 * rngLootingBonus ||
							!died.getWorld().getEnvironment().equals(World.Environment.THE_END) && random.nextDouble() < 0.03 * rngLootingBonus) && p != null) {
						item = CorruptPearl.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Corrupted Pearl");
						Bukkit.getLogger().info(killer.getName() + " dropped a Corrupted Pearl.");
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
						item = NullBlade.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Null Blade");
						Bukkit.getLogger().info(killer.getName() + " dropped a Null Blade.");
					}
				} else if(random.nextDouble() < 0.02 * rngLootingBonus && p != null) {
					item = Antimatter.getItem();
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Antimatter");
					Bukkit.getLogger().info(killer.getName() + " dropped an Antimatter.");
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
						item = TarantulaSilk.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Tarantula Silk");
						Bukkit.getLogger().info(killer.getName() + " dropped a Tarantula Silk.");
					}
				} else if(random.nextDouble() < 0.03 * rngLootingBonus && p != null) {
					item = SpiderRelic.getItem();
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Spider Relic");
					Bukkit.getLogger().info(killer.getName() + " dropped a Spider Relic.");
				}
			}
			case Squid ignored -> {
				item = new ItemStack(Material.INK_SAC);
				item.setAmount(random.nextInt(3 + lootingLevel) + 1);
				world.dropItemNaturally(l, item);
			}
			case Stray ignored -> {
				if(random.nextDouble() < 0.01 * rngLootingBonus && p != null) {
					item = IceSpray.getItem();
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Ice Spray Wand");
					Bukkit.getLogger().info(killer.getName() + " dropped an Ice Spray Wand.");
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
					item = WardenHeart.getItem();
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Warden Heart");
					Bukkit.getLogger().info(killer.getName() + " dropped a Warden Heart.");
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
						item = MaxorSecrets.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Maxor's Secrets");
						Bukkit.getLogger().info(killer.getName() + " dropped a Maxor's Secrets.");
					} else if(random.nextDouble() < 0.06 * rngLootingBonus) {
						item = ShadowWarp.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Shadow Warp");
						Bukkit.getLogger().info(killer.getName() + " dropped a Shadow Warp.");
					}
				} else if(Objects.requireNonNull(wither.getCustomName()).contains("Storm")) {
					wither.getServer().dispatchCommand(Bukkit.getConsoleSender(), "weather clear");
					if(random.nextDouble() < 0.01 * rngLootingBonus) {
						item = StormSecrets.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Storm's Secrets");
						Bukkit.getLogger().info(killer.getName() + " dropped a Storm's Secrets.");
					} else if(random.nextDouble() < 0.06 * rngLootingBonus) {
						item = Implosion.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Implosion");
						Bukkit.getLogger().info(killer.getName() + " dropped an Implosion.");
					}
				} else if(Objects.requireNonNull(wither.getCustomName()).contains("Goldor")) {
					if(random.nextDouble() < 0.01 * rngLootingBonus) {
						item = GoldorSecrets.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Goldor's Secrets");
						Bukkit.getLogger().info(killer.getName() + " dropped a Goldor's Secrets.");
					} else if(random.nextDouble() < 0.06 * rngLootingBonus) {
						item = WitherShield.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Wither Shield");
						Bukkit.getLogger().info(killer.getName() + " dropped a Wither Shield.");
					}
				} else if(Objects.requireNonNull(wither.getCustomName()).contains("Necron")) {
					if(random.nextDouble() < 0.01 * rngLootingBonus) {
						item = NecronSecrets.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Necron's Secrets");
						Bukkit.getLogger().info(killer.getName() + " dropped a Necron's Secrets.");
					} else if(random.nextDouble() < 0.06 * rngLootingBonus) {
						item = Handle.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Necron's Handle");
						Bukkit.getLogger().info(killer.getName() + " dropped a Necron's Handle.");
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
						new InfuriatedWitherSkeleton().onSpawn(p, skeleton);
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
						item = Viscera.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Revenant Viscera");
						Bukkit.getLogger().info(killer.getName() + " dropped a Revenant Viscera.");
					}
				} else if(Objects.requireNonNull(zombie.getCustomName()).contains("Sadan")) {
					if(random.nextDouble() < 0.1 * rngLootingBonus) {
						item = GiantSwordRemnant.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Remnant of the Giant's Sword");
						Bukkit.getLogger().info(killer.getName() + " dropped a Remnant of the Giant's Sword.");
					}
					Objects.requireNonNull(Plugin.getInstance().getServer().getBossBar(new NamespacedKey(Plugin.getInstance(), "sadan"))).removePlayer(p);
				} else if(random.nextDouble() < 0.005 * rngLootingBonus && p != null) {
					item = GiantZombieFlesh.getItem();
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Giant Zombie Flesh");
					Bukkit.getLogger().info(killer.getName() + " dropped a Giant Zombie Flesh.");
				} else if(random.nextDouble() < 0.01 * rngLootingBonus && p != null) {
					item = AtonedFlesh.getItem();
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Atoned Flesh");
					Bukkit.getLogger().info(killer.getName() + " dropped an Atoned Flesh.");
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
		if(died.getScoreboardTags().contains("SkyblockBoss")) {
			e.setDroppedExp(e.getDroppedExp() * 10);
		}
	}
}