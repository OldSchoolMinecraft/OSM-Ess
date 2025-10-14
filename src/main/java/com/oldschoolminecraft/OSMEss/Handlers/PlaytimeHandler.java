package com.oldschoolminecraft.OSMEss.Handlers;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;

public class PlaytimeHandler {

    public OSMEss plugin;

    public PlaytimeHandler(OSMEss plugin) {
        this.plugin = plugin;
    }

//  METHODS ARE MOSTLY PLACEHOLDERS ATM AND ALSO MEANT TO GET RID OF ERRORS.


    public void setTotalPlaytime(Player player, long totalPlaytime) { //Update this when the player logs out.

    }

    public Calendar getTotalPlaytime(Player player) {
        return null;
    }

    public Calendar getTotalPlaytime(OfflinePlayer player) {
        return null;
    }

    public Calendar getFirstJoinDate(Player player) {
        Date date = new Date();


        return null;
    }

    public Calendar getFirstJoinDate(OfflinePlayer player) {
        return null;
    }

    public java.util.List<java.util.Map.Entry<String, Integer>> getTopLongestPlayTime(int limit) { // Ripped from LoginStreaks.
        java.util.List<java.util.Map.Entry<String, Integer>> topPlaytimes = new java.util.ArrayList<>();

        // Get all player data files
        java.io.File playerDataDir = new java.io.File(plugin.getDataFolder().getAbsolutePath(), "playtime-log");
        if (!playerDataDir.exists()) {
            return topPlaytimes;
        }

        java.io.File[] playerFiles = playerDataDir.listFiles();
        if (playerFiles == null) {
            return topPlaytimes;
        }

        // Read each player's longest streak
        for (java.io.File playerFile : playerFiles) {
            if (playerFile.getName().endsWith(".yml")) {
                String playerName = playerFile.getName().substring(0, playerFile.getName().length() - 4);
                int longestStreak = 1; //Place holder to stop errors, change when methods are in place.
                if (longestStreak > 0) {
                    topPlaytimes.add(new java.util.AbstractMap.SimpleEntry<>(playerName, longestStreak));
                }
            }
        }

        // Sort by longest streak descending
        java.util.Collections.sort(topPlaytimes, new java.util.Comparator<java.util.Map.Entry<String, Integer>>() {
            public int compare(java.util.Map.Entry<String, Integer> a, java.util.Map.Entry<String, Integer> b) {
                return b.getValue().compareTo(a.getValue());
            }
        });

        // Return top N results
        if (topPlaytimes.size() > limit) {
            return topPlaytimes.subList(0, limit);
        }
        return topPlaytimes;
    }
}
