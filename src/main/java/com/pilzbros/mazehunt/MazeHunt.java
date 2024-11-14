package com.pilzbros.mazehunt;

import com.pilzbros.mazehunt.command.AdminCommand;
import com.pilzbros.mazehunt.command.UserCommand;
import com.pilzbros.mazehunt.controller.GameController;
import com.pilzbros.mazehunt.io.InputOutput;
import com.pilzbros.mazehunt.listener.BlockListener;
import com.pilzbros.mazehunt.listener.PlayerListener;
import com.pilzbros.mazehunt.listener.SignListener;
import com.pilzbros.mazehunt.runnable.CountRunnable;
import com.pilzbros.mazehunt.runnable.GameRunnable;
import com.pilzbros.mazehunt.runnable.SignRunnable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class MazeHunt extends JavaPlugin implements Listener {
    public static final String pluginName = "MazeHunt";
    public static final String pluginVersion = "1.5";
    public static final String pluginPrefix;
    public static final String pluginAdminPrefix;
    public static final String signPrefix;
    public static final String signJoinText;
    public static final String signWaitingText;
    public static final String signInprogressText;
    public static MazeHunt instance;
    public static final GameController gameController;
    public static final Logger log;
    public static InputOutput IO;
    public static boolean updateNeeded;
    private BukkitTask signTask;
    private BukkitTask gameTask;
    private BukkitTask countTask;

    static {
        pluginPrefix = ChatColor.RED + "[MazeHunt] ";
        pluginAdminPrefix = ChatColor.GREEN + "[MazeHunt Admin] ";
        signPrefix = ChatColor.RED + "[MazeHunt]";
        signJoinText = ChatColor.GREEN + "Join";
        signWaitingText = ChatColor.AQUA + "Waiting for players...";
        signInprogressText = ChatColor.LIGHT_PURPLE + "In Progress";
        gameController = new GameController();
        log = Logger.getLogger("Minecraft");
    }

    public void onLoad() {
    }

    public void onEnable() {
        long startMili = System.currentTimeMillis() % 1000L;
        instance = this;
        IO = new InputOutput();
        IO.LoadSettings();
        IO.prepareDB();
        IO.loadArena();
        IO.loadSigns();
        IO.loadLocations();
        this.getCommand("mh").setExecutor(new UserCommand());
        this.getCommand("mha").setExecutor(new AdminCommand());
        this.getCommand("mazehunt").setExecutor(new UserCommand());
        this.getCommand("mazehuntadmin").setExecutor(new AdminCommand());
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new BlockListener(), this);
        this.getServer().getPluginManager().registerEvents(new SignListener(), this);
        this.signTask = Bukkit.getScheduler().runTaskTimer(this, new SignRunnable(), 40L, 40L);
        this.gameTask = Bukkit.getScheduler().runTaskTimer(this, new GameRunnable(), 20L, 20L);
        this.countTask = Bukkit.getScheduler().runTaskTimer(this, new CountRunnable(), 21L, 21L);
        log.log(Level.INFO, "[MazeHunt] Bootup took " + (System.currentTimeMillis() % 1000L - startMili) + " ms");
    }

    public void onReload() {
        this.signTask.cancel();
        this.gameTask.cancel();
        this.countTask.cancel();
        gameController.serverReload();
    }

    public void onDisable() {
        this.signTask.cancel();
        this.gameTask.cancel();
        this.countTask.cancel();
        gameController.serverReload();
    }
}