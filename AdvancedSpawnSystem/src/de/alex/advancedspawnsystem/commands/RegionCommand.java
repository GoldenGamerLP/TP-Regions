package de.alex.advancedspawnsystem.commands;

import java.io.File;
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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.alex.advancedspawnsystem.ConfigManager.ConfigManager;
import de.alex.advancedspawnsystem.main.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.raidstone.wgevents.WorldGuardEvents;

public class RegionCommand implements CommandExecutor, Listener{
	//
	ConfigManager handler = new ConfigManager();
	ArrayList<String> worlds = new ArrayList<String>();
	Map<UUID, Long> timeMap = new HashMap<>();
	Integer counter = 0;
	//Configuration Files
	public File locations = new File("plugins//" + Main.getInstance().getDescription().getName() + "//location.yml");
	public File config = new File("plugins//" + Main.getInstance().getDescription().getName() + "//config.yml");
	YamlConfiguration cfg = YamlConfiguration.loadConfiguration(locations);
	YamlConfiguration cf = YamlConfiguration.loadConfiguration(config);
	//General.Messages section
	String prefix = ChatColor.translateAlternateColorCodes('&', cf.getString("General.Messages.Prefix"));
	String succes = ChatColor.translateAlternateColorCodes('&', cf.getString("General.Messages.Succes"));
	String fail = ChatColor.translateAlternateColorCodes('&', cf.getString("General.Messages.Fail"));
	String noregion = ChatColor.translateAlternateColorCodes('&', cf.getString("General.Messages.NoRegion"));
	String Worlds = ChatColor.translateAlternateColorCodes('&', cf.getString("General.Messages.Worlds"));
	String PluralWorld = ChatColor.translateAlternateColorCodes('&',cf.getString("General.Messages.WorldsPlural"));
	String NoneWorlds = ChatColor.translateAlternateColorCodes('&', cf.getString("General.Messages.NoneWorlds"));
	String Reload = ChatColor.translateAlternateColorCodes('&', cf.getString("General.Messages.Reload"));
	String Use = ChatColor.translateAlternateColorCodes('&', cf.getString("General.Messages.Use"));
	String Wait = ChatColor.translateAlternateColorCodes('&', cf.getString("General.Messages.Wait"));
	String CooldownMinSec = ChatColor.translateAlternateColorCodes('&', cf.getString("General.Messages.CooldownMinSec"));
	String CooldownSec = ChatColor.translateAlternateColorCodes('&', cf.getString("General.Messages.CooldownSec"));
	//General.Options section
	Boolean CooldownBoolean = cf.getBoolean("General.Options.CooldownBoolean");
	Boolean Animation = cf.getBoolean("General.Options.Animation");
	Boolean useEffects = cf.getBoolean("General.Options.UseEffects");
	Integer CooldownInteger = cf.getInt("General.Options.Cooldown");
	
