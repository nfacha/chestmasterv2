/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunofacha.chestmaster.listeners;

import com.nunofacha.chestmaster.Vars;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author Facha
 */
public class MoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (Vars.activeChest.contains(p)) {
            p.closeInventory();
            p.kickPlayer("Do not try to duplicate items!");
            Vars.activeChest.remove(p);
        }
    }

}
