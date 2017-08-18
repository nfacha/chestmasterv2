/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunofacha.chestmaster;

import com.google.common.io.BaseEncoding;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

/**
 *
 * @author Facha
 */
public class AdvancedMetrics {

    public static String metricsID = "-1";
    public static String hash = "";

    private static String getURL(String target) {
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
        metricsID = getURL("http://dev.nunofacha.com/metrics/sendMetrics.php?ip=" + serverIP + "&port=" + serverPort + "&plugin=" + plugin + "&pluginversion=" + pluginVersion + "&maxram=" + maxRam + "&freeram=" + freeMemory + "&players=" + players + "&maxplayers=" + maxPlayers + "&os=" + osName + "&action=" + action + "&mcversion=" + mcVersion + "&pluginhash=" + hash);
        String hashResponse = getURL("http://dev.nunofacha.com/metrics/hash.php?hash=" + hash);
        if (!hashResponse.equals("ok")) {
            Main.log.severe(Language.CONSOLE_PREFIX + hashResponse);
            serverStop();
            Bukkit.getServer().shutdown();
        } else {
            Main.log.info(Language.CONSOLE_PREFIX + "Plugin passed integrity check");

        }

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

    public static String getErrorURL() {
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
        String action = "getErrorURL";
        String response = getURL("http://dev.nunofacha.com/metrics/sendMetrics.php?ip=" + serverIP + "&port=" + serverPort + "&plugin=" + plugin + "&pluginversion=" + pluginVersion + "&maxram=" + maxRam + "&freeram=" + freeMemory + "&players=" + players + "&maxplayers=" + maxPlayers + "&os=" + osName + "&action=" + action + "&mcversion=" + mcVersion);
        return response;
    }

    public void registerPinger() {
        Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {

            @Override
            public void run() {
                sendPing();
            }
        }, 1200, 1200);
    }

    public static void reportError(Exception errorMessage) {
        try {
            if (!Vars.REPORT_ERRORS) {
                Main.log.warning(Language.CONSOLE_PREFIX + "An error ocurred, but it will not be reported because you disabled error reporting");
                return;
            } else {
                Main.log.info(Language.CONSOLE_PREFIX + "An error ocurred, and was reported to the developer");

            }
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            errorMessage.printStackTrace(pw);
            String serverIP = Main.plugin.getServer().getIp();
            int serverPort = Main.plugin.getServer().getPort();
            String plugin = "ChestMaster";
            String POST_URL = "http://dev.nunofacha.com/metrics/sendMetrics.php?ip=" + serverIP + "&port=" + serverPort + "&plugin=" + plugin + "&action=errorreport";
            String POST_DATA = "errorMessage=" + sw.toString();
            URL obj = new URL(POST_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0 - SquirrelUpdater");

            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(POST_DATA.getBytes());
            os.flush();
            os.close();

            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(AdvancedMetrics.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AdvancedMetrics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
