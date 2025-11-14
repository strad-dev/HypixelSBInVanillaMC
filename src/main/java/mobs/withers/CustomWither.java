package mobs.withers;

import mobs.CustomMob;
import org.bukkit.entity.WitherSkull;

import java.util.Random;

public interface CustomWither extends CustomMob {
	static CustomWither spawnRandom() {
		Random random = new Random();
		switch(random.nextInt(4)) {
			case 0 -> {
				return new Maxor();
			}
			case 1 -> {
				return new Storm();
			}
			case 2 -> {
				return new Goldor();
			}
			case 3 -> {
				return new Necron();
			}
		}
		return new Default();
	}

	void whenShootingSkull(WitherSkull skull);
}