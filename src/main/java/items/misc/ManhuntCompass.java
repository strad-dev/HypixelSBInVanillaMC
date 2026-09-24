package items.misc;

import items.AbilityItem;
import manhunt.Manhunt;
import misc.MinecraftFont;
import misc.Plugin;
import misc.SkyblockId;
import misc.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Hunters' tracker. Right click points the needle at the target; sneak right click cycles targets. Target is
 * on the stack (an index into {@link Manhunt#speedrunners()}), so two compasses can track two players.
 */
public class ManhuntCompass implements AbilityItem {
	public static final String ID = "skyblock/manhunt/compass";

	private static NamespacedKey targetKey() {
		return new NamespacedKey(Plugin.getInstance(), "manhunt_target");
	}

	public static ItemStack getItem() {
		return getItem(0, null);
	}

	/** For {@code ItemReloader}: keeps needle and target. */
	public static ItemStack refresh(ItemStack old) {
		Location needle = null;
		if(old.getItemMeta() instanceof CompassMeta meta && meta.hasLodestone()) {
			needle = meta.getLodestone();
		}
		return getItem(targetOf(old), needle);
	}

	public static ItemStack getItem(int target, Location needle) {
		ItemStack compass = new ItemStack(Material.COMPASS);

		CompassMeta data = (CompassMeta) compass.getItemMeta();
		data.displayName(Utils.mm("<red>Manhunt Compass"));
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		// Forced off, not unset: vanilla glints any compass with a lodestone target.
		data.setEnchantmentGlintOverride(false);
		data.getPersistentDataContainer().set(targetKey(), PersistentDataType.INTEGER, Math.max(0, target));
		if(needle != null) {
			// Untracked lets it point at bare coordinates, and spin once the target leaves the dimension.
			data.setLodestone(needle);
			data.setLodestoneTracked(false);
		}

		List<Component> lore = new ArrayList<>();
		lore.add(Utils.mm(ID));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gray>Tracking: <yellow>" + Manhunt.speedrunnerName(target)));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold>Ability: Track <green><bold>RIGHT CLICK"));
		lore.addAll(MinecraftFont.wrapLore("<gray>Point the needle at the tracked Speedrunner's position right now."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<gold>Ability: Change Target <green><bold>SNEAK RIGHT CLICK"));
		lore.addAll(MinecraftFont.wrapLore("<gray>Move to the next Speedrunner."));
		lore.add(Utils.mm(""));
		lore.add(Utils.mm("<red><bold><obfuscated>a</obfuscated> VERY SPECIAL ITEM <obfuscated>a</obfuscated>"));

		data.lore(lore);
		compass.setItemMeta(data);

		return SkyblockId.stamp(compass);
	}

	public static int targetOf(ItemStack item) {
		if(item == null || !item.hasItemMeta()) return 0;
		Integer stored = item.getItemMeta().getPersistentDataContainer().get(targetKey(), PersistentDataType.INTEGER);
		return stored == null ? 0 : stored;
	}

	@Override
	public boolean hasLeftClickAbility() {
		return false;
	}

	@Override
	public boolean onRightClick(Player p) {
		ItemStack held = p.getInventory().getItemInMainHand();

		// With one or no Speedrunner, sneak right click is just a Track.
		if(p.isSneaking() && Manhunt.speedrunners().size() > 1) {
			int next = (targetOf(held) + 1) % Manhunt.speedrunners().size();
			p.getInventory().setItemInMainHand(getItem(next, needleOf(held)));
			p.sendMessage(Utils.msg("<green>Now tracking " + Manhunt.speedrunnerName(next) + "."));
			p.playSound(p, Sound.UI_BUTTON_CLICK, 1, 1.5F);
			return true;
		}

		Player target = Manhunt.speedrunnerAt(targetOf(held));
		// Offline or other dimension: leave the needle on their last known spot.
		if(target == null || !target.getWorld().equals(p.getWorld())) {
			p.sendMessage(Utils.msg("<red>No players to track!"));
			p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.50F);
			return true;
		}

		p.getInventory().setItemInMainHand(getItem(targetOf(held), target.getLocation()));
		// No sound on purpose: Hunters track constantly and the click got old fast.
		p.sendMessage(Utils.msg("<green>Tracking " + target.getName()));
		return true;
	}

	private static Location needleOf(ItemStack item) {
		if(item != null && item.getItemMeta() instanceof CompassMeta meta && meta.hasLodestone()) {
			return meta.getLodestone();
		}
		return null;
	}

	@Override
	public boolean onLeftClick(Player p) {
		return false;
	}

	@Override
	public int manaCost() {
		return 0;
	}

	@Override
	public String cooldownTag() {
		return "";
	}

	@Override
	public int cooldown() {
		return 0;
	}
}
