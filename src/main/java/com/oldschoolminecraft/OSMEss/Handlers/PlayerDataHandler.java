package com.oldschoolminecraft.OSMEss.Handlers;

import com.oldschoolminecraft.OSMEss.OSMEss;
import com.oldschoolminecraft.OSMEss.compat.OSMPLUserData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.*;

public class PlayerDataHandler {

    public OSMEss plugin;

    public PlayerDataHandler(OSMEss plugin) {
        this.plugin = plugin;
    }


    public void createData(Player player) {

        try {
            File file = new File(plugin.getDataFolder().getAbsolutePath() + "/player-logs", player.getName().toLowerCase() + ".json");

            if (!file.exists()) {
                file.createNewFile();
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", player.getName());
            jsonObject.put("lastLogIn", System.currentTimeMillis());
            jsonObject.put("lastLogOut", 0);
            jsonObject.put("playTime", 0);
            jsonObject.put("firstJoin", System.currentTimeMillis());
            jsonObject.put("ignoreBroadcast", false);

            Writer writer = new FileWriter(file, false);
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();

            Bukkit.getServer().getLogger().info("[OSM-Ess] Created & saved data for " + player.getName() + "! (Filename: " + file.getName() + ")");
        } catch (IOException ex) {
            Bukkit.getServer().getLogger().info("[OSM-Ess] Error creating data for " + player.getName() + ": " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    public boolean hasData(OfflinePlayer player) {
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/player-logs", player.getName().toLowerCase() + ".json");

        if (file.exists()) {
            return true;
        }
        else return false;
    }

    public void updateIgnoreBroadcast(Player player, boolean option) {
        try (FileReader reader = new FileReader(new File(plugin.getDataFolder().getAbsolutePath() + "/player-logs", player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            JSONObject jsonObject = new JSONObject();

            //Retain
            long lastLoginRETAIN = data.lastLogIn;
            long lastLogoutRETAIN = data.lastLogOut;
            long totalPlayTimeRETAIN = data.playTime;
            long firstJoinRETAIN = data.firstJoin;

            //Values to calculate updates
            boolean ignoreBroadcast = option;

            //Update
            jsonObject.put("name", player.getName());
            jsonObject.put("lastLogIn", lastLoginRETAIN);
            jsonObject.put("lastLogOut", lastLogoutRETAIN);
            jsonObject.put("playTime", totalPlayTimeRETAIN);
            jsonObject.put("firstJoin", firstJoinRETAIN);
            jsonObject.put("ignoreBroadcast", ignoreBroadcast);

            //Write/Close
            FileWriter writer = new FileWriter(new File(plugin.getDataFolder().getAbsolutePath() + "/player-logs", player.getName().toLowerCase() + ".json"));
            writer.write(jsonObject.toJSONString());
            writer.close();
//            Bukkit.getServer().getLogger().info("[OSM-Ess] Saved lastLogIn data for " + player.getName() + "! (Filename: " + player.getName().toLowerCase() + ".yml)");

        } catch (IOException ex) {
            Bukkit.getServer().getLogger().info("[OSM-Ess] Error saving lastLogIn data for " + player.getName() + ": " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    public boolean hasIgnoreBroadcast(OfflinePlayer player) {
        if (hasData(player)) {
            try (FileReader reader = new FileReader(new File(plugin.getDataFolder().getAbsolutePath() + "/player-logs", player.getName().toLowerCase() + ".json"))) {
                OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
                if (data.ignoreBroadcast == true) return true;
                else return false;

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
        return false;
    }
}
