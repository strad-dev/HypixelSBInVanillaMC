package items.summonItems;

import mobs.CustomMob;
import mobs.generic.*;

public interface SummonItem {
	static CustomMob spawnABoss(String id) {
		switch(id) {
			case "skyblock/summon/superior_remnant" -> {
				return new VoidgloomSeraph();
			}
			case "skyblock/summon/corrupt_pearl" -> {
				return new MutantEnderman();
			}
			case "skyblock/summon/antimatter" -> {
				return new meloGnorI();
			}
			case "skyblock/summon/omega_egg" -> {
				return new Chickzilla();
			}
			case "skyblock/summon/spider_relic" -> {
				return new Broodfather();
			}
			case "skyblock/summon/atoned_flesh" -> {
				return new AtonedHorror();
			}
			case "skyblock/summon/giant_flesh" -> {
				return new Sadan();
			}
			case "skyblock/summon/wither_skeleton_spawn_egg" -> {
				return new InfuriatedWitherSkeleton();
			}
			default -> {
				return null;
			}
		}
	}
}