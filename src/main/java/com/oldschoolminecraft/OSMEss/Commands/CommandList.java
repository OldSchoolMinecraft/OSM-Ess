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

import java.util.ArrayList;
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

            if (plugin.isPermissionsExEnabled()) { //Use grouping ranks if PermissionsEx exists.
                Map<PermissionGroup, ArrayList<PermissionUser>> groups = new ConcurrentHashMap<>();
                List<Player> players = new ArrayList<>();

                //Todo: methods of organizing player's by ranks.


                sender.sendMessage("§7There are §8" + Bukkit.getServer().getOnlinePlayers().length + " §7out of a maximum §8" + Bukkit.getServer().getMaxPlayers() + " §7players online.");
                sender.sendMessage(stringBuilder.toString());
                return true;
            }
            else { //Regular list of players. Fallback option if PermissionsEx is not installed.
                for (Player all : Bukkit.getServer().getOnlinePlayers()) {

                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }

                    if (plugin.isInvisimanEnabled()) { // If Invisiman is installed, check to see who's invis and hide them from list.
                        if (plugin.invisiman.isVanished(all)) {
                            stringBuilder.append(false);
                        }
                        else {
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



    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
