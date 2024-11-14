package com.pilzbros.mazehunt.manager;

import com.pilzbros.mazehunt.game.Arena;

public class ArenaManager {
    private Arena arena;

    public ArenaManager(Arena a) {
        this.arena = a;
    }

    public void prepare() {
        this.resetArena();
    }

    protected void resetArena() {
    }
}
