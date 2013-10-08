package nl.Steffion.BlockHunt.Listeners;

import me.libraryaddict.disguise.DisguiseAPI;
import nl.Steffion.BlockHunt.Arena;
import nl.Steffion.BlockHunt.Arena.ArenaState;
import nl.Steffion.BlockHunt.ArenaHandler;
import nl.Steffion.BlockHunt.ConfigC;
import nl.Steffion.BlockHunt.W;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OnEntityDamageByEntityEvent implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		Player player = null;
		if (event.getEntity() instanceof Player) {
			player = (Player) event.getEntity();
		}
		
		Player damager = null;

		if (event.getDamager() instanceof Player) {
			damager = (Player)event.getDamager();
		}
		
		if (event.getDamager() instanceof Projectile) {
			Entity projectile = (Projectile)event.getDamager();
			projectile.remove();
			
			LivingEntity shooter = ((Projectile)event.getDamager()).getShooter();
			if (shooter instanceof Player) {
				damager = (Player)shooter;
			}
		}

		if (player != null && damager != null) {
			for (Arena arena : W.arenaList) {
				if (arena.playersInArena.contains(player)) {
					if (arena.gameState == ArenaState.WAITING
							|| arena.gameState == ArenaState.STARTING) {
						event.setCancelled(true);
					} else {
						if (arena.seekers.contains(player)
								&& arena.seekers.contains(damager)) {
							event.setCancelled(true);
						} else if (arena.playersInArena.contains(player)
								&& arena.playersInArena.contains(damager)
								&& !arena.seekers.contains(damager)
								&& !arena.seekers.contains(player)) {
							event.setCancelled(true);
						} else {
							player.getWorld().playSound(player.getLocation(), Sound.HURT_FLESH, 1, 1);

							if (event.getDamage() >= player.getHealth()) {
								player.setHealth(20);
								player.getActivePotionEffects().clear();
								event.setCancelled(true);

								DisguiseAPI.undisguiseToAll(player);
								W.pBlock.remove(player);

								if (!arena.seekers.contains(player)) {

									arena.seekers.add(player);
									ArenaHandler
											.sendFMessage(
													arena,
													ConfigC.normal_HiderDied,
													"playername-"
															+ player.getName(),
													"left-"
															+ (arena.playersInArena
																	.size() - arena.seekers
																	.size()));
								} else {
									ArenaHandler.sendFMessage(arena,
											ConfigC.normal_SeekerDied,
											"playername-" + player.getName(),
											"secs-" + arena.waitingTimeSeeker);
								}

								player.getInventory().clear();
								player.updateInventory();

								if (arena.seekers.size() >= arena.playersInArena
										.size()) {
									ArenaHandler.seekersWin(arena);
								} else {
									DisguiseAPI.undisguiseToAll(player);
									W.seekertime.put(player,
											arena.waitingTimeSeeker);
									player.teleport(arena.seekersWarp);
									player.setGameMode(GameMode.SURVIVAL);
								}
							}
						}
					}
				}
			}
		}
	}
}
