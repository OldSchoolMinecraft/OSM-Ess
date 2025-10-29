package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import com.oldschoolminecraft.vanish.Invisiman;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CommandList implements CommandExecutor {

    private final OSMEss plugin;
    private final JSONObject data;
    public static List<Player> vanished = new ArrayList<>(); //Used to get the count of vanished ppl to minus from player count for getPlayerCountVisisble().

    public CommandList(OSMEss plugin) {
        this.plugin = plugin;
        this.data = new JSONObject();
        this.plugin.getCommand("list").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("list") || cmd.getName().equalsIgnoreCase("online")) {
            StringBuilder stringBuilder = new StringBuilder();


            if (plugin.isPermissionsExEnabled()) { // Use grouping ranks if PermissionsEx exists.
                String listHeader = "§7There are §8" + Bukkit.getServer().getOnlinePlayers().length + " §7out of a maximum §8" + Bukkit.getServer().getMaxPlayers() + " §7players online.";
                stringBuilder.append(listHeader);

                ConcurrentHashMap<PermissionGroup, ArrayList<PermissionUser>> groups = new ConcurrentHashMap<>();

                for (Player all : Bukkit.getOnlinePlayers()) {
                    PermissionUser pexUser = PermissionsEx.getPermissionManager().getUser(all);
                    PermissionGroup pexGroup = pexUser.getGroups()[0];

                    groups.getOrDefault(pexGroup, new ArrayList<>()).add(pexUser);

                    if (groups.containsKey(pexGroup)) {
                        groups.get(pexGroup).add(pexUser);
                    } else {
                        ArrayList<PermissionUser> newGroupList = new ArrayList<>();
                        newGroupList.add(pexUser);
                        groups.put(pexGroup, newGroupList);
                    }
                }

                for (PermissionGroup group : groups.keySet()) {
                    stringBuilder.append("\n§7").append(group.getName()).append("§7: ");
                    int userIndex = 0;
//                    for (PermissionUser user : group.getUsers()) { Old method.
//                        userIndex++;
//
//                        stringBuilder.append("§8").append(user.getName());
//                        if (userIndex < group.getUsers().length)
//                            stringBuilder.append(ChatColor.GRAY + ", ");
//                    }

                    for (PermissionUser user : group.getUsers()) { //Replica of above method but filtering to look for only online players in their respective group.
                        for (Player all : Arrays.stream(Bukkit.getOnlinePlayers()).filter(all -> user.getName().equalsIgnoreCase(all.getName())).collect(Collectors.toList())) {
                            userIndex++;

                            stringBuilder.append("§8").append(all.getName());
                            if (group.getUsers().length > userIndex)
                                stringBuilder.append(ChatColor.GRAY + ", ");
                        }
                    }

                }

                String finalOut = stringBuilder.toString();
                finalOut = removeSuffix(finalOut.trim(), ",");

                sendMultiline(sender, finalOut);
                return true;
            }
            else { // Regular list of players. Fallback option if PermissionsEx is not installed.
                if (plugin.isInvisimanEnabled()) { // Invisiman is installed. Filter out vanished players.
                    for (Player all : Arrays.stream(Bukkit.getOnlinePlayers()).filter(all -> !Invisiman.instance.isVanished(all)).collect(Collectors.toList())) {
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(", ");
                        }

                        stringBuilder.append("§8" + all.getName()).append("§7");
                    }

                    sender.sendMessage("§7There are §8" + getOnlinePlayerCountVisible() + " §7out of a maximum §8" + Bukkit.getServer().getMaxPlayers() + " §7players online.");
                    sender.sendMessage(stringBuilder.toString());
                    return true;
                }
                else { // Invisiman is not installed. No filtering out vanished players.
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(", ");
                        }

                        stringBuilder.append("§8" + all.getName()).append("§7");
                    }

                    sender.sendMessage("§7There are §8" + Bukkit.getServer().getOnlinePlayers().length + " §7out of a maximum §8" + Bukkit.getServer().getMaxPlayers() + " §7players online.");
                    sender.sendMessage(stringBuilder.toString());
                    return true;
                }
            }
        }
        return true;
    }

    public Integer getOnlinePlayerCountVisible() { // May need additional work!
        for (Player all : Bukkit.getOnlinePlayers()) {
            if ((Invisiman.instance.isVanished(all))) {
                if (!vanished.contains(all)) {
                    vanished.add(all);
                }
                return Bukkit.getOnlinePlayers().length - vanished.size();
            }

        }
        return Bukkit.getOnlinePlayers().length;
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
