package to.epac.factorycraft.bossbarhealth.hpbar;

import static to.epac.factorycraft.bossbarhealth.BossBarHealth.usePapi;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.clip.placeholderapi.PlaceholderAPI;
import to.epac.factorycraft.bossbarhealth.BossBarHealth;
import to.epac.factorycraft.bossbarhealth.Utils.Utils;

public class HealthBar {
	
	public enum BarType {
		NORMAL, HPLOST, HPGAIN;
	}
	
	private BossBarHealth plugin = BossBarHealth.inst();
	
	public static HashMap<Player, HealthBar> bars = new HashMap<>();
	public static List<UUID> hide = new ArrayList<>();
	public static List<BukkitRunnable> refresh = new ArrayList<>();
	
	private BossBar self;
	private BossBar enemy;
	private LivingEntity target;
	private long lastUpdate;
	private long e_lastUpdate;
	
	public HealthBar() {}
	public HealthBar(BossBar self) {
		this.self = self;
	}
	public HealthBar(BossBar self, BossBar enemy, LivingEntity target) {
		this.self = self;
		this.enemy = enemy;
		this.target = target;
	}

	
	
	public static void updateAll() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			
			if (!hide.contains(player.getUniqueId())) {
				
				HealthBar bar = bars.get(player);
				
				if (bar == null) {
					bar = new HealthBar();
					bar.update(player, null, 0.0, true);
				}
				else
					bar.update(player, null, 0.0, false);
			}
		});
	}
	
	/*public static void createAll() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			
			
			
			
		});
	}*/
	
	public static void removeAll() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			
			if (!hide.contains(player.getUniqueId())) {
				
				HealthBar bar = bars.get(player);
				
				if (bar != null) {
					bar.remove();
					bar.removeEnemy();
				}
			}
		});
	}
	
	
	
	/**
	 * Update SelfBar when it is expired (elapsed time - expire time >= -20)
	 * 
	 * @param player Player to update
	 */
	public void attemptUpdate(Player player) {
		long elapsedTime = System.currentTimeMillis() - lastUpdate;
		long confVal = plugin.getConfigManager().getEnemyDurNormal() / 20 * 1000L;
		
		if (elapsedTime - confVal >= -20)
			update(player, null, 0.0, false);
	}
	
	
	public void update(Player player, @Nullable BarType type, double lostgain, boolean create) {
		double hp = player.getHealth() * plugin.getConfigManager().getScale();
		double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * plugin.getConfigManager().getScale();
		
		
		
		// Apply decimal format
		String pattern = "#";
		for (int i = 0; i < plugin.getConfigManager().getDecimal(); i++)
			pattern += (i == 0 ? "." : "#");
		DecimalFormat df = new DecimalFormat(pattern);
		
		
		
		String title = "";
		// TODO - See if lostgain comparison usable or not, if not remove it
		if ((type == null && lostgain < 0.0) || type == BarType.HPLOST)
			title = plugin.getConfigManager().getFormatHpLost().replaceAll("%change%", df.format(lostgain * plugin.getConfigManager().getScale()));
		else if ((type == null && lostgain > 0.0) || type == BarType.HPGAIN)
			title = plugin.getConfigManager().getFormatHpGain().replaceAll("%change%", "+" + df.format(lostgain * plugin.getConfigManager().getScale()));
		else if ((type == null && lostgain == 0.0) || type == BarType.NORMAL)
			title = plugin.getConfigManager().getFormatNormal();
			
		
		
		
		// Update placeholders
		title = title
				.replaceAll("%name%", 		 player.getName())
				.replaceAll("%displayname%", player.getDisplayName())
				.replaceAll("%hp%", 		 df.format(hp))
				.replaceAll("%max%", 		 df.format(max))
				.replaceAll("%hp_int%", 	 (int) Math.ceil(hp) + "")
				.replaceAll("%max_int%", 	 (int) Math.ceil(max) + "")
				
				.replaceAll("%direction_cardinalfull%", Utils.getDirection(player.getLocation().getYaw(), "CARDINAL_FULL"))
				.replaceAll("%direction_ordinalfull%", 	Utils.getDirection(player.getLocation().getYaw(), "ORDINAL_FULL"))
				.replaceAll("%direction_cardinal%", 	Utils.getDirection(player.getLocation().getYaw(), "CARDINAL"))
				.replaceAll("%direction_ordinal%", 		Utils.getDirection(player.getLocation().getYaw(), "ORDINAL"))
				.replaceAll("%direction_number%", 		Utils.getDirection(player.getLocation().getYaw(), "NUMBER"));
		title = ChatColor.translateAlternateColorCodes('&', title);
		// If PlaceholderAPI is installed
		if (usePapi)
			title = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, title);
		
		
		
		// If we are creating a new HealthBar
		if (create) {
			self = Bukkit.createBossBar(title, plugin.getConfigManager().getColor(),
					plugin.getConfigManager().getStyle(), new BarFlag[0]);
			
			// If SelfBar is enabled
			if (plugin.getConfigManager().isSelfEnabled())
				// If player didn't hide his own bar
				if (!hide.contains(player.getUniqueId()))
					// Add him to BossBar display list
					self.addPlayer(player);
			
			// Update lastUpdate
			lastUpdate = System.currentTimeMillis();
			
			// Put it in list
			bars.put(player, this);
		}
		// If we are not creating a new one, then just update the title
		else {
			self.setTitle(title);
		}
		
		
		
		// Update SelfBar progress
		if (hp / max > 1.0)
			self.setProgress(1.0);
		else if (hp / max < 0.0)
			self.setProgress(0.0);
		else
			self.setProgress(hp / max);
	}
	
	
	
	
	public void updateEnemy(Player player, LivingEntity target, @Nullable BarType type, double lostgain, boolean create) {
		if (plugin.getConfigManager().getOverride())
			self.removePlayer(player);
		
		double hp = player.getHealth() * plugin.getConfigManager().getScale();
		double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * plugin.getConfigManager().getScale();
		
		double e_hp = target.getHealth() * plugin.getConfigManager().getEnemyScale();
		double e_max = target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * plugin.getConfigManager().getEnemyScale();
		double e_lostgain = lostgain * plugin.getConfigManager().getScale();
		
		
		
		String pattern = "#";
		for (int i = 0; i < plugin.getConfigManager().getDecimal(); i++)
			pattern += (i == 0 ? "." : "#");
		DecimalFormat df = new DecimalFormat(pattern);
		
		
		
		String title = "";
		if ((type == null && lostgain < 0.0) || type == BarType.HPLOST)
			title = plugin.getConfigManager().getEnemyFormatHpLost().replaceAll("%e_change%", df.format(e_lostgain));
		else if ((type == null && lostgain >= 0.0) || type == BarType.HPGAIN)
			title = plugin.getConfigManager().getEnemyFormatHpGain().replaceAll("%e_change%", "+" + df.format(e_lostgain));
		
		
		
		title = title
				// Self
				.replaceAll("%name%", 		 player.getName())
				.replaceAll("%displayname%", player.getDisplayName())
				.replaceAll("%hp%", 		 df.format(hp))
				.replaceAll("%max%", 		 df.format(max))
				.replaceAll("%hp_int%", 	 (int) Math.ceil(hp) + "")
				.replaceAll("%max_int%", 	 (int) Math.ceil(max) + "")
				
				.replaceAll("%direction_cardinalfull%", Utils.getDirection(player.getLocation().getYaw(), "CARDINAL_FULL"))
				.replaceAll("%direction_ordinalfull%", 	Utils.getDirection(player.getLocation().getYaw(), "ORDINAL_FULL"))
				.replaceAll("%direction_cardinal%", 	Utils.getDirection(player.getLocation().getYaw(), "CARDINAL"))
				.replaceAll("%direction_ordinal%", 		Utils.getDirection(player.getLocation().getYaw(), "ORDINAL"))
				.replaceAll("%direction_number%", 		Utils.getDirection(player.getLocation().getYaw(), "NUMBER"))
				
				// Enemy
				.replaceAll("%e_hp%", 		 df.format(e_hp))
				.replaceAll("%e_max%", 		 df.format(e_max))
				.replaceAll("%e_hp_int%", 	 (int) Math.ceil(e_hp) + "")
				.replaceAll("%e_max_int%", 	 (int) Math.ceil(e_max) + "")
				.replaceAll("%e_type%", 	 target.getType() + "")
				
				.replaceAll("%e_direction_cardinalfull%", Utils.getDirection(target.getLocation().getYaw(), "CARDINAL_FULL"))
				.replaceAll("%e_direction_ordinalfull%",  Utils.getDirection(target.getLocation().getYaw(), "ORDINAL_FULL"))
				.replaceAll("%e_direction_cardinal%",     Utils.getDirection(target.getLocation().getYaw(), "CARDINAL"))
				.replaceAll("%e_direction_ordinal%",      Utils.getDirection(target.getLocation().getYaw(), "ORDINAL"))
				.replaceAll("%e_direction_number%",       Utils.getDirection(target.getLocation().getYaw(), "NUMBER"));
		
		if (target instanceof Player) {
			title = title
					.replaceAll("%e_name%", ((Player) target).getName())
					.replaceAll("%e_displayname%", ((Player) target).getDisplayName());
		}
		else
			title = title
					.replaceAll("%e_name%", target.getName())
					.replaceAll("%e_displayname%", target.getCustomName() != null ? target.getCustomName() : "");
		
		title = ChatColor.translateAlternateColorCodes('&', title);
		
		if (usePapi)
			title = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, title);
		
		
		
		if (create) {
			enemy = Bukkit.createBossBar(title, plugin.getConfigManager().getEnemyColor(),
					plugin.getConfigManager().getEnemyStyle(), new BarFlag[0]);
			
			if (plugin.getConfigManager().isEnemyEnabled())
				if (!hide.contains(player.getUniqueId()))
					enemy.addPlayer(player);
			
			this.target = target;
			
			e_lastUpdate = System.currentTimeMillis();
			
			bars.put(player, this);
		}
		else {
			enemy.setTitle(title);
		}
		
		
		
		if (e_hp / e_max > 1.0)
			enemy.setProgress(1.0);
		else if (e_hp / e_max < 0.0)
			enemy.setProgress(0.0);
		else
			enemy.setProgress(e_hp / e_max);
	}
	
	public boolean attemptRemove() {
		long elapsedTime = System.currentTimeMillis() - e_lastUpdate;
		long confVal = plugin.getConfigManager().getEnemyDurNormal() / 20 * 1000L;
		if (elapsedTime - confVal >= -20) {
			removeEnemy();
			return true;
		}
		return false;
	}
	
	public void remove() {
		if (self != null)
			self.removeAll();
	}
	
	public void removeEnemy() {
		if (enemy != null) {
			enemy.removeAll();
			enemy = null;
			target = null;
		}
	}
	
	
	
	
	
	
	public BossBar getSelfBar() {
		return self;
	}
	public BossBar getEnemyBar() {
		return enemy;
	}
	public LivingEntity getTarget() {
		return target;
	}
	public long getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public long getEnemyLastUpdate() {
		return e_lastUpdate;
	}
	public void setEnemyLastUpdate(long lastUpdate) {
		this.e_lastUpdate = lastUpdate;
	}
}
