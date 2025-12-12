package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.AuctionStatus;
import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBid implements CommandExecutor {

    private final OSMEss plugin;
    public final Object lock;

    public CommandBid(OSMEss plugin) {
        this.plugin = plugin;
        lock = plugin.auctionHandler.lock;
        this.plugin.getCommand("bid").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bid")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (!plugin.isAuctionSystemEnabled()) {
                    player.sendMessage("§cThe auction system is currently disabled!");
                    return true;
                }

                synchronized (lock) {
                    if (args.length != 1) {
                        if (plugin.isScheduledDeathEnabled()) {
                            if (plugin.scheduledDeath.getTimeToLive() <= 180) { // Disallow the command within 3 minutes of a restart.
                                player.sendMessage("§cCommand is disabled as the server is about to restart!");
                                return true;
                            }
                        }

                        player.sendMessage("§cUsage: /bid <amount>");
                        return true;
                    }

                    if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.INACTIVE) {
                        player.sendMessage("§cThere is no active auction to bid on!");
                        return true;
                    }
                    else {
                        if (plugin.auctionHandler.getAuctionHost().getName().equalsIgnoreCase(player.getName())) {
                            player.sendMessage("§cYou cannot bid on your own auction!");
                            return true;
                        }
                        else {
                            try {
                                double amount = Math.round(Double.parseDouble(args[0]) * 100.0) / 100.0;

                                if (args[0].contains("-") || args[0].contains("+") || args[0].contains("*") || args[0].contains("/")) {
                                    player.sendMessage("§cYou may not use special characters!");
                                    return true;
                                }

                                if (amount < plugin.auctionHandler.getStartingBid()) {
                                    player.sendMessage("§cYour bid amount is lower than the starting bid!");
                                    return true;
                                }

                                if (plugin.auctionHandler.getTopBidAmount() >= amount) {
                                    player.sendMessage("§cYour bid amount must be higher than $" + plugin.auctionHandler.getTopBidAmount() + "!");
                                    return true;
                                }
                                if (amount >= plugin.essentials.getUser(player).getMoney()) {
                                    player.sendMessage("§cYou don't have enough money to bid this amount!");
                                    return true;
                                }
                                else {
                                    plugin.auctionHandler.addToAuction(player, amount);
                                }
                            } catch (NumberFormatException ex) {
                                player.sendMessage("§cInvalid integer provided!");
                            }

                            return true;
                        }
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
