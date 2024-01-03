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
	private String newName;

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
			boolean changeName = false;
			String entityName = entity.getCustomName();
			if(entityName == null) {
				entityName = "";
			}
			if(entity instanceof Enderman enderman && !entityName.contains("Voidgloom Seraph") && !entityName.contains("Mutant Enderman")) {
				if(item.isSimilar(supRemnant)) {
					spawnVoidgloomSeraph(enderman);
					item.setAmount(item.getAmount() - 1);
					changeName = true;
				} else if(item.isSimilar(corruptPearl)) {
					spawnMutantEnderman(enderman);
					item.setAmount(item.getAmount() - 1);
					changeName = true;
				}
			} else if(entity instanceof IronGolem golem && !entityName.contains("meloG norI")) {
				if(item.isSimilar(antimatter)) {
					spawnmeloGnorI(golem);
					item.setAmount(item.getAmount() - 1);
					changeName = true;
				}
			} else if(entity instanceof Chicken chicken && !entityName.contains("Chickzilla")) {
				if(item.isSimilar(omegaEgg)) {
					spawnChickzilla(chicken);
					item.setAmount(item.getAmount() - 1);
					changeName = true;
				}
			} else if(entity instanceof Spider spider && !entityName.contains("Tarantula Broodfather")) {
				if(item.isSimilar(spiderRelic)) {
					spawnTarantulaBroodfather(spider);
					item.setAmount(item.getAmount() - 1);
					changeName = true;
				}
			}

			// add health to the entity name
			if(changeName) {
				int health = (int) (entity.getHealth() + entity.getAbsorptionAmount());
				int maxHealth = (int) Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
				newName += " " + ChatColor.RED + "❤ " + ChatColor.YELLOW + health + "/" + maxHealth;
				// " ♥ 20/20";
				entity.setCustomName(newName);
				entity.setCustomNameVisible(true);
			}
		}
	}

	public void spawnVoidgloomSeraph(Enderman enderman) {
		newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Voidgloom Seraph" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		Objects.requireNonNull(enderman.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(300.0);
		enderman.setHealth(300.0);
		Objects.requireNonNull(enderman.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(15.0);
		Objects.requireNonNull(enderman.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(30.0);
		Objects.requireNonNull(enderman.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.6);
		enderman.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		enderman.setTarget(Plugin.getNearestPlayer(enderman));
		enderman.setCustomNameVisible(true);
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The remains of the Superior Dragon has drawn the attention of the Voidgloom Seraph!  Defeat it before it's too late!");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
	}

	public void spawnMutantEnderman(Enderman enderman) {
		newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Mutant Enderman" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		Objects.requireNonNull(enderman.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(150.0);
		enderman.setHealth(150.0);
		Objects.requireNonNull(enderman.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(7.5);
		Objects.requireNonNull(enderman.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(20.0);
		Objects.requireNonNull(enderman.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.45);
		enderman.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		enderman.setTarget(Plugin.getNearestPlayer(enderman));
		enderman.setCustomNameVisible(true);
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Pearl corrupts the Enderman.  It has become a Mutated Enderman!");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
	}

	public void spawnmeloGnorI(IronGolem golem) {
		newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "meloG norI" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		Objects.requireNonNull(golem.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(200.0);
		golem.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		golem.setTarget(Plugin.getNearestPlayer(golem));
		golem.setHealth(200.0);
		golem.setCustomNameVisible(true);
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Antimatter has done strange things to this Iron Golem...");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
	}

	public void spawnChickzilla(Chicken chicken) {
		newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Chickzilla" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		Objects.requireNonNull(chicken.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(1000.0);
		chicken.setHealth(1000.0);
		chicken.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		chicken.setTarget(Plugin.getNearestPlayer(chicken));
		chicken.setCustomNameVisible(true);
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Omega Egg hatches into the Chickzilla!");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
	}

	public void spawnTarantulaBroodfather(Spider spider) {
		newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Tarantula Broodfather" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		spider.setHealth(16.0);
		Objects.requireNonNull(spider.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.75);
		spider.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		spider.setTarget(Plugin.getNearestPlayer(spider));
		spider.setCustomNameVisible(true);
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Spider Relic draws the attention of the Tarantula Broodfather!");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
	}
}