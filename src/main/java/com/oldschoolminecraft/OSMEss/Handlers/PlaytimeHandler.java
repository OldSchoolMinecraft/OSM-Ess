package com.oldschoolminecraft.OSMEss.Handlers;

import com.google.gson.Gson;
import com.oldschoolminecraft.OSMEss.OSMEss;
import com.oldschoolminecraft.OSMEss.compat.OSMPLUserData;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class PlaytimeHandler {
    private static Gson gson = new Gson();

    public OSMEss plugin;
    private static File PLAYER_DATA_DIR;

    public PlaytimeHandler(OSMEss plugin) {
        this.plugin = plugin;
        PLAYER_DATA_DIR = new File(plugin.getDataFolder().getAbsolutePath(), "playtime-log");
    }

//  METHODS ARE MOSTLY PLACEHOLDERS ATM AND ALSO MEANT TO GET RID OF ERRORS.


    public void setTotalPlaytime(Player player, long totalPlaytime) { //Update this when the player logs out.

    }

    public String getTotalPlaytime(OfflinePlayer player) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName() + ".json"))) {
            OSMPLUserData data = gson.fromJson(reader, OSMPLUserData.class);
            long millis = data.playTime;
            long firstJoinMillis = data.firstJoin;

            if (millis <= 0) return "0 minutes";

            Instant startInstant = Instant.ofEpochMilli(firstJoinMillis);
            Instant endInstant = startInstant.plusMillis(millis);

            ZoneId zone = ZoneOffset.UTC;
            LocalDateTime start = LocalDateTime.ofInstant(startInstant, zone);
            LocalDateTime end = LocalDateTime.ofInstant(endInstant, zone);

            // calculate the calendar period (years, months, days)
            Period dateDiff = Period.between(start.toLocalDate(), end.toLocalDate());

            // calculate the remaining time-of-day difference (hours, minutes, seconds)
            LocalDateTime intermediate = start.plusYears(dateDiff.getYears()).plusMonths(dateDiff.getMonths()).plusDays(dateDiff.getDays());
            Duration timeDiff = Duration.between(intermediate, end);

            long years = dateDiff.getYears();
            long months = dateDiff.getMonths();
            long days = dateDiff.getDays();
            long hours = timeDiff.toHoursPart();
            long minutes = timeDiff.toMinutesPart();

            // readable string
            StringBuilder sb = new StringBuilder();
            if (years > 0) sb.append(years).append(" year").append(years > 1 ? "s " : " ");
            if (months > 0) sb.append(months).append(" month").append(months > 1 ? "s " : " ");
            if (days > 0) sb.append(days).append(" day").append(days > 1 ? "s " : " ");
            if (hours > 0) sb.append(hours).append(" hour").append(hours > 1 ? "s " : " ");
            if (minutes > 0) sb.append(minutes).append(" minute").append(minutes > 1 ? "s " : " ");

            return sb.toString().trim();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return "N/A";
        }
    }

    public String getFirstJoinDate(OfflinePlayer player) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName() + ".json")))
        {
            OSMPLUserData data = gson.fromJson(reader, OSMPLUserData.class);
            long firstJoinMillis = data.firstJoin;
            ZoneId zone = ZoneOffset.UTC;
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(firstJoinMillis), zone);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a").withZone(zone);
            return dateTime.atZone(zone).format(formatter);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return "N/A";
        }
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
