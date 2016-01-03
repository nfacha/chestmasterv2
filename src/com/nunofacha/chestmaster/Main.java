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
import net.vexgames.chestmaster.Updater;
import net.vexgames.chestmaster.Updater.UpdateResult;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

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
        if (!new File(Main.plugin.getDataFolder() + "/config.yml").exists()) {
            saveResource("config.yml", false);
            log.info(Language.CONSOLE_PREFIX + "Created config file!");
            Utils.readConfig();
        } else {
            if (!Main.plugin.getConfig().isSet("migrated")) {
                try {
                    NewVersionConverter.convert();
                    log.info(Language.CHAT_PREFIX + "Your old config file was renamed to config_old.yml, a new one will be created and loaded");
                    File oldConfig = new File(Main.plugin.getDataFolder() + "/config.yml");
                    oldConfig.renameTo(new File(Main.plugin.getDataFolder() + "/config_old.yml"));
                    saveResource("config.yml", false);
                    Utils.readConfig();
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                log.info(Language.CONSOLE_PREFIX + "Config file detected, reading config now!");
                Utils.readConfig();
            }
        }
        log.info(Language.CONSOLE_PREFIX + "Starting ChestMaster");
        if (Vars.USE_SQL) {
            log.info(Language.CONSOLE_PREFIX + "Using MySQL!");
            try {
                Connection c = DriverManager.getConnection(Vars.DB_URL, Vars.DB_USER, Vars.DB_PASS);
                Main.conn = c;
                Utils.createTables();
            } catch (SQLException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            registerMySQLKeepAlive();

        } else {
            log.info(Language.CONSOLE_PREFIX + "Using SQLite!");
            boolean e = registerSQLitekeepAlive();
            if (!e) {
                try {
                    Utils.createTables();
                } catch (SQLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Main.log.info(Language.CONSOLE_PREFIX + "SQLite already exists!");
            }

        }
        try {
            Utils.checkSQLVersion();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (Vars.UPDATER) {
            Updater updater = new Updater(this, 88582, this.getFile(), Updater.UpdateType.DEFAULT, false);
            if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
                Vars.UPDATE_FOUND = true;
                log.warning(Language.CONSOLE_PREFIX + "New update available, update at: http://dev.bukkit.org/bukkit-plugins/chestmaster/");
            } else {
                log.info(Language.CONSOLE_PREFIX + "You are running the latest version :)");

            }
        } else {
            log.warning(Language.CONSOLE_PREFIX + "Updater is disabled :(");

        }
        if (Vars.METRICS) {
            try {
                MetricsLite metrics = new MetricsLite(this);
                metrics.start();
                log.info(Language.CONSOLE_PREFIX+"Metrics Started");
            } catch (IOException e) {
                log.warning(Language.CONSOLE_PREFIX + "Error on ChestMaster stats system!");
            }
        } else {
            log.warning(Language.CONSOLE_PREFIX + "Metrics are disabled :(");
        }

        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
    }

    public static Connection getConnection() throws SQLException {
        Connection c = null;
        if (!Vars.USE_SQL) {
            return Main.conn;
        } else {
            Connection conn = DriverManager.getConnection(Vars.DB_URL, Vars.DB_USER, Vars.DB_PASS);
            return conn;
        }

        //return c;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("chest")) {
            Player p = (Player) sender;
            int n = 1;

            try {
                if (args.length >= 1) {
                    n = Integer.valueOf(args[0]);
                }
                if(n < 0){
                    p.sendMessage(Language.INVALID_CHEST_NUMBER);
                    return false;
                }
                if (n != 1) {
                    if (!p.hasPermission("chestmaster.multiple." + n)) {
                        p.sendMessage(Language.NO_PERMISSION_CHEST_NUMBER);
                        return false;
                    }
                } else {
                    if (!p.hasPermission("chestmaster.open")) {
                        p.sendMessage(Language.NO_PERMISSION);
                        return false;

                    }
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

    public static void registerMySQLKeepAlive() {
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
