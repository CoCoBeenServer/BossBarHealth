package to.epac.factorycraft.bossbarhealth.handlers;

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

import to.epac.factorycraft.bossbarhealth.BossBarHealth;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar.BarType;

public class DamageHandler implements Listener {
	
	private BossBarHealth plugin = BossBarHealth.inst();

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Entity victim = event.getEntity();
		
		if (event.isCancelled()) return;
		// If victim is not LivintEntity (eg. Ender Crystal)
		if (!(victim instanceof LivingEntity)) return;
		
		
		
		Bukkit.getScheduler().runTask(plugin, () -> {
			// If the victim is any player, update his bar
			if (victim instanceof Player) {
				
				Player player = (Player) victim;
				HealthBar bar = HealthBar.bars.get(player);
				
				if (bar != null) {
					bar.update(player, BarType.HPLOST, event.getFinalDamage() * -1, false);
					bar.setLastUpdate(System.currentTimeMillis());
					
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						bar.attemptUpdate(player);
					}, plugin.getConfigManager().getDurationNormal());
				}
			}
			
			
			
			// If EnemyBar is not enabled
			if (!plugin.getConfigManager().isEnemyEnabled()) return;
			if (plugin.getConfigManager().getBlacklist().contains(victim.getType().toString())) return;
			
			
			
			// If Entity Damage By Entity
			if (event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent) event;
				
				Player damager = null;
				
				// If entity damaged by player
				if (edbeEvent.getDamager() instanceof Player) {
					// If damager isn't victim himself
					if (!edbeEvent.getDamager().equals(victim))
						damager = (Player) edbeEvent.getDamager();
				}
				// If player damaged by entity projectile
				else if (edbeEvent.getDamager() instanceof Projectile) {
					Projectile proj = (Projectile) edbeEvent.getDamager();
					// If shooter is player
					if (proj.getShooter() instanceof Player)
						damager = (Player) proj.getShooter();
				}
				
				if (damager != null) {
					HealthBar bar = HealthBar.bars.get(damager);
					
					if (bar != null) {
						boolean create = bar.getTarget() == null;
						bar.updateEnemy(damager, (LivingEntity) victim, BarType.HPLOST, event.getFinalDamage() * -1, create);
						bar.setEnemyLastUpdate(System.currentTimeMillis());
					}
				}
			}
			
			
			
			// Update everyone's EnemyBar if their target is the victim
			for (Map.Entry<Player, HealthBar> entry : HealthBar.bars.entrySet()) {
				Player player = entry.getKey();
				HealthBar bar = entry.getValue();
				
				if (bar.getTarget() != null && bar.getTarget().equals(victim)) {
					
					bar.updateEnemy(player, (LivingEntity) victim, BarType.HPLOST, event.getDamage() * -1, false);
					
					Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
						if (bar.attemptRemove())
							if (plugin.getConfigManager().isSelfEnabled())
								if (!HealthBar.hide.contains(player.getUniqueId()))
									bar.getSelfBar().addPlayer(player);
						
					}, plugin.getConfigManager().getEnemyDurNormal());
				}
			}
		});
	}
}