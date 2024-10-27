package mobs.withers;

import mobs.CustomMob;
import mobs.hardmode.withers.*;
import org.bukkit.entity.WitherSkull;

import java.util.Random;

public interface CustomWither extends CustomMob {
	static CustomWither spawnRandom(boolean hardMode) {
		Random random = new Random();
		if(hardMode) {
			if(random.nextDouble() < 0.50) {
				return new MasterWitherKing();
			} else {
				switch(random.nextInt(4)) {
					case 0 -> {
						return new MasterMaxor();
					}
					case 1 -> {
						return new MasterStorm();
					}
					case 2 -> {
						return new MasterGoldor();
					}
					case 3 -> {
						return new MasterNecron();
					}
				}
			}
		} else {
			if(random.nextDouble() < 0.04) {
				return new WitherKing();
			} else {
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
			}
		}
		return null;
	}

	void whenShootingSkull(WitherSkull skull);
}