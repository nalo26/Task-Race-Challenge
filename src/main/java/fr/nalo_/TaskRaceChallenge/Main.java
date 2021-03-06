package fr.nalo_.TaskRaceChallenge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.nalo_.ChallengesEnum.Advancements;
import fr.nalo_.ChallengesEnum.Deaths;
import fr.nalo_.ChallengesEnum.Kills;
import fr.nalo_.Scoreboard.FastBoard;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {
	
	public static int pointsToWin = 5;

	public final Map<UUID, FastBoard> boards = new HashMap<>();
	public final Map<UUID, Integer> players = new HashMap<>();
	public final Map<String, List<String>> challenges = new HashMap<>();
	public String currentChallengeType;
	public String currentChallenge;
	public BossBar bossbar;
	
	public void onEnable() {
		System.out.println("[TaskRaceChallenge] Plugin just started!");
		getCommand("race").setExecutor(new Race(this));
		getServer().getPluginManager().registerEvents(new EventListener(this), this);
		fillChallenges();
		
		this.bossbar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
		this.bossbar.setVisible(false);
	}

	public void onDisable() {
		System.out.println("[TaskRaceChallenge] Plugin just stopped!");
	}
	
	public static void setScoreToWin(int score) {
		pointsToWin = score;
	}
	
	public void addPlayerPoint(Player player) {
		UUID playerUUID = player.getUniqueId();
		int score = this.players.get(playerUUID).intValue() + 1;
		this.players.put(playerUUID, score);
		for(UUID uuid : this.players.keySet()) {
			Bukkit.getPlayer(uuid).sendTitle(player.getDisplayName() + " won a point!", "", 10, 5 * 20, 10);
		}

		this.challenges.get(this.currentChallengeType).remove(this.currentChallenge);
		this.updateBoard(true);
		
		if(score >= pointsToWin) {
			TimerTask.setRunning(false);
			String text = "Victory of " + player.getDisplayName() + "!";
			this.bossbar.setTitle(text);
			for(UUID uuid : this.players.keySet()) {
				Bukkit.getPlayer(uuid).sendTitle(text, "", 10, 5 * 20, 10);
			}
			return;
		}
		
		this.randomChallengePick();
	}
	
	public void randomChallengePick() {
		Random rand = new Random();
		Boolean valid = false;
		String chalType = "", chal = "";
		
		while(!valid) {
			Object[] keys = this.challenges.keySet().toArray();
			chalType = (String) keys[rand.nextInt(keys.length)];
			chal = this.challenges.get(chalType).get(rand.nextInt(this.challenges.get(chalType).size()));
			
			valid = isValid(chalType, chal);
		}
		
		this.currentChallengeType = chalType;
		this.currentChallenge = chal;
		
		String text = this.currentChallengeType + ": " + this.currentChallenge;
		this.bossbar.setTitle(text);
		Bukkit.broadcastMessage(ChatColor.YELLOW + text);

		TimerTask.resetTimer();
	}
	
	public boolean isValid(String chalType, String chal) {
		if(chalType.equals(this.currentChallengeType)) return false;
		if(chalType.equals("Kill") || chalType.equals("Death")) return true;
		
		if(chalType.equals("Item")) {
			for(UUID uuid : this.players.keySet()) {
				Player player = Bukkit.getPlayer(uuid);
				if(!player.isOnline()) continue;
				if(player.getInventory().contains(Material.valueOf(chal.toUpperCase()))) return false;
			}
			return true;
		}
		
		if(chalType.equals("Advancement")) {
			Advancement adv = getAdvancement(chal);
			for(UUID uuid : this.players.keySet()) {
				Player player = Bukkit.getPlayer(uuid);
				if(!player.isOnline()) continue;
				if(player.getAdvancementProgress(adv).isDone()) return false;
			}
			return true;
		}
		
		return true;
	}
	
	public Advancement getAdvancement(String name) {
		String advName = Advancements.getFromDescription(name);
		
		for(Iterator<Advancement> iter = Bukkit.getServer().advancementIterator(); iter.hasNext(); ) {
			Advancement adv = iter.next();
			if(adv.getKey().getKey().equalsIgnoreCase(advName)) return adv;
		}
		return null;
	}
	
	private void fillChallenges() {
		
		// Advancements
		List<String> advancements = new ArrayList<>();
		for(Advancements d : Advancements.values()) {
			advancements.add(d.getDescription());
		}
		this.challenges.put("Advancement", advancements);
		
		// Death Messages
		List<String> deaths = new ArrayList<>();
		for(Deaths d : Deaths.values()) {
			deaths.add(d.getDescription());
		}
		this.challenges.put("Death", deaths);
		
		// Items
		List<String> items = new ArrayList<>();
		for(Material m : Material.values()) {
			items.add(m.name().toLowerCase());
		}
		this.challenges.put("Item", items);
		
		// Kills
		List<String> kills = new ArrayList<>();
		for(Kills k : Kills.values()) {
			kills.add(k.toString());
		}
		this.challenges.put("Kill", kills);
		
	}

	public void updateBoard(boolean updatePlayer) {
		if(!updatePlayer) {
			for(FastBoard board : this.boards.values()) {
				board.updateLine(0, ChatColor.YELLOW + TimerTask.formatTime(TimerTask.timeSpent));
				board.updateLine(1, ChatColor.GOLD + "Goal: " + ChatColor.RED + Main.pointsToWin);
			}
			return;
		}
		List<String> sortedPlayers = this.sortPlayers();
		sortedPlayers.add(0, ChatColor.YELLOW + TimerTask.formatTime(TimerTask.timeSpent));
		sortedPlayers.add(1, ChatColor.GOLD + "Goal: " + ChatColor.RED + Main.pointsToWin);
		for(FastBoard board : this.boards.values()) {
			board.updateLines(sortedPlayers);
		}		
	}

	private List<String> sortPlayers() {
		Map<UUID, Integer> sortedMap = this.players.entrySet()
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
		Collections.reverse(result);
		return result;
	}

}









