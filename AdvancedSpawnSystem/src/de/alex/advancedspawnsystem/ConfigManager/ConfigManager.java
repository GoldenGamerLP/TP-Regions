package de.alex.advancedspawnsystem.ConfigManager;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import de.alex.advancedspawnsystem.main.Main;

public class ConfigManager {
	public static ConfigManager handler;
	
	public ConfigManager() {
		handler = this;
	}
	
	public void createFile(String name) {
		
		File file = new File("plugins//" + Main.getInstance().getDescription().getName() + "//"+ name.toLowerCase() + ".yml");
		
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean existsFile(String name) {
		
		File file = new File("plugins//" + Main.getInstance().getDescription().getName() + "//"+ name.toLowerCase() + ".yml");
		
		if(file.exists()) {			
			return true;
		} else {		
			return false;			
		}
	}
	
	public void deleteFile(String name) {
		
		File file = new File("plugins//" + Main.getInstance().getDescription().getName() + "//"+ name.toLowerCase() + ".yml");
		
		if(file.exists()) {
			file.delete();
		}
	}
	
	public void setValue(String name, String key, Object value) {

		File file = new File("plugins//" + Main.getInstance().getDescription().getName() + "//"+ name.toLowerCase() + ".yml");
		
		if(file.exists()) {
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			
			cfg.set(key, value);
			
			try {
				cfg.save(file);
			} catch (IOException e) {
				Bukkit.getConsoleSender().sendMessage("§7The file couldn't be §csaved");
			}
		}
	}

	
	public void saveFile(String name, Boolean save) {
		File file = new File("plugins//" + Main.getInstance().getDescription().getName() + "//"+ name.toLowerCase() + ".yml");
		if(file.exists()) {
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			cfg.options().copyDefaults(true);
			try {
				cfg.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
