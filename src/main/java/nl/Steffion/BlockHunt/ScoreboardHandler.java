package nl.Steffion.BlockHunt;

import java.util.HashMap;

import nl.Steffion.BlockHunt.Arena.ArenaState;
import nl.Steffion.BlockHunt.Managers.MessageM;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardHandler {
	
	static HashMap<String, Scoreboard> originalScoreboards = new HashMap<String, Scoreboard>();
	
	public static void createScoreboard(Arena arena) {
		if ((Boolean) W.config.get(ConfigC.scoreboard_enabled) == true) {
			Scoreboard board = arena.scoreboard;
			if (board.getObjective(arena.arenaName) != null) {
				updateScoreboard(arena);
				return;
			}

			Objective object = board.registerNewObjective(arena.arenaName,
					"dummy");
			object.setDisplaySlot(DisplaySlot.SIDEBAR);
			object.setDisplayName(MessageM.replaceAll((String) W.config
					.get(ConfigC.scoreboard_title)));
			Score timeleft = object.getScore(Bukkit.getOfflinePlayer(MessageM
					.replaceAll((String) W.config
							.get(ConfigC.scoreboard_timeleft))));
			timeleft.setScore(arena.timer);
			Score seekers = object.getScore(Bukkit.getOfflinePlayer(MessageM
					.replaceAll((String) W.config
							.get(ConfigC.scoreboard_seekers))));
			seekers.setScore(arena.seekers.size());
			Score hiders = object.getScore(Bukkit.getOfflinePlayer(MessageM
					.replaceAll((String) W.config
							.get(ConfigC.scoreboard_hiders))));
			hiders.setScore(arena.playersInArena.size() - arena.seekers.size());
			
			for (ItemStack blockType : arena.disguiseBlocks) {
				Score block = object.getScore(Bukkit.getOfflinePlayer(formatMaterialName(blockType.getType().name())));
				block.setScore(1);
			}
			
			if (arena.gameState == ArenaState.INGAME) {
				for (Player pl : arena.playersInArena) {
					if (!ScoreboardHandler.originalScoreboards.containsKey(pl.getName()))
						ScoreboardHandler.originalScoreboards.put(pl.getName(), pl.getScoreboard());
					
					pl.setScoreboard(board);
				}
			} else {
				for (Player pl : arena.playersInArena) {
					if (ScoreboardHandler.originalScoreboards.containsKey(pl.getName())) {
						pl.setScoreboard(ScoreboardHandler.originalScoreboards.get(pl.getName()));
						ScoreboardHandler.originalScoreboards.remove(pl.getName());
					}
				}
			}
		}
	}

	private static String formatMaterialName(String name) {
		name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
		name = name.replaceAll("_", " ");
		
		if (name.length() > 15) {
			name = name.substring(0, 15);
		}
		
		return name;
	}

	public static void updateScoreboard(Arena arena) {
		if ((Boolean) W.config.get(ConfigC.scoreboard_enabled) == true) {
			Scoreboard board = arena.scoreboard;
			Objective object = board.getObjective(DisplaySlot.SIDEBAR);
			object.setDisplayName(MessageM.replaceAll((String) W.config
					.get(ConfigC.scoreboard_title)));
			Score timeleft = object.getScore(Bukkit.getOfflinePlayer(MessageM
					.replaceAll((String) W.config
							.get(ConfigC.scoreboard_timeleft))));
			timeleft.setScore(arena.timer);
			Score seekers = object.getScore(Bukkit.getOfflinePlayer(MessageM
					.replaceAll((String) W.config
							.get(ConfigC.scoreboard_seekers))));
			seekers.setScore(arena.seekers.size());
			Score hiders = object.getScore(Bukkit.getOfflinePlayer(MessageM
					.replaceAll((String) W.config
							.get(ConfigC.scoreboard_hiders))));
			hiders.setScore(arena.playersInArena.size() - arena.seekers.size());
			
			for (ItemStack blockType : arena.disguiseBlocks) {
				Score block = object.getScore(Bukkit.getOfflinePlayer(formatMaterialName(blockType.getType().name())));
				block.setScore(0);
			}
			
			if (arena.gameState == ArenaState.INGAME) {
				for (Player pl : arena.playersInArena) {
					if (!ScoreboardHandler.originalScoreboards.containsKey(pl.getName()))
						ScoreboardHandler.originalScoreboards.put(pl.getName(), pl.getScoreboard());
					
					pl.setScoreboard(board);
				}
			} else {
				for (Player pl : arena.playersInArena) {
					if (ScoreboardHandler.originalScoreboards.containsKey(pl.getName())) {
						pl.setScoreboard(ScoreboardHandler.originalScoreboards.get(pl.getName()));
						ScoreboardHandler.originalScoreboards.remove(pl.getName());
					}
				}
			}
		}
	}

	public static void removeScoreboard(Player player) {
		if (ScoreboardHandler.originalScoreboards.containsKey(player.getName())) {
			player.setScoreboard(ScoreboardHandler.originalScoreboards.get(player.getName()));
			ScoreboardHandler.originalScoreboards.remove(player.getName());
		}
	}
}
