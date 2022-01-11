package fr.nalo_.TaskRaceChallenge;

import org.bukkit.scheduler.BukkitRunnable;

public class TimerTask extends BukkitRunnable {

	private Main main;
	public static boolean RUN = false;
	public static int DEFAULT_TIMER_VALUE = 300; // 5 minutes
	private static int default_timer_value; // time in seconds before auto next task
	private static int timeLeft;
	public static int timeSpent;
	
	public TimerTask(Main main, int timer) {
		this.main = main;
		default_timer_value = timer;
		timeSpent = 0;
		resetTimer();
		this.main.updateBoard(true);
	}
	public TimerTask(Main main) {
		this(main, DEFAULT_TIMER_VALUE);
	}
	
	@Override
	public void run() {
		this.main.updateBoard(false);
		
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
	
	public static void setRunning(boolean state) {
		RUN = state;
	}
	
	public static void resetTimer() {
		timeLeft = default_timer_value;
	}
	
	public static String formatTime(int secs) {
		return String.format("%02d:%02d:%02d", secs / 3600, (secs % 3600) / 60, secs % 60);
	}
}
