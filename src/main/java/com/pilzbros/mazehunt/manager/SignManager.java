package com.pilzbros.mazehunt.manager;

import com.pilzbros.mazehunt.MazeHunt;
import com.pilzbros.mazehunt.game.Arena;
import com.pilzbros.mazehunt.game.Signs;
import java.util.Iterator;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class SignManager {
    private Arena arena;

    public SignManager(Arena a) {
        this.arena = a;
    }

    public void prepare() {
    }

    public void addSign(Sign sign, Arena arena) {
        arena.signs.add(new Signs(sign, arena));
    }

    public void newSign(Block block) {
        Sign thisSign = (Sign)block.getState();
        this.addSign(thisSign, this.arena);
        MazeHunt.IO.storeSign(block.getWorld().getName(), (double)block.getX(), (double)block.getY(), (double)block.getZ(), this.arena.getName());
    }

    public void removeSign(Sign sign) {
        this.arena.signs.remove(sign);
        MazeHunt.IO.removeSign(sign.getWorld().getName(), (double)sign.getX(), (double)sign.getY(), (double)sign.getZ());
    }

    public int numSigns() {
        return this.arena.signs.size();
    }

    public void setDeleted() {
        List<Signs> tmp = this.arena.getSigns();
        Iterator i = tmp.iterator();

        while(i.hasNext()) {
            Signs sign = (Signs)i.next();
            sign.setDeleted();
        }

    }

    public void updateSigns() {
        List<Signs> tmp = this.arena.getSigns();
        Iterator i = tmp.iterator();

        while(i.hasNext()) {
            Signs sign = (Signs)i.next();
            sign.update();
        }

    }
}
