package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandParty implements CommandExecutor {

    private final OSMEss plugin;

    public CommandParty(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("party").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("party")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0) {
                    player.sendMessage("§2-= PARTY COMMANDS =-");
                    player.sendMessage("§a/party chat <message> §8- §7Sends a message to online players in your party.");
                    player.sendMessage("§a/party create <name> §8- §7Creates a new party.");
                    player.sendMessage("§a/party delete §8- §7Deletes an existing party you created.");
                    player.sendMessage("§a/party delhome §8- §7Deletes an existing home for your party.");
                    player.sendMessage("§a/party home §8- §7Teleports to your party's home, if created.");
                    player.sendMessage("§a/party info §8- §7Displays information of your current party.");
                    player.sendMessage("§a/party invite <player> §8- §7Invites a player to your party.");
                    player.sendMessage("§a/party kick <player> §8- §7Kicks a player from your party.");
                    player.sendMessage("§a/party leave §8- §7Removes you from an invited party.");
                    player.sendMessage("§a/party sethome §8- §7Sets the home for your party.");
                    player.sendMessage("§a/party tp <player> §8- §7Teleports to an online player in your party.");
                    player.sendMessage("§2-= END OF PARTY COMMANDS =-");
                    return true;
                }

                if (args[0].equalsIgnoreCase("chat")) {
                    if (args.length < 2) {
                        player.sendMessage("§cUsage: /party chat <message>");
                        return true;
                    }

                    if (!plugin.partyDataHandler.isInParty(player)) {
                        player.sendMessage("§cYou are not in a party.");
                        return true;
                    }
                    else {
                        String message = "";
                        for (int i = 1; i < args.length; i++) {
                            message = message + args[i] + " ";
                        }

                        if (player.hasPermission("essentials.chatcolor")) {
                            message = ChatColor.translateAlternateColorCodes('&', message);
                        }

                        plugin.partyDataHandler.sendPartyChatMessage(plugin.partyDataHandler.getPartyPlayerIsIn(player), player, message);
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("create")) {
                    if (args.length != 2) {
                        player.sendMessage("§cUsage: /party create <name>");
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("delete")) {
                    if (args.length != 1) {
                        player.sendMessage("§cUsage: /party delete");
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("delhome")) {
                    if (args.length != 1) {
                        player.sendMessage("§cUsage: /party delhome");
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("home")) {
                    if (args.length != 1) {
                        player.sendMessage("§cUsage: /party home");
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("info")) {
                    if (args.length != 1) {
                        player.sendMessage("§cUsage: /party info");
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("invite")) {
                    if (args.length != 2) {
                        player.sendMessage("§cUsage: /party invite <player>");
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("kick")) {
                    if (args.length != 2) {
                        player.sendMessage("§cUsage: /party kick <player>");
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("leave")) {
                    if (args.length != 2) {
                        player.sendMessage("§cUsage: /party leave <name>");
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("sethome")) {
                    if (args.length != 1) {
                        player.sendMessage("§cUsage: /party sethome");
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {}
                else {
                    player.sendMessage("§2-= PARTY COMMANDS =-");
                    player.sendMessage("§a/party chat <message> §8- §7Sends a message to online players in your party.");
                    player.sendMessage("§a/party create <name> §8- §7Creates a new party.");
                    player.sendMessage("§a/party delete §8- §7Deletes an existing party you created.");
                    player.sendMessage("§a/party delhome §8- §7Deletes an existing home for your party.");
                    player.sendMessage("§a/party home §8- §7Teleports to your party's home, if created.");
                    player.sendMessage("§a/party info §8- §7Displays information of your current party.");
                    player.sendMessage("§a/party invite <player> §8- §7Invites a player to your party.");
                    player.sendMessage("§a/party kick <player> §8- §7Kicks a player from your party.");
                    player.sendMessage("§a/party leave §8- §7Removes you from an invited party.");
                    player.sendMessage("§a/party sethome §8- §7Sets the home for your party.");
                    player.sendMessage("§a/party tp <player> §8- §7Teleports to an online player in your party.");
                    player.sendMessage("§2-= END OF PARTY COMMANDS =-");
                    return true;
                }
                return true;
            }
            else {
                sender.sendMessage("Command can only be executed by a player!");
                return true;
            }
        }

        return true;
    }
}
