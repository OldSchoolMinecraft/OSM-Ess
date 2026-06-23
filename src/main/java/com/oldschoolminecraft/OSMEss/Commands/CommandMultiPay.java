package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandMultiPay implements CommandExecutor {

    private final OSMEss plugin;

    public CommandMultiPay(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("multipay").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("multipay")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                List<Player> targetPlayersOnline = new ArrayList<>();
                List<String> targetPlayersOffline = new ArrayList<>();

                if (args.length != 2) {
                    player.sendMessage("§cUsage: /multipay <player1,player2,...> <amount>");
                    player.sendMessage("§cMinimum required to multipay: §b2");
                    player.sendMessage("§cMaximum allowed to multipay: §b5");
                    return true;
                }

                String combinedNames = args[0];
                String[] targetNames = combinedNames.split(",");

                Set<String> uniqueNames = new HashSet<>();

                for (String name : targetNames) {
                    String trimmedName = name.trim();

                    if (!trimmedName.isEmpty()) {uniqueNames.add(trimmedName.toLowerCase());}
                }

                List<String> nameList = new ArrayList<>(uniqueNames);

                for (int i = 0; i < nameList.size(); i++) {
                    String name = nameList.get(i);

                    Player other = Bukkit.getServer().getPlayerExact(name);

                    if (other != null && other.isOnline()) {targetPlayersOnline.add(other);}
                    else {targetPlayersOffline.add(name);}

                    if (i == nameList.size() - 1) {
                        try {
                            double amountPerPlayer = Math.round(Double.parseDouble(args[1]) * 100.0) / 100.0; // Total amount each player gets.
                            double totalAmount = Math.round(amountPerPlayer * targetPlayersOnline.size() * 100.0) / 100.0; //Total amount the payer has to spend across all players.

                            if (args[1].contains("-") || args[1].contains("+") || args[1].contains("*") || args[1].contains("[") || args[1].contains("]") || args[1].contains("<") || args[1].contains(">") || args[1].contains("(") || args[1].contains(")")) {
                                player.sendMessage("§cYou may not use special characters!");
                                return true;
                            }

                            if (totalAmount >= plugin.essentials.getUser(player).getMoney()) {
                                player.sendMessage("§cError: You do not have sufficient funds.");
                                return true;
                            }

                            if (uniqueNames.size() < 2) {
                                player.sendMessage("§cError: Minimum 2 players needed to multipay.");
                                return true;
                            }

                            if (uniqueNames.size() > 5) {
                                player.sendMessage("§cError: Maximum 5 players allowed to multipay.");
                                return true;
                            }

                            plugin.essentials.getUser(other).giveMoney(amountPerPlayer);
                            plugin.essentials.getUser(player).takeMoney(totalAmount);
                            if (nameList.contains(player.getName().toLowerCase())) {plugin.essentials.getUser(player).giveMoney(amountPerPlayer);}

                            player.sendMessage("§a$" + totalAmount + " has been sent to " + targetPlayersOnline.size() + " players.");
                        } catch (NumberFormatException ex) {
                            player.sendMessage(plugin.invalidNumPara);
                        }
                    }
                }

                StringBuilder stringBuilderOnline = new StringBuilder();
                StringBuilder stringBuilderOffline = new StringBuilder();

                for (Player targetPlayer : targetPlayersOnline) {
                    if (stringBuilderOnline.length() > 0) {stringBuilderOnline.append(", ");}
                    stringBuilderOnline.append("§a" + targetPlayer.getName()).append("§f");
                }


                for (String offline : targetPlayersOffline) {
                    if (stringBuilderOffline.length() > 0) {stringBuilderOffline.append(", ");}
                    stringBuilderOffline.append("§4" + offline).append("§f");
                }

                player.sendMessage(stringBuilderOnline.toString());
                player.sendMessage(stringBuilderOffline.toString());


                targetPlayersOnline.clear();
                targetPlayersOffline.clear();
                uniqueNames.clear();
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