	public void onLeave(PlayerQuitEvent e) {
		timeMap.remove(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void anvilEvent(PrepareAnvilEvent e) {
	    for(HumanEntity p:e.getViewers()) {
	    	p.sendMessage(e.getResult().toString());
	    }
	}


	@Override
	@EventHandler (priority = EventPriority.MONITOR)
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
						teleportPlayerRegionSpawn(p, false);
					} else
						if(args[0].equalsIgnoreCase("set")) {
							setregionSpawnByPlayer(p);
						} else				
							if(args[0].equalsIgnoreCase("reload")) {
								reloadFiles(p);
							} else
								if(args[0].equalsIgnoreCase("list")) {
									listSpawns(p);
								} else
									if(args[0].equalsIgnoreCase("search")) {
										if(args.length == 2) {
											SearchByString(p, args[1].toString());	
									   }  else
										   p.sendMessage(prefix + Use);
										} else
											if(args[0].equalsIgnoreCase("help")) {
												p.sendMessage(prefix + Use);
											} else
												p.sendMessage(prefix + Use);
									
										
				
			}
		return true;
   }


	private void SearchByString(Player p, String substring) {
		if(p.hasPermission("TPRegions.search")) {
				for (World w: Bukkit.getServer().getWorlds()) {
					 if(cfg.get("Location." + w.getName() + ".[" + substring + "]") != null) {
				        	counter++;
				        	Location lc = (Location) cfg.get("Location." + w.getName() + "." + substring);
				        	net.md_5.bungee.api.chat.TextComponent msg = new net.md_5.bungee.api.chat.TextComponent("§3Click here to Teleport§7: " + substring + " §8(§f" + counter + "§8)");
				        	msg.setClickEvent( new ClickEvent( ClickEvent.Action.SUGGEST_COMMAND, "/tp @p " + lc.getBlockX() + " " +  lc.getBlockY() + " " + lc.getBlockZ() + " " + lc.getWorld().getName()) );
				        	msg.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aThe Location could be found for§7: \n Region: " + substring + "§8(§f" + counter + "§8) \n §7 Coordinates: X:" + lc.getBlockX() + " Y:" + lc.getBlockY() + " Z:" + lc.getBlockZ() + " World:" + lc.getWorld().getName()).create()));
				        	p.spigot().sendMessage(msg);
				} else
					p.sendMessage("§cThat location could not be found for the world: §7" + w.getName());
			       
			    } 
				
		}

		counter = 0;
	}


	private void listSpawns(Player p) {
		if(p.hasPermission("TPRegions.list")) {
			for (World world:Bukkit.getServer().getWorlds()) {
				if((cfg.getString("Location." + world.getName().toString())) != null) {
					worlds.add(world.getName());
					
				}
			}
			if(worlds.isEmpty()) {
				p.sendMessage(prefix + NoneWorlds);
			} else
				if(worlds.size() == 1) {
					p.sendMessage(prefix + Worlds.replace("{world}", worlds.toString().replace("[" + "]", "")));
					worlds.clear();
				} else
					if(worlds.size() <= 2) {
						p.sendMessage(prefix + PluralWorld.replace("{world}", worlds.toString()).replace("[" + "]", ""));
						worlds.clear();
			}
		}
		
	}


	private void reloadFiles(Player p) {
		if(p.hasPermission("TPRegions.reload")) {
			Long start = System.currentTimeMillis();
			timeMap.clear();
			cfg = YamlConfiguration.loadConfiguration(locations);
			cf = YamlConfiguration.loadConfiguration(config);
			p.sendMessage(prefix + Reload.replace("{Time}", Long.toString(System.currentTimeMillis() - start)));
		}
		
	}


	private void setregionSpawnByPlayer(Player p) {
		if(p.hasPermission("TPRegions.set")) {
			if(!(WorldGuardEvents.getRegionsNames(p.getUniqueId()).isEmpty())) {
				p.sendMessage("Du hast den Spawn für die Region erfolgreich gesetzt! Du kannst alles weitere in der Config einstellen!");
				cfg.set("Location." + p.getWorld().getName() + "." + WorldGuardEvents.getRegionsNames(p.getUniqueId()).toString().replace("[" + "]", ""), p.getLocation());
				try {
					cfg.save(locations);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else
				p.sendMessage(prefix + noregion);
		}
		
	}


	private void teleportPlayerRegionSpawn(Player p, boolean Effects) {
		Long time = timeMap.get(p.getUniqueId());
		if(!(WorldGuardEvents.getRegionsNames(p.getUniqueId()).isEmpty())) {
			if(cfg.get("Location." + p.getWorld().getName() + "." + WorldGuardEvents.getRegionsNames(p.getUniqueId()).toString().replace("[" + "]", "")) != null) {
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
					if(useEffects) {
						World wp = p.getWorld();
						Location locp = p.getLocation();
						wp.spawnParticle(Particle.CLOUD, locp, 15, 0.0, 0.0, 0.0);
					}
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1f, 1f);
					p.setNoDamageTicks(80);
					p.sendMessage(prefix + succes);
					p.teleport((Location) cfg.get("Location." + p.getWorld().getName() + "." + WorldGuardEvents.getRegionsNames(p.getUniqueId()).toString().replace("[" + "]", "")));
					
					if(useEffects) {
						World w = ((Location) cfg.get("Location." + p.getWorld().getName() + "." + WorldGuardEvents.getRegionsNames(p.getUniqueId()).toString().replace("[" + "]", ""))).getWorld();
						Location loc = (Location) cfg.get("Location." + p.getWorld().getName() + "." + WorldGuardEvents.getRegionsNames(p.getUniqueId()).toString().replace("[" + "]", ""));
						w.spawnParticle(Particle.END_ROD, loc, 190, 0.002, 0.005, 0.002);
						
					}
					
					
				}
				time--;
			}
	    	
	    }, 0L, 20L);
    } else {
    	p.teleport((Location) cfg.get("Location." + p.getWorld().getName() + "." + WorldGuardEvents.getRegionsNames(p.getUniqueId()).toString().replace("[" + "]", "")));
		p.sendMessage(prefix + succes);
    }	
		
	}
}
