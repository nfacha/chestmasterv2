package com.nunofacha.chestmaster;

import com.nunofacha.chestmaster.commands.AdmChestCommand;
import com.nunofacha.chestmaster.commands.ChestDebugCommand;
import com.nunofacha.chestmaster.commands.ChestHashCommand;
import com.nunofacha.chestmaster.listeners.CommandEvent;
import com.nunofacha.chestmaster.listeners.InventoryListener;
import com.nunofacha.chestmaster.listeners.MoveListener;
import net.gravitydevelopment.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        } else if (!Main.plugin.getConfig().isSet("migrated")) {
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
            if (!Main.plugin.getConfig().isSet("disable_dupe_kick")) {
                Main.plugin.getConfig().set("disable_dupe_kick", false);
                saveConfig();
                log.info(Language.CONSOLE_PREFIX + "Added disable_dupe_kick key to config file as FALSE");
            }
            if (!Main.plugin.getConfig().isSet("command_name")) {
                Main.plugin.getConfig().set("command_name", "chest");
                saveConfig();
                log.info(Language.CONSOLE_PREFIX + "Added command_name key to config file as chest");
            }
            if (!Main.plugin.getConfig().isSet("block_creative_access")) {
                Main.plugin.getConfig().set("block_creative_access", false);
                saveConfig();
                log.info(Language.CONSOLE_PREFIX + "Added block_creative_access key to config file as false");
            }
            if (!Main.plugin.getConfig().isSet("lang.NO_PERMISSION_CREATIVE")) {
                Main.plugin.getConfig().set("lang.NO_PERMISSION_CREATIVE", "You cant use /chest while in creative gamemode");
                saveConfig();
                log.info(Language.CONSOLE_PREFIX + "Added lang.NO_PERMISSION_CREATIVE language string to config file as You cant use /chest while in creative gamemode");
            }
            log.info(Language.CONSOLE_PREFIX + "Config file detected, reading config now!");
            Utils.readConfig();
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
            if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
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
                log.info(Language.CONSOLE_PREFIX + "Metrics Started");
            } catch (IOException e) {
                log.warning(Language.CONSOLE_PREFIX + "Error on ChestMaster stats system!");
            }
        } else {
            log.warning(Language.CONSOLE_PREFIX + "Metrics are disabled :(");
        }

        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        if (!Vars.DISABLE_DUPE_KICK) {
            Bukkit.getPluginManager().registerEvents(new MoveListener(), this);
        } else {
            log.warning(Language.CONSOLE_PREFIX + "Kick when dupe attemp is detected is disabled, this is NOT recommended!");
        }
        Bukkit.getPluginManager().registerEvents(new CommandEvent(), this);
        getCommand("chestdebug").setExecutor(new ChestDebugCommand());
        getCommand("chesthash").setExecutor(new ChestHashCommand());
    }

    public void onDisable() {

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
        if (command.getName().equalsIgnoreCase("chestreload")) {
            if (!sender.hasPermission("chestmaster.reload")) {
                sender.sendMessage(Language.NO_PERMISSION);
                return false;
            } else {
                Utils.readConfig();
                sender.sendMessage("§6Config reloaded");
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
