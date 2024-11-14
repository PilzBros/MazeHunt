package com.pilzbros.mazehunt.manager;

import com.pilzbros.mazehunt.MazeHunt;
import com.pilzbros.mazehunt.game.Loc;
import com.pilzbros.mazehunt.io.Setting;
import com.pilzbros.mazehunt.io.Settings;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerManager {
    private GameManager gameManager;
    private HashMap<String, Inventory> playerInventory;

    public PlayerManager(GameManager gm) {
        this.gameManager = gm;
        this.playerInventory = new HashMap();
    }

    public void prepare() {
    }

    public void playerJoin(Player player) {
        if (!this.gameManager.inProgress()) {
            if (this.gameManager.inWaiting()) {
                if (this.gameManager.arena.locationManager.availableLocation()) {
                    this.preparePlayer(player);
                    this.gameManager.checkStatus();
                } else {
                    player.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + Settings.getGlobalString(Setting.NoRoomMessage));
                    this.gameManager.checkStatus();
                }
            } else if (this.gameManager.arena.locationManager.availableLocation()) {
                this.preparePlayer(player);
                this.gameManager.checkStatus();
            } else {
                player.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + Settings.getGlobalString(Setting.NoRoomMessage));
                this.gameManager.checkStatus();
            }
        } else {
            player.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + Settings.getGlobalString(Setting.GameAlreadyInSession));
        }

    }

    public void playerDeath(Player player) {
        if (this.gameManager.swordAwarded() && this.gameManager.swordPlayer.equals(player)) {
            this.swordPlayerDeath(player);
        } else {
            this.regularPlayerDeath(player);
        }

    }

    protected void playerCheck() {
        if (this.gameManager.players.size() > 1) {
            if (this.gameManager.swordAwarded() && !this.gameManager.isPlaying(this.gameManager.getSwordPlayer())) {
                this.regularPlayerWin();
                this.gameManager.gameEnd();
            }
        } else if (this.gameManager.players.size() == 1) {
            if (this.gameManager.swordAwarded() && this.gameManager.isPlaying(this.gameManager.getSwordPlayer())) {
                this.swordPlayerWin();
                this.gameManager.gameEnd();
            } else {
                this.regularPlayerWin();
                this.gameManager.gameEnd();
            }
        }

    }

    private void swordPlayerWin() {
        this.playerDeathActions(this.gameManager.swordPlayer);
        this.gameManager.swordPlayer.sendMessage(MazeHunt.pluginPrefix + ChatColor.GOLD + Settings.getGlobalString(Setting.WinMessage));
    }

    private void regularPlayerWin() {
        Iterator it = this.gameManager.players.entrySet().iterator();

        while(it.hasNext()) {
            Entry pairs = (Entry)it.next();
            Player p = (Player)pairs.getValue();
            p.sendMessage(MazeHunt.pluginPrefix + ChatColor.GOLD + Settings.getGlobalString(Setting.WinMessageReg));
        }

    }

    protected void removeAllPlayers() {
        Iterator it = this.gameManager.players.entrySet().iterator();

        while(it.hasNext()) {
            Entry pairs = (Entry)it.next();
            Player p = (Player)pairs.getValue();
            this.playerDeathActions(p, false);
        }

    }

    private void swordPlayerDeath(Player player) {
        this.playerDeathActions(player, true);
        player.sendMessage(MazeHunt.pluginPrefix + ChatColor.GOLD + Settings.getGlobalString(Setting.SwordDeathMessage));
        this.gameManager.gameEnd();
    }

    private void regularPlayerDeath(Player player) {
        this.playerDeathActions(player);
        player.sendMessage(MazeHunt.pluginPrefix + ChatColor.GOLD + Settings.getGlobalString(Setting.DeathMessage));
    }

    private void playerDeathActions(Player player) {
        MazeHunt.log.log(Level.INFO, "Death actions for " + player.getName());
        ++this.gameManager.scoreboardDead;
        this.restorePlayer(player);
        this.removePlayer(player);
        this.teleportPlayerOut(player);
    }

    private void playerDeathActions(Player player, boolean check) {
        this.playerDeathActions(player);
        this.playerCheck();
    }

    public void playerQuit(Player player) {
        this.playerDeathActions(player);
        player.sendMessage(MazeHunt.pluginPrefix + ChatColor.GREEN + Settings.getGlobalString(Setting.QuitMessage));
        if (this.gameManager.swordAwarded() && this.gameManager.swordPlayer.getName().equals(player.getName())) {
            Iterator it = this.gameManager.players.entrySet().iterator();

            while(it.hasNext()) {
                Entry pairs = (Entry)it.next();
                Player p = (Player)pairs.getValue();
                this.regularPlayerDeath(p);
                p.sendMessage(MazeHunt.pluginPrefix + ChatColor.GREEN + Settings.getGlobalString(Setting.SwordPlayerQuit));
            }
        }

    }

    public void playerKick(Player player) {
        this.restorePlayer(player);
        this.removePlayer(player);
        player.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + Settings.getGlobalString(Setting.KickMessage));
        if (this.gameManager.swordAwarded() && this.gameManager.swordPlayer.getName().equals(player.getName())) {
            Iterator it = this.gameManager.players.entrySet().iterator();

            while(it.hasNext()) {
                Entry pairs = (Entry)it.next();
                Player p = (Player)pairs.getValue();
                this.regularPlayerDeath(p);
                p.sendMessage(MazeHunt.pluginPrefix + ChatColor.GREEN + Settings.getGlobalString(Setting.SwordPlayerQuit));
            }
        }

    }

    public void playerKickReload(Player player) {
        this.playerDeathActions(player);
        player.sendMessage(MazeHunt.pluginPrefix + ChatColor.YELLOW + Settings.getGlobalString(Setting.ReloadMessage));
    }

    public void playerTeleport(Player player) {
        player.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + Settings.getGlobalString(Setting.TeleportDisable));
    }

    public void playerExeCommand(Player player) {
        player.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + Settings.getGlobalString(Setting.CommandsDisable));
    }

    public void playerLogoff(Player player) {
        try {
            this.restorePlayer(player);
        } catch (Exception var5) {
            MazeHunt.log.log(Level.INFO, "[MazeHunt] User " + player.getName() + " left in game and could not be restored to before game settings");
        }

        this.removePlayer(player);
        if (this.gameManager.swordAwarded() && this.gameManager.swordPlayer.getName().equals(player.getName())) {
            Iterator it = this.gameManager.players.entrySet().iterator();

            while(it.hasNext()) {
                Entry pairs = (Entry)it.next();
                Player p = (Player)pairs.getValue();
                this.playerDeathActions(p);
                p.sendMessage(MazeHunt.pluginPrefix + ChatColor.GREEN + Settings.getGlobalString(Setting.SwordPlayerQuit));
            }
        }

    }

    private void messagePlayers(String message) {
        Iterator it = this.gameManager.players.entrySet().iterator();

        while(it.hasNext()) {
            Entry pairs = (Entry)it.next();
            Player p = (Player)pairs.getValue();
            p.sendMessage(MazeHunt.pluginPrefix + message);
        }

    }

    private void preparePlayer(Player player) {
        Loc playerLoc = this.gameManager.arena.locationManager.assignLocation(player);
        if (playerLoc != null) {
            this.teleportPlayerIn(player, playerLoc.getLocation());
            this.addPlayer(player);
            this.storeInventory(player);
            this.gameManager.scoreboardManager.displayCountdown(player);
            player.sendMessage(MazeHunt.pluginPrefix + ChatColor.GREEN + Settings.getGlobalString(Setting.WaitingMessage));
            this.messagePlayers(ChatColor.GREEN + player.getName() + ChatColor.WHITE + " just joined the game!");
        } else {
            this.removePlayer(player);
            player.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + Settings.getGlobalString(Setting.NoRoomMessage) + " 2");
            this.gameManager.checkStatus();
        }

    }

    private void restorePlayer(Player player) {
        this.gameManager.arena.locationManager.unassignLocation(player);
        this.gameManager.scoreboardManager.removeBoard(player);
        this.clearInventory(player);
        this.restoreInventory(player);
        this.teleportPlayerOut(player);
    }

    private void storeInventory(Player player) {
        PlayerInventory inv = player.getInventory();
        Inventory temp = Bukkit.getServer().createInventory((InventoryHolder)null, InventoryType.PLAYER);
        ItemStack[] var7;
        int var6 = (var7 = inv.getContents()).length;

        ItemStack stack;
        for(int var5 = 0; var5 < var6; ++var5) {
            stack = var7[var5];
            if (stack != null) {
                temp.addItem(new ItemStack[]{stack});
            }
        }

        this.playerInventory.put(player.getName(), temp);
        player.getInventory().clear();
        stack = new ItemStack(Material.GOLDEN_APPLE, 1);
        player.getInventory().addItem(new ItemStack[]{stack});
        player.updateInventory();
    }

    private void restoreInventory(Player player) {
        if (this.playerInventory.containsKey(player.getName())) {
            player.getInventory().clear();
            Inventory inv = (Inventory)this.playerInventory.get(player.getName());
            ItemStack[] var6;
            int var5 = (var6 = inv.getContents()).length;

            for(int var4 = 0; var4 < var5; ++var4) {
                ItemStack stack = var6[var4];
                if (stack != null) {
                    player.getInventory().addItem(new ItemStack[]{stack});
                }
            }

            this.playerInventory.remove(player.getName());
            player.updateInventory();
        } else {
            player.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + "There was an error while attempting to restore your inventory");
        }

    }

    private void clearInventory(Player player) {
        player.getInventory().clear();
    }

    private void teleportPlayerIn(Player player, Location location) {
        player.teleport(location);
    }

    private void teleportPlayerOut(Player player) {
        player.teleport(this.gameManager.arena.getReturnPoint());
    }

    private void removePlayer(Player player) {
        MazeHunt.log.log(Level.INFO, "removing player");
        this.gameManager.players.remove(player.getName());
        MazeHunt.gameController.removePlayer(player);
    }

    private void addPlayer(Player player) {
        MazeHunt.gameController.addPlayer(player, this.gameManager.arena);
        this.gameManager.players.put(player.getName(), player);
    }

    public void countdownNotify() {
        Iterator it = this.gameManager.players.entrySet().iterator();

        while(it.hasNext()) {
            Entry entry = (Entry)it.next();
            String name = (String)entry.getKey();
            Player player = (Player)entry.getValue();
            player.sendMessage(MazeHunt.pluginPrefix + ChatColor.GREEN + "Game starts in " + this.gameManager.countdownSeconds);
        }

    }

    protected void displayScoreboard() {
        Iterator it = this.gameManager.players.entrySet().iterator();

        while(it.hasNext()) {
            Entry entry = (Entry)it.next();
            String name = (String)entry.getKey();
            Player player = (Player)entry.getValue();
            this.gameManager.scoreboardManager.game.setScoreboard(player);
        }

    }

    protected void notEnoughPlayers() {
        Iterator it = this.gameManager.players.entrySet().iterator();

        while(it.hasNext()) {
            Entry entry = (Entry)it.next();
            String name = (String)entry.getKey();
            Player player = (Player)entry.getValue();
            this.playerDeathActions(player);
            player.sendMessage(MazeHunt.pluginPrefix + ChatColor.GOLD + "Not enough players joined the game before the countdown finished!");
        }

    }

    protected void awardSword(Player player) {
        ItemStack itemstack = new ItemStack(Material.getMaterial(Settings.getGlobalInt(Setting.WinSword)), 1);
        ItemMeta i = itemstack.getItemMeta();
        itemstack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        itemstack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
        itemstack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 5);
        ItemMeta itemMeta = itemstack.getItemMeta();
        itemMeta.setDisplayName("MazeHunter Assassin");
        itemstack.setItemMeta(itemMeta);
        player.getInventory().addItem(new ItemStack[]{itemstack});
        player.updateInventory();
        player.sendMessage(MazeHunt.pluginPrefix + ChatColor.GREEN + Settings.getGlobalString(Setting.SwordReachedMessage));
    }

    protected void awardPlayerSword(Player player) {
        ItemStack itemstack = new ItemStack(Material.getMaterial(Settings.getGlobalInt(Setting.OtherSword)), 1);
        ItemMeta i = itemstack.getItemMeta();
        itemstack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        itemstack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
        itemstack.addUnsafeEnchantment(Enchantment.LUCK, 10);
        ItemMeta itemMeta = itemstack.getItemMeta();
        itemMeta.setDisplayName("MazeHunter Survivor");
        itemstack.setItemMeta(itemMeta);
        ItemStack potion = new ItemStack(Material.POTION, 1, (short)8206);
        player.getInventory().addItem(new ItemStack[]{potion});
        player.getInventory().addItem(new ItemStack[]{itemstack});
        player.updateInventory();
        player.sendMessage(MazeHunt.pluginPrefix + ChatColor.RED + this.gameManager.swordPlayer.getName() + Settings.getGlobalString(Setting.SwordOtherMessage));
    }
}
