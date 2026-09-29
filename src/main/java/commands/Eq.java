package commands;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import items.misc.HolyIce;
import items.misc.IceSpray;
import items.weapons.Scylla;
import items.weapons.SwordOfBadHealth;
import listeners.CustomDamage;
import misc.Menus;
import misc.Plugin;
import misc.Utils;
import net.kyori.adventure.text.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/*
 * /eq, ported from M7 TAS. 1-row GUI: worn armor in slots 0-3, offensive/defensive stats in 6-7, sugar cane in
 * slot 8 whose count encodes speed (100 = vanilla, exact figure on the tooltip). Clicking armor in your own
 * inventory equips it.
 * Both the executor and a listener; the GUI is identified by EqHolder.
 */
public class Eq implements CommandExecutor, Listener {

	private static final Component TITLE = Utils.msg("<dark_gray>Equipment");
	private static final int OFFENSE_SLOT = 6;
	private static final int DEFENSE_SLOT = 7;
	private static final int SPEED_SLOT = 8;
	/** Modifiers currentSpeed skips: vanilla mechanics, not the speed stat. Speed/Soul Speed deliberately count. */
	private static final Set<NamespacedKey> IGNORED_SPEED_MODIFIERS = Set.of(NamespacedKey.minecraft("sprinting"));
	/** Last swap tick per player; collapses a double-click burst into one swap. */
	private static final Map<UUID, Integer> lastSwapTick = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player p)) {
			sender.sendMessage(Utils.msg("Only players can run this"));
			return true;
		}
		open(p);
		return true;
	}

	private static void open(Player p) {
		EqHolder holder = new EqHolder();
		Inventory gui = Bukkit.createInventory(holder, 9, TITLE);
		holder.setInventory(gui);
		refresh(p, gui);
		p.openInventory(gui);
		applySpeedCane(p); // after the menu exists: writes via NMS
	}

	/** Armor into slots 0-3, stats into 6-7. The cane is set separately via NMS. */
	private static void refresh(Player p, Inventory gui) {
		for(int i = 0; i < 4; i++) {
			ItemStack worn = getArmor(p, i);
			gui.setItem(i, worn == null ? null : worn.clone());
		}
		refreshStats(p, gui);
	}

	private static void refreshStats(Player p, Inventory gui) {
		gui.setItem(OFFENSE_SLOT, offenseItem(p));
		gui.setItem(DEFENSE_SLOT, defenseItem(p));
	}

	private static ItemStack offenseItem(Player p) {
		ItemStack held = p.getInventory().getItemInMainHand();
		var attr = p.getAttribute(Attribute.ATTACK_DAMAGE);
		double damage = attr == null ? 1 : attr.getValue();
		damage = Math.max(0, damage + CustomDamage.sharpnessBonus(held.getEnchantmentLevel(Enchantment.SHARPNESS))
				- CustomDamage.strengthPenalty(p));

		double multiplier = 1;
		List<Component> modifiers = new ArrayList<>();
		if(p.getScoreboardTags().contains("IceSprayed")) {
			multiplier *= IceSpray.DAMAGE_DEALT_PENALTY;
			modifiers.add(modifierLine("Ice Sprayed", IceSpray.DAMAGE_DEALT_PENALTY, true));
		}
		if(p.getScoreboardTags().contains("BadHealthBuffed")) {
			multiplier *= SwordOfBadHealth.DAMAGE_BONUS;
			modifiers.add(modifierLine("Sword of Bad Health", SwordOfBadHealth.DAMAGE_BONUS, true));
		}

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("<gray>Damage: <red>" + Utils.tenthNumber(damage * multiplier)));
		int smite = held.getEnchantmentLevel(Enchantment.SMITE);
		if(smite > 0) {
			lore.add(Utils.mm("<gray>Damage vs Undead: <red>"
					+ Utils.tenthNumber((damage + CustomDamage.smiteBonus(smite)) * multiplier)));
		}
		int bane = held.getEnchantmentLevel(Enchantment.BANE_OF_ARTHROPODS);
		if(bane > 0) {
			lore.add(Utils.mm("<gray>Damage vs Arthropods: <red>"
					+ Utils.tenthNumber((damage + CustomDamage.smiteBonus(bane)) * multiplier)));
		}
		if(!modifiers.isEmpty()) {
			lore.add(Component.empty());
			lore.addAll(modifiers);
		}
		lore.add(Component.empty());
		lore.add(Utils.mm("<dark_gray>Fully charged, before crits"));
		return statsItem(Material.DIAMOND_SWORD, "<red>Offensive Stats", lore);
	}

	private static ItemStack defenseItem(Player p) {
		double armor = CustomDamage.armorPoints(p);
		double taken = CustomDamage.armorMultiplier(armor);

		List<Component> modifiers = new ArrayList<>();
		int prot = CustomDamage.protectionLevels(p);
		if(prot > 0) {
			double m = CustomDamage.protectionMultiplier(prot);
			taken *= m;
			modifiers.add(modifierLine("Protection (" + prot + ")", m, false));
		}
		PotionEffect resistance = p.getPotionEffect(PotionEffectType.RESISTANCE);
		if(resistance != null) {
			double m = CustomDamage.resistanceMultiplier(p);
			taken *= m;
			modifiers.add(modifierLine("Resistance " + (resistance.getAmplifier() + 1), m, false));
		}
		if(p.getScoreboardTags().contains("WitherShield")) {
			double m = 1 - Scylla.witherShieldReduction(p);
			taken *= m;
			modifiers.add(modifierLine("Wither Shield", m, false));
		}
		if(p.getScoreboardTags().contains("HolyIce")) {
			taken *= HolyIce.DAMAGE_TAKEN;
			modifiers.add(modifierLine("Holy Ice", HolyIce.DAMAGE_TAKEN, false));
		}
		if(p.getScoreboardTags().contains("IceSprayed")) {
			taken *= IceSpray.DAMAGE_TAKEN_BONUS;
			modifiers.add(modifierLine("Ice Sprayed", IceSpray.DAMAGE_TAKEN_BONUS, false));
		}

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm("<gray>Armor: <green>" + Utils.tenthNumber(armor)));
		lore.add(Utils.mm("<gray>Armor Reduction: <green>" + Utils.percent(1 - CustomDamage.armorMultiplier(armor))));
		if(!modifiers.isEmpty()) {
			lore.add(Component.empty());
			lore.addAll(modifiers);
		}
		lore.add(Component.empty());
		lore.add(Utils.mm("<gray>Damage Taken: <white>" + Utils.percent(taken)));
		if(taken <= 0) {
			lore.add(Utils.mm("<gray>Effective HP: <white>Immune"));
		} else {
			var maxHealth = p.getAttribute(Attribute.MAX_HEALTH);
			double health = maxHealth == null ? 20 : maxHealth.getValue();
			lore.add(Utils.mm("<gray>Effective HP: <white>" + Utils.tenthNumber(health / taken)
					+ " <dark_gray>(" + Utils.tenthNumber(1 / taken) + "x)"));
		}
		lore.add(Component.empty());
		lore.add(Utils.mm("<dark_gray>Against a melee or arrow hit"));
		return statsItem(Material.DIAMOND_CHESTPLATE, "<green>Defensive Stats", lore);
	}

	private static Component modifierLine(String label, double multiplier, boolean higherIsGood) {
		boolean up = multiplier > 1;
		String colour = up == higherIsGood ? "<green>" : "<red>";
		return Utils.mm("<gray>" + label + ": " + colour + (up ? "+" : "-") + Utils.percent(Math.abs(multiplier - 1)));
	}

	private static ItemStack statsItem(Material material, String title, List<Component> lore) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		if(meta == null) return item;
		meta.displayName(Utils.mm(title));
		meta.lore(lore);
		item.setItemMeta(meta);
		item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay()
				.addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS));
		return item;
	}

	/**
	 * Count is speed/10: the client clamps count to max_stack_size, hard-capped at 99. MAX_STACK_SIZE is raised
	 * so 65-99 render.
	 */
	private static void applySpeedCane(Player p) {
		ServerPlayer sp = ((CraftPlayer) p).getHandle();
		AbstractContainerMenu menu = sp.containerMenu;
		if(SPEED_SLOT >= menu.slots.size()) return;
		int amount = Math.clamp(currentSpeed(p) / 10, 1, 99);
		net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(speedItem(p));
		nms.set(DataComponents.MAX_STACK_SIZE, amount);
		nms.setCount(amount);
		menu.getSlot(SPEED_SLOT).set(nms);
		sp.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), SPEED_SLOT, nms));
	}

	private static ItemStack speedItem(Player p) {
		int speed = currentSpeed(p);
		ItemStack cane = new ItemStack(Material.SUGAR_CANE);
		ItemMeta meta = cane.getItemMeta();
		if(meta != null) {
			meta.displayName(Utils.mm("<aqua>Speed: <white>" + speed));
			cane.setItemMeta(meta);
		}
		return cane;
	}

	/** 100 = vanilla, sprinting excluded. */
	private static int currentSpeed(Player p) {
		var attr = p.getAttribute(Attribute.MOVEMENT_SPEED);
		if(attr == null || attr.getBaseValue() == 0) return 100;
		// Not getValue(): sprinting is a +30% MULTIPLY_SCALAR_1 modifier and read 30% high. The three passes
		// mirror AttributeInstance.calculateValue; floor 0 like sanitizeValue.
		double vanillaBase = attr.getBaseValue(); // the 100 scale is relative to this
		Collection<AttributeModifier> mods = attr.getModifiers();

		double base = vanillaBase;
		for(AttributeModifier mod : mods) {
			if(counts(mod, AttributeModifier.Operation.ADD_NUMBER)) base += mod.getAmount();
		}
		double value = base;
		for(AttributeModifier mod : mods) {
			if(counts(mod, AttributeModifier.Operation.ADD_SCALAR)) value += base * mod.getAmount();
		}
		for(AttributeModifier mod : mods) {
			if(counts(mod, AttributeModifier.Operation.MULTIPLY_SCALAR_1)) value *= 1 + mod.getAmount();
		}
		return (int) Math.round(Math.max(0, value) / vanillaBase * 100);
	}

	private static boolean counts(AttributeModifier mod, AttributeModifier.Operation op) {
		return mod.getOperation() == op && !IGNORED_SPEED_MODIFIERS.contains(mod.getKey());
	}

	// =================== Click handling ===================

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(!(e.getView().getTopInventory().getHolder() instanceof EqHolder)) return;
		if(Menus.ignoreDoubleClick(e)) return;
		e.setCancelled(true); // only the armor swap below changes anything
		if(!(e.getWhoClicked() instanceof Player p)) return;
		Inventory clicked = e.getClickedInventory();
		if(clicked == null || !clicked.equals(p.getInventory())) return; // only bottom-inventory clicks act

		ItemStack item = e.getCurrentItem();
		if(item == null || item.getType() == Material.AIR) return;
		int idx = armorSlotIndex(item.getType());
		if(idx < 0) return;

		// One swap per player per tick.
		int now = MinecraftServer.currentTick;
		if(lastSwapTick.getOrDefault(p.getUniqueId(), -1) == now) return;
		lastSwapTick.put(p.getUniqueId(), now);

		ItemStack worn = getArmor(p, idx);
		setArmor(p, idx, item.clone());
		e.setCurrentItem(worn); // null clears the slot if nothing was worn
		Inventory gui = e.getView().getTopInventory();
		gui.setItem(idx, item.clone());
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			if(p.getOpenInventory().getTopInventory().getHolder() instanceof EqHolder) {
				refreshStats(p, gui);
				applySpeedCane(p);
			}
		}, 2L);
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		if(!(e.getView().getTopInventory().getHolder() instanceof EqHolder)) return;
		int topSize = e.getView().getTopInventory().getSize();
		for(int slot : e.getRawSlots()) {
			if(slot < topSize) {
				e.setCancelled(true);
				return;
			}
		}
	}

	// =================== Armor helpers ===================

	/** 0 helmet, 1 chestplate, 2 leggings, 3 boots, else -1. */
	private static int armorSlotIndex(Material m) {
		String n = m.name();
		if(n.endsWith("_HELMET") || m == Material.PLAYER_HEAD || m == Material.CARVED_PUMPKIN) return 0;
		if(n.endsWith("_CHESTPLATE") || m == Material.ELYTRA) return 1;
		if(n.endsWith("_LEGGINGS")) return 2;
		if(n.endsWith("_BOOTS")) return 3;
		return -1;
	}

	private static ItemStack getArmor(Player p, int idx) {
		PlayerInventory inv = p.getInventory();
		return switch(idx) {
			case 0 -> inv.getHelmet();
			case 1 -> inv.getChestplate();
			case 2 -> inv.getLeggings();
			case 3 -> inv.getBoots();
			default -> null;
		};
	}

	private static void setArmor(Player p, int idx, ItemStack item) {
		PlayerInventory inv = p.getInventory();
		switch(idx) {
			case 0 -> inv.setHelmet(item);
			case 1 -> inv.setChestplate(item);
			case 2 -> inv.setLeggings(item);
			case 3 -> inv.setBoots(item);
		}
	}

	/** Identifies the /eq GUI. */
	public static final class EqHolder implements InventoryHolder {
		private Inventory inv;
		void setInventory(Inventory inv) { this.inv = inv; }
		@Override public Inventory getInventory() { return inv; }
	}
}
