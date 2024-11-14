package com.pilzbros.mazehunt.command;

import com.pilzbros.mazehunt.MazeHunt;
import com.pilzbros.mazehunt.game.Arena;
import com.pilzbros.mazehunt.io.Setting;
import com.pilzbros.mazehunt.io.Settings;
import java.util.Iterator;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("mazehunt.admin")) {
            if (args.length < 1) {
                sender.sendMessage(MazeHunt.pluginAdminPrefix + "MazeHunt v" + "1.5");
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("setup")) {
                    sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "Proper Syntax: setup [name]");
                } else if (args[0].equalsIgnoreCase("arena")) {
                    sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "Proper Syntax: arena [name]");
                } else if (args[0].equalsIgnoreCase("arenas")) {
                    if (MazeHunt.gameController.hasArenas()) {
                        sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "-- MazeHunt Arenas --");
                        int count = 1;

                        for(Iterator var7 = MazeHunt.gameController.getArenas().entrySet().iterator(); var7.hasNext(); ++count) {
                            Entry<String, Arena> a = (Entry)var7.next();
                            sender.sendMessage(count + ". " + ((Arena)a.getValue()).getName());
                        }
                    } else {
                        sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "There are no MazeHunt arenas!");
                    }
                } else if (args[0].equalsIgnoreCase("reset")) {
                    sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "Command Deprecated");
                } else if (args[0].equalsIgnoreCase("update")) {
                    if (Settings.getGlobalBoolean(Setting.CheckForUpdates)) {
                        if (MazeHunt.updateNeeded) {
                            sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.YELLOW + "There is a MazeHunt update available!!");
                        } else {
                            sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.GREEN + "MazeHunt is up to date :)");
                        }
                    } else {
                        sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "You disabled update checking in MazeHunt's global.yml file! :(");
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    MazeHunt.IO.LoadSettings();
                    MazeHunt.IO.prepareDB();
                    MazeHunt.gameController.serverReload();
                    MazeHunt.IO.loadArena();
                    MazeHunt.IO.loadSigns();
                    MazeHunt.IO.loadLocations();
                    sender.sendMessage(MazeHunt.pluginAdminPrefix + "MazeHunt reloaded!");
                } else {
                    sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "Unknown Command!");
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("setup")) {
                    if (!MazeHunt.gameController.arenaExist(args[1])) {
                        ArenaCreation.selectstart((Player)sender, args[1]);
                    } else {
                        sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "Arena " + args[1] + " already exists! Please choose another name or delete " + args[1]);
                    }
                } else if (args[0].equalsIgnoreCase("arena")) {
                    if (MazeHunt.gameController.arenaExist(args[1])) {
                        Arena arena = MazeHunt.gameController.getArena(args[1]);
                        sender.sendMessage(ChatColor.RED + "----- " + ChatColor.GREEN + args[1] + ChatColor.RED + "-----");
                        sender.sendMessage("Locations: " + arena.locationManager.numLocations());
                    } else {
                        sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "Arena " + args[1] + " does not exist!");
                    }
                } else if (args[0].equalsIgnoreCase("end")) {
                    if (!MazeHunt.gameController.getArena(args[1]).gameManager.inProgress && !MazeHunt.gameController.getArena(args[1]).gameManager.inWaiting) {
                        sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "There's no current game in arena " + args[1]);
                    } else {
                        MazeHunt.gameController.getArena(args[1]).gameManager.forceEnd();
                        MazeHunt.gameController.getArena(args[1]).gameManager.prepare();
                        sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.GREEN + "The game in arena " + args[1] + " has been forcefully ended!");
                    }
                } else if (args[0].equalsIgnoreCase("kick")) {
                    if (MazeHunt.gameController.playerPlaying(args[1])) {
                        MazeHunt.gameController.kickPlayer(args[1]);
                        sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.GREEN + args[1] + " has been kicked from MazeHunt!");
                    } else {
                        sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "Player " + args[1] + " is not playing MazeHunt!");
                    }
                } else if (args[0].equalsIgnoreCase("delete")) {
                    sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "Command coming soon!");
                } else {
                    sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "Unknown Command!");
                }
            } else {
                sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "Unknown Command!");
            }
        } else {
            sender.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "Insufficent MazeHunt permissions!");
        }

        return true;
    }
}
