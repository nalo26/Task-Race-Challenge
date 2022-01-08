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
			help(sender);
			return false;
		}
		
		if(args[0].equalsIgnoreCase("start")) {
			return start(sender, args);
		}
		
		if(args[0].equalsIgnoreCase("pause")) {
			return pause(sender);
		}
		
		if(args[0].equalsIgnoreCase("resume")) {
			return resume(sender);
		}
		
		if(args[0].equalsIgnoreCase("skip")) {
			return skip(sender);
		}
		
		if(args[0].equalsIgnoreCase("goal")) {
			return setGoalPoints(sender, args);
		}
		
		help(sender);
		return true;
	}
	
	public boolean start(CommandSender sender, String[] args) {
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
	
	public boolean pause(CommandSender sender) {
		if(!TimerTask.RUN) {
			sender.sendMessage(ChatColor.RED + "Game is already paused!");
			return false;
		}
		TimerTask.setRunning(false);
		this.main.bossbar.setTitle("-- PAUSED --");
		Bukkit.broadcastMessage(ChatColor.RED + "---------- Game paused! ----------");
		return true;
	}
	
	public boolean resume(CommandSender sender) {
		if(TimerTask.RUN) {
			sender.sendMessage(ChatColor.RED + "Game is already running!");
			return false;
		}
		TimerTask.setRunning(true);
		this.main.bossbar.setTitle(this.main.currentChallengeType + ": " + this.main.currentChallenge);
		Bukkit.broadcastMessage(ChatColor.RED + "---------- Game resumed! ---------");
		return true;
	}
	
	public boolean skip(CommandSender sender) {
		if(!TimerTask.RUN) {
			sender.sendMessage(ChatColor.RED + "Game is not running!");
			return false;
		}
		TimerTask.resetTimer();
		this.main.randomChallengePick();
		return true;
	}
	
	public boolean setGoalPoints(CommandSender sender, String[] args) {
		try {
			int score = Integer.parseInt(args[1]);
			Main.setScoreToWin(score);
			Bukkit.broadcastMessage(ChatColor.GOLD + "Score target now set to " + ChatColor.BOLD + score);
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			sender.sendMessage(ChatColor.RED + "Please specify a valid number!");
			return false;
		}
		return true;
	}
	
	public void help(CommandSender sender) {
		String msg = "";
		msg += ChatColor.GOLD + "start [length]\t" + ChatColor.GRAY + "| Start the game (tasks will last 'length' seconds (" + TimerTask.DEFAULT_TIMER_VALUE + "))\n";
		msg += ChatColor.GOLD + "pause\t\t"        + ChatColor.GRAY + "| Pause the running game\n";
		msg += ChatColor.GOLD + "resume\t\t"       + ChatColor.GRAY + "| Resume the paused game\n";
		msg += ChatColor.GOLD + "skip\t\t"         + ChatColor.GRAY + "| Skip the current task\n";
		msg += ChatColor.GOLD + "goal <score>\t"   + ChatColor.GRAY + "| Set the goal score to win to 'score'\n";
		msg += ChatColor.GOLD + "help\t\t"         + ChatColor.GRAY + "| Print this message\n";

		sender.sendMessage(msg);
	}

}
