package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import com.oldschoolminecraft.OSMEss.compat.OSMPLUserData;
import com.oldschoolminecraft.OSMEss.compat.TimeZoneUserData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;

import static com.oldschoolminecraft.OSMEss.Handlers.PlayerDataHandler.TIMEZONE_DATA_DIR;

public class CommandSeen implements CommandExecutor {

    private final OSMEss plugin;

    public CommandSeen(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("seen").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("seen")) {
            if (sender instanceof Player) {
//              Player executes /seen
                Player player = (Player) sender;

                if (args.length == 0) {
                    if (plugin.essentials.getUser(player.getName()).getNickname() != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "§8Seen §7" + player.getName() + " §8(§7" + plugin.essentials.getUser(player.getName()).getNickname() + "§8)"));
                    } else {
                        player.sendMessage("§8Seen §7" + player.getName());
                    }

//                  /seen (your own stats)
                    if (plugin.playerDataHandler.hasTimeZoneData(player) && !getPlayerTimeZone(player).endsWith("c")) {
                        player.sendMessage("§8Logged in at: §7" + plugin.playtimeHandler.getLastLoginByTimeZone(player, player));
                        player.sendMessage("§8Play time in session: §7" + plugin.playtimeHandler.getPlayTimeInSession(player));
                        player.sendMessage("§8Total play time: §7" + plugin.playtimeHandler.getTotalPlaytimeLive(player));
                        player.sendMessage("§8First join date: §7" + plugin.playtimeHandler.getFirstJoinDateByTimeZone(player, player));
                        return true;
                    }
                    else {
                        player.sendMessage("§8Logged in at: §7" + plugin.playtimeHandler.getLastLogin(player));
                        player.sendMessage("§8Play time in session: §7" + plugin.playtimeHandler.getPlayTimeInSession(player));
                        player.sendMessage("§8Total play time: §7" + plugin.playtimeHandler.getTotalPlaytimeLive(player));
                        player.sendMessage("§8First join date: §7" + plugin.playtimeHandler.getFirstJoinDate(player));
                        return true;
                    }
                }

                if (args.length == 1) {
                    Player other = Bukkit.getPlayerExact(args[0]);

                    if (other == null) {
                        OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);

                        if (!plugin.playerDataHandler.hasData(offline)) {
                            player.sendMessage(plugin.errorNeverJoinedNoData);
                            return true;
                        }

                        if (plugin.essentials.getOfflineUser(offline.getName()) == null) {
                            player.sendMessage(plugin.errorNeverJoinedEss);
                            return true;
                        }

                        if (plugin.essentials.getOfflineUser(offline.getName()).getNickname() != null) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "§8Seen §7" + offline.getName() + " §8(§7" + plugin.essentials.getOfflineUser(offline.getName()).getNickname() + "§8)"));
                        } else {
                            player.sendMessage("§8Seen §7" + offline.getName());
                        }

//                      /seen <player> (who is offline)

                        if (plugin.playerDataHandler.hasTimeZoneData(player) && !getPlayerTimeZone(player).endsWith("c")) {
                            player.sendMessage("§8Last seen: §7" + plugin.playtimeHandler.getLastLogoutByTimeZone(offline, player));
                            player.sendMessage("§8Total Play time: §7" + plugin.playtimeHandler.getTotalPlaytime(offline));
                            player.sendMessage("§8First join date: §7" + plugin.playtimeHandler.getFirstJoinDateByTimeZone(offline, player));
                            return true;
                        }
                        else {
                            player.sendMessage("§8Last seen: §7" + plugin.playtimeHandler.getLastLogout(offline));
                            player.sendMessage("§8Total Play time: §7" + plugin.playtimeHandler.getTotalPlaytime(offline));
                            player.sendMessage("§8First join date: §7" + plugin.playtimeHandler.getFirstJoinDate(offline));
                            return true;
                        }
                    }

