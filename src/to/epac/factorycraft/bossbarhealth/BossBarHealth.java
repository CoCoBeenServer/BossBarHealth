package to.epac.factorycraft.bossbarhealth;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import to.epac.factorycraft.bossbarhealth.commands.Commands;
import to.epac.factorycraft.bossbarhealth.config.ConfigManager;
import to.epac.factorycraft.bossbarhealth.handlers.DamageHandler;
import to.epac.factorycraft.bossbarhealth.handlers.JoinHandler;
import to.epac.factorycraft.bossbarhealth.handlers.PlayerMoveHandler;
import to.epac.factorycraft.bossbarhealth.handlers.QuitHandler;
import to.epac.factorycraft.bossbarhealth.handlers.RegainHealthHandler;
import to.epac.factorycraft.bossbarhealth.handlers.RespawnHandler;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;
import to.epac.factorycraft.bossbarhealth.metrics.Metrics;

public class BossBarHealth extends JavaPlugin {
	
    private static BossBarHealth inst;
    
    public ConfigManager configManager;
    
    public static boolean usePapi = false;

    public void onEnable() {
        inst = this;

        configManager = new ConfigManager(this);
        configManager.load();

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