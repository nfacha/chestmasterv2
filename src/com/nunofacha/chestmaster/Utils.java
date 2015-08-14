package com.nunofacha.chestmaster;

import com.google.gson.Gson;
import com.mysql.jdbc.log.Log;
import static com.nunofacha.chestmaster.Language.CHAT_PREFIX;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

/**
 *
 * @author facha
 */
public class Utils {

    public static Gson g = new Gson();

    public static String getPlayerIdentifier(Player p) {
        String i = "";
        if (Vars.USE_UUID) {
            i = p.getUniqueId().toString();
        } else {
            i = p.getName();
        }
        return i;
    }

    public static String getOfflinePlayerIdentifier(OfflinePlayer p) throws Exception {
        String i = "";
        if (Vars.USE_UUID) {
            i = UUIDFetcher.getUUIDOf(p.getName()).toString();
        } else {
            i = p.getName();
        }
        if (i == null) {
            return "0";
        }
        return i;
    }

    public static void createTables() throws SQLException {
        if (Vars.USE_SQL) {
            PreparedStatement st = Main.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `chests`(`id` int(11)NOT NULL AUTO_INCREMENT,`uuid` varchar(255)DEFAULT NULL,`number` int(11)DEFAULT'0',`inventory` varchar(10000)DEFAULT NULL,PRIMARY KEY(`id`));");
            st.executeUpdate();
        } else {

            PreparedStatement st = Main.getConnection().prepareStatement("CREATE TABLE `chests`(`id` int(11),`uuid` varchar(255)DEFAULT NULL,`number` int(11)DEFAULT'0',`inventory` varchar(10000)DEFAULT NULL,PRIMARY KEY(`id`));");
            st.executeUpdate();
            Main.log.info("SQLite created");

        }
    }

    public static String serializeInventory(Inventory v) {
        String response = "";
        int c = 0;
        for (ItemStack i : v.getContents()) {
            String json = itemTo64(i);
            if (i != null) {
                if (c != 0) {
                    response = response + "@" + json;
                } else {
                    response = json;
                }
                c++;
            }
        }
        if (response.endsWith("@")) {
            response = removeLastAt(response);
        }
        return response;
    }

    public static Inventory unserializeInventory(String data) throws IOException {
        Inventory v = Bukkit.createInventory(null, 9);
        String[] dataArray = data.split("@");
        for (String s : dataArray) {
            s.replace("@", "");
            ItemStack i = itemFrom64(s);
            if (i != null) {
                v.addItem(i);
            }
        }

        return v;
    }

    private static String removeLastAt(String str) {
        if (str.length() > 0 && str.charAt(str.length() - 1) == 'x') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private static String itemTo64(ItemStack stack) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(stack);

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stack.", e);
        }
    }

    private static ItemStack itemFrom64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            try {
                return (ItemStack) dataInput.readObject();
            } finally {
                dataInput.close();
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    public static boolean isInventoryEmpty(Inventory v) {
        for (ItemStack i : v.getContents()) {
            if (i != null) {
                return false;
            }
        }
        return true;
    }

    public static void readConfig() {
        if (Main.plugin.getConfig().getString("storage").equals("sqlite")) {
            Vars.USE_SQL = false;
        } else {
            if (Main.plugin.getConfig().getString("storage").equals("mysql")) {
                Vars.USE_SQL = true;
            } else {
                configError("Invalid Storage, must use 'sqlite' or 'mysql'");
                return;
            }
        }
        if (Main.plugin.getConfig().getInt("chest_size") % 9 == 0) {
            Vars.CHEST_SIZE = Main.plugin.getConfig().getInt("chest_size");
        } else {
            configError("chest_size must be divisible by 9!");
            return;
        }
        Vars.METRICS = Main.plugin.getConfig().getBoolean("networking.use_metrics");
        Vars.UPDATER = Main.plugin.getConfig().getBoolean("networking.use_autoupdate");
        Vars.USE_UUID = Main.plugin.getConfig().getBoolean("use_uuid");
        //Load Language Files
        Language.ADM_CHEST_USAGE = CHAT_PREFIX +Main.plugin.getConfig().getString("lang.ADM_CHEST_USAGE");
        Language.INVALID_CHEST_NUMBER = CHAT_PREFIX +Main.plugin.getConfig().getString("lang.INVALID_CHEST_NUMBER");
        Language.NO_PERMISSION = CHAT_PREFIX +Main.plugin.getConfig().getString("lang.NO_PERMISSION");
        Language.NO_PERMISSION_CHEST_NUMBER = CHAT_PREFIX + Main.plugin.getConfig().getString("lang.NO_PERMISSION_CHEST_NUMBER");
        Vars.CHEST_NAME = Main.plugin.getConfig().getString("lang.CHEST_NAME");
        Vars.DB_HOST = Main.plugin.getConfig().getString("mysql.hostname");
        Vars.DB_USER = Main.plugin.getConfig().getString("mysql.username");
        Vars.DB_PASS = Main.plugin.getConfig().getString("mysql.password");
        Vars.DB_NAME = Main.plugin.getConfig().getString("mysql.database");
        Vars.DB_URL = "jdbc:mysql://" + Vars.DB_HOST + ":3306/" + Vars.DB_NAME;
        Main.log.info(Language.CONSOLE_PREFIX+"Config loaded!");
    }

    public static void configError(String a) {
        Main.plugin.log.severe(Language.CONSOLE_PREFIX+"CONFIG ERROR, STOPING SERVER: " + a);
        Main.plugin.getServer().shutdown();
    }

}