//                  /seen <player> who's online.
                    if (plugin.essentials.getUser(other.getName()).getNickname() != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "§8Seen §7" + other.getName() + " §8(§7" + plugin.essentials.getUser(other.getName()).getNickname() + "§8)"));
                    } else {
                        player.sendMessage("§8Seen §7" + other.getName());
                    }

                    if (plugin.playerDataHandler.hasTimeZoneData(player) && !getPlayerTimeZone(player).endsWith("c")) {
                        player.sendMessage("§8Logged in at: §7" + plugin.playtimeHandler.getLastLoginByTimeZone(other, player));
                        player.sendMessage("§8Play time in session: §7" + plugin.playtimeHandler.getPlayTimeInSession(other));
                        player.sendMessage("§8Total play time: §7" + plugin.playtimeHandler.getTotalPlaytimeLive(other));
                        player.sendMessage("§8First join date: §7" + plugin.playtimeHandler.getFirstJoinDateByTimeZone(other, player));
                        return true;
                    }
                    else {
                        player.sendMessage("§8Logged in at: §7" + plugin.playtimeHandler.getLastLogin(other));
                        player.sendMessage("§8Play time in session: §7" + plugin.playtimeHandler.getPlayTimeInSession(other));
                        player.sendMessage("§8Total play time: §7" + plugin.playtimeHandler.getTotalPlaytimeLive(other));
                        player.sendMessage("§8First join date: §7" + plugin.playtimeHandler.getFirstJoinDate(other));
                        return true;
                    }
                }

                return true;
            }
            else {
//              CONSOLE Executed /seen

                if (args.length != 1) {
                    sender.sendMessage("Usage: /seen <player>");
                    return true;
                }

                Player other = Bukkit.getPlayerExact(args[0]);

                if (other == null) {
                    OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);

                    if (!plugin.playerDataHandler.hasData(offline)) {
                        sender.sendMessage("Error: Player never logged in before.");
                        return true;
                    }

                    if (plugin.essentials.getOfflineUser(offline.getName()) == null) {
                        sender.sendMessage("Error: Player never logged in before. (no Essentials data)");
                        return true;
                    }

                    if (plugin.essentials.getOfflineUser(offline.getName()).getNickname() != null) {
                        sender.sendMessage("Seen " + offline.getName() + " (" + plugin.essentials.getOfflineUser(offline.getName()).getNickname() + ")");
                    } else {
                        sender.sendMessage("Seen " + offline.getName());
                    }

//                  /seen <player> (who is offline)
                    sender.sendMessage("Last seen: " + plugin.playtimeHandler.getLastLogout(offline));
                    sender.sendMessage("Total play time: " + plugin.playtimeHandler.getTotalPlaytime(offline));
                    sender.sendMessage("First join date: " + plugin.playtimeHandler.getFirstJoinDate(offline));
                    return true;
                }

//              /seen <player> who's online.
                if (plugin.essentials.getUser(other.getName()).getNickname() != null) {
                    sender.sendMessage("Seen " + other.getName() + " (" + plugin.essentials.getOfflineUser(other.getName()).getNickname() + ")");
                } else {
                    sender.sendMessage("Seen " + other.getName());
                }

                sender.sendMessage("Logged in at: " + plugin.playtimeHandler.getLastLogin(other));
                sender.sendMessage("Play time in session: " + plugin.playtimeHandler.getPlayTimeInSession(other));
                sender.sendMessage("Total play time: " + plugin.playtimeHandler.getTotalPlaytime(other));
                sender.sendMessage("First join date: " + plugin.playtimeHandler.getFirstJoinDate(other));
                return true;
            }
        }
        return true;
    }

    public static String getPlayerTimeZone(CommandSender sender) {
        try (FileReader reader = new FileReader(new File(TIMEZONE_DATA_DIR, sender.getName().toLowerCase() + ".json"))) {
            TimeZoneUserData data = TimeZoneUserData.gson.fromJson(reader, TimeZoneUserData.class);
            return data.timeZone;

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return "utc";
        }
    }
}
