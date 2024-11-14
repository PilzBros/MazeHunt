package com.pilzbros.mazehunt.game;

import com.pilzbros.mazehunt.MazeHunt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Loc {
    private Arena arena;
    private double X;
    private double Y;
    private double Z;
    private String world;
    private String player;

    public Loc(Arena a, double xx, double yy, double zz, String w) {
        this.arena = a;
        this.X = xx;
        this.Y = yy;
        this.Z = zz;
        this.world = w;
        this.player = null;
    }

    public boolean isAvailable() {
        return this.player == null || this.player.equalsIgnoreCase("null");
    }

    public void assignPlayer(String p) {
        this.player = p;
    }

    public void clearPlayer() {
        this.player = null;
    }

    public Player getPlayer() {
        return Bukkit.getServer().getPlayer(this.player);
    }

    public Location getLocation() {
        return new Location(MazeHunt.instance.getServer().getWorld(this.world), this.X, this.Y, this.Z);
    }
}
