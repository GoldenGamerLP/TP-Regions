package de.alex.advancedspawnsystem.utils;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import de.alex.advancedspawnsystem.main.Main;

public class Utils {
	
	public static File locations = new File("plugins//" + Main.getInstance().getDescription().getName() + "//location.yml");
	public static File config = new File("plugins//" + Main.getInstance().getDescription().getName() + "//config.yml");
	public static YamlConfiguration lcFile = YamlConfiguration.loadConfiguration(locations);
	public static YamlConfiguration cfgFile = YamlConfiguration.loadConfiguration(config);
	
	public static String getPermission(String s) {
		return "TPRegions." + s;	
	}

}
