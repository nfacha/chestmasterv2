package com.nunofacha.chestmaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.entity.Player;

/**
 *
 * @author facha
 */
public class Vars {

    //This will be loaded from config
    public static boolean USE_SQL = false;
    public static boolean USE_UUID = false;
    public static HashMap<String, Integer> open_chests = new HashMap<>();
    public static String CHEST_NAME = "ChestMaster";
    public static int CHEST_SIZE = 9;
    public static HashMap<String, Integer> open_chests_adm = new HashMap<>();
    public static HashMap<String, String> open_chests_adm_owner = new HashMap<>();
    public static boolean UPDATER = false;
    public static boolean METRICS = false;
    public static final String JDBC_Driver = "com.mysql.jdbc.driver";
    public static String DB_URL = "jdbc:mysql://127.0.0.1:3306/dbname";
    public static String DB_USER = "";
    public static String DB_PASS = "";
    public static String DB_HOST = "";
    public static String DB_NAME = "";
    public static boolean UPDATE_FOUND = false;
    public static List<Player> activeChest = new ArrayList<Player>();
    public static boolean ADVANCED_METRICS = true;
    public static boolean DISABLE_DUPE_KICK = false;
    public static boolean BLOCK_CREATIVE_ACCESS = false;

}
