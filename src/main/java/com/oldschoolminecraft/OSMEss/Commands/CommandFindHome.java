package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CommandFindHome implements CommandExecutor {

    private final OSMEss plugin;

    public CommandFindHome(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("findhome").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("findhome")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0 || args.length > 2) {
                    if (player.isOp() || player.hasPermission("")) {
                        player.sendMessage("§cUsage: /findhome <name> [player]");
                        return true;
                    }
                    else {
                        player.sendMessage("§cUsage: /findhome <name>");
                        return true;
                    }
                }

                if (args.length == 1) {
                    if (plugin.essentials.getUser(player).hasHome() || !plugin.essentials.getUser(player).getHomes().isEmpty()) {

                        List<String> homes = plugin.essentials.getUser(player).getHomes();

                        List<String> filteredList = homes.stream().filter(str -> str.contains(args[0])).collect(Collectors.toList());

                        if (filteredList.isEmpty()) {
                            player.sendMessage("§7Found §40 §7homes containing §8'§3" + args[0] + "§8'§7.");
                            return true;
                        }
                        else {
                            if (filteredList.size() == 1) {player.sendMessage("§7Found §2" + filteredList.size() + " §7home containing §8'§3" + args[0] + "§8'§7:");}
                            else player.sendMessage("§7Found §2" + filteredList.size() + " §7homes containing §8'§3" + args[0] + "§8'§7:");

                            for (int i = 0; i < filteredList.size(); i++) {
                                player.sendMessage("§7- §8" + filteredList.get(i) + " §7(§b/home §3" + filteredList.get(i) + "§7)");
                            }

                            return true;
                        }
                    }
                    else {
                        player.sendMessage("§cError: You don't have any homes.");
                        return true;
                    }
                }

                if (args.length == 2) {
                    if (player.isOp() || player.hasPermission("essentials.home.others")) {
                        Player other = Bukkit.getServer().getPlayer(args[1]);

                        if (other == null) {
                            OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(args[1]);

                            if (plugin.essentials.getOfflineUser(offline.getName()) == null) {
                                player.sendMessage(plugin.errorNeverJoinedEss);
                                return true;
                            }

                            if (plugin.essentials.getUser(offline.getName().toLowerCase()).hasHome() || !plugin.essentials.getUser(offline.getName().toLowerCase()).getHomes().isEmpty()) {

                                List<String> homes = plugin.essentials.getUser(offline.getName().toLowerCase()).getHomes();

                                List<String> filteredList = homes.stream().filter(str -> str.contains(args[0])).collect(Collectors.toList());

                                if (filteredList.isEmpty()) {
                                    player.sendMessage("§7Found §40 §7homes from §8" + offline.getName() + " §7containing §8'§3" + args[0] + "§8'§7.");
                                    return true;
                                }
                                else {
                                    if (filteredList.size() == 1) {player.sendMessage("§7Found §2" + filteredList.size() + " §7home from §8" + offline.getName() + " §7containing §8'§3" + args[0] + "§8'§7:");}
                                    else player.sendMessage("§7Found §2" + filteredList.size() + " §7homes from §8" + offline.getName() + " §7containing §8'§3" + args[0] + "§8'§7:");

                                    for (int i = 0; i < filteredList.size(); i++) {
                                        player.sendMessage("§7- §8" + filteredList.get(i) + " §7(§b/home §3" + offline.getName() + ":" + filteredList.get(i) + "§7)");
                                    }

                                    return true;
                                }
                            }
                            else {
                                player.sendMessage("§cError: Player doesn't have any homes.");
                                return true;
                            }
                        }

                        if (plugin.essentials.getUser(other).hasHome() || !plugin.essentials.getUser(other).getHomes().isEmpty()) {

                            List<String> homes = plugin.essentials.getUser(other).getHomes();

                            List<String> filteredList = homes.stream().filter(str -> str.contains(args[0])).collect(Collectors.toList());

                            if (filteredList.isEmpty()) {
                                player.sendMessage("§7Found §40 §7homes from §8" + other.getName() + " §7containing §8'§3" + args[0] + "§8'§7.");
                                return true;
                            }
                            else {
                                if (filteredList.size() == 1) {player.sendMessage("§7Found §2" + filteredList.size() + " §7home from §8" + other.getName() + " §7containing §8'§3" + args[0] + "§8'§7:");}
                                else player.sendMessage("§7Found §2" + filteredList.size() + " §7homes from §8" + other.getName() + " §7containing §8'§3" + args[0] + "§8'§7:");

                                for (int i = 0; i < filteredList.size(); i++) {
                                    player.sendMessage("§7- §8" + filteredList.get(i) + " §7(§b/home §3" + other.getName() + ":" + filteredList.get(i) + "§7)");
                                }

                                return true;
                            }
                        }
                        else {
                            player.sendMessage("§cError: Player doesn't have any homes.");
                            return true;
                        }
                    }
                    else { // No Permission
                        player.sendMessage("§cError: You do not have permission to view the homes of others");
                        return true;
                    }
                }

            }
            else {
                if (args.length != 2) {
                    sender.sendMessage("Usage: /findhome <name> <player>");
                    return true;
                }

                Player other = Bukkit.getServer().getPlayer(args[1]);

                if (other == null) {
                    OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(args[1]);

                    if (plugin.essentials.getOfflineUser(offline.getName()) == null) {
                        sender.sendMessage("Error: Player never logged in before. (no Essentials data)");
                        return true;
                    }

                    if (plugin.essentials.getUser(offline.getName().toLowerCase()).hasHome() || !plugin.essentials.getUser(offline.getName().toLowerCase()).getHomes().isEmpty()) {

                        List<String> homes = plugin.essentials.getUser(offline.getName().toLowerCase()).getHomes();

                        List<String> filteredList = homes.stream().filter(str -> str.contains(args[0])).collect(Collectors.toList());

                        if (filteredList.isEmpty()) {
                            sender.sendMessage("Found 0 homes from " + offline.getName() + " containing '" + args[0] + "'§7.");
                            return true;
                        }
                        else {
                            if (filteredList.size() == 1) {sender.sendMessage("Found " + filteredList.size() + " home from " + offline.getName() + " containing '" + args[0] + "':");}
                            else sender.sendMessage("Found " + filteredList.size() + " homes from " + offline.getName() + " containing '" + args[0] + "':");

                            for (int i = 0; i < filteredList.size(); i++) {
                                sender.sendMessage("- " + filteredList.get(i));
                            }

                            return true;
                        }
                    }
                    else {
                        sender.sendMessage("Error: Player doesn't have any homes.");
                        return true;
                    }
                }

                if (plugin.essentials.getUser(other).hasHome() || !plugin.essentials.getUser(other).getHomes().isEmpty()) {

                    List<String> homes = plugin.essentials.getUser(other).getHomes();

                    List<String> filteredList = homes.stream().filter(str -> str.contains(args[0])).collect(Collectors.toList());

                    if (filteredList.isEmpty()) {
                        sender.sendMessage("Found 0 homes from " + other.getName() + " containing '" + args[0] + "'§7.");
                        return true;
                    }
                    else {
                        if (filteredList.size() == 1) {sender.sendMessage("Found " + filteredList.size() + " home from " + other.getName() + " containing '" + args[0] + "':");}
                        else sender.sendMessage("Found " + filteredList.size() + " homes from " + other.getName() + " containing '" + args[0] + "':");

                        for (int i = 0; i < filteredList.size(); i++) {
                            sender.sendMessage("- " + filteredList.get(i));
                        }

                        return true;
                    }
                }
                else {
                    sender.sendMessage("Error: Player doesn't have any homes.");
                    return true;
                }
            }
        }

        return true;
    }
}
