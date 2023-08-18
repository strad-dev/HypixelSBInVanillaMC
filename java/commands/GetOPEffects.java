package commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.bukkit.Bukkit.getServer;

public class GetOPEffects implements CommandExecutor {
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if(commandSender.isOp()) {
			if(commandSender instanceof Player player) {
				try {
					player = getServer().getPlayer(strings[1]);
				} catch(Exception exception) {
					//nothing here lol
				}
				if(player == null) {
					commandSender.sendMessage(ChatColor.RED + "Invalid player provided.");
					return false;
				}
				boolean clearEffects;
				try {
					clearEffects = Boolean.parseBoolean(strings[0]);
				} catch(Exception exception) {
					commandSender.sendMessage(ChatColor.RED + "You have to tell me whether you want to clear your current effects or not.");
					return false;
				}
				if(clearEffects) {
					for(PotionEffect effect : player.getActivePotionEffects()) {
						player.removePotionEffect(effect.getType());
					}
				}
				PotionEffect[] effects = {new PotionEffect(PotionEffectType.CONDUIT_POWER, PotionEffect.INFINITE_DURATION, 255, true, false, false), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, PotionEffect.INFINITE_DURATION, 255, true, false, false), new PotionEffect(PotionEffectType.FAST_DIGGING, PotionEffect.INFINITE_DURATION, 255, true, false, false), new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 255, true, false, false), new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, PotionEffect.INFINITE_DURATION, 255, true, false, false), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, PotionEffect.INFINITE_DURATION, 255, true, false, false), new PotionEffect(PotionEffectType.JUMP, PotionEffect.INFINITE_DURATION, 4, true, false, false), new PotionEffect(PotionEffectType.LUCK, PotionEffect.INFINITE_DURATION, 255, true, false, false), new PotionEffect(PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, 255, true, false, false), new PotionEffect(PotionEffectType.SATURATION, PotionEffect.INFINITE_DURATION, 255, true, false, false), new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 4, true, false, false), new PotionEffect(PotionEffectType.WATER_BREATHING, PotionEffect.INFINITE_DURATION, 255, true, false, false)};
				for(PotionEffect effect : effects) {
					player.addPotionEffect(effect);
				}
				commandSender.sendMessage("Successfully gave " + player.getName() + " OP Effects");
			}
		}
		return true;
	}
}