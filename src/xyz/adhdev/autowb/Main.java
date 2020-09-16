package xyz.adhdev.autowb;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.wimbli.WorldBorder.Config;

public class Main extends JavaPlugin implements Listener {
	
	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		createConfig();
		Logs("Started");
	}
	
	@Override
	public void onDisable() {
		Logs("Stopped");
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		int pcount = Bukkit.getServer().getOnlinePlayers().size();
		if(pcount == config.getInt("Minimum Player")) {
			wbstop();
		}else if(pcount <= config.getInt("Minimum Player")-1) {
			wbstart();
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				int pcount = Bukkit.getServer().getOnlinePlayers().size();
				if(pcount == config.getInt("Minimum Player")) {
					wbstop();
				}else if(pcount <= config.getInt("Minimum Player")-1) {
					wbstart();
				}
			}
        }.runTaskLater(this, 20);
	}
	
	public void Logs(String message) {
		console.sendMessage(ChatColor.GREEN+"[AutoWB] "+ChatColor.WHITE+" "+message);
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("awb")) {
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("player")){
					int playernum = Integer.parseInt(args[1]);
					if(playernum == (int)playernum) {
						config.set("Minimum Player", playernum);
						sender.sendMessage(ChatColor.GREEN+"[AutoWB] "+ChatColor.WHITE+"Minimum Player Changed To "+playernum);
						saveConfig();
						this.getConfig();
					}else {
						sender.sendMessage(ChatColor.RED+"ERROR "+ChatColor.WHITE+": Invalid number");
					}
					
				}
			}
		}
		return false;
	}
	
	public void wbstart() {
		if (!makeSureFillIsRunning())
            return;
		if(Config.fillTask.isPaused()) {
			Config.fillTask.pause();
			Logs("Starting World Border");
		}else {
			Logs("World Border Is Already Running");
		}
	}
	
	public void wbstop() {
		if (!makeSureFillIsRunning())
            return;
		if(!Config.fillTask.isPaused()) {
			Config.fillTask.pause();
			Logs("Stopping World Border");
		}else {
			Logs("World Border Is Already Stopped");
		}
	}
	
	public boolean makeSureFillIsRunning() {
        if (Config.fillTask != null && Config.fillTask.valid())
            return true;
        Logs("The world map generation task is not currently running");
        Logs("Please Do /wb [world] fill");
        return false;
    }
	
	public void createConfig() {
        try {
            if(!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
            	Logs("Config.yml Not Found, Creating");
            	config.options().header("Minimum Player Before WorldBorder Get Stopped");
            	config.addDefault("Minimum Player", 1);
                config.options().copyDefaults(true);
                saveConfig();
                this.getConfig();
            } else {
            	if(config.getInt("Minimum Player") <= 0) {
            		Logs("Minimum Player Detected null Fixing..");
            		config.set("Minimum Player", 1);
            		Logs("Minimum Player Fixed");
                    saveConfig();
                    this.getConfig();
            	}else {
            		Logs("Config.yml Found, Loading");
                    this.getConfig();
            	}
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public ConsoleCommandSender console = Bukkit.getConsoleSender();
	
	FileConfiguration config = getConfig();
	
	Config cWorldBorder = new Config();
}
