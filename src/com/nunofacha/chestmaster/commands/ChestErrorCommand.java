/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunofacha.chestmaster.commands;

import com.nunofacha.chestmaster.AdvancedMetrics;
import com.nunofacha.chestmaster.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Facha
 */
public class ChestErrorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Language.NO_PERMISSION);
            return false;
        }
        
        String message = "";
        sender.sendMessage("§aGenerating link, please wait...");
        message = AdvancedMetrics.getErrorURL();
        if(!message.contains("Permission denied")){
        sender.sendMessage("§2This is you unique, one-time use link to access your error log on AdvancedMetrics");
        sender.sendMessage("§6http://dev.nunofacha.com/metrics/errorLog.php?hash="+message);
        sender.sendMessage("§cBe advised, this link will work only once!");
        }else{
            sender.sendMessage("§cFailed to generate link, failed security check!");
        }
        return false;
    }

}
