package com.pilzbros.mazehunt.command;

import com.pilzbros.mazehunt.MazeHunt;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UserCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("mazehunt.user")) {
            if (args.length < 1) {
                sender.sendMessage(MazeHunt.pluginPrefix + "MazeHunt v" + "1.5");
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("play")) {
                    sender.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + "Syntax Error: /mazehunt play [arena name]");
                } else if (args[0].equalsIgnoreCase("quit")) {
                    if (MazeHunt.gameController.playerPlaying(sender.getName())) {
                        MazeHunt.gameController.getPlayerGame(sender.getName()).gameManager.playerManager.playerQuit((Player)sender);
                    } else {
                        sender.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + "You're not currently playing MazeHunt!");
                    }
                } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                    sender.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + "-- MazeHunt --");
                    sender.sendMessage(MazeHunt.pluginPrefix + ChatColor.WHITE + "/mazehunt play [arena] - Play MazeHunt");
                    sender.sendMessage(MazeHunt.pluginPrefix + ChatColor.WHITE + "/mazehunt quit - Quit playing");
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("play")) {
                    if (!MazeHunt.gameController.playerPlaying(sender.getName())) {
                        if (MazeHunt.gameController.gameExists(args[1])) {
                            MazeHunt.gameController.getGame(args[1]).gameManager.playerManager.playerJoin((Player)sender);
                        } else {
                            sender.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + "Arena " + args[1] + " does not exist!");
                        }
                    } else {
                        sender.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + "You're already playing MazeHunt!");
                    }
                } else {
                    sender.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + "Unknown Command!");
                }
            } else {
                sender.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + "Unknown Command!");
            }
        } else {
            sender.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + "You don't have permissions to MazeHunt :(");
        }

        return true;
    }
}
