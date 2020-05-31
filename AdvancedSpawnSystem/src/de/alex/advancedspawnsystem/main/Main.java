package de.alex.advancedspawnsystem.main;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import de.alex.advancedspawnsystem.ConfigManager.ConfigManager;
import de.alex.advancedspawnsystem.commands.RegionCommand;

public class Main extends JavaPlugin{
	ConfigManager handler = new ConfigManager();
	
	public void onEnable() {
		
		instance = this;
		createFiles();
		load();
        check();	
        
        
       
	}
	
	
    private void registerCommands() {
    	 getCommand("tpregion").setExecutor(new RegionCommand());
		
	}


	private void createFiles() {
    	handler.createFile("location");
    	handler.createFile("config");
		
	}


	private void check() {
		if(handler.existsFile("config") && handler.existsFile("location")) {
			Bukkit.getConsoleSender().sendMessage("§8[§3TPRegion§8] §aPlugin was seccusfully enabled! §aV: " + getDescription().getVersion() + " §aBy: " + getDescription().getAuthors());
			//
			//Only register the cmd when the .yml files where succesfully generated.
			registerCommands();		
			//
			
		} else {
			Bukkit.getConsoleSender().sendMessage("§8[§3TPRegion§8] §cTHe Plugin could not be enabled! §6Something went wrong, please contact me! GoldenGamer LP on SpigotMC! §4Disabling the Plugin!");
			this.setEnabled(false);
		}
		
	}
	private void load() {
		File config = new File("plugins//" + Main.getInstance().getDescription().getName() + "//config.yml");
		YamlConfiguration cf = YamlConfiguration.loadConfiguration(config);
    	cf.options().copyDefaults(true);
    	//Genral.Messages section
    	cf.addDefault("General.Messages.Prefix","&8[&3TPRegion&8] &7");
    	cf.addDefault("General.Messages.Succes","&aDu wurdest erfolgreich zum Spawn des Ortes teleportiert.");
    	cf.addDefault("General.Messages.Fail","&cDu konntest nicht zu einem Spawn des Ortes teleportiert werden! Bitte benarchitige ein Teammitglied!");
    	cf.addDefault("General.Messages.NoRegion","&cDu bist in keiner Region!");
    	cf.addDefault("General.Messages.Worlds","&7Es wurde für die Welt &3{world} &7eine Konnfiguration gefunden!");
    	cf.addDefault("General.Messages.WorldsPlural","&7Es wurden für die Welten &3{world} &7eine Konfiguration gefunden!");
    	cf.addDefault("General.Messages.NoneWorlds","&7Es wurden keine Konfigurierten Welten gefunden!");
    	cf.addDefault("General.Messages.Reload","&7Die Config & Locations wurden &aerfolgreich &7neugeladen in &3{Time} &7Ms.");
    	cf.addDefault("General.Messages.Use","Help: /tpregions <set/spawn/reload/list/search/help>");
    	cf.addDefault("General.Messages.Wait","&7Du wirst in {Time} Sekunden teleportiert!");
    	cf.addDefault("General.Messages.CooldownMinSec","&7Du musst noch {Min} Minuten und {Sec} Sekunden warten bevor du nochmal zum Spawn kommst!");
    	cf.addDefault("General.Messages.CooldownSec","&7Du musst noch {Sec} Sekunden warten bevor du nochmal zum Spawn kommst!");
    	//General.Options section
    	cf.addDefault("General.Options.CooldownBoolean", true);
    	cf.addDefault("General.Options.Cooldown", 600);
    	cf.addDefault("General.Options.Animation", true);
    	cf.addDefault("General.Options.UseEffects", true);
    	//Try to save, because sometimes the config does not regenerate on the first time
    	try {
			cf.save(config);
		} catch (IOException e) {
			Bukkit.getConsoleSender().sendMessage("§8[§3TPRegion§8] §cCould find the config.yml file! Maybe an system error? Contact GoldenGamer on Spigot");
			e.printStackTrace();
		}
    	
	}
    public static Main instance;
    public static Main getInstance(){
    return instance;
    }
    
}


