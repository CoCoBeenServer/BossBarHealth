package to.epac.factorycraft.bossbarhealth.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import to.epac.factorycraft.bossbarhealth.BossBarHealth;

public class ConfigManager {
	
	private BossBarHealth plugin;
	
	public static int decimal;
	
	public static boolean self;
	public static String color;
	public static String style;
	public static double scale;
	public static String fnormal;
	public static String fhplost;
	public static String fhpgain;
	public static int durnormal;
	public static int durzero;
	public static int refresh;
	
	public static boolean enemy;
	public static String e_color;
	public static String e_style;
	public static double e_scale;
	public static boolean override;
	public static String e_fhplost;
	public static String e_fhpgain;
	public static int e_durnormal;
	public static int e_durzero;
	public static int e_refresh;
	
	public static List<String> blacklist;
	
	public ConfigManager(BossBarHealth plugin) {
		this.plugin = plugin;
	}
	
	
	
	public void load() {
		File confFile = new File(plugin.getDataFolder(), "config.yml");
		if (!confFile.exists())
			plugin.saveResource("config.yml", false);
		
		
		
		FileConfiguration conf = YamlConfiguration.loadConfiguration(confFile);
		decimal = conf.getInt("BossBarHealth.Decimal", 2);
		
		self = conf.getBoolean("BossBarHealth.Self.Enabled", true);
		color = conf.getString("BossBarHealth.Self.Color", "RED");
		style = conf.getString("BossBarHealth.Self.Style", "SEGMENTED_20");
		scale = conf.getDouble("BossBarHealth.Self.Scale", 1.0);
		fnormal = conf.getString("BossBarHealth.Self.Format.Normal", "&b%hp_int%/%max_int%");
		fhplost = conf.getString("BossBarHealth.Self.Format.HpLost", "&b%hp_int%/%max_int% &7(&c%change%&7)");
		fhpgain = conf.getString("BossBarHealth.Self.Format.HpGain", "&b%hp_int%/%max_int% &7(&a%change%&7)");
		durnormal = conf.getInt("BossBarHealth.Self.Format.Duration.Normal", 40);
		durzero = conf.getInt("BossBarHealth.Self.Format.Duration.Zero", 10);
		refresh = conf.getInt("BossBarHealth.Self.Facing.Refresh", 20);
		
		enemy = conf.getBoolean("BossBarHealth.Enemy.Enabled", true);
		e_color = conf.getString("BossBarHealth.Enemy.Color", "GREEN");
		e_style = conf.getString("BossBarHealth.Enemy.Style", "SEGMENTED_20");
		e_scale = conf.getDouble("BossBarHealth.Enemy.Scale", 1.0);
		override = conf.getBoolean("BossBarHealth.Enemy.Override", false);
		e_fhplost = conf.getString("BossBarHealth.Enemy.Format.HpLost", "%e_displayname%: %e_hp_int%/%e_max_int% &7(&c%e_change%&7)");
		e_fhpgain = conf.getString("BossBarHealth.Enemy.Format.HpGain", "%e_displayname%: %e_hp_int%/%e_max_int% &7(&a%e_change%&7)");
		e_durnormal = conf.getInt("BossBarHealth.Enemy.Format.Duration.Normal", 40);
		e_durzero = conf.getInt("BossBarHealth.Enemy.Format.Duration.Zero", 10);
		refresh = conf.getInt("BossBarHealth.Enemy.Facing.Refresh", 20);
		
		blacklist = conf.getStringList("BossBarHealth.Blacklist");
		
	}
	
	public void save() {
		File confFile = new File(this.plugin.getDataFolder(), "config.yml");
		if (!confFile.exists()) {
			confFile.getParentFile().mkdirs();
			
			try {
				confFile.createNewFile();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
		FileConfiguration conf = new YamlConfiguration();
		conf.set("BossBarHealth.Decimal", decimal);
		
		conf.set("BossBarHealth.Self.Enabled", self);
		conf.set("BossBarHealth.Self.Color", color);
		conf.set("BossBarHealth.Self.Style", style);
		conf.set("BossBarHealth.Self.Scale", scale);
		conf.set("BossBarHealth.Self.Format.Normal", fnormal);
		conf.set("BossBarHealth.Self.Format.HpLost", fhplost);
		conf.set("BossBarHealth.Self.Format.HpGain", fhpgain);
		conf.set("BossBarHealth.Self.Format.Duration.Normal", durnormal);
		conf.set("BossBarHealth.Self.Format.Duration.Zero", durzero);
		conf.set("BossBarHealth.Self.Format.Facing.Refresh", refresh);
		
		conf.set("BossBarHealth.Enemy.Enabled", enemy);
		conf.set("BossBarHealth.Enemy.Color", e_color);
		conf.set("BossBarHealth.Enemy.Style", e_style);
		conf.set("BossBarHealth.Enemy.Scale", e_scale);
		conf.set("BossBarHealth.Enemy.Override", override);
		conf.set("BossBarHealth.Enemy.Format.HpLost", e_fhplost);
		conf.set("BossBarHealth.Enemy.Format.HpGain", e_fhpgain);
		conf.set("BossBarHealth.Enemy.Format.Duration.Normal", e_durnormal);
		conf.set("BossBarHealth.Enemy.Format.Duration.Zero", e_durzero);
		conf.set("BossBarHealth.Enemy.Facing.Refresh", e_refresh);
		
		conf.set("BossBarHealth.Blacklist", blacklist);
		
		try {
			conf.save(confFile);
		}
		catch (IOException e) {
			plugin.getLogger().log(Level.WARNING, "Could not save configuration file.");
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	public int getDecimal() {
		return decimal;
	}
	
	public boolean isSelfEnabled() {
		return self;
	}
	
	public BarColor getColor() {
		try {
			return BarColor.valueOf(color);
		}
		catch (NullPointerException e) {
			return BarColor.WHITE;
		}
	}
	
	public BarStyle getStyle() {
		try {
			return BarStyle.valueOf(style);
		}
		catch (NullPointerException e) {
			return BarStyle.SEGMENTED_20;
		}
	}
	
	public double getScale() {
		return scale;
	}
	
	public String getFormatNormal() {
		return fnormal;
	}
	
	public String getFormatHpLost() {
		return fhplost;
	}
	
	public String getFormatHpGain() {
		return fhpgain;
	}
	
	public int getDurationNormal() {
		return durnormal;
	}
	
	public int getDurationZero() {
		return durzero;
	}
	
	public int getFacingRefresh() {
		return refresh;
	}
	
	
	
	
	
	public boolean isEnemyEnabled() {
		return enemy;
	}
	
	public BarColor getEnemyColor() {
		try {
			return BarColor.valueOf(e_color);
		}
		catch (NullPointerException e) {
			return BarColor.WHITE;
		}
	}
	
	public BarStyle getEnemyStyle() {
		try {
			return BarStyle.valueOf(e_style);
		}
		catch (NullPointerException e) {
			return BarStyle.SEGMENTED_20;
		}
	}
	
	public double getEnemyScale() {
		return e_scale;
	}
	
	public boolean getOverride() {
		return override;
	}
	
	public String getEnemyFormatHpLost() {
		return e_fhplost;
	}
	
	public String getEnemyFormatHpGain() {
		return e_fhpgain;
	}
	
	public int getEnemyDurNormal() {
		return e_durnormal;
	}
	
	public int getEnemyDurZero() {
		return e_durzero;
	}
	
	public int getEnemyFacingRefresh() {
		return e_refresh;
	}
	
	
	
	public List<String> getBlacklist() {
		return blacklist;
	}
}
