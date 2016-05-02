/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunofacha.chestmaster.listeners;

import com.nunofacha.chestmaster.AdvancedMetrics;
import com.nunofacha.chestmaster.Language;
import com.nunofacha.chestmaster.Main;
import com.nunofacha.chestmaster.Vars;
import com.nunofacha.chestmaster.commands.ChestCommand;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author Facha
 */
public class CommandEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent ev) {
        String command = ev.getMessage().split(" ")[0];
        String[] args = ev.getMessage().replace(command, "").split(" ");
        try {
            if (command.equalsIgnoreCase("/" + Vars.CHEST_COMMAND_NAME)) {
                Player p = ev.getPlayer();
                ev.setCancelled(true);
                int n = 1;
                try {
                    if (args.length >= 2) {
                        n = Integer.valueOf(args[1]);
                    }
                    if (n < 0) {
                        p.sendMessage(Language.INVALID_CHEST_NUMBER);
                        return;
                    }
                    if (n != 1) {
                        if (!p.hasPermission("chestmaster.multiple." + n)) {
                            p.sendMessage(Language.NO_PERMISSION_CHEST_NUMBER);
                            return;
                        }
                    } else {
                        if (!p.hasPermission("chestmaster.open")) {
                            p.sendMessage(Language.NO_PERMISSION);
                            return;

                        }
                    }
                    ChestCommand.openChest(p, n);
                } catch (SQLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    AdvancedMetrics.reportError(ex);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    AdvancedMetrics.reportError(ex);
                } catch (NumberFormatException e) {
                    p.sendMessage(Language.INVALID_CHEST_NUMBER);
                }

            }
        } catch (Exception r) {
            AdvancedMetrics.reportError(r);
        }
    }//
}
