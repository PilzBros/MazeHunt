package com.pilzbros.mazehunt.manager;

import com.pilzbros.mazehunt.game.Arena;
import com.pilzbros.mazehunt.game.Loc;
import java.util.Iterator;
import java.util.List;
import org.bukkit.entity.Player;

public class LocationManager {
    private Arena arena;

    public LocationManager(Arena a) {
        this.arena = a;
    }

    public void addLocation(Loc l) {
        this.arena.locations.add(l);
    }

    public int numLocations() {
        return this.arena.locations.size();
    }

    public boolean availableLocation() {
        List<Loc> tmp = this.arena.getLocs();
        Iterator i = tmp.iterator();

        while(i.hasNext()) {
            Loc tmpLoc = (Loc)i.next();
            if (tmpLoc.isAvailable()) {
                return true;
            }
        }

        return false;
    }

    public Loc assignLocation(Player player) {
        List<Loc> tmp = this.arena.getLocs();
        Iterator i = tmp.iterator();

        while(i.hasNext()) {
            Loc tmpLoc = (Loc)i.next();
            if (tmpLoc.isAvailable()) {
                tmpLoc.assignPlayer(player.getName());
                return tmpLoc;
            }
        }

        return null;
    }

    public void unassignLocation(Player player) {
        List<Loc> tmp = this.arena.getLocs();
        Iterator i = tmp.iterator();

        while(i.hasNext()) {
            Loc tmpLoc = (Loc)i.next();
            if (!tmpLoc.isAvailable() && tmpLoc.getPlayer().getName().equalsIgnoreCase(player.getName())) {
                tmpLoc.clearPlayer();
            }
        }

    }

    public void resetLocations() {
        List<Loc> tmp = this.arena.getLocs();
        Iterator i = tmp.iterator();

        while(i.hasNext()) {
            Loc tmpLoc = (Loc)i.next();
            tmpLoc.clearPlayer();
        }

    }
}
