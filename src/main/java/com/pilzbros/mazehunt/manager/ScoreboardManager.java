package com.pilzbros.mazehunt.manager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class ScoreboardManager {
    private GameManager gameManager;
    protected BoardManager countdown;
    protected BoardManager game;
    protected BoardManager blank;

    public ScoreboardManager(GameManager gm) {
        this.gameManager = gm;
        this.countdown = new BoardManager("MazeHuntCountdown", ChatColor.RED + "MazeHunt", DisplaySlot.SIDEBAR);
        this.game = new BoardManager("MazeHuntGame", ChatColor.RED + "MazeHunt", DisplaySlot.SIDEBAR);
        this.blank = new BoardManager("test", "dummy", DisplaySlot.SIDEBAR);
    }

    protected void displayCountdown(Player player) {
        this.countdown.setScoreboard(player);
    }

    protected void displayScore(Player player) {
        this.game.setScoreboard(player);
    }

    protected void removeBoard(Player player) {
        this.blank.setScoreboard(player);
    }

    public void updateCountdown() {
        this.countdown.setObjectiveScore(ChatColor.GREEN + "Starts In", this.gameManager.countdownSeconds);
    }

    public void updateScore() {
        this.game.setObjectiveScore(ChatColor.DARK_GREEN + "Alive", this.gameManager.scoreboardAlive);
        this.game.setObjectiveScore(ChatColor.DARK_GREEN + "Dead", this.gameManager.scoreboardDead);
    }
}
