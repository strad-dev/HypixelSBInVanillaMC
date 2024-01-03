package misc;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ExtractMethodRecommender")
public class SimilarData {
	public static void changeName(LivingEntity entity) {
		if(!(entity instanceof Player)) {
			String[] oldName = Objects.requireNonNull(entity.getCustomName()).split(" ");
			int health = (int) (entity.getHealth() + entity.getAbsorptionAmount());
			int maxHealth = (int) Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
			oldName[oldName.length - 1] = ChatColor.YELLOW + "" + health + "/" + maxHealth;
			StringBuilder newName = new StringBuilder(oldName[0]);
			for(int i = 1; i < oldName.length; i++) {
				newName.append(" ").append(oldName[i]);
			}
			entity.setCustomName(newName.toString());
		}
	}

	public static ItemMeta AOTVMeta(ItemMeta data) {
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Aspect of the Void");
		AttributeModifier attackDamage = new AttributeModifier("AOTVModifier", -1, AttributeModifier.Operation.ADD_NUMBER);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Base Damage: " + ChatColor.RED + "0");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Instant Transmission " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Teleport " + ChatColor.GREEN + "12 blocks" + ChatColor.GRAY + " ahead of you.");
		lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + "0");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Ether Transmission " + ChatColor.GREEN + ChatColor.BOLD + "SNEAK RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Teleport to your targetted block");
		lore.add(ChatColor.GRAY + "up to " + ChatColor.GREEN + "61 blocks" + ChatColor.GRAY + " blocks away.");
		lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + "0");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC SHOVEL " + ChatColor.MAGIC + "a");

		data.setLore(lore);

		return data;
	}

	public static ItemMeta scyllaMeta(ItemMeta data) {
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Hyperion");
		AttributeModifier attackSpeed = new AttributeModifier("scyllaModifier", 100, AttributeModifier.Operation.ADD_NUMBER);
		AttributeModifier attackDamage = new AttributeModifier("scyllaModifierDmg", 8, AttributeModifier.Operation.ADD_NUMBER);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeed);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Base Damage: " + ChatColor.RED + "9");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Wither Impact " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Teleport " + ChatColor.GREEN + "10 blocks" + ChatColor.GRAY + " ahead of");
		lore.add(ChatColor.GRAY + "you.  Then implode, dealing");
		lore.add(ChatColor.RED + "4-6" + ChatColor.GRAY + " damage to nearby");
		lore.add(ChatColor.GRAY + "enemies.  Also applies the Wither");
		lore.add(ChatColor.GRAY + "Shield Scroll Ability, reducing");
		lore.add(ChatColor.GRAY + "damage taken and granting an");
		lore.add(ChatColor.GRAY + "absorption shield for " + ChatColor.YELLOW + "5");
		lore.add(ChatColor.GRAY + "seconds.");
		lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + "8");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC SWORD " + ChatColor.MAGIC + "a");

		data.setLore(lore);

		return data;
	}
	
	public static ItemMeta termMeta(ItemMeta data) {
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Terminator");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Base Damage: " + ChatColor.RED + "2");
		lore.add("");
		lore.add(ChatColor.GOLD + "Shortbow: Instantly Shoots!");
		lore.add(ChatColor.GRAY + "Shoots " + ChatColor.AQUA + "3" + ChatColor.GRAY + " arrows at once.");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Salvation " + ChatColor.GREEN + ChatColor.BOLD + "LEFT CLICK");
		lore.add(ChatColor.GRAY + "Can be casted after landing " + ChatColor.GOLD + "3" + ChatColor.GRAY + " hits.");
		lore.add(ChatColor.GRAY + "Shoot a beam, penetrating up");
		lore.add(ChatColor.GRAY + "to " + ChatColor.YELLOW + "5" + ChatColor.GRAY + " foes and dealing " + ChatColor.RED + "2x");
		lore.add(ChatColor.GRAY + "the damage an arrow would.");
		lore.add(ChatColor.GRAY + "The beam always crits.");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC BOW " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		
		return data;
	}

	public static ItemMeta iceSprayMeta(ItemMeta data) {
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.GOLD + "Ice Spray Wand");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Ice Spray " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Produces a cone of ice in front");
		lore.add(ChatColor.GRAY + "of the caster that deals");
		lore.add(ChatColor.RED + "1" + ChatColor.GRAY + " damage to mobs and");
		lore.add(ChatColor.GRAY + "freezes them in place for " + ChatColor.GREEN + "5");
		lore.add(ChatColor.GRAY + "seconds!  Frozen mobs take");
		lore.add(ChatColor.RED + "+1" + ChatColor.GRAY + " increased damage!");
		lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + "2");
		lore.add(ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + "5s");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY WAND " + ChatColor.MAGIC + "a");

		data.setLore(lore);

		return data;
	}

	public static ItemMeta claymoreMeta(ItemMeta data) {
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Dark Claymore");
		AttributeModifier attackSpeed = new AttributeModifier("claymoreModifier", 100, AttributeModifier.Operation.ADD_NUMBER);
		AttributeModifier attackDamage = new AttributeModifier("claymoreModifierDmg", 9, AttributeModifier.Operation.ADD_NUMBER);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeed);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Base Damage: " + ChatColor.RED + "10");
		lore.add("");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "That thing was too big to be");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "called a sword, it was more like");
		lore.add(ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "a large hunk of stone.");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC SWORD " + ChatColor.MAGIC + "a");

		data.setLore(lore);

		return data;
	}

	public static ItemMeta shadowWarpMeta(ItemMeta data) {
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
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		return data;
	}

	public static ItemMeta implosionMeta(ItemMeta data) {
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
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		return data;
	}

	public static ItemMeta witherShieldMeta(ItemMeta data) {
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
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		return data;
	}

	public static ItemMeta necronHandleMeta(ItemMeta data) {
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
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		return data;
	}

	public static ItemMeta coreMeta(ItemMeta data) {
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Judgement Core");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A core so powerful that even");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the most dedicated players");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "tremble at it's power.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use this to craft the Terminator!");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY " + ChatColor.MAGIC + "a");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		return data;
	}

	public static ItemMeta tessellatedPearlMeta(ItemMeta data) {
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Tessellated Ender Pearl");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "An Ender Pearl so dense that even");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the most knowledgeable players");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "are mystified by it.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use 2 of this to craft the Terminator!");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		return data;
	}

	public static ItemMeta nullBladeBeta(ItemMeta data) {
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Null Blade");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A pair of Shears so null that even");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the most intelligent players");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "are confused by it.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use 3 of these to craft the Terminator!");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY " + ChatColor.MAGIC + "a");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		return data;
	}

	public static ItemMeta braidedFeatherMeta(ItemMeta data) {
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Braided Feather");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A Feather so sturdy that even");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the most powerful players");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "cannot destroy it.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use this to craft the Terminator!");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		return data;
	}

	public static ItemMeta taraSilkMeta(ItemMeta data) {
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Tarantula Silk");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A Web so perfect that even");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the most powerful players");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "cannot escape it.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use 2 of this to craft the Terminator!");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		return data;
	}

	public static ItemMeta supRemnantMeta(ItemMeta data) {
		data.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Remnant of the Superior Dragon");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "The remains of the strongest");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Dragon to ever exist.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use this on an Enderman to summon");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the terrifying Voidgloom Seraph!");
		lore.add("");
		lore.add(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " LEGENDARY " + ChatColor.MAGIC + "a");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		return data;
	}

	public static ItemMeta corruptedPearlMeta(ItemMeta data) {
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Corrupted Pearl");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "An interesting mutation");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "of Ender Pearls.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use this on an Enderman to");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "summon a Mutant Enderman!");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		return data;
	}

	public static ItemMeta antimatterMeta(ItemMeta data) {
		data.setDisplayName(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + "Antimatter");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "The consequence of storing such");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "massive amounts of Iron together.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use this on an Iron Golem");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "to summon a meloG norI!");
		lore.add("");
		lore.add(ChatColor.DARK_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + ChatColor.BOLD + " EPIC " + ChatColor.MAGIC + "a");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		return data;
	}

	public static ItemMeta omegaEggMeta(ItemMeta data) {
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Omega Egg");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "A strange specimen used to");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "create the strongest Chickens.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use this on a Chicken");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "to summon Chickzilla!");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		return data;
	}

	public static ItemMeta spiderRelicMeta(ItemMeta data) {
		data.setDisplayName(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Spider Relic");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "An ancient artifact left");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "by the Broodfather itself.");
		lore.add("");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "Use this on a Spider to summon");
		lore.add(ChatColor.RESET + String.valueOf(ChatColor.GRAY) + "the Tarantula Broodfather!");
		lore.add("");
		lore.add(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD + " RARE " + ChatColor.MAGIC + "a");
		data.setLore(lore);
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

		return data;
	}
}