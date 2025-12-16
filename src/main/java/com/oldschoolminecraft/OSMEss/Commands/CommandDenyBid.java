package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.AuctionStatus;
import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDenyBid implements CommandExecutor {

    private final OSMEss plugin;

    public CommandDenyBid(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("denybid").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("denybid")) {

            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (!plugin.isAuctionSystemEnabled()) {
                    player.sendMessage("§cThe auction system is currently disabled!");
                    return true;
                }

                if (args.length != 0) {
                    player.sendMessage("§cUsage: /denybid");
                    return true;
                }

                if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.INACTIVE) {
                    player.sendMessage("§cThere is no active auction to bid on!");
                    return true;
                }
                else {
                    if (CommandBid.confirmBidList.containsKey(player.getName())) {
                        player.sendMessage("§b$" + CommandBid.confirmBidList.get(player.getName()) + " §9bid §4revoked§9!");

                        Bukkit.getServer().getLogger().info("[OSM-Ess] " + player.getName() + " REVOKED their bid of $" + CommandBid.confirmBidList.get(player.getName()) + "!");
                        CommandBid.confirmBidList.remove(player.getName());
                        return true;
                    }
                    else {
                        player.sendMessage("§cYou don't have a bid to confirm!");
                        return true;
                    }
                }
            }
            else {
                sender.sendMessage("§cCommand can only be executed by a player!");
                return true;
            }
        }

        return true;
    }
}
