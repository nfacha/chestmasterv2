package com.nunofacha.chestmaster.commands;

import com.nunofacha.chestmaster.Main;
import com.nunofacha.chestmaster.Utils;
import com.nunofacha.chestmaster.Vars;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author facha
 */
public class ChestCommand {

    public static void openChest(Player p, int number) throws SQLException, IOException {
        PreparedStatement st = Main.getConnection().prepareStatement("SELECT * FROM chests WHERE uuid = ? and number = ?");
        st.setString(1, Utils.getPlayerIdentifier(p));
        st.setInt(2, number);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            String Data = rs.getString("inventory");
            Inventory iv = Utils.unserializeInventory(Data);
            p.openInventory(iv);
        } else {
            Inventory v = Bukkit.createInventory(null, 9);
//            PreparedStatement st2 = Main.getConnection().prepareStatement("INSERT INTO `chests` (`id`, `uuid`, `number`, `inventory`) VALUES (null, ?, ?, ?);");
//            st2.setString(1, Utils.getPlayerIdentifier(p));
//            st2.setInt(2, number);
//            st2.setString(3, "{}");
//            st2.executeUpdate();
            Inventory iv = Bukkit.createInventory(null, 9);
            p.openInventory(iv);
        }
        Vars.open_chests.put(Utils.getPlayerIdentifier(p), number);
    }

}
