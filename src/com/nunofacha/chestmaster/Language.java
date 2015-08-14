/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunofacha.chestmaster;

/**
 *
 * @author facha
 */
public class Language {

    public static final String CHAT_PREFIX = "§6[ChestMaster] §f";
    public static final String CONSOLE_PREFIX = "[ChestMaster] ";
    public static String NO_PERMISSION_CHEST_NUMBER = CHAT_PREFIX + Main.plugin.getConfig().getString("lang.NO_PERMISSION_CHEST_NUMBER");
    public static String NO_PERMISSION = CHAT_PREFIX + Main.plugin.getConfig().getString("lang.NO_PERMISSION");
    public static String INVALID_CHEST_NUMBER = CHAT_PREFIX+Main.plugin.getConfig().getString("lang.INVALID_CHEST_NUMBER");
    public static String ADM_CHEST_USAGE = CHAT_PREFIX+Main.plugin.getConfig().getString("lang.ADM_CHEST_USAGE");

}
