package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
            if (plugin.isScheduledDeathEnabled()) {
                if (plugin.scheduledDeath.getTimeToLive() <= 30) {
                    sender.sendMessage(plugin.cmdDisabledRestart);
                    return true;
                }
            }

            sender.sendMessage("§7Users with the top play time:");
            java.util.List<java.util.Map.Entry<String, Long>> topPlaytimes = getTopPlaytimes(10);

            if (topPlaytimes.isEmpty()) {
                sender.sendMessage("§cNo playtime data available yet.");
                return true;
            }
            else {
                int rank = 1; //Ripped from LoginStreaks.
                for (java.util.Map.Entry<String, Long> entry : topPlaytimes) {
                    String playerName = entry.getKey();
                    long longestPlaytime = entry.getValue();

                    sender.sendMessage("§8" + rank + "§7. §7" + playerName + ": §8" + formatTime(playerName, longestPlaytime));
                    rank++;
                }

                return true;
            }
        }

        return true;
    }

    public java.util.List<java.util.Map.Entry<String, Long>> getTopPlaytimes(int limit) {
        // Return from cache instead of reading files
        if (plugin.cachedTopPlaytimes.size() > limit) {
            return plugin.cachedTopPlaytimes.subList(0, limit);
        }
        return plugin.cachedTopPlaytimes;
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
        long hoursPart = timeDiff.toHours() % 24;
        long minutesPart = timeDiff.toMinutes() % 60;
        
        StringBuilder sb = new StringBuilder();

        if (millis >= 86400000) { // 1 Day
            if (years > 0) sb.append(years).append(" year").append(years > 1 ? "s " : " ");
            if (months > 0) sb.append(months).append(" month").append(months > 1 ? "s " : " ");
            if (days > 0) sb.append(days).append(" day").append(days > 1 ? "s " : " ");
        }
        else {
            if (years > 0) sb.append(years).append(" year").append(years > 1 ? "s " : " ");
            if (months > 0) sb.append(months).append(" month").append(months > 1 ? "s " : " ");
            if (days > 0) sb.append(days).append(" day").append(days > 1 ? "s " : " ");
            if (hoursPart > 0) sb.append(hoursPart).append(" hour").append(hoursPart > 1 ? "s " : " ");
            if (minutesPart > 0) sb.append(minutesPart).append(" minute").append(minutesPart > 1 ? "s " : " ");
        }

        return sb.toString().trim();
    }
}

