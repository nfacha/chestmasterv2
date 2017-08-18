/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunofacha.chestmaster.commands;

import com.nunofacha.chestmaster.AdvancedMetrics;
import com.nunofacha.chestmaster.Language;
import com.nunofacha.chestmaster.Main;
import com.nunofacha.chestmaster.Utils;
import com.nunofacha.chestmaster.Vars;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author facha
 */
public class AdmChestCommand {

    public static void adminOpenChest(Player p, String target, int number) {
        try {
            if (!p.hasPermission("chestmaster.admchest")) {
                p.sendMessage(Language.NO_PERMISSION);
                return;
            }
            String targetIdentifier = "";
            if (Bukkit.getPlayer(target) != null) {
                targetIdentifier = Utils.getPlayerIdentifier(Bukkit.getPlayer(target));
            } else {
                targetIdentifier = Utils.getOfflinePlayerIdentifier(Bukkit.getOfflinePlayer(target));
            }
            PreparedStatement st = Main.getConnection().prepareStatement("SELECT * FROM chests WHERE uuid = ? and number = ?");
            st.setString(1, targetIdentifier);
            st.setInt(2, number);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String Data = rs.getString("inventory");
                Inventory iv = Utils.unserializeInventory(Data);
                p.openInventory(iv);
                Vars.open_chests_adm.put(p.getName(), number);
                Vars.open_chests_adm_owner.put(p.getName(), targetIdentifier);
            } else {
                p.sendMessage(Language.INVALID_CHEST_NUMBER);
            }
        } catch (Exception r) {
            AdvancedMetrics.reportError(r);
        }
    }

}
