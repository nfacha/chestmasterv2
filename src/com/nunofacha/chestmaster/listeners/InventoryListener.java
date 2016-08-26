package com.nunofacha.chestmaster.listeners;

import com.nunofacha.chestmaster.AdvancedMetrics;
import com.nunofacha.chestmaster.Main;
import com.nunofacha.chestmaster.Utils;
import com.nunofacha.chestmaster.Vars;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        try {
            if (e.getPlayer() instanceof Player) {
                Player p = (Player) e.getPlayer();
                String identifier = Utils.getPlayerIdentifier(p);
                if (Vars.open_chests.get(identifier) != null) {
                    int chest_ID = Vars.open_chests.get(identifier);
                    Inventory v = e.getInventory();
                    String serializedInventory = Utils.serializeInventory(v);
                    PreparedStatement st = Main.getConnection().prepareStatement("SELECT * FROM chests WHERE uuid = ? and number = ?");
                    st.setString(1, Utils.getPlayerIdentifier(p));
                    st.setInt(2, chest_ID);
                    ResultSet rs = st.executeQuery();
                    if (!rs.next()) {
                        if (!Utils.isInventoryEmpty(v)) {
                            PreparedStatement st2 = Main.getConnection().prepareStatement("INSERT INTO `chests` (`id`, `uuid`, `number`, `inventory`) VALUES (null, ?, ?, ?);");
                            st2.setString(1, Utils.getPlayerIdentifier(p));
                            st2.setInt(2, chest_ID);
                            st2.setString(3, serializedInventory);
                            st2.executeUpdate();
                        }
                    } else if (!Utils.isInventoryEmpty(v)) {
                        PreparedStatement st2 = Main.getConnection().prepareStatement("UPDATE chests SET inventory = ? WHERE uuid = ? AND number = ?");
                        st2.setString(1, serializedInventory);
                        st2.setString(2, identifier);
                        st2.setInt(3, chest_ID);
                        st2.executeUpdate();
                    } else {
                        PreparedStatement st2 = Main.getConnection().prepareStatement("DELETE from chests WHERE uuid = ? AND number = ?");
                        st2.setString(1, identifier);
                        st2.setInt(2, chest_ID);
                        st2.executeUpdate();
                    }
                    Vars.open_chests.remove(identifier);
                }
                if (Vars.open_chests_adm.containsKey(p.getName())) {
                    String targetOwner = Vars.open_chests_adm_owner.get(p.getName());
                    int chest_ID = Vars.open_chests_adm.get(p.getName());
                    Inventory v = e.getInventory();
                    String serializedInventory = Utils.serializeInventory(v);
                    if (!Utils.isInventoryEmpty(v)) {
                        PreparedStatement st2 = Main.getConnection().prepareStatement("UPDATE chests SET inventory = ? WHERE uuid = ? AND number = ?");
                        st2.setString(1, serializedInventory);
                        st2.setString(2, targetOwner);
                        st2.setInt(3, chest_ID);
                        st2.executeUpdate();
                    } else {
                        PreparedStatement st2 = Main.getConnection().prepareStatement("DELETE from chests WHERE uuid = ? AND number = ?");
                        st2.setString(1, targetOwner);
                        st2.setInt(2, chest_ID);
                        st2.executeUpdate();
                    }
                    Vars.open_chests_adm.remove(p.getName());
                    Vars.open_chests_adm_owner.remove(p.getName());
                }
                if (Vars.activeChest.contains(p)) {
                    Vars.activeChest.remove(p);
                }
            }
        } catch (Exception r) {
            AdvancedMetrics.reportError(r);
        }
    }

}
