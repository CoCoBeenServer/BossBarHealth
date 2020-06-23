package to.epac.factorycraft.bossbarhealth.events;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import to.epac.factorycraft.bossbarhealth.BossBarHealth;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;

public class BossBarHealthHandler implements Listener {
	
	private BossBarHealth plugin = BossBarHealth.inst();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		HealthBar bar = HealthBar.bars.get(player);
		
		if (bar != null) {
			bar.update(player, 0.0);
		}
		else {
			bar = new HealthBar();
			bar.create(player);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		HealthBar bar = HealthBar.bars.get(player);
		
		if (bar != null)
			bar.remove();
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		if (!plugin.getConfigManager().isSelfEnabled()) return;
		
		Player player = event.getPlayer();
		Bukkit.getScheduler().runTask(plugin, () -> {
			
			HealthBar bar = HealthBar.bars.get(player);
			if (bar != null)
				bar.update(player, 0.0);
		});
	}

	@EventHandler
	public void onRegainHealth(EntityRegainHealthEvent event) {
		Entity entity = event.getEntity();
		
		Bukkit.getScheduler().runTask(plugin, () -> {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				
				if (!plugin.getConfigManager().isSelfEnabled()) return;
				
				else {
					HealthBar bar = HealthBar.bars.get(player);
					if (bar != null) {
						bar.update(player, event.getAmount());
						bar.setLastUpdate(System.currentTimeMillis());
						
						Bukkit.getScheduler().runTaskLater(plugin, () -> {
							bar.attemptUpdate(player);
							
						}, plugin.getConfigManager().getHpChangeDuration());
					}
				}
			}
			
			if (!plugin.getConfigManager().isEnemyEnabled()) return;
			
			for (Map.Entry<Player, HealthBar> entry : HealthBar.bars.entrySet()) {
				Player player = entry.getKey();
				HealthBar bar = entry.getValue();
				
				if (bar.getTarget() != null && bar.getTarget().equals(entity)) {
					bar.updateEnemy(player, (LivingEntity) entity, event.getAmount());
					
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						if (bar.attemptRemove())
							bar.getSelfBar().addPlayer(player);
						
					}, plugin.getConfigManager().getEnemyDuration());
				}
			}
		});
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Entity victim = event.getEntity();
		
		if (event.isCancelled()) return;
		
		Bukkit.getScheduler().runTask(plugin, () -> {
			
			// If the victim is any player, update his bar
			if (victim instanceof Player) {
				Player player = (Player) victim;
				HealthBar bar = HealthBar.bars.get(player);
				
				if (bar != null) {
					bar.update(player, event.getFinalDamage() * -1);
					bar.setLastUpdate(System.currentTimeMillis());
					
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						bar.attemptUpdate(player);
						
					}, plugin.getConfigManager().getHpChangeDuration());
				}
			}
			
			// If EnemyBar is not enabled
			if (!plugin.getConfigManager().isEnemyEnabled()) return;
			
			// If Entity Damage By Entity
			if (event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent) event;
				
				// If victim is not LivintEntity (eg. Ender Crystal)
				if (!(victim instanceof LivingEntity)) return;
				
				// If entity damaged by player
				if (edbeEvent.getDamager() instanceof Player) {
					Player player = (Player) edbeEvent.getDamager();
					HealthBar bar = HealthBar.bars.get(player);
					
					// If player damage player
					if (victim instanceof Player) {
						if (!player.equals((Player) victim)) {
							if (bar != null) {
								if (bar.getTarget() != null) {
									bar.updateEnemy(player, (LivingEntity) victim, event.getFinalDamage() * -1);
									bar.setEnemyLastUpdate(System.currentTimeMillis());
								}
								else
									bar.createEnemy(player, (LivingEntity) victim, event.getFinalDamage() * -1);
							}
						}
					}
					// If player damage entity
					else {
						if (bar != null) {
							if (bar.getTarget() != null) {
								bar.updateEnemy(player, (LivingEntity) victim, event.getFinalDamage() * -1);
								bar.setEnemyLastUpdate(System.currentTimeMillis());
							}
							else {
								bar.createEnemy(player, (LivingEntity) victim, event.getFinalDamage() * -1);
							}
						}
					}
				}
				// If player damaged by entity projectile
				else if (edbeEvent.getDamager() instanceof Projectile) {
					Projectile projectile = (Projectile) edbeEvent.getDamager();
					
					// If shooter is player
					if (projectile.getShooter() instanceof Player) {
						Player player = (Player) projectile.getShooter();
						HealthBar bar = HealthBar.bars.get(player);
						
						// If victim is player
						if (victim instanceof Player) {
							if (!player.equals((Player) victim)) {
								if (bar != null) {
									if (bar.getTarget() != null) {
										bar.updateEnemy(player, (LivingEntity) victim, event.getFinalDamage() * -1);
										bar.setEnemyLastUpdate(System.currentTimeMillis());
									}
									else
										bar.createEnemy(player, (LivingEntity) victim, event.getFinalDamage() * -1);
								}
							}
						}
					}
				}
			}
			
			// Update everyone's EnemyBar if their target is the victim
			for (Map.Entry<Player, HealthBar> entry : HealthBar.bars.entrySet()) {
				Player player = entry.getKey();
				HealthBar bar = entry.getValue();
				
				if (bar.getTarget() != null && bar.getTarget().equals(victim)) {
					
					bar.updateEnemy(player, (LivingEntity) victim, event.getDamage() * -1);
					
					Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
						if (bar.attemptRemove())
							if (plugin.getConfigManager().isSelfEnabled())
								bar.getSelfBar().addPlayer(player);
						
					}, plugin.getConfigManager().getEnemyDuration());
				}
			}
		});
	}
}