package to.epac.factorycraft.bossbarhealth;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import to.epac.factorycraft.bossbarhealth.commands.Commands;
import to.epac.factorycraft.bossbarhealth.config.ConfigManager;
import to.epac.factorycraft.bossbarhealth.config.LangManager;
import to.epac.factorycraft.bossbarhealth.handlers.*;
import to.epac.factorycraft.bossbarhealth.hooks.WorldGuard.WgRegionHandler;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;
import to.epac.factorycraft.bossbarhealth.metrics.Metrics;

public class BossBarHealth extends JavaPlugin {
	
    private static BossBarHealth inst;
    
    public ConfigManager configManager;
    public LangManager langManager;
    
    public static boolean usePapi = false;
    public static boolean useWorldGuard = false;

    public void onEnable() {
        inst = this;

        configManager = new ConfigManager(this);
        langManager = new LangManager(this);
        configManager.load();
        langManager.load();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new DamageHandler(), this);
        pm.registerEvents(new JoinHandler(), this);
        pm.registerEvents(new QuitHandler(), this);
        pm.registerEvents(new RegainHealthHandler(), this);
        pm.registerEvents(new RespawnHandler(), this);
        
        getCommand("BossBarHealth").setExecutor(new Commands());
        
        if (configManager.isSelfEnabled() || configManager.isEnemyEnabled())
            HealthBar.updateAll();
        
        PlayerMoveHandler.start();
        
        
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
        	getLogger().info("PlaceholderAPI was found. You may use its placeholders in config.");
        	usePapi = true;
        }
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null
        		&& Bukkit.getPluginManager().getPlugin("WorldGuardEvents") != null) {
        	getLogger().info("WorldGuard and WorldGuardEvents were found. WorldGuard hook settings will now work.");
        	useWorldGuard = true;
        	pm.registerEvents(new WgRegionHandler(), this);
        }
        
        

        int pluginId = 6432;
        Metrics metrics = new Metrics(this, pluginId);
    }

    public void onDisable() {
        
        HealthBar.removeAll();

        inst = null;
    }

    public static BossBarHealth inst() {
        return inst;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}