package listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

public class EditSkull implements Listener {
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		Random random = new Random();
		if(e.getEntity() instanceof WitherSkull skull) {
			String name = ((Wither) Objects.requireNonNull(skull.getShooter())).getCustomName();
			assert name != null;
			if(name.contains("Maxor")) {
				Vector zoooooooooooom = skull.getDirection();
				zoooooooooooom = zoooooooooooom.add(zoooooooooooom).add(zoooooooooooom);
				skull.setVelocity(zoooooooooooom);
			} else if(name.contains("Storm") && random.nextDouble() < 0.1) {
				CustomMobs.spawnLightning(skull);
			}

			/*if(random.nextDouble() < 0.05) {
				String message = name + ChatColor.RESET + ChatColor.BOLD + ChatColor.RED + ": ";
				switch(random.nextInt(19)) {
					case 0 -> message += "Eat Wither Skulls, scum!";
					case 1 -> message += "How about you taste some rapid fire Wither Skulls!";
					case 2 -> message += "Time for me to blast you away for good!";
					case 3 -> message += "Slowing me down will be your greatest accomplishment!";
					case 4 -> message += "The Age of Men is over, we are creating tens, hundreds of withers!!";
					case 5 -> message += "Not just your land, but every kingdom will soon be ruled by our army of undead!";
					case 6 -> message += "No more adventurers, no more heroes, death and thunder!";
					case 7 -> message += "The days are numbered until I am finally unleashed again on the world!";
					case 8 -> message += "I am the death zone, you are smart to flee.";
					case 9 -> message += "There is no stopping me!";
					case 10 -> message += "Punishment served, nothing survives my reach.";
					case 11 -> message += "That's a very impressive trick.  Too bad it doesn't do anything!";
					case 12 -> message += "Fight for your life!";
					case 13 -> message += "Not just your Village, but the whole world will end!";
					case 14 -> message += "Sometimes when you have a problem, you just need to destroy it all and start again";
					case 15 -> message += "WITNESS MY RAW NUCLEAR POWER!";
					case 16 -> message += "BOOOOOOOOOOOOOOOOOOOOOOOOOM";
					case 17 -> message += "HASTA LA VISTA, BABY!";
					case 18 -> message += "IF YOU WANT TO STAY COOL, DON'T LOOK!";
				}
				Bukkit.broadcastMessage(message);
			}*/
		} else if(e.getEntity() instanceof DragonFireball fireball) {
			String name = ((EnderDragon) Objects.requireNonNull(fireball.getShooter())).getCustomName();
			assert name != null;
			if(name.contains("Unstable Dragon") && random.nextDouble() < 0.667) {
				CustomMobs.spawnLightning(fireball);
			} else if(name.contains("Superior Dragon") && random.nextDouble() < 0.333) {
				CustomMobs.spawnLightning(fireball);
			} else if(name.contains("Young Dragon")) {
				Vector zoooooooooooom = fireball.getDirection();
				zoooooooooooom = zoooooooooooom.add(zoooooooooooom).add(zoooooooooooom);
				fireball.setVelocity(zoooooooooooom);
			}

			/*if(random.nextDouble() < 0.2) {
				String message = name + ChatColor.RESET + ChatColor.BOLD + ChatColor.RED + ": ";
				switch(random.nextInt(8)) {
					case 0 -> message += "Your puny little weapons stand no chance against me!";
					case 1 -> message += "I will avenge all of the Endermen that have been slain in cold blood!";
					case 2 -> message += "You are so slow that my grandfather can fly faster than you.";
					case 3 -> message += "LF Chimera V T7 Dark Claymore.";
					case 4 -> {
						message += "No wonder ";
						switch(random.nextInt(4)) {
							case 0 -> message += "Maxor";
							case 1 -> message += "Storm";
							case 2 -> message += "Goldor";
							case 3 -> message += "Necron";
						}
						message += " was laughing his ass off about you, you're pathetic!";
					}
					case 5 -> message += "Honestly, you should jump into the Void, it'll be a less painful death than facing me.";
					case 6 -> message += "You're so bad at the video game that you will somehow find a way to die in a superflat world with one bedrock layer and one layer of water.";
					case 7 -> message += "I bet you're the type of person to yeet themselves with Explosive Arrows when fighting the Voidgloom Seraph.";
				}
				Bukkit.broadcastMessage(message);
			}*/
		}
	}
}
