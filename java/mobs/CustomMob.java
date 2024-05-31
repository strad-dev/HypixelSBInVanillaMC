package mobs;

import listeners.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.Set;

public interface CustomMob {
	static CustomMob getMob(Entity e) {
		if(e != null) {
			Set<String> tags = e.getScoreboardTags();
			if(tags.contains("AtonedHorror")) {
				return new AtonedHorror();
			} else if(tags.contains("Sadan")) {
				return new Sadan();
			} else if(tags.contains("Voidgloom")) {
				return new VoidgloomSeraph();
			} else if(tags.contains("MutantEnderman")) {
				return new MutantEnderman();
			} else if(tags.contains("meloGnorI")) {
				return new meloGnorI();
			} else if(tags.contains("Broodfather")) {
				return new Broodfather();
			} else if(tags.contains("Chickzilla")) {
				return new Chickzilla();
			} else if(tags.contains("InfuriatedSkeleton")) {
				return new InfuriatedWitherSkeleton();
			} else {
				return null;
			}
		}
		return null;
		// TODO Implement Withers & Ender Dragons into this interface, either directly or through subinterfaces
	}

	String onSpawn(Player p, Mob e);

	// return true --> allow for normal damage to be calculated
	// return false --> do not allow for normal damage to be calculated
	boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type);

	boolean whenDamaging(LivingEntity damagee);
}