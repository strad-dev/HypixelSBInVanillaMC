package listeners;

import misc.Plugin;
import misc.SimilarData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

@SuppressWarnings("DataFlowIssue")
public class CustomItemUses implements Listener {
	private Player p;

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		p = e.getPlayer();
		PlayerInventory inventory = p.getInventory();
		ItemStack item = inventory.getItemInMainHand();

		// SUP REMNANT
		ItemStack supRemnant = new ItemStack(Material.QUARTZ);
		supRemnant.setItemMeta(SimilarData.supRemnantMeta(supRemnant.getItemMeta()));
		supRemnant.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		// CORRUPTED PEARL
		ItemStack corruptPearl = new ItemStack(Material.ENDER_EYE);
		corruptPearl.setItemMeta(SimilarData.corruptedPearlMeta(corruptPearl.getItemMeta()));
		corruptPearl.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		// ANTIMATTER
		ItemStack antimatter = new ItemStack(Material.WARPED_FUNGUS);
		antimatter.setItemMeta(SimilarData.antimatterMeta(antimatter.getItemMeta()));
		antimatter.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		// OMEGA EGG
		ItemStack omegaEgg = new ItemStack(Material.EGG);
		omegaEgg.setItemMeta(SimilarData.omegaEggMeta(omegaEgg.getItemMeta()));
		omegaEgg.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		// SPIDER RELIC
		ItemStack spiderRelic = new ItemStack(Material.FERMENTED_SPIDER_EYE);
		spiderRelic.setItemMeta(SimilarData.spiderRelicMeta(spiderRelic.getItemMeta()));
		spiderRelic.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		if(e.getRightClicked() instanceof LivingEntity entity) {
			String entityName = entity.getCustomName();
			if(entityName == null) {
				entityName = "";
			}
			if(entity instanceof Enderman enderman && !entityName.contains("Voidgloom Seraph") && !entityName.contains("Mutant Enderman")) {
				if(item.isSimilar(supRemnant)) {
					spawnVoidgloomSeraph(enderman);
					item.setAmount(item.getAmount() - 1);
				} else if(item.isSimilar(corruptPearl)) {
					spawnMutantEnderman(enderman);
					item.setAmount(item.getAmount() - 1);
				}
			} else if(entity instanceof IronGolem golem && !entityName.contains("meloG norI")) {
				if(item.isSimilar(antimatter)) {
					spawnmeloGnorI(golem);
					item.setAmount(item.getAmount() - 1);
				}
			} else if(entity instanceof Chicken chicken && !entityName.contains("Chickzilla")) {
				if(item.isSimilar(omegaEgg)) {
					spawnChickzilla(chicken);
					item.setAmount(item.getAmount() - 1);
				}
			} else if(entity instanceof Spider spider && !entityName.contains("Tarantula Broodfather")) {
				if(item.isSimilar(spiderRelic)) {
					spawnTarantulaBroodfather(spider);
					item.setAmount(item.getAmount() - 1);
				}
			}
		}
	}

	public void spawnVoidgloomSeraph(Enderman enderman) {
		enderman.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Voidgloom Seraph" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
		enderman.setHealth(40.0);
		Objects.requireNonNull(enderman.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(20.0);
		Objects.requireNonNull(enderman.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(4.0);
		Objects.requireNonNull(enderman.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.9);
		enderman.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		enderman.setTarget(Plugin.getNearestPlayer(enderman));
		enderman.setCustomNameVisible(true);
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The remains of the Superior Dragon has drawn the attention of the Voidgloom Seraph!  Defeat it before it's too late!");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
	}

	public void spawnMutantEnderman(Enderman enderman) {
		enderman.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Mutant Enderman" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
		enderman.setHealth(40.0);
		Objects.requireNonNull(enderman.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(6.25);
		Objects.requireNonNull(enderman.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(6.25);
		Objects.requireNonNull(enderman.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.6);
		enderman.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		enderman.setTarget(Plugin.getNearestPlayer(enderman));
		enderman.setCustomNameVisible(true);
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Pearl corrupts the Enderman.  It has become a Mutated Enderman!");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
	}

	public void spawnmeloGnorI(IronGolem golem) {
		golem.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "meloG norI" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
		Objects.requireNonNull(golem.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(200.0);
		golem.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		golem.setTarget(Plugin.getNearestPlayer(golem));
		golem.setHealth(200.0);
		golem.setCustomNameVisible(true);
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Antimatter has done strange things to this Iron Golem...");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
	}

	public void spawnChickzilla(Chicken chicken) {
		chicken.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Chickzilla" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
		chicken.setHealth(4.0);
		Objects.requireNonNull(chicken.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(20.0);
		Objects.requireNonNull(chicken.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)).setBaseValue(20.0);
		chicken.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, -1, 3));
		chicken.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		chicken.setTarget(Plugin.getNearestPlayer(chicken));
		chicken.setCustomNameVisible(true);
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Omega Egg hatches into the Chickzilla!");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
	}

	public void spawnTarantulaBroodfather(Spider spider) {
		spider.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Tarantula Broodfather" + ChatColor.GOLD + ChatColor.BOLD + " ﴿");
		spider.setHealth(16.0);
		Objects.requireNonNull(spider.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(1.2);
		spider.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		spider.setTarget(Plugin.getNearestPlayer(spider));
		spider.setCustomNameVisible(true);
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Spider Relic draws the attention of the Tarantula Broodfather!");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
	}
}