package com.pilzbros.mazehunt.listener;

import com.pilzbros.mazehunt.MazeHunt;
import com.pilzbros.mazehunt.command.ArenaCreation;
import com.pilzbros.mazehunt.io.Setting;
import com.pilzbros.mazehunt.io.Settings;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        if (Settings.getGlobalBoolean(Setting.NotifyOnAustinPilz)) {
            Iterator var3 = Bukkit.getServer().getOnlinePlayers().iterator();

            while (var3.hasNext()) {
                Player p = (Player) var3.next();
                if (p.hasPermission("mazehunt.admin")) {
                    p.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.GREEN + "MazeHunt developer has joined the server!");
                }
            }
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        if (MazeHunt.gameController.playerPlaying(evt.getPlayer().getName())) {
            MazeHunt.gameController.getPlayerGame(evt.getPlayer().getName()).gameManager.playerManager.playerLogoff(evt.getPlayer());
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onPlayerDieEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (MazeHunt.gameController.playerPlaying(player.getName()) && player.getHealth() <= event.getDamage()) {
                event.setCancelled(true);
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                player.setFireTicks(0);
                MazeHunt.gameController.getPlayerGame(player.getName()).gameManager.playerManager.playerDeath(player);
            }

        }
    }

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onPlayerRespawn(PlayerRespawnEvent evt) {
        MazeHunt.gameController.playerPlaying(evt.getPlayer().getName());
    }

    @EventHandler(
            priority = EventPriority.LOW
    )
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent evt) {
        if (MazeHunt.gameController.playerPlaying(evt.getPlayer().getName()) && !Settings.getGlobalBoolean(Setting.InGameCommands) && !evt.getMessage().toLowerCase().startsWith("/mh") && !evt.getMessage().toLowerCase().startsWith("/mha") && !evt.getMessage().toLowerCase().startsWith("/mazehunt") && !evt.getMessage().toLowerCase().startsWith("/mazehuntadmin")) {
            evt.setCancelled(true);
            MazeHunt.gameController.getPlayerGame(evt.getPlayer().getName()).gameManager.playerManager.playerExeCommand(evt.getPlayer());
        }

    }

    @EventHandler(
            priority = EventPriority.LOW
    )
    public void onPlayerTeleport(PlayerTeleportEvent evt) {
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getItemInHand().getTypeId() == Settings.getGlobalInt(Setting.SelectionTool)) {
            if (ArenaCreation.players.containsKey(event.getPlayer().getName())) {
                ArenaCreation.select(event.getPlayer(), event.getClickedBlock());
                event.setCancelled(true);
            } else if (ArenaCreation.players.containsKey(event.getPlayer().getName())) {
                ArenaCreation.select(event.getPlayer(), event.getClickedBlock());
                event.setCancelled(true);
            }
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onPlayerMove(PlayerMoveEvent evt) {
        if (MazeHunt.gameController.playerPlaying(evt.getPlayer().getName())) {
            MazeHunt.gameController.getPlayerGame(evt.getPlayer().getName()).gameManager.moveCheck(evt.getPlayer(), evt.getPlayer().getLocation(), evt);
        }

    }
}
