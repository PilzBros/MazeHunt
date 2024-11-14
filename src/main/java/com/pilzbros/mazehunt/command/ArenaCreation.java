package com.pilzbros.mazehunt.command;

import com.pilzbros.mazehunt.MazeHunt;
import com.pilzbros.mazehunt.game.Arena;
import com.pilzbros.mazehunt.game.Loc;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ArenaCreation {
    private int state = 0;
    public static HashMap<String, ArenaCreation.CreationPlayer> players = new HashMap();

    public static void selectstart(Player player, String name) {
        if (players.containsKey(player.getName())) {
            players.remove(player.getName());
        }

        player.sendMessage(ChatColor.AQUA + "---------- MazeHunt Arena Setup ----------");
        player.sendMessage(ChatColor.GREEN + "Select the first point of the arena " + name);
        player.sendMessage(ChatColor.AQUA + "--------------------------------------");
        players.put(player.getName(), new ArenaCreation.CreationPlayer());
        ((ArenaCreation.CreationPlayer) players.get(player.getName())).name = name.toLowerCase();
    }

    public static void select(Player player, Block block) {
        switch (((ArenaCreation.CreationPlayer) players.get(player.getName())).state) {
            case 1:
                firstPoint(player, block);
                break;
            case 2:
                secondPoint(player, block);
                break;
            case 3:
                winPoint(player);
                break;
            case 4:
                startLoc1(player, block);
                break;
            case 5:
                startLoc2(player, block);
                break;
            case 6:
                startLoc3(player, block);
                break;
            case 7:
                startLoc4(player, block);
                break;
            case 8:
                returnPoint(player);
        }

    }

    private static void firstPoint(Player player, Block block) {
        player.sendMessage(ChatColor.AQUA + "---------- MazeHunt Arena Setup ----------");
        player.sendMessage(ChatColor.GREEN + "Select the second point of the arena");
        player.sendMessage(ChatColor.AQUA + "---------------------------------------");
        ArenaCreation.CreationPlayer cr = (ArenaCreation.CreationPlayer) players.get(player.getName());
        cr.X1 = (double) block.getX();
        cr.Y1 = (double) block.getY();
        cr.Z1 = (double) block.getZ();
        ++cr.state;
    }

    private static void secondPoint(Player player, Block block) {
        player.sendMessage(ChatColor.GOLD + "---------- MazeHunt Arena Setup ----------");
        player.sendMessage(ChatColor.GREEN + "Select anything to set your current location as sword location");
        player.sendMessage(ChatColor.GOLD + "----------------------------------------");
        ArenaCreation.CreationPlayer cr = (ArenaCreation.CreationPlayer) players.get(player.getName());
        cr.X2 = player.getLocation().getX();
        cr.Y2 = player.getLocation().getY();
        cr.Z2 = player.getLocation().getZ();
        cr.teleWorld = block.getWorld().getName();
        ++cr.state;
    }

    private static void winPoint(Player player) {
        player.sendMessage(ChatColor.BLUE + "---------- MazeHunt Arena Setup ----------");
        player.sendMessage(ChatColor.GREEN + "1st Starting Point: Select anything to set your current location as the 1st starting point");
        player.sendMessage(ChatColor.BLUE + "----------------------------------------");
        ArenaCreation.CreationPlayer cr = (ArenaCreation.CreationPlayer) players.get(player.getName());
        cr.winX = player.getLocation().getX();
        cr.winY = player.getLocation().getY();
        cr.winZ = player.getLocation().getZ();
        ++cr.state;
    }

    private static void startLoc1(Player player, Block block) {
        player.sendMessage(ChatColor.BLUE + "---------- MazeHunt Arena Setup ----------");
        player.sendMessage(ChatColor.GREEN + "2nd Starting Point: Select anything to set your current location as the 2nd starting point");
        player.sendMessage(ChatColor.BLUE + "----------------------------------------");
        ArenaCreation.CreationPlayer cr = (ArenaCreation.CreationPlayer) players.get(player.getName());
        cr.l1X = player.getLocation().getX();
        cr.l1Y = player.getLocation().getY();
        cr.l1Z = player.getLocation().getZ();
        ++cr.state;
    }

    private static void startLoc2(Player player, Block block) {
        player.sendMessage(ChatColor.BLUE + "---------- MazeHunt Arena Setup ----------");
        player.sendMessage(ChatColor.GREEN + "3rd Starting Point: Select anything to set your current location as the 3rd starting point");
        player.sendMessage(ChatColor.BLUE + "----------------------------------------");
        ArenaCreation.CreationPlayer cr = (ArenaCreation.CreationPlayer) players.get(player.getName());
        cr.l2X = player.getLocation().getX();
        cr.l2Y = player.getLocation().getY();
        cr.l2Z = player.getLocation().getZ();
        ++cr.state;
    }

    private static void startLoc3(Player player, Block block) {
        player.sendMessage(ChatColor.BLUE + "---------- MazeHunt Arena Setup ----------");
        player.sendMessage(ChatColor.GREEN + "Final Starting Point: Select anything to set your current location as the final starting point");
        player.sendMessage(ChatColor.BLUE + "----------------------------------------");
        ArenaCreation.CreationPlayer cr = (ArenaCreation.CreationPlayer) players.get(player.getName());
        cr.l3X = player.getLocation().getX();
        cr.l3Y = player.getLocation().getY();
        cr.l3Z = player.getLocation().getZ();
        ++cr.state;
    }

    private static void startLoc4(Player player, Block block) {
        player.sendMessage(ChatColor.RED + "---------- MazeHunt Arena Setup ----------");
        player.sendMessage(ChatColor.GREEN + "Select anything to set your location where players will be sent after the game");
        player.sendMessage(ChatColor.RED + "----------------------------------------");
        ArenaCreation.CreationPlayer cr = (ArenaCreation.CreationPlayer) players.get(player.getName());
        cr.l4X = player.getLocation().getX();
        cr.l4Y = player.getLocation().getY();
        cr.l4Z = player.getLocation().getZ();
        ++cr.state;
    }

    private static void returnPoint(Player player) {
        ArenaCreation.CreationPlayer cr = (ArenaCreation.CreationPlayer) players.get(player.getName());
        cr.returnX = player.getLocation().getX();
        cr.returnY = player.getLocation().getY();
        cr.returnZ = player.getLocation().getZ();
        cr.freeWorld = player.getLocation().getWorld().getName();
        ++cr.state;
        MazeHunt.gameController.addGameManager(new Arena(cr.name, cr.X1, cr.Y1, cr.Z1, cr.X2, cr.Y2, cr.Z2, cr.teleWorld, cr.returnX, cr.returnY, cr.returnZ, cr.freeWorld, cr.winX, cr.winY, cr.winZ));
        MazeHunt.IO.storeArena(cr.name, cr.X1, cr.Y1, cr.Z1, cr.X2, cr.Y2, cr.Z2, cr.teleWorld, cr.returnX, cr.returnY, cr.returnZ, cr.freeWorld, cr.winX, cr.winY, cr.winZ);
        MazeHunt.IO.storeLocation(cr.teleWorld, cr.l1X, cr.l1Y, cr.l1Z, cr.name);
        MazeHunt.IO.storeLocation(cr.teleWorld, cr.l2X, cr.l2Y, cr.l2Z, cr.name);
        MazeHunt.IO.storeLocation(cr.teleWorld, cr.l3X, cr.l3Y, cr.l3Z, cr.name);
        MazeHunt.IO.storeLocation(cr.teleWorld, cr.l4X, cr.l4Y, cr.l4Z, cr.name);
        MazeHunt.gameController.getArena(cr.name).locationManager.addLocation(new Loc(MazeHunt.gameController.getArena(cr.name), cr.l1X, cr.l1Y, cr.l1Z, cr.teleWorld));
        MazeHunt.gameController.getArena(cr.name).locationManager.addLocation(new Loc(MazeHunt.gameController.getArena(cr.name), cr.l2X, cr.l2Y, cr.l2Z, cr.teleWorld));
        MazeHunt.gameController.getArena(cr.name).locationManager.addLocation(new Loc(MazeHunt.gameController.getArena(cr.name), cr.l3X, cr.l3Y, cr.l3Z, cr.teleWorld));
        MazeHunt.gameController.getArena(cr.name).locationManager.addLocation(new Loc(MazeHunt.gameController.getArena(cr.name), cr.l4X, cr.l4Y, cr.l4Z, cr.teleWorld));
        player.sendMessage(MazeHunt.pluginAdminPrefix + ChatColor.GREEN + "MazeHunt arena " + cr.name + " setup successfully!");
        players.remove(player.getName());
    }

    private static class CreationPlayer {
        public int state = 1;
        private String name;
        private double X1;
        private double Y1;
        private double Z1;
        private double X2;
        private double Y2;
        private double Z2;
        private double winX;
        private double winY;
        private double winZ;
        private double returnX;
        private double returnY;
        private double returnZ;
        private String teleWorld;
        private String freeWorld;
        private double l1X;
        private double l1Y;
        private double l1Z;
        private double l2X;
        private double l2Y;
        private double l2Z;
        private double l3X;
        private double l3Y;
        private double l3Z;
        private double l4X;
        private double l4Y;
        private double l4Z;

        public CreationPlayer() {
        }
    }
}
