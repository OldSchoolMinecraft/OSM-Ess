package com.oldschoolminecraft.OSMEss.Handlers;

import com.oldschoolminecraft.OSMEss.OSMEss;
import com.oldschoolminecraft.OSMEss.UnixTime;
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
    private static File PLAYER_DATA_DIR;

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
            long lastLogoutRETAIN = data.lastLogout;
            long totalPlayTimeRETAIN = data.totalPlaytime;
            long firstJoinRETAIN = data.firstJoin;

            //Values to calculate updates
            long lastLogin = System.currentTimeMillis();

            //Update
            jsonObject.put("name", player.getName());
            jsonObject.put("lastLogin", lastLogin);
            jsonObject.put("lastLogout", lastLogoutRETAIN);
            jsonObject.put("totalPlaytime", totalPlayTimeRETAIN);
            jsonObject.put("firstJoin", firstJoinRETAIN);

            //Write/Close
            FileWriter writer = new FileWriter(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"));
            writer.write(jsonObject.toJSONString());
            writer.close();
            Bukkit.getServer().getLogger().info("[OSM-Ess] Saved lastLogin data for " + player.getName() + "! (Filename: " + player.getName().toLowerCase() + ")");

        } catch (IOException ex) {
            Bukkit.getServer().getLogger().info("[OSM-Ess] Error saving lastLogin data for " + player.getName() + ": " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }
    public void updateTotalPlaytime(Player player) { //Update this when player logs out.

        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            JSONObject jsonObject = new JSONObject();

            //Retain
            long lastLoginRETAIN = data.lastLogin;
            long firstJoinRETAIN = data.firstJoin;
            long totalOldPlaytime = data.totalPlaytime;

            //Values to calculate updates
            long lastLogout = System.currentTimeMillis();
            long diff = lastLogout - lastLoginRETAIN;
            long totalNewPlaytime = totalOldPlaytime + diff;

            //Update
            jsonObject.put("name", player.getName());
            jsonObject.put("lastLogin", lastLoginRETAIN);
            jsonObject.put("lastLogout", lastLogout);
            jsonObject.put("totalPlaytime", totalNewPlaytime);
            jsonObject.put("firstJoin", firstJoinRETAIN);

            //Write/Close
            FileWriter writer = new FileWriter(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"));
            writer.write(jsonObject.toJSONString());
            writer.close();
            Bukkit.getServer().getLogger().info("[OSM-Ess] Saved totalPlayTime data for " + player.getName() + "! (Filename: " + player.getName().toLowerCase() + ")");

        } catch (IOException ex) {
            Bukkit.getServer().getLogger().info("[OSM-Ess] Error saving totalPlayTime data for " + player.getName() + ": " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }


//  Get Methods (Date Format)
    public String getLastLogin(Player player) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            long firstJoinMillis = data.lastLogin;
            ZoneId zone = ZoneOffset.UTC;
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(firstJoinMillis), zone);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a").withZone(zone);
            return dateTime.atZone(zone).format(formatter);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return "N/A";
        }
    }
    public String getLastLogout(Player player) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            long firstJoinMillis = data.lastLogout;
            ZoneId zone = ZoneOffset.UTC;
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(firstJoinMillis), zone);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a").withZone(zone);
            return dateTime.atZone(zone).format(formatter);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return "N/A";
        }
    }
    public String getTotalPlaytime(OfflinePlayer player) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            long millis = data.totalPlaytime;
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
            long hours = timeDiff.toHours();
            long minutes = timeDiff.toMinutes();

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
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json")))
        {
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
    public long getLastLoginInMillis(Player player) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            return data.lastLogin;

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        return 0;
    }
    public long getLastLogoutInMillis(Player player) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            return data.lastLogout;

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        return 0;
    }
    public long getPlayTimeInMillis(Player player) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            return data.totalPlaytime;

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        return 0;
    }


//  Get Methods (Regular Time Format)
    public String getPlayTimeInSession(Player player) { //Needs more work, not correct yet.
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            long lastLoginMillis = data.lastLogin;

            if (lastLoginMillis <= 0) return "0 minutes";

            Instant startInstant = Instant.ofEpochMilli(lastLoginMillis);
            Instant endInstant = startInstant.plusMillis(UnixTime.now());

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
            long hours = timeDiff.toHours();
            long minutes = timeDiff.toMinutes();

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
}
