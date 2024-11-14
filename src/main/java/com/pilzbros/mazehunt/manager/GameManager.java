package com.pilzbros.mazehunt.manager;

import com.pilzbros.mazehunt.MazeHunt;
import com.pilzbros.mazehunt.game.Arena;
import com.pilzbros.mazehunt.io.Setting;
import com.pilzbros.mazehunt.io.Settings;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class GameManager {
    protected final Arena arena;
    public final PlayerManager playerManager;
    public final ScoreboardManager scoreboardManager;
    public boolean inWaiting;
    public boolean inProgress;
    public int countdownSeconds;
    public int scoreboardAlive;
    public int scoreboardDead;
    protected HashMap<String, Player> players;
    protected Player swordPlayer;

    public GameManager(Arena a) {
        this.arena = a;
        this.playerManager = new PlayerManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.players = new HashMap();
        this.inWaiting = false;
        this.inProgress = false;
        this.countdownSeconds = Settings.getGlobalInt(Setting.CountdownTime);
        this.swordPlayer = null;
        this.scoreboardAlive = 0;
        this.scoreboardDead = 0;
    }

    public void prepare() {
        this.arena.arenaManager.resetArena();
        this.resetCounter();
    }

    public void autoCheck() {
        this.updateScoreboardNumbers();
        this.checkStatus();
    }

    protected void checkStatus() {
        if (!this.inProgress && !this.inWaiting) {
            if (this.arePlayers()) {
                this.inWaiting = true;
                this.arena.signManager.updateSigns();
            }
        } else if (this.inProgress) {
            if (!this.arePlayers()) {
                this.inProgress = false;
                this.gameEnd();
            } else {
                this.updateScoreboardNumbers();
                this.scoreboardManager.updateScore();
                this.arena.signManager.updateSigns();
                this.playerManager.playerCheck();
            }
        } else if (this.inWaiting && !this.arePlayers()) {
            this.inWaiting = false;
        }

    }

    public void updateScoreboardNumbers() {
        this.scoreboardAlive = this.getNumberPlayers();
    }

    public void forceEnd() {
        this.serverReload();
        this.gameEnd();
    }

    public void serverReload() {
        Iterator it = this.players.entrySet().iterator();

        while(it.hasNext()) {
            Entry pairs = (Entry)it.next();
            Player p = (Player)pairs.getValue();
            this.playerManager.playerKickReload(p);
        }

    }

    public void timesUp() {
        if (this.players.size() >= Settings.getGlobalInt(Setting.MinPlayers)) {
            this.gamePrepare();
        } else {
            this.playerManager.notEnoughPlayers();
            this.gameEnd();
        }

    }

    private void gamePrepare() {
        this.arena.arenaManager.resetArena();
        this.gameStart();
    }

    private void gameStart() {
        this.inWaiting = false;
        this.inProgress = true;
        this.scoreboardAlive = this.players.size();
        this.scoreboardDead = 0;
        this.arena.signManager.updateSigns();
        this.scoreboardManager.updateScore();
        this.playerManager.displayScoreboard();
        this.resetCounter();
        Iterator it = this.players.entrySet().iterator();

        while(it.hasNext()) {
            Entry pairs = (Entry)it.next();
            Player p = (Player)pairs.getValue();
            p.sendMessage(MazeHunt.pluginPrefix + ChatColor.GOLD + Settings.getGlobalString(Setting.GameBeginMessage));
        }

    }

    protected void gameEnd() {
        this.playerManager.removeAllPlayers();
        this.inProgress = false;
        this.inWaiting = false;
        this.arena.signManager.updateSigns();
        this.arena.locationManager.resetLocations();
        this.swordPlayer = null;
        this.scoreboardAlive = 0;
        this.scoreboardDead = 0;
        this.resetCounter();
    }

    public boolean inWaiting() {
        return this.inWaiting;
    }

    public boolean inProgress() {
        return this.inProgress;
    }

    public boolean arePlayers() {
        return !this.players.isEmpty();
    }

    public boolean swordAwarded() {
        return this.swordPlayer != null;
    }

    public void winPointReached(Player player) {
        this.swordPlayer = player;
        this.playerManager.awardSword(player);
        Iterator it = this.players.entrySet().iterator();

        while(it.hasNext()) {
            Entry pairs = (Entry)it.next();
            Player p = (Player)pairs.getValue();
            if (!p.getName().equalsIgnoreCase(this.swordPlayer.getName())) {
                this.playerManager.awardPlayerSword(p);
            }
        }

    }

    public boolean isPlaying(Player player) {
        return this.players.containsKey(player.getName());
    }

    public Player getSwordPlayer() {
        return this.swordPlayer;
    }

    private void resetCounter() {
        this.countdownSeconds = Settings.getGlobalInt(Setting.CountdownTime);
    }

    public int getNumberPlayers() {
        return this.players.size();
    }

    public int getNumberPlayersAlive() {
        return this.scoreboardAlive;
    }

    public int getNumberPlayersDead() {
        return this.scoreboardDead;
    }

    public void moveCheck(Player p, Location l, PlayerMoveEvent event) {
        if (this.arena.gameManager.inWaiting) {
            p.teleport(event.getFrom());
        } else if (this.arena.gameManager.inProgress) {
            Location wp = this.arena.getWinPoint();
            if (wp.getBlockX() == l.getBlockX() && wp.getBlockY() == l.getBlockY() && wp.getBlockZ() == l.getBlockZ()) {
                if (this.arena.gameManager.swordAwarded()) {
                    p.sendMessage(MazeHunt.pluginPrefix + Settings.getGlobalString(Setting.SwordAlreadyAwarded));
                } else {
                    this.arena.gameManager.winPointReached(p);
                    new Location(l.getWorld(), (double)(l.getBlockX() + 2), (double)l.getBlockY(), (double)l.getBlockZ());
                }
            }
        }

    }
}
