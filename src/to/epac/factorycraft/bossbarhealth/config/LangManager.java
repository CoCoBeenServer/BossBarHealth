package to.epac.factorycraft.bossbarhealth.config;

import java.io.File;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import to.epac.factorycraft.bossbarhealth.BossBarHealth;

public class LangManager {
	
	private BossBarHealth plugin;
	
	public static HashMap<EntityType, String> translates;
	
	
	public LangManager(BossBarHealth plugin) {
		this.plugin = plugin;
		
		translates = new HashMap<>();
	}
	
	public void load() {
		File confFile = new File(plugin.getDataFolder(), "lang.yml");
		if (!confFile.exists())
			plugin.saveResource("lang.yml", false);
		
		
		
		FileConfiguration conf = YamlConfiguration.loadConfiguration(confFile);
		
		for (String type : conf.getConfigurationSection("").getKeys(false)) {
			translates.put(EntityType.valueOf(type), conf.getString(type));
		}
	}
	
	public void save() {
		
	}
	
	public String getText(Entity entity) {
		String text = translates.get(entity.getType());
		return text == null ? entity.getName() : text;
	}
}
