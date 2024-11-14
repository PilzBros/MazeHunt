package com.pilzbros.mazehunt.controller;

import com.pilzbros.mazehunt.game.Arena;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GameController {
    private HashMap<String, Arena> games = new HashMap();
    private HashMap<String, Arena> players = new HashMap();

    public void addGameManager(Arena arena) {
        this.games.put(arena.getName(), arena);
        arena.prepare();
    }

    public void removeGameManager(Arena a) {
        a.gameManager.forceEnd();
        a.signManager.setDeleted();
        this.games.remove(a.getName());
    }

    public Arena getArena(String name) {
        return (Arena) this.games.get(name);
    }

    public HashMap<String, Arena> getArenas() {
        return this.games;
    }

    public boolean arenaExist(String name) {
        return this.games.containsKey(name);
    }

    public boolean hasArenas() {
        return !this.games.isEmpty();
    }

    public void addPlayer(Player player, Arena a) {
        this.players.put(player.getName(), a);
    }

    public void removePlayer(Player player) {
        this.players.remove(player.getName());
    }

    public HashMap<String, Arena> getGames() {
        return this.games;
    }

    public boolean gameExists(String name) {
        return this.games.containsKey(name);
    }

    public boolean playerPlaying(String name) {
        return this.players.containsKey(name);
    }

    public Arena getGame(String name) {
        return (Arena) this.games.get(name);
    }

    public Arena getPlayerGame(String name) {
        return (Arena) this.players.get(name);
    }

    public void kickPlayer(String name) {
        if (this.playerPlaying(name)) {
            ((Arena) this.players.get(name)).gameManager.playerManager.playerKick(Bukkit.getPlayer(name));
            this.removePlayer(Bukkit.getPlayer(name));
        }

    }

    public void serverReload() {
        Iterator it = this.games.entrySet().iterator();

        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            String name = (String) entry.getKey();
            Arena arena = (Arena) entry.getValue();
            arena.gameManager.serverReload();
        }

    }
}