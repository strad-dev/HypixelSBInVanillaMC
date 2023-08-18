package listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Random;

public class DeathListener implements Listener {
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		String vanilla = e.getDeathMessage();
		String player = e.getEntity().getName();
		Random random = new Random();
		assert vanilla != null;
		if(vanilla.contains("fell out of the world")) { // void, /kill
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " tactically nuked themselves to teleport to their spawn location.");
		} else if(vanilla.contains("was shot by")) { // arrows
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " was filled with flint.");
		} else if(vanilla.contains("was pummeled by")) { // snowballs
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + "'s flimsy skin was frozen off by a snowball.");
		} else if(vanilla.contains("was pricked to death")) { // cactus
			e.setDeathMessage(ChatColor.RED + "Stupidity got to the best of " + ChatColor.GREEN + player + ChatColor.RED + ".");
		} else if(vanilla.contains("drowned")) { // drowning
			e.setDeathMessage(ChatColor.RED + "Air is vital to life.  Looks like " + ChatColor.GREEN + player + ChatColor.RED + " didn't get the memo.");
		} else if(vanilla.contains("experienced kinetic energy")) { // elytra
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " met their end due to not studying Physics fourty hours a day.");
		} else if(vanilla.contains("blew up") || vanilla.contains("was blown up")) { // explosions
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " was reduced to atoms due to trinitrotoluene.");
		} else if(vanilla.contains("Intentional Game Design")) { // beds
			if(random.nextDouble() > 0.0001) {
				e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " does not know how Minecraft works.");
			} else {
				e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " does not know how Minceraft works.");
			}
		} else if(vanilla.contains("hit the ground too hard")) { // falling
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " misjudged their health.");
		} else if(vanilla.contains("fell from a high place")) { // falling
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " thought they could Water MLG, but failed miserably.");
		} else if(vanilla.contains("fell off a ladder")) { // ladder falling
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " lost their footing.");
		} else if(vanilla.contains("fell off some vines") || vanilla.contains("fell off some weeping vines") || vanilla.contains("fell off some twisting vines") || vanilla.contains("fell while climbing")) { // vines
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " thought they were Tarazan, but they aren't.");
		} else if(vanilla.contains("fell off scaffolding")) { // scaffolding
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " did not take proper safety precautions while building.");
		} else if(vanilla.contains("death.fell.accient.water")) { // lol
			e.setDeathMessage(ChatColor.RED + "death.fell.accident.water\n" + ChatColor.WHITE + player + " has completed the challenge " + ChatColor.DARK_PURPLE + "[Stupidly Complicated Death]");
		} else if(vanilla.contains("impaled on a stalagmite")) { // falling onto rocks
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " was NOT paying attention, and paid the price.  Also that must hurt.  A lot.  Right where you DON'T want it to hurt.");
		} else if(vanilla.contains("was doomed to fall")) { // doomed to fall
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " was doomed to fall.");
		} else if(vanilla.contains("was squashed by a falling anvil")) {  // anvil
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " was flattened into a pancake from 27121.5 kilograms of iron.");
		} else if(vanilla.contains("was squashed by a falling block")) { // non-anvil anvils
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " was flattened into a pancake using non-natural means.");
		} else if(vanilla.contains("was skewered by a falling stalactite")) { // stalactite
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " was skewered in half because they weren't aware of their surroundings.");
		} else if(vanilla.contains("went up in flames") || vanilla.contains("walked into fire whilst fighting")) { // fire
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " thought they could resist fire.  They were proven wrong.");
		} else if(vanilla.contains("burned to death") || vanilla.contains("was burnt to a crisp")) { // burn
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " thought they could pull a Dream and survive on half a heart, but miscalculated by about half a heart.");
		} else if(vanilla.contains("went off with a bang")) { // fireworks
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " learned that playing with dangerous items is not the best idea.");
		} else if(vanilla.contains("tried to swim in lava")) { // lava
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " realized that the \"water\" they were bathing in was not water.");
		} else if(vanilla.contains("was struck by lightning")) { // lightning
			e.setDeathMessage(ChatColor.BOLD + String.valueOf(ChatColor.RED) + "GOD " + ChatColor.RESET + ChatColor.RED + "did not like what " + ChatColor.GREEN + player + ChatColor.RED + " was doing, and decided to smite them.");
		} else if(vanilla.contains("discovered the floor was lava") || vanilla.contains("walked into danger zone")) { // magma
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " wasn't paying attention to where they were going and burned themselves.");
		} else if(vanilla.contains("was killed by magic") || vanilla.contains("using magic")) { // magic
			e.setDeathMessage(ChatColor.RED + "AVADA KEDAVRA!  was cast onto " + ChatColor.GREEN + player + ChatColor.RED + ".");
		} else if(vanilla.contains("froze to death") || vanilla.contains("was frozen to death")) { // powder snow
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " realized that they are not Elsa and that the cold does bother them.");
		} else if(vanilla.contains("was slain by")) { // mobs
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " was BRUTALLY MURDERED.");
		} else if(vanilla.contains("was fireballed by")) { // fireballs
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " enraged the Fire Lord, who decided to dish out some punishment.");
		} else if(vanilla.contains("was stung to death")) { // bees
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " angered nature, and nature responded.  Violently.");
		} else if(vanilla.contains("was shot by a skull")) { // wither
			e.setDeathMessage(ChatColor.RED + "The Wither King was displeased with " + ChatColor.GREEN + player + ChatColor.RED + " and wiped them from the Earth.");
		} else if(vanilla.contains("was obliterated by a sonically-charged shriek")) { // warden
			e.setDeathMessage(ChatColor.RED + "Turns out excessive noise CAN kill.  " + ChatColor.GREEN + player + ChatColor.RED + ", unfortunately, did not know that.");
		} else if(vanilla.contains("starved to death")) { // hunger
			e.setDeathMessage(ChatColor.RED + "You may not need water, but you need food.  " + ChatColor.GREEN + player + ChatColor.RED + " decided to ignore this wisdom, and paid the price.");
		} else if(vanilla.contains("suffocated in a wall")) { // suffocation
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " thought it would be a GREAT idea to stuff themselves into a block.");
		} else if(vanilla.contains("was squished") || vanilla.contains("was squashed by")) { // entity cramming
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " died from excessive social interaction.");
		} else if(vanilla.contains("was poked to death")) { // suffocation
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " didn't pay attention to where they were walking, and bled out completely.");
		} else if(vanilla.contains("trying to hurt")) { // thorns
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " did not understand Newton's Third Law: Every Action has an Equal and Opposite Reaction.  In this case, a deadly Reaction.");
		} else if(vanilla.contains("was impaled by")) { // trident
			e.setDeathMessage(ChatColor.RED + "Poseidon was mad at " + ChatColor.GREEN + player + ChatColor.RED + " and decided to impale them.");
		} else if(vanilla.contains("didn't want to live in the same world as")) {
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " did not enjoy social interaction, and yeeted themselves out of the world.");
		} else if(vanilla.contains("withered away")) {
			e.setDeathMessage(ChatColor.RED + "The Wither King's effect is extensive.  He has decided that " + ChatColor.GREEN + player + ChatColor.RED + " does not deserve to live.");
		} else {
			e.setDeathMessage(ChatColor.GREEN + player + ChatColor.RED + " simply died.");
		}
	}
}
