package to.epac.factorycraft.bossbarhealth.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.epac.factorycraft.bossbarhealth.BossBarHealth;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;

public class Commands implements CommandExecutor {
	
	private BossBarHealth plugin = BossBarHealth.inst();
	
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	
        if (args.length == 0) {
            helpPage(sender);
            return false;
        }
        
    	if (args[0].equalsIgnoreCase("reload")) {
    		if (!sender.hasPermission("BossBarHealth.Admin")) {
    			sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
                return false;
            }
    		
        	plugin.getConfigManager().load();
        	
        	HealthBar.removeAll();
        	
        	if (plugin.getConfigManager().isSelfEnabled() || plugin.getConfigManager().isEnemyEnabled())
                   HealthBar.updateAll();
        	
        	sender.sendMessage(ChatColor.GREEN + "Configuration reloaded.");
        	return true;
        }
    	
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute command.");
            return false;
        }
        
        
        
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        
        if (!player.hasPermission("BossBarHealth.Admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
            return false;
        }
        
        else if (args[0].equalsIgnoreCase("help")) {
            helpPage(sender);
        }
        
        else if (args[0].equalsIgnoreCase("show")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3[&dBoss&5Bar&cHealth&3]&r &aShowing health bar."));
            
            if (HealthBar.hide.contains(uuid))
                HealthBar.hide.remove(uuid);
            
            HealthBar bar = HealthBar.bars.get(player);
            if (bar != null) {
            	if (plugin.getConfigManager().isSelfEnabled())
            		if (!bar.getSelfBar().getPlayers().contains(player))
            			bar.getSelfBar().addPlayer(player);
            }
            else {
            	bar = new HealthBar();
            	bar.update(player, null, 0, true);
            }
            
        }
        
        else if (args[0].equalsIgnoreCase("hide")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3[&dBoss&5Bar&cHealth&3]&r &cHiding health bar."));
            
            if (!HealthBar.hide.contains(uuid))
                HealthBar.hide.add(uuid);
            
            HealthBar bar = HealthBar.bars.get(player);
            if (bar != null) {
                bar.remove();
                bar.removeEnemy();
            }
        }
        return true;
    }
    
    public void helpPage(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9&m----------&b[&dBoss&5Bar&cHealth&b]&9&m----------"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dMain command: /bs"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c<>: Required &d[]: Optional"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/bbh Show&b: &3Show BossBarHealth to player."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/bbh Hide&b: &3Hide BossBarHealth from player."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/bbh Help&b: &3Show the help page."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9&m----------&b[&dBoss&5Bar&cHealth&b]&9&m----------"));
    }
}
