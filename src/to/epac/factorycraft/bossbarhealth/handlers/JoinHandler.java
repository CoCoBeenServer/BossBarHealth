package to.epac.factorycraft.bossbarhealth.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;

public class JoinHandler implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		HealthBar bar = HealthBar.bars.get(player);
		
		if (bar == null) {
			bar = new HealthBar();
			bar.update(player, null, 0.0, true);
		}
		else
			bar.update(player, null, 0.0, false);
	}
}
