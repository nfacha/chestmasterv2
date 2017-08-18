package com.nunofacha.chestmaster.commands;

import com.nunofacha.chestmaster.Language;
import com.nunofacha.chestmaster.Main;
import com.nunofacha.chestmaster.Utils;
import com.nunofacha.chestmaster.Vars;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author facha
 */
public class ChestCommand {

    public static void openChest(Player p, int number) throws SQLException, IOException {
        if (Vars.BLOCK_CREATIVE_ACCESS && p.getGameMode() == GameMode.CREATIVE) {
            p.sendMessage(Language.NO_PERMISSION_CREATIVE);
            return;
        }
        if (Vars.activeChest.contains(p)) {
            p.sendMessage(Language.NO_PERMISSION);
            return;
        }
        PreparedStatement st = Main.getConnection().prepareStatement("SELECT * FROM chests WHERE uuid = ? and number = ?");
        st.setString(1, Utils.getPlayerIdentifier(p));
        st.setInt(2, number);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            String Data = rs.getString("inventory");
            Inventory iv = Utils.unserializeInventory(Data);
            Inventory to_open = Bukkit.createInventory(null, Vars.CHEST_SIZE, Vars.CHEST_NAME);
            to_open.setContents(iv.getContents());
            p.openInventory(to_open);
        } else {
            Inventory iv = Bukkit.createInventory(null, Vars.CHEST_SIZE, Vars.CHEST_NAME);
            p.openInventory(iv);
        }
        Vars.open_chests.put(Utils.getPlayerIdentifier(p), number);
        Vars.activeChest.add(p);

    }

}
