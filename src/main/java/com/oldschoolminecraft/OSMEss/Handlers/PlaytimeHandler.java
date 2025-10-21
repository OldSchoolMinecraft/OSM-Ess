package com.oldschoolminecraft.OSMEss.Handlers;

import com.oldschoolminecraft.OSMEss.OSMEss;
import com.oldschoolminecraft.OSMEss.compat.OSMPLUserData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class PlaytimeHandler {

    public OSMEss plugin;
    public static File PLAYER_DATA_DIR;

    public PlaytimeHandler(OSMEss plugin) {
        this.plugin = plugin;
        PLAYER_DATA_DIR = new File(plugin.getDataFolder().getAbsolutePath(), "player-logs");
    }


//  Update Methods
    public void updateLastLogin(Player player) { //Update this when player logs in.
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            JSONObject jsonObject = new JSONObject();

            //Retain
            long lastLogoutRETAIN = data.lastLogOut;
            long totalPlayTimeRETAIN = data.playTime;
            long firstJoinRETAIN = data.firstJoin;
            boolean ignoreBroadcastRETAIN = data.ignoreBroadcast;

            //Values to calculate updates
            data.lastLogIn = System.currentTimeMillis();

            //Update
            jsonObject.put("name", player.getName());
            jsonObject.put("lastLogIn", data.lastLogIn);
            jsonObject.put("lastLogOut", lastLogoutRETAIN);
            jsonObject.put("playTime", totalPlayTimeRETAIN);
            jsonObject.put("firstJoin", firstJoinRETAIN);
            jsonObject.put("ignoreBroadcast", ignoreBroadcastRETAIN);

            //Write/Close
            FileWriter writer = new FileWriter(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"));
            writer.write(jsonObject.toJSONString());
            writer.close();
//            Bukkit.getServer().getLogger().info("[OSM-Ess] Saved lastLogIn data for " + player.getName() + "! (Filename: " + player.getName().toLowerCase() + ".yml)");

        } catch (IOException ex) {
            Bukkit.getServer().getLogger().info("[OSM-Ess] Error saving lastLogIn data for " + player.getName() + ": " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }
    public void updateTotalPlaytime(Player player) { //Update this when player logs out.

        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            JSONObject jsonObject = new JSONObject();

            //Retain
            long lastLogInRETAIN = data.lastLogIn;
            long firstJoinRETAIN = data.firstJoin;
            boolean ignoreBroadcastRETAIN = data.ignoreBroadcast;
            long playTimeOLD = data.playTime;

            //Values to calculate updates
            data.lastLogOut = System.currentTimeMillis();
            long diff = data.lastLogOut - lastLogInRETAIN;
            long playTimeNEW = playTimeOLD + diff;

            //Update
            jsonObject.put("name", player.getName());
            jsonObject.put("lastLogIn", lastLogInRETAIN);
            jsonObject.put("lastLogOut", data.lastLogOut);
            jsonObject.put("playTime", playTimeNEW);
            jsonObject.put("firstJoin", firstJoinRETAIN);
            jsonObject.put("ignoreBroadcast", ignoreBroadcastRETAIN);

            //Write/Close
            FileWriter writer = new FileWriter(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"));
            writer.write(jsonObject.toJSONString());
            writer.close();
//            Bukkit.getServer().getLogger().info("[OSM-Ess] Saved playTime data for " + player.getName() + "! (Filename: " + player.getName().toLowerCase() + ".yml)");

        } catch (IOException ex) {
            Bukkit.getServer().getLogger().info("[OSM-Ess] Error saving playTime data for " + player.getName() + ": " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }


//  Get Methods (Date Format)
    public String getLastLogin(Player player) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            long firstJoinMillis = data.lastLogIn;
            ZoneId zone = ZoneOffset.UTC;
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(firstJoinMillis), zone);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a").withZone(zone);
            return dateTime.atZone(zone).format(formatter);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return "N/A";
        }
    }
    public String getLastLogout(OfflinePlayer player) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            long lastLogoutMillis = data.lastLogOut;
            ZoneId zone = ZoneOffset.UTC;
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastLogoutMillis), zone);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a").withZone(zone);
            return dateTime.atZone(zone).format(formatter);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return "N/A";
        }
    }
    public String getFirstJoinDate(OfflinePlayer player) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
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


//  Get Methods (currentTimeMillis Format)
    public long getTotalPlayTimeInMillis(OfflinePlayer player) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            return data.playTime;

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        return 0;
    }

    public long getFirstJoinInMillis(OfflinePlayer player) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            return data.firstJoin;

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        return 0;
    }

//  Get Methods (Regular Time Format)
    public String getPlayTimeInSession(Player player) { //Partially Passed. Unknown result passed 1 hour.
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            long lastLoginMillis = data.lastLogIn;
            long liveMillis = System.currentTimeMillis();
            long ellapsedMillis = liveMillis - lastLoginMillis;
            if (ellapsedMillis <= 0) return "0 seconds";

            long totalSeconds = ellapsedMillis / 1000;
            long seconds = totalSeconds % 60;
            long minutes = totalSeconds / 60;
            long hours = totalSeconds / 3600;
            long days = hours / 24; //Unlikely if the server restarts every 12 hours.

            // readable string
            StringBuilder sb = new StringBuilder();
            if (hours > 0) sb.append(hours).append(" hour").append(hours > 1 ? "s " : " ");
            if (minutes > 0) sb.append(minutes).append(" minute").append(minutes > 1 ? "s " : " ");
            if (seconds > 0) sb.append(seconds).append(" second").append(seconds > 1 ? "s " : " ");

            return sb.toString().trim();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return "N/A";
        }

    }

    public String getTotalPlaytime(OfflinePlayer player) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            long millis = data.playTime;
            long firstJoinMillis = data.firstJoin;

            if (millis < 60000) return "0 minutes"; //Less than 1 minute

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
            long hoursPart = timeDiff.toHours();
            long minutesPart = timeDiff.toMinutes();

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
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return "N/A";
        }
    }
}
