package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFriend implements CommandExecutor {

    private final OSMEss plugin;

    public CommandFriend(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("friend").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("friend")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0) {
                    player.sendMessage("§2-= FRIEND COMMANDS =-");
                    player.sendMessage("§a/friend add §8- §7Adds a player to your friend list.");
                    player.sendMessage("§a/friend list §8- §7Grabs a list of all your friends.");
                    player.sendMessage("§a/friend remove §8- §7Removes a player from your friend list.");
                    player.sendMessage("§a/friend tp §8- §7Teleport to one of your friends online.");
                    player.sendMessage("§a/friend location §8- §7See the location of your friends online.");
                    player.sendMessage("§2-= END OF COMMANDS =-");
                    return true;
                }

                if (args[0].equalsIgnoreCase("add")) {
                    if (args.length != 2) {
                        player.sendMessage("§cUsage: /friend add <player>");
                        return true;
                    }
                    Player other = Bukkit.getServer().getPlayerExact(args[0]);

                    if (other == null) {
                        player.sendMessage(plugin.playerNotFound);
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("list")) {
                    if (args.length != 2) {
                        player.sendMessage("§cUsage: /friend list");
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length != 2) {
                        player.sendMessage("§cUsage: /friend remove <player>");
                        return true;
                    }
                    Player other = Bukkit.getServer().getPlayerExact(args[0]);

                    if (other == null) {
                        player.sendMessage(plugin.playerNotFound);
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("tp")) {
                    if (args.length != 2) {
                        player.sendMessage("§cUsage: /friend tp <player>");
                        return true;
                    }
                    Player other = Bukkit.getServer().getPlayerExact(args[0]);

                    if (other == null) {
                        player.sendMessage(plugin.playerNotFound);
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("location")) {
                    if (args.length == 1) {
                        //See location of all friends online.
                    }

                    else if (args.length == 2) {
                        Player other = Bukkit.getServer().getPlayerExact(args[0]);

                        if (other == null) {
                            player.sendMessage(plugin.playerNotFound);
                            return true;
                        }

                        //Todo: Check if player is in friend list.


                        player.sendMessage("§2-= §a" + other.getName() + "'s §2LOCATION INFO =-");
                        player.sendMessage("§6World: §e" + other.getWorld().getName());
                        player.sendMessage("§6X: §e" + other.getLocation().getBlockX());
                        player.sendMessage("§6Y: §e" + other.getLocation().getBlockY());
                        player.sendMessage("§6Z: §e" + other.getLocation().getBlockZ());
                        player.sendMessage("§2-= END OF INFO =-");
                        return true;
                    }
                    else {
                        player.sendMessage("§cUsage: /friend list [player]");
                        return true;
                    }
                }
                else {
                    player.sendMessage("§2-= FRIEND COMMANDS =-");
                    player.sendMessage("§a/friend add §8- §7Adds a player to your friend list.");
                    player.sendMessage("§a/friend list §8- §7Grabs a list of all your friends.");
                    player.sendMessage("§a/friend remove §8- §7Removes a player from your friend list.");
                    player.sendMessage("§a/friend tp §8- §7Teleport to one of your friends online.");
                    player.sendMessage("§a/friend location §8- §7See the location of your friends online.");
                    player.sendMessage("§2-= END OF COMMANDS =-");
                    return true;
                }
            }
            else { //Todo: Add console ability to edit friend info of players.
                sender.sendMessage("Command can only be executed by a player!");
                return true;
            }
        }

        return true;
    }
}
