package com.pilzbros.mazehunt.game;

import com.pilzbros.mazehunt.MazeHunt;
import com.pilzbros.mazehunt.manager.ArenaManager;
import com.pilzbros.mazehunt.manager.GameManager;
import com.pilzbros.mazehunt.manager.LocationManager;
import com.pilzbros.mazehunt.manager.SignManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Location;

public class Arena {
    String name;
    private double X1;
    private double Y1;
    private double Z1;
    private double X2;
    private double Y2;
    private double Z2;
    private String world;
    private Location returnLocation;
    private Location winLocation;
    private boolean enabled;
    public final SignManager signManager = new SignManager(this);
    public final GameManager gameManager = new GameManager(this);
    public final ArenaManager arenaManager = new ArenaManager(this);
    public final LocationManager locationManager = new LocationManager(this);
    public List<Signs> signs = new ArrayList();
    public List<Loc> locations = new ArrayList();

    public Arena(String name, double x1, double y1, double z1, double x2, double y2, double z2, String world, double returnx, double returny, double returnz, String returnworld, double winx, double winy, double winz) {
        this.winLocation = new Location(MazeHunt.instance.getServer().getWorld(world), winx, winy, winz);
        this.returnLocation = new Location(MazeHunt.instance.getServer().getWorld(returnworld), returnx, returny, returnz);
        this.enabled = true;
        this.name = name;
        this.X1 = x1;
        this.X2 = x2;
        this.Y1 = y1;
        this.Y2 = y2;
        this.Z1 = z1;
        this.Z2 = z2;
        this.world = world;
    }

    public String getName() {
        return this.name;
    }

    public List<Signs> getSigns() {
        return this.signs;
    }

    public List<Loc> getLocs() {
        return this.locations;
    }

    public void prepare() {
        this.gameManager.prepare();
        this.arenaManager.prepare();
        this.signManager.prepare();
        MazeHunt.log.log(Level.INFO, "[MazeHunt] Arena " + this.name + " prepared!");
    }

    public Location getReturnPoint() {
        return this.returnLocation;
    }

    public Location getWinPoint() {
        return this.winLocation;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
