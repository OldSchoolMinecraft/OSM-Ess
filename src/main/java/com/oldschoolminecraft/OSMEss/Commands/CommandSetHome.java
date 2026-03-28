package com.oldschoolminecraft.OSMEss.Commands;

import com.earth2me.essentials.User;
import com.oldschoolminecraft.OSMEss.OSMEss;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetHome implements CommandExecutor {

    private final OSMEss plugin;

    public CommandSetHome(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("sethome").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("sethome")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0) {
                    User user = plugin.essentials.getUser(player);


                    if (player.isOp() || player.hasPermission("essentials.sethome.multiple")) {
                        if ((user.isAuthorized("essentials.sethome.multiple.unlimited")) || (user.getHomes().size() < plugin.essentials.getSettings().getMultipleHomes()) || (user.getHomes().contains("home"))) {
                            try {
                                if (plugin.essentials.getUser(player).getHome("home") == null) {

                                    user.setHome("home", user.getLocation());
                                    player.sendMessage("§7Home §8home §7set.");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§cError: You already have a home with that name.");
                                    return true;
                                }
                            } catch (Exception ex) {
                                player.sendMessage("§cError: " + ex.getMessage());

                                Bukkit.getLogger().severe("Error whilst creating home home for " + player.getName() + "!");
                                Bukkit.getServer().getLogger().severe(ex.getMessage());
                                return true;
                            }
                        }
                        else {
                            player.sendMessage("§cError: You cannot set more than " + Integer.valueOf(plugin.essentials.getSettings().getMultipleHomes()) + " homes.");
                            return true;
                        }
                    }
                    else {
                        player.sendMessage("§cError: You cannot set more than 1 home.");
                        return true;
                    }
                }

                else if (args.length == 1) {
                    if (args[0].contains(":")) { // Contains ':' somewhere; use it to define a player and get the home name after ':'.
                        if (player.isOp() || player.hasPermission("essentials.home.others")) {

                            char seperator = ':';
                            Player other = Bukkit.getServer().getPlayerExact(StringUtils.substringBefore(args[0], seperator));

                            if (other == null) {
                                OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(StringUtils.substringBefore(args[0], seperator));

                                if (plugin.essentials.getOfflineUser(offline.getName()) == null) {
                                    player.sendMessage(plugin.errorNeverJoinedEss);
                                    return true;
                                }

                                //Offline
                                int separatorIndex = args[0].indexOf(seperator);

                                if (separatorIndex != -1) {
                                    String homeOfOffline = args[0].substring(separatorIndex + 1);

                                    try {
                                        if (plugin.essentials.getUser(offline.getName().toLowerCase()).getHome(homeOfOffline) == null) {
                                            User user = plugin.essentials.getUser(player);
                                            User user2 = plugin.essentials.getUser(offline.getName().toLowerCase());

                                            if (args[0].contains(".") ||
                                                args[0].contains("%") ||
                                                args[0].contains("+") ||
                                                args[0].contains("-") ||
                                                args[0].contains("!") ||
                                                args[0].contains("/")) {

                                                player.sendMessage("§cError: Home name cannot have a special character.");
                                                return true;
                                            }
                                            else {
                                                user2.setHome(homeOfOffline, user.getLocation());
                                                player.sendMessage("§7Home §8" + homeOfOffline + " §7set for §8" + offline.getName() + "§7.");
                                                return true;
                                            }
                                        }
                                        else {
                                            player.sendMessage("§cError: Player already has a home with that name.");
                                            return true;
                                        }

                                    } catch (Exception ex) {
                                        player.sendMessage("§cError: " + ex.getMessage());

                                        Bukkit.getLogger().severe("Error whilst creating home " + args[0] + " for " + offline.getName() + "!");
                                        Bukkit.getServer().getLogger().severe(ex.getMessage());
                                        return true;
                                    }
                                }
                                else {
                                    player.sendMessage("§c':' not found!");
                                    return true;
                                }
                            }

                            //Online
                            int separatorIndex = args[0].indexOf(seperator);

                            if (separatorIndex != -1) {
                                String homeOfOnline = args[0].substring(separatorIndex + 1);

                                try {
                                    if (plugin.essentials.getUser(other).getHome(homeOfOnline) == null) {
                                        User user = plugin.essentials.getUser(player);
                                        User user2 = plugin.essentials.getUser(other);

                                        if (args[0].contains(".") ||
                                            args[0].contains("%") ||
                                            args[0].contains("+") ||
                                            args[0].contains("-") ||
                                            args[0].contains("!") ||
                                            args[0].contains("/")) {

                                            player.sendMessage("§cError: Home name cannot have a special character.");
                                            return true;
                                        }
                                        else {
                                            user2.setHome(homeOfOnline, user.getLocation());
                                            player.sendMessage("§7Home §8" + homeOfOnline + " §7set for §8" + other.getName() + "§7.");
                                            return true;
                                        }
                                    }
                                    else {
                                        player.sendMessage("§cError: Player already has a home with that name.");
                                        return true;
                                    }
                                } catch (Exception ex) {
                                    player.sendMessage("§cError: " + ex.getMessage());

                                    Bukkit.getLogger().severe("Error whilst creating home " + args[0] + " for " + other.getName() + "!");
                                    Bukkit.getServer().getLogger().severe(ex.getMessage());
                                    return true;
                                }
                            }
                            else {
                                player.sendMessage("§cError: ':' not found!");
                                return true;
                            }
                        }
                        else { // No Permission
                            player.sendMessage("§cError: You do not have permission to view the homes of others");
                            return true;
                        }
                    }
                    else { // No ':' at the end; therefore it's the player's own home creation.
                        User user = plugin.essentials.getUser(player);

                        if (args[0].contains(".") ||
                            args[0].contains("%") ||
                            args[0].contains("+") ||
                            args[0].contains("-") ||
                            args[0].contains("!") ||
                            args[0].contains("/")) {

                            player.sendMessage("§cError: Home name cannot have a special character.");
                            return true;
                        }
                        else {
                            if (player.isOp() || player.hasPermission("essentials.sethome.multiple")) {
                                if ((user.isAuthorized("essentials.sethome.multiple.unlimited")) || (user.getHomes().size() < plugin.essentials.getSettings().getMultipleHomes()) || (user.getHomes().contains(args[0].toLowerCase()))) {
                                    try {
                                        if (plugin.essentials.getUser(player).getHome(args[0]) == null) {

                                            user.setHome(args[0], user.getLocation());
                                            player.sendMessage("§7Home §8" + args[0] + " §7set.");
                                            return true;
                                        }
                                        else {
                                            player.sendMessage("§cError: You already have a home with that name.");
                                            return true;
                                        }
                                    } catch (Exception ex) {
                                        player.sendMessage("§cError: " + ex.getMessage());

                                        Bukkit.getLogger().severe("Error whilst creating home " + args[0] + " for " + player.getName() + "!");
                                        Bukkit.getServer().getLogger().severe(ex.getMessage());
                                        return true;
                                    }
                                }
                                else {
                                    player.sendMessage("§cError: You cannot set more than " + Integer.valueOf(plugin.essentials.getSettings().getMultipleHomes()) + " homes.");
                                    return true;
                                }
                            }
                            else {
                                player.sendMessage("§cError: You cannot set more than 1 home.");
                                return true;
                            }
                        }
                    }
                }
                else {
                    if (player.isOp() || player.hasPermission("essentials.home.others")) {
                        player.sendMessage("§cUsage: /sethome [player]:<name>");
                        return true;
                    }
                    else {
                        player.sendMessage("§cUsage: /sethome <name>");
                        return true;
                    }
                }
            }
            else {
                sender.sendMessage("Command can only be executed by a player!");
                return true;
            }
        }

        return true;
    }
}
