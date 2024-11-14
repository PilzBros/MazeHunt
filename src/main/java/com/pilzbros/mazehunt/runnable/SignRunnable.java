package com.pilzbros.mazehunt.runnable;

import com.pilzbros.mazehunt.MazeHunt;
import com.pilzbros.mazehunt.game.Arena;
import java.util.Iterator;
import java.util.Map.Entry;
import org.bukkit.scheduler.BukkitRunnable;

public class SignRunnable extends BukkitRunnable {
    public void run() {
        Iterator it = MazeHunt.gameController.getArenas().entrySet().iterator();

        while(it.hasNext()) {
            Entry entry = (Entry)it.next();
            String name = (String)entry.getKey();
            Arena arena = (Arena)entry.getValue();
            arena.signManager.updateSigns();
        }

    }
}
