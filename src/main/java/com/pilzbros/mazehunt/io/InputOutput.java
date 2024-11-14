package com.pilzbros.mazehunt.io;

import com.pilzbros.mazehunt.MazeHunt;
import com.pilzbros.mazehunt.game.Arena;
import com.pilzbros.mazehunt.game.Loc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class InputOutput {
    public static YamlConfiguration global;
    private static Connection connection;

    public InputOutput() {
        if (!MazeHunt.instance.getDataFolder().exists()) {
            try {
                MazeHunt.instance.getDataFolder().mkdir();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }

        global = new YamlConfiguration();
    }

    public void LoadSettings() {
        try {
            if (!(new File(MazeHunt.instance.getDataFolder(), "global.yml")).exists()) {
                global.save(new File(MazeHunt.instance.getDataFolder(), "global.yml"));
            }

            global.load(new File(MazeHunt.instance.getDataFolder(), "global.yml"));
            Setting[] var4;
            int var3 = (var4 = Setting.values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
                Setting s = var4[var2];
                if (global.get(s.getString()) == null) {
                    global.set(s.getString(), s.getDefault());
                }
            }

            global.save(new File(MazeHunt.instance.getDataFolder(), "global.yml"));
        } catch (FileNotFoundException var5) {
            var5.printStackTrace();
        } catch (IOException var6) {
            var6.printStackTrace();
        } catch (InvalidConfigurationException var7) {
            var7.printStackTrace();
        }

    }

    public static synchronized Connection getConnection() {
        if (connection == null) {
            connection = createConnection();
        }

        try {
            if (connection.isClosed()) {
                connection = createConnection();
            }
        } catch (SQLException var1) {
            var1.printStackTrace();
        }

        return connection;
    }

    private static Connection createConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection ret = DriverManager.getConnection("jdbc:sqlite:" + (new File(MazeHunt.instance.getDataFolder().getPath(), "db.sqlite")).getPath());
            ret.setAutoCommit(false);
            return ret;
        } catch (ClassNotFoundException var1) {
            MazeHunt.log.log(Level.SEVERE, "[MazeHunt] Fatal database connectione error (Class)");
            var1.printStackTrace();
            return null;
        } catch (SQLException var2) {
            MazeHunt.log.log(Level.SEVERE, "[MazeHunt] Fatal database connectione error (SQL)");
            var2.printStackTrace();
            return null;
        }
    }

    public static synchronized void freeConnection() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException var2) {
                var2.printStackTrace();
            }
        }

    }

    public void prepareDB() {
        Connection conn = getConnection();
        Statement st = null;

        try {
            st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS \"mazehunt_arena\" (\"Name\" VARCHAR PRIMARY KEY  NOT NULL , \"X1\" DOUBLE, \"Y1\" DOUBLE, \"Z1\" DOUBLE, \"X2\" DOUBLE, \"Y2\" DOUBLE, \"Z2\" DOUBLE, \"ReturnX\" DOUBLE, \"ReturnY\" DOUBLE, \"ReturnZ\" DOUBLE, \"WinX\" DOUBLE, \"WinY\" DOUBLE, \"WinZ\" DOUBLE, \"playWorld\" VARCHAR, \"returnWorld\" VARCHAR)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS \"mazehunt_signs\" (\"X\" DOUBLE, \"Y\" DOUBLE, \"Z\" DOUBLE, \"World\" VARCHAR, \"Arena\" VARCHAR)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS \"mazehunt_locations\" (\"X\" DOUBLE, \"Y\" DOUBLE, \"Z\" DOUBLE, \"World\" VARCHAR, \"Arena\" VARCHAR)");
            conn.commit();
            st.close();
        } catch (SQLException var4) {
            MazeHunt.log.log(Level.SEVERE, "[MazeHunt] Error preparing database! (SQL)");
            var4.printStackTrace();
        } catch (Exception var5) {
            MazeHunt.log.log(Level.SEVERE, "[MazeHunt] Error preparing database! (Unknown)");
        }

    }

    public void updateDB() {
    }

    public void Update(String check, String sql) {
        this.Update(check, sql, sql);
    }

    public void Update(String check, String sqlite, String mysql) {
        try {
            Statement statement = getConnection().createStatement();
            statement.executeQuery(check);
            statement.close();
        } catch (SQLException var13) {
            try {
                String[] query = sqlite.split(";");
                Connection conn = getConnection();
                Statement st = conn.createStatement();
                String[] var11 = query;
                int var10 = query.length;

                for(int var9 = 0; var9 < var10; ++var9) {
                    String q = var11[var9];
                    st.executeUpdate(q);
                }

                conn.commit();
                st.close();
                MazeHunt.log.log(Level.INFO, "[MazeHunt] Database upgraded");
            } catch (SQLException var12) {
                MazeHunt.log.log(Level.SEVERE, "[MazeHunt] Error while upgrading database to new version");
                var12.printStackTrace();
            }
        }

    }

    public void loadArena() {
        try {
            PreparedStatement ps = null;
            ResultSet result = null;
            Connection conn = getConnection();
            ps = conn.prepareStatement("SELECT `Name`, `X1`, `Y1`, `Z1`, `X2`, `Y2`, `Z2`, `ReturnX`, `ReturnY`, `ReturnZ`, `WinX`, `WinY`, `WinZ`, `playWorld`, `returnWorld` FROM `mazehunt_arena`");
            result = ps.executeQuery();

            int count;
            for(count = 0; result.next(); ++count) {
                MazeHunt.gameController.addGameManager(new Arena(result.getString("Name"), result.getDouble("X1"), result.getDouble("Y1"), result.getDouble("Z1"), result.getDouble("X2"), result.getDouble("Y2"), result.getDouble("Z2"), result.getString("playworld"), result.getDouble("ReturnX"), result.getDouble("ReturnY"), result.getDouble("ReturnZ"), result.getString("returnworld"), result.getDouble("WinX"), result.getDouble("WinY"), result.getDouble("WinZ")));
            }

            if (count > 0) {
                MazeHunt.log.log(Level.INFO, "[MazeHunt] " + count + " arena(s) loaded");
            }

            conn.commit();
            ps.close();
        } catch (SQLException var5) {
            MazeHunt.log.log(Level.WARNING, "[MazeHunt] Encountered an issue loading arenas");
        }

    }

    public void loadSigns() {
        try {
            PreparedStatement ps = null;
            ResultSet result = null;
            Connection conn = getConnection();
            ps = conn.prepareStatement("SELECT `X`, `Y`, `Z`, `World`, `Arena` FROM `mazehunt_signs`");
            result = ps.executeQuery();
            int count = 0;
            int removed = 0;
            new ArrayList();

            while(result.next()) {
                Location tmp = new Location(MazeHunt.instance.getServer().getWorld(result.getString("World")), result.getDouble("X"), result.getDouble("Y"), result.getDouble("Z"));

                try {
                    if (MazeHunt.gameController.arenaExist(result.getString("Arena"))) {
                        Block block = tmp.getBlock();
                        Sign sign = (Sign)block.getState();
                        Arena arena = MazeHunt.gameController.getArena(result.getString("Arena"));
                        arena.signManager.addSign(sign, arena);
                        ++count;
                    } else {
                        this.removeSign(result.getString("World"), result.getDouble("X"), result.getDouble("Y"), result.getDouble("Z"));
                        ++removed;
                    }
                } catch (ClassCastException var11) {
                    this.removeSign(result.getString("World"), result.getDouble("X"), result.getDouble("Y"), result.getDouble("Z"));
                    ++removed;
                } catch (Exception var12) {
                    this.removeSign(result.getString("World"), result.getDouble("X"), result.getDouble("Y"), result.getDouble("Z"));
                    ++removed;
                }
            }

            MazeHunt.log.log(Level.INFO, "[MazeHunt] Loaded " + count + " signs");
            result.close();
            conn.commit();
            ps.close();
        } catch (SQLException var13) {
            MazeHunt.log.log(Level.WARNING, "[MazeHunt] Encountered an issue loading signs: " + var13.getMessage());
        }

    }

    public void loadLocations() {
        try {
            PreparedStatement ps = null;
            ResultSet result = null;
            Connection conn = getConnection();
            ps = conn.prepareStatement("SELECT `X`, `Y`, `Z`, `World`, `Arena` FROM `mazehunt_locations`");
            result = ps.executeQuery();
            int count = 0;
            int removed = false;
            new ArrayList();

            while(result.next()) {
                MazeHunt.gameController.getArena(result.getString("Arena")).locationManager.addLocation(new Loc(MazeHunt.gameController.getArena(result.getString("Arena")), result.getDouble("X"), result.getDouble("Y"), result.getDouble("Z"), result.getString("World")));
                ++count;
            }

            conn.commit();
            ps.close();
            MazeHunt.log.log(Level.INFO, "[MazeHunt] Loaded " + count + " locations");
        } catch (SQLException var7) {
            MazeHunt.log.log(Level.SEVERE, "[MazeHunt] Encountered an issue loading locations: " + var7.getMessage());
        }

    }

    public void deleteArena(Arena arena) {
        this.removeArena(arena.getName());
    }

    public void storeSign(String world, double X, double Y, double Z, String arena) {
        try {
            Connection conn = getConnection();
            String sql = "INSERT INTO mazehunt_signs (`World`, `X`, `Y`, `Z`, `Arena`) VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, world);
            preparedStatement.setDouble(2, X);
            preparedStatement.setDouble(3, Y);
            preparedStatement.setDouble(4, Z);
            preparedStatement.setString(5, arena);
            preparedStatement.executeUpdate();
            conn.commit();
            MazeHunt.log.log(Level.INFO, "[MazeHunt] Sign stored to DB");
        } catch (SQLException var12) {
            MazeHunt.log.log(Level.WARNING, "[MazeHunt] Unexpected error while storing sign in database!");
            var12.printStackTrace();
        }

    }

    public void storeLocation(String world, double X, double Y, double Z, String arena) {
        try {
            Connection conn = getConnection();
            String sql = "INSERT INTO mazehunt_locations (`World`, `X`, `Y`, `Z`, `Arena`) VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, world);
            preparedStatement.setDouble(2, X);
            preparedStatement.setDouble(3, Y);
            preparedStatement.setDouble(4, Z);
            preparedStatement.setString(5, arena);
            preparedStatement.executeUpdate();
            conn.commit();
            MazeHunt.log.log(Level.INFO, "[MazeHunt] Location for arena " + arena + " stored to DB");
        } catch (SQLException var12) {
            MazeHunt.log.log(Level.WARNING, "[MazeHunt] Unexpected error while storing location in database!");
            var12.printStackTrace();
        }

    }

    public void removeSign(String world, double X, double Y, double Z) {
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM mazehunt_signs WHERE World = ? AND X = ? AND Y = ? AND Z = ?");
            ps.setString(1, world);
            ps.setDouble(2, X);
            ps.setDouble(3, Y);
            ps.setDouble(4, Z);
            ps.executeUpdate();
            conn.commit();
            ps.close();
        } catch (SQLException var10) {
            MazeHunt.log.log(Level.WARNING, "[MazeHunt] Error while removing sign from database");
            var10.printStackTrace();
        }

    }

    private void removeArena(String name) {
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM sandfall_arena WHERE Name = ?");
            ps.setString(1, name);
            ps.executeUpdate();
            conn.commit();
            ps.close();
        } catch (SQLException var4) {
            MazeHunt.log.log(Level.WARNING, "[MazeHunt] Error while removing arena");
            var4.printStackTrace();
        }

    }

    public void storeArena(String name, double x1, double y1, double z1, double x2, double y2, double z2, String world, double returnx, double returny, double returnz, String returnworld, double winx, double winy, double winz) {
        try {
            Connection conn = getConnection();
            String sql = "INSERT INTO mazehunt_arena (`Name`, `X1`, `Y1`, `Z1`, `X2`, `Y2`, `Z2`, `ReturnX`, `ReturnY`, `ReturnZ`, `WinX`, `WinY`, `WinZ`, `playWorld`, `returnWorld`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, x1);
            preparedStatement.setDouble(3, y1);
            preparedStatement.setDouble(4, z1);
            preparedStatement.setDouble(5, x2);
            preparedStatement.setDouble(6, y2);
            preparedStatement.setDouble(7, z2);
            preparedStatement.setDouble(8, returnx);
            preparedStatement.setDouble(9, returny);
            preparedStatement.setDouble(10, returnz);
            preparedStatement.setDouble(11, winx);
            preparedStatement.setDouble(12, winy);
            preparedStatement.setDouble(13, winz);
            preparedStatement.setString(14, world);
            preparedStatement.setString(15, returnworld);
            preparedStatement.executeUpdate();
            conn.commit();
        } catch (SQLException var31) {
            MazeHunt.log.log(Level.WARNING, "[MazeHunt] Unexpected error while storing arena in database!");
            var31.printStackTrace();
        }

    }

    public void updateArena() {
    }
}
