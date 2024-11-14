package com.pilzbros.mazehunt.runnable;

import com.pilzbros.mazehunt.MazeHunt;
import com.pilzbros.mazehunt.game.Arena;
import java.util.Iterator;
import java.util.Map.Entry;
import org.bukkit.scheduler.BukkitRunnable;

public class CountRunnable extends BukkitRunnable {
    public void run() {
        if (MazeHunt.gameController.hasArenas()) {
            Iterator it = MazeHunt.gameController.getArenas().entrySet().iterator();

            while(it.hasNext()) {
                Entry entry = (Entry)it.next();
                String name = (String)entry.getKey();
                Arena arena = (Arena)entry.getValue();
                arena.gameManager.autoCheck();
                if (arena.gameManager.inWaiting()) {
                    if (arena.gameManager.countdownSeconds <= 0) {
                        arena.gameManager.timesUp();
                    } else {
                        --arena.gameManager.countdownSeconds;
                        arena.gameManager.scoreboardManager.updateCountdown();
                        arena.signManager.updateSigns();
                        if (arena.gameManager.countdownSeconds <= 0) {
                            arena.gameManager.timesUp();
                        } else if (arena.gameManager.countdownSeconds <= 5) {
                            arena.gameManager.playerManager.countdownNotify();
                        }
                    }
                }
            }
        }

    }
}
