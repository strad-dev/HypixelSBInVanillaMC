package mobs.enderDragons;

import mobs.generic.CustomMob;
import org.bukkit.entity.DragonFireball;

import java.util.Random;

public interface CustomDragon extends CustomMob {
	static CustomDragon spawnRandom() {
		Random random = new Random();
		if(random.nextDouble() < 0.04) {
			return new Superior();
		} else {
			switch(random.nextInt(7)) {
				case 0 -> {
					return new Unstable();
				}
				case 1 -> {
					return new Strong();
				}
				case 2 -> {
					return new Holy();
				}
				case 3 -> {
					return new Protector();
				}
				case 4 -> {
					return new Young();
				}
				case 5 -> {
					return new Old();
				}
				case 6 -> {
					return new Wise();
				}
			}
		}
		return null;
	}

	void whenShootingFireball(DragonFireball fireball);
}