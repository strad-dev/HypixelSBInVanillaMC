package mobs;

import listeners.DamageType;
import mobs.enderDragons.*;
import mobs.generic.*;
import mobs.hardmode.withers.*;
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
				if(tags.contains("HardMode")) {
					return new MasterMaxor();
				} else {
					return new Maxor();
				}
			} else if(tags.contains("Storm")) {
				if(tags.contains("HardMode")) {
					return new MasterStorm();
				} else {
					return new Storm();
				}
			} else if(tags.contains("Goldor")) {
				if(tags.contains("HardMode")) {
					return new MasterGoldor();
				} else {
					return new Goldor();
				}
			} else if(tags.contains("Necron")) {
				if(tags.contains("HardMode")) {
					return new MasterNecron();
				} else {
					return new Necron();
				}
			} else if(tags.contains("WitherKing")) {
				if(tags.contains("HardMode")) {
					return new MasterWitherKing();
				} else {
					return new WitherKing();
				}
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
			} else if(tags.contains("Power")) {
				return new PowerWitherSkeleton();
			} else if(tags.contains("Fire")) {
				return new FireWitherSkeleton();
			} else if(tags.contains("Ice")) {
				return new IceWitherSkeleton();
			} else if(tags.contains("Soul")) {
				return new SoulWitherSkeleton();
			} else if(tags.contains("Martial")) {
				return new MartialWitherSkeleton();
			} else if(tags.contains("GuardSkeleton")) {
				return new WitherKingSkeleton();
			} else {
				return null;
			}
		}
		return null;
	}

	String onSpawn(Player p, Mob e);

	/**
	 * Handles custom entity behavior when the mob is damaged
	 *
	 * @param damagee        the custom entity
	 * @param damager        the entity dealing damage
	 * @param originalDamage the original damage amount
	 * @param type           the originla damage type
	 * @return true if the original calculation can proceed; false if not
	 */
	boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type);

	/**
	 * Handles custom entity behavior when the mob deals damage
	 *
	 * @param damagee        the entity taking the damage
	 * @param damager        the custom entity
	 * @param originalDamage the original damage amount
	 * @param type           the originla damage type
	 * @return true if the original calculation can proceed; false if not
	 */
	boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type);
}