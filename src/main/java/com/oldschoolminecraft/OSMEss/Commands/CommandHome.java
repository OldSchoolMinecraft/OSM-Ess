package com.oldschoolminecraft.OSMEss.Commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.oldschoolminecraft.OSMEss.OSMEss;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandHome implements CommandExecutor {

    private final OSMEss plugin;

    public CommandHome(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("home").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("home")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0) {
                    // Show the player's own homes.
                    if (plugin.essentials.getUser(player).hasHome() || !plugin.essentials.getUser(player).getHomes().isEmpty()) {
                        List<String> homes = plugin.essentials.getUser(player).getHomes();
                        StringBuilder stringBuilder = new StringBuilder();

                        for (String home : homes) {
                            if (stringBuilder.length() > 0) {
                                stringBuilder.append(", ");
                            }

                            stringBuilder.append("§8" + home + "§7");
                        }

                        player.sendMessage("§7Homes (§3" + homes.size() + "§7): §8" + stringBuilder.toString());
                        return true;
                    }
                    else {
                        player.sendMessage("§cError: You don't have any homes.");
                        return true;
                    }
                }

                if (args.length == 1) {
                    if (args[0].endsWith(":")) { // Nothing after ':'; view a specific player's homes.
                        if (player.isOp() || player.hasPermission("essentials.home.others")) {
                            Player other = Bukkit.getServer().getPlayer(args[0].substring(0, args[0].length() - 1));

                            if (other == null) {
                                OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(args[0].substring(0, args[0].length() - 1));

                                if (plugin.essentials.getOfflineUser(offline.getName()) == null) {
                                    player.sendMessage(plugin.errorNeverJoinedEss);
                                    return true;
                                }

                                if (plugin.essentials.getUser(offline.getName().toLowerCase()).hasHome() || !plugin.essentials.getUser(offline.getName().toLowerCase()).getHomes().isEmpty()) {
                                    List<String> homesOffline = plugin.essentials.getUser(offline.getName().toLowerCase()).getHomes();
                                    StringBuilder stringBuilder = new StringBuilder();

                                    for (String homeOfOffline : homesOffline) {
                                        if (stringBuilder.length() > 0) {
                                            stringBuilder.append(", ");
                                        }

                                        stringBuilder.append("§8" + homeOfOffline + "§7");
                                    }

                                    player.sendMessage("§8" + offline.getName() + "§7's Homes (§3" + homesOffline.size() + "§7): §8" + stringBuilder.toString());
                                    return true;
                                }
                                else {
                                    player.sendMessage("§cError: Player doesn't have any homes.");
                                    return true;
                                }
                            }

                            if (plugin.essentials.getUser(other).hasHome() || !plugin.essentials.getUser(other).getHomes().isEmpty()) {
                                List<String> homesOther = plugin.essentials.getUser(other).getHomes();
                                StringBuilder stringBuilder = new StringBuilder();

                                for (String homeOfOther : homesOther) {
                                    if (stringBuilder.length() > 0) {
                                        stringBuilder.append(", ");
                                    }

                                    stringBuilder.append("§8" + homeOfOther + "§7");
                                }

                                player.sendMessage("§8" + other.getName() + "§7's Homes (§3" + homesOther.size() + "§7): §8" + stringBuilder.toString());
                                return true;
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
                    else if (args[0].contains(":")) { // Contains ':' somewhere; use it to define a player and get the home name after ':'.
                        if (player.isOp() || player.hasPermission("essentials.home.others")) {
                            char seperator = ':';
                            Player other = Bukkit.getServer().getPlayer(StringUtils.substringBefore(args[0], seperator));

                            if (other == null) {
                                OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(StringUtils.substringBefore(args[0], seperator));

                                if (plugin.essentials.getOfflineUser(offline.getName()) == null) {
                                    player.sendMessage(plugin.errorNeverJoinedEss);
                                    return true;
                                }

                                if (plugin.essentials.getUser(offline.getName().toLowerCase()).hasHome() || !plugin.essentials.getUser(offline.getName().toLowerCase()).getHomes().isEmpty()) {
                                    int separatorIndex = args[0].indexOf(seperator);

                                    if (separatorIndex != -1) {
                                        String homeOfOffline = args[0].substring(separatorIndex + 1);

                                        try {
                                            if (plugin.essentials.getUser(offline.getName().toLowerCase()).getHome(homeOfOffline) != null) {
//                                                World world = plugin.essentials.getUser(offline.getName().toLowerCase()).getHome(homeOfOffline).getWorld();
//                                                double x = plugin.essentials.getUser(offline.getName().toLowerCase()).getHome(homeOfOffline).getBlockX() + 0.5;
//                                                double y = plugin.essentials.getUser(offline.getName().toLowerCase()).getHome(homeOfOffline).getBlockY();
//                                                double z = plugin.essentials.getUser(offline.getName().toLowerCase()).getHome(homeOfOffline).getBlockZ() + 0.5;
//                                                float yaw = plugin.essentials.getUser(offline.getName().toLowerCase()).getHome(homeOfOffline).getYaw();
//                                                float pitch = plugin.essentials.getUser(offline.getName().toLowerCase()).getHome(homeOfOffline).getPitch();
//
//                                                player.teleport(new Location(world, x, y, z, yaw, pitch));


                                                User user = plugin.essentials.getUser(player);
                                                Trade trade = new Trade(player.getName(), plugin.essentials);
                                                user.getTeleport().home(plugin.essentials.getUser(offline.getName().toLowerCase()), homeOfOffline, trade);
                                                return true; /* Fixed */
                                            }
                                            else {
                                                player.sendMessage("§cError: Player doesn't have a home with that name.");
                                                return true;
                                            }
                                        } catch (Exception ex) {
                                            player.sendMessage("§cError: " + ex.getMessage());

                                            Bukkit.getLogger().severe("Error whilst teleporting " + player.getName() + " to " + args[0] + "!");
                                            Bukkit.getServer().getLogger().severe(ex.getMessage());
                                            return true;
                                        }
                                    }
                                    else {
                                        player.sendMessage("§c':' not found!");
                                        return true;
                                    }
                                }
                                else {
                                    player.sendMessage("§cError: Player doesn't have any homes.");
                                    return true;
                                }
                            }

                            if (plugin.essentials.getUser(other).hasHome() || !plugin.essentials.getUser(other).getHomes().isEmpty()) {
                                int separatorIndex = args[0].indexOf(seperator);

                                if (separatorIndex != -1) {
                                    String homeOfOnline = args[0].substring(separatorIndex + 1);

                                    try {
                                        if (plugin.essentials.getUser(other).getHome(homeOfOnline) != null) {
//                                            World world = plugin.essentials.getUser(other).getHome(homeOfOffline).getWorld();
//                                            double x = plugin.essentials.getUser(other).getHome(homeOfOffline).getBlockX() + 0.5;
//                                            double y = plugin.essentials.getUser(other).getHome(homeOfOffline).getBlockY();
//                                            double z = plugin.essentials.getUser(other).getHome(homeOfOffline).getBlockZ() + 0.5;
//                                            float yaw = plugin.essentials.getUser(other).getHome(homeOfOffline).getYaw();
//                                            float pitch = plugin.essentials.getUser(other).getHome(homeOfOffline).getPitch();
//
//                                            player.teleport(new Location(world, x, y, z, yaw, pitch));

                                            User user = plugin.essentials.getUser(player);
                                            Trade trade = new Trade(player.getName(), plugin.essentials);
                                            user.getTeleport().home(plugin.essentials.getUser(other), homeOfOnline, trade);
                                            return true; /* Fixed */
                                        }
                                        else {
                                            player.sendMessage("§cError: Player doesn't have a home with that name.");
                                            return true;
                                        }
                                    } catch (Exception ex) {
                                        player.sendMessage("§cError: " + ex.getMessage());

                                        Bukkit.getLogger().severe("Error whilst teleporting " + player.getName() + " to " + args[0] + "!");
                                        Bukkit.getServer().getLogger().severe(ex.getMessage());
                                        return true;
                                    }
                                }
                                else {
                                    player.sendMessage("§cError: ':' not found!");
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
                    else { // No ':' at the end; therefore it's the player's own home.
                        if (plugin.essentials.getUser(player).hasHome() || !plugin.essentials.getUser(player).getHomes().isEmpty()) {
                            try {
                                if (plugin.essentials.getUser(player).getHome(args[0]) != null) {
//                                    World world = plugin.essentials.getUser(player).getHome(args[0]).getWorld();
//                                    double x = plugin.essentials.getUser(player).getHome(args[0]).getBlockX() + 0.5;
//                                    double y = plugin.essentials.getUser(player).getHome(args[0]).getBlockY();
//                                    double z = plugin.essentials.getUser(player).getHome(args[0]).getBlockZ() + 0.5;
//                                    float yaw = plugin.essentials.getUser(player).getHome(args[0]).getYaw();
//                                    float pitch = plugin.essentials.getUser(player).getHome(args[0]).getPitch();
//
//                                    player.teleport(new Location(world, x, y, z, yaw, pitch));

                                    User user = plugin.essentials.getUser(player);
                                    Trade trade = new Trade(player.getName(), plugin.essentials);
                                    user.getTeleport().home(user, args[0], trade);
                                    return true; /* Fixed */
                                }
                                else {
                                    player.sendMessage("§cError: You don't have a home with that name.");
                                    return true;
                                }
                            } catch (Exception ex) {
                                player.sendMessage("§cError: " + ex.getMessage());

                                Bukkit.getLogger().severe("Error whilst teleporting " + player.getName() + " to " + args[0] + "!");
                                Bukkit.getServer().getLogger().severe(ex.getMessage());
                                return true;
                            }
                        }
                        else {
                            player.sendMessage("§cError: You don't have any homes.");
                            return true;
                        }
                    }
                }
                else {
                    if (player.isOp() || player.hasPermission("essentials.home.others")) {
                        player.sendMessage("§cUsage: /home <home name> or /home [player]:<home name>");
                        return true;
                    }
                    else {
                        player.sendMessage("§cUsage: /home <home name> ");
                        return true;
                    }
                }
            }
            else {
                sender.sendMessage("Command can only be executed by a player!");

                if (args.length != 1) {
                    sender.sendMessage("Usage: /home <player>");
                    return true;
                }

                Player other = Bukkit.getServer().getPlayer(args[0]);

                if (other == null) {
                    // Show the offline player's homes to the CONSOLE.
                    OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(args[0]);

                    if (plugin.essentials.getOfflineUser(offline.getName()) == null) {
                        sender.sendMessage("Error: Player never logged in before. (no Essentials data)");
                        return true;
                    }

                    if (plugin.essentials.getUser(offline.getName().toLowerCase()).hasHome() || !plugin.essentials.getUser(offline.getName().toLowerCase()).getHomes().isEmpty()) {
                        List<String> homes = plugin.essentials.getUser(offline.getName().toLowerCase()).getHomes();
                        StringBuilder stringBuilder = new StringBuilder();

                        for (String home : homes) {
                            if (stringBuilder.length() > 0) {
                                stringBuilder.append(", ");
                            }

                            stringBuilder.append(home);
                        }

                        sender.sendMessage(offline.getName() + "'s Homes (" + homes.size() + "): " + stringBuilder.toString());
                    }
                    else {
                        sender.sendMessage("Error: Player doesn't have any homes.");
                        return true;
                    }
                }

                // Show the online player's homes to the CONSOLE.
                if (plugin.essentials.getUser(other).hasHome() || !plugin.essentials.getUser(other).getHomes().isEmpty()) {
                    List<String> homes = plugin.essentials.getUser(other).getHomes();
                    StringBuilder stringBuilder = new StringBuilder();

                    for (String home : homes) {
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(", ");
                        }

                        stringBuilder.append(home);
                    }

                    sender.sendMessage(other.getName() + "'s Homes (" + homes.size() + "): " + stringBuilder.toString());
                    return true;
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
