package com.oldschoolminecraft.OSMEss.Handlers;

import com.oldschoolminecraft.OSMEss.OSMEss;
import com.oldschoolminecraft.OSMEss.compat.OSMPLUserData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

import static com.oldschoolminecraft.OSMEss.Commands.CommandSeen.getPlayerTimeZone;

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


//  Get Methods (Date Format) (Locked to UTC)
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

//  Get Methods (Date Format) (TimeZone Adjusted )
    public String getLastLoginByTimeZone(Player player, CommandSender sender) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            long firstJoinMillis = data.lastLogIn;
            ZoneId zone = ZoneId.of(getPlayerTimeZone(sender));
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(firstJoinMillis), zone);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a").withZone(zone);
            return dateTime.atZone(zone).format(formatter);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return "N/A";
        }
    }
    public String getLastLogoutByTimeZone(OfflinePlayer player, CommandSender sender) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            long lastLogoutMillis = data.lastLogOut;
            ZoneId zone = ZoneId.of(getPlayerTimeZone(sender));
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastLogoutMillis), zone);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a").withZone(zone);
            return dateTime.atZone(zone).format(formatter);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return "N/A";
        }
    }
    public String getFirstJoinDateByTimeZone(OfflinePlayer player, CommandSender sender) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            long firstJoinMillis = data.firstJoin;
            ZoneId zone = ZoneId.of(getPlayerTimeZone(sender));
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
            if (ellapsedMillis < 1000) return "0 seconds";

            long totalSeconds = ellapsedMillis / 1000;
            long seconds = totalSeconds % 60;
            long minutes = (totalSeconds % 3600) / 60;
            long hours = totalSeconds / 3600;


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
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            long millis = data.playTime;

            long totalSeconds = millis / 1000;
            long years = totalSeconds / (86400 * 365);
            long remainingAfterYears = totalSeconds % (86400 * 365);
            long months = remainingAfterYears / (86400 * 30);
            long remainingAfterMonths = remainingAfterYears % (86400 * 30);
            long days = remainingAfterMonths / 86400;
            long remainingAfterDays = remainingAfterMonths % 86400;
            long hours = remainingAfterDays / 3600;
            long minutes = (remainingAfterDays % 3600) / 60;
            long seconds = remainingAfterDays % 60;

            if (minutes < 1 && seconds >= 0) return seconds + " second(s)"; //Less than 1 minute

            StringBuilder sb = new StringBuilder();

            appendUnit(sb, years, "year");
            appendUnit(sb, months, "month");
            appendUnit(sb, days, "day");

            if (years <= 0)
            {
                appendUnit(sb, hours, "hour");

                if (months <= 0)
                    appendUnit(sb, minutes, "minute");
            }

            if (sb.length() == 0 && seconds > 0)
                appendUnit(sb, seconds, "second");

            return sb.toString().trim();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return "0 minutes";
        }
    }

    public String getTotalPlaytimeLive(OfflinePlayer player) {
        try (FileReader reader = new FileReader(new File(PLAYER_DATA_DIR, player.getName().toLowerCase() + ".json"))) {
            OSMPLUserData data = OSMPLUserData.gson.fromJson(reader, OSMPLUserData.class);
            long millis = getTotalPlayTimeInMillis(player) + (System.currentTimeMillis() - data.lastLogIn);
            long totalSeconds = millis / 1000;
            long years = totalSeconds / (86400 * 365);
            long remainingAfterYears = totalSeconds % (86400 * 365);
            long months = remainingAfterYears / (86400 * 30);
            long remainingAfterMonths = remainingAfterYears % (86400 * 30);
            long days = remainingAfterMonths / 86400;
            long remainingAfterDays = remainingAfterMonths % 86400;
            long hours = remainingAfterDays / 3600;
            long minutes = (remainingAfterDays % 3600) / 60;
            long seconds = remainingAfterDays % 60;

            if (minutes < 1 && seconds >= 0) return seconds + " second(s)"; //Less than 1 minute

            StringBuilder sb = new StringBuilder();

            appendUnit(sb, years, "year");
            appendUnit(sb, months, "month");
            appendUnit(sb, days, "day");

            if (years <= 0)
            {
                appendUnit(sb, hours, "hour");
                if (months <= 0)
                    appendUnit(sb, minutes, "minute");
            }

            if (sb.length() == 0 && seconds > 0)
                appendUnit(sb, seconds, "second");
            return sb.toString().trim();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return "0 minutes";
        }
    }

    private void appendUnit(StringBuilder sb, long value, String unit)
    {
        if (value <= 0) return;
        if (sb.length() > 0) sb.append(" ");
        sb.append(value).append(" ").append(unit).append(value > 1 ? "s" : "");
    }
}
