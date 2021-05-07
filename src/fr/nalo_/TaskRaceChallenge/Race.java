package fr.nalo_.TaskRaceChallenge;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class Race implements CommandExecutor {

	private Main main;
	
	public Race(Main main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if(args.length < 1) {
			sender.sendMessage(ChatColor.RED + "{ start [time] / pause / resume / skip / targetScore {score} } ?");
			return false;
		}
		
		// --------------------------------------------------------------------------------------
		if(args[0].equalsIgnoreCase("start")) {
			Bukkit.getWorld("world").setTime(0);
			
			for(UUID player : this.main.players.keySet()) {
				this.main.players.put(player, 0);
				this.main.bossbar.addPlayer(Bukkit.getPlayer(player));
			}
			
			this.main.getServer().dispatchCommand(this.main.getServer().getConsoleSender(), "advancement revoke @a everything");
			
			this.main.randomChallengePick();
			this.main.bossbar.setVisible(true);
			
			TimerTask timer;
			try {
				timer = new TimerTask(this.main, Integer.parseInt(args[1]));
			} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
				timer = new TimerTask(this.main);
			}
			TimerTask.setRunning(true);
			timer.runTaskTimer(this.main, 0, 20);
			
			Bukkit.broadcastMessage(ChatColor.RED + "---------- Game is starting! ----------");
			return true;
		}
		
		// --------------------------------------------------------------------------------------
		if(args[0].equalsIgnoreCase("pause")) {
			if(!TimerTask.RUN) {
				sender.sendMessage(ChatColor.RED + "Game is already paused!");
				return false;
			}
			TimerTask.setRunning(false);
			this.main.bossbar.setTitle("-- PAUSED --");
			Bukkit.broadcastMessage(ChatColor.RED + "---------- Game paused! ----------");
			return true;
		}
		
		// --------------------------------------------------------------------------------------
		if(args[0].equalsIgnoreCase("resume")) {
			if(TimerTask.RUN) {
				sender.sendMessage(ChatColor.RED + "Game is already running!");
				return false;
			}
			TimerTask.setRunning(true);
			this.main.bossbar.setTitle(this.main.currentChallengeType + ": " + this.main.currentChallenge);
			Bukkit.broadcastMessage(ChatColor.RED + "---------- Game resumed! ---------");
			return true;
		}
		
		// --------------------------------------------------------------------------------------
		if(args[0].equalsIgnoreCase("skip")) {
			if(!TimerTask.RUN) {
				sender.sendMessage(ChatColor.RED + "Game is not running!");
				return false;
			}
			this.main.randomChallengePick();
			return true;
		}
		
		// --------------------------------------------------------------------------------------
		if(args[0].equalsIgnoreCase("targetScore")) {
			try {
				Main.setScoreToWin(Integer.parseInt(args[1]));
			} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
				sender.sendMessage(ChatColor.RED + "Please specify a valid number!");
				return false;
			}
			return true;
		}
		
		
		return true;
	}

}
