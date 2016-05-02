/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunofacha.chestmaster.commands;

import com.nunofacha.chestmaster.AdvancedMetrics;
import com.nunofacha.chestmaster.Language;
import com.nunofacha.chestmaster.Main;
import com.nunofacha.chestmaster.Vars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Facha
 */
public class ChestDebugCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Language.NO_PERMISSION);
            return false;
        }
        String message = "";
        message += "=== ChestMaster Debug Information ===";
        message += "\nOnline Mode: " + Main.plugin.getServer().getOnlineMode();
        message += "\nUsing UUID: " + Vars.USE_UUID;
        message += "\nUsing MySQL: " + Vars.USE_SQL;
        message += "\nAuto Update: " + Vars.UPDATER;
        message += "\nMetrics: " + Vars.METRICS;
        message += "\nAdvanced Metrics: " + Vars.ADVANCED_METRICS;
        message += "\nMetrics ID: " + AdvancedMetrics.metricsID;
        message += "\nError Reporting: " + Vars.REPORT_ERRORS;
        message += "\nServer Version: " + Main.plugin.getServer().getVersion();
        message += "\nOS: " + System.getProperty("os.name");
        message += "\nPlugin Version: " + Main.plugin.getServer().getPluginManager().getPlugin("ChestMaster").getDescription().getVersion();
        message += "\n************************************";
        sender.sendMessage(message);

        return false;
    }

}
