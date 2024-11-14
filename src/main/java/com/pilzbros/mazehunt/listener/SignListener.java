package com.pilzbros.mazehunt.listener;

import com.pilzbros.mazehunt.MazeHunt;
import com.pilzbros.mazehunt.game.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {
    @EventHandler
    public void blockPlaced(SignChangeEvent event) {
        if (event.getBlock().getType() == Material.WALL_SIGN || event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.SIGN) {
            String[] signline = event.getLines();
            if (signline[0].equalsIgnoreCase("[MazeHunt]")) {
                if (event.getPlayer().hasPermission("mazehunt.admin")) {
                    if (MazeHunt.gameController.arenaExist(signline[1])) {
                        if (signline[2].equalsIgnoreCase("sword")) {
                            Sign sign = (Sign) event.getBlock().getState();
                            sign.setLine(0, MazeHunt.signPrefix);
                            sign.setLine(1, signline[1]);
                            sign.setLine(2, ChatColor.RED + "GET SWORD");
                            sign.update();
                            sign.update(true);
                            event.getPlayer().sendMessage("sword sign");
                        } else {
                            MazeHunt.gameController.getArena(signline[1]).signManager.newSign(event.getBlock());
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.GREEN + "MazeHunt arena " + signline[1] + " sign created!");
                        }
                    } else {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.RED + "The arena " + signline[1] + " does not exist!");
                    }
                } else {
                    event.getPlayer().sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + "You don't have permissions to make MazeHunt signs");
                    event.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void clickHandler(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block clickedBlock = e.getClickedBlock();
            if (clickedBlock.getType() == Material.SIGN || clickedBlock.getType() == Material.SIGN_POST || clickedBlock.getType() == Material.WALL_SIGN) {
                Sign thisSign = (Sign) clickedBlock.getState();
                String[] lines = thisSign.getLines();
                if (lines[0].equalsIgnoreCase(MazeHunt.signPrefix)) {
                    if (!MazeHunt.gameController.playerPlaying(e.getPlayer().getName())) {
                        if (MazeHunt.gameController.arenaExist(lines[1])) {
                            Arena arena = MazeHunt.gameController.getArena(lines[1]);
                            if (lines[2].equalsIgnoreCase(ChatColor.RED + "GET SWORD")) {
                                if (arena.gameManager.swordAwarded()) {
                                    e.getPlayer().sendMessage(MazeHunt.pluginPrefix + "Sword has already been reached!");
                                } else {
                                    arena.gameManager.winPointReached(e.getPlayer());
                                }
                            } else if (arena.isEnabled()) {
                                if (!arena.gameManager.inProgress() && !arena.gameManager.inWaiting()) {
                                    arena.gameManager.playerManager.playerJoin(e.getPlayer());
                                } else if (arena.gameManager.inWaiting()) {
                                    arena.gameManager.playerManager.playerJoin(e.getPlayer());
                                } else if (arena.gameManager.inProgress()) {
                                    e.getPlayer().sendMessage(MazeHunt.pluginPrefix + "The game in this arena is already in progress. Please wait until the game ends");
                                }
                            } else {
                                e.getPlayer().sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + "This arena has been disabled!");
                            }
                        } else {
                            e.getPlayer().sendMessage(MazeHunt.pluginPrefix + "That arena does not exist!");
                        }
                    } else {
                        e.getPlayer().sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + "You're already playing MazeHunt! To leave your current game, type /mazehunt quit");
                    }
                }
            }
        }

    }
}
