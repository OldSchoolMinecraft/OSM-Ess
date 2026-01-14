package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandHomes implements CommandExecutor {

    private final OSMEss plugin;

    public CommandHomes(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("homes").setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("homes")) {

            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 2) {
                    if (player.isOp() || player.hasPermission("essentials.home.others")) {
                        Player other = Bukkit.getServer().getPlayer(args[1]);

                        if (other == null) {
                            // Paginate the offline player's homes to the player command sender.
                            OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(args[1]);

                            if (plugin.essentials.getOfflineUser(offline.getName()) == null) {
                                player.sendMessage(plugin.errorNeverJoinedEss);
                                return true;
                            }

                            int page;

                            try {
                                page = args.length == 0 ? 0 : Integer.valueOf(args[0]) - 1;

                                if (plugin.essentials.getUser(offline.getName().toLowerCase()).hasHome() || !plugin.essentials.getUser(offline.getName().toLowerCase()).getHomes().isEmpty()) {
                                    List<String> homes = plugin.essentials.getUser(offline.getName().toLowerCase()).getHomes();

                                    if (homes.isEmpty()) {
                                        player.sendMessage("§cError: Player doesn't have any homes.");
                                        return true;
                                    }

                                    int linesPerPage = 5;
                                    int totalPages = (int)Math.ceil((double)homes.size() / (double)linesPerPage);
                                    int startingRecord = page * linesPerPage;
                                    int titlePageNum = page + 1;

                                    if (page < 0 || page > totalPages - 1) {
                                        player.sendMessage(plugin.invalidPageNum);
                                        return true;
                                    }

                                    player.sendMessage("§8" + offline.getName().toLowerCase() + "§7's Homes (§3" + homes.size() + "§7) §7Page §8" + titlePageNum + " §7of §8" + totalPages + "§7:");

                                    for (int i = startingRecord; i < homes.size() && i < startingRecord + linesPerPage; i++) {
                                        player.sendMessage("§7- §8" + homes.get(i) + " §7(§b/home §3" + offline.getName().toLowerCase() + ":" + homes.get(i) + "§7)");
                                    }

                                    return true;
                                }
                                else {
                                    player.sendMessage("§cError: Player doesn't have any homes.");
                                    return true;
                                }

                            } catch (NumberFormatException ex) {
                                player.sendMessage(plugin.invalidNumPara);
                            }

                            return true;
                        }

                        // Paginate the online player's homes to the player command sender.
                        int page;

                        try {
                            page = args.length == 0 ? 0 : Integer.valueOf(args[0]) - 1;

                            if (plugin.essentials.getUser(other).hasHome() || !plugin.essentials.getUser(other).getHomes().isEmpty()) {
                                List<String> homes = plugin.essentials.getUser(other).getHomes();

                                if (homes.isEmpty()) {
                                    player.sendMessage("§cError: Player doesn't have any homes.");
                                    return true;
                                }

                                int linesPerPage = 5;
                                int totalPages = (int)Math.ceil((double)homes.size() / (double)linesPerPage);
                                int startingRecord = page * linesPerPage;
                                int titlePageNum = page + 1;

                                if (page < 0 || page > totalPages - 1) {
                                    player.sendMessage(plugin.invalidPageNum);
                                    return true;
                                }

                                player.sendMessage("§8" + other.getName() + "§7's Homes (§3" + homes.size() + "§7) §7Page §8" + titlePageNum + " §7of §8" + totalPages + "§7:");

                                for (int i = startingRecord; i < homes.size() && i < startingRecord + linesPerPage; i++) {
                                    player.sendMessage("§7- §8" + homes.get(i) + " §7(§b/home §3" + other.getName() + ":" + homes.get(i) + "§7)");
                                }

                                return true;
                            }
                            else {
                                player.sendMessage("§cError: Player doesn't have any homes.");
                                return true;
                            }

                        } catch (NumberFormatException ex) {
                            player.sendMessage(plugin.invalidNumPara);
                        }

                        return true;
                    }
                    else { // No Permission
                        player.sendMessage("§cError: You do not have permission to view the homes of others");
                        return true;
                    }
                }

                else if (args.length == 0 || args.length == 1) {
                    // Paginate the player's own homes.
                    int page;

                    try {
                        page = args.length == 0 ? 0 : Integer.valueOf(args[0]) - 1;

                        if (plugin.essentials.getUser(player).hasHome() || !plugin.essentials.getUser(player).getHomes().isEmpty()) {
                            List<String> homes = plugin.essentials.getUser(player).getHomes();

                            if (homes.isEmpty()) {
                                player.sendMessage("§cError: You don't have any homes.");
                                return true;
                            }

                            int linesPerPage = 5;
                            int totalPages = (int)Math.ceil((double)homes.size() / (double)linesPerPage);
                            int startingRecord = page * linesPerPage;
                            int titlePageNum = page + 1;

                            if (page < 0 || page > totalPages - 1) {
                                player.sendMessage(plugin.invalidPageNum);
                                return true;
                            }

                            player.sendMessage("§7Page §8" + titlePageNum + " §7of §8" + totalPages + "§7.");

                            for (int i = startingRecord; i < homes.size() && i < startingRecord + linesPerPage; i++) {
                                player.sendMessage("§7- §8" + homes.get(i) + " §7(§b/home §3" + homes.get(i) + "§7)");
                            }

                            return true;
                        }
                        else {
                            player.sendMessage("§cError: You don't have any homes.");
                            return true;
                        }

                    } catch (NumberFormatException ex) {
                        player.sendMessage(plugin.invalidNumPara);
                    }

                    return true;
                }
                else {
                    if (player.isOp() || player.hasPermission("essentials.home.others")) {
                        player.sendMessage("§cUsage: /homes [page] [player]");
                        return true;
                    }
                    else {
                        player.sendMessage("§cUsage: /homes [page]");
                        return true;
                    }
                }
            }
            else {
                if (args.length != 2) {
                    sender.sendMessage("Usage: /homes <page> <player>");
                    return true;
                }

                Player other = Bukkit.getServer().getPlayer(args[1]);

                if (other == null) {
                    OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(args[1]);

                    if (plugin.essentials.getOfflineUser(offline.getName()) == null) {
                        sender.sendMessage("Error: Player never logged in before. (no Essentials data)");
                        return true;
                    }

                    // Paginate the offline player's homes to the CONSOLE.
                    int page;

                    try {
                        page = args.length == 0 ? 0 : Integer.valueOf(args[0]) - 1;

                        if (plugin.essentials.getUser(offline.getName().toLowerCase()).hasHome() || !plugin.essentials.getUser(offline.getName().toLowerCase()).getHomes().isEmpty()) {
                            List<String> homes = plugin.essentials.getUser(offline.getName().toLowerCase()).getHomes();

                            if (homes.isEmpty()) {
                                sender.sendMessage("Error: Player doesn't have any homes.");
                                return true;
                            }

                            int linesPerPage = 5;
                            int totalPages = (int)Math.ceil((double)homes.size() / (double)linesPerPage);
                            int startingRecord = page * linesPerPage;
                            int titlePageNum = page + 1;

                            if (page < 0 || page > totalPages - 1) {
                                sender.sendMessage("Error: Invalid page number provided.");
                                return true;
                            }

                            sender.sendMessage(offline.getName().toLowerCase() + "'s Homes (" + homes.size() + ") Page " + titlePageNum + " of " + totalPages + ":");

                            for (int i = startingRecord; i < homes.size() && i < startingRecord + linesPerPage; i++) {
                                sender.sendMessage("- " + homes.get(i));
                            }

                            return true;
                        }
                        else {
                            sender.sendMessage("Error: Player doesn't have any homes.");
                            return true;
                        }

                    } catch (NumberFormatException ex) {
                        sender.sendMessage("Error: Invalid integer provided.");
                    }

                    return true;
                }

                // Paginate the online player's homes to the CONSOLE.
                int page;

                try {
                    page = args.length == 0 ? 0 : Integer.valueOf(args[0]) - 1;

                    if (plugin.essentials.getUser(other).hasHome() || !plugin.essentials.getUser(other).getHomes().isEmpty()) {
                        List<String> homes = plugin.essentials.getUser(other).getHomes();

                        if (homes.isEmpty()) {
                            sender.sendMessage("Error: Player doesn't have any homes.");
                            return true;
                        }

                        int linesPerPage = 5;
                        int totalPages = (int)Math.ceil((double)homes.size() / (double)linesPerPage);
                        int startingRecord = page * linesPerPage;
                        int titlePageNum = page + 1;

                        if (page < 0 || page > totalPages - 1) {
                            sender.sendMessage("Error: Invalid page number provided.");
                            return true;
                        }

                        sender.sendMessage(other.getName() + "'s Homes (" + homes.size() + ") Page " + titlePageNum + " of " + totalPages + ":");

                        for (int i = startingRecord; i < homes.size() && i < startingRecord + linesPerPage; i++) {
                            sender.sendMessage("- " + homes.get(i));
                        }

                        return true;
                    }
                    else {
                        sender.sendMessage("Error: Player doesn't have any homes.");
                        return true;
                    }

                } catch (NumberFormatException ex) {
                    sender.sendMessage("Error: Invalid integer provided.");
                }

                return true;
            }
        }

        return true;
    }
}
