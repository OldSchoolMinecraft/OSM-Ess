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
                Player player = (Player) sender;

                if (args.length == 0) {
                    if (plugin.essentials.getUser(player.getName()).getNickname() != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "§8Seen §7" + player.getName() + "§8(" + plugin.essentials.getUser(player.getName()).getNickname() + "§8)"));
                    } else {
                        player.sendMessage("§8Seen §7" + player.getName());
                    }

                    // /seen (your own stats)
                    player.sendMessage("§8Logged in at: §7" + plugin.essentials.getUser(player.getName()).getLastLogin());
                    player.sendMessage("§8Play time in session: §7" + plugin.essentials.getUser(player.getName()).getPlayerTime());
                    player.sendMessage("§8Total play time: §7" + plugin.playtimeHandler.getTotalPlaytime(player));
                    player.sendMessage("§8First join date: §7" + plugin.playtimeHandler.getFirstJoinDate(player));
                    return true;
                }

                if (args.length == 1) {
                    Player other = Bukkit.getPlayer(args[0]);

                    if (other == null) {
                        OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);

                        if (plugin.essentials.getOfflineUser(offline.getName()).getLastLogin() == 0) {
                            player.sendMessage("§cPlayer has never logged in before!");
                            return true;
                        }

                        if (plugin.essentials.getOfflineUser(offline.getName()).getNickname() != null) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "§8Seen §7" + offline.getName() + "§8(" + plugin.essentials.getOfflineUser(offline.getName()).getNickname() + "§8)"));
                        } else {
                            player.sendMessage("§8Seen §7" + offline.getName());
                        }

                        // /seen <player> (who is offline)
                        player.sendMessage("§8Last seen: §7" + plugin.essentials.getOfflineUser(offline.getName()).getLastLogout());
                        player.sendMessage("§8Play time: §7" + plugin.playtimeHandler.getTotalPlaytime(offline));
                        player.sendMessage("§8First join date: §7" + plugin.playtimeHandler.getFirstJoinDate(offline));
                        return true;
                    }

                    if (plugin.essentials.getOfflineUser(other.getName()).getLastLogin() == 0) {
                        player.sendMessage("§cPlayer has never logged in before!");
                        return true;
                    }

                    // /seen <player> (who is online)
                    if (plugin.essentials.getOfflineUser(other.getName()).getNickname() != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "§8Seen §7" + other.getName() + "§8(" + plugin.essentials.getOfflineUser(other.getName()).getNickname() + "§8)"));
                    } else {
                        player.sendMessage("§8Seen §7" + other.getName());
                    }
                    player.sendMessage("§8Logged in at: §7" + plugin.essentials.getUser(other.getName()).getLastLogin());
                    player.sendMessage("§8Play time in session: §7" + plugin.essentials.getUser(other.getName()).getPlayerTime());
                    player.sendMessage("§8Total play time: §7" + plugin.playtimeHandler.getTotalPlaytime(other));
                    player.sendMessage("§8First join date: §7" + plugin.playtimeHandler.getFirstJoinDate(other));
                    return true;
                }
            }
        }
        return true;
    }
}
