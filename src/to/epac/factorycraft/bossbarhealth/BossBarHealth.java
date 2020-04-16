package to.epac.factorycraft.bossbarhealth;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import to.epac.factorycraft.bossbarhealth.commands.Commands;
import to.epac.factorycraft.bossbarhealth.config.ConfigManager;
import to.epac.factorycraft.bossbarhealth.events.BossBarHealthHandler;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;
import to.epac.factorycraft.bossbarhealth.metrics.Metrics;

public class BossBarHealth extends JavaPlugin {
	
    private static BossBarHealth inst;
    
    public ConfigManager configManager;

    public void onEnable() {
        inst = this;

        configManager = new ConfigManager(this);
        configManager.load();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BossBarHealthHandler(), this);

        getCommand("BossBarHealth").setExecutor(new Commands());
        
        if (configManager.isSelfEnabled())
            HealthBar.createAll();

        int pluginId = 6432;
        Metrics metrics = new Metrics(this, pluginId);
    }

    public void onDisable() {
        inst = null;
        HealthBar.removeAll();
    }

    public static BossBarHealth inst() {
        return inst;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}