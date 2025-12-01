package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.AuctionStatus;
import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAuction implements CommandExecutor {

    private final OSMEss plugin;

    public CommandAuction(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("auction").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("auction")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0) {
                    if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.ACTIVE) {
                        player.sendMessage("§5-= §dAUCTION §5=-");
                        player.sendMessage("§6Time Left: §e" + plugin.auctionHandler.formatTime(plugin.auctionHandler.getAuctionTimeLeft()));
                        player.sendMessage("§6Total Bidders: §e" + plugin.auctionHandler.totalBidders);
                        player.sendMessage("§6Current Bid: §a$" + plugin.auctionHandler.getTopBidAmount());
                        player.sendMessage("§6Starting Bid: §a$" + plugin.auctionHandler.getStartingBid());
                        player.sendMessage("§6Prize: §b" + plugin.auctionHandler.getAuctionItem().getAmount() + "x " +  plugin.auctionHandler.getAuctionItemName());
                        return true;
                    }
                    else {
                        if (plugin.isScheduledDeathEnabled()) {
                            if (plugin.scheduledDeath.getTimeToLive() <= 180) { // Disallow the command within 3 minutes of a restart.
                                sender.sendMessage("§cCommand is disabled as the server is about to restart!");
                                return true;
                            }
                        }

                        player.sendMessage("§cUsage: /auction <price>");
                        return true;
                    }
                }

                if (args.length != 1) {
                    if (plugin.playtimeHandler.getTotalPlayTimeInMillis(player) < plugin.getMinimumRequiredPlaytimeToAuction()) {// 12 hours. Prevent new players from auctioning stolen items.
                        player.sendMessage("§cYou need a minimum 12 hours of playtime to auction items!");
                        return true;
                    }
                    else {
                        if (plugin.isScheduledDeathEnabled()) {
                            if (plugin.scheduledDeath.getTimeToLive() <= 180) { // Disallow the command within 3 minutes of a restart.
                                sender.sendMessage("§cCommand is disabled as the server is about to restart!");
                                return true;
                            }
                        }

                        player.sendMessage("§cUsage: /auction <price>");
                        return true;
                    }
                }

                if (plugin.playtimeHandler.getTotalPlayTimeInMillis(player) < plugin.getMinimumRequiredPlaytimeToAuction()) {// 12 hours. Prevent new players from auctioning stolen items.
                    player.sendMessage("§cYou need a minimum 12 hours of playtime to auction items!");
                    return true;
                }
                else {
                    //Todo: Check if auction is running before starting a new one.
                    if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.ACTIVE) {
                        player.sendMessage("§cThere is currently an active auction!");
                        return true;
                    }
                    else {
                        if (plugin.isScheduledDeathEnabled()) {
                            if (plugin.scheduledDeath.getTimeToLive() <= 180) { // Disallow the command within 3 minutes of a restart.
                                sender.sendMessage("§cCommand is disabled as the server is about to restart!");
                            }
                        }

                        long now = System.currentTimeMillis();
                        if (now - plugin.auctionHandler.lastAuctionEndTime < plugin.auctionHandler.AUCTION_COOLDOWN_MS) {
                            long remaining = (plugin.auctionHandler.AUCTION_COOLDOWN_MS - (now - plugin.auctionHandler.lastAuctionEndTime)) / 1000;

                            if (remaining == 1) {
                                player.sendMessage("§cYou must wait §e" + remaining + " §csecond before starting a new auction!");
                                return true;
                            }
                            else {
                                player.sendMessage("§cYou must wait §e" + remaining + " §cseconds before starting a new auction!");
                                return true;
                            }
                        }

                        try {
                            int price = Integer.parseInt(args[0]);

                            if (args[0].contains("-") || args[0].contains("+") || args[0].contains("*") || args[0].contains("/")) {
                                player.sendMessage("§cYou may not use special characters!");
                                return true;
                            }

                            if (price == 0) {
                                player.sendMessage("§cYou may not start an auction at $0!");
                                return true;
                            }

                            if (price >= plugin.getMaxAllowedStartingBid()) {
                                player.sendMessage("§cYou may not start an auction with a bid of $" + price + "!");
                                return true;
                            }

                            plugin.auctionHandler.startAuction(player, price);
                        } catch (NumberFormatException ex) {
                            player.sendMessage("§cInvalid integer provided!");
                        }

                        return true;
                    }
                }
            }
            else {
                if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.ACTIVE) {
                    sender.sendMessage("-= AUCTION =-");
                    sender.sendMessage("Time Left: " + plugin.auctionHandler.formatTime(plugin.auctionHandler.getAuctionTimeLeft()));
                    sender.sendMessage("Total Bidders: " + plugin.auctionHandler.totalBidders);
                    sender.sendMessage("Current Bid: $" + plugin.auctionHandler.getTopBidAmount());
                    sender.sendMessage("Starting Bid: $" + plugin.auctionHandler.getStartingBid());
                    sender.sendMessage("Prize: " + plugin.auctionHandler.getAuctionItem().getAmount() + "x " +  plugin.auctionHandler.getAuctionItemName());
                    return true;
                }
                else {
                    sender.sendMessage("§cThere is no auction to look at!");
                    return true;
                }
            }
        }

        return true;
    }
}
