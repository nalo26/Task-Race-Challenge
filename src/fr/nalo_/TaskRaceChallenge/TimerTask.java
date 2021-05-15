package fr.nalo_.TaskRaceChallenge;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nalo_.Scoreboard.FastBoard;

public class TimerTask extends BukkitRunnable {

	private Main main;
	public static boolean RUN = false;
	public static int DEFAULT_TIMER_VALUE = 300; // 5 minutes
	private static int default_timer_value; // time in seconds before auto next task
	private static int timeLeft;
	private static int timeSpent;
	
	public TimerTask(Main main, int timer) {
		this.main = main;
		default_timer_value = timer;
		timeSpent = 0;
		resetTimer();
	}
	public TimerTask(Main main) {
		this(main, DEFAULT_TIMER_VALUE);
	}
	
	@Override
	public void run() {
		this.updateBoard();
		
		if(RUN) {
			timeLeft--;
			timeSpent++;
			this.main.bossbar.setProgress((float)timeLeft / (float)default_timer_value);
			
			if(timeLeft <= 0) {
				resetTimer();
				this.main.randomChallengePick();
			}
		}
		
	}
	
	private void updateBoard() {
		List<String> sortedPlayers = this.sortPlayers();
		sortedPlayers.add(0, ChatColor.YELLOW + formatTime(timeSpent));
		sortedPlayers.add(1, ChatColor.GOLD + "Goal: " + ChatColor.RED + Main.pointsToWin);
		for(FastBoard board : this.main.boards.values()) {
			board.updateLines(sortedPlayers);
		}
	}
	
	public static void setRunning(boolean state) {
		RUN = state;
	}
	
	public static void resetTimer() {
		timeLeft = default_timer_value;
	}
	
	public String formatTime(int secs) {
		return String.format("%02d:%02d:%02d", secs / 3600, (secs % 3600) / 60, secs % 60);
	}
	
	private List<String> sortPlayers() {
		Map<UUID, Integer> sortedMap = this.main.players.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(
						Map.Entry::getKey, 
						Map.Entry::getValue, 
						(oldValue, newValue) -> oldValue, LinkedHashMap::new));
		
		List<String> result = new ArrayList<>();
		for(Map.Entry<UUID, Integer> entry : sortedMap.entrySet()) {
			String player = Bukkit.getOfflinePlayer(entry.getKey()).getName();
			result.add(ChatColor.WHITE + player + ChatColor.GRAY + ": " + ChatColor.GREEN + entry.getValue());
		}
		return result;
	}
}
