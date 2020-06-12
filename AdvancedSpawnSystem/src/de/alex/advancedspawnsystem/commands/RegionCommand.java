package de.alex.advancedspawnsystem.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import de.alex.advancedspawnsystem.ConfigManager.ConfigManager;
import de.alex.advancedspawnsystem.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.raidstone.wgevents.WorldGuardEvents;

public class RegionCommand implements CommandExecutor, Listener{
	//
	ConfigManager handler = new ConfigManager();
	ArrayList<String> worlds = new ArrayList<String>();
	Map<UUID, Long> timeMap = new HashMap<>();
	Integer counter = 0;
	Integer ct = 0;
	//Configuration Files
	//General.Messages section
	String prefix = ChatColor.translateAlternateColorCodes('&', Utils.cfgFile.getString("General.Messages.Prefix"));
	String succes = ChatColor.translateAlternateColorCodes('&', Utils.cfgFile.getString("General.Messages.Succes"));
	String fail = ChatColor.translateAlternateColorCodes('&', Utils.cfgFile.getString("General.Messages.Fail"));
	String noregion = ChatColor.translateAlternateColorCodes('&', Utils.cfgFile.getString("General.Messages.NoRegion"));
	String Reload = ChatColor.translateAlternateColorCodes('&', Utils.cfgFile.getString("General.Messages.Reload"));
	String Use = ChatColor.translateAlternateColorCodes('&', Utils.cfgFile.getString("General.Messages.Use"));
	String Wait = ChatColor.translateAlternateColorCodes('&', Utils.cfgFile.getString("General.Messages.Wait"));
	String CooldownMinSec = ChatColor.translateAlternateColorCodes('&', Utils.cfgFile.getString("General.Messages.CooldownMinSec"));
	String CooldownSec = ChatColor.translateAlternateColorCodes('&', Utils.cfgFile.getString("General.Messages.CooldownSec"));
	String FoundRegions = ChatColor.translateAlternateColorCodes('&', Utils.cfgFile.getString("General.Messages.FoundRegions"));
	String NoPermission = ChatColor.translateAlternateColorCodes('&', Utils.cfgFile.getString("General.Messages.NoPermission"));
	//General.Options section
	Boolean CooldownBoolean = Utils.cfgFile.getBoolean("General.Options.CooldownBoolean");
	Boolean UseEffects = Utils.cfgFile.getBoolean("General.Options.UseEffects");
	Boolean Animation = Utils.cfgFile.getBoolean("General.Options.Animation");
	Integer CooldownInteger = Utils.cfgFile.getInt("General.Options.Cooldown");
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if(timeMap.containsKey(e.getPlayer().getUniqueId())) {
			timeMap.remove(e.getPlayer().getUniqueId());
		}
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		

		
		if(sender instanceof Player) {
			Player p = ((Player) sender).getPlayer();
			if (!(cmd.getName().equalsIgnoreCase(label))) {
				teleportPlayerRegionSpawn(p, true);
			} else	
				if(!(args.length != 0)) {
					p.sendMessage(prefix + Use);
				} else
					if(args[0].equalsIgnoreCase("spawn")) {
						if(p.hasPermission(Utils.getPermission(args[0].toLowerCase()))) 
							teleportPlayerRegionSpawn(p, false);
						else
							PlayerNoPerm(p);		
					} else
						if(args[0].equalsIgnoreCase("set")) {
							if(p.hasPermission(Utils.getPermission(args[0].toLowerCase())))
								setregionSpawnByPlayer(p);
							else
								PlayerNoPerm(p);
						} else				
							if(args[0].equalsIgnoreCase("reload")) {
								if(p.hasPermission(Utils.getPermission(args[0].toLowerCase())))
									reloadFiles(p);
								else
									PlayerNoPerm(p);
							} else
								if(args[0].equalsIgnoreCase("list")) {
									if(p.hasPermission(Utils.getPermission(args[0].toLowerCase())))
										listSpawns(p);
									else
										PlayerNoPerm(p);
								} else
									if(args[0].equalsIgnoreCase("search")) {
										if(p.hasPermission(Utils.getPermission(args[0].toLowerCase())))
											if(args.length == 2) {
												SearchByString(p, args[1].toString());	
											}  else
												p.sendMessage(prefix + Use);
										else
											PlayerNoPerm(p);
										} else
											if(args[0].equalsIgnoreCase("help")) {
													if(p.hasPermission(Utils.getPermission(args[0].toLowerCase())))
															p.sendMessage(prefix + Use);
													else
														PlayerNoPerm(p);
											} else
												if(p.hasPermission(Utils.getPermission(args[0].toLowerCase())))
													p.sendMessage(prefix + Use);
												else
													PlayerNoPerm(p);
									
										
				
			}
		return true;
   }


	private void PlayerNoPerm(Player p) {
			p.sendMessage(NoPermission);
		
		
	}


	private void SearchByString(Player p, String substring) {
		p.sendMessage(prefix + FoundRegions);
		for (World w: Bukkit.getServer().getWorlds()) {
			 if(Utils.lcFile.get("Location." + w.getName() + ".[" + substring + "]") != null) {
		        	counter++;
		        	Location lc = (Location) Utils.lcFile.get("Location." + w.getName() + ".[" + substring + "]");
		        	TextComponent msg = new net.md_5.bungee.api.chat.TextComponent("§eClick here to Teleport§7: " + substring + " §8(§f" + counter + "§8)");
		        	msg.setClickEvent( new ClickEvent( ClickEvent.Action.SUGGEST_COMMAND, "/tp @p " + lc.getBlockX() + " " +  lc.getBlockY() + " " + lc.getBlockZ() + " " + lc.getWorld().getName()));
		        	msg.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aThe Location could be found for§7: \n Region: " + substring + "§8(§f" + counter + "§8) \n §7Coordinates: X:" + lc.getBlockX() + " Y:" + lc.getBlockY() + " Z:" + lc.getBlockZ() + " World:" + lc.getWorld().getName()).create()));
		        	p.spigot().sendMessage(msg);
		} 
			//p.sendMessage("§cThe Region: §7" + substring + "§c, could not be found for World: §7" + w.getName());
	       
	    } 		
		if(counter == 0)
			p.sendMessage(prefix + "§cNone Regions found with the name§7: " + substring);

		counter = 0;
	}



	private void listSpawns(Player p) {
			p.sendMessage(prefix + "§7Spawn set regions:");
			if(Utils.lcFile.getKeys(false).size() != 0) {
				for(String w:Utils.lcFile.getConfigurationSection("Location").getKeys(false)){
					for(String r:Utils.lcFile.getConfigurationSection("Location." + w).getKeys(false)) {
						Location lc = (Location) Utils.lcFile.get("Location." + w +  "." + r);
						ct++;
						TextComponent msg = new net.md_5.bungee.api.chat.TextComponent("§8(§f" + ct + "§8) §eClick here to Teleport§7: " + r);
			        	msg.setClickEvent( new ClickEvent( ClickEvent.Action.SUGGEST_COMMAND, "/tp @p " + lc.getBlockX() + " " +  lc.getBlockY() + " " + lc.getBlockZ() + " " + lc.getWorld().getName()));
			        	msg.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aRegion: " + r + "\n §7Coordinates: X:" + lc.getBlockX() + " Y:" + lc.getBlockY() + " Z:" + lc.getBlockZ() + " World:" + lc.getWorld().getName()).create()));
			        	p.spigot().sendMessage(msg);
			        	}
				}	
			} else
				p.sendMessage(prefix + "§cNo Spawn set Regions found!");
		
		ct = 0;

		
	}


	private void reloadFiles(Player p) {
			Long start = System.currentTimeMillis();
			timeMap.clear();
			Utils.lcFile = YamlConfiguration.loadConfiguration(Utils.locations);
			Utils.cfgFile = YamlConfiguration.loadConfiguration(Utils.config);
			p.sendMessage(prefix + Reload.replace("{Time}", Long.toString(System.currentTimeMillis() - start)));
	}


	private void setregionSpawnByPlayer(Player p) {
			if(!(WorldGuardEvents.getRegionsNames(p.getUniqueId()).isEmpty())) {
				//cfg.set("Location." + p.getWorld().getName() + "." + WorldGuardEvents.getRegionsNames(p.getUniqueId()).toString(), p.getLocation());
				if(Utils.lcFile.get("Location." + p.getWorld().getName() + "." + WorldGuardEvents.getRegionsNames(p.getUniqueId()).toString()) != null) {
					p.sendMessage(prefix + "§cThe Spawn is already set for this region, pelase edit it via the Location.yml!");
				} else {
					try {
						Utils.lcFile.set("Location." + p.getWorld().getName() + "." + WorldGuardEvents.getRegionsNames(p.getUniqueId()).toString(), p.getLocation());
						Utils.lcFile.save(Utils.locations);
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						Utils.lcFile.get("Location." + p.getWorld().getName() + "." + WorldGuardEvents.getRegionsNames(p.getUniqueId()).toString());
					} catch (NullPointerException e) {
						p.sendMessage(prefix + "§cThe Spawn location could not be saved!");
					}
					p.sendMessage(prefix + "§aThe Location was succesfully saved to the config!");
				}
				
					
			} else
				p.sendMessage(prefix + noregion);
		
	}


	private void teleportPlayerRegionSpawn(Player p, boolean Effects) {
		Long time = timeMap.get(p.getUniqueId());
		if(!(WorldGuardEvents.getRegionsNames(p.getUniqueId()).isEmpty())) {
			if(Utils.lcFile.get("Location." + p.getWorld().getName() + "." + WorldGuardEvents.getRegionsNames(p.getUniqueId()).toString()) != null) {
				if(CooldownBoolean && Effects) {
					if(time != null) {
						if(System.currentTimeMillis() - time >= (CooldownInteger * 1000)) {
							handlerPlayerTeleport(p, Effects);
							timeMap.replace(p.getUniqueId(), System.currentTimeMillis());
						} else {
							long min = (time + (CooldownInteger * 1000) - System.currentTimeMillis() ) / 1000 / 60;
							long sec = ((time + (CooldownInteger * 1000) - System.currentTimeMillis()) / 1000) % 60;
							if(min >= 1) {
								p.sendMessage(prefix + (CooldownMinSec.replace("{Min}", Long.toString(min)).replace("{Sec}", Long.toString(sec))));
							} else {
								p.sendMessage(prefix + (CooldownSec.replace("{Sec}", Long.toString(sec))));
					}
						
				}
			} else {
				timeMap.put(p.getUniqueId(), System.currentTimeMillis());
				handlerPlayerTeleport(p, Effects);
			}
				
					
				
		}  else
			handlerPlayerTeleport(p, Effects);		
		
	} else
		p.sendMessage(prefix + fail);

	} else
		p.sendMessage(prefix + noregion);
}


	private void handlerPlayerTeleport(Player p, boolean Effects) { 

    if(Animation && Effects) {
	    Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("TPRegions"), new Runnable() {
	    	private int time = 3;

			@Override
			public void run() {
				if(time == 3) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 90));
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 90, 30));
					p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 200));
					p.sendMessage(prefix + Wait.replace("{Time}", Integer.toString(time)));
					p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
				}
				if(time == 2)
					p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f);
				if(time == 1)
					 p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f);
				if(time == 0) {
					if(UseEffects) {
						World wp = p.getWorld();
						Location locp = p.getLocation();
						wp.spawnParticle(Particle.CLOUD, locp, 15, 0.0, 0.0, 0.0);
					}
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1f, 1f);
					p.setNoDamageTicks(80);
					p.sendMessage(prefix + succes);
					p.teleport((Location) Utils.lcFile.get("Location." + p.getWorld().getName() + "." + WorldGuardEvents.getRegionsNames(p.getUniqueId()).toString()));
					
					if(UseEffects) {
						World w = ((Location) Utils.lcFile.get("Location." + p.getWorld().getName() + "." + WorldGuardEvents.getRegionsNames(p.getUniqueId()).toString())).getWorld();
						Location loc = (Location) Utils.lcFile.get("Location." + p.getWorld().getName() + "." + WorldGuardEvents.getRegionsNames(p.getUniqueId()).toString());
						w.spawnParticle(Particle.END_ROD, loc, 190, 0.002, 0.005, 0.002);	
					}	
				}
				time--;
			}
	    	
	    }, 0L, 20L);
    } else {
    	p.teleport((Location) Utils.lcFile.get("Location." + p.getWorld().getName() + "." + WorldGuardEvents.getRegionsNames(p.getUniqueId()).toString()));
		p.sendMessage(prefix + succes);
    	}		
    }
}