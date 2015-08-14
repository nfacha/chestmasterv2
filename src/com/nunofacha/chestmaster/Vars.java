package com.nunofacha.chestmaster;

import java.util.HashMap;

/**
 *
 * @author facha
 */
public class Vars {

    public static boolean USE_SQL = false;
    public static boolean USE_UUID = true;
    public static HashMap<String, Integer> open_chests = new HashMap<>();
    public static String CHEST_NAME = "ChestMaster";
    public static String SQL_DB_NAME = "chestmaster";
    public static int CHEST_SIZE = 9;
    public static HashMap<String, Integer> open_chests_adm = new HashMap<>();
    public static HashMap<String, String> open_chests_adm_owner = new HashMap<>();
}
