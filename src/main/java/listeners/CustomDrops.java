package listeners;

import items.armor.WitherKingCrown;
import items.ingredients.misc.*;
import items.ingredients.witherLords.*;
import items.misc.IceSpray;
import items.summonItems.*;
import misc.BossBarManager;
import misc.Plugin;
import misc.Utils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_21_R7.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

import static misc.Utils.sendRareDropMessage;

@SuppressWarnings({"DataFlowIssue"})
public class CustomDrops implements Listener {
	public static void loot(LivingEntity died, Entity killer) {
		Player p;
		int lootingLevel = 0;
		if(killer instanceof Player p1) {
			p = p1;
			try {
				lootingLevel = Math.min(5, p1.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOTING));
			} catch(Exception exception) {
				// do nothing
			}
		} else {
			p = Utils.getNearestPlayer(died);
			if(p != null && p.getLocation().distanceSquared(died.getLocation()) > 256) {
				p = null;
			}
		}
		double rngLootingBonus = 1.0;
		switch(lootingLevel) {
			case 1 -> rngLootingBonus = 1.15;
			case 2 -> rngLootingBonus = 1.30;
			case 3 -> rngLootingBonus = 1.50;
			case 4 -> rngLootingBonus = 1.70;
			case 5 -> rngLootingBonus = 2.00;
		}
		Random random = new Random();
		World world = died.getWorld();
		Location l = died.getLocation();
		boolean onFire = died.getFireTicks() > 0;
		ItemStack item;
		boolean hardMode = died.getScoreboardTags().contains("HardMode");
		switch(died) {
			case Blaze ignored -> {
				item = new ItemStack(Material.BLAZE_ROD);
				item.setAmount(random.nextInt(2 + lootingLevel));
				world.dropItemNaturally(l, item);
			}
			case Bogged ignored -> {
				item = new ItemStack(Material.BONE);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				item = new ItemStack(Material.ARROW);
				item.setAmount(random.nextInt(3 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(random.nextDouble() < 0.5 * lootingLevel) {
					Arrow arrow = (Arrow) new ItemStack(Material.ARROW);
					arrow.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 100, 0), true);
					world.dropItemNaturally(l, (ItemStack) arrow);
				}
				if(killer instanceof Creeper c && c.isPowered()) {
					item = new ItemStack(Material.SKELETON_SKULL);
					world.dropItemNaturally(l, item);
					sendRareDropMessage(p, "Skeleton Skull");
				}
			}
			case Breeze ignored -> {
				item = new ItemStack(Material.BREEZE_ROD);
				item.setAmount(random.nextInt(2 + lootingLevel));
				world.dropItemNaturally(l, item);
			}
			// Camel has no drops
			case CamelHusk ignored -> {
				item = new ItemStack(Material.ROTTEN_FLESH);
				item.setAmount(random.nextInt(2 + lootingLevel) + 2);
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
				if(chicken.getScoreboardTags().contains("Chickzilla")) {
					if(random.nextDouble() < 0.05 * rngLootingBonus) {
						item = BraidedFeather.getItem();
						chicken.getWorld().dropItem(chicken.getLocation(), item);
						sendRareDropMessage(p, "Braided Feather");
					}
					Plugin.getAdvancementAPI().getAdvancement("skyblock:defeat_chickzilla").incrementProgression(p);
				} else if(chicken.getScoreboardTags().contains("EnragedChickzilla")) {
					if(random.nextDouble() < 0.25 * rngLootingBonus) {
						item = BraidedFeather.getItem();
						chicken.getWorld().dropItem(chicken.getLocation(), item);
						sendRareDropMessage(p, "Braided Feather");
					}
					Plugin.getAdvancementAPI().getAdvancement("skyblock:defeat_hard_chickzilla").incrementProgression(p);
				} else if(random.nextDouble() < 0.02 * rngLootingBonus && p != null) {
					item = OmegaEgg.getItem();
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
			case CopperGolem ignored -> {
				item = new ItemStack(Material.COPPER_INGOT);
				item.setAmount(random.nextInt(3 + lootingLevel) + 1);
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
			// Creaking has no drops
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
//				if(random.nextDouble() < 0.005 * rngLootingBonus) {
//					item = new ItemStack(Material.TRIDENT);
//					world.dropItemNaturally(l, item);
//					sendRareDropMessage(p, "Trident");
//				}
//				if(random.nextDouble() < 0.03 * rngLootingBonus) {
//					item = new ItemStack(Material.NAUTILUS_SHELL);
//					world.dropItemNaturally(l, item);
//					sendRareDropMessage(p, "Nautilus Shell");
//				}
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
				if(!dragon.getScoreboardTags().contains("WitherKingDragon")) {
					if(dragon.getScoreboardTags().contains("HardMode")) {
						item = new ItemStack(Material.DRAGON_EGG);
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Dragon Egg");
						item = SuperiorRemnant.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Remnant of the Superior Dragon");
						if(random.nextDouble() < 0.25 * rngLootingBonus) {
							item = AncientDragonEgg.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Ancient Dragon Egg");
						}
					} else if(dragon.getScoreboardTags().contains("SuperiorDragon") || random.nextDouble() < 0.02 * rngLootingBonus) {
						item = SuperiorRemnant.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Remnant of the Superior Dragon");
					}
				}
			}
			case Enderman enderman -> {
				item = new ItemStack(Material.ENDER_PEARL);
				item.setAmount(random.nextInt(2 + lootingLevel));
				world.dropItemNaturally(l, item);
				if(enderman.getScoreboardTags().contains("VoidgloomSeraph")) {
					if(random.nextDouble() < 0.05 * rngLootingBonus) {
						item = Core.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Judgement Core");
					}
					if(random.nextDouble() < 0.1 * rngLootingBonus) {
						item = NullOvoid.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Null Ovoid");
					}
					Plugin.getAdvancementAPI().getAdvancement("skyblock:defeat_voidgloom_seraph").incrementProgression(p);
				} else if(enderman.getScoreboardTags().contains("VoidcrazedSeraph")) {
					if(random.nextDouble() < 0.25 * rngLootingBonus) {
						item = Core.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Judgement Core");
					}
					if(random.nextDouble() < 0.5 * rngLootingBonus) {
						item = NullOvoid.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Null Ovoid");
					}
					Plugin.getAdvancementAPI().getAdvancement("skyblock:defeat_voidcrazed_seraph").incrementProgression(p);
				} else if(enderman.getScoreboardTags().contains("Zealot")) {
					if(random.nextDouble() < 0.05 * rngLootingBonus) {
						item = TessellatedPearl.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Tessellated Ender Pearl");
					}
					Plugin.getAdvancementAPI().getAdvancement("skyblock:defeat_zealot").incrementProgression(p);
				} else if(enderman.getScoreboardTags().contains("ZealotBrusier")) {
					if(random.nextDouble() < 0.25 * rngLootingBonus) {
						item = TessellatedPearl.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Tessellated Ender Pearl");
					}
					Plugin.getAdvancementAPI().getAdvancement("skyblock:defeat_zealot_brusier").incrementProgression(p);
				} else {
					if((died.getWorld().getEnvironment().equals(World.Environment.THE_END) && random.nextDouble() < 0.005 * rngLootingBonus ||
							!died.getWorld().getEnvironment().equals(World.Environment.THE_END) && random.nextDouble() < 0.03 * rngLootingBonus) && p != null) {
						item = CorruptPearl.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Corrupted Pearl");
					}
				}
			}
			// no drops
			case Evoker ignored -> {
				item = new ItemStack(Material.TOTEM_OF_UNDYING);
				if(lootingLevel == 5) {
					item.setAmount(2);
				}
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
			case HappyGhast ghast -> {
				item = ghast.getEquipment().getItem(EquipmentSlot.BODY);
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
				if(!golem.getScoreboardTags().contains("WokeGolem")) {
					if(golem.getScoreboardTags().contains("meloGnorI")) {
						if(random.nextDouble() < 0.05 * rngLootingBonus) {
							item = NullBlade.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Null Blade");
						}
						Plugin.getAdvancementAPI().getAdvancement("skyblock:defeat_melog_nori").incrementProgression(p);
					} else if(golem.getScoreboardTags().contains("ObfuscatedmeloGnorI")) {
						if(random.nextDouble() < 0.25 * rngLootingBonus) {
							item = NullBlade.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Null Blade");
						}
						Plugin.getAdvancementAPI().getAdvancement("skyblock:defeat_hard_melog_nori").incrementProgression(p);
					} else if(random.nextDouble() < 0.02 * rngLootingBonus && p != null) {
						item = Antimatter.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Antimatter");
					}
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
			case Nautilus ignored -> {
				if(random.nextDouble() < 0.05 * rngLootingBonus) {
					item = new ItemStack(Material.NAUTILUS_SHELL);
					world.dropItemNaturally(l, item);
				}
			}
			case Parched ignored -> {
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
			case Pillager pillager -> {
				if(pillager.getEquipment().getHelmet() != null && pillager.getEquipment().getHelmet().getType().equals(Material.WHITE_BANNER)) {
					Raid activeRaid = pillager.getRaid();
					if(activeRaid == null) {
						item = new ItemStack(Material.OMINOUS_BOTTLE);
						int randomLevel = random.nextInt(5);
						net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
						nmsStack.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, new OminousBottleAmplifier(randomLevel));
						item = CraftItemStack.asBukkitCopy(nmsStack);
						world.dropItemNaturally(l, item);
					}
					item = new ItemStack(Material.WHITE_BANNER);
					BannerMeta meta = (BannerMeta) item.getItemMeta();

					// Add all the banner patterns in the correct order
					meta.addPattern(new Pattern(DyeColor.CYAN, PatternType.RHOMBUS));
					meta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.STRIPE_BOTTOM));
					meta.addPattern(new Pattern(DyeColor.GRAY, PatternType.STRIPE_CENTER));
					meta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.HALF_HORIZONTAL));
					meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
					meta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.HALF_HORIZONTAL));
					meta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.CIRCLE));
					meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));

					// Set custom name (gold color, not italic)
					meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.ITALIC + "Ominous Banner");

					// Hide additional tooltip
					meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

					item.setItemMeta(meta);
					world.dropItemNaturally(l, item);
				}
			}
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
			case Sheep sheep -> {
				switch(sheep.getColor()) {
					case DyeColor.BLACK -> item = new ItemStack(Material.BLACK_WOOL);
					case DyeColor.BLUE -> item = new ItemStack(Material.BLUE_WOOL);
					case DyeColor.GREEN -> item = new ItemStack(Material.GREEN_WOOL);
					case DyeColor.CYAN -> item = new ItemStack(Material.CYAN_WOOL);
					case DyeColor.RED -> item = new ItemStack(Material.RED_WOOL);
					case DyeColor.PURPLE -> item = new ItemStack(Material.PURPLE_WOOL);
					case DyeColor.ORANGE -> item = new ItemStack(Material.ORANGE_WOOL);
					case DyeColor.LIGHT_GRAY -> item = new ItemStack(Material.LIGHT_GRAY_WOOL);
					case DyeColor.GRAY -> item = new ItemStack(Material.GRAY_WOOL);
					case DyeColor.LIGHT_BLUE -> item = new ItemStack(Material.LIGHT_BLUE_WOOL);
					case DyeColor.LIME -> item = new ItemStack(Material.LIME_WOOL);
					case DyeColor.BROWN -> item = new ItemStack(Material.BROWN_WOOL);
					case DyeColor.PINK -> item = new ItemStack(Material.PINK_WOOL);
					case DyeColor.MAGENTA -> item = new ItemStack(Material.MAGENTA_WOOL);
					case DyeColor.YELLOW -> item = new ItemStack(Material.YELLOW_WOOL);
					default -> item = new ItemStack(Material.WHITE_WOOL);
				}
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
				if(spider.getScoreboardTags().contains("TarantulaBroodfather")) {
					if(random.nextDouble() < 0.05 * rngLootingBonus) {
						item = TarantulaSilk.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Tarantula Silk");
					}
					Plugin.getAdvancementAPI().getAdvancement("skyblock:defeat_tarantula_broodfather").incrementProgression(p);
				} else if(spider.getScoreboardTags().contains("ConjoinedBrood")) {
					if(random.nextDouble() < 0.25 * rngLootingBonus) {
						item = TarantulaSilk.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Tarantula Silk");
					}
					Plugin.getAdvancementAPI().getAdvancement("skyblock:defeat_primordial_broodfather").incrementProgression(p);
				} else if(random.nextDouble() < 0.03 * rngLootingBonus && p != null) {
					item = SpiderRelic.getItem();
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
					item = IceSpray.getItem();
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
					item = WardenHeart.getItem();
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
				double multiplier = 1;
				if(hardMode) {
					multiplier = 2;
				}
				if(wither.getScoreboardTags().contains("Maxor")) {
					if(random.nextDouble() < 0.025 * rngLootingBonus * multiplier) {
						item = MaxorSecrets.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Maxor's Secrets");
					} else if(random.nextDouble() < 0.075 * rngLootingBonus * multiplier) {
						item = ShadowWarp.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Shadow Warp");
					}
				} else if(wither.getScoreboardTags().contains("Storm")) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=lightning_bolt]");
					wither.getWorld().setThundering(false);
					if(random.nextDouble() < 0.025 * rngLootingBonus * multiplier) {
						item = StormSecrets.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Storm's Secrets");
					} else if(random.nextDouble() < 0.075 * rngLootingBonus * multiplier) {
						item = Implosion.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Implosion");
					}
				} else if(wither.getScoreboardTags().contains("Goldor")) {
					if(random.nextDouble() < 0.025 * rngLootingBonus * multiplier) {
						item = GoldorSecrets.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Goldor's Secrets");
					} else if(random.nextDouble() < 0.075 * rngLootingBonus * multiplier) {
						item = WitherShield.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Wither Shield");
					}
				} else if(wither.getScoreboardTags().contains("Necron")) {
					if(random.nextDouble() < 0.025 * rngLootingBonus * multiplier) {
						item = NecronSecrets.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Necron's Secrets");
					} else if(random.nextDouble() < 0.075 * rngLootingBonus * multiplier) {
						item = Handle.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Necron's Handle");
					}
				} else if(wither.getScoreboardTags().contains("WitherKing")) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=lightning_bolt]");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[tag=WitherKingDragon]");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[tag=GuardSkeleton]");
					wither.getWorld().setThundering(false);
					for(int i = 0; i < 1 * multiplier; i++) {
						if(random.nextDouble() < 0.04 * rngLootingBonus) {
							item = MaxorSecrets.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Maxor's Secrets");
						} else if(random.nextDouble() < 0.12 * rngLootingBonus) {
							item = ShadowWarp.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Shadow Warp");
						} else if(random.nextDouble() < 0.16 * rngLootingBonus) {
							item = StormSecrets.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Storm's Secrets");
						} else if(random.nextDouble() < 0.24 * rngLootingBonus) {
							item = Implosion.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Implosion");
						} else if(random.nextDouble() < 0.28 * rngLootingBonus) {
							item = GoldorSecrets.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Goldor's Secrets");
						} else if(random.nextDouble() < 0.36 * rngLootingBonus) {
							item = WitherShield.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Wither Shield");
						} else if(random.nextDouble() < 0.4 * rngLootingBonus) {
							item = NecronSecrets.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Necron's Secrets");
						} else if(random.nextDouble() < 0.48 * rngLootingBonus) {
							item = Handle.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Necron's Handle");
						} else if(random.nextDouble() < 0.5 * rngLootingBonus) {
							item = WitherKingCrown.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Crown of the Wither King");
						}
					}
				}
			}
			case WitherSkeleton skeleton -> {
				if(!skeleton.getScoreboardTags().contains("GuardSkeleton")) {
					item = new ItemStack(Material.COAL);
					item.setAmount(random.nextInt(1 + lootingLevel) + 1);
					world.dropItemNaturally(l, item);
					item = new ItemStack(Material.BONE);
					item.setAmount(random.nextInt(3 + lootingLevel));
					world.dropItemNaturally(l, item);
					if(skeleton.getScoreboardTags().contains("InfuriatedSkeleton")) {
						world.dropItemNaturally(l, new ItemStack(Material.WITHER_SKELETON_SKULL));
						sendRareDropMessage(p, "Wither Skeleton Skull");
					} else {
						if(random.nextDouble() < 0.03 * rngLootingBonus && p != null) {
							item = HighlyInfuriatedWitherSkeletonSpawnEgg.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Highly Infuriated Wither Skeleton Spawn Egg");
						}
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
				if(!zombie.getScoreboardTags().contains("MutantGiant")) {
					if(zombie.getScoreboardTags().contains("RevenantHorror")) {
						if(random.nextDouble() < 0.05 * rngLootingBonus) {
							item = Viscera.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Revenant Viscera");
						}
						Plugin.getAdvancementAPI().getAdvancement("skyblock:defeat_revenant_horror").incrementProgression(p);
					} else if(zombie.getScoreboardTags().contains("AtonedHorror")) {
						if(random.nextDouble() < 0.25 * rngLootingBonus) {
							item = Viscera.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Revenant Viscera");
						}
						Plugin.getAdvancementAPI().getAdvancement("skyblock:defeat_atoned_horror").incrementProgression(p);
					} else if(zombie.getScoreboardTags().contains("Sadan")) {
						if(random.nextDouble() < 0.05 * rngLootingBonus) {
							item = GiantSwordRemnant.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Remnant of the Giant's Sword");
						}
						BossBarManager.removeBossBar(died);
						Plugin.getAdvancementAPI().getAdvancement("skyblock:defeat_sadan").incrementProgression(p);
					} else if(zombie.getScoreboardTags().contains("TheGiantOne")) {
						if(random.nextDouble() < 0.25 * rngLootingBonus) {
							item = GiantSwordRemnant.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Remnant of the Giant's Sword");
						}
						if(random.nextDouble() < 0.25 * rngLootingBonus) {
							item = NecromancerBrooch.getItem();
							world.dropItemNaturally(l, item);
							sendRareDropMessage(p, "Necromancer's Brooch");
						}
						BossBarManager.removeBossBar(died);
						Plugin.getAdvancementAPI().getAdvancement("skyblock:defeat_hard_sadan").incrementProgression(p);
						Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Sadan" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": NOOOOOOOOOO!!! THIS IS IMPOSSIBLE!!");
						Utils.scheduleTask(() -> Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Sadan" + ChatColor.GOLD + ChatColor.BOLD + " ﴿" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + ": FATHER, FORGIVE ME!!!"), 60);
					} else if(random.nextDouble() < 0.005 * rngLootingBonus && p != null) {
						item = GiantZombieFlesh.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Giant Zombie Flesh");
					} else if(random.nextDouble() < 0.01 * rngLootingBonus && p != null) {
						item = AtonedFlesh.getItem();
						world.dropItemNaturally(l, item);
						sendRareDropMessage(p, "Atoned Flesh");
					}
				}
			}
			case ZombieNautilus ignored -> {
				item = new ItemStack(Material.ROTTEN_FLESH);
				item.setAmount(random.nextInt(4 + lootingLevel));
				world.dropItemNaturally(l, item);
			}
			case null, default -> {
			}
		}

		if(!(died instanceof Player)) {
			EntityEquipment equipment = died.getEquipment();
			if(random.nextDouble() < equipment.getItemInMainHandDropChance()) {
				world.dropItemNaturally(l, equipment.getItemInMainHand());
				if(died instanceof Drowned) {
					if(equipment.getItemInMainHand().getType().equals(Material.TRIDENT)) {
						sendRareDropMessage(p, "Trident");
					} else if(equipment.getItemInMainHand().getType().equals(Material.NAUTILUS_SHELL)) {
						sendRareDropMessage(p, "Nautilus Shell");
					}
				}
			}
			if(random.nextDouble() < equipment.getItemInOffHandDropChance()) {
				world.dropItemNaturally(l, equipment.getItemInOffHand());
				if(died instanceof Drowned) {
					if(equipment.getItemInOffHand().getType().equals(Material.TRIDENT)) {
						sendRareDropMessage(p, "Trident");
					} else if(equipment.getItemInOffHand().getType().equals(Material.NAUTILUS_SHELL)) {
						sendRareDropMessage(p, "Nautilus Shell");
					}
				}
			}
			if(random.nextDouble() < equipment.getHelmetDropChance()) {
				world.dropItemNaturally(l, equipment.getHelmet());
			}
			if(random.nextDouble() < equipment.getChestplateDropChance()) {
				world.dropItemNaturally(l, equipment.getChestplate());
			}
			if(random.nextDouble() < equipment.getLeggingsDropChance()) {
				world.dropItemNaturally(l, equipment.getLeggings());
			}
			if(random.nextDouble() < equipment.getBootsDropChance()) {
				world.dropItemNaturally(l, equipment.getBoots());
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		LivingEntity died = e.getEntity();
		if(e instanceof PlayerDeathEvent playerDeath) {
			playerDeath.setDeathMessage("");
			return;
		}
		if(died instanceof Player || died instanceof ArmorStand || died instanceof AbstractHorse) {
			return;
		}
		List<ItemStack> drops = e.getDrops();
		drops.clear();
		e.setDroppedExp(calculateMobXP(died));

		Player p;
		if(e.getEntity().getKiller() != null) {
			p = e.getEntity().getKiller();
		} else {
			p = Utils.getNearestPlayer(died);
			if(p != null && p.getLocation().distanceSquared(died.getLocation()) > 256) {
				p = null;
			}
		}

		if(died.getScoreboardTags().contains("HardMode")) {
			e.setDroppedExp(e.getDroppedExp() * 100);
		} else if(died.getScoreboardTags().contains("SkyblockBoss")) {
			e.setDroppedExp(e.getDroppedExp() * 10);
		} else if(p == null) {
			e.setDroppedExp(0);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(e.getDeathMessage().contains(" died")) {
			e.setDeathMessage("");
		}
	}

	public static int calculateMobXP(LivingEntity mob) {
		return switch(mob.getType()) {
			// Hostile Mobs - 5 XP
			case ZOMBIE, HUSK, ZOMBIE_VILLAGER, DROWNED -> 5;
			case SKELETON, STRAY, WITHER_SKELETON -> 5;
			case CREEPER -> 5;
			case SPIDER, CAVE_SPIDER -> 5;
			case ENDERMAN -> 5;
			case WITCH -> 5;
			case SILVERFISH -> 5;
			case ENDERMITE -> 5;
			case GUARDIAN, ELDER_GUARDIAN, BLAZE -> 10;
			case SHULKER -> 5;
			case VEX -> 3;
			case VINDICATOR, EVOKER, PILLAGER, RAVAGER -> 5;
			case PHANTOM -> 5;
			case GHAST -> 5;
			case MAGMA_CUBE -> mob instanceof MagmaCube m ? m.getSize() : 4;
			case SLIME -> mob instanceof Slime s ? s.getSize() : 4;
			case ZOMBIFIED_PIGLIN, PIGLIN, PIGLIN_BRUTE -> 5;
			case HOGLIN, ZOGLIN -> 5;
			case STRIDER -> 5;
			case WARDEN -> 5;

			// Bosses - Special XP
			case ENDER_DRAGON -> 6400;
			case WITHER -> 50;

			// Neutral Mobs - 1-3 XP
			case WOLF, POLAR_BEAR -> 1;
			case BEE -> 1;
			case PANDA -> 1;
			case LLAMA, TRADER_LLAMA -> 1;
			case DOLPHIN -> 1;
			case IRON_GOLEM -> 0; // Iron golems don't drop XP naturally
			case SNOW_GOLEM -> 0; // Snow golems don't drop XP

			// Ocean Mobs
			case COD, SALMON, TROPICAL_FISH, PUFFERFISH -> 1;
			case SQUID, GLOW_SQUID -> 1;

			// Farm Animals - 1-3 XP
			case COW, SHEEP, PIG, CHICKEN -> 1;
			case HORSE, DONKEY, MULE, SKELETON_HORSE, ZOMBIE_HORSE -> 1;
			case RABBIT -> 1;
			case OCELOT, CAT -> 1;
			case PARROT -> 1;
			case TURTLE -> 1;
			case FOX -> 1;
			case GOAT -> 1;
			case AXOLOTL -> 1;
			case FROG -> 1;
			case TADPOLE -> 1;
			case CAMEL -> 1;
			case SNIFFER -> 1;
			case ARMADILLO -> 1;

			// Special Cases
			case VILLAGER -> 0; // Villagers don't drop XP when killed
			case WANDERING_TRADER -> 0; // Wandering traders don't drop XP

			default -> 0; // Entities that don't drop XP (like armor stands, boats, etc.)
		};
	}
}