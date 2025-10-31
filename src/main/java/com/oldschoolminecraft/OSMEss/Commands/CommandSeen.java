package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

//                if (plugin.isScheduledDeathEnabled()) {
//                    if (plugin.scheduledDeath.getTimeToLive() <= 30) {
//                        sender.sendMessage("§cCommand is disabled as the server is about to restart!");
//                        return true;
//                    }
//                }

                if (args.length == 0) {
                    if (plugin.essentials.getUser(player.getName()).getNickname() != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "§8Seen §7" + player.getName() + " §8(§7" + plugin.essentials.getUser(player.getName()).getNickname() + "§8)"));
                    } else {
                        player.sendMessage("§8Seen §7" + player.getName());
                    }

//                  /seen (your own stats)
                    player.sendMessage("§8Logged in at: §7" + plugin.playtimeHandler.getLastLogin(player)); //Passed.
                    player.sendMessage("§8Play time in session: §7" + plugin.playtimeHandler.getPlayTimeInSession(player)); //Partially Passed. Unknown result passed 1 hour.
                    player.sendMessage("§8Total play time: §7" + plugin.playtimeHandler.getTotalPlaytime(player));
                    player.sendMessage("§8First join date: §7" + plugin.playtimeHandler.getFirstJoinDate(player)); //Passed.
                    return true;
                }

                if (args.length == 1) {
                    Player other = Bukkit.getPlayer(args[0]);

                    if (other == null) {
                        OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);

                        if (!plugin.playerDataHandler.hasData(offline)) {
                            player.sendMessage("§cPlayer has never logged in before!");
                            return true;
                        }

                        if (plugin.essentials.getOfflineUser(offline.getName()) == null) {
                            player.sendMessage("§cPlayer has never logged in before! (no Essentials data)");
                            return true;
                        }

                        if (plugin.essentials.getOfflineUser(offline.getName()).getNickname() != null) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "§8Seen §7" + offline.getName() + " §8(§7" + plugin.essentials.getOfflineUser(offline.getName()).getNickname() + "§8)"));
                        } else {
                            player.sendMessage("§8Seen §7" + offline.getName());
                        }

//                      /seen <player> (who is offline)
                        player.sendMessage("§8Last seen: §7" + plugin.playtimeHandler.getLastLogout(offline)); //Potential to Pass.
                        player.sendMessage("§8Total Play time: §7" + plugin.playtimeHandler.getTotalPlaytime(offline));
                        player.sendMessage("§8First join date: §7" + plugin.playtimeHandler.getFirstJoinDate(offline)); //Passed.
                        return true;
                    }

//                  /seen <player> who's online.
                    if (plugin.essentials.getUser(other.getName()).getNickname() != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "§8Seen §7" + other.getName() + " §8(§7" + plugin.essentials.getUser(other.getName()).getNickname() + "§8)"));
                    } else {
                        player.sendMessage("§8Seen §7" + other.getName());
                    }

                    player.sendMessage("§8Logged in at: §7" + plugin.playtimeHandler.getLastLogin(other)); //Passed.
                    player.sendMessage("§8Play time in session: §7" + plugin.playtimeHandler.getPlayTimeInSession(other)); //Partially Passed. Unknown result passed 1 hour.
                    player.sendMessage("§8Total play time: §7" + plugin.playtimeHandler.getTotalPlaytime(other));
                    player.sendMessage("§8First join date: §7" + plugin.playtimeHandler.getFirstJoinDate(other)); //Passed.
                    return true;
                }

                return true;
            }
            else {
//              CONSOLE Executed /seen

                if (args.length != 1) {
                    sender.sendMessage("§cUsage: /seen <player>");
                    return true;
                }

                Player other = Bukkit.getPlayer(args[0]);

                if (other == null) {
                    OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);

                    if (!plugin.playerDataHandler.hasData(offline)) {
                        sender.sendMessage("§cPlayer has never logged in before!");
                        return true;
                    }

                    if (plugin.essentials.getOfflineUser(offline.getName()) == null) {
                        sender.sendMessage("§cPlayer has never logged in before! (no Essentials data)");
                        return true;
                    }

                    if (plugin.essentials.getOfflineUser(offline.getName()).getNickname() != null) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "§8Seen §7" + offline.getName() + " §8(§7" + plugin.essentials.getOfflineUser(offline.getName()).getNickname() + "§8)"));
                    } else {
                        sender.sendMessage("§8Seen §7" + offline.getName());
                    }

//                  /seen <player> (who is offline)
                    sender.sendMessage("§8Last seen: §7" + plugin.playtimeHandler.getLastLogout(offline)); //Potential to Pass.
                    sender.sendMessage("§8Total play time: §7" + plugin.playtimeHandler.getTotalPlaytime(offline));
                    sender.sendMessage("§8First join date: §7" + plugin.playtimeHandler.getFirstJoinDate(offline)); //Passed.
                    return true;
                }

//              /seen <player> who's online.
                if (plugin.essentials.getUser(other.getName()).getNickname() != null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "§8Seen §7" + other.getName() + " §8(§7" + plugin.essentials.getUser(other.getName()).getNickname() + "§8)"));
                } else {
                    sender.sendMessage("§8Seen §7" + other.getName());
                }

                sender.sendMessage("§8Logged in at: §7" + plugin.playtimeHandler.getLastLogin(other)); //Passed.
                sender.sendMessage("§8Play time in session: §7" + plugin.playtimeHandler.getPlayTimeInSession(other)); //Partially Passed. Unknown result passed 1 hour.
                sender.sendMessage("§8Total play time: §7" + plugin.playtimeHandler.getTotalPlaytime(other));
                sender.sendMessage("§8First join date: §7" + plugin.playtimeHandler.getFirstJoinDate(other)); //Passed.
                return true;
            }
        }
        return true;
    }
}
