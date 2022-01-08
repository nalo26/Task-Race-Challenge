package fr.nalo_.TaskRaceChallenge;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import fr.nalo_.ChallengesEnum.Advancements;
import fr.nalo_.ChallengesEnum.Deaths;
import fr.nalo_.ChallengesEnum.Kills;
import fr.nalo_.Scoreboard.FastBoard;

public class EventListener implements Listener {
	
	private Main main;

	public EventListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		UUID player_uuid = player.getUniqueId();
		FastBoard board = new FastBoard(player);
		
		board.updateTitle(ChatColor.YELLOW + "" + ChatColor.BOLD + "Challenges");
		board.updateLines("Waiting for", "the game", "to start");
		
		this.main.boards.put(player_uuid, board);
		this.main.bossbar.addPlayer(player);
		if(TimerTask.RUN && !this.main.players.containsKey(player.getUniqueId())) {
			player.setGameMode(GameMode.SPECTATOR);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		FastBoard board = this.main.boards.remove(player.getUniqueId());
		this.main.bossbar.removePlayer(player);
		if(board != null) board.delete();
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		Entity victim = e.getEntity();
		Entity attacker = e.getEntity().getKiller();
		
		if(!TimerTask.RUN) return;
		
		if(victim instanceof Player) { // DEATH -----------------------------------------------------------
			Player player = (Player) victim;
			DamageCause dc = victim.getLastDamageCause().getCause();
			
			if(!this.main.currentChallengeType.equalsIgnoreCase("Death")) return;
			
			if(dc.equals(DamageCause.valueOf(Deaths.getFromDescription(this.main.currentChallenge)))) {
				this.main.addPlayerPoint(player);
			}
			
			return;
		}
		
		if(attacker instanceof Player) { // KILL -----------------------------------------------------------
			Player player = (Player) attacker;
			
			if(!TimerTask.RUN) return;
			if(!this.main.currentChallengeType.equalsIgnoreCase("Kill")) return;
			
			EntityType type = victim.getType();
			if(type.equals(EntityType.valueOf(Kills.toEnumName(this.main.currentChallenge)))) {
				this.main.addPlayerPoint(player);
			}
			
			return;
		}
	}
	
	@EventHandler
	public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent e) {
		// ADVANCEMENT -------------------------------------------------------------------------------------
		Advancement advancement = e.getAdvancement();
		Player player = e.getPlayer();

		if(!TimerTask.RUN) return;
		if(!this.main.currentChallengeType.equalsIgnoreCase("Advancement")) return;
		
		String advName = Advancements.getFromDescription(this.main.currentChallenge);

		if(advancement.getKey().getKey().equalsIgnoreCase(advName)) {
			this.main.addPlayerPoint(player);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		// ITEM -------------------------------------------------------------------------------------
		Player player = (Player) e.getWhoClicked();
		ItemStack item = e.getCurrentItem();
		if(item == null) return;
		Material material = item.getType();

		if(!TimerTask.RUN) return;
		if(!this.main.currentChallengeType.equalsIgnoreCase("Item")) return;
		if(material.equals(Material.valueOf(this.main.currentChallenge.toUpperCase()))) {
			this.main.addPlayerPoint(player);
		}

	}

	@EventHandler
	public void onInventoryPickUpItem(EntityPickupItemEvent e) {
		EntityType entity = e.getEntityType();
		Item item = e.getItem();
		Material material = item.getItemStack().getType();

		if(!TimerTask.RUN) return;
		if(!entity.equals(EntityType.PLAYER)) return;
		Player player = (Player) e.getEntity();
		
		if(!this.main.currentChallengeType.equalsIgnoreCase("Item")) return;

		if(material.equals(Material.valueOf(this.main.currentChallenge.toUpperCase()))) {
			this.main.addPlayerPoint(player);
		}
		
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		EntityType entity = e.getEntityType();

		if(TimerTask.RUN) return;
		if(!entity.equals(EntityType.PLAYER)) return;

		e.setDamage(0.0);
		e.setCancelled(true);
	}
}
 