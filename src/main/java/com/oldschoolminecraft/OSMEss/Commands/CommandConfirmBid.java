package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.AuctionStatus;
import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandConfirmBid implements CommandExecutor {

    private final OSMEss plugin;
    public final Object lock;

    public CommandConfirmBid(OSMEss plugin) {
        this.plugin = plugin;
        lock = plugin.auctionHandler.lock;
        this.plugin.getCommand("confirmbid").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("confirmbid")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (!plugin.isAuctionSystemEnabled()) {
                    player.sendMessage(plugin.auctionNotEnabled);
                    return true;
                }

                synchronized (lock) {
                    if (args.length != 0) {
                        player.sendMessage("§cUsage: /confirmbid");
                        return true;
                    }

                    if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.INACTIVE) {
                        player.sendMessage("§cThere is no active auction to bid on!");
                        return true;
                    }
                    else {
                        if (CommandBid.confirmBidList.containsKey(player.getName())) {

                            // Unlikely.
                            if (plugin.auctionHandler.getAuctionHost().getName().equalsIgnoreCase(player.getName())) {
                                player.sendMessage("§cYou cannot bid on your own auction!");
                                CommandBid.confirmBidList.remove(player.getName());
                                return true;
                            }

                            // Very unlikely.
                            if (CommandBid.confirmBidList.get(player.getName()) < plugin.auctionHandler.getStartingBid()) {
                                player.sendMessage("§cYour bid amount is lower than the starting bid!");
                                CommandBid.confirmBidList.remove(player.getName());
                                return true;
                            }


                            if (plugin.auctionHandler.getTopBidAmount() >= CommandBid.confirmBidList.get(player.getName())) {
                                player.sendMessage("§cYour bid amount must be higher than $" + plugin.auctionHandler.getTopBidAmount() + "!");
                                CommandBid.confirmBidList.remove(player.getName());
                                return true;
                            }
                            if (CommandBid.confirmBidList.get(player.getName()) >= plugin.essentials.getUser(player).getMoney()) {
                                player.sendMessage("§cYou don't have enough money to bid this amount!");
                                CommandBid.confirmBidList.remove(player.getName());
                                return true;
                            }

                            player.sendMessage("§b$" + CommandBid.confirmBidList.get(player.getName()) + " §9bid §2confirmed§9!");
                            plugin.auctionHandler.addToAuction(player, CommandBid.confirmBidList.get(player.getName()));

                            Bukkit.getServer().getLogger().info("[OSM-Ess] " + player.getName() + " CONFIRMED their bid of $" + CommandBid.confirmBidList.get(player.getName()) + "!");
                            CommandBid.confirmBidList.remove(player.getName());
                            return true;
                        }
                        else {
                            player.sendMessage("§cYou don't have a bid to confirm!");
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
