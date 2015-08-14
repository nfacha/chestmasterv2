package com.nunofacha.chestmaster;

import com.nunofacha.chestmaster.commands.AdmChestCommand;
import com.nunofacha.chestmaster.commands.ChestCommand;
import com.nunofacha.chestmaster.listeners.InventoryListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author facha
 */
public class Main extends JavaPlugin {

    public static Logger log = Bukkit.getLogger();
    public static Main plugin;
    public static Connection conn = null;

    public void onEnable() {
        plugin = this;
        log.info(Language.CONSOLE_PREFIX+"Starting ChestMaster");
        if (Vars.USE_SQL) {
            log.info(Language.CONSOLE_PREFIX+"Using MySQL, connecting now!");
        } else {
            log.info(Language.CONSOLE_PREFIX+"Using SQLite!");
            saveResource("config.yml", false);

        }
        boolean e = registerSQLitekeepAlive();
        if (!e) {
            try {
                Utils.createTables();
            } catch (SQLException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Main.log.info(Language.CONSOLE_PREFIX+"SQLite already exists!");
        }

        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
    }

    public static Connection getConnection() {
        Connection c = null;
        if (!Vars.USE_SQL) {
            return Main.conn;
        }

        return c;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("chest")) {
            Player p = (Player) sender;
            int n = 1;
            try {
                if (args.length >= 1) {
                    n = Integer.valueOf(args[0]);
                }
                if (!p.hasPermission("chestmaster.chest." + n)) {
                    p.sendMessage(Language.NO_PERMISSION_CHEST_NUMBER);
                    return false;
                }
                ChestCommand.openChest(p, n);
            } catch (SQLException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NumberFormatException e) {
                p.sendMessage(Language.INVALID_CHEST_NUMBER);
            }
        }
        if (command.getName().equals("admchest")) {
            Player p = (Player) sender;
            int n = 1;
            if (args.length < 2) {
                p.sendMessage(Language.ADM_CHEST_USAGE);
                return false;
            }
            try {
                n = Integer.valueOf(args[1]);
            } catch (NumberFormatException e) {
                p.sendMessage(Language.INVALID_CHEST_NUMBER);
                return false;
            }
            try {
                AdmChestCommand.adminOpenChest(p, args[0], n);
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return false;
    }

    public static boolean registerSQLitekeepAlive() {
        boolean existed = new File(Main.plugin.getDataFolder() + "/penguin.db").exists();
        String path = Main.plugin.getDataFolder().getAbsolutePath();
        try {
            Class.forName("org.sqlite.JDBC");
            Main.conn = DriverManager.getConnection("jdbc:sqlite:" + path + "/penguin.db");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {

            @Override
            public void run() {
                try {
                    PreparedStatement st = Main.getConnection().prepareStatement("SELECT 1");
                    st.executeQuery();
                } catch (SQLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }, 300, 300);
        return existed;
    }

}
