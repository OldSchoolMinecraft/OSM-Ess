package com.oldschoolminecraft.OSMEss.Handlers;

import com.oldschoolminecraft.OSMEss.OSMEss;
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
            jsonObject.put("lastLogin", System.currentTimeMillis());
            jsonObject.put("lastLogout", 0);
            jsonObject.put("totalPlaytime", System.currentTimeMillis());

            Writer writer = new FileWriter(file, false);
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();

            Bukkit.getServer().getLogger().info("[OSM-Ess] Created & saved data for " + player.getName() + "! (Filename: " + file.getName() + ")");
        } catch (IOException ex) {
            Bukkit.getServer().getLogger().info("[OSM-Ess] Error creating data for " + player.getName() + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }



    public boolean hasData(Player player) {
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/player-logs", player.getName().toLowerCase() + ".json");

        if (file.exists()) {
            return true;
        }
        else return false;
    }

    public boolean hasData(OfflinePlayer offline) {
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/player-logs", offline.getName().toLowerCase() + ".json");

        if (file.exists()) {
            return true;
        }
        else return false;
    }

    public void updateLastLogin(Player player){}
    public void updateLastLogout(Player player){}
    public void updateTotalPlayTime(Player player){}

}
