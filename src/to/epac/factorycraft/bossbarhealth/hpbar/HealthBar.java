package to.epac.factorycraft.bossbarhealth.hpbar;

import static to.epac.factorycraft.bossbarhealth.BossBarHealth.usePapi;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import to.epac.factorycraft.bossbarhealth.BossBarHealth;

public class HealthBar {
	
	private BossBarHealth plugin = BossBarHealth.inst();
	
	public static HashMap<Player, HealthBar> bars = new HashMap<Player, HealthBar>();
	public static List<UUID> hide = new ArrayList<UUID>();
	
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
				if (bar != null)
					bar.update(player, 0.0);
			}
		});
	}
	
	public static void createAll() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			
			if (!hide.contains(player.getUniqueId())) {
				HealthBar bar = bars.get(player);
				if (bar == null) {
					bar = new HealthBar();
					bar.create(player);
				}
			}
		});
	}
	
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
	
	public void attemptUpdate(Player player) {
		long elapsedTime = System.currentTimeMillis() - e_lastUpdate;
		long confVal = plugin.getConfigManager().getEnemyDuration() / 20 * 1000L;
		if (elapsedTime - confVal >= -20)
			update(player, 0.0);
	}
	
	public void update(Player player, double lostgain) {
		double hp = player.getHealth() * plugin.getConfigManager().getScale();
		double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * plugin.getConfigManager().getScale();
		
		
		
		String pattern = "#";
		for (int i = 0; i < plugin.getConfigManager().getDecimal(); i++) {
			if (i == 0)
				pattern = pattern + ".";
			pattern = pattern + "#";
		}
		DecimalFormat df = new DecimalFormat(pattern);
		
		
		
		String title;
		if (lostgain < 0.0)
			title = plugin.getConfigManager().getFormatHpLost().replaceAll("%change%", df.format(lostgain * plugin.getConfigManager().getScale()));
		else if (lostgain > 0.0)
			title = plugin.getConfigManager().getFormatHpGain().replaceAll("%change%", "+" + df.format(lostgain * plugin.getConfigManager().getScale()));
		else
			title = plugin.getConfigManager().getFormatNormal();
		
		title = title
				.replaceAll("%name%", player.getName())
				.replaceAll("%displayname%", player.getDisplayName())
				.replaceAll("%hp%", df.format(hp))
				.replaceAll("%max%", df.format(max))
				.replaceAll("%hp_int%", (int) Math.ceil(hp) + "")
				.replaceAll("%max_int%", (int) Math.ceil(max) + "");
		title = ChatColor.translateAlternateColorCodes('&', title);
		if (usePapi)
			title = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, title);
		
		
		
		if (hp / max > 1.0)
			self.setProgress(1.0);
		else if (hp / max < 0.0)
			self.setProgress(0.0);
		else
			self.setProgress(hp / max);
		
		self.setTitle(title);
	}
	
	public void updateEnemy(Player player, LivingEntity target, double lostgain) {
		if (plugin.getConfigManager().getOverride())
			self.removePlayer(player);
		
		double hp = player.getHealth() * plugin.getConfigManager().getScale();
		double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * plugin.getConfigManager().getScale();
		
		double e_hp = target.getHealth() * plugin.getConfigManager().getEnemyScale();
		double e_max = target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * plugin.getConfigManager().getEnemyScale();
		
		
		
		String pattern = "#";
		for (int i = 0; i < plugin.getConfigManager().getDecimal(); i++) {
			if (i == 0)
				pattern = pattern + ".";
			pattern = pattern + "#";
		}
		DecimalFormat df = new DecimalFormat(pattern);
		
		
		
		String title;
		if (lostgain <= 0.0)
			title = plugin.getConfigManager().getEnemyFormatHpLost().replaceAll("%e_change%", df.format(lostgain * plugin.getConfigManager().getScale()));
		else
			title = plugin.getConfigManager().getEnemyFormatHpGain().replaceAll("%e_change%", "+" + df.format(lostgain * plugin.getConfigManager().getScale()));
		
		title = title
				.replaceAll("%name%", player.getName())
				.replaceAll("%displayname%", player.getDisplayName())
				.replaceAll("%hp%", df.format(hp))
				.replaceAll("%max%", df.format(max))
				.replaceAll("%hp_int%", (int) Math.ceil(hp) + "")
				.replaceAll("%max_int%", (int) Math.ceil(max) + "")
				.replaceAll("%e_hp%", df.format(e_hp))
				.replaceAll("%e_max%", df.format(e_max))
				.replaceAll("%e_hp_int%", (int) Math.ceil(e_hp) + "")
				.replaceAll("%e_max_int%", (int) Math.ceil(e_max) + "")
				.replaceAll("%e_type%", target.getType() + "");
		
		if (target instanceof Player) {
			Player enemy = (Player) target;
			title = title.replaceAll("%e_name%", enemy.getName()).replaceAll("%e_displayname%", enemy.getDisplayName());
		}
		else {
			title = title.replaceAll("%e_name%", target.getName());
			
			if (target.getCustomName() != null)
				title = title.replaceAll("%e_displayname%", target.getCustomName());
			else
				title = title.replaceAll("%e_displayname%", "");
		}
		title = ChatColor.translateAlternateColorCodes('&', title);
		if (usePapi)
			title = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, title);
		
		
		
		if (e_hp / e_max > 1.0)
			enemy.setProgress(1.0);
		else if (e_hp / e_max < 0.0)
			enemy.setProgress(0.0);
		else
			enemy.setProgress(e_hp / e_max);
		
		enemy.setTitle(title);
		
		this.target = target;
	}
	
	public boolean attemptRemove() {
		long elapsedTime = System.currentTimeMillis() - e_lastUpdate;
		long confVal = plugin.getConfigManager().getEnemyDuration() / 20 * 1000L;
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
	
	public void create(Player player) {
		double hp = player.getHealth() * plugin.getConfigManager().getScale();
		double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * plugin.getConfigManager().getScale();
		
		String pattern = "#";
		for (int i = 0; i < plugin.getConfigManager().getDecimal(); i++) {
			if (i == 0)
				pattern = pattern + ".";
			pattern = pattern + "#";
		}
		DecimalFormat df = new DecimalFormat(pattern);
		
		
		
		String title = plugin.getConfigManager().getFormatNormal()
				.replaceAll("%name%", player.getName())
				.replaceAll("%displayname%", player.getDisplayName())
				.replaceAll("%hp%", df.format(hp))
				.replaceAll("%max%", df.format(max))
				.replaceAll("%hp_int%", (int)Math.ceil(hp) + "")
				.replaceAll("%max_int%", (int)Math.ceil(max) + "");
		title = ChatColor.translateAlternateColorCodes('&', title);
		if (usePapi)
			title = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, title);
		
		
		
		BossBar bossBar = Bukkit.createBossBar(title, plugin.getConfigManager().getColor(),
				plugin.getConfigManager().getStyle(), new BarFlag[0]);
		
		if (hp / max > 1.0)
			bossBar.setProgress(1.0);
		else if (hp / max < 0.0)
			bossBar.setProgress(0.0);
		else
			bossBar.setProgress(hp / max);
		
		if (plugin.getConfigManager().isSelfEnabled())
			if (!hide.contains(player.getUniqueId()))
				bossBar.addPlayer(player);
		
		self = bossBar;
		lastUpdate = System.currentTimeMillis();
		
		bars.put(player, this);
	}
	
	public void createEnemy(Player player, LivingEntity target, double lostgain) {
		if (plugin.getConfigManager().getOverride())
			self.removePlayer(player);
		
		double hp = player.getHealth() * plugin.getConfigManager().getScale();
		double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * plugin.getConfigManager().getScale();
		
		double e_hp = target.getHealth() * plugin.getConfigManager().getEnemyScale();
		double e_max = target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * plugin.getConfigManager().getEnemyScale();
		double e_lostgain = lostgain * plugin.getConfigManager().getScale();
		
		String pattern = "#";
		for (int i = 0; i < plugin.getConfigManager().getDecimal(); i++) {
			if (i == 0)
				pattern = pattern + ".";
			pattern = pattern + "#";
		}
		DecimalFormat df = new DecimalFormat(pattern);
		
		
		
		String title;
		if (lostgain < 0.0)
			title = plugin.getConfigManager().getEnemyFormatHpLost().replaceAll("%change%", df.format(e_lostgain));
		else if (lostgain > 0.0)
			title = plugin.getConfigManager().getEnemyFormatHpGain().replaceAll("%change%", "+" + df.format(e_lostgain));
		else
			title = plugin.getConfigManager().getEnemyFormatHpGain().replaceAll("%change%", "-" + df.format(e_lostgain));
		
		title = title
				.replaceAll("%name%", player.getName())
				.replaceAll("%displayname%", player.getDisplayName())
				.replaceAll("%hp%", df.format(hp))
				.replaceAll("%max%", df.format(max))
				.replaceAll("%hp_int%", (int) Math.ceil(hp) + "")
				.replaceAll("%max_int%", (int) Math.ceil(max) + "")
				.replaceAll("%e_hp%", df.format(e_hp))
				.replaceAll("%e_max%", df.format(e_max))
				.replaceAll("%e_hp_int%", (int) Math.ceil(e_hp) + "")
				.replaceAll("%e_max_int%", (int) Math.ceil(e_max) + "")
				.replaceAll("%e_type%", target.getType() + "");
		
		if (target instanceof Player) {
			Player enemy = (Player) target;
			title = title.replaceAll("%e_name%", enemy.getName()).replaceAll("%e_displayname%", enemy.getDisplayName());
		}
		else {
			title = title.replaceAll("%e_name%", target.getName());
			if (target.getCustomName() != null)
				title = title.replaceAll("%e_displayname%", target.getCustomName());
			else
				title = title.replaceAll("%e_displayname%", "");
		}
		title = ChatColor.translateAlternateColorCodes('&', title);
		if (usePapi)
			title = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, title);
		
		
		
		BossBar bossBar = Bukkit.createBossBar(title, plugin.getConfigManager().getEnemyColor(),
				plugin.getConfigManager().getEnemyStyle(), new BarFlag[0]);
		
		if (e_hp / e_max > 1.0)
			bossBar.setProgress(1.0);
		else if (e_hp / e_max < 0.0)
			bossBar.setProgress(0.0);
		else
			bossBar.setProgress(e_hp / e_max);
		
		if (plugin.getConfigManager().isEnemyEnabled())
			if (!hide.contains(player.getUniqueId()))
				bossBar.addPlayer(player);
		
		this.enemy = bossBar;
		this.target = target;
		this.e_lastUpdate = System.currentTimeMillis();
		
		bars.put(player, this);
	}
	
	public BossBar getSelfBar() {
		return this.self;
	}
	
	public BossBar getEnemyBar() {
		return this.enemy;
	}
	
	public LivingEntity getTarget() {
		return this.target;
	}
	
	public long getLastUpdate() {
		return this.lastUpdate;
	}
	
	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public long getEnemyLastUpdate() {
		return this.e_lastUpdate;
	}
	
	public void setEnemyLastUpdate(long lastUpdate) {
		this.e_lastUpdate = lastUpdate;
	}
}
