package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class CommandList implements CommandExecutor {

    private final OSMEss plugin;
    private final JSONObject data;

    public CommandList(OSMEss plugin) {
        this.plugin = plugin;
        this.data = new JSONObject();
        this.plugin.getCommand("list").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("list") || cmd.getName().equalsIgnoreCase("online")) {
            StringBuilder stringBuilder = new StringBuilder();

            ConcurrentHashMap<PermissionGroup, ArrayList<PermissionUser>> groups = new ConcurrentHashMap<>();

            if (plugin.isPermissionsExEnabled()) { //Use grouping ranks if PermissionsEx exists.
                for (Player onlinePlayer : Bukkit.getOnlinePlayers())
                {
                    PermissionUser pexUser = PermissionsEx.getPermissionManager().getUser(onlinePlayer);
                    PermissionGroup pexGroup = pexUser.getGroups()[0];

                    groups.get(pexGroup).add(pexUser);
                }

                String listHeader = "§7There are §8" + Bukkit.getServer().getOnlinePlayers().length + " §7out of a maximum §8" + Bukkit.getServer().getMaxPlayers() + " §7players online.";
                stringBuilder.append(listHeader);

                for (PermissionGroup group : groups.keySet())
                {
                    stringBuilder.append("\n§7").append(group.getName()).append("§7: ");
                    int userIndex = 0;
                    for (PermissionUser user : group.getUsers())
                    {
                        userIndex++;
                        stringBuilder.append("§8").append(user.getName());
                        if (userIndex < group.getUsers().length)
                            stringBuilder.append("§7, ");
                    }
                }

                String finalOut = stringBuilder.toString();
                finalOut = removePrefix(finalOut, "\n");
                finalOut = removeSuffix(finalOut, "\n");

                sendMultiline(sender, finalOut);
                return true;
            }
            else { //Regular list of players. Fallback option if PermissionsEx is not installed.
                for (Player all : Bukkit.getServer().getOnlinePlayers()) {

                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }

                    if (plugin.isInvisimanEnabled()) { // If Invisiman is installed, check to see who's invis and hide them from list.
                        if (!plugin.invisiman.isVanished(all)) {
                            stringBuilder.append("§7" + all.getName()).append("§8");
                        }
                    }
                    else { // Invisiman is not installed, show the regular list regardless who's vanished.
                        stringBuilder.append("§7" + all.getName()).append("§8");
                    }
                }

                sender.sendMessage("§7There are §8" + Bukkit.getServer().getOnlinePlayers().length + " §7out of a maximum §8" + Bukkit.getServer().getMaxPlayers() + " §7players online.");
                sender.sendMessage(stringBuilder.toString());
                return true;
            }
        }
        return true;
    }

    public static String removePrefix(String str, String prefix) {
        if (str == null || prefix == null) return str;
        return str.startsWith(prefix) ? str.substring(prefix.length()) : str;
    }

    public static String removeSuffix(String str, String suffix) {
        if (str == null || suffix == null) return str;
        return str.endsWith(suffix) ? str.substring(0, str.length() - suffix.length()) : str;
    }

    private static void sendMultiline(CommandSender sender, String message) {
        String[] lines = message.split("\n");
        for (String line : lines)
            sender.sendMessage(line);
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

