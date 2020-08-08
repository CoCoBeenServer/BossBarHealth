package to.epac.factorycraft.bossbarhealth.handlers;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import to.epac.factorycraft.bossbarhealth.BossBarHealth;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar.BarType;

public class RegainHealthHandler implements Listener {
	
	private BossBarHealth plugin = BossBarHealth.inst();
	
	@EventHandler
	public void onRegainHealth(EntityRegainHealthEvent event) {
		LivingEntity entity = (LivingEntity) event.getEntity();
		
		Bukkit.getScheduler().runTask(plugin, () -> {
			
			// If the entity regaining health is player
			if (entity instanceof Player) {
				Player player = (Player) entity;
				
				// If SelfBar is not enabled
				if (!plugin.getConfigManager().isSelfEnabled()) return;
				
				HealthBar bar = HealthBar.bars.get(player);
				
				if (bar != null) {
					// Update with HpGain format
					bar.update(player, BarType.HPGAIN, event.getAmount(), false);
					bar.setLastUpdate(System.currentTimeMillis());
					
					// Attempt to update SelfBar after fade-out time
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						bar.attemptUpdate(player);
					}, plugin.getConfigManager().getDurationNormal());
				}
			}
			
			
			
			// If EnemyBar is not enabled
			if (!plugin.getConfigManager().isEnemyEnabled()) return;
			// If entity's type is not in blacklist
			if (plugin.getConfigManager().getBlacklist().contains(entity.getType().toString())) return;
			
			for (Map.Entry<Player, HealthBar> entry : HealthBar.bars.entrySet()) {
				Player player = entry.getKey();
				HealthBar bar = entry.getValue();
				
				if (bar.getTarget() != null && bar.getTarget().equals(entity)) {
					bar.updateEnemy(player, entity, BarType.HPGAIN, event.getAmount(), false);
					
					int delay = plugin.getConfigManager().getEnemyDurNormal();
					
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						if (bar.attemptRemove(delay))
							if (!HealthBar.hide.contains(player.getUniqueId()))
								bar.getSelfBar().addPlayer(player);
						
					}, delay);
				}
			}
		});
	}
}
