package de.alex.advancedspawnsystem.tabcompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.alex.advancedspawnsystem.utils.Utils;

public class TabCompleter implements org.bukkit.command.TabCompleter {
	
	Boolean useTabComplete = Utils.cfgFile.getBoolean("General.Options.TabComplete");
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("tpregion") && useTabComplete && (cmd.getName().equalsIgnoreCase(label))) {
			if(sender instanceof Player) {
				if(sender.hasPermission(Utils.getPermission("TabAutocomplete"))) {
					if(args.length == 1) {
						List<String> cmpl = new ArrayList<>();
						cmpl.add("set");
						cmpl.add("spawn");
						cmpl.add("reload");
						cmpl.add("list");
						cmpl.add("search");
						cmpl.add("help");
						Collections.sort(cmpl);
						return cmpl;
					} else
						if(args.length == 2 && args[0].equalsIgnoreCase("search")) {
							final List<String> regions = new ArrayList<>();
							if(Utils.lcFile.getKeys(false).size() != 0) {
								for(String w:Utils.lcFile.getConfigurationSection("Location").getKeys(false)){
									for(String r:Utils.lcFile.getConfigurationSection("Location." + w).getKeys(false)) {
										regions.add(r.replace("[", "").replace("]", ""));
									}
								}
							}
							Collections.sort(regions);
							return regions;
						} else
							return null;
					
				} else
					return null;
				
			}
		}
		return null;
	}

}
