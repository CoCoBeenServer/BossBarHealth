package to.epac.factorycraft.bossbarhealth.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import to.epac.factorycraft.bossbarhealth.BossBarHealth;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar.BarType;

public class PlayerMoveHandler {
	
	public static void start() {
		
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					
					HealthBar bar = HealthBar.bars.get(player);
					
					if (bar == null) {
						bar = new HealthBar();
						bar.update(player, BarType.NORMAL, 0.0, true);
					}
					else {
						bar.update(player, bar.getType(), bar.getLostgain(), false);
						
						if (bar.getTarget() != null)
							bar.updateEnemy(player, bar.getTarget(), bar.getEnemyType(), bar.getEnemyLostgain(), false);
					}
				}
			}
		};
		runnable.runTaskTimer(BossBarHealth.inst(), 0, 20);
	}
}
