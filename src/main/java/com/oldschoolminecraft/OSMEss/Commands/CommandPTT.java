package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import com.oldschoolminecraft.OSMEss.compat.OSMPLUserData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.time.*;

public class CommandPTT implements CommandExecutor {

    private final OSMEss plugin;

    public CommandPTT(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("ptt").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("ptt")) {
            sender.sendMessage("§7Users with the top play time:");
            java.util.List<java.util.Map.Entry<String, Integer>> topPlaytimes = getTopLongestPlayTime(10);

            if (topPlaytimes.isEmpty()) {
                sender.sendMessage("§cNo playtime data available yet.");
                return true;
            }
            else {
                int rank = 1; //Ripped from LoginStreaks.
                for (java.util.Map.Entry<String, Integer> entry : topPlaytimes) {
                    String playerName = entry.getKey();
                    int longestPlaytime = entry.getValue();

                    sender.sendMessage("§8" + rank + "§7. §7" + playerName + ": §8" + formatTime(playerName, longestPlaytime));
                    rank++;
                }

                return true;
            }
        }

        return true;
    }

    public java.util.List<java.util.Map.Entry<String, Integer>> getTopLongestPlayTime(int limit) { // Ripped from LoginStreaks.
        java.util.List<java.util.Map.Entry<String, Integer>> topPlaytimes = new java.util.ArrayList<>();

        // Get all player data files
        java.io.File playerDataDir = new java.io.File(plugin.getDataFolder().getAbsolutePath(), "player-logs");
        if (!playerDataDir.exists()) {
            return topPlaytimes;
        }

        java.io.File[] playerFiles = playerDataDir.listFiles();
        if (playerFiles == null) {
            return topPlaytimes;
        }

        // Read each player's longest streak
        for (java.io.File playerFile : playerFiles) {
            if (playerFile.getName().endsWith(".json")) {
                String playerName = playerFile.getName().substring(0, playerFile.getName().length() - 5);
                int longestPlaytime = (int) plugin.playtimeHandler.getTotalPlayTimeInMillis(Bukkit.getOfflinePlayer(playerName)); //PLACE HOLDER TO STOP ERROR, REPLACE WHEN METHOD IS READY!
                if (longestPlaytime > 0) {
                    topPlaytimes.add(new java.util.AbstractMap.SimpleEntry<>(playerName, longestPlaytime));
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

    public String formatTime(String name, long timestamp) {
        long millis = plugin.playtimeHandler.getTotalPlayTimeInMillis(Bukkit.getOfflinePlayer(name));
        timestamp = plugin.playtimeHandler.getFirstJoinInMillis(Bukkit.getOfflinePlayer(name));

        if (millis < 60000) return "0 minutes"; //Less than 1 minute

        Instant startInstant = Instant.ofEpochMilli(timestamp);
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
        long hoursPart = timeDiff.toHours();
        long minutesPart = timeDiff.toMinutes();

        // readable string
        StringBuilder sb = new StringBuilder();
        if (years > 0) sb.append(years).append(" year").append(years > 1 ? "s " : " ");
        if (months > 0) sb.append(months).append(" month").append(months > 1 ? "s " : " ");
        if (days > 0) sb.append(days).append(" day").append(days > 1 ? "s " : " ");
        if (hoursPart > 0) sb.append(hoursPart).append(" hour").append(hoursPart > 1 ? "s " : " ");
        if (minutesPart > 0) sb.append(minutesPart).append(" minute").append(minutesPart > 1 ? "s " : " ");

        return sb.toString().trim();
    }
}
