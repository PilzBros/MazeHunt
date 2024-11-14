package com.pilzbros.mazehunt.listener;


import com.pilzbros.mazehunt.MazeHunt;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {
    @EventHandler(
            priority = EventPriority.NORMAL
    )
    public void onBlockDestroy(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.WALL_SIGN && event.getBlock().getType() != Material.SIGN_POST) {
            if (MazeHunt.gameController.playerPlaying(event.getPlayer().getName()) && !event.getPlayer().hasPermission("MazeHunt.admin")) {
                event.setCancelled(true);
            }
        } else {
            Sign thisSign = (Sign)event.getBlock().getState();
            String[] lines = thisSign.getLines();
            if (lines[0].equalsIgnoreCase(MazeHunt.signPrefix)) {
                if (event.getPlayer().hasPermission("MazeHunt.admin")) {
                    if (MazeHunt.gameController.arenaExist(lines[1])) {
                        MazeHunt.gameController.getArena(lines[1]).signManager.removeSign(thisSign);
                        event.getPlayer().sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.GREEN + "MazeHunt arena " + MazeHunt.gameController.getArena(lines[1]).getName() + " sign removed!");
                    }
                } else {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + "You don't have permission to destroy MazeHunt signs!");
                }
            }
        }


