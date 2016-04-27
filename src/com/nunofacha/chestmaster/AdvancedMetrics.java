/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunofacha.chestmaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 *
 * @author Facha
 */
public class AdvancedMetrics {

    private String getURL(String target) {
        try {
            URL url = new URL(target);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0 - SquirrelUpdater");
            conn.connect();
            BufferedReader serverResponse = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String response = "";
            StringBuilder out = new StringBuilder();
            while ((line = serverResponse.readLine()) != null) {
                out.append(line);
            }
            response = out.toString().replace("<br>", "\n");
            return response;
        } catch (MalformedURLException ex) {
            System.out.println("URL Error on AdvancedMetrics");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("IO Error on AdvancedMetrics");
            ex.printStackTrace();
        }
        return "";
    }

    public void serverStart() {
        String serverIP = Main.plugin.getServer().getIp();
        int serverPort = Main.plugin.getServer().getPort();
        String plugin = "ChestMaster";
        String pluginVersion = Main.plugin.getServer().getPluginManager().getPlugin(plugin).getDescription().getVersion();
        long maxRam = Runtime.getRuntime().maxMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        int players = Main.plugin.getServer().getOnlinePlayers().size();
        int maxPlayers = Main.plugin.getServer().getMaxPlayers();
        String mcVersion = URLEncoder.encode(Main.plugin.getServer().getVersion());
        String osName = URLEncoder.encode(System.getProperty("os.name"));
        String action = "start";
        getURL("http://dev.nunofacha.com/metrics/sendMetrics.php?ip=" + serverIP + "&port=" + serverPort + "&plugin=" + plugin + "&pluginversion=" + pluginVersion + "&maxram=" + maxRam + "&freeram=" + freeMemory + "&players=" + players + "&maxplayers=" + maxPlayers + "&os=" + osName + "&action=" + action + "&mcversion=" + mcVersion);

    }

    public void serverStop() {
        String serverIP = Main.plugin.getServer().getIp();
        int serverPort = Main.plugin.getServer().getPort();
        String plugin = "ChestMaster";
        String pluginVersion = Main.plugin.getServer().getPluginManager().getPlugin(plugin).getDescription().getVersion();
        long maxRam = Runtime.getRuntime().maxMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        int players = Main.plugin.getServer().getOnlinePlayers().size();
        int maxPlayers = Main.plugin.getServer().getMaxPlayers();
        String mcVersion = URLEncoder.encode(Main.plugin.getServer().getVersion());
        String osName = URLEncoder.encode(System.getProperty("os.name"));
        String action = "stop";
        getURL("http://dev.nunofacha.com/metrics/sendMetrics.php?ip=" + serverIP + "&port=" + serverPort + "&plugin=" + plugin + "&pluginversion=" + pluginVersion + "&maxram=" + maxRam + "&freeram=" + freeMemory + "&players=" + players + "&maxplayers=" + maxPlayers + "&os=" + osName + "&action=" + action + "&mcversion=" + mcVersion);
    }

    private void sendPing() {
        String serverIP = Main.plugin.getServer().getIp();
        int serverPort = Main.plugin.getServer().getPort();
        String plugin = "ChestMaster";
        String pluginVersion = Main.plugin.getServer().getPluginManager().getPlugin(plugin).getDescription().getVersion();
        long maxRam = Runtime.getRuntime().maxMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        int players = Main.plugin.getServer().getOnlinePlayers().size();
        int maxPlayers = Main.plugin.getServer().getMaxPlayers();
        String mcVersion = URLEncoder.encode(Main.plugin.getServer().getVersion());
        String osName = URLEncoder.encode(System.getProperty("os.name"));
        String action = "ping";
        getURL("http://dev.nunofacha.com/metrics/sendMetrics.php?ip=" + serverIP + "&port=" + serverPort + "&plugin=" + plugin + "&pluginversion=" + pluginVersion + "&maxram=" + maxRam + "&freeram=" + freeMemory + "&players=" + players + "&maxplayers=" + maxPlayers + "&os=" + osName + "&action=" + action + "&mcversion=" + mcVersion);
    }

    public void registerPinger() {
        Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {

            @Override
            public void run() {
                sendPing();
            }
        }, 1200, 1200);
    }

}
