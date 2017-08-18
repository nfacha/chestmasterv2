/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunofacha.chestmaster.commands;

import com.nunofacha.chestmaster.Main;
import org.apache.commons.codec.binary.Hex;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.*;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Facha
 */
public class ChestHashCommand implements CommandExecutor {

    public static String getDigest(InputStream is, MessageDigest md, int byteArraySize) {

        try {
            md.reset();
            byte[] bytes = new byte[byteArraySize];
            int numBytes;
            while ((numBytes = is.read(bytes)) != -1) {
                md.update(bytes, 0, numBytes);
            }
            byte[] digest = md.digest();
            String result = new String(Hex.encodeHex(digest));
            return result;
        } catch (IOException ex) {
            Logger.getLogger(ChestHashCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Failed to get hash!";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        sender.sendMessage(getPluginHash());

        return false;

    }

    public static String getPluginHash() {
        try {
            String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            String digest = getDigest(new FileInputStream(decodedPath), md, 2048);
            return digest;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ChestHashCommand.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ChestHashCommand.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ChestHashCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Failed to get MD5!";

    }
}
