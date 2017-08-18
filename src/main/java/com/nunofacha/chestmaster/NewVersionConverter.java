/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunofacha.chestmaster;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.nunofacha.chestmaster.Main.registerSQLitekeepAlive;

/**
 *
 * @author facha
 */
public class NewVersionConverter {

    public static void convert() {
        try {
            boolean e = registerSQLitekeepAlive();
            if (!e) {
                try {
                    Utils.createTables();
                } catch (SQLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Main.log.info(Language.CONSOLE_PREFIX + "SQLite already exists!");
            }
            Main.log.info(Language.CONSOLE_PREFIX + "Starting to migrate data to new ChestMaster 2.0 Storage System!");
            int chests = 0;
            Set<String> allPlayers = Main.plugin.getConfig().getConfigurationSection("inventarios").getKeys(false);
            Main.log.info(Language.CONSOLE_PREFIX + allPlayers.size() + " players found");
            for (String name : allPlayers) {
                Set<String> player_chests = Main.plugin.getConfig().getConfigurationSection("inventarios." + name).getKeys(false);
                for (String key : player_chests) {
                    chests = chests + player_chests.size();
                }
            }
            Main.log.info(Language.CONSOLE_PREFIX + chests + " chests found");

            Main.log.info(Language.CONSOLE_PREFIX + chests + " chests found");
            Main.log.info(Language.CONSOLE_PREFIX + "Starting migration now, this can take a LONG time depending the chests amount!");
            for (String name : allPlayers) {
                Main.log.info(Language.CONSOLE_PREFIX + "Migrating " + name + " chests");
                Set<String> key = Main.plugin.getConfig().getConfigurationSection("inventarios." + name).getKeys(false);
                Main.log.info(Language.CONSOLE_PREFIX + key.size() + " chests found for player " + name);
                for (String c : key) {
                    int chest_number = Integer.valueOf(c);
                    String chest_data = Main.plugin.getConfig().getString("inventarios." + name + "." + c);
                    Main.log.info(Language.CONSOLE_PREFIX + " Migrating chest " + chest_number);
                    Inventory v = StringToInventory(chest_data);
                    String serializedInventory = Utils.serializeInventory(v);
                    String target = Utils.getOfflinePlayerIdentifier(Bukkit.getOfflinePlayer(name));
                    if (!Utils.isInventoryEmpty(v)) {
                        PreparedStatement st2 = Main.getConnection().prepareStatement("INSERT INTO `chests` (`id`, `uuid`, `number`, `inventory`) VALUES (null, ?, ?, ?);");
                        st2.setString(1, target);
                        st2.setInt(2, chest_number);
                        st2.setString(3, serializedInventory);
                        st2.executeUpdate();
                        Main.log.info(Language.CONSOLE_PREFIX + "Migrated chest number " + chest_number + " from player " + name + " successfuly!");
                    } else {
                        Main.log.info(Language.CONSOLE_PREFIX + "Chest " + chest_number + " from player " + name + " was not migrated because its empty!");
                    }
                }
            }
            Main.log.info(Language.CONSOLE_PREFIX + "Migration Completed!");

        } catch (Exception r) {
            r.printStackTrace();
        }

    }

    private static Inventory StringToInventory(String invString) {
        String[] serializedBlocks = invString.split(";");
        String invInfo = serializedBlocks[0];
        Inventory deserializedInventory = Bukkit.getServer().createInventory(null, Integer.valueOf(invInfo));

        for (int i = 1; i < serializedBlocks.length; i++) {
            String[] serializedBlock = serializedBlocks[i].split("#");
            int stackPosition = Integer.valueOf(serializedBlock[0]);

            if (stackPosition >= deserializedInventory.getSize()) {
                continue;
            }

            ItemStack is = null;
            Boolean createdItemStack = false;

            String[] serializedItemStack = serializedBlock[1].split(":");
            for (String itemInfo : serializedItemStack) {
                String[] itemAttribute = itemInfo.split("@");
                if (itemAttribute[0].equals("t")) {
                    is = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
                    createdItemStack = true;
                } else if (itemAttribute[0].equals("d") && createdItemStack) {
                    is.setDurability(Short.valueOf(itemAttribute[1]));
                } else if (itemAttribute[0].equals("a") && createdItemStack) {
                    is.setAmount(Integer.valueOf(itemAttribute[1]));
                } else if (itemAttribute[0].equals("e") && createdItemStack) {
                    is.addUnsafeEnchantment(Enchantment.getById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]));
                }
            }
            deserializedInventory.setItem(stackPosition, is);
        }

        return deserializedInventory;
    }

}
