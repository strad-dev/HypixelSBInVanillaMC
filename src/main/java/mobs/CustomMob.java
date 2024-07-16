package mobs;

import listeners.DamageType;
import mobs.enderDragons.*;
import mobs.generic.*;
import mobs.withers.*;
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
			} else if(tags.contains("Maxor")) {
				return new Maxor();
			} else if(tags.contains("Storm")) {
				return new Storm();
			} else if(tags.contains("Goldor")) {
				return new Goldor();
			} else if(tags.contains("Necron")) {
				return new Necron();
			} else if(tags.contains("WitherKing")) {
				return new WitherKing();
			} else if(tags.contains("HolyDragon")) {
				return new Holy();
			} else if(tags.contains("OldDragon")) {
				return new Old();
			} else if(tags.contains("ProtectorDragon")) {
				return new Protector();
			} else if(tags.contains("StrongDragon")) {
				return new Strong();
			} else if(tags.contains("SuperiorDragon")) {
				return new Superior();
			} else if(tags.contains("UnstableDragon")) {
				return new Unstable();
			} else if(tags.contains("WiseDragon")) {
				return new Wise();
			} else if(tags.contains("YoungDragon")) {
				return new Young();
			} else if(tags.contains("WitherKingDragon")) {
				return new WitherKingDragon();
			} else if(tags.contains("GuardSkeleton")) {
				return new WitherKingSkeleton();
			} else {
				return null;
			}
		}
		return null;
	}

	String onSpawn(Player p, Mob e);

	// return true --> allow for normal damage to be calculated
	// return false --> do not allow for normal damage to be calculated
	boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type);

	boolean whenDamaging(LivingEntity damagee);
}