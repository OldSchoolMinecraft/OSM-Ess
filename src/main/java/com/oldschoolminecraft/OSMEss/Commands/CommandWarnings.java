package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandWarnings implements CommandExecutor {

    private final OSMEss plugin;

    public CommandWarnings(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("warnings").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("warnings")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.isOp() || player.hasPermission("osmess.warnings")) {
                    if (args.length == 0 || args.length > 2) {
                        player.sendMessage("§cUsage: /warnings <player> [clear]");
                        return true;
                    }
                    if (args.length == 1) {
                        Player other = Bukkit.getServer().getPlayerExact(args[0]);

                        if (other == null) {
                            OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(args[0]);

                            if (plugin.isPlayerInWarningLogs(offline)) { //Their records exist & have been warned before.
                                player.sendMessage("§8" + offline.getName() + "§7's Warnings:");

                                List<String> warnings = plugin.warningsCFG.getStringList("Players." + offline.getName().toLowerCase() + ".Warnings",  new ArrayList<>());

                                for (String warning : warnings) {
                                    player.sendMessage("§8- §7" + warning);
                                }
                                return true;
                            }
                            else { //Their records don't exist. Never been warned before or have had their records cleared.
                                player.sendMessage("§cError: Player doesn't have any warnings.");
                                return true;
                            }
                        }
                        if (plugin.isPlayerInWarningLogs(other)) { //Their records exist & have been warned before.
                            player.sendMessage("§8" + other.getName() + "§7's Warnings:");

                            List<String> warnings = plugin.warningsCFG.getStringList("Players." + other.getName().toLowerCase() + ".Warnings",  new ArrayList<>());

                            for (String warning : warnings) {
                                player.sendMessage("§8- §7" + warning);
                            }
                            return true;
                        }
                        else { //Their records don't exist. Never been warned before or have had their records cleared.
                            player.sendMessage("§cError: Player doesn't have any warnings.");
                            return true;
                        }
                    }
                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("clear")) {
                            Player other = Bukkit.getServer().getPlayerExact(args[0].toLowerCase());

                            if (other == null) {
                                OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(args[0].toLowerCase());

                                if (plugin.isPlayerInWarningLogs(offline)) { //Their records exist & have been warned before.
                                    plugin.clearWarnings(offline);
                                    player.sendMessage("§8" + offline.getName() + "§7's warnings have been cleared!");
                                    return true;
                                }
                                else { //Their records don't exist. Never been warned before or have had their records cleared.
                                    player.sendMessage("§cError: Player doesn't have any warnings.");
                                    return true;
                                }
                            }
                            if (plugin.isPlayerInWarningLogs(other)) { //Their records exist & have been warned before.
                                plugin.clearWarnings(other);
                                player.sendMessage("§8" + other.getName() + "§7's warnings have been cleared!");
                                return true;
                            }
                            else { //Their records don't exist. Never been warned before or have had their records cleared.
                                player.sendMessage("§cError: Player doesn't have any warnings.");
                                return true;
                            }
                        }
                        else {
                            player.sendMessage("§cUsage: /warnings <player> [clear]");
                            return true;
                        }
                    }
                }
                else {
                    player.sendMessage(plugin.noPermission);
                    return true;
                }
            }
            else {
                if (args.length == 0 || args.length > 2) {
                    sender.sendMessage("Usage: /warnings <player> [clear]");
                    return true;
                }
                if (args.length == 1) {
                    //Todo: Check if player exists in the warning logs and list all their warnings.
                    Player other = Bukkit.getServer().getPlayerExact(args[0]);

                    if (other == null) {
                        OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(args[0]);

                        if (plugin.isPlayerInWarningLogs(offline)) { //Their records exist & have been warned before.
                            sender.sendMessage(offline.getName() + "'s Warnings:");

                            List<String> warnings = plugin.warningsCFG.getStringList("Players." + offline.getName().toLowerCase() + ".Warnings",  new ArrayList<>());

                            for (String warning : warnings) {
                                sender.sendMessage("- " + warning);
                            }
                            return true;
                        }
                        else { //Their records don't exist. Never been warned before or have had their records cleared.
                            sender.sendMessage("Error: Player doesn't have any warnings.");
                            return true;
                        }
                    }
                    if (plugin.isPlayerInWarningLogs(other)) { //Their records exist & have been warned before.
                        sender.sendMessage(other.getName() + "'s Warnings:");

                        List<String> warnings = plugin.warningsCFG.getStringList("Players." + other.getName().toLowerCase() + ".Warnings",  new ArrayList<>());

                        for (String warning : warnings) {
                            sender.sendMessage("- " + warning);
                        }
                        return true;
                    }
                    else { //Their records don't exist. Never been warned before or have had their records cleared.
                        sender.sendMessage("Error: Player doesn't have any warnings.");
                        return true;
                    }
                }
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("clear")) {
                        //Todo: Check if player exits in warnings log and clear/remove them from the logs.
                        Player other = Bukkit.getServer().getPlayerExact(args[0].toLowerCase());

                        if (other == null) {
                            OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(args[0].toLowerCase());

                            if (plugin.isPlayerInWarningLogs(offline)) { //Their records exist & have been warned before.
                                plugin.clearWarnings(offline);
                                sender.sendMessage(offline.getName() + "'s warnings have been cleared!");
                                return true;
                            }
                            else { //Their records don't exist. Never been warned before or have had their records cleared.
                                sender.sendMessage("Error: Player doesn't have any warnings.");
                                return true;
                            }
                        }
                        if (plugin.isPlayerInWarningLogs(other)) { //Their records exist & have been warned before.
                            plugin.clearWarnings(other);
                            sender.sendMessage( other.getName() + "'s warnings have been cleared!");
                            return true;
                        }
                        else { //Their records don't exist. Never been warned before or have had their records cleared.
                            sender.sendMessage("Error: Player doesn't have any warnings.");
                            return true;
                        }
                    }
                    else {
                        sender.sendMessage("Usage: /warnings <player> [clear]");
                        return true;
                    }
                }
            }
        }
        return true;
    }
}



