package com.pilzbros.mazehunt.game;

import com.pilzbros.mazehunt.MazeHunt;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

public class Signs {
    private Sign sign;
    private Arena arena;

    public Signs(Sign s, Arena a) {
        this.sign = s;
        this.arena = a;
        this.prepare();
    }

    public void update() {
        if (this.arena.gameManager.inProgress()) {
            this.setInProgress();
        } else if (this.arena.gameManager.inWaiting()) {
            this.setWaiting();
        } else {
            this.setJoin();
        }

    }

    private void prepare() {
        this.sign.setLine(0, MazeHunt.signPrefix);
        this.sign.setLine(1, this.arena.getName());
        this.sign.setLine(2, "");
        this.sign.setLine(3, "");
        this.sign.update();
        this.setJoin();
    }

    private void setJoin() {
        this.sign.setLine(2, MazeHunt.signJoinText);
        this.sign.setLine(3, "");
        this.sign.update();
    }

    private void setWaiting() {
        this.sign.setLine(2, MazeHunt.signWaitingText);
        this.sign.setLine(3, "" + ChatColor.GREEN + this.arena.gameManager.getNumberPlayers() + ChatColor.BLACK + " players");
        this.sign.update();
    }

    private void setInProgress() {
        this.sign.setLine(2, MazeHunt.signInprogressText);
        this.sign.setLine(3, "" + ChatColor.GREEN + this.arena.gameManager.getNumberPlayersAlive() + ChatColor.BLACK + " Alive" + ChatColor.YELLOW + " / " + ChatColor.RED + this.arena.gameManager.getNumberPlayersDead() + ChatColor.BLACK + " Dead");
        this.sign.update();
    }

    public void setDeleted() {
        this.sign.setLine(2, "" + ChatColor.RED + ChatColor.BOLD + "Deleted");
    }
}